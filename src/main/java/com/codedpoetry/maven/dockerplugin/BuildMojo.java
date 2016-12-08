package com.codedpoetry.maven.dockerplugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.html.HTML.Tag;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.codedpoetry.maven.dockerplugin.file.FileCreator;
import com.codedpoetry.maven.dockerplugin.templates.TemplateRenderer;
import com.codedpoetry.maven.dockerplugin.templates.VelocityTemplateRenderer;
import com.google.common.base.Strings;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.Container;

/**
 * Goal which builds a Docker image from a template
 *
 */
@Mojo(name = "build", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class BuildMojo extends AbstractMojo {
	
	private TemplateRenderer templateRenderer = new VelocityTemplateRenderer();

	private FileCreator fileCreator = new FileCreator();

	@Parameter(defaultValue = "${project.build.directory}", required = true)
	private File outputDirectory;

	/**
	 * Location of the dockerfiles.
	 */
	@Parameter(defaultValue = "${project.build.directory}", property = "dockerfilePath", required = true)
	private String dockerfilePath;

	/**
	 * Docker image name
	 */
	@Parameter(property = "imageName", required = true)
	private String imageName;


	/**
	 * Docker image tag
	 */
	@Parameter(property = "imageTag", required = true)
	private String imageTag;

	/**
	 * Maven Project, used to inject it in Dockerfile templates
	 */
	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	public void execute() throws MojoExecutionException {
		try {
			String templateUrl = dockerfilePath + "/Dockerfile.vm";
			File dockerfilesDirectory = new File(templateUrl);

			if (!dockerfilesDirectory.exists() || dockerfilesDirectory.isDirectory()) {
				throw new MojoExecutionException("No Dockerfile found at " + dockerfilePath);
			}
			
			if(Strings.isNullOrEmpty(imageName)){
				throw new MojoExecutionException("imageName cannot be null nor empty");
			}

			Map context = new HashMap<>();
			context.put("project", project);

			StringWriter sw = templateRenderer.renderTemplate(templateUrl, context);

			File dockerfile = fileCreator.createFile(outputDirectory, "Dockerfile", sw.toString());

			final DockerClient docker = DefaultDockerClient.fromEnv().build();

			Path dockerfiles = outputDirectory.toPath();
			
			String imageAndTag = contatImageNameAndTag();
			this.getLog().info("Building image '" + imageName +"' with tag '" + imageTag + "'");
			docker.build(dockerfiles, imageAndTag);
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private String contatImageNameAndTag() {
		if (Strings.isNullOrEmpty(imageTag)){
			return imageName;
		} else {
			return imageName + ":" + imageTag;
		}
	}

}

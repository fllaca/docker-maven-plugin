package com.codedpoetry.maven.dockerplugin.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.common.base.Strings;

public class FileCreator {

	public File createFile(File outputDirectory, String filename, String content) throws IOException {

		if (Strings.isNullOrEmpty(filename)) {
			throw new IllegalArgumentException("Filename cannnot be null nor empty");
		}

		File f = outputDirectory;

		if (!f.exists()) {
			f.mkdirs();
		} else if (!f.isDirectory()) {
			throw new IllegalArgumentException("Output directory must be a directory");
		}

		File touch = new File(f, filename);

		try (FileWriter w = new FileWriter(touch)) {

			w.write(content);
		} 
		return touch;
	}

}
package com.codedpoetry.maven.dockerplugin.templates;

import java.io.StringWriter;
import java.util.Map;

public interface TemplateRenderer {

	StringWriter renderTemplate(String templateUrl, Map context);

}
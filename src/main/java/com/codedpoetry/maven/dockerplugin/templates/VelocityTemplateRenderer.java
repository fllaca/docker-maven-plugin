package com.codedpoetry.maven.dockerplugin.templates;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class VelocityTemplateRenderer implements TemplateRenderer{
	
	/* (non-Javadoc)
	 * @see com.codedpoetry.maven.dockerplugin.TemplateRenderer#renderTemplate(java.lang.String, java.util.Map)
	 */
	@Override
	public StringWriter renderTemplate(String templateUrl, Map context) {
		VelocityEngine ve = new VelocityEngine();
		ve.init();
		Template template = ve.getTemplate(templateUrl);
		VelocityContext vc = new VelocityContext(context);
		StringWriter sw = new StringWriter();
		template.merge(vc, sw);
		return sw;
	}
}

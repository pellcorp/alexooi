/*
 *
 */
package au.com.alexooi.mojos.advent.generator;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

public class JavaGenerator {
	private static final String VELOCITYLOGGER = "velocitylogger";
	private final ConfigurationFactory configurationFactory;

	public JavaGenerator(ClassLoader loader, List<String> extraBuilderMethodSupportFqns) {
		this.configurationFactory = new ConfigurationFactory(loader, extraBuilderMethodSupportFqns);
	}

	public List<GeneratedClass> generate(String classFqn) {
		Configuration configuration = configurationFactory.createFrom(classFqn);
		GeneratedClass testBuilderClass = new GeneratedClass(generateTestBuilderSource(configuration),
				configuration.getPackageName(), getTestBuiderClassName(configuration));
		GeneratedClass updateListenerClass = new GeneratedClass(generateUpdateListenerSource(configuration),
				configuration.getPackageName(), getUpdateListenerClassName(configuration));
		return Arrays.asList(testBuilderClass, updateListenerClass);
	}

	private String generateUpdateListenerSource(Configuration configuration) {
		VelocityContext velocityContext = new VelocityContext();
		velocityContext.put("configuration", configuration);
		velocityContext.put("updateListenerClassName", getUpdateListenerClassName(configuration));
		velocityContext.put("builderClassName", getTestBuiderClassName(configuration));
		try {
			VelocityEngine velocityEngine = createProperties();
			Template template = velocityEngine.getTemplate("UpdateListener.vm");
			StringWriter stringWriter = new StringWriter();
			template.merge(velocityContext, stringWriter);
			return stringWriter.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String generateTestBuilderSource(Configuration configuration) {
		VelocityContext velocityContext = new VelocityContext();
		velocityContext.put("configuration", configuration);
		velocityContext.put("builderClassName", getTestBuiderClassName(configuration));
		try {
			VelocityEngine velocityEngine = createProperties();
			Template template = velocityEngine.getTemplate("TestBuilder.vm");
			StringWriter stringWriter = new StringWriter();
			template.merge(velocityContext, stringWriter);
			return stringWriter.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private VelocityEngine createProperties() {
		Properties props = new Properties();
		props.put("resource.loader", "class");
		props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
				"org.apache.velocity.runtime.log.Log4JLogChute");
		velocityEngine.setProperty("runtime.log.logsystem.log4j.logger", VELOCITYLOGGER);

		velocityEngine.init(props);
		return velocityEngine;
	}

	private String getTestBuiderClassName(Configuration configuration) {
		return configuration.getClassName() + "TestBuilder";
	}

	private String getUpdateListenerClassName(Configuration configuration) {
		return configuration.getClassName() + "UpdateListener";
	}
}

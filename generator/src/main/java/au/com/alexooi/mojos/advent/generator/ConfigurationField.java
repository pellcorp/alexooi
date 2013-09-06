/*
 *
 */
package au.com.alexooi.mojos.advent.generator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.com.alexooi.mojos.advent.generator.methods.BuilderMethod;
import org.apache.commons.lang.StringUtils;

public class ConfigurationField {
	private final Field field;

	private final List<BuilderMethod> builderMethods;

	public ConfigurationField(Field field) {
		this.field = field;
		this.builderMethods = new ArrayList<BuilderMethod>();
	}

	public List<BuilderMethod> getBuilderMethods() {
		return Collections.unmodifiableList(builderMethods);
	}

	public void addBuilderMethods(List<BuilderMethod> builderMethodsForAdding) {
		for (BuilderMethod builderMethodForAdd : builderMethodsForAdding) {
			this.builderMethods.add(builderMethodForAdd);
		}
	}

	public String getCamelCaseName() {
		return StringUtils.capitalize(getName());
	}

	public String getName() {
		return field.getName();
	}

	public Class<?> getType() {
		return field.getType();
	}
}

/*
 *
 */
package au.com.alexooi.mojos.advent.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import au.com.alexooi.mojos.advent.generator.methods.ExtraBuilderMethod;

public class Configuration {
	private final List<ConfigurationField> fields;

	private final TreeSet<ExtraBuilderMethod> extraMethods;

	private final String className;
	private final String packageName;

	public Configuration(String className, String packageName) {
		this.packageName = packageName;
		this.className = className;
		this.fields = new ArrayList<ConfigurationField>();
		this.extraMethods = new TreeSet<ExtraBuilderMethod>(new Comparator<ExtraBuilderMethod>() {
			@Override
			public int compare(ExtraBuilderMethod o1, ExtraBuilderMethod o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
	}

	public void addExtraMethods(List<ExtraBuilderMethod> extraMethods) {
		this.extraMethods.addAll(extraMethods);
	}

	public void addField(ConfigurationField field) {
		this.fields.add(field);
	}

	public String getClassName() {
		return className;
	}

	public String getPackageName() {
		return packageName;
	}

	public List<ExtraBuilderMethod> getExtraMethods() {
		return new ArrayList<ExtraBuilderMethod>(extraMethods);
	}

	public List<ConfigurationField> getFields() {
		return Collections.unmodifiableList(fields);
	}
}

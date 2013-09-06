/*
 *
 */
package au.com.alexooi.mojos.advent.generator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import au.com.alexooi.mojos.advent.api.extension.ExtraBuilderMethodsSupport;
import au.com.alexooi.mojos.advent.api.extension.ExtraMethod;
import au.com.alexooi.mojos.advent.generator.methods.AddToCollectionMethod;
import au.com.alexooi.mojos.advent.generator.methods.BuilderMethod;
import au.com.alexooi.mojos.advent.generator.methods.ExtraBuilderMethod;
import au.com.alexooi.mojos.advent.generator.methods.SetterMethod;
import org.apache.commons.lang.StringUtils;

import static java.util.Arrays.asList;

public class ConfigurationFactory {
	private final ClassLoader classLoader;
	private Map<Class, Class<ExtraBuilderMethodsSupport>> extraBuilderMethodsSupportMap;

	public ConfigurationFactory(ClassLoader classLoader, List<String> extraMethodSupportFqns) {
		this.classLoader = classLoader;
		ExtraMethodsSupportMapper mapper = new ExtraMethodsSupportMapper(classLoader);
		this.extraBuilderMethodsSupportMap = mapper.map(extraMethodSupportFqns);
	}

	public Configuration createFrom(String classFqn) {
		Class<?> originalClazz = getClazz(classFqn);
		Class<?> clazz = originalClazz;
		Configuration configuration = new Configuration(clazz.getSimpleName(), clazz.getPackage().getName());
		while (clazz != null) {
			addConfigurationFieldsFor(clazz, configuration);
			clazz = clazz.getSuperclass();
		}
		List<ExtraBuilderMethod> extraMethodsFor = getExtraMethodsFor(originalClazz);
		configuration.addExtraMethods(extraMethodsFor);
		return configuration;
	}

	private List<ExtraBuilderMethod> getExtraMethodsFor(Class clazz) {
		List<ExtraBuilderMethod> builderMethods = new ArrayList<ExtraBuilderMethod>();
		Class<ExtraBuilderMethodsSupport> extraMethodsClazz = extraBuilderMethodsSupportMap.get(clazz);
		if (extraMethodsClazz != null) {
			for (Method extraMethod : extraMethodsClazz.getDeclaredMethods()) {
				if (extraMethod.isAnnotationPresent(ExtraMethod.class)) {
					builderMethods.add(new ExtraBuilderMethod(extraMethodsClazz, extraMethod));
				}
			}
		}
		return builderMethods;
	}

	private void addConfigurationFieldsFor(Class<?> clazz, Configuration configuration) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			ConfigurationField configurationField = new ConfigurationField(field);
			List<BuilderMethod> builderMethods = createBuilderMethodsFor(field, clazz);
			configurationField.addBuilderMethods(builderMethods);
			configuration.addField(configurationField);
		}
	}

	private List<BuilderMethod> createBuilderMethodsFor(Field field, Class<?> parentClazz) {
		List<BuilderMethod> methods = new ArrayList<BuilderMethod>();
		methods.addAll(createSetterMethods(field, parentClazz));
		methods.addAll(createCollectionMethods(field, parentClazz));
		return methods;
	}

	private List<BuilderMethod> createCollectionMethods(Field field, Class parentClazz) {
		try {
			parentClazz.getMethod("get" + StringUtils.capitalize(field.getName()));
			List<BuilderMethod> methods = new ArrayList<BuilderMethod>();
			// PERHAPS INTRODUCE A CHAIN OF RESP HERE
			if (field.getType() == Collection.class) {
				methods.add(new AddToCollectionMethod(field));
			} else {
				for (Class<?> interfaceClazz : field.getType().getInterfaces()) {
					if (Collection.class == interfaceClazz) {
						methods.add(new AddToCollectionMethod(field));
					}
				}
			}
			return methods;
		} catch (NoSuchMethodException e) {
			return Collections.emptyList();
		}
	}

	private List<BuilderMethod> createSetterMethods(Field field, Class<?> parentClazz) {
		try {
			final Method method = parentClazz.getMethod("set" + StringUtils.capitalize(field.getName()),
					field.getType());
			return asList((BuilderMethod) new SetterMethod(field, method));
		} catch (NoSuchMethodException e) {
			return Collections.emptyList();
		}
	}

	private Class<?> getClazz(String classFqn) {
		try {
			return classLoader.loadClass(classFqn);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}

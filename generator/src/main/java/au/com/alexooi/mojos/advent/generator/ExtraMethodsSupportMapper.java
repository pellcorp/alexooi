/*
 *
 */
package au.com.alexooi.mojos.advent.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.alexooi.mojos.advent.api.extension.ExtraBuilderMethodsSupport;
import au.com.alexooi.mojos.advent.api.extension.ExtraMethods;

class ExtraMethodsSupportMapper {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ClassLoader classLoader;

	ExtraMethodsSupportMapper(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	Map<Class, Class<ExtraBuilderMethodsSupport>> map(List<String> extraMethodSupportFqns) {
		try {
			Map<Class, Class<ExtraBuilderMethodsSupport>> map = new HashMap<Class, Class<ExtraBuilderMethodsSupport>>();
			for (String extraMethodsFqn : extraMethodSupportFqns) {
				Class<ExtraBuilderMethodsSupport> extraMethodsSupportClazz = (Class<ExtraBuilderMethodsSupport>) classLoader
						.loadClass(extraMethodsFqn);
				map(map, extraMethodsSupportClazz);
			}
			return map;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void map(Map<Class, Class<ExtraBuilderMethodsSupport>> map,
			Class<ExtraBuilderMethodsSupport> extraMethodsSupportClazz) throws ClassNotFoundException {
		List<Class> clazzHierarchy = getClassHierarchy(extraMethodsSupportClazz);
		ExtraMethods extraMethods = getExtraMethodsAnnotation(extraMethodsSupportClazz, clazzHierarchy);
		if (extraMethods != null) {
			mapExtraMethodsAnnotatedClazz(map, extraMethodsSupportClazz, extraMethods.forClass(), clazzHierarchy);
		} else {
			logger.warn("The clazz [" + extraMethodsSupportClazz + "] is not annotated with"
					+ "au.com.alexooi.mojos.advent.api.extension.ExtraMethods. "
					+ "This annotation must be present on the clazz so that we know what POJO it adds "
					+ "extra methods for. This class will be ignored until this problem is fixed.");
		}
	}

	private ExtraMethods getExtraMethodsAnnotation(Class<ExtraBuilderMethodsSupport> extraMethodsSupportClazz,
			List<Class> clazzHierarchy) {
		ExtraMethods extraMethods = null;
		for (Class clazz : clazzHierarchy) {
			extraMethods = (ExtraMethods) clazz.getAnnotation(ExtraMethods.class);
			if (extraMethods != null) {
				break;
			}
		}
		return extraMethods;
	}

	private void mapExtraMethodsAnnotatedClazz(Map<Class, Class<ExtraBuilderMethodsSupport>> map,
			Class<ExtraBuilderMethodsSupport> extraMethodsSupportClazz, Class forClazz, List<Class> clazzHierarchy) {
		if (clazzHierarchy.contains(ExtraBuilderMethodsSupport.class)) {
			map.put(forClazz, extraMethodsSupportClazz);
		} else {
			logger.warn("The clazz [" + extraMethodsSupportClazz + "] is annotated with "
					+ "au.com.alexooi.mojos.advent.api.extension.ExtraMethods but does not extend from "
					+ "au.com.alexooi.mojos.advent.api.extension.ExtraBuilderMethodsSupport. Classes "
					+ "annotated with ExtraMethods must inherit from ExtraBuilderMethodsSupport. "
					+ "This clazz will be ignored until this is done.");
		}
	}

	private List<Class> getClassHierarchy(Class clazz) {
		List<Class> classes = new ArrayList<Class>();
		classes.add(clazz);
		Class superclass;
		while ((superclass = clazz.getSuperclass()) != null) {
			classes.add(superclass);
			clazz = superclass;
		}
		return classes;
	}
}

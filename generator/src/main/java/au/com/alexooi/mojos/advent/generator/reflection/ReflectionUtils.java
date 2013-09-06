/*
 *
 */
package au.com.alexooi.mojos.advent.generator.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtils.class);

	public static List<GenericsInformation> getGenericTypes(Field field) {
		Type genericType = field.getGenericType();
		return getNestedTypesFor(genericType);
	}

	public static List<GenericsInformation> getNestedTypesFor(Type genericType) {
		List<GenericsInformation> types = new ArrayList<GenericsInformation>();
		if (genericType instanceof Class) {
			LOGGER.debug("Generic Type is of type class. No need for generic information");
		} else if (genericType instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) genericType;
			for (Type type : parameterizedType.getActualTypeArguments()) {
				if (type instanceof Class) {
					types.add(new GenericsInformation((Class) type));
				} else {
					ParameterizedType nestedParametizedType = (ParameterizedType) type;
					List<GenericsInformation> children = getNestedTypesFor(type);
					GenericsInformation genericsInformation = new GenericsInformation(
							(Class) nestedParametizedType.getRawType(), children);
					types.add(genericsInformation);

				}
			}
		} else {
			throw createUnsupportedException(genericType);
		}
		return types;
	}

	private static UnsupportedOperationException createUnsupportedException(Type genericType) {
		return new UnsupportedOperationException("The type [" + genericType + "] is not yet supported");
	}
}

/*
 *
 */
package au.com.alexooi.mojos.advent.generator.methods;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.com.alexooi.mojos.advent.api.extension.ExtraBuilderMethodsSupport;
import org.apache.commons.lang.StringUtils;

public class ExtraBuilderMethod implements BuilderMethod {
	private final Class<? extends ExtraBuilderMethodsSupport> extraMethodClazz;
	private final Method method;

	public ExtraBuilderMethod(Class<? extends ExtraBuilderMethodsSupport> extraMethodClazz, Method method) {
		this.extraMethodClazz = extraMethodClazz;
		this.method = method;
	}

	public Class<? extends ExtraBuilderMethodsSupport> getExtraMethodClazz() {
		return extraMethodClazz;
	}

	public String getName() {
		return method.getName();
	}

	public List<ExtraMethodParameter> getExtraMethodParameters() {
		List<ExtraMethodParameter> parameters = new ArrayList<ExtraMethodParameter>();
		int i = 1;
		for (Type parameterType : method.getGenericParameterTypes()) {
			parameters.add(new ExtraMethodParameter("p" + i++, parameterType));
		}
		return parameters;
	}

	public String getMethodSignatureParamters() {
		List<String> parameters = new ArrayList<String>();
		for (ExtraMethodParameter extraMethodParameter : getExtraMethodParameters()) {
			parameters.add(extraMethodParameter.getGenericTypeName() + " " + extraMethodParameter.getName());
		}
		return StringUtils.join(parameters, ", ");
	}

	public String getMethodSignatureNames() {
		List<String> parameters = new ArrayList<String>();
		for (ExtraMethodParameter extraMethodParameter : getExtraMethodParameters()) {
			parameters.add(extraMethodParameter.getName());
		}
		return StringUtils.join(parameters, ", ");
	}

	@Override
	public BuilderMethodType getType() {
		return BuilderMethodType.EXTRA;
	}

	public List<Class> getMethodExceptions() {
		Class[] exceptionTypes = method.getExceptionTypes();
		return Arrays.asList(exceptionTypes);
	}

	@Override
	public String getMethodExceptionsSignature() {
		String names = "";
		if (getMethodExceptions().size() > 0) {
			List<String> exceptionNames = new ArrayList<String>();
			for (Class exceptionClazz : getMethodExceptions()) {
				exceptionNames.add(exceptionClazz.getCanonicalName());
			}
			names = "throws " + StringUtils.join(exceptionNames, ", ");
		}
		return names;
	}
}

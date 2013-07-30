/*
 *
 */
package au.com.alexooi.mojos.advent.generator.methods;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import au.com.alexooi.mojos.advent.generator.reflection.GenericsInformation;
import au.com.alexooi.mojos.advent.generator.reflection.ReflectionUtils;
import org.apache.commons.lang.StringUtils;

public class SetterMethod implements BuilderMethod
{
    private final Field field;
    private final Method fieldSetterMethod;

    public SetterMethod(Field field, Method fieldSetterMethod)
    {
        this.field = field;
        this.fieldSetterMethod = fieldSetterMethod;
    }

    public Field getField()
    {
        return field;
    }

    public String getGenericTypeName()
    {
        List<GenericsInformation> genericTypes = ReflectionUtils.getGenericTypes(field);
        String cannonicalName = field.getType().getCanonicalName();
        if (!genericTypes.isEmpty())
        {
            List<String> fullFqns = new ArrayList<String>();
            for (GenericsInformation type : genericTypes)
            {
                fullFqns.add(type.getFullFqn());
            }
            return cannonicalName + "<" + StringUtils.join(fullFqns, ",") + ">";
        }
        return cannonicalName;
    }

    @Override
    public BuilderMethodType getType()
    {
        return BuilderMethodType.SETTER;
    }

    @Override public String getMethodExceptionsSignature()
    {
        final Class<?>[] exceptionTypes = fieldSetterMethod.getExceptionTypes();
        final List<String> exceptionClazzNames = new ArrayList<String>();
        for (Class<?> exceptionType : exceptionTypes)
        {
            exceptionClazzNames.add(exceptionType.getCanonicalName());
        }
        String signature = "";
        if(!exceptionClazzNames.isEmpty()) {
            signature = "throws " + StringUtils.join(exceptionClazzNames, ",");
        }
        return signature;
    }
}

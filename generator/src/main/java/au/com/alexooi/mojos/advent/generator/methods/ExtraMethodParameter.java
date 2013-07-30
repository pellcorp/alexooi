/*
 *
 */
package au.com.alexooi.mojos.advent.generator.methods;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ExtraMethodParameter
{
    private final String name;
    private final Type type;

    public ExtraMethodParameter(String name, Type type)
    {
        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public String getGenericTypeName()
    {
        if (type instanceof Class)
        {
            return ((Class) type).getCanonicalName();
        }
        else if (type instanceof ParameterizedType)
        {
            return type.toString();
        }
        else
        {
            throw createUnsupportedException(type);
        }
    }

    private static UnsupportedOperationException createUnsupportedException(Type genericType)
    {
        return new UnsupportedOperationException("The type [" + genericType
            + "] is not yet supported");
    }

}

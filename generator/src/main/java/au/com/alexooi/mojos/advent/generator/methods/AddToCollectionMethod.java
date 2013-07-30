/*
 *
 */
package au.com.alexooi.mojos.advent.generator.methods;

import java.lang.reflect.Field;
import java.util.List;

import au.com.alexooi.mojos.advent.generator.methods.transformations.WordTransformer;
import au.com.alexooi.mojos.advent.generator.reflection.GenericsInformation;
import au.com.alexooi.mojos.advent.generator.reflection.ReflectionUtils;

import static org.apache.commons.lang.StringUtils.capitalize;

public class AddToCollectionMethod implements BuilderMethod
{
    private final Field field;
    private WordTransformer wordTransformer;

    public AddToCollectionMethod(Field field)
    {
        this.field = field;
        wordTransformer = new WordTransformer();
    }

    public String getGenericTypeName()
    {
        List<GenericsInformation> genericTypes = ReflectionUtils.getGenericTypes(field);
        if (genericTypes.isEmpty())
        {
            return Object.class.getName();
        }
        else
        {
            return genericTypes.get(0).getFullFqn();
        }
    }

    public String getSingularFieldName()
    {
        return wordTransformer.singularize(capitalize(field.getName()));
    }

    @Override
    public BuilderMethodType getType()
    {
        return BuilderMethodType.ADD_TO_COLLECTION;
    }

    @Override public String getMethodExceptionsSignature()
    {
        return null;
    }
}

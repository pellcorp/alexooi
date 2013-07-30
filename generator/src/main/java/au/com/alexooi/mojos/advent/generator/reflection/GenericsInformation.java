/*
 *
 */
package au.com.alexooi.mojos.advent.generator.reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import static java.util.Arrays.asList;

public class GenericsInformation
{
    private final Class clazz;

    private final List<GenericsInformation> children;

    public GenericsInformation(Class clazz, List<GenericsInformation> children)
    {
        this.clazz = clazz;
        this.children = children;
    }

    public GenericsInformation(Class clazz, GenericsInformation... children)
    {
        this.clazz = clazz;
        this.children = asList(children);
    }

    public GenericsInformation(Class clazz)
    {
        this.clazz = clazz;
        this.children = new ArrayList<GenericsInformation>();
    }

    public Class getClazz()
    {
        return clazz;
    }

    public List<GenericsInformation> getChildren()
    {
        return Collections.unmodifiableList(children);
    }

    public String getFullFqn()
    {
        String clazzName = getClazz().getCanonicalName();
        if (!getChildren().isEmpty())
        {
            List<String> classNames = new ArrayList<String>();
            for (GenericsInformation child : children)
            {
                classNames.add(child.getFullFqn());
            }
            clazzName += "<" + StringUtils.join(classNames, ",") + ">";
        }
        return clazzName;
    }
}

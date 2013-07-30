/*
 *
 */
package au.com.alexooi.mojos.advent.api.provisioning;

public class DefaultConstructorPojoProvider<T> implements DefaultPojoProvider<T>
{
    private Class<T> clazz;

    public DefaultConstructorPojoProvider(Class<T> clazz)
    {
        this.clazz = clazz;
    }

    @Override
    public T newInstance()
    {
        try
        {
            return clazz.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}

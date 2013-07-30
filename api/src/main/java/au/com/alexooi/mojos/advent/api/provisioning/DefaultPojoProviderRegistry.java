/*
 *
 */
package au.com.alexooi.mojos.advent.api.provisioning;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class DefaultPojoProviderRegistry
{
    private static final Logger LOG = Logger.getLogger(DefaultPojoProviderRegistry.class);
    private static final Map<Class, DefaultPojoProvider> providers = new HashMap<Class, DefaultPojoProvider>();

    public static <M extends DefaultPojoProvider<T>, T> void register(Class<T> pojoClazz, M provider)
    {
        if (provider == null)
        {
            throw new IllegalArgumentException("The DefaultPojoProvider cannot be null");
        }
        providers.put(pojoClazz, provider);
    }

    @SuppressWarnings(value = {"unchecked"})
    public static <T> DefaultPojoProvider<T> getProvider(Class<T> pojoClazz)
    {
        DefaultPojoProvider provider = providers.get(pojoClazz);
        if (provider == null)
        {
            LOG.warn("No provider has been registered for [" + pojoClazz
                + "]. Using the default constructor provider for the time being. If "
                + "you wish to provide your own default values when a [" + pojoClazz
                + "] is constructed then consider implementing the "
                + "au.com.alexooi.mojos.advent.api.DefaultPojoProvider interface and "
                + "providing your own default values.");
            provider = new DefaultConstructorPojoProvider(pojoClazz);
        }
        return provider;
    }
}

/*
 *
 */
package au.com.alexooi.mojos.advent.api.listeners;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class PojoUpdateListenerRegistry
{
    private static final Logger LOG = Logger.getLogger(PojoUpdateListenerRegistry.class);

    private static final Map<Class, PojoUpdateListener> classToUpdateListenerMappings =
        new HashMap<Class, PojoUpdateListener>();

    public static <T> void register(Class<T> clazz, PojoUpdateListener<T> updateListener)
    {
        LOG.info(MessageFormat.format("Registering listener [{0}] for POJO clazz [{1}]",
            updateListener.getClass(), clazz));
        classToUpdateListenerMappings.put(clazz, updateListener);
    }

    public static <T> PojoUpdateListener<T> getUpdateListener(Class<T> clazz)
    {
        PojoUpdateListener<T> updateListener = classToUpdateListenerMappings.get(clazz);
        LOG.debug(MessageFormat.format("Found listener [{0}] for clazz [{1}]", updateListener, clazz));
        return updateListener;
    }
}

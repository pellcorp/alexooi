/*
 *
 */
package au.com.alexooi.mojos.advent.api.persistence;

import java.util.HashMap;
import java.util.Map;

public class PojoPersistenceManagerRegistry
{
    private static final Map<Class, PojoPersistenceManager> managers = new HashMap<Class, PojoPersistenceManager>();

    private static PojoPersistenceManager defaultManager = new NoOpPojoPersistenceManager();

    public static void registerDefaultManager(PojoPersistenceManager newDefaultManager)
    {
        if (newDefaultManager == null)
        {
            throw new IllegalArgumentException("The default PojoPersistenceManager cannot be null");
        }
        defaultManager = newDefaultManager;
    }

    public static <T, M extends PojoPersistenceManager<T>> void register(Class<T> clazz, M persistenceManager)
    {
        managers.put(clazz, persistenceManager);
    }

    @SuppressWarnings(value = {"unchecked"})
    public static <T> PojoPersistenceManager<T> getManager(Class<T> pojoClazz)
    {
        PojoPersistenceManager returnManager = managers.get(pojoClazz);
        if (returnManager == null)
        {
            returnManager = defaultManager;
        }
        return returnManager;
    }

}

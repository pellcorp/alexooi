/*
 *
 */
package au.com.alexooi.mojos.advent.api.persistence;

import org.apache.log4j.Logger;

class NoOpPojoPersistenceManager<T> implements PojoPersistenceManager<T>
{
    private static final Logger LOG = Logger.getLogger(NoOpPojoPersistenceManager.class);

    @Override
    public T persist(T pojo)
    {
        LOG.warn("No Persistence Manager has been defined. A no-op one is being used instead. "
            + "This does NOT do any persistence! If you need to persist some data then you "
            + "really should define your own implementation of the PojoPersistenceManager");
        return pojo;
    }
}

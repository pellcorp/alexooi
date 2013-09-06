/*
 *
 */
package au.com.alexooi.mojos.advent.api.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class NoOpPojoPersistenceManager<T> implements PojoPersistenceManager<T> {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public T persist(T pojo) {
		logger.warn("No Persistence Manager has been defined. A no-op one is being used instead. "
				+ "This does NOT do any persistence! If you need to persist some data then you "
				+ "really should define your own implementation of the PojoPersistenceManager");
		return pojo;
	}
}

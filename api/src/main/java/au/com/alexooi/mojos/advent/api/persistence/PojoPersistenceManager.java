/*
 *
 */
package au.com.alexooi.mojos.advent.api.persistence;

public interface PojoPersistenceManager<T> {
	T persist(T pojo);
}

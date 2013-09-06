/*
 *
 */
package au.com.alexooi.mojos.advent.api.provisioning;

public interface DefaultPojoProvider<T> {
	T newInstance();
}

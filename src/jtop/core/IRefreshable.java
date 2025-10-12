package jtop.core;
/**
 * Interface for objects that can be refreshed periodically.
 */
public interface IRefreshable {
	/**
	 * Refresh the object.
	 * Implementations should update internal state or display as needed.
	 */
	void refresh();
}
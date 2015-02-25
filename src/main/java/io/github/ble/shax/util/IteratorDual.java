package io.github.ble.shax.util;

public interface IteratorDual<T> {
	void next(T t);
	void done(); //aka `hasntNext()`
}

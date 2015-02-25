package io.github.ble.shax.util;

//why am I not using java 8?
public interface Function2<A, B, C> {
	C apply(A a, B b);
}

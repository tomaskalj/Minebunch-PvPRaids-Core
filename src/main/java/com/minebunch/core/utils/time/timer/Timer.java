package com.minebunch.core.utils.time.timer;

public interface Timer {
	boolean isActive(boolean autoReset);

	boolean isActive();

	String formattedExpiration();

	void reset();
}

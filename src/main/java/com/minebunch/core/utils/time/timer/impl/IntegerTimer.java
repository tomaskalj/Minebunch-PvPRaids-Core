package com.minebunch.core.utils.time.timer.impl;

import com.minebunch.core.utils.time.TimeUtil;
import com.minebunch.core.utils.time.timer.AbstractTimer;
import java.util.concurrent.TimeUnit;

public class IntegerTimer extends AbstractTimer {
	public IntegerTimer(TimeUnit unit, int amount) {
		super(unit, amount);
	}

	@Override
	public String formattedExpiration() {
		return TimeUtil.formatTimeMillis(TimeUnit.NANOSECONDS.toMillis(expiry - System.nanoTime()));
	}
}

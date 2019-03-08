package com.minebunch.core.utils.time.timer.impl;

import com.minebunch.core.utils.time.timer.AbstractTimer;
import java.util.concurrent.TimeUnit;

public class DoubleTimer extends AbstractTimer {
    public DoubleTimer(int seconds) {
        super(TimeUnit.SECONDS, seconds);
    }

    @Override
    public String formattedExpiration() {
        double seconds = (expiry - System.nanoTime()) / 1000000000.0;
        return String.format("%.1f seconds", seconds);
    }
}

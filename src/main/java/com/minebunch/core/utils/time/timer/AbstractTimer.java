package com.minebunch.core.utils.time.timer;

import java.util.concurrent.TimeUnit;

public abstract class AbstractTimer implements Timer {
    private final long nanos;
    protected long expiry;

    protected AbstractTimer(TimeUnit unit, int amount) {
        this.nanos = unit.toNanos(amount);
    }

    @Override
    public boolean isActive() {
        return isActive(true);
    }

    @Override
    public boolean isActive(boolean autoReset) {
        boolean active = System.nanoTime() < expiry;

        if (autoReset && !active) {
            reset();
        }

        return active;
    }

    @Override
    public void reset() {
        this.expiry = System.nanoTime() + nanos;
    }
}

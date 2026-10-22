package com.mentality.homeostatic_heater;

/**
 * Server-owned fuel queue. The single lava allowance includes the currently
 * burning lava bucket, so adding another bucket cannot bypass the limit.
 * Turning the heater off pauses consumption without destroying stored fuel.
 */
public final class HeaterFuelState {
    private int burnTicksLeft;
    private int queuedCoal;
    private boolean burningLava;
    private boolean queuedLava;
    private boolean enabled = true;

    public boolean addCoal(HeaterConfig config) {
        int coalCount = queuedCoal + (burnTicksLeft > 0 && !burningLava ? 1 : 0);
        if (coalCount >= config.maxCoal()) return false;
        if (burnTicksLeft == 0) {
            burnTicksLeft = config.burnTicksPerCoal();
            burningLava = false;
        } else {
            queuedCoal++;
        }
        return true;
    }

    public boolean addLava(HeaterConfig config) {
        if (queuedLava || (burnTicksLeft > 0 && burningLava)) return false;
        if (burnTicksLeft == 0) {
            burnTicksLeft = config.burnTicksPerLava();
            burningLava = true;
        } else {
            queuedLava = true;
        }
        return true;
    }

    /** Runs only on the logical server. A queued lava bucket burns before queued coal. */
    public void tick(HeaterConfig config) {
        if (!enabled || config.alwaysActive()) return;
        if (burnTicksLeft > 0) burnTicksLeft--;
        if (burnTicksLeft != 0) return;
        burningLava = false;
        if (queuedLava) {
            queuedLava = false;
            burningLava = true;
            burnTicksLeft = config.burnTicksPerLava();
        } else if (queuedCoal > 0) {
            queuedCoal--;
            burnTicksLeft = config.burnTicksPerCoal();
        }
    }

    public void restore(int burnTicks, int coal, boolean wasLava, boolean lavaWaiting, boolean wasEnabled) {
        burnTicksLeft = Math.max(0, burnTicks);
        queuedCoal = Math.max(0, Math.min(64, coal));
        burningLava = burnTicksLeft > 0 && wasLava;
        queuedLava = lavaWaiting && !burningLava;
        enabled = wasEnabled;
    }

    public boolean isLit(HeaterConfig config) {
        return enabled && (config.alwaysActive() || burnTicksLeft > 0);
    }

    public long remainingTicks(HeaterConfig config) {
        return FuelDuration.totalTicks(burnTicksLeft, queuedCoal, config.burnTicksPerCoal())
                + (queuedLava ? (long) config.burnTicksPerLava() : 0L);
    }

    public boolean toggleEnabled() { enabled = !enabled; return enabled; }
    public boolean enabled() { return enabled; }
    public int burnTicksLeft() { return burnTicksLeft; }
    public int queuedCoal() { return queuedCoal; }
    public boolean burningLava() { return burningLava; }
    public boolean queuedLava() { return queuedLava; }
}

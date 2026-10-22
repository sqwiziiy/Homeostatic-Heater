package com.mentality.homeostatic_heater;

/** Server-computed status sent to clients for display only. */
public record HeaterDisplayStatus(boolean active, boolean alwaysActive, FuelDuration remaining) {
    public static HeaterDisplayStatus fromFuel(int burnTicksLeft, int queuedCoal,
                                                int ticksPerCoal, boolean alwaysActive) {
        FuelDuration remaining = FuelDuration.fromTicks(
                FuelDuration.totalTicks(burnTicksLeft, queuedCoal, ticksPerCoal));
        return new HeaterDisplayStatus(alwaysActive || burnTicksLeft > 0, alwaysActive, remaining);
    }
}

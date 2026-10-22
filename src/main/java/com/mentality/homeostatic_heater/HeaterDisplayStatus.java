package com.mentality.homeostatic_heater;

/** Server-computed display state, sent to clients without allowing client-side edits. */
public record HeaterDisplayStatus(boolean active, boolean alwaysActive, boolean enabled, FuelDuration remaining) {
    public static HeaterDisplayStatus fromFuel(HeaterFuelState fuel, HeaterConfig config) {
        return new HeaterDisplayStatus(fuel.isLit(config), config.alwaysActive(), fuel.enabled(),
                FuelDuration.fromTicks(fuel.remainingTicks(config)));
    }
}

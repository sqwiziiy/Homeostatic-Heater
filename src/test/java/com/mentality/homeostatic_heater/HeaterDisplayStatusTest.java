package com.mentality.homeostatic_heater;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HeaterDisplayStatusTest {
    private static final HeaterConfig CONFIG = new HeaterConfig(1600, 20000, 16, false);

    @Test
    void emptyHeaterIsInactive() {
        HeaterDisplayStatus status = HeaterDisplayStatus.fromFuel(new HeaterFuelState(), CONFIG);
        assertFalse(status.active());
        assertFalse(status.alwaysActive());
        assertTrue(status.enabled());
        assertEquals(new FuelDuration(0, 0), status.remaining());
    }

    @Test
    void queuedFuelIsIncludedInDisplay() {
        HeaterFuelState fuel = new HeaterFuelState();
        assertTrue(fuel.addCoal(CONFIG));
        assertTrue(fuel.addLava(CONFIG));
        HeaterDisplayStatus status = HeaterDisplayStatus.fromFuel(fuel, CONFIG);
        assertTrue(status.active());
        assertEquals(new FuelDuration(18, 0), status.remaining());
    }

    @Test
    void pauseIsDifferentFromNoFuelAndAlwaysActiveHonorsPause() {
        HeaterFuelState fuel = new HeaterFuelState();
        assertTrue(fuel.addCoal(CONFIG));
        fuel.toggleEnabled();
        HeaterDisplayStatus disabled = HeaterDisplayStatus.fromFuel(fuel, CONFIG);
        assertFalse(disabled.active());
        assertFalse(disabled.enabled());
        assertEquals(new FuelDuration(1, 20), disabled.remaining());
        HeaterDisplayStatus freeButDisabled = HeaterDisplayStatus.fromFuel(fuel,
                new HeaterConfig(1600, 20000, 16, true));
        assertTrue(freeButDisabled.alwaysActive());
        assertFalse(freeButDisabled.active());
    }
}

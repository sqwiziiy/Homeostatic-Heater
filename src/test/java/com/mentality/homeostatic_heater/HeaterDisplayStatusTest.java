package com.mentality.homeostatic_heater;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HeaterDisplayStatusTest {
    @Test
    void serverMarksEmptyHeaterInactive() {
        HeaterDisplayStatus status = HeaterDisplayStatus.fromFuel(0, 0, 1600, false);
        assertFalse(status.active());
        assertFalse(status.alwaysActive());
        assertEquals(new FuelDuration(0, 0), status.remaining());
    }

    @Test
    void serverComputesRemainingMinutesAndSeconds() {
        HeaterDisplayStatus status = HeaterDisplayStatus.fromFuel(20, 2, 1600, false);
        assertTrue(status.active());
        assertEquals(new FuelDuration(2, 41), status.remaining());
    }

    @Test
    void serverMarksAlwaysActiveWithoutFuel() {
        HeaterDisplayStatus status = HeaterDisplayStatus.fromFuel(0, 0, 1600, true);
        assertTrue(status.active());
        assertTrue(status.alwaysActive());
    }
}

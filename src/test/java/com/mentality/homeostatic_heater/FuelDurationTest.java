package com.mentality.homeostatic_heater;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FuelDurationTest {
    @Test
    void roundsUpPartialSecondsWithoutInventingFuel() {
        assertEquals(new FuelDuration(0, 0), FuelDuration.fromTicks(0));
        assertEquals(new FuelDuration(0, 1), FuelDuration.fromTicks(1));
        assertEquals(new FuelDuration(0, 1), FuelDuration.fromTicks(20));
        assertEquals(new FuelDuration(0, 2), FuelDuration.fromTicks(21));
        assertEquals(new FuelDuration(0, 0), FuelDuration.fromTicks(-20));
    }

    @Test
    void countsCurrentAndQueuedCoal() {
        assertEquals(3_220L, FuelDuration.totalTicks(20, 2, 1600));
        assertEquals(new FuelDuration(2, 41), FuelDuration.fromTicks(3_220));
        assertEquals("01", FuelDuration.fromTicks(20).paddedSeconds());
    }

    @Test
    void handlesLongConfiguredBurnsWithoutIntegerOverflow() {
        assertEquals(110_592_000L, FuelDuration.totalTicks(0, 64, 1_728_000));
    }
}

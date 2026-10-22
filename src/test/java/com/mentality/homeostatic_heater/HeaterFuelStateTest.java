package com.mentality.homeostatic_heater;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HeaterFuelStateTest {
    private static final HeaterConfig CONFIG = new HeaterConfig(1600, 20000, 16, false);

    @Test
    void lavaIsLongerThanCoalAndReturnsToIdleAfterBurn() {
        HeaterFuelState fuel = new HeaterFuelState();
        assertTrue(fuel.addLava(CONFIG));
        assertEquals(20000L, fuel.remainingTicks(CONFIG));
        assertTrue(fuel.isLit(CONFIG));
        for (int i = 0; i < 20000; i++) fuel.tick(CONFIG);
        assertEquals(0L, fuel.remainingTicks(CONFIG));
        assertFalse(fuel.isLit(CONFIG));
        assertTrue(fuel.addLava(CONFIG));
    }

    @Test
    void acceptsAtMostOneLavaBucketIncludingQueuedAndBurning() {
        HeaterFuelState fuel = new HeaterFuelState();
        assertTrue(fuel.addCoal(CONFIG));
        assertTrue(fuel.addLava(CONFIG)); // queued while coal burns
        assertFalse(fuel.addLava(CONFIG)); // no second lava bucket
        assertEquals(21600L, fuel.remainingTicks(CONFIG));
        for (int i = 0; i < 1600; i++) fuel.tick(CONFIG);
        assertTrue(fuel.burningLava());
        assertFalse(fuel.queuedLava());
        assertFalse(fuel.addLava(CONFIG));
    }

    @Test
    void queuedLavaHasPriorityButCoalRemainsStored() {
        HeaterFuelState fuel = new HeaterFuelState();
        assertTrue(fuel.addCoal(CONFIG));
        assertTrue(fuel.addCoal(CONFIG));
        assertTrue(fuel.addLava(CONFIG));
        assertEquals(23200L, fuel.remainingTicks(CONFIG));
        for (int i = 0; i < 1600; i++) fuel.tick(CONFIG);
        assertTrue(fuel.burningLava());
        assertEquals(21600L, fuel.remainingTicks(CONFIG));
        for (int i = 0; i < 20000; i++) fuel.tick(CONFIG);
        assertFalse(fuel.burningLava());
        assertEquals(1600, fuel.burnTicksLeft());
        assertEquals(0, fuel.queuedCoal());
    }

    @Test
    void coalCapDoesNotCountLavaAndRejectsOverfill() {
        HeaterConfig tiny = new HeaterConfig(1600, 20000, 2, false);
        HeaterFuelState fuel = new HeaterFuelState();
        assertTrue(fuel.addLava(tiny));
        assertTrue(fuel.addCoal(tiny));
        assertTrue(fuel.addCoal(tiny));
        assertFalse(fuel.addCoal(tiny));
        assertEquals(23200L, fuel.remainingTicks(tiny));
    }

    @Test
    void manualTogglePausesTimerAndRadiationIncludingAlwaysActiveMode() {
        HeaterFuelState fuel = new HeaterFuelState();
        assertTrue(fuel.addLava(CONFIG));
        assertFalse(fuel.toggleEnabled());
        for (int i = 0; i < 100; i++) fuel.tick(CONFIG);
        assertEquals(20000L, fuel.remainingTicks(CONFIG));
        assertFalse(fuel.isLit(CONFIG));
        HeaterConfig free = new HeaterConfig(1600, 20000, 16, true);
        assertFalse(fuel.isLit(free));
        assertTrue(fuel.toggleEnabled());
        assertTrue(fuel.isLit(free));
        fuel.tick(free);
        assertEquals(20000L, fuel.remainingTicks(free));
        fuel.tick(CONFIG);
        assertEquals(19999L, fuel.remainingTicks(CONFIG));
    }

    @Test
    void restoreOlderCoalSavesDefaultsToEnabledAndKeepsFuel() {
        HeaterFuelState fuel = new HeaterFuelState();
        fuel.restore(300, 3, false, false, true);
        assertTrue(fuel.enabled());
        assertEquals(5100L, fuel.remainingTicks(CONFIG));
        assertTrue(fuel.isLit(CONFIG));
    }

    @Test
    void restoreNewLavaStateAndDisabledFlagRoundTrips() {
        HeaterFuelState fuel = new HeaterFuelState();
        fuel.restore(4321, 2, true, false, false);
        assertFalse(fuel.enabled());
        assertTrue(fuel.burningLava());
        assertEquals(7521L, fuel.remainingTicks(CONFIG));
        assertFalse(fuel.isLit(CONFIG));
        fuel.toggleEnabled();
        assertTrue(fuel.isLit(CONFIG));
    }
}

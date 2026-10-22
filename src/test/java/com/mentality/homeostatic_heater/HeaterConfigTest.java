package com.mentality.homeostatic_heater;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class HeaterConfigTest {
    @Test
    void acceptsCustomSettingsAndDefaultsMissingKeys() {
        assertEquals(new HeaterConfig(200, 8000, 3, true), HeaterConfig.parse("""
                {"burn_ticks_per_coal": 200, "burn_ticks_per_lava": 8000, "max_coal": 3, "always_active": true}
                """));
        assertEquals(new HeaterConfig(1600, 20000, 16, false), HeaterConfig.parse("{}"));
        // Existing config files without the lava setting must still work.
        assertEquals(new HeaterConfig(200, 20000, 3, false), HeaterConfig.parse("""
                {"burn_ticks_per_coal": 200, "max_coal": 3}
                """));
    }

    @Test
    void rejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class,
                () -> HeaterConfig.parse("{\"burn_ticks_per_coal\": 0}"));
        assertThrows(IllegalArgumentException.class,
                () -> HeaterConfig.parse("{\"burn_ticks_per_lava\": 0}"));
        assertThrows(IllegalArgumentException.class,
                () -> HeaterConfig.parse("{\"max_coal\": 65}"));
        assertThrows(IllegalArgumentException.class,
                () -> HeaterConfig.parse("{\"always_active\": \"yes\"}"));
    }
}

package com.mentality.homeostatic_heater;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Server settings, loaded once at startup; invalid files are preserved. */
public record HeaterConfig(int burnTicksPerCoal, int burnTicksPerLava, int maxCoal, boolean alwaysActive) {
    private static final Logger LOGGER = LoggerFactory.getLogger("homeostatic_heater");
    private static final String DEFAULT_JSON = """
            {
              "burn_ticks_per_coal": 1600,
              "burn_ticks_per_lava": 20000,
              "max_coal": 16,
              "always_active": false
            }
            """;

    public static HeaterConfig load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("homeostatic_heater.json");
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, DEFAULT_JSON, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
            }
            return parse(Files.readString(path, StandardCharsets.UTF_8));
        } catch (IOException | RuntimeException exception) {
            LOGGER.warn("Cannot read {}; using default heater settings without changing the file", path, exception);
            return new HeaterConfig(1600, 20000, 16, false);
        }
    }

    static HeaterConfig parse(String jsonText) {
        JsonObject json = JsonParser.parseString(jsonText).getAsJsonObject();
        return new HeaterConfig(
                integer(json, "burn_ticks_per_coal", 1600, 1, 1_728_000),
                integer(json, "burn_ticks_per_lava", 20000, 1, 1_728_000),
                integer(json, "max_coal", 16, 1, 64),
                booleanValue(json, "always_active", false));
    }

    private static int integer(JsonObject json, String key, int defaultValue, int minimum, int maximum) {
        if (!json.has(key)) return defaultValue;
        JsonElement value = json.get(key);
        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
            throw new IllegalArgumentException(key + " must be an integer");
        }
        int result = Integer.parseInt(value.getAsString());
        if (result < minimum || result > maximum) {
            throw new IllegalArgumentException(key + " must be between " + minimum + " and " + maximum);
        }
        return result;
    }

    private static boolean booleanValue(JsonObject json, String key, boolean defaultValue) {
        if (!json.has(key)) return defaultValue;
        JsonElement value = json.get(key);
        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean()) {
            throw new IllegalArgumentException(key + " must be true or false");
        }
        return value.getAsBoolean();
    }
}

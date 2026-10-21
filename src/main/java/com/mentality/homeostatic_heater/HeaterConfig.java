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

/** Server-side settings loaded at startup; invalid files are never overwritten. */
public record HeaterConfig(int burnTicksPerCoal, int maxCoal, boolean alwaysActive) {
    private static final Logger LOGGER = LoggerFactory.getLogger("homeostatic_heater");
    private static final String DEFAULT_JSON = """
            {
              "burn_ticks_per_coal": 1600,
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
            JsonObject json = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
            return new HeaterConfig(
                    integer(json, "burn_ticks_per_coal", 1600, 1, 1_728_000),
                    integer(json, "max_coal", 16, 1, 64),
                    booleanValue(json, "always_active", false));
        } catch (IOException | RuntimeException exception) {
            LOGGER.warn("Cannot read {}; using default heater settings without changing the file", path, exception);
            return new HeaterConfig(1600, 16, false);
        }
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

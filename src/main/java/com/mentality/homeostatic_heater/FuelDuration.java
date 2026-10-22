package com.mentality.homeostatic_heater;

/** Small, Minecraft-independent helpers for the fuel timer shown on the HUD. */
public record FuelDuration(long minutes, int seconds) {
    public static long totalTicks(int burningTicks, int queuedCoal, int ticksPerCoal) {
        return Math.max(0, burningTicks) + (long) Math.max(0, queuedCoal) * Math.max(0, ticksPerCoal);
    }

    public static FuelDuration fromTicks(long ticks) {
        long positiveTicks = Math.max(0, ticks);
        long totalSeconds = positiveTicks / 20 + (positiveTicks % 20 == 0 ? 0 : 1);
        return new FuelDuration(totalSeconds / 60, (int) (totalSeconds % 60));
    }

    public String paddedSeconds() {
        return seconds < 10 ? "0" + seconds : Integer.toString(seconds);
    }
}

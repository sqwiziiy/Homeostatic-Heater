# In-game test checklist

[Русская версия](TESTING_RU.md)

Test in a disposable Minecraft 1.20.1 Fabric world with Fabric API and
Homeostatic. Back up your main world and remove the old heater jar first.

1. Craft the heater using the [recipe](README.md#crafting). The **Heating Season** advancement should appear with a campfire icon. `/give` should not award it.
2. Place it. Hold Shift and aim at the block: without fuel, the HUD should say **Inactive**.
3. Add one coal. The ignition sound should play, and the HUD should start at about **1 min 20 sec** with the default config, decreasing about once per second. Occasional small smoke particles should rise from the top.
4. Load 16 pieces, then try another. The extra coal should remain in your hand and the full message should appear. Listen for occasional quiet crackling.
5. Wait until it turns off. Fire, light, and smoke should stop, and the HUD should say **Inactive**. Restart the server while it is burning and check that fuel time persists.
6. Set `burn_ticks_per_coal` to `200`, `max_coal` to `2`, and `always_active` to `false` in the server config. Restart and check 10 seconds per coal and the two-piece limit.
7. Set `always_active` to `true` and restart. An empty heater should stay lit, the HUD should say **Always active**, and coal should not be consumed. Restore `false` afterward.
8. With Homeostatic, compare body temperature near a lit and unlit heater, then outside its range. If your test world has old `mentality_heater` or `stormheater` blocks, check that they still load.
9. Join with two clients. Both should receive the same server-computed status and remaining time. Changing a client's files must not alter the server's fuel consumption or warmth.

CI checks the build, resource JSON, and Java unit tests. Rendering, smoke,
sound, crafting, and Homeostatic behavior require manual client testing.

## v1.6.0 regression checks

1. With Homeostatic installed, compare player temperature by an **unlit** heater, an active coal heater, a manually switched-off heater, and an active lava heater.
2. Add one lava bucket: one empty bucket must return; confirm 16 min 40 sec on HUD with default settings. Try a second lava bucket: it must remain in hand.
3. Queue lava behind coal and coal behind lava; fuel must continue without creating or deleting extra items.
4. Turn off with empty main hand; verify smoke, light and warming stop immediately and the timer freezes. Turn on and verify it resumes.
5. Restart dedicated server with burning and queued lava, with manual switch off, and with older v1.5.0/1.2.0 heaters. Fuel and state must survive.
6. Mine with a pickaxe in Survival: heaters using new and legacy IDs must drop. Try bare hands and confirm no free drop with requiresTool.
7. Use two clients in multiplayer and test full inventory, Creative, chunk unload/reload and `always_active=true`.
8. Confirm GitHub Release publish workflow builds the exact version tag; publishing to Modrinth requires credentials.

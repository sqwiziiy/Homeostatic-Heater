# Homeostatic Heater (Fabric 1.20.1)

Coal Heater is a small coal-fired block. It integrates with
Homeostatic **only through Homeostatic's published datapack block-radiation
loader**. There are no mixins, reflection, patched Homeostatic classes, or
world-conversion code.

## In game

Craft it with six iron ingots, two redstone dust, and a furnace:

```text
Iron      Redstone  Iron
Iron      Furnace   Iron
Iron      Redstone  Iron
```

Find it in the Functional Blocks creative tab or use
`/give @s homeostatic_heater:coal_heater`. Right-click it with coal or
charcoal to add fuel. Each piece burns for 1,600 ticks (80 seconds); up to 16
pieces can be buffered. It emits light while burning and drops itself when
broken (unused fuel is intentionally not recovered).

It has no inventory screen: coal is inserted directly by right-clicking.
The front grille has separate dark and glowing 16×16 pixel textures, the sides
are iron vents, and the top is a vented metal plate. The inventory item uses a
clear front-face icon. Successfully adding fuel plays the flint-and-steel
ignition sound; a full heater does not consume coal or play the sound.

When Homeostatic is installed, a burning heater is registered as a heat-radiant
block with `max_radiation: 8325.0`, the same value Homeostatic uses for a lit
Create blaze burner. Homeostatic's 1.20.1 source scans from a player only 12
blocks horizontally and 3 blocks above, so a heater effectively covers a
25×25 area on the same floor, subject to Homeostatic's normal distance and
obstruction calculations. Use several heaters in a large villa; a single block
cannot safely extend that fixed Homeostatic scan radius.

## Configure heat strength

The built-in compatibility file is
`data/homeostatic_heater/environment/block_radiation/coal_heater.json`. To override
it without changing the jar, zip the *contents* of `server-config-datapack/`
as `homeostatic_heater-config.zip`, place it in `<world>/datapacks/`, edit
`max_radiation`, then run `/reload`. Higher numbers heat more strongly, but do
not increase Homeostatic's 12-block scan radius. The supplied 8325 is a
reasonable indoor default.

## Build with Java 17

Use a JDK 17, even if the production server launches with a newer Java:

```sh
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
./gradlew build
```

The distributable jar is `build/libs/homeostatic-heater-1.2.0.jar`.

## Install

1. Install Fabric Loader for Minecraft 1.20.1 and Fabric API on the server and
   each client.
2. Install a Fabric 1.20.1 Homeostatic release (the mod is marked suggested,
   not hard-required, so the heater itself also works without it).
3. Remove the older heater jar, then copy this mod jar to both
   the server's and every client's `mods/` folders. Keep only one version.
4. Start the server, join with a matching client mod set, and craft/test a
   heater. Do **not** add this only to the server: it adds a block and must be
   present on clients too.

The Fabric mod ID is `homeostatic_heater`; Java sources use
`com.mentality.homeostatic_heater`. Blocks, block entities, and items saved
under the earlier `mentality_heater` and `stormheater` IDs are still registered
so existing placed heaters and inventory items load. Newly crafted blocks use
the new ID. Back up the world before changing any installed mods.

License: GNU General Public License version 3 only (`GPL-3.0-only`). The full
terms are in `LICENSE`. The game textures in `artwork/` and `src/main/resources/`
are original assets created for this mod and distributed under the same license.

## Verification checklist

* Run `./gradlew build` under JDK 17.
* In a disposable world, place a heater, add coal, and confirm its light turns
  on; wait 80 seconds and confirm it turns off.
* With Homeostatic installed, compare the body-temperature HUD near a lit and
  an unlit heater, then repeat at 13+ horizontal blocks to confirm the expected
  Homeostatic range limit.
* Restart the server while it is lit; it must resume burning rather than lose
  its timer.

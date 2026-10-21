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
charcoal to add fuel. By default, each piece burns for 1,600 ticks (80 seconds)
and the heater holds up to 16 pieces, counting the currently burning one. If
it is full, an action-bar message appears and no coal is consumed. It emits
light while burning and drops itself when broken (unused fuel is intentionally
not recovered).

It has no inventory screen: coal is inserted directly by right-clicking.
The front grille has a dark 16×16 texture and a gently interpolated two-frame
16×16 fire animation while lit. The sides are iron vents and the top is a
vented metal plate. The inventory item is a 3D block model, not a flat sprite.
Successfully adding fuel plays the flint-and-steel ignition sound; a full
heater does not consume coal or play the sound. A running heater occasionally
plays a quiet campfire crackle.

## Server settings

After the first server start, edit `config/homeostatic_heater.json` in the
server directory (an example is in `example-config/`):

```json
{
  "burn_ticks_per_coal": 1600,
  "max_coal": 16,
  "always_active": false
}
```

There are 20 ticks per second. `burn_ticks_per_coal` accepts 1–1,728,000;
`max_coal` accepts 1–64. Set `always_active` to `true` to keep the heater lit
and heating without consuming coal. Already stored coal is preserved and will
be used if this setting is turned off later. Restart the server after edits;
the client uses the server's lit block state and does not need a matching
config. Invalid config files are left untouched and the defaults are used;
check the server log for a warning.

Existing 1.2.0 and older heaters migrate their remaining burn time when loaded.
Lowering `max_coal` does not delete fuel already stored; it only prevents new
coal from being inserted until the stored amount falls below the new limit.

When Homeostatic is installed, a lit heater is registered as a heat-radiant
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

The distributable jar is `build/libs/homeostatic-heater-1.3.0.jar`.

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
  on; wait 80 seconds and confirm it turns off at the default setting.
* Fill it to the configured limit; another coal should remain in hand and a
  message should appear. Listen for ignition and occasional crackling.
* Test `always_active: true` without fuel, then restore `false`; confirm it
  returns to normal fuel use. Check the animated front and 3D inventory model.
* With Homeostatic installed, compare the body-temperature HUD near a lit and
  an unlit heater, then repeat at 13+ horizontal blocks to confirm the expected
  Homeostatic range limit.
* Restart the server while it is lit; it must resume burning rather than lose
  its timer.

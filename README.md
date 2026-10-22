# 🔥 Homeostatic Heater

[Russian version](README_RU.md)

[![Build and test](https://github.com/sqwiziiy/Homeostatic-Heater/actions/workflows/build.yml/badge.svg)](https://github.com/sqwiziiy/Homeostatic-Heater/actions/workflows/build.yml)
![Minecraft 1.20.1](https://img.shields.io/badge/Minecraft-1.20.1-3C8527)
![Fabric](https://img.shields.io/badge/Loader-Fabric-DBD0B4)
![Java 17](https://img.shields.io/badge/Java-17-E76F00)
![GPL-3.0-only](https://img.shields.io/badge/License-GPL--3.0--only-3B57A6)

A compact coal-fired heater for Minecraft Fabric 1.20.1. It warms nearby players
through Homeostatic's block-radiation datapack system, without mixins, patched
Homeostatic classes, or world conversion.

[Download the latest release](https://github.com/sqwiziiy/Homeostatic-Heater/releases/latest) ·
[Installation](#installation) · [In-game test checklist](TESTING.md) ·
[Changelog](CHANGELOG.md)

## Features

- Heats players while lit, following Homeostatic's range and obstruction rules.
- Hold Shift and aim at a placed heater to see its remaining minutes and seconds,
  **Inactive**, or **Always active**. The server computes the status and time;
  clients only display the synchronized result.
- Crafting it awards the **Heating Season** advancement with a campfire icon.
- Configurable burn time, coal capacity, and fuel-free always-active mode.
- A 3D inventory model, gently animated fire, ignition sound, occasional
  crackling, and occasional smoke from the top while active.

## Crafting

Six iron ingots, two redstone dust, and one furnace make one heater:

<table>
  <tr>
    <td><img src='docs/recipe/iron_ingot.png' width='40' height='40' alt='Iron ingot'></td>
    <td><img src='docs/recipe/redstone.png' width='40' height='40' alt='Redstone dust'></td>
    <td><img src='docs/recipe/iron_ingot.png' width='40' height='40' alt='Iron ingot'></td>
  </tr>
  <tr>
    <td><img src='docs/recipe/iron_ingot.png' width='40' height='40' alt='Iron ingot'></td>
    <td><img src='docs/recipe/furnace_front.png' width='40' height='40' alt='Furnace'></td>
    <td><img src='docs/recipe/iron_ingot.png' width='40' height='40' alt='Iron ingot'></td>
  </tr>
  <tr>
    <td><img src='docs/recipe/iron_ingot.png' width='40' height='40' alt='Iron ingot'></td>
    <td><img src='docs/recipe/redstone.png' width='40' height='40' alt='Redstone dust'></td>
    <td><img src='docs/recipe/iron_ingot.png' width='40' height='40' alt='Iron ingot'></td>
  </tr>
</table>

→ <img src='src/main/resources/assets/homeostatic_heater/textures/item/coal_heater.png' width='40' height='40' alt='Coal Heater'> **Coal Heater**

The ingredient PNGs are Minecraft 1.20.1 textures (© Mojang Studios) used
only to illustrate the recipe; they are not included in the mod jar.

## Using the heater

Find it in the **Functional Blocks** creative tab, or use
`/give @s homeostatic_heater:coal_heater`. Right-click it with coal or charcoal.
By default, each piece burns for 1,600 ticks (80 seconds) and the heater holds
16 pieces, including the currently burning one. When full, it keeps your coal
and displays a message. It emits light while active and drops itself when
broken; unused fuel is not recovered.

There is no inventory GUI: fuel goes directly into the block. The front has a
restrained two-frame fire animation while lit; the inventory item uses a 3D
block model. Adding fuel plays the flint-and-steel sound. While active, the
heater occasionally crackles and releases a small smoke particle from its top.

The Shift status is informational only. Burn time, fuel capacity, and active
state are controlled by the server; a modified client cannot grant itself heat
or fuel. The server synchronizes display data about once per second.

## Server configuration

After the first server start, edit `config/homeostatic_heater.json` in the
server directory. An [example](example-config/homeostatic_heater.json) is included.

```json
{
  "burn_ticks_per_coal": 1600,
  "max_coal": 16,
  "always_active": false
}
```

| Setting | Default | Purpose |
| --- | ---: | --- |
| `burn_ticks_per_coal` | `1600` | Heat duration per coal; 20 ticks = 1 second |
| `max_coal` | `16` | Maximum coal pieces including the burning one |
| `always_active` | `false` | Stay lit without consuming fuel |

`burn_ticks_per_coal` accepts 1–1,728,000 and `max_coal` accepts 1–64. Restart
the server after changes. Clients do not need a matching config. Existing stored
coal is preserved when always-active mode is enabled. Invalid config files are
left untouched; defaults are used and a warning is logged.

Heaters from version 1.2.0 and earlier migrate their remaining fuel when
loaded. Lowering `max_coal` does not delete fuel already stored; it only blocks
new coal until the stored amount drops below the new limit.

## Homeostatic heat strength

The built-in datapack entry gives a lit heater `max_radiation: 8325.0`, the
same value Homeostatic uses for a lit Create blaze burner. Homeostatic 1.20.1
scans only 12 blocks horizontally and 3 blocks above a player, so use multiple
heaters across a large villa. Distance and walls still affect warmth.

To change heat strength, edit the optional
[`server-config-datapack/`](server-config-datapack/) and zip its **contents**.
Put the zip into `<world>/datapacks/` and run `/reload`. The release also
includes a ready-made zip to edit. Increasing radiation does not extend
Homeostatic's scan radius.

## Installation

1. Install Fabric Loader for Minecraft 1.20.1 and Fabric API on the server and
   every client.
2. Install a compatible Homeostatic 1.20.1 build to enable warming. The heater
   block itself can load without Homeostatic.
3. Remove older heater jars. Put the release jar in the server's and every
   client's `mods/` directory. Keep only one version.
4. Back up your world, start the server, join with a matching client mod set,
   and follow the [test checklist](TESTING.md).

The Fabric mod ID is `homeostatic_heater`; Java package is
`com.mentality.homeostatic_heater`. Legacy IDs `mentality_heater` and
`stormheater` remain registered so existing blocks and items still load.

## Building and testing

Compile with JDK 17 even if your production server runs a newer Java:

```sh
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
./gradlew build
```

The jar is `build/libs/homeostatic-heater-1.5.0.jar`. GitHub Actions validates
resource JSON, runs unit tests, builds the jar with Java 17, and uploads it as
a build artifact. Rendering, particles, sound, crafting, and Homeostatic
temperature behavior still require the [in-game checklist](TESTING.md).

License: [GNU GPL-3.0-only](LICENSE) for the mod source and original assets in
`artwork/` and `src/main/resources/`. The three vanilla recipe icons in
`docs/recipe/` remain © Mojang Studios.

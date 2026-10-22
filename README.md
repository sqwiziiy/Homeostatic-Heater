# 🔥 Homeostatic Heater

[Russian version](README_RU.md)

[![Build and test](https://github.com/sqwiziiy/Homeostatic-Heater/actions/workflows/build.yml/badge.svg)](https://github.com/sqwiziiy/Homeostatic-Heater/actions/workflows/build.yml)
![Minecraft 1.20.1](https://img.shields.io/badge/Minecraft-1.20.1-3C8527)
![Fabric](https://img.shields.io/badge/Loader-Fabric-DBD0B4)
![Java 17](https://img.shields.io/badge/Java-17-E76F00)
![GPL-3.0-only](https://img.shields.io/badge/License-GPL--3.0--only-3B57A6)

A compact coal- and lava-fueled heater for Minecraft Fabric 1.20.1. It warms nearby players
using the Homeostatic temperature system.

[Download the latest release](https://github.com/sqwiziiy/Homeostatic-Heater/releases/latest) ·
[Installation](#installation) · [Changelog](CHANGELOG.md)

## Features

- Heats players while lit, following Homeostatic's range and obstruction rules.
- Hold Shift and aim at a placed heater to see its remaining minutes and seconds,
  **Inactive**, **Switched off**, or **Always active**.
- Crafting it awards the **Heating Season** advancement with a campfire icon.
- Burns coal, charcoal, or up to **one lava bucket** (returns the empty bucket). Lava burns longer than coal.
- Right-click with an empty main hand to switch the heater off/on, pausing fuel consumption.
- Configurable coal and lava burn durations, coal capacity, and fuel-free always-active mode.
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

Find the heater in **Functional Blocks**, or use
`/give @s homeostatic_heater:coal_heater`.

- Right-click with coal or charcoal to add fuel. By default, one coal burns
  for **80 seconds**. Up to 16 coal can be stored, including burning coal.
- Right-click with a **lava bucket** to add a single long-burning fuel load.
  One lava bucket burns for **16 minutes 40 seconds**, and returns **one empty
  bucket**. A second lava bucket is rejected while the first is burning or
  queued. You can still store coal alongside lava. Queued lava burns before
  queued coal.
- Right-click with an **empty main hand** to turn the heater off or on.
  Turning it off pauses the fuel timer and immediately disables light, smoke,
  and Homeostatic heat; restarting resumes from the stored time.
- Hold Shift and look at a placed heater for the server-synchronized status.
  **Switched off** shows the remaining fuel time.

There is no inventory GUI. The block drops itself if broken, but unused
stored fuel is not refunded. Creative mode does not consume items.

## Server configuration

Edit `config/homeostatic_heater.json` **on the server** and restart to apply changes:

```json
{
  "burn_ticks_per_coal": 1600,
  "burn_ticks_per_lava": 20000,
  "max_coal": 16,
  "always_active": false
}
```

| Setting | Default | Meaning |
| --- | ---: | --- |
| `burn_ticks_per_coal` | `1600` | Coal / charcoal burn ticks |
| `burn_ticks_per_lava` | `20000` | One lava bucket burn ticks |
| `max_coal` | `16` | Maximum stored coal items (independent of lava) |
| `always_active` | `false` | Free heating **only while manually switched on** |

20 ticks = 1 second. Both burn durations accept 1–1,728,000;
`max_coal` accepts 1–64. Settings are read on server startup. Existing
configurations work without changes; the new lava setting uses its default
until configured. Previously placed heaters and their stored fuel are
preserved when upgrading.

## Homeostatic heat strength

The built-in datapack entry gives a lit heater `max_radiation: 8325.0`, the
same value Homeostatic uses for a lit Create blaze burner. Homeostatic 1.20.1
scans only 12 blocks horizontally and 3 blocks above a player, so use multiple
heaters across a large villa. Distance and walls still affect warmth.

To change heat strength, edit the optional
[`server-config-datapack/`](server-config-datapack/) and zip its **contents**.
Put the zip into `<world>/datapacks/` and run `/reload`.
Increasing radiation does not extend Homeostatic's scan radius.

## Installation

1. Install Fabric Loader for Minecraft 1.20.1 and Fabric API on the server and
   every client.
2. Install a compatible Homeostatic 1.20.1 build on **both server and client**.
   Homeostatic is a required dependency.
3. Remove older heater jars. Put the release jar in the server's and every
   client's `mods/` directory. Keep only one version.
4. Back up your world before upgrading. Start the server and join with the
   same mod version on every client.

The Fabric mod ID is `homeostatic_heater`; Java package is
`com.mentality.homeostatic_heater`. Legacy IDs `mentality_heater` and
`stormheater` remain registered so existing blocks and items still load.

## Building from source

Compile with JDK 17 even if your production server runs a newer Java:

```sh
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
./gradlew build
```

The jar is `build/libs/homeostatic-heater-1.6.0.jar`. Automated builds and
unit tests run with GitHub Actions.

License: [GNU GPL-3.0-only](LICENSE) for the mod source and original assets in
`artwork/` and `src/main/resources/`. The three vanilla recipe icons in
`docs/recipe/` remain © Mojang Studios.

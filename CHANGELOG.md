# Changelog

[Русская версия](CHANGELOG_RU.md)

## 1.6.0

- Added one lava bucket as long-burning fuel (returns empty bucket); lava and coal may coexist, and only one lava bucket is allowed at a time.
- Added empty-main-hand on/off toggle with persistent fuel pause and off-state Homeostatic radiation disabled.
- Added configurable `burn_ticks_per_lava` (default 20,000 ticks), retaining old configuration and block NBT compatibility.
- Fixed the vanilla pickaxe mining tag namespace, including legacy blocks.
- Added fuel state tests, updated localization, and Modrinth publishing on GitHub Release (with repository secret and variable).

## 1.5.0

- The server now computes the HUD's remaining time and state; clients receive display-only values.
- An empty heater says **Inactive** instead of `0 min 00 sec`.
- A working heater occasionally emits a small smoke particle.
- Separate English and Russian READMEs and a visual recipe with Minecraft PNG icons.

## 1.4.0

- Shift + aim HUD for remaining minutes and seconds or always-active mode.
- **Heating Season** crafting advancement with a campfire icon.
- Server-to-client timer synchronization, unit tests, and GitHub Actions CI.

## 1.3.0

- Configuration for burn time, capacity, and always-active mode.
- Full-fuel message, occasional crackling, calm fire animation, and 3D item model.

## 1.2.0

- Homeostatic Heater name, simpler textures, and ignition sound when adding coal.

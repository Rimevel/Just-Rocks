# Just Rocks

---

A library for Minecraft 1.21.1 NeoForge. It provides datapack functionality to place procedural rocks into the landscape.

This library is primarely a layer in between Minecrafts terrain generator and the excellent [Rock Lib](https://www.curseforge.com/minecraft/mc-mods/rock-lib) made by [Melvingatto](https://github.com/Melvingatto).

## How to use

Create a new `configured feature` JSON in a datapack or other data location under `data/<your_mod_or_pack_id>/worldgen/configured_feature/`

```json
{
  "type": "just_rocks:rock",
  "config": {
    "rock_type": "fractured_boulder",
    "material": {
      ...
    },
    "width": 16,
    "length": 16,
    "height": 220,
    "orientation": 0.0,
    "embed_depth": 0,
    "decoration_rules": {
      ...
    }
  }
}
```

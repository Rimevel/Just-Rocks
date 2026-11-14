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

### Properties

- **"type"**: `just_rocks:rock` is the only feature type in this mod and used for all rocks.
- **"config"**: The configuration for your rocks. All fields are optional except `rock_type`.
  - **"rock_type"**: The base shape of the rock, see the list of types below.
  - **"material"**: The material of the rock. See below. Defaults to stone.
  - **"width"**: Max width of the generated rock. Must be >= 4. Values larger than 32 can cause clipping.
  - **"length"**: Max length of the generated rock. Must be >= 4.
  - **"height"**: Max height of the generated rock. Must be >= 4. Values larger than 32 can cause clipping.
  - **"orientation"**: Set a fixed angle to rock will be rotated at. Defaults to any rotation.
  - **"embed_depth"**: How far it sinks into the ground. Must be => 0.
  - **"maximum_surface_relief"**: How much the rock will follow the terrain.
  - **"decorations**: Decorations generated with the rock. See below.

## Rock Types

There are 12 different geometric rock shapes you can use when configuring your rocks.

```
SANDSTONE_FIN      
STEPPED_BUTTE      
FRACTURED_BOULDER  Often have a crack in it
TAPERED_SPIRE      Pointy rock with a flat but narrow top
TRIANGULAR_WEDGE
TABLE_ROCK         Narrow foot with wide top
TILTED_MONOLITH    Angular cylinder shape with flat top
LAYERED_SLAB       
SPLIT_CROWN        
BLADE_RIDGE        
TALUS_CONE         
PYRAMIDAL_PEAK     Biased toward a a pyramid shape
```

## Material

The blocks the rock is made out of are defined as a material. There are two variants:

### Uniform

Uniform is a material with just one block for the whole rock.

```json
{
  "type": "uniform",
  "block": {
    "Name": "minecraft:calcite"
  }
}
```

- **"type"**: `Uniform`
- **"block"**: A block state definition for the block that will be used for the rock.

### Palette

Define segments of the rock, from bottom to the top. Each segment can contain one or more blocks as material.

```json
{
  "type": "palette",
  "seed_offset": 1234,
  "patch_scale": 7.0,
  "patch_influence": 0.65,
  "bands": [
    {
      "from": 0.0,
      "to": 0.75,
      "components": [
        {
          "block": {
            "Name": "minecraft:granite"
          },
          "weight": 8.0
        },
        {
          "block": {
            "Name": "minecraft:stone"
          },
          "weight": 2.0
        }
      ]
    },
    {
      "from": 0.75,
      "to": 1.0,
      "components": [
        {
          "block": {
            "Name": "minecraft:andesite"
          },
          "weight": 1.0
        }
      ]
    }
  ]
}
```

- **"type"**: `palette`
- **"seed_offset"**: Added to the world seed to make sure rocks with similar configs are more unique.
- **"patch_scale"**: The spatial size in blocks for coherent material patches (>= 1).
- **"patch_influence"**: A value between 0.0 and 1.0, how finely the mix is between blocks in the same band.
- **"bands"**: An array of one or more bands. MUST contain at least one band.
  - **"from"**: Distance from the bottom of the block where the band starts (0.0 - 1.0).
  - **"to"**: Distance from the bottom of the block where the band ends. MUST be higher than `from`.
  - **"components"**: Array of components the band is made from. MUST contain at least one component.
    - **"block"**: Blockstate for the block that can be used.
    - **"weight"**: The relative chance that this component is picked for this band. Checked each block during generation.
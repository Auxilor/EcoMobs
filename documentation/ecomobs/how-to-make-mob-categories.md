---
title: "How to Make Mob Categories"
sidebar_position: 2
---

A category is a config file that controls how a group of mobs spawns. Every mob belongs to one **category**, which decides whether mobs of that category appear naturally, how (by **replacing** vanilla mobs or through EcoMobs' own **custom spawning**), and whether they **persist**. This page covers building a category, naming it, and every part of its config.

## Quick start

1. Open `plugins/EcoMobs/categories/` and copy `_example.yml`, renaming it to your category's ID, e.g. `common.yml`. The file name (without `.yml`) is the category ID.
2. Pick a spawning `type`: `replace`, `custom`, or `none`.
3. Fill in the matching block (the `replace` mobs and chance, or the `custom` spawn types, conditions, and chance).
4. Set `persistent` to control whether these mobs despawn naturally.
5. Set `category: <id>` on a mob, run `/ecomobs reload`, and confirm the mob spawns the way the category defines.

:::tip
`_example.yml` is included as a reference and is **never loaded**, so copy or rename it to make a real category. You can also organise categories into subfolders inside `categories/`, and they'll still load.
:::

## Naming and IDs

The category's file name without `.yml` is its ID. This is the value you put under `category:` when [creating a mob](how-to-make-a-custom-mob).

:::warning ID rules
IDs may only contain lowercase letters, numbers, and underscores (a-z, 0-9, _). No spaces, capitals, or hyphens, or the category will not load.
:::

## The structure of a category

A category config has two parts.

| Part | What it controls |
| --- | --- |
| **Spawning** | How mobs of this category enter the world |
| **Despawning** | Whether mobs of this category persist |

Here is a complete category:

```yaml
# === Spawning: how mobs of this category appear ===
spawning:
  type: custom # replace, custom, or none
  replace: # Used when type is replace
    replace: # Vanilla mobs to replace
      - zombie
      - skeleton
    chance: 100 # Percent chance to replace the vanilla mob when it spawns
  custom: # Used when type is custom
    spawn-types: # Choose from land, water
      - land
    conditions: [ ] # Conditions the location must meet to spawn
    chance: 1.5 # Percent chance to spawn when a valid point is found

# === Despawning: whether mobs persist ===
persistent: false # If true, mobs of this category never despawn naturally
```

### Spawning

The `type` field picks one of three spawning methods, and you fill in the matching block.

`replace` uses the vanilla spawning system, swapping the listed vanilla mobs for your custom mob when they spawn:

```yaml
spawning:
  type: replace
  replace:
    replace: # Vanilla mobs to replace
      - zombie
      - skeleton
    chance: 100 # Percent chance to replace the vanilla mob when it spawns
```

`custom` uses EcoMobs' own spawning system, spawning the mob wherever a valid point matches your conditions:

```yaml
spawning:
  type: custom
  custom:
    spawn-types: # Choose from land, water
      - land
    conditions: [ ] # Conditions the location must meet to spawn
    chance: 1.5 # Percent chance to spawn when a valid point is found
```

`none` disables natural spawning entirely, for mobs you only summon via commands, eggs, or totems:

```yaml
spawning:
  type: none
```

:::danger Conditions are their own system
The `conditions` list uses the shared eco conditions system, configured the same way everywhere.

- [Configuring a Condition](https://plugins.auxilor.io/effects/configuring-a-condition)
:::

### Despawning

Set whether mobs of this category stay loaded or despawn like vanilla mobs.

```yaml
persistent: false # If true, mobs of this category never despawn naturally
```

:::tip Troubleshooting
- **Category won't load?** Check the ID rules above; the file name must be lowercase letters, numbers, and underscores only.
- **Mobs never spawn naturally?** Confirm the `type` is `replace` or `custom`, not `none`, and that the chance is above zero.
- **Custom spawns never appear?** Check the `conditions` are actually met at the spawn location, and that `spawn-types` matches the terrain.
:::

<hr/>

## Where to go next

- **Make a mob:** [How to Make a Custom Mob](how-to-make-a-custom-mob) and point its `category` at this file.
- **Conditions:** [Configuring a Condition](https://plugins.auxilor.io/effects/configuring-a-condition) for custom spawning rules.
- **Default configs:** browse the shipped examples [here](https://github.com/Auxilor/EcoMobs/tree/master/eco-core/core-plugin/src/main/resources/categories), and community configs on [lrcdb](https://lrcdb.auxilor.io/).
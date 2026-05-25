---
title: How to make Mob Categories
sidebar_position: 2
---

## What are categories?

In EcoMobs, each mob belongs to a category, which control things like spawning behavior.

For example, you might have a category for common mobs, one for rare mobs, one for nether bosses, etc.

## How to add categories
Each category is its own config file, placed in the `/categories/` folder, and you can add or remove them as you please. There's an example config called `_example.yml` to help you out!

The ID of the category is the file name. This is what you use when [creating a mob](https://plugins.auxilor.io/ecomobs/how-to-make-a-custom-mob).
ID's must be lowercase letters, numbers, and underscores only.

## Example Category Config

```yaml
persistent: false

spawning:
  type: custom

  replace:
    replace:
      - zombie
      - skeleton

  custom:
    spawn-types:
      - land
    conditions: [ ]
    chance: 1.5
```

## Understanding all the sections

### The Despawning Section

```yaml
# If the mob is persistent, then it will not despawn naturally.
persistent: false (true/false)
```

### The Spawning Section

Three spawning methods are available: `replace`, `custom`, or `none`.

#### replace
Uses the vanilla spawning system by replacing the listed vanilla mobs with your custom mob when they spawn. 

```yaml
spawning:
  type: replace
  replace:
    replace: # The list of mobs to replace
      - zombie
      - skeleton
    chance: 100 # The chance for the mob to replace the vanilla mob when it spawns
```

#### custom
Uses EcoMobs' custom spawning system. 

Example:

```yaml
spawning:
  type: custom
  custom:
    spawn-types: # Choose from land or water
      - land
    conditions: [ ] # The conditions that need to be met for the mob to spawn, read more about conditions here: https://plugins.auxilor.io/effects/configuring-a-condition
    chance: 1.5 # The chance for the mob to spawn when the conditions are met
```

#### none
Disables natural spawning for this mob category.

Example:

```yaml
spawning:
  type: none
```

<hr/>

## Default configs
The default configs can be found [here](https://github.com/Auxilor/EcoMobs/tree/master/eco-core/core-plugin/src/main/resources/mobs). <br/>
You can find additional user-created configs on [lrcdb](https://lrcdb.auxilor.io/).
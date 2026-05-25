---
title: How to make a Mob
sidebar_position: 1
---

## How to add mobs
Each mob is its own config file, placed in the `/mobs/` folder, and you can add or remove them as you please. There's an example config called `_example.yml` to help you out!

The ID of the mob is the file name. This is what you use in commands, effects, the [Entity Lookup System](https://plugins.auxilor.io/the-entity-lookup-system) and in the [Item Lookup System](https://plugins.auxilor.io/the-item-lookup-system).
ID's must be lowercase letters, numbers, and underscores only.

## Example Mob Config

```yaml
mob: zombie attack-damage:90 movement-speed:1.5 follow-range:16 health:1200
category: common
display-name: "&cNecrotic Soldier &7| &c%health%♥ &7| &e%time%"
lifespan: 120

equipment:
  hand: diamond_sword sharpness:2
  off-hand: shield
  head: ""
  chest: ""
  legs: ""
  feet: ""

integrations:
  levelled-mobs:
    can-level: true
  model-engine:
    id: ""
  better-model:
    id: ""
  libs-disguises:
    id: ""

custom-ai:
  clear: false
  target-goals: [ ]
  entity-goals: [ ]

effects:
  permanent-effects: [ ]
  spawn: [ ]
  despawn: [ ]
  interact: [ ]
  melee-attack: [ ]
  ranged-attack: [ ]
  any-attack: [ ]
  take-damage: [ ]
  damage-player: [ ]
  kill-player: [ ]
  death: [ ]
  kill: [ ]

defence:
  can-mount: true
  damage-modifiers:
    hot_floor: 1
    fire_tick: 1
    lava: 1
    suffocation: 1
    drowning: 1
    entity_explosion: 1
    block_explosion: 1

drops:
  experience: 30
  items:
    - chance: 100
      items:
        - diamond_sword unbreaking:1 name:"Example Sword"

boss-bar:
  enabled: true
  color: white
  style: progress
  radius: 120

spawn:
  totem:
    enabled: false
    top: netherite_block
    middle: iron_block
    bottom: magma_block
    conditions: [ ]
  egg:
    enabled: true
    conditions: [ ]
    item: evoker_spawn_egg unbreaking:1 hide_enchants
    name: "&cNecrotic Soldier&f Spawn Egg"
    lore:
      - ""
      - "&8&oPlace on the ground to"
      - "&8&osummon a &cNecrotic Soldier"

    craftable: true

    recipe:
      - iron_block
      - netherite_block
      - iron_block
      
      - air
      - ecoitems:boss_core ? nether_star
      - air
      
      - iron_block
      - netherite_block
      - iron_block
```

## Understanding all the sections

### The Mob Info Section

:::dangerCategories:
You **must** configure a category for the mob. This controls the natural spawning of the mob, even if you don't want any natural spawning.
:::

```yaml
# A base mob and modifiers
# View an explanation for this system here: https://plugins.auxilor.io/the-entity-lookup-system
mob: zombie attack-damage:90 movement-speed:1.5 follow-range:16 health:1200

# The ID of the mob category, handles spawning.
category: common

# Supported placeholders:
# %health%, %max_health%, %health_percent%, %time% (formats as minutes:seconds, eg 1:56)
display-name: "&cNecrotic Soldier &7| &c%health%♥ &7| &e%time%"

# The lifespan of the mob, in seconds. Set to -1 to disable.
lifespan: 120
```

### The Equipment Section

```yaml
# If the mob you're using supports equipment, you can specify the items in each slot.
# Remove any slots that you don't want to put equipment in.
equipment:
  hand: diamond_sword sharpness:2
  off-hand: shield
  head: ""
  chest: ""
  legs: ""
  feet: ""
```

### The Integrations Section

```yaml
# Options for plugin integrations
# Remove sections for plugins you're not using
integrations:
  # Options for LevelledMobs
  levelled-mobs:
    can-level: true

  # Options for ModelEngine
  model-engine:
    id: ""

  # Options for BetterModel
  better-model:
    id: ""    

  # Options for LibsDisguises
  libs-disguises:
    id: ""
```

### The Custom AI Section

```yaml
# Custom Mob AI
# Read here: https://plugins.auxilor.io/all-plugins/custom-entity-ai
custom-ai:
  # If custom AI should override the vanilla entity AI.
  clear: false

  # How the mob decides who to attack.
  target-goals: [ ]

  # How the mob should behave.
  entity-goals: [ ]
```

### The Effects & Conditions Section
:::dangerEffects Section

The effects section is the core functionality of the mob. You can configure effects, conditions, filters, mutators to activate on specific mob actions.

Check out [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect) to understand how to configure this section correctly.

For more advanced users or setups, you can configure chains in this section to string together different effects under one trigger. Check out [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain) for more info.

:::
```yaml
# Some effects are ran from the perspective of the entity, and others from the perspective
# of the player - each section is marked with which perspective it is run from.

# You can use display name placeholders in effects
# You can also use top damager placeholders:
# %top_damager_<place>_name%, %top_damager_<place>_damage%, %top_damager_<place>_display%
effects:
  # Effects that are active all the time
  # Ran from the perspective of the entity
  permanent-effects: [ ]

  # Effects ran when the mob spawns
  # Ran from the perspective of the entity
  spawn: [ ]

  # Effects ran when the mob despawns
  # Ran from the perspective of the entity
  despawn: [ ]

  # Effects ran when the player interacts with the mob
  # Ran from the perspective of the player
  interact: [ ]

  # Effects ran when the player melee attacks the mob
  # Ran from the perspective of the player
  melee-attack: [ ]

  # Effects ran when the player does a ranged attack on the mob
  # Ran from the perspective of the player
  ranged-attack: [ ]

  # Effects ran when the player attacks the mob
  # Ran from the perspective of the player
  any-attack: [ ]

  # Effects ran when the mob takes damage
  # Ran from the perspective of the entity
  take-damage: [ ]

  # Effects ran when the player is damaged by the mob
  # Ran from the perspective of the player
  damage-player: [ ]

  # Effects ran when the player is killed by the mob
  # Ran from the perspective of the player
  kill-player: [ ]

  # Effects ran when the mob dies
  # Ran from the perspective of the entity
  death: [ ]

  # Effects ran when the mob is killed by the player
  # Ran from the perspective of the player
  kill: [ ]
```

### The Defence Section

```yaml
defence:
  # If the mob can get into boats, minecarts, etc.
  can-mount: true

  # A list of damage causes that the mob should multiply incoming damage by.
  # The list of damage causes can be found here:
  # https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html
  damage-modifiers:
    hot_floor: 1
    fire_tick: 1
    lava: 1
    suffocation: 1
    drowning: 1
    entity_explosion: 1
    block_explosion: 1
```

### The Drops Section

```yaml
# Options for what the mob drops
drops:
  # The amount of experience to drop
  experience: 30

  # You can specify as many drops as you want, and group several drops together under one chance
  items:
    - chance: 100
      items:
        - diamond_sword unbreaking:1 name:"Example Sword"
```

### The Boss-Bar Section

```yaml
# Options for the boss bar
boss-bar:
  # If the mob should have a boss bar
  enabled: true

  # Options: blue, green, pink, purple, red, white, yellow
  color: white

  # Options: progress, notched_20, notched_12, notched_10, notched_6
  style: progress

  # The distance from the mob where the boss bar is visible
  radius: 120
```

### The Spawn Section

```yaml
# Options for spawning the mob
spawn:
  # A spawn totem is a set of 3 blocks on top of each other to spawn a mob (like a snow golem)
  totem:
    # If spawn totems should be enabled
    enabled: false
    # The top block
    top: netherite_block
    # The middle block
    middle: iron_block
    # The bottom block
    bottom: magma_block
    # The conditions for the totem to work
    conditions: [ ]

  # Options for a spawn egg
  egg:
    # If the mob should have a spawn egg
    enabled: true
    # The conditions for the spawn egg to work
    # not-met-lines will show up on the spawn egg
    conditions: [ ]
    # The spawn egg item
    item: evoker_spawn_egg unbreaking:1 hide_enchants
    name: "&cNecrotic Soldier&f Spawn Egg"
    lore:
      - ""
      - "&8&oPlace on the ground to"
      - "&8&osummon a &cNecrotic Soldier"
    craftable: true
    crafting-permission: "ecoitems.craft.boss_core" # (Optional) The permission required to craft this recipe.
    recipe:
      - iron_block
      - netherite_block
      - iron_block
      - air
      - ecoitems:boss_core ? nether_star
      - air
      - iron_block
      - netherite_block
      - iron_block
```
:::tip

We support shaped and shapeless recipes. Check out [Recipes](https://plugins.auxilor.io/the-item-lookup-system/recipes) for more info on how to configure these.

:::

### Internal Placeholders

| Placeholder                     | Value                                                     |
| ------------------------------- | --------------------------------------------------------- |
| `%health%`                      | The current health of the mob.                            |
| `%max_health%`                  | The max health of the mob.                                |
| `%health_percent%`              | The percentage of health the mob has.                     |
| `%time%`                        | The time left before the mob despawns (`minutes:seconds`) |
| `%top_damager_<place>_name%`    | The name of the [0-9] top damager                         |
| `%top_damager_<place>_damage%`  | The damage dealt by the [0-9] top damager                 |
| `%top_damager_<place>_display%` | The ranking of the [0-9] top damager                      |

<hr/>

## Default configs
The default configs can be found [here](https://github.com/Auxilor/EcoMobs/tree/master/eco-core/core-plugin/src/main/resources/mobs). <br/>
You can find additional user-created configs on [lrcdb](https://lrcdb.auxilor.io/).

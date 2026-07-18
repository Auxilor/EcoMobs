---
title: "How to Make a Custom Mob"
sidebar_position: 1
---

A custom mob is a vanilla **base entity** wrapped with your own **stats, equipment, effects, drops, and spawning**, all defined in one config file. EcoMobs reads each file in the `mobs/` folder and registers a mob you can spawn, drop loot from, and hook effects onto. This page covers building one from scratch, naming it, every part of its config, and the placeholders you can use inside it.

## Quick start

1. Open `plugins/EcoMobs/mobs/` and copy `_example.yml`, renaming it to your mob's ID, e.g. `necrotic_soldier.yml`. The file name (without `.yml`) is the mob ID.
2. Set the base `mob` and stat modifiers, and pick a `category` (this controls natural spawning).
3. Set the `display-name`, then fill in the parts you want: `equipment`, `effects`, `drops`, `boss-bar`, and `spawn`.
4. Run `/ecomobs reload` to load the mob.
5. Run `/ecomobs spawn necrotic_soldier` and confirm the mob appears in the world with the name and stats you set.

:::tip
`_example.yml` is included as a reference and is **never loaded**, so copy or rename it to make a real mob. You can also organise mobs into subfolders inside `mobs/`, and they'll still load.
:::

## Naming and IDs

The mob's file name without `.yml` is its ID. This is what you pass to commands, effects, the [Entity Lookup System](https://plugins.auxilor.io/the-entity-lookup-system), and the [Item Lookup System](https://plugins.auxilor.io/the-item-lookup-system).

:::warning ID rules
IDs may only contain lowercase letters, numbers, and underscores (a-z, 0-9, _). No spaces, capitals, or hyphens, or the mob will not load.
:::

## The structure of a mob

A mob config is a set of named parts, each controlling one aspect of the mob.

| Part | What it controls |
| --- | --- |
| **Mob info** | The base entity, stat modifiers, category, display name, and lifespan |
| **Equipment** | The items the mob wears in each slot |
| **Integrations** | Hooks into other plugins (LevelledMobs, ModelEngine, etc.) |
| **Custom AI** | The mob's targeting and behaviour goals |
| **Effects** | Effects and conditions that fire on mob actions |
| **Defence** | Mounting and per-cause damage modifiers |
| **Drops** | Experience and item drops on death |
| **Boss bar** | The on-screen health bar |
| **Spawn** | Spawn totems and craftable spawn eggs |

Here is a complete mob with every part in place:

```yaml
# === Mob info: the base entity and identity ===
mob: zombie attack-damage:90 movement-speed:1.5 follow-range:16 health:1200 # Base entity plus stat modifiers; see the Entity Lookup System
category: common # The category ID; controls natural spawning (required)
display-name: "&cNecrotic Soldier &7| &c%health%♥ &7| &e%time%" # Supports the internal placeholders below
lifespan: 120 # Seconds before the mob despawns; set to -1 to disable

# === Equipment: what the mob wears ===
equipment:
  hand: diamond_sword sharpness:2 # Remove any slots you don't want to fill
  off-hand: shield
  head: ""
  chest: ""
  legs: ""
  feet: ""

# === Integrations: hooks into other plugins ===
integrations:
  levelled-mobs:
    can-level: true # Allow LevelledMobs to level this mob
  model-engine:
    id: "" # ModelEngine model ID; leave blank for none
  better-model:
    id: "" # BetterModel model ID; leave blank for none
  libs-disguises:
    id: "" # LibsDisguises disguise ID; leave blank for none

# === Custom AI: how the mob behaves ===
custom-ai:
  clear: false # If true, custom AI replaces the vanilla entity AI
  target-goals: [ ] # How the mob decides who to attack
  entity-goals: [ ] # How the mob moves and behaves

# === Effects: actions that fire on mob triggers ===
effects:
  permanent-effects: [ ] # Always active; run as the entity
  spawn: [ ] # On spawn; run as the entity
  despawn: [ ] # On despawn; run as the entity
  interact: [ ] # On player interact; run as the player
  melee-attack: [ ] # On player melee attack; run as the player
  ranged-attack: [ ] # On player ranged attack; run as the player
  any-attack: [ ] # On any player attack; run as the player
  take-damage: [ ] # When the mob takes damage; run as the entity
  damage-player: [ ] # When the mob damages a player; run as the player
  kill-player: [ ] # When the mob kills a player; run as the player
  death: [ ] # When the mob dies; run as the entity
  kill: [ ] # When a player kills the mob; run as the player

# === Defence: incoming damage handling ===
defence:
  can-mount: true # If the mob can enter boats, minecarts, etc.
  damage-modifiers: # Multiply incoming damage by cause
    hot_floor: 1
    fire_tick: 1
    lava: 1
    suffocation: 1
    drowning: 1
    entity_explosion: 1
    block_explosion: 1

# === Drops: rewards on death ===
drops:
  experience: 30 # Experience dropped on death
  items:
    - chance: 100 # Percent chance for this group to drop
      items:
        - diamond_sword unbreaking:1 name:"Example Sword"

# === Boss bar: on-screen health bar ===
boss-bar:
  enabled: true # If the mob shows a boss bar
  color: white # blue, green, pink, purple, red, white, yellow
  style: progress # progress, notched_20, notched_12, notched_10, notched_6
  radius: 120 # Distance from the mob where the bar is visible

# === Spawn: totems and eggs ===
spawn:
  totem:
    enabled: false # If a 3-block totem can summon the mob
    top: netherite_block
    middle: iron_block
    bottom: magma_block
    conditions: [ ] # Conditions for the totem to work
  egg:
    enabled: true # If the mob has a spawn egg
    conditions: [ ] # Conditions for the egg to work; not-met-lines show on the egg
    item: evoker_spawn_egg unbreaking:1 hide_enchants # The spawn egg item
    name: "&cNecrotic Soldier&f Spawn Egg"
    lore:
      - ""
      - "&8&oPlace on the ground to"
      - "&8&osummon a &cNecrotic Soldier"
    craftable: true # If the spawn egg can be crafted
    recipe-permission: "ecomobs.craft.necrotic_soldier" # Optional; permission required to craft the egg
    shapeless: false # Optional; whether the recipe is shapeless, defaults to false
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

### Mob info

The top-level fields define what the mob is and how it identifies itself.

```yaml
mob: zombie attack-damage:90 movement-speed:1.5 follow-range:16 health:1200 # Base entity plus stat modifiers
category: common # The category ID; controls natural spawning
display-name: "&cNecrotic Soldier &7| &c%health%♥ &7| &e%time%" # Supports the internal placeholders below
lifespan: 120 # Seconds before the mob despawns; set to -1 to disable
```

The `mob` line is a base entity with stat modifiers, read through the [Entity Lookup System](https://plugins.auxilor.io/the-entity-lookup-system).

:::warning
Every mob **must** set a `category`, even if you want no natural spawning. The category drives spawning behaviour; without it the mob will not load. Use a category whose spawning `type` is `none` to opt out of natural spawning.
:::

### Equipment

If the base entity supports equipment, set an item per slot.

```yaml
equipment:
  hand: diamond_sword sharpness:2 # Remove any slot you don't want to fill
  off-hand: shield
  head: ""
  chest: ""
  legs: ""
  feet: ""
```

### Integrations

Hook the mob into other plugins you run. Remove the blocks for plugins you don't use.

```yaml
integrations:
  levelled-mobs:
    can-level: true # Allow LevelledMobs to level this mob
  model-engine:
    id: "" # ModelEngine model ID
  better-model:
    id: "" # BetterModel model ID
  libs-disguises:
    id: "" # LibsDisguises disguise ID
```

### Custom AI

Replace or extend the mob's behaviour with custom goals.

```yaml
custom-ai:
  clear: false # If true, custom AI replaces the vanilla entity AI
  target-goals: [ ] # How the mob decides who to attack
  entity-goals: [ ] # How the mob moves and behaves
```

See the [Custom Entity AI](https://plugins.auxilor.io/all-plugins/custom-entity-ai) docs for the full list of goals.

### Effects

Each key is a trigger; the effects you list under it fire when that trigger happens. Some run from the perspective of the entity, others from the player, marked per line.

```yaml
effects:
  permanent-effects: [ ] # Always active; run as the entity
  spawn: [ ] # On spawn; run as the entity
  interact: [ ] # On player interact; run as the player
  melee-attack: [ ] # On player melee attack; run as the player
  take-damage: [ ] # When the mob takes damage; run as the entity
  death: [ ] # When the mob dies; run as the entity
  kill: [ ] # When a player kills the mob; run as the player
```

Display-name placeholders and the top-damager placeholders (`%top_damager_<place>_name%`, `%top_damager_<place>_damage%`, `%top_damager_<place>_display%`) work inside effects.

:::danger Effects are their own system
Effects and conditions are a shared system across every eco plugin, configured the same way everywhere.

- [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect)
- [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain)
:::

### Defence

Control mounting and scale incoming damage per cause.

```yaml
defence:
  can-mount: true # If the mob can enter boats, minecarts, etc.
  damage-modifiers: # Multiply incoming damage by cause
    fire_tick: 1
    lava: 1
    entity_explosion: 1
```

The full list of damage causes is on the Spigot `EntityDamageEvent.DamageCause` javadoc.

### Drops

Set the experience and item drops awarded on death. Group several items under one `chance` to roll them together.

```yaml
drops:
  experience: 30 # Experience dropped on death
  items:
    - chance: 100 # Percent chance for this group to drop
      items:
        - diamond_sword unbreaking:1 name:"Example Sword"
```

### Boss bar

Give the mob an on-screen health bar visible within a radius.

```yaml
boss-bar:
  enabled: true # If the mob shows a boss bar
  color: white # blue, green, pink, purple, red, white, yellow
  style: progress # progress, notched_20, notched_12, notched_10, notched_6
  radius: 120 # Distance from the mob where the bar is visible
```

### Spawn

Let players summon the mob with a 3-block totem, a spawn egg, or both.

```yaml
spawn:
  totem:
    enabled: false # If a 3-block totem can summon the mob
    top: netherite_block
    middle: iron_block
    bottom: magma_block
    conditions: [ ] # Conditions for the totem to work
  egg:
    enabled: true # If the mob has a spawn egg
    conditions: [ ] # Conditions for the egg; not-met-lines show on the egg
    item: evoker_spawn_egg unbreaking:1 hide_enchants # The spawn egg item
    name: "&cNecrotic Soldier&f Spawn Egg"
    craftable: true # If the spawn egg can be crafted
    recipe-permission: "ecomobs.craft.necrotic_soldier" # Optional; permission to craft the egg
    shapeless: false # Optional; whether the recipe is shapeless, defaults to false
    recipe: # 9 slots for shaped, any order for shapeless
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

## Internal placeholders

These placeholders work in the `display-name` and in effects on this mob.

| Placeholder | Value |
| --- | --- |
| `%health%` | The current health of the mob |
| `%max_health%` | The max health of the mob |
| `%health_percent%` | The percentage of health the mob has |
| `%time%` | The time left before the mob despawns (`minutes:seconds`) |
| `%top_damager_<place>_name%` | The name of the [0-9] top damager |
| `%top_damager_<place>_damage%` | The damage dealt by the [0-9] top damager |
| `%top_damager_<place>_display%` | The ranking of the [0-9] top damager |

:::tip Troubleshooting
- **Mob won't load?** Check the ID rules above; the file name must be lowercase letters, numbers, and underscores only.
- **Mob loads but never spawns naturally?** It needs a `category` whose spawning `type` is `replace` or `custom`; a missing or `none` category means no natural spawning.
- **Spawn egg won't craft?** Confirm `craftable: true` and that the crafting player has the `recipe-permission` if you set one.
- **Equipment ignored?** The base entity must support that slot; not every vanilla mob wears armour or holds items.
:::

<hr/>

## Where to go next

- **Default configs:** browse the shipped examples [here](https://github.com/Auxilor/EcoMobs/tree/master/eco-core/core-plugin/src/main/resources/mobs), and community configs on [lrcdb](https://lrcdb.auxilor.io/).
- **Spawning:** [How to Make Mob Categories](how-to-make-mob-categories) to control where and how mobs appear.
- **Commands:** [Commands and Permissions](commands-and-permissions) for spawning and giving mobs.
- **Effects:** [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect) to bring the effects section to life.
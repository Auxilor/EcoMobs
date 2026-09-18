---
title: "Custom Spawners"
sidebar_position: 3
---

A custom spawner is an ordinary spawner block carrying EcoMobs settings. It can spawn an EcoMob or a plain vanilla entity, and every setting a vanilla spawner has — delay, count, radius, player range — is editable per spawner, alongside EcoMobs extras like pickup rules, explosion immunity, and particle animations.

The settings ride on the item, so a spawner picked up and placed somewhere else keeps everything it was given.

## Getting a spawner

```
/ecomobs spawner give <player> <mob> [amount] [attributes...]
```

`<mob>` is an EcoMob ID or a vanilla entity type, so `/ecomobs spawner give Notch hollow_king` and `/ecomobs spawner give Notch zombie` both work.

Attributes can be set as you hand it out:

```
/ecomobs spawner give Notch voidling 3 delay 100 200 count 6 explosion-proof true
```

To change one later, hold the spawner item — or look at a placed spawner within 5 blocks — and run:

```
/ecomobs spawner modify <attribute> [value...]
```

Each attribute is gated by its own permission, `ecomobs.command.spawner.modify.<attribute>`, on both commands.

### Attributes

| Attribute | Value(s) | Default | What it does |
| --- | --- | --- | --- |
| `mob` | `<mobId>` | — | The EcoMob ID or vanilla entity type the spawner spawns |
| `delay` | `<min> <max>` | `200 800` | The tick range between spawn cycles, rolled fresh each cycle |
| `radius` | `<blocks>` | `4` | How far from the spawner mobs appear |
| `player-radius` | `<blocks>` | `16` | How close a player has to be for the spawner to run at all |
| `count` | `<number>` | `4` | Mobs spawned per cycle |
| `max-nearby` | `<number>` | `6` | Mobs of that kind nearby before the spawner holds off |
| `pickup` | `allow` \| `silk_touch` \| `deny` | `deny` | Whether breaking it gives the spawner back |
| `particle` | `none` \| `<animation>` | `none` | The particle animation drawn above it |
| `explosion-proof` | `true` \| `false` | `false` | Whether it survives creepers and TNT |
| `stack-size` | `<number>` | `1` | How many spawners it stands in for — see [Spawner Stacking](spawner-stacking) |

Particle animations are defined in `config.yml` under `spawner-animations`; see the [Plugin Config](plugin-config) reference.

### As an item

Every spawner is registered as an eco item under `ecomobs:<mob>_spawner`, so it can be used anywhere an item is taken — mob drops, crafting recipes, shops, crate rewards, and other plugins' configs:

```yaml
drops:
  - chance: 5
    items:
      - ecomobs:hollow_king_spawner
```

Vanilla entity types work the same way, so `ecomobs:zombie_spawner` is a plain zombie spawner. The item comes with every attribute at its default; a spawner matches the lookup on its mob alone, so one that has since had its delay or particle changed still counts as that spawner.

## How spawners tick

EcoMobs ticks every spawner itself — its own and vanilla's, dungeon spawners included — and applies the spawn requirements the server used to apply. Each one is a toggle, and every default is what vanilla does, so a spawner left alone behaves like a vanilla spawner.

```yaml
spawners:
  tick-rate: 5 # Ticks between loop runs
  redstone-deactivates: true # Whether a powered spawner stops spawning
  max-mobs-per-chunk: 50 # Entities a chunk can hold before its spawners stop
  checks:
    spawn-space: true
    solid-ground: false
    max-nearby: true
    player-range: true
    light-level: true
    max-light-level: 0
```

What the loop does every cycle:

- The countdown only runs while a player is inside `player-radius`, and is rolled from the `delay` range each cycle.
- `count` mobs are spawned per cycle, scattered within `radius` horizontally and one block either side vertically. A stack of spawners spawns that many mobs per spawner in the stack, on the one cycle.
- The spawner holds off while `max-nearby` or more of that mob are already around — counted in a box of double `radius` across and 4 blocks tall, and only mobs of that exact kind count. Two EcoMobs built on the same base entity do not block each other.

### Spawners with mob stacking on

While `stacking.enabled` is true, a cycle goes into the world as **stack size, not as one entity per mob**.

- If there is a stack of that mob within `stacking.radius`, the cycle is added to it and nothing is spawned.
- If there isn't, one mob is spawned already standing for the whole cycle, up to `stacking.max-size`.
- Anything that doesn't fit is dropped. A full stack beside the spawner holds it up the same way `max-nearby` does.

This is what lets a wall of spawners represent hundreds of thousands of mobs without the server ticking hundreds of thousands of entities. A spawner set to `count: 200`, stacked 64 high, puts **one** entity into the world per cycle instead of 12 800.

Note that `max-nearby` counts stacked mobs as every mob they stand for, so on a stacking server the default of `6` stops a spawner almost immediately — raise it on farm spawners.

Growing a stack this way spawns nothing, so no merge event is fired for it.

### `checks`

| Check | Default | What it does |
| --- | --- | --- |
| `spawn-space` | `true` | A mob needs room where it would spawn, rather than appearing inside blocks. |
| `solid-ground` | `false` | A mob needs solid ground under it. Vanilla spawners do **not** require this — unlike natural spawning, they let mobs appear mid-air and fall. Turn it on to keep spawns on the floor. |
| `max-nearby` | `true` | The `max-nearby` cap applies. A stacked mob counts as every mob it stands in for, so a stack of sixty reads as sixty. |
| `player-range` | `true` | A player has to be within `player-radius` for the spawner to count down. Off makes spawners run whether anyone is there or not. |
| `light-level` | `true` | Mobs that need darkness are held to it, so a torch beside a zombie spawner switches it off, as in vanilla. Blazes and silverfish need light 12 rather than darkness. Animals, villagers and nether mobs ignore light either way. |
| `max-light-level` | `0` | The most block light a darkness-spawning mob tolerates. Vanilla is `0`; raising it lets spawners keep working in dim rooms. |

For a grinder or an arena that should fire regardless of the light, set `light-level: false`. To let mobs spawn inside a 1x1 hole as well, add `spawn-space: false`.

### `max-mobs-per-chunk`

How many things a chunk can hold before the spawners in it stop for that cycle. `0` turns it off.

:::info
**This counts entities, not mobs. It does not add up stack sizes.**

One stacked mob is one entity, even when it stands for 60 mobs. A stack of 60 counts as **1**, exactly like a single mob standing on its own. So `50` is 50 entities in the chunk, which with mob stacking on can be thousands of mobs.

The limit exists because of the work the server has to do, and one stack is one thing for the server to tick, however many mobs it holds.
:::

With mob stacking off, a cycle spawns at most as many mobs as the chunk has room for, and the rest of the cycle is dropped.

### `redstone-deactivates`

`true` stops a powered spawner from spawning, directly or through a block next to it — a lever, a redstone torch, a comparator line. The cycle still counts down while it is off, so cutting the power doesn't hand back a spawn that was held.

Vanilla spawners ignore redstone entirely, so set this to `false` if you want that.

:::warning
EcoMobs now drives dungeon spawners too. The defaults match vanilla, but turning `light-level` or `spawn-space` off changes every spawner on the server, not only the ones you placed.
:::

## Picking spawners back up

The `pickup` attribute decides what a break gives you, and each mode has a permission behind it:

| `pickup` | What happens |
| --- | --- |
| `deny` | The spawner breaks and drops nothing |
| `allow` | Drops the spawner item, if the player has `ecomobs.spawner.pickup` |
| `silk_touch` | Drops the spawner item if the player has `ecomobs.spawner.pickup.silktouch` **and** is holding a Silk Touch tool |

Without the matching permission the block cannot be broken at all, and the player is told why. The message is `spawner-cannot-pickup` in `lang.yml`.

The dropped item carries every setting the block had, so a spawner keeps its delay, particle, and the rest across the move.

In creative, pick-block on a custom spawner gives you a copy of it, stack size included.

## Explosion immunity

`explosion-proof: true` removes the spawner from the blast list of creepers, TNT, ghasts, end crystals, and anything else that explodes. It is left standing rather than dropped.

## Item display

The name and lore of a spawner item are built from `spawner-display` in `config.yml`, and update live as attributes change.

| Placeholder | Value |
| --- | --- |
| `%mob%` | The mob ID |
| `%mob_formatted%` | The mob ID as a title, so `hollow_king` reads `Hollow King` |
| `%delay_min%` / `%delay_max%` | The delay range in ticks |
| `%radius%` | The spawn radius |
| `%player_range%` | The required player range |
| `%count%` | Mobs per cycle |
| `%max_nearby%` | The nearby cap |
| `%pickup%` | The pickup mode |
| `%particle%` | The particle animation, or `none` |
| `%explosion_proof%` | `true` or `false` |
| `%size%` | The stack size |

PlaceholderAPI placeholders work in these lines too.

:::info
EcoMobs picks up spawners placed by hand, loaded in a chunk, or first seen when they tick. A spawner pasted in with WorldEdit or set with `/setblock` is registered the first time it tries to spawn, so it needs no extra step.
:::

<hr/>

## Where to go next

- **Stack spawners together:** [Spawner Stacking](spawner-stacking).
- **Stack the mobs they spawn:** [Mob Stacking](mob-stacking).
- **Commands and permissions:** [Commands and Permissions](commands-and-permissions).
- **Particle animations:** the [Plugin Config](plugin-config) reference.

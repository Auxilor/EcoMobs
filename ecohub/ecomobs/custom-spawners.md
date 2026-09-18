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

## Spawn methods

`spawners.mode` in `config.yml` decides who ticks spawners. The attributes above mean the same thing in both modes — what changes is which vanilla requirements still apply.

```yaml
spawners:
  mode: vanilla # or ecomobs
  tick-rate: 5 # Ticks between loop runs; only used when mode is ecomobs
  all-spawners: false # Whether the ecomobs loop takes over vanilla spawners too
```

### `vanilla` (default)

The server ticks the spawner exactly as it always has. When it decides to spawn, EcoMobs cancels the vanilla mob and puts its own at the same spot.

Everything vanilla checks still applies: light level, block space, the spawner needing a valid surface, and the usual spawn cycle timing. A spawner set to a mob that only spawns in the dark will sit idle in daylight, and one buried in solid blocks will not fire.

Pick this if you want spawners to feel like vanilla spawners.

### `ecomobs`

EcoMobs runs its own loop every `tick-rate` ticks and cancels vanilla's attempt entirely.

Light level and block space are **ignored**. A mob will happily appear at midday, on any surface, inside a 1x1 hole. What the loop still honours:

- A player has to be within `player-radius`, same as vanilla.
- The countdown is rolled from the `delay` range each cycle.
- `count` mobs are spawned per cycle, scattered within `radius` horizontally and one block either side vertically.
- The spawner holds off while `max-nearby` or more of that mob are already around — counted in a box of double `radius` across and 4 blocks tall, and only mobs of that exact kind count. Two EcoMobs built on the same base entity do not block each other.

Pick this for grinders, arenas, or anywhere a spawner needs to fire regardless of the light.

### `all-spawners`

Only read when the mode is `ecomobs`.

- `false` — the loop only drives EcoMobs spawners. Dungeon spawners and anything else vanilla are left completely alone.
- `true` — every spawner in the world is ticked by EcoMobs, including vanilla ones, which lose their light and block-space requirements along with everything else.

:::warning
`all-spawners: true` makes every dungeon spawner on the server fire in daylight. It changes the balance of existing worlds, so turn it on deliberately.
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

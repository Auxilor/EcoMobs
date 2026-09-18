---
title: "Mob Stacking"
sidebar_position: 4
---

Mob stacking merges nearby mobs of the same kind into a single entity that stands in for all of them. Two hundred zombies in a grinder become a handful of entities carrying a count, so the server ticks a fraction of the mobs it used to.

It works on EcoMobs and vanilla mobs alike, and needs nothing per mob — turn it on and it applies everywhere.

## Config

Everything lives under `stacking` in `config.yml`.

```yaml
stacking:
  enabled: true

  radius: 8 # How far to look for a stack to merge into
  max-size: 64 # The most mobs one stack can hold
  sweep-rate: 100 # How often (in ticks) to sweep for mobs that drifted together

  # Whether killing a stacked mob kills the whole stack, dropping loot and XP for every
  # mob in it. When false, each kill takes a single mob off the stack.
  kill-whole-stack: false

  # Whether the death animation is hidden when a stacked mob is killed.
  hide-death-animation: false

  # Whether babies and adults are kept in separate stacks.
  match-age: true

  # The nameplate shown above a stack. %size% and %name% are replaced.
  nameplate: "&f%name% &7x%size%"

  # What never stacks. EcoMob IDs and vanilla entity types are both valid.
  blacklist:
    - villager
    - wandering_trader
    - ender_dragon
    - wither

  exclude:
    tamed: true # Pets and other tamed mobs
    leashed: true # Mobs on a lead
    mounted: true # Mobs riding something else
    ridden: true # Mobs carrying passengers
    named: true # Mobs already named, by a name tag or another plugin
```

Run `/ecomobs reload` to apply changes.

## How mobs merge

A mob looks for a stack twice:

- **When it spawns.** Every spawn method ends up here — natural spawning, spawners, breeding, spawn eggs, commands, other plugins — so nothing needs wiring up per source.
- **On the sweep.** Every `sweep-rate` ticks, mobs within 48 blocks of a player try again. This catches mobs that wandered together long after they spawned.

Two mobs merge when all of this holds:

1. They are the same kind — the same EcoMob ID, or the same vanilla entity type for plain mobs.
2. They are both adults or both babies, unless `match-age: false`.
3. Neither is excluded or blacklisted.
4. The combined size fits inside `max-size`.
5. They are within `radius` blocks of each other.

The mob joins the **nearest** stack that fits. The absorbed entity is removed and the one it joined grows by its size, so two stacks meeting merge into one rather than resetting to a pair.

Stack size is stored on the entity, so a stack survives chunk unloads, restarts, and anything else that takes it out of memory. A stack of one carries no data at all — it is simply a mob again.

## Spawners

With stacking on, spawners don't spawn a mob per mob. A cycle is added to the nearest stack within `radius`, or spawned as a single mob already standing for the whole cycle, capped at `max-size`. See [Custom Spawners](./custom-spawners.md#spawners-with-mob-stacking-on).

`max-nearby` on a spawner counts a stack as every mob it holds, so the vanilla default of `6` stops a stacking spawner almost at once. Raise it on any spawner meant to feed a farm.

## Nameplates

Stacks are labelled with `nameplate`, drawn above the mob:

```yaml
nameplate: "&f%name% &7x%size%"
```

`%size%` is the stack size and `%name%` is the mob's name — an EcoMob's `display-name` with its own placeholders already filled in, or the vanilla name otherwise. PlaceholderAPI placeholders work here, and are filled per viewer.

Nameplates are drawn clientside, so they do not touch the mob's real name and nothing else on the server sees them. An unstacked mob shows nothing.

## What happens on death

This is the setting worth thinking about.

### `kill-whole-stack: false` (default)

One mob comes off the stack. The dead mob drops its own loot and XP as normal, and the remainder comes back immediately as a fresh mob at full health, carrying the new size.

Every mob has to be killed one at a time. Best for survival servers, where a stack should be no more rewarding than the mobs in it.

### `kill-whole-stack: true`

The whole stack dies at once, and pays out for every mob in it. A stack of 32 killed with one hit drops 32 mobs' worth.

For EcoMobs, the drop table is rolled again per mob, so chance-based drops stay chance-based — a stack of 32 does not guarantee a 10% drop, it gets 32 rolls at it. For vanilla mobs the drops are copied and the XP is multiplied.

Best for grinders and arenas, where clearing a stack in one hit is the point.

### `hide-death-animation: false` (default)

Whether the dead mob's body plays out its death animation. This applies to both settings above — the whole stack dying at once, and a single mob coming off the stack.

Left at `false`, the corpse tips over and fades out the way any mob does, taking about a second. On a stack killed one mob at a time that second is spent lying next to the replacement mob, so a fast grinder ends up with a pile of bodies on top of the live stack.

Set it to `true` and the corpse is taken away right after the kill, so the mob simply vanishes. Drops, XP, death messages, and everything else that hangs off the kill are unaffected — only the body goes early.

:::warning
`kill-whole-stack: true` multiplies loot, and a large `max-size` multiplies it a lot. Check what a full stack of your most valuable mob pays out before turning it on.
:::

## What never stacks

Only mobs stack in the first place. Players, armour stands, items, projectiles, and every other non-mob entity are ruled out by type.

On top of that:

- **`blacklist`** — EcoMob IDs and vanilla entity types both work, so `hollow_king` and `zombie` are equally valid entries. The default list keeps villagers, wandering traders, and the two vanilla bosses out.
- **`exclude.tamed`** — pets and other tamed mobs, so a player's wolves stay separate animals.
- **`exclude.leashed`** — anything on a lead.
- **`exclude.mounted`** — mobs riding something else, so a skeleton on a horse stays whole.
- **`exclude.ridden`** — mobs carrying a passenger.
- **`exclude.named`** — mobs given a name by a name tag or another plugin. EcoMobs' own names are drawn clientside and do not count, so a named EcoMob still stacks.

Set any of the `exclude` entries to `false` to let that group stack anyway.

:::info
Bosses are worth blacklisting by ID. A boss with damage stages works fine stacked, but the stack shares one health bar and one set of stages, which is rarely the fight you designed.
:::

<hr/>

## Where to go next

- **Stack the spawners too:** [Spawner Stacking](spawner-stacking).
- **Spawner settings:** [Custom Spawners](custom-spawners).
- **Make a mob:** [How to Make a Custom Mob](how-to-make-a-custom-mob).

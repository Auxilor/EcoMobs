---
title: "API and Triggers"
sidebar_position: 9
---

EcoMobs exposes what it does as **libreforge triggers**, for effects in any eco plugin, and as **Bukkit events**, for other plugins to listen to.

## Triggers

Use these like any other libreforge trigger, for example in an EcoMob's `effects`, or in an EcoSkills or EcoEnchants effect.

| Trigger | When | Parameters |
| --- | --- | --- |
| `ecomobs_place_spawner` | A player places a spawner | player, block, location, text = mob, value = stack size |
| `ecomobs_break_spawner` | A player breaks a whole spawner | player, block, location, text = mob, value = stack size |
| `ecomobs_stack_spawner` | Spawners are added to a stack | player, block, location, text = mob, value = spawners added, alt_value = size before |
| `ecomobs_unstack_spawner` | Spawners are taken off a stack | player, block, location, text = mob, value = spawners taken, alt_value = size before |
| `ecomobs_spawner_spawn` | A spawner spawns a mob | block, location, text = mob |
| `ecomobs_use_spawn_egg` | A spawn egg spawns its mob | player, location, text = mob |
| `ecomobs_build_totem` | A player completes a spawn totem | player, location, text = mob |
| `ecomobs_damage_mob` | A player damages an EcoMob | player, victim, location, text = mob, value = damage |
| `ecomobs_stage_change` | An EcoMob moves to its next damage stage | player, victim, location, text = mob, value = new stage, counting from 1 |
| `ecomobs_mob_drops` | An EcoMob's drops are given out | player, location, text = mob, value = experience, alt_value = items dropped |
| `ecomobs_stack_merge` | A mob joins a stack | victim, location, text = mob, value = size after merging |
| `ecomobs_stack_death` | A stacked mob dies | player, victim, location, text = mob (empty for vanilla mobs), value = stack size |

`text` is the mob ID, or the vanilla entity type for a vanilla spawner or stack. Use it with a text filter to react to one mob only.

## Events

All events are in `com.willfp.ecomobs.event`. Most can be cancelled, and many let you change the outcome before it happens.

### Mobs

| Event | When | Cancellable | Can change |
| --- | --- | --- | --- |
| `EcoMobPreSpawnEvent` | Before an EcoMob spawns | Yes | |
| `EcoMobSpawnEvent` | After an EcoMob spawns | | |
| `EcoMobDamageEvent` | An EcoMob takes damage, after its damage modifiers | Yes | `damage` |
| `EcoMobStageChangeEvent` | An EcoMob finishes a damage stage | | |
| `EcoMobDropsEvent` | An EcoMob's drop table is rolled, before anything is given out; once per mob in a stack | Yes | `drops`, `experience` |
| `EcoMobKillPlayerEvent` | An EcoMob kills a player | | |
| `EcoMobLifespanExpireEvent` | An EcoMob's lifespan runs out | Yes, keeps it alive for now | |
| `EcoMobEggUseEvent` | A spawn egg is about to spawn its mob; `source` is `PLAYER` or `DISPENSER` | Yes | |
| `EcoMobTotemBuildEvent` | A spawn totem is completed, before its mob spawns | Yes | |

### Spawners

Every spawner event carries the spawner's `location` and `mobId`.

| Event | When | Cancellable | Can change |
| --- | --- | --- | --- |
| `EcoMobSpawnerPlaceEvent` | A player places a spawner | Yes, cancels the placement | |
| `EcoMobSpawnerBreakEvent` | A player breaks a whole spawner | Yes, cancels the break | `dropsItem` |
| `EcoMobSpawnerStackEvent` | Spawners are about to join a stack | Yes | `amount` |
| `EcoMobSpawnerUnstackEvent` | A player takes spawners off a stack | Yes | `amount`, `dropsItem` |
| `EcoMobSpawnerExplodeEvent` | A spawner is caught in an explosion | | `isProtected` |
| `EcoMobSpawnerPickBlockEvent` | A creative player picks a spawner | Yes | |
| `EcoMobSpawnerTickEvent` | A spawner is about to run a spawn cycle, for vanilla spawners too | Yes | `spawnCount`, `stackSize` |
| `EcoMobSpawnerSpawnEvent` | A spawner is about to spawn a mob | Yes | |

### Stacks

Vanilla mobs stack too, so stack events carry the Bukkit `entity` rather than an EcoMob.

| Event | When | Cancellable | Can change |
| --- | --- | --- | --- |
| `EcoMobStackMergeEvent` | A mob is about to join a nearby stack | Yes, leaves both alone | |
| `EcoMobStackDeathEvent` | A stacked mob dies | | `killWholeStack` |
| `EcoMobStackSplitEvent` | The rest of a stack is about to come back after one died | Yes, drops the rest | `remaining` |

<hr/>

## Where to go next

- **Making mobs:** [How to Make a Custom Mob](how-to-make-a-custom-mob).
- **Spawners:** [Custom Spawners](custom-spawners).

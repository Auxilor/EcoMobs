---
title: "Commands and Permissions"
sidebar_position: 3
---

| Command                                    | Description                                          | Permission               |
|--------------------------------------------|------------------------------------------------------|--------------------------|
| `/ecomobs reload`                          | Reloads the plugin                                   | `ecomobs.command.reload` |
| `/ecomobs spawn <mob> [x] [y] [z] [world] [amount]` | Spawns the EcoMobs, optionally spawning multiple at once | `ecomobs.command.spawn`  |
| `/ecomobs give <player> <mob>`             | Give the player an EcoMob spawn egg                   | `ecomobs.command.give`   |
| `/ecomobs import <id>`                     | Import a mob from [lrcdb](https://lrcdb.auxilor.io/) | `ecomobs.command.import` |
| `/ecomobs export <id>`                     | Export a mob to [lrcdb](https://lrcdb.auxilor.io/)   | `ecomobs.command.export` |

## Spawner Commands

| Command                                                          | Description                                                                        | Permission                          |
|------------------------------------------------------------------|------------------------------------------------------------------------------------|-------------------------------------|
| `/ecomobs spawner give <player> <mob> [amount] [attributes...]`  | Give spawner items to a player, with optional attribute customization              | `ecomobs.command.spawner.give`      |
| `/ecomobs spawner modify <attribute> [value...]`                 | Modify attributes on a held spawner item or targeted spawner block (player only)  | `ecomobs.command.spawner.modify`    |

To modify a placed spawner, look at a spawner within 5 blocks and run the modify command.

### Spawner Attributes

Both the `give` and `modify` commands accept the following attributes:

| Attribute          | Value(s)                                       | Description                                          | Modify Permission                                  |
|--------------------|------------------------------------------------|------------------------------------------------------|----------------------------------------------------|
| `mob`              | `<mobId>`                                      | The mob type the spawner spawns                      | `ecomobs.command.spawner.modify.mob`               |
| `delay`            | `<min> <max>`                                  | Spawn delay range in ticks                           | `ecomobs.command.spawner.modify.delay`             |
| `radius`           | `<value>`                                      | Spawn radius in blocks (min 1)                       | `ecomobs.command.spawner.modify.radius`            |
| `player-radius`    | `<value>`                                      | Required player proximity in blocks (min 1)          | `ecomobs.command.spawner.modify.player-radius`     |
| `count`            | `<value>`                                      | Number of entities to spawn per cycle (min 1)        | `ecomobs.command.spawner.modify.count`             |
| `max-nearby`       | `<value>`                                      | Maximum nearby entities before suppressing (min 1)   | `ecomobs.command.spawner.modify.max-nearby`        |
| `pickup`           | `allow` \| `silk_touch` \| `deny`              | How players can pick up the spawner                  | `ecomobs.command.spawner.modify.pickup`            |
| `particle`         | `none` \| `<animation_name>`                   | Particle animation displayed by the spawner          | `ecomobs.command.spawner.modify.particle`          |
| `explosion-proof`  | `true` \| `false`                              | Whether the spawner survives explosions              | `ecomobs.command.spawner.modify.explosion-proof`   |

### Additional Permissions

These permissions are not tied to commands but control player interactions with spawners:

| Permission                           | Description                                                              |
|--------------------------------------|--------------------------------------------------------------------------|
| `ecomobs.spawner.pickup`             | Allows picking up spawners when the pickup mode is set to `allow`        |
| `ecomobs.spawner.pickup.silktouch`   | Allows picking up spawners with Silk Touch when pickup mode is `silk_touch` |
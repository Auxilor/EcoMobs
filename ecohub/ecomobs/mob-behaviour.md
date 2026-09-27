---
title: "Mob Behaviour"
sidebar_position: 6
---

Two optional tweaks take away the vanilla weaknesses that get in the way of farms and arenas: water hurting endermen and blazes, and undead mobs burning in daylight. Both are off by default and live at the top level of `config.yml`.

They apply to **every** mob of the listed types on the server, not only EcoMobs or mobs from spawners. Run `/ecomobs reload` to apply changes.

## Water sensitivity

```yaml
water-sensitivity:
  enabled: false
  mobs:
    - enderman
    - blaze
```

With `enabled: true`, the listed mobs stop taking damage from water — standing in it, rain, and bubble columns — and endermen stop teleporting away from it. An enderman farm can hold its endermen in water, and a blaze spawner can sit next to one.

Any mob that vanilla hurts with water can go in the list, such as `snow_golem`.

:::info
**Paper** says why an enderman teleports, so only the teleports away from water are stopped. **Spigot** doesn't, so every teleport is stopped while the enderman is wet — in water, or out in the rain — including dodging an arrow or getting away from a player.
:::

## Sunlight burning

```yaml
sunlight-burning:
  enabled: false
  mobs:
    - zombie
    - zombie_villager
    - skeleton
    - stray
    - bogged
    - drowned
    - phantom
```

With `enabled: true`, the listed mobs no longer catch fire in daylight. They still burn in lava and fire, and from fire aspect and flaming arrows.

<hr/>

## Where to go next

- **Spawners:** [Custom Spawners](custom-spawners) for how spawners tick.
- **Server settings:** the [Plugin Config](plugin-config) reference.

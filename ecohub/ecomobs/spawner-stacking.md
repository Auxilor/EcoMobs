---
title: "Spawner Stacking"
sidebar_position: 5
---

Spawner stacking lets players merge identical spawners into one block that spawns for all of them. Sixty-four spawners in a grinder become one block spawning sixty-four times a cycle, instead of sixty-four blocks fighting for space.

It applies to EcoMobs spawners — see [Custom Spawners](custom-spawners) for how to make one. Vanilla dungeon spawners do not stack.

## Config

Everything lives under `spawner-stacking` in `config.yml`.

```yaml
spawner-stacking:
  enabled: true

  max-size: 64 # The most spawners one stack can hold

  hologram:
    enabled: true
    look-at-only: false # Show only to a player aiming at the spawner
    look-at-distance: 5 # How far away that still counts; only used with look-at-only
    height: 1.5 # Blocks above the spawner to float the text

    # %size% is the combined size, %mob% and %mob_formatted% are the mob.
    line: "&f%size%x &7%mob_formatted% Spawner"

    header: "" # Shown above the lines; leave empty for none
```

Run `/ecomobs reload` to apply changes.

## Stacking spawners

**Right-click** a placed spawner while holding a matching one to add it to the stack. **Sneak** and right-click to add every spawner in your hand at once. Placing a spawner against a matching one does the same thing.

Only what fits is taken. Sneak right-clicking a stack of 60 with 16 spawners in hand, at `max-size: 64`, adds 4 and leaves 12 in your inventory.

Creative mode consumes nothing.

### What counts as matching

Two spawners merge only when every attribute is identical — mob, delay, radius, player radius, count, max nearby, pickup, particle, and explosion-proof. Stack size itself is not compared.

Two spawners for the same mob will not stack if one was given a different delay or a different particle. If players are ending up with spawners that refuse to merge, check that whatever hands them out sets the same attributes every time.

## Breaking a stack

A normal break takes **one spawner** off the stack and drops it as a single item, leaving the rest standing.

**Sneak** and break to take the **whole stack** as one item, carrying its size with it.

Either way the `pickup` attribute still decides whether anything drops at all, and the permission behind it still applies. See [Custom Spawners](custom-spawners#picking-spawners-back-up).

## How a stack spawns

A stacked spawner spawns as many mobs as it holds, on the one cycle. A stack of 10 with `count: 4` spawns 40 mobs when it fires.

It fires on the same schedule a single spawner would — stacking multiplies the mobs per cycle, not the number of cycles. This holds in both [spawn methods](custom-spawners#spawn-methods).

:::warning
`max-nearby` is unchanged by stacking, so it is what actually caps a grinder. A stack of 64 that spawns 256 mobs at once will stop firing almost immediately against the default `max-nearby: 6`. Raise it on spawners meant to be stacked, or pair them with [Mob Stacking](mob-stacking) so the mobs merge as fast as they appear.
:::

## Holograms

A stacked spawner floats a hologram showing what it holds.

Spawners stacked **directly on top of each other** share a single hologram at the top of the column, with one line per distinct mob and their sizes summed, largest first. A tower of zombie and skeleton spawners reads as two lines, not one per block.

```yaml
line: "&f%size%x &7%mob_formatted% Spawner"
header: "&8&m---------"
```

| Placeholder | Value |
| --- | --- |
| `%size%` | The combined size of that mob in the column |
| `%mob%` | The mob ID |
| `%mob_formatted%` | The mob ID as a title, so `hollow_king` reads `Hollow King` |

`header` is optional and prints once above the lines. A lone unstacked spawner shows no hologram.

Set `look-at-only: true` to hide holograms until a player aims at the column, within `look-at-distance` blocks. Useful on servers with dense spawner rooms where a wall of floating text gets in the way.

:::info
Holograms need a supported hologram plugin installed. Without one, stacking still works — there is simply nothing floating above the block. Everything else on this page is unaffected.
:::

## Giving out stacked spawners

`stack-size` is a spawner attribute like any other:

```
/ecomobs spawner give Notch zombie stack-size 16
```

That hands over one item that places as a stack of 16.

The item's lore gains the `spawner-display.stacked-lore` lines from `config.yml` whenever it holds more than one:

```yaml
spawner-display:
  stacked-lore:
    - "&8Stack: &f%size%"
```

<hr/>

## Where to go next

- **Spawner attributes and spawn methods:** [Custom Spawners](custom-spawners).
- **Stack the mobs they spawn:** [Mob Stacking](mob-stacking).
- **Commands and permissions:** [Commands and Permissions](commands-and-permissions).

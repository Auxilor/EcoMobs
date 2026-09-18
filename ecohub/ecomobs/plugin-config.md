---
title: "Plugin Config"
sidebar_position: 7
---

The main settings for EcoMobs live in `config.yml`, found at `/plugins/EcoMobs/config.yml`. It controls the custom spawning loop, mob and spawner stacking, how spawners tick, the spawner item display, and the particle animations spawners can use. Edit the file and run `/ecomobs reload` to apply your changes.

The stacking and spawner blocks have pages of their own — [Mob Stacking](mob-stacking), [Spawner Stacking](spawner-stacking), and [Custom Spawners](custom-spawners) — which cover what each setting does in the world rather than just what it is.

## Default config.yml

```yaml
discover-recipes: true # If spawn-egg recipes are auto-unlocked in the recipe book

top-damager-places: 10 # How many %top_damager_<place>_...% placeholders to generate

custom-spawning:
  spawn-rate: 12 # Ticks between spawn attempts; 20 ticks = 1 second, higher = rarer
  radius-around-player: 32 # Radius in blocks to generate spawn points around each player
  max-points-per-player: 8 # Max spawn points generated per player
  max-mobs-per-player: 24 # Max custom mobs alive per player
  max-attempts: 64 # Max tries to generate a valid spawn point per player

stacking: # Merges nearby mobs of the same kind into one entity; see Mob Stacking
  enabled: true
  radius: 8 # How far to look for a stack to merge into
  max-size: 64 # The most mobs one stack can hold
  sweep-rate: 100 # Ticks between sweeps for mobs that drifted together after spawning
  kill-whole-stack: false # true kills and pays out the whole stack at once
  hide-death-animation: false # true takes the corpse away instead of playing the death animation
  match-age: true # Whether babies and adults are kept in separate stacks
  nameplate: "&f%name% &7x%size%" # Shown above a stack; %size% and %name%
  blacklist: # Never stacks; EcoMob IDs and vanilla entity types both work
    - villager
    - wandering_trader
    - ender_dragon
    - wither
  exclude: # Set any to false to let that group stack anyway
    tamed: true # Pets and other tamed mobs
    leashed: true # Mobs on a lead
    mounted: true # Mobs riding something else
    ridden: true # Mobs carrying passengers
    named: true # Mobs already named, by a name tag or another plugin

spawners: # How spawners tick; see Custom Spawners
  tick-rate: 5 # Ticks between spawner loop runs
  redstone-deactivates: true # Whether a powered spawner stops spawning
  max-mobs-per-chunk: 50 # Entities a chunk can hold before its spawners stop; counts entities, NOT summed stack sizes; 0 is off
  checks: # The spawn requirements; every default is what vanilla does
    spawn-space: true # Whether a mob needs room where it would spawn
    solid-ground: false # Whether a mob needs solid ground; vanilla spawners do not require it
    max-nearby: true # Whether the nearby-mob cap applies
    player-range: true # Whether a player has to be in range for the spawner to count down
    light-level: true # Whether mobs that need darkness are held to it
    max-light-level: 0 # The most block light a darkness-spawning mob tolerates

spawner-stacking: # Merges identical spawners into one block; see Spawner Stacking
  enabled: true
  max-size: 64 # The most spawners one stack can hold
  hologram:
    enabled: true
    look-at-only: false # Show only to a player aiming at the spawner
    look-at-distance: 5 # How far away that still counts; only used with look-at-only
    height: 1.5 # Blocks above the spawner to float the text
    line: "&f%size%x &7%mob_formatted% Spawner" # One line per mob in the column
    header: "" # Shown above the lines; leave empty for none

spawner-display: # The held/placed spawner item's name and lore; supports the placeholders shown
  title: "&f%mob% Spawner"
  lore:
    - "&8Mob: &f%mob%"
    - "&8Delay: &f%delay_min%-%delay_max% ticks"
    - "&8Radius: &f%radius%"
    - "&8Player Range: &f%player_range%"
    - "&8Count: &f%count%"
    - "&8Max Nearby: &f%max_nearby%"
    - "&8Pickup: &f%pickup%"
    - "&8Particle: &f%particle%"
    - "&8Explosion-Proof: &f%explosion_proof%"
    - "&8No AI: &f%no_ai%"
  stacked-lore: # Added only when the item holds more than one spawner
    - "&8Stack: &f%size%"

animations: # Reusable particle motion shapes, referenced by spawner-animations below
  circle:
    spirals-per-second: 0.5 # How fast the particle orbits
    radius: 1.0 # Orbit radius in blocks
    height: 0.5 # Height above the spawner
    count: 1 # Particles emitted per step
  spiral:
    spirals-per-second: 0.5 # How fast the particle orbits
    rises-per-second: 0.3 # How fast the spiral climbs
    radius: 1.0 # Orbit radius in blocks
    height: 0.5 # Base height above the spawner
    count: 1 # Particles emitted per step
  double_spiral:
    spirals-per-second: 0.5 # How fast each particle orbits
    rises-per-second: 0.3 # How fast the spirals climb
    radius: 1.0 # Orbit radius in blocks
    height: 0.5 # Base height above the spawner
    count: 1 # Particles emitted per step
  tilted_rings:
    spirals-per-second: 0.5 # How fast the ring rotates
    radius: 1.0 # Ring radius in blocks
    x-offset: 0.5 # Horizontal tilt offset
    y-offset: 0.5 # Vertical tilt offset
    count: 1 # Particles emitted per step
  twirl:
    small-radius: 0.2 # Inner radius in blocks
    large-radius: 1.0 # Outer radius in blocks
    ticks: 40.0 # Duration of one twirl cycle in ticks
    start-height: 0.0 # Height the twirl starts at
    end-height: 1.0 # Height the twirl ends at
    spirals-per-second: 0.5 # How fast the particle orbits
    count: 1 # Particles emitted per step

spawner-animations: # Named animations players can pick via the spawner particle attribute
  circle_flame:
    particle: flame # The Bukkit particle to draw
    type: circle # The animation shape from the animations block above
  spiral_end_rod:
    particle: end_rod # The Bukkit particle to draw
    type: spiral # The animation shape from the animations block above
```

<hr/>

## Where to go next

- **Spawners:** [Custom Spawners](custom-spawners) for attributes and spawn methods.
- **Stacking:** [Mob Stacking](mob-stacking) and [Spawner Stacking](spawner-stacking).
- **Commands:** [Commands and Permissions](commands-and-permissions) for giving and modifying spawner items.
- **Make a mob:** [How to Make a Custom Mob](how-to-make-a-custom-mob).
- **Spawning behaviour:** [How to Make Mob Categories](how-to-make-mob-categories).
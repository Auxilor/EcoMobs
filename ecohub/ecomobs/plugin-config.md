---
title: "Plugin Config"
sidebar_position: 4
---

The main settings for EcoMobs live in `config.yml`, found at `/plugins/EcoMobs/config.yml`. It controls the custom spawning loop, the spawner item display, and the particle animations spawners can use. Edit the file and run `/ecomobs reload` to apply your changes.

## Default config.yml

```yaml
discover-recipes: true # If spawn-egg recipes are auto-unlocked in the recipe book

custom-spawning:
  spawn-rate: 12 # Ticks between spawn attempts; 20 ticks = 1 second, higher = rarer
  radius-around-player: 32 # Radius in blocks to generate spawn points around each player
  max-points-per-player: 8 # Max spawn points generated per player
  max-mobs-per-player: 24 # Max custom mobs alive per player
  max-attempts: 64 # Max tries to generate a valid spawn point per player

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

- **Spawners:** [Commands and Permissions](commands-and-permissions) for giving and modifying spawner items.
- **Make a mob:** [How to Make a Custom Mob](how-to-make-a-custom-mob).
- **Spawning behaviour:** [How to Make Mob Categories](how-to-make-mob-categories).
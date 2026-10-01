---
title: ecomobs:last_damager_player
---

Makes a mob target the player who damaged it last, if they are in range

# Example Config
```yaml
- key: ecomobs:last_damager_player
  priority: 0
  args:
    range: 40 # The distance to scan for players.
    interval: 10 # The time to wait between choosing a target, in ticks.
```

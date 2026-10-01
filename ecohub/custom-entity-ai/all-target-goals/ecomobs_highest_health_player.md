---
title: ecomobs:highest_health_player
---

Makes a mob target the player in range with the highest health

# Example Config
```yaml
- key: ecomobs:highest_health_player
  priority: 0
  args:
    range: 40 # The distance to scan for players.
    interval: 10 # The time to wait between choosing a target, in ticks.
```

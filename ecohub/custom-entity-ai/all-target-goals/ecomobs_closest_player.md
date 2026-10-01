---
title: ecomobs:closest_player
---

Makes a mob target the closest player in range

# Example Config
```yaml
- key: ecomobs:closest_player
  priority: 0
  args:
    range: 40 # The distance to scan for players.
    interval: 10 # The time to wait between choosing a target, in ticks.
```

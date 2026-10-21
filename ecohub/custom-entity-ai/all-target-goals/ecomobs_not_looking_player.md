---
title: ecomobs:not_looking_player
---

Makes a mob target the closest player in range who is facing away from it

# Example Config
```yaml
- key: ecomobs:not_looking_player
  priority: 0
  args:
    range: 40 # The distance to scan for players.
    interval: 10 # The time to wait between choosing a target, in ticks.
```

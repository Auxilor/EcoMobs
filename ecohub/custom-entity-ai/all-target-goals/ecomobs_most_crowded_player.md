---
title: ecomobs:most_crowded_player
---

Makes a mob target the player in range with the most other players near them

# Example Config
```yaml
- key: ecomobs:most_crowded_player
  priority: 0
  args:
    range: 40 # The distance to scan for players.
    interval: 10 # The time to wait between choosing a target, in ticks.
    radius: 5 # The distance to count other players within.
```

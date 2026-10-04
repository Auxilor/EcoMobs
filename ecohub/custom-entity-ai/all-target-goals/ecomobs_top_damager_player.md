---
title: ecomobs:top_damager_player
---

Makes a mob target the player in range who has dealt it the most damage

# Example Config
```yaml
- key: ecomobs:top_damager_player
  priority: 0
  args:
    range: 40 # The distance to scan for players.
    interval: 10 # The time to wait between choosing a target, in ticks.
```

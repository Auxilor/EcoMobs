---
title: ecomobs:random_player
---

Makes a mob target a random player in range, chosen again every interval

# Example Config
```yaml
- key: ecomobs:random_player
  priority: 0
  args:
    range: 40 # The distance to scan for players.
    interval: 10 # The time to wait between choosing a target, in ticks.
```

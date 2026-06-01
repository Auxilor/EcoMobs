---
title: "API"
sidebar_position: 5
---

This page is for developers who want to build against EcoMobs from their own plugin. EcoMobs is open-source, so you can read the code, depend on it, and hook into its mobs and events.

## Source code

The source code lives on GitHub [here](https://github.com/Auxilor/EcoMobs).

## Adding the dependency

1. Add the Auxilor repository to your `build.gradle.kts`:

   ```kotlin
   repositories {
       maven("https://repo.auxilor.io/repository/maven-public/")
   }
   ```

2. Add EcoMobs as a `compileOnly` dependency:

   ```kotlin
   dependencies {
       compileOnly("com.willfp:EcoMobs:<version>")
   }
   ```

The latest version available on the repo can be found [here](https://github.com/Auxilor/EcoMobs/tags).

<hr/>

## Where to go next

- **eco framework:** the shared APIs live in [eco](https://github.com/Auxilor/eco).
- **Config side:** [How to Make a Custom Mob](how-to-make-a-custom-mob) for the config-driven workflow.
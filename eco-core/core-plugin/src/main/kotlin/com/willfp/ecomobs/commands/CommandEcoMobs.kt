package com.willfp.ecomobs.commands

import com.willfp.ecomobs.plugin
import wiki.ecohub.core.command.EcoCommand
import wiki.ecohub.core.command.command

internal fun ecoMobsCommand(): EcoCommand =
    plugin.command(name = "ecomobs", permission = "ecomobs.command.ecomobs") {
        mount(CommandReload, CommandSpawn, CommandGive, CommandKillAll)
        subcommand(name = "spawner", permission = "ecomobs.command.spawner") {
            mount(CommandSpawnerGive, CommandSpawnerModify)
        }
        subcommand(name = "schedule", permission = "ecomobs.command.schedule") {
            mount(CommandScheduleList, CommandScheduleReset)
        }
    }

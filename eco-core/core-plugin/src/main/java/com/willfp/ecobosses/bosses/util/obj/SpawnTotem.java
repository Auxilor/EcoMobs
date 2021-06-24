package com.willfp.ecobosses.bosses.util.obj;

import lombok.Data;
import org.bukkit.Material;

public record SpawnTotem(Material bottom,
                         Material middle,
                         Material top) {
}

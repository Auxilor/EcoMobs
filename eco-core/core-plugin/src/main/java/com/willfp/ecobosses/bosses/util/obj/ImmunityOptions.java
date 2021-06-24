package com.willfp.ecobosses.bosses.util.obj;

import lombok.Data;

public record ImmunityOptions(boolean immuneToFire,
                              boolean immuneToSuffocation,
                              boolean immuneToDrowning,
                              boolean immuneToProjectiles,
                              boolean immuneToExplosions) {
}

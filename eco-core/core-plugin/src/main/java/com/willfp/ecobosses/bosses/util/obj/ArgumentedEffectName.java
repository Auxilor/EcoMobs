package com.willfp.ecobosses.bosses.util.obj;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ArgumentedEffectName(@NotNull String name, @NotNull List<String> args) {
}

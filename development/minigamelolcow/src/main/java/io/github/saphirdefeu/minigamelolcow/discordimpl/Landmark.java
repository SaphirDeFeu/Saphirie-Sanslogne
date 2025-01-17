package io.github.saphirdefeu.minigamelolcow.discordimpl;

import org.jetbrains.annotations.NotNull;

public final class Landmark {
    public final int x;
    public final int z;
    public final String name;

    public Landmark(@NotNull String name, int x, int z) {
        this.name = name;
        this.x = x;
        this.z = z;
    }
}

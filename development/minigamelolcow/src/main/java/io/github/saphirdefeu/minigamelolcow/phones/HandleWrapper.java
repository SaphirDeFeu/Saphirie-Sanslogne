package io.github.saphirdefeu.minigamelolcow.phones;

import org.jetbrains.annotations.NotNull;

public class HandleWrapper {
    private String type;
    private String namespacedId;
    private Runnable runnable;

    public HandleWrapper(@NotNull String type, @NotNull String namespacedId, @NotNull Runnable runnable) {
        this.type = type;
        this.namespacedId = namespacedId;
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public String getNamespacedId() {
        return namespacedId;
    }

    public String getType() {
        return type;
    }
}

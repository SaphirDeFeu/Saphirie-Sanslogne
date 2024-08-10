package io.github.saphirdefeu.minigamelolcow.tests;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import jdk.jfr.Experimental;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

@Experimental
public abstract class PluginCommandCreator {
    private String name;
    private String description;
    private Collection<String> aliases;

    public PluginCommandCreator(@NotNull String name, String description, Collection<String> aliases) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
    }

    public final LiteralCommandNode<CommandSourceStack> createLiteral() {
        return Commands.literal(this.name)
                .executes(ctx -> this.execute(ctx.getSource()))
                .build();
    }

    public final String getName() { return name; }

    public final String getDescription() { return description; }

    public final Collection<String> getAliases() { return aliases; }

    public abstract int execute(CommandSourceStack stack);
}
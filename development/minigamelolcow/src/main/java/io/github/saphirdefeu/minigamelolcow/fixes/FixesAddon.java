package io.github.saphirdefeu.minigamelolcow.fixes;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class FixesAddon {

    public static final String RESOURCE_LOCATION = "mlc:fixes";

    public FixesAddon(JavaPlugin plugin) {
        registerCommands(plugin);
    }

    private void registerCommands(JavaPlugin plugin) {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            // ! Commandes

            // !
        });
    }
}

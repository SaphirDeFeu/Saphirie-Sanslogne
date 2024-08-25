package io.github.saphirdefeu.minigamelolcow.phones;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class PhonesAddon {
    public static final String RESOURCE_LOCATION = "mlc:phones";


    public PhonesAddon(JavaPlugin plugin) {
        registerCommands(plugin);
    }

    public void registerCommands(JavaPlugin plugin) {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            commands.register()
        });
    }
}

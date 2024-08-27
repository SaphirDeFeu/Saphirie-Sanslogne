package io.github.saphirdefeu.minigamelolcow.phones;

import io.github.saphirdefeu.minigamelolcow.phones.cmd.*;
import io.github.saphirdefeu.minigamelolcow.phones.api.*;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.python.util.PythonInterpreter;

import java.util.Properties;

public final class PhonesAddon {
    public static final String RESOURCE_LOCATION = "mlc:phones";
    private PythonInterpreter interpreter;

    public PhonesAddon(JavaPlugin plugin) {
        Data.init();

        initJython();

        registerCommands(plugin);
    }

    private void initJython() {
        Properties properties = new Properties();
        properties.put("python.import.site", "false");
        PythonInterpreter.initialize(System.getProperties(), properties, new String[0]);
        interpreter = new PythonInterpreter();

        // API exposition
        interpreter.setOut(new Debug());
    }

    private void registerCommands(JavaPlugin plugin) {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            commands.register(Phone.name, Phone.description, Phone.aliases, new Phone());
        });
    }

    public void destroy() {
        if(interpreter != null) {
            interpreter.close();
        }
    }
}

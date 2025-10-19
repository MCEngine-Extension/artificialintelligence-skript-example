package io.github.mcengine.extension.skript.artificialintelligence.example;

import io.github.mcengine.api.core.MCEngineCoreApi;
import io.github.mcengine.api.core.extension.logger.MCEngineExtensionLogger;
import io.github.mcengine.api.artificialintelligence.extension.skript.IMCEngineArtificialIntelligenceSkript;

import io.github.mcengine.extension.skript.artificialintelligence.example.command.AISkriptCommand;
import io.github.mcengine.extension.skript.artificialintelligence.example.listener.AISkriptListener;
import io.github.mcengine.extension.skript.artificialintelligence.example.tabcompleter.AISkriptTabCompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Main class for the Artificial Intelligence <b>Skript</b> example module.
 * <p>
 * Registers the {@code /aiskriptexample} command and related event listeners.
 * This class demonstrates how to wire up commands and listeners while integrating
 * with the {@link IMCEngineArtificialIntelligenceSkript} extension lifecycle.
 */
public class ExampleArtificialIntelligenceSkript implements IMCEngineArtificialIntelligenceSkript {

    /** Custom extension logger for this module, with contextual labeling. */
    private MCEngineExtensionLogger logger;

    /**
     * Initializes the AI Skript example module.
     * Called automatically by the MCEngine core plugin.
     *
     * @param plugin The Bukkit plugin instance.
     */
    @Override
    public void onLoad(Plugin plugin) {
        // Initialize contextual logger once and keep it for later use.
        this.logger = new MCEngineExtensionLogger(plugin, "Skript", "ArtificialIntelligenceExampleSkript");

        try {
            // Register event listener
            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.registerEvents(new AISkriptListener(plugin, this.logger), plugin);

            // Reflectively access Bukkit's CommandMap
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            // Define the /aiskriptexample command
            Command aiSkriptExampleCommand = new Command("aiskriptexample") {

                /** Handles command execution for /aiskriptexample. */
                private final AISkriptCommand handler = new AISkriptCommand();

                /** Handles tab-completion for /aiskriptexample. */
                private final AISkriptTabCompleter completer = new AISkriptTabCompleter();

                @Override
                public boolean execute(CommandSender sender, String label, String[] args) {
                    return handler.onCommand(sender, this, label, args);
                }

                @Override
                public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
                    return completer.onTabComplete(sender, this, alias, args);
                }
            };

            aiSkriptExampleCommand.setDescription("Artificial Intelligence Skript example command.");
            aiSkriptExampleCommand.setUsage("/aiskriptexample");

            // Dynamically register the /aiskriptexample command
            commandMap.register(plugin.getName().toLowerCase(), aiSkriptExampleCommand);

            this.logger.info("Enabled successfully.");
        } catch (Exception e) {
            this.logger.warning("Failed to initialize ExampleArtificialIntelligenceSkript: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called when the AI Skript example module is disabled/unloaded.
     *
     * @param plugin The Bukkit plugin instance.
     */
    @Override
    public void onDisload(Plugin plugin) {
        if (this.logger != null) {
            this.logger.info("Disabled.");
        }
    }

    /**
     * Sets the unique ID for this module.
     *
     * @param id the assigned identifier (ignored; a fixed ID is used for consistency)
     */
    @Override
    public void setId(String id) {
        MCEngineCoreApi.setId("mcengine-artificialintelligence-skript-example");
    }
}

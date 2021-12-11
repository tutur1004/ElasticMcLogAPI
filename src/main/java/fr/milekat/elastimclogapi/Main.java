package fr.milekat.elastimclogapi;

import fr.milekat.elastimclogapi.commands.ConfigRl;
import fr.milekat.elastimclogapi.data.DataManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static DataManager dataManager;

    @Override
    public void onEnable() {
        //  Init config file
        this.getConfig();
        // TODO: 11/12/2021 Debug :)
        dataManager = new DataManager(this);
        PluginCommand command = this.getCommand("eslogapi-reload");
        if (command!=null) command.setExecutor(new ConfigRl());
    }

    @Override
    public void onDisable() {
        dataManager.closePool();
    }

    /**
     * Get data manager
     */
    public static DataManager getDataManager() {
        return dataManager;
    }
}

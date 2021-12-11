package fr.milekat.elastimclogapi;

import fr.milekat.elastimclogapi.commands.ConfigRl;
import fr.milekat.elastimclogapi.data.DataManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class ESMcLog extends JavaPlugin {
    private DataManager dataManager;
    private static Api api;

    @Override
    public void onEnable() {
        //  Init config file
        this.getConfig();
        // TODO: 11/12/2021 Debug :)
        dataManager = new DataManager(this);
        api = new Api(dataManager);
        PluginCommand command = this.getCommand("eslogapi-reload");
        if (command!=null) command.setExecutor(new ConfigRl(dataManager));
    }

    @Override
    public void onDisable() {
        dataManager.closePool();
    }

    /**
     * Get api
     */
    public static Api getApi() {
        return api;
    }
}

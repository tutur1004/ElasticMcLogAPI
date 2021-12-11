package fr.milekat.elastimclogapi.commands;

import fr.milekat.elastimclogapi.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public class ConfigRl implements CommandExecutor {
    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command,
                             @Nonnull String label, @Nonnull String[] args) {
        Main.getDataManager().reloadConfigs();
        Main.getDataManager().restartPool();
        return true;
    }
}

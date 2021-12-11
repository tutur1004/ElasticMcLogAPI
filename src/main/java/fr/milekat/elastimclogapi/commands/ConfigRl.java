package fr.milekat.elastimclogapi.commands;

import fr.milekat.elastimclogapi.data.DataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public record ConfigRl(DataManager dataManager) implements CommandExecutor {
    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command,
                             @Nonnull String label, @Nonnull String[] args) {
        dataManager.reloadConfigs();
        dataManager.restartPool();
        return true;
    }
}

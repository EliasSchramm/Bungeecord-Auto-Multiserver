package de.epsdev.bungeeautoserver.spigottest.Bungeeautoserver.commands;

import de.epsdev.bungeeautoserver.api.EPS_API;
import de.epsdev.bungeeautoserver.api.packages.RequestServerAvailabilityChangePackage;
import de.epsdev.bungeeautoserver.spigottest.Bungeeautoserver.BungeecordAutoConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class c_closeserver implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;

            if(player.hasPermission("bungee.opencloseserver")){
                BungeecordAutoConfig.eps_api.connection.send(new RequestServerAvailabilityChangePackage(
                        true
                ));

                player.sendMessage(ChatColor.DARK_RED + "Closed this server. Type /openserver to open it again.");
            }else {
                player.sendMessage(ChatColor.RED + "You don't have the required permissions to do that.");
            }
        }
        return true;
    }
}

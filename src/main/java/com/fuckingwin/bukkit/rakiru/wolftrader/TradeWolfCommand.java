package com.fuckingwin.bukkit.rakiru.wolftrader;

import java.util.List;
import java.util.Random;
import java.lang.Math;
import org.bukkit.util.Vector;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.ChatColor;
import org.bukkit.util.config.Configuration;

/**
 * Handler for the /tradewolf command.
 * @author rakiru
 */
public class TradeWolfCommand implements CommandExecutor {

    private final WolfTraderPlugin plugin;

    public TradeWolfCommand(WolfTraderPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        // Check if they can give away wolves
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to give your wolves");
            return true;
        }
        // Check if they have permission to give away their wolves - defaults to all users
        if (plugin.usePermissions && !(plugin.permissionHandler.has((Player) sender, "wolftrader.give"))) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return true;
        }
        Player giver = (Player)sender;
        String giverName = giver.getDisplayName();
        if (split.length == 1) {
            List<Player> matchedPlayers = plugin.getServer().matchPlayer(split[0]);
            if (matchedPlayers.size() == 1) {
                //player found with no force argument
                String receiverName = matchedPlayers.get(0).getDisplayName();
                List<Entity> nearbyEntities = giver.getNearbyEntities(7, 5, 7);
                if (nearbyEntities.isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "You do not have any wolves near you");
                    return true;
                }
                for (Entity nearbyEntity : nearbyEntities) {
                    if (nearbyEntity instanceof Wolf) {
                        Wolf nearbyWolf = (Wolf) nearbyEntity;
                        Player nearbyWolfOwner = (Player)nearbyWolf.getOwner();
                        if (nearbyWolfOwner.equals(giver)) {
                            transferWolf(matchedPlayers.get(0), nearbyWolf, giver);
                            return true;
                        }
                    }
                }
                sender.sendMessage(ChatColor.RED + "You do not have any wolves near you");
                return true;
            } else if (matchedPlayers.isEmpty()) {
                //player not found
                sender.sendMessage("Player " + split[0] + " not found");
            } else if (matchedPlayers.size() > 1) {
                //Multiple matching players found
                sender.sendMessage(split[0] + " matches multiple players");
            } else {
                //Unknown error
                sender.sendMessage("Ah, crap!");
            }
            return true;
        } else {
            return false;
        }
    }

    private void transferWolf(Player receiver, Wolf wolf, Player giver) {
        wolf.setOwner((AnimalTamer) receiver);
        receiver.sendMessage(ChatColor.GREEN + "You have received a wolf from " + giver.getDisplayName());
        giver.sendMessage(ChatColor.GREEN + "You ahve given a wolf to " + receiver.getDisplayName());
    }
}

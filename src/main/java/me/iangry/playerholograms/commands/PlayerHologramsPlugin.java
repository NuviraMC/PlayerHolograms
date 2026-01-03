package me.iangry.playerholograms.commands;

import me.iangry.playerholograms.HologramGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PlayerHologramsPlugin implements CommandExecutor {
   private final HologramGUI plugin;

   public PlayerHologramsPlugin(HologramGUI plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
      if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
         if (sender.hasPermission("playerholograms.reload")) {
            this.plugin.reloadConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2[&aPlayerHolograms&2]"));
            sender.sendMessage(String.valueOf(ChatColor.RED) + "Main configuration reloaded.");
            sender.sendMessage(String.valueOf(ChatColor.RED) + "If you want hologram text updated, restart the server instead.");
         } else {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission to reload the configuration.");
         }

         return true;
      } else if (args.length == 0) {
         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2[&aPlayerHolograms&2]"));
         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDeveloper: &7iAngry"));
         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aVersion: &7" + this.plugin.getDescription().getVersion()));
         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aCommands: &7/HologramGUI"));
         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aCommands: &7/PlayerHologramsPlugin Reload"));
         return true;
      } else {
         sender.sendMessage(String.valueOf(ChatColor.RED) + "Unknown command. Use '/HologramGUI'");
         return false;
      }
   }
}

package me.iangry.playerholograms.commands;

import me.iangry.playerholograms.HologramGUI;
import me.iangry.playerholograms.gui.GUIManager;
import me.iangry.playerholograms.utils.ColourUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HologramCommand implements CommandExecutor {
   private final HologramGUI plugin;

   public HologramCommand(HologramGUI plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!(sender instanceof Player)) {
         sender.sendMessage(String.valueOf(ChatColor.RED) + "This command is only for players.");
         return true;
      } else {
         Player player = (Player)sender;
         if (player.hasPermission("playerholograms.use")) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.chat.opening-player-holograms"))));
            GUIManager.openMainMenu(player);
            return true;
         } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtils.colorize(String.valueOf(ChatColor.RED) + "You do not have permission to create holograms.")));
            return true;
         }
      }
   }
}

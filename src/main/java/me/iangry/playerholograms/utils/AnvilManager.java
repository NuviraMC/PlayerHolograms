package me.iangry.playerholograms.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import me.iangry.playerholograms.HologramGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AnvilManager implements Listener {
   private static final Map<UUID, Consumer<String[]>> pendingInputs = new HashMap<>();

   public AnvilManager() {
      // Register listener
      Bukkit.getPluginManager().registerEvents(this, HologramGUI.getInstance());
   }

   public void openAnvilInput(Player player, Consumer<String[]> callback) {
      // Store the callback for this player
      pendingInputs.put(player.getUniqueId(), callback);

      // Send instructions to player
      player.sendMessage(ChatColor.DARK_GRAY + "==============================");
      player.sendMessage(ChatColor.GRAY + "Bitte gebe dein §bHologramm-Text§7 in den §bChat§7 ein:");
      player.sendMessage(ChatColor.GRAY + "Nutze den '|' Strich um mehrere Zeilen zu erstellen.");
      player.sendMessage(ChatColor.GRAY + "Beispiel: Hallo NuviraMC|Dies hier ist mein Plot");
      player.sendMessage(ChatColor.GRAY + "schreibe §8'§ccancel§8'§7 um abzubrechen.");
      player.sendMessage(ChatColor.DARK_GRAY + "==============================");
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onPlayerChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      UUID playerId = player.getUniqueId();

      // Check if this player has a pending input
      if (pendingInputs.containsKey(playerId)) {
         event.setCancelled(true); // Cancel the chat event

         String message = event.getMessage().trim();
         Consumer<String[]> callback = pendingInputs.remove(playerId);

         // Handle cancel
         if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(ChatColor.RED + "Hologram creation cancelled.");
            return;
         }

         // Check if empty
         if (message.isEmpty()) {
            player.sendMessage(ChatColor.RED + "You must enter some text.");
            // Re-add the callback so they can try again
            pendingInputs.put(playerId, callback);
            return;
         }

         // Split by | for multiple lines
         String[] lines = message.split("\\|");

         // Send confirmation
         String inputReceivedMsg = HologramGUI.getInstance().getConfig().getString("messages.anvil.input-received");
         player.sendMessage(ColourUtils.colorize(inputReceivedMsg) + message);

         // Call the callback on the main thread
         Bukkit.getScheduler().runTask(HologramGUI.getInstance(), () -> callback.accept(lines));
      }
   }
}

package me.iangry.playerholograms.gui;

import me.iangry.playerholograms.HologramGUI;
import me.iangry.playerholograms.hologram.HologramManager;
import me.iangry.playerholograms.utils.AnvilManager;
import me.iangry.playerholograms.utils.ColourUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {
   private final HologramManager hologramManager;
   private final AnvilManager anvilManager;

   public GUIListener(HologramManager hologramManager, AnvilManager anvilManager) {
      this.hologramManager = hologramManager;
      this.anvilManager = anvilManager;
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      if (event.getWhoClicked() instanceof Player) {
         Player player = (Player)event.getWhoClicked();
         if (event.getClickedInventory() != null && event.getCurrentItem() != null) {
            ItemStack clickedItem = event.getCurrentItem();
            if (event.getView().getTitle().equals(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.gui.main-gui.title")))) {
               event.setCancelled(true);
               if (clickedItem.getType() == Material.NAME_TAG) {
                  player.sendMessage(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.gui.main-gui.opening-anvil")));
                  player.closeInventory();
                  this.anvilManager.openAnvilInput(player, (newLines) -> {
                     this.hologramManager.createHologram(player, newLines);
                  });
               } else if (clickedItem.getType() == Material.PAPER) {
                  (new HologramListGUI(this.hologramManager, this.anvilManager)).open(player);
               } else if (clickedItem.getType() == Material.BARRIER) {
                  player.sendMessage(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.gui.main-gui.exited")));
                  player.closeInventory();
               }
            }

         }
      }
   }
}

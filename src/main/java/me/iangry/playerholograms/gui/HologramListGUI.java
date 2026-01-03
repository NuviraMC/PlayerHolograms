package me.iangry.playerholograms.gui;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import me.iangry.playerholograms.HologramGUI;
import me.iangry.playerholograms.hologram.HologramManager;
import me.iangry.playerholograms.utils.AnvilManager;
import me.iangry.playerholograms.utils.ColourUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class HologramListGUI implements Listener {
   private final HologramManager hologramManager;
   private final AnvilManager anvilManager;

   public HologramListGUI(HologramManager hologramManager, AnvilManager anvilManager) {
      this.hologramManager = hologramManager;
      this.anvilManager = anvilManager;
      Bukkit.getPluginManager().registerEvents(this, HologramGUI.getInstance());
   }

   public void open(Player player) {
      List<String> holograms = this.hologramManager.listHolograms(player);
      int maxHolograms = this.hologramManager.getMaxHolograms(player);
      String maxHologramsStr = maxHolograms == -1 ? "*" : String.valueOf(maxHolograms);
      String var10000 = ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.gui.listedit-gui-1.title"));
      String title = var10000 + " (" + holograms.size() + "/" + maxHologramsStr + ")";
      int rows = (int)Math.ceil((double)holograms.size() / 9.0D);
      rows = Math.max(1, rows);
      int size = rows * 9;
      Inventory inventory = Bukkit.createInventory((InventoryHolder)null, size, title);
      Iterator var9 = holograms.iterator();

      while(var9.hasNext()) {
         String hologram = (String)var9.next();
         List<String> lines = this.hologramManager.getHologramLines(hologram);
         ItemStack item = new ItemStack(Material.PAPER);
         ItemMeta meta = item.getItemMeta();
         String displayName = lines.isEmpty() ? hologram : String.join("\n", lines);
         meta.setDisplayName(ColourUtils.colorize(displayName));
         meta.setLore(List.of("", ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.gui.left-click")), ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.gui.right-click"))));
         item.setItemMeta(meta);
         item = this.setHologramName(item, hologram);
         inventory.addItem(new ItemStack[]{item});
      }

      player.openInventory(inventory);
   }

   private ItemStack setHologramName(ItemStack item, String hologramName) {
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         meta.getPersistentDataContainer().set(new NamespacedKey(HologramGUI.getInstance(), "hologramName"), PersistentDataType.STRING, hologramName);
         item.setItemMeta(meta);
      } else {
         HologramGUI.getInstance().getLogger().log(Level.SEVERE, "Failed to set hologram name: ItemMeta is null");
      }

      return item;
   }

   private String getHologramName(ItemStack item) {
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         return (String)meta.getPersistentDataContainer().get(new NamespacedKey(HologramGUI.getInstance(), "hologramName"), PersistentDataType.STRING);
      } else {
         HologramGUI.getInstance().getLogger().log(Level.SEVERE, "Failed to get hologram name: ItemMeta is null");
         return null;
      }
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      Player player = (Player)event.getWhoClicked();
      Inventory inventory = event.getClickedInventory();
      if (inventory != null && event.getView().getTitle().startsWith(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.gui.listedit-gui-1.title")))) {
         event.setCancelled(true);
         ItemStack clickedItem = event.getCurrentItem();
         if (clickedItem != null && clickedItem.getType() != Material.AIR) {
            String hologramName = this.getHologramName(clickedItem);
            if (hologramName == null) {
               player.sendMessage(String.valueOf(ChatColor.RED) + "Hologram name not found.");
            } else {
               if (event.isLeftClick()) {
                  (new HologramEditLineGUI(this.hologramManager, this.anvilManager)).open(player, hologramName);
               } else if (event.isRightClick()) {
                  this.hologramManager.deleteHologram(player, hologramName);
                  inventory.remove(clickedItem);
               }

            }
         }
      }
   }
}

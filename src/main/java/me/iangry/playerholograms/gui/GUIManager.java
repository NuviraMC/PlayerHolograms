package me.iangry.playerholograms.gui;

import java.util.List;
import me.iangry.playerholograms.HologramGUI;
import me.iangry.playerholograms.utils.ColourUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIManager {
   public static void openMainMenu(Player player) {
      Inventory gui = Bukkit.createInventory((InventoryHolder)null, 27, ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.gui.main-gui.title")));
      ItemStack createHologram = new ItemStack(Material.NAME_TAG);
      ItemMeta createMeta = createHologram.getItemMeta();
      createMeta.setDisplayName(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.gui.main-gui.create-hologram-item-name")));
      createMeta.setLore(List.of(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.gui.main-gui.create-hologram-item-lore"))));
      createHologram.setItemMeta(createMeta);
      ItemStack listeditHologram = new ItemStack(Material.PAPER);
      ItemMeta listeditMeta = listeditHologram.getItemMeta();
      listeditMeta.setDisplayName(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.gui.main-gui.listedit-hologram-item-name")));
      listeditHologram.setItemMeta(listeditMeta);
      ItemStack exit = new ItemStack(Material.BARRIER);
      ItemMeta exitMeta = exit.getItemMeta();
      exitMeta.setDisplayName(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.gui.main-gui.exit-item-name")));
      exit.setItemMeta(exitMeta);
      gui.setItem(11, createHologram);
      gui.setItem(13, listeditHologram);
      gui.setItem(15, exit);
      player.openInventory(gui);
   }
}

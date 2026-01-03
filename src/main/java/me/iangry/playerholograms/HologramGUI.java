package me.iangry.playerholograms;

import java.io.File;
import java.io.IOException;
import me.iangry.playerholograms.commands.HologramCommand;
import me.iangry.playerholograms.commands.PlayerHologramsPlugin;
import me.iangry.playerholograms.gui.GUIListener;
import me.iangry.playerholograms.gui.HologramEditLineGUI;
import me.iangry.playerholograms.gui.HologramListGUI;
import me.iangry.playerholograms.hologram.HologramManager;
import me.iangry.playerholograms.utils.AnvilManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HologramGUI extends JavaPlugin {
   private static HologramGUI instance;
   private HologramManager hologramManager;
   private AnvilManager anvilManager;
   private FileConfiguration hologramConfig;

   public void onEnable() {
      instance = this;
      this.saveDefaultConfig();
      this.updateConfig();
      this.loadHologramConfig();
      this.hologramManager = new HologramManager(this.hologramConfig);
      this.anvilManager = new AnvilManager();
      this.getCommand("hologramgui").setExecutor(new HologramCommand(this));
      this.getCommand("playerhologramsplugin").setExecutor(new PlayerHologramsPlugin(this));
      PluginManager pm = Bukkit.getPluginManager();
      pm.registerEvents(new GUIListener(this.hologramManager, this.anvilManager), this);
      pm.registerEvents(new HologramListGUI(this.hologramManager, this.anvilManager), this);
      pm.registerEvents(new HologramEditLineGUI(this.hologramManager, this.anvilManager), this);
      this.getLogger().info("PlayerHolograms | Plugin Enabled!");
   }

   public void onDisable() {
      this.hologramManager.saveHolograms();
      this.saveHologramConfig();
      this.saveConfig();
      this.getLogger().info("PlayerHolograms | Plugin Disabled!");
   }

   public static HologramGUI getInstance() {
      return instance;
   }

   public void updateConfig() {
      FileConfiguration config = this.getConfig();
      config.options().copyDefaults(true);
      this.saveConfig();
   }

   private void loadHologramConfig() {
      File hologramFile = new File(this.getDataFolder(), "holograms.yml");
      if (!hologramFile.exists()) {
         hologramFile.getParentFile().mkdirs();
         this.saveResource("holograms.yml", false);
      }

      this.hologramConfig = YamlConfiguration.loadConfiguration(hologramFile);
   }

   private void saveHologramConfig() {
      try {
         this.hologramConfig.save(new File(this.getDataFolder(), "holograms.yml"));
      } catch (IOException var2) {
         this.getLogger().severe("Could not save holograms.yml!");
         var2.printStackTrace();
      }

   }
}

package me.iangry.playerholograms.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import me.iangry.playerholograms.HologramGUI;
import me.iangry.playerholograms.utils.BentoboxChecker;
import me.iangry.playerholograms.utils.ColourUtils;
import me.iangry.playerholograms.utils.GriefPreventionChecker;
import me.iangry.playerholograms.utils.PlotsquaredChecker;
import me.iangry.playerholograms.utils.SuperiorSkyblockChecker;
import me.iangry.playerholograms.utils.TownyChecker;
import me.iangry.playerholograms.utils.WorldGuardChecker;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class HologramManager {
   private final Map<UUID, List<String>> playerHolograms = new HashMap();
   private final FileConfiguration config;

   public HologramManager(FileConfiguration config) {
      this.config = config;
      this.loadHolograms();
   }

   public void createHologram(Player player, String[] lines) {
      boolean hasBypassPermission = player.hasPermission("playerholograms.bypass");
      List<String> bannedWords = HologramGUI.getInstance().getConfig().getStringList("bannedWords");
      String[] var5 = lines;
      int var6 = lines.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String line = var5[var7];
         String strippedLine = ChatColor.stripColor(ColourUtils.colorize(line));
         Iterator var10 = bannedWords.iterator();

         while(var10.hasNext()) {
            String bannedWord = (String)var10.next();
            String regex = "(?i)\\b" + Pattern.quote(bannedWord) + "\\b";
            if (Pattern.compile(regex).matcher(strippedLine).find()) {
               player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.chat.banned-word"))));
               return;
            }
         }
      }

      if (!hasBypassPermission) {
         int maxHolograms = this.getMaxHolograms(player);
         List<String> holograms = (List)this.playerHolograms.getOrDefault(player.getUniqueId(), new ArrayList());
         if (holograms.size() >= maxHolograms && maxHolograms != -1) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.chat.max-holograms-reached"))));
            return;
         }

         Location location = player.getLocation().add(0.0D, 2.0D, 0.0D);
         if (this.isWorldGuardRestrictedLocation(location)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.chat.restricted-location"))));
            return;
         }

         if (HologramGUI.getInstance().getConfig().getBoolean("griefprevention.allow-only-in-own-claims") && !GriefPreventionChecker.isInOwnClaim(player)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.chat.restricted-claim"))));
            return;
         }

         if (HologramGUI.getInstance().getConfig().getBoolean("superiorskyblock.allow-only-in-own-island") && !SuperiorSkyblockChecker.isInOwnIsland(player)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.chat.restricted-island"))));
            return;
         }

         if (HologramGUI.getInstance().getConfig().getBoolean("towny.allow-only-in-own-town") && !TownyChecker.isInOwnTown(player)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.chat.restricted-town"))));
            return;
         }


         if (HologramGUI.getInstance().getConfig().getBoolean("bentobox.allow-only-in-own-island") && !BentoboxChecker.isInOwnIsland(player)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.chat.restricted-island"))));
            return;
         }

         if (HologramGUI.getInstance().getConfig().getBoolean("plotsquared.allow-only-in-own-plot") && !PlotsquaredChecker.isInOwnPlot(player)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.chat.restricted-plot"))));
            return;
         }
      }

      String var10000 = String.valueOf(player.getUniqueId());
      String hologramName = "holo_" + var10000 + "_" + String.valueOf(UUID.randomUUID());
      Location location = player.getLocation().add(0.0D, 2.0D, 0.0D);
      DHAPI.createHologram(hologramName, location, Arrays.asList(lines));
      List<String> holograms = (List)this.playerHolograms.getOrDefault(player.getUniqueId(), new ArrayList());
      holograms.add(hologramName);
      this.playerHolograms.put(player.getUniqueId(), holograms);
      this.saveHologramDetails(hologramName, location, lines);
      this.saveHolograms();
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.chat.hologram-created"))));
   }

   public void deleteHologram(Player player, String hologramName) {
      List<String> holograms = (List)this.playerHolograms.getOrDefault(player.getUniqueId(), new ArrayList());
      if (!holograms.contains(hologramName)) {
         player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.chat.dont-own-hologram"))));
      } else {
         DHAPI.removeHologram(hologramName);
         holograms.remove(hologramName);
         this.config.set("hologramDetails." + hologramName, (Object)null);
         this.saveHolograms();
         player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.chat.hologram-deleted"))));
      }
   }

   public void updateHologramLine(Player player, String hologramName, int lineIndex, String newText) {
      List<String> holograms = (List)this.playerHolograms.getOrDefault(player.getUniqueId(), new ArrayList());
      if (!holograms.contains(hologramName)) {
         player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.chat.dont-own-hologram"))));
      } else {
         String strippedText = ChatColor.stripColor(ColourUtils.colorize(newText));
         List<String> bannedWords = HologramGUI.getInstance().getConfig().getStringList("bannedWords");
         Iterator var8 = bannedWords.iterator();

         while(var8.hasNext()) {
            String bannedWord = (String)var8.next();
            String regex = "(?i)\\b" + Pattern.quote(bannedWord) + "\\b";
            if (Pattern.compile(regex).matcher(strippedText).find()) {
               player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.chat.banned-word"))));
               return;
            }
         }

         List<String> lines = this.getHologramLines(hologramName);
         if (lineIndex < lines.size()) {
            lines.set(lineIndex, newText);
         } else {
            for(int i = lines.size(); i <= lineIndex; ++i) {
               lines.add("");
            }

            lines.set(lineIndex, newText);
         }

         Location location = this.deserializeLocation(this.config.getString("hologramDetails." + hologramName + ".location"));
         DHAPI.removeHologram(hologramName);
         DHAPI.createHologram(hologramName, location, lines);
         this.saveHologramDetails(hologramName, location, (String[])lines.toArray(new String[0]));
         this.saveHolograms();
         player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.chat.hologram-updated"))));
      }
   }

   public List<String> listHolograms(Player player) {
      List<String> holograms = (List)this.playerHolograms.getOrDefault(player.getUniqueId(), new ArrayList());
      if (holograms.isEmpty()) {
         player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.chat.no-holograms"))));
         return holograms;
      } else {
         player.sendMessage(ColourUtils.colorize(HologramGUI.getInstance().getConfig().getString("messages.chat.your-holograms")));
         Iterator var3 = holograms.iterator();

         while(var3.hasNext()) {
            String hologram = (String)var3.next();
            List<String> lines = this.getHologramLines(hologram);
            String joinedLines = String.join("\n", lines);
            player.sendMessage(ColourUtils.colorize("&b - " + joinedLines + "&b"));
         }

         return holograms;
      }
   }

   private boolean isWorldGuardRestrictedLocation(Location location) {
      List<String> restrictedRegions = HologramGUI.getInstance().getConfig().getStringList("worldguard.restrictedRegions");
      return Bukkit.getPluginManager().getPlugin("WorldGuard") != null && WorldGuardChecker.isRestricted(location, restrictedRegions);
   }

   public void saveHolograms() {
      Iterator var1 = this.playerHolograms.keySet().iterator();

      while(var1.hasNext()) {
         UUID uuid = (UUID)var1.next();
         this.config.set("holograms." + uuid.toString(), this.playerHolograms.get(uuid));
      }

      try {
         this.config.save(new File(HologramGUI.getInstance().getDataFolder(), "holograms.yml"));
      } catch (IOException var3) {
         HologramGUI.getInstance().getLogger().severe("Could not save holograms.yml!");
         var3.printStackTrace();
      }

   }

   private void loadHolograms() {
      if (this.config.contains("holograms")) {
         Iterator var1 = this.config.getConfigurationSection("holograms").getKeys(false).iterator();

         while(var1.hasNext()) {
            String uuidString = (String)var1.next();
            UUID uuid = UUID.fromString(uuidString);
            List<String> holograms = this.config.getStringList("holograms." + uuidString);
            this.playerHolograms.put(uuid, holograms);
            Iterator var5 = holograms.iterator();

            while(var5.hasNext()) {
               String hologramName = (String)var5.next();
               String locationString = this.config.getString("hologramDetails." + hologramName + ".location");
               Location location = this.deserializeLocation(locationString);
               List<String> lines = this.config.getStringList("hologramDetails." + hologramName + ".lines");
               DHAPI.createHologram(hologramName, location, lines);
            }
         }
      }

   }

   private void saveHologramDetails(String hologramName, Location location, String[] lines) {
      this.config.set("hologramDetails." + hologramName + ".location", this.serializeLocation(location));
      this.config.set("hologramDetails." + hologramName + ".lines", Arrays.asList(lines));
   }

   private Location deserializeLocation(String locationString) {
      String[] parts = locationString.split(",");
      World world = Bukkit.getWorld(parts[0]);
      double x = Double.parseDouble(parts[1]);
      double y = Double.parseDouble(parts[2]);
      double z = Double.parseDouble(parts[3]);
      float yaw = Float.parseFloat(parts[4]);
      float pitch = Float.parseFloat(parts[5]);
      return new Location(world, x, y, z, yaw, pitch);
   }

   private String serializeLocation(Location location) {
      String var10000 = location.getWorld().getName();
      return var10000 + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
   }

   public List<String> getHologramLines(String hologramName) {
      return this.config.getStringList("hologramDetails." + hologramName + ".lines");
   }

   public int getMaxHolograms(Player player) {
      for(int i = 100; i > 0; --i) {
         if (player.hasPermission("playerholograms.use." + i)) {
            return i;
         }
      }

      if (player.hasPermission("playerholograms.use.*")) {
         return -1;
      } else {
         return 0;
      }
   }
}

package me.iangry.playerholograms.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldGuardChecker {
   public static boolean isRestricted(Location location, List<String> restrictedRegions) {
      if (location != null && restrictedRegions != null && !restrictedRegions.isEmpty()) {
         RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
         World bukkitWorld = location.getWorld();
         if (bukkitWorld == null) {
            return false;
         } else {
            RegionManager regionManager = container.get(BukkitAdapter.adapt(bukkitWorld));
            if (regionManager == null) {
               return false;
            } else {
               ApplicableRegionSet applicableRegions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
               Iterator var6 = applicableRegions.iterator();

               ProtectedRegion region;
               do {
                  if (!var6.hasNext()) {
                     return false;
                  }

                  region = (ProtectedRegion)var6.next();
               } while(!restrictedRegions.contains(region.getId()));

               return true;
            }
         }
      } else {
         return false;
      }
   }
}

package com.minebunch.core.utils.player;

import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;

@UtilityClass
public class PlayerUtil {
    public void clearPlayer(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        Inventory craftingInventory = entityPlayer.activeContainer.getBukkitView().getTopInventory();

        if (craftingInventory instanceof CraftingInventory) {
            craftingInventory.clear();
        }

        player.setHealth(player.getMaxHealth());
        player.setMaximumNoDamageTicks(20);
        player.setFallDistance(0.0F);
        player.setFoodLevel(20);
        player.setSaturation(5.0F);
        player.setFireTicks(0);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setItemOnCursor(null);
        player.updateInventory();
    }

    public int getPing(Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }

    public List<Player> getNearbyPlayers(Entity entity, double radius) {
        return entity.getNearbyEntities(radius, radius, radius).stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .collect(Collectors.toList());
    }
}

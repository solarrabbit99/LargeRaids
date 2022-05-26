package com.solarrabbit.largeraids.raid.mob.manager;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.raid.mob.FireworkPillager;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class FireworkPillagerManager implements CustomRaiderManager, Listener {
    private static final float DEFAULT_MAX_HEALTH = 48.0f;

    @Override
    public FireworkPillager spawn(Location location) {
        Pillager entity = (Pillager) location.getWorld().spawnEntity(location, EntityType.PILLAGER);
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(DEFAULT_MAX_HEALTH);
        entity.setHealth(DEFAULT_MAX_HEALTH);
        EntityEquipment equipment = entity.getEquipment();
        equipment.setItemInOffHand(getDefaultFirework());
        equipment.setHelmet(getDefaultBanner());
        equipment.setHelmetDropChance(1.0f);
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        pdc.set(getPillagerNamespacedKey(), PersistentDataType.BYTE, (byte) 0);
        entity.setCustomName("Firework Pillager");
        return new FireworkPillager(entity);
    }

    @EventHandler
    private void onBowShoot(EntityShootBowEvent evt) {
        if (evt.getEntityType() != EntityType.PILLAGER)
            return;
        Pillager pillager = (Pillager) evt.getEntity();
        if (isFireworkPillager(pillager)) {
            pillager.getEquipment().setItemInOffHand(getDefaultFirework());
            evt.getProjectile().getPersistentDataContainer().set(getFireworkNamespacedKey(), PersistentDataType.BYTE,
                    (byte) 0);
        }
    }

    @EventHandler
    private void onDamageRaider(EntityDamageByEntityEvent evt) {
        if (evt.getCause() != DamageCause.ENTITY_EXPLOSION)
            return;
        if (evt.getDamager().getType() != EntityType.FIREWORK)
            return;
        if (!(evt.getEntity() instanceof Raider))
            return;
        Firework firework = (Firework) evt.getDamager();
        if (isPillagerFirework(firework))
            evt.setCancelled(true);
    }

    private ItemStack getDefaultFirework() {
        ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
        FireworkMeta meta = (FireworkMeta) firework.getItemMeta();
        FireworkEffect effect = FireworkEffect.builder().with(Type.BALL_LARGE)
                .withColor(Color.AQUA, Color.ORANGE, Color.FUCHSIA).flicker(true).build();
        meta.addEffects(effect, effect, effect, effect, effect);
        firework.setItemMeta(meta);
        return firework;
    }

    private ItemStack getDefaultBanner() {
        ItemStack banner = new ItemStack(Material.YELLOW_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.MAGENTA, PatternType.STRIPE_CENTER));
        meta.addPattern(new Pattern(DyeColor.ORANGE, PatternType.CURLY_BORDER));
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.STRIPE_SMALL));
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.RHOMBUS_MIDDLE));
        meta.addPattern(new Pattern(DyeColor.YELLOW, PatternType.FLOWER));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.setDisplayName(ChatColor.GOLD.toString() + ChatColor.ITALIC + "Firework Pillager Banner");
        banner.setItemMeta(meta);
        return banner;
    }

    private boolean isFireworkPillager(Pillager pillager) {
        PersistentDataContainer pdc = pillager.getPersistentDataContainer();
        return pdc.has(getPillagerNamespacedKey(), PersistentDataType.BYTE);
    }

    private boolean isPillagerFirework(Firework firework) {
        PersistentDataContainer pdc = firework.getPersistentDataContainer();
        return pdc.has(getFireworkNamespacedKey(), PersistentDataType.BYTE);
    }

    private NamespacedKey getPillagerNamespacedKey() {
        return new NamespacedKey(JavaPlugin.getPlugin(LargeRaids.class), "firework_pillager");
    }

    private NamespacedKey getFireworkNamespacedKey() {
        return new NamespacedKey(JavaPlugin.getPlugin(LargeRaids.class), "pillager_firework");
    }

}

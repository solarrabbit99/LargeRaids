package com.solarrabbit.largeraids.raid.mob;

import com.solarrabbit.largeraids.LargeRaids;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Ravager;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.Spellcaster.Spell;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpellCastEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Juggernaut implements EventBoss, RaiderRider, Listener {
    private static final double RAVAGER_MAX_HEALTH = 300;
    private static final double RAVAGER_ATTACK_DAMAGE = 48;
    private static final int WEAKNESS_TICK = 20 * 20;
    private static final int INVISIBILITY_TICK = 20 * 60;
    private static final EntityType RIDER_TYPE = EntityType.EVOKER;
    private Raider rider;

    @Override
    public Raider spawn(Location location) {
        Ravager ravager = (Ravager) location.getWorld().spawnEntity(location, EntityType.RAVAGER);
        ravager.setCustomName("Juggernaut");
        ravager.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(RAVAGER_MAX_HEALTH);
        ravager.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(RAVAGER_ATTACK_DAMAGE);
        ravager.setHealth(RAVAGER_MAX_HEALTH);
        ravager.getPersistentDataContainer().set(getJuggernautNamespacedKey(), PersistentDataType.BYTE, (byte) 0);
        ravager.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 4));
        createBossBar(ravager);

        Spellcaster rider = (Spellcaster) location.getWorld().spawnEntity(location, RIDER_TYPE);
        EntityEquipment equipment = rider.getEquipment();
        equipment.setItemInMainHand(null);
        equipment.setHelmet(getDefaultBanner());
        equipment.setHelmetDropChance(1.0f);
        rider.getPersistentDataContainer().set(getJuggernautKingNamespacedKey(), PersistentDataType.BYTE, (byte) 0);

        ravager.addPassenger(rider);
        this.rider = rider;
        return ravager;
    }

    @Override
    public Raider getRider() {
        return rider;
    }

    @EventHandler
    private void onSpellcast(EntitySpellCastEvent evt) {
        if (evt.getEntityType() != RIDER_TYPE)
            return;
        Spellcaster caster = (Spellcaster) evt.getEntity();
        if (!isJuggernautKing(caster))
            return;
        switch (evt.getSpell()) {
            case SUMMON_VEX:
                caster.getTarget().addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, WEAKNESS_TICK, 1));
                break;
            case FANGS:
                Entity vehicle = caster.getVehicle();
                if (vehicle instanceof Ravager && isJuggernaut((Ravager) vehicle))
                    ((Ravager) vehicle).addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 1));
                else
                    caster.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, INVISIBILITY_TICK, 0));
                break;
            default:
                break;
        }
        evt.setCancelled(true);
        caster.setSpell(Spell.NONE);
    }

    @EventHandler
    private void onKingDamage(EntityDamageEvent evt) {
        if (evt.getEntityType() != RIDER_TYPE)
            return;
        Spellcaster king = (Spellcaster) evt.getEntity();
        Entity vehicle = king.getVehicle();
        // Kings riding juggernauts are invulnerable
        if (isJuggernautKing(king) && vehicle instanceof Ravager && isJuggernaut((Ravager) vehicle))
            evt.setCancelled(true);
    }

    private ItemStack getDefaultBanner() {
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.CYAN, PatternType.RHOMBUS_MIDDLE));
        meta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.STRIPE_BOTTOM));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.HALF_HORIZONTAL));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE));
        meta.addPattern(new Pattern(DyeColor.GRAY, PatternType.STRIPE_CENTER));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.SKULL));
        meta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.CIRCLE_MIDDLE));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLE_TOP));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.setDisplayName(ChatColor.GOLD.toString() + ChatColor.ITALIC + "King Raider Banner");
        banner.setItemMeta(meta);
        return banner;
    }

    // private ItemStack getDefaultBanner() {
    // ItemStack banner = new ItemStack(Material.RED_BANNER);
    // BannerMeta meta = (BannerMeta) banner.getItemMeta();
    // meta.addPattern(new Pattern(DyeColor.YELLOW, PatternType.RHOMBUS_MIDDLE));
    // meta.addPattern(new Pattern(DyeColor.RED, PatternType.CREEPER));
    // meta.addPattern(new Pattern(DyeColor.YELLOW, PatternType.STRIPE_CENTER));
    // meta.addPattern(new Pattern(DyeColor.RED, PatternType.STRIPE_TOP));
    // meta.addPattern(new Pattern(DyeColor.RED, PatternType.STRIPE_BOTTOM));
    // meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.GRADIENT_UP));
    // meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.CURLY_BORDER));
    // meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_TOP));
    // meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
    // meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
    // meta.setDisplayName(ChatColor.GOLD.toString() + ChatColor.ITALIC +
    // "Juggernaut King Banner");
    // banner.setItemMeta(meta);
    // return banner;
    // }

    private boolean isJuggernaut(Ravager entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        return pdc.has(getJuggernautNamespacedKey(), PersistentDataType.BYTE);
    }

    private boolean isJuggernautKing(Raider entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        return pdc.has(getJuggernautKingNamespacedKey(), PersistentDataType.BYTE);
    }

    private NamespacedKey getJuggernautNamespacedKey() {
        return new NamespacedKey(JavaPlugin.getPlugin(LargeRaids.class), "juggernaut");
    }

    private NamespacedKey getJuggernautKingNamespacedKey() {
        return new NamespacedKey(JavaPlugin.getPlugin(LargeRaids.class), "juggernaut_king");
    }

}

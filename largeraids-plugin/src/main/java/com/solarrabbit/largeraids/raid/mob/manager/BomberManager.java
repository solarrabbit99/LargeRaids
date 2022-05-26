package com.solarrabbit.largeraids.raid.mob.manager;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.raid.mob.Bomber;
import com.solarrabbit.largeraids.util.VersionUtil;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Vex;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class BomberManager implements CustomRaiderManager, Listener {
    private static final float BOMBER_EXPLOSIVE_POWER = 1.0f;
    private static final float TNT_EXPLOSIVE_POWER = 1.5f;
    private static final int PRIMED_TNT_TICKS = 20;

    @Override
    public Bomber spawn(Location location) {
        Spellcaster evoker = (Spellcaster) location.getWorld().spawnEntity(location, EntityType.EVOKER);
        EntityEquipment equipment = evoker.getEquipment();
        equipment.setHelmet(getDefaultBanner());
        equipment.setHelmetDropChance(1.0f);
        evoker.getPersistentDataContainer().set(getBomberNamespacedKey(), PersistentDataType.BYTE, (byte) 0);
        evoker.setCustomName("Bomber");
        return new Bomber(evoker);
    }

    @EventHandler
    private void onFangSpawn(EntitySpawnEvent evt) {
        if (evt.getEntityType() != EntityType.EVOKER_FANGS)
            return;
        EvokerFangs fangs = (EvokerFangs) evt.getEntity();
        LivingEntity owner = fangs.getOwner();
        if (owner instanceof Spellcaster && isBomber((Spellcaster) owner)) {
            evt.setCancelled(true);
            fangs.getWorld().createExplosion(fangs.getLocation(), BOMBER_EXPLOSIVE_POWER, false, false, owner);
        }
    }

    @EventHandler
    private void onDamageRaider(EntityDamageByEntityEvent evt) {
        if (evt.getCause() != DamageCause.ENTITY_EXPLOSION)
            return;
        if (evt.getDamager().getType() != EntityType.EVOKER)
            return;
        if (!(evt.getEntity() instanceof Raider))
            return;
        Spellcaster evoker = (Spellcaster) evt.getDamager();
        if (isBomber(evoker))
            evt.setCancelled(true);
    }

    @EventHandler
    private void onSummonVex(CreatureSpawnEvent evt) {
        if (evt.getEntityType() != EntityType.VEX)
            return;
        Vex vex = (Vex) evt.getEntity();
        LivingEntity owner = VersionUtil.getCraftVexWrapper(vex).getOwner();
        if (!(owner instanceof Spellcaster))
            return;
        if (isBomber((Spellcaster) owner)) {
            vex.getPersistentDataContainer().set(getVexNamespacedKey(), PersistentDataType.BYTE, (byte) 0);
            vex.getEquipment().setItemInMainHand(new ItemStack(Material.TNT));
            vex.setCustomName("Bomber Vex");
        }
    }

    @EventHandler
    private void onVexAttack(EntityDamageByEntityEvent evt) {
        if (evt.getDamager().getType() != EntityType.VEX)
            return;
        Vex damager = (Vex) evt.getDamager();
        if (!isBomberVex(damager))
            return;
        evt.setCancelled(true);
        TNTPrimed tnt = (TNTPrimed) damager.getWorld().spawnEntity(damager.getLocation(), EntityType.PRIMED_TNT);
        tnt.setFuseTicks(PRIMED_TNT_TICKS);
        tnt.getPersistentDataContainer().set(getTNTNamespacedKey(), PersistentDataType.BYTE, (byte) 0);
    }

    @EventHandler
    private void onTNTDetonate(EntityExplodeEvent evt) {
        if (evt.getEntityType() != EntityType.PRIMED_TNT)
            return;
        TNTPrimed tnt = (TNTPrimed) evt.getEntity();
        if (!isVexTNT(tnt))
            return;
        evt.setCancelled(true);
        tnt.getWorld().createExplosion(tnt.getLocation(), TNT_EXPLOSIVE_POWER, false, false);
    }

    @EventHandler
    private void onTNTDamage(EntityDamageByEntityEvent evt) {
        if (evt.getDamager().getType() != EntityType.PRIMED_TNT)
            return;
        TNTPrimed tnt = (TNTPrimed) evt.getDamager();
        if (!(isVexTNT(tnt)))
            return;
        evt.setCancelled(true);
    }

    private ItemStack getDefaultBanner() {
        ItemStack banner = new ItemStack(Material.LIGHT_GRAY_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.ORANGE, PatternType.RHOMBUS_MIDDLE));
        meta.addPattern(new Pattern(DyeColor.YELLOW, PatternType.FLOWER));
        meta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.DIAGONAL_LEFT_MIRROR));
        meta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.DIAGONAL_RIGHT));
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.GRADIENT_UP));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.CIRCLE_MIDDLE));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.setDisplayName(ChatColor.GOLD.toString() + ChatColor.ITALIC + "Bomber Banner");
        banner.setItemMeta(meta);
        return banner;
    }

    private boolean isBomber(Spellcaster entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        return pdc.has(getBomberNamespacedKey(), PersistentDataType.BYTE);
    }

    private boolean isBomberVex(Vex entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        return pdc.has(getVexNamespacedKey(), PersistentDataType.BYTE);
    }

    private boolean isVexTNT(TNTPrimed entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        return pdc.has(getTNTNamespacedKey(), PersistentDataType.BYTE);
    }

    private NamespacedKey getBomberNamespacedKey() {
        return new NamespacedKey(JavaPlugin.getPlugin(LargeRaids.class), "bomber");
    }

    private NamespacedKey getVexNamespacedKey() {
        return new NamespacedKey(JavaPlugin.getPlugin(LargeRaids.class), "bomber_vex");
    }

    private NamespacedKey getTNTNamespacedKey() {
        return new NamespacedKey(JavaPlugin.getPlugin(LargeRaids.class), "bomber_vex_tnt");
    }

}

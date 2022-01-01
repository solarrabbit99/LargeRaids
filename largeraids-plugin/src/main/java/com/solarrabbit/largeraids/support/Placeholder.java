package com.solarrabbit.largeraids.support;

import java.util.Optional;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.raid.LargeRaid;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class Placeholder extends PlaceholderExpansion {
    private final LargeRaids plugin;

    public Placeholder(LargeRaids plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "SolarRabbit";
    }

    @Override
    public String getIdentifier() {
        return "largeraids";
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        Optional<LargeRaid> raid = Optional.ofNullable(player.getPlayer()).map(Player::getLocation)
                .flatMap(loc -> plugin.getRaidManager().getLargeRaid(loc));
        switch (params) {
            case "in_range":
                return String.valueOf(raid.isPresent());
            case "wave":
                return raid.map(LargeRaid::getCurrentWave).map(Object::toString).orElse(null);
            case "total_waves":
                return raid.map(LargeRaid::getTotalWaves).map(Object::toString).orElse(null);
            case "remaining_raiders":
                return raid.map(LargeRaid::getTotalRaidersAlive).map(Object::toString).orElse(null);
            case "omen_level":
                return raid.map(LargeRaid::getBadOmenLevel).map(Object::toString).orElse(null);
            case "player_kills":
                if (!raid.isPresent())
                    return null;
                else
                    return raid.map(LargeRaid::getPlayerKills).map(map -> map.get(player.getUniqueId()))
                            .map(Object::toString).orElse("0");
            case "debug_total_registered":
                return String.valueOf(plugin.getRaidManager().getNumOfRegisteredRaids());
            default:
                return null;
        }
    }
}

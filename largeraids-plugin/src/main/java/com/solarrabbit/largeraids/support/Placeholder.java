package com.solarrabbit.largeraids.support;

import java.util.Optional;

import com.solarrabbit.largeraids.config.PlaceholderConfig;
import com.solarrabbit.largeraids.raid.LargeRaid;
import com.solarrabbit.largeraids.raid.RaidManager;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class Placeholder extends PlaceholderExpansion {
    private final RaidManager manager;
    private final PlaceholderConfig conf;

    public Placeholder(RaidManager manager, PlaceholderConfig conf) {
        this.manager = manager;
        this.conf = conf;
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
                .flatMap(loc -> manager.getLargeRaid(loc));
        String replacement = conf.getNotInRangeString();
        switch (params) {
            case "in_range":
                return String.valueOf(raid.isPresent());
            case "wave":
                return raid.map(LargeRaid::getCurrentWave).map(Object::toString).orElse(replacement);
            case "total_waves":
                return raid.map(LargeRaid::getTotalWaves).map(Object::toString).orElse(replacement);
            case "remaining_raiders":
                return raid.map(LargeRaid::getTotalRaidersAlive).map(Object::toString).orElse(replacement);
            case "omen_level":
                return raid.map(LargeRaid::getBadOmenLevel).map(Object::toString).orElse(replacement);
            case "player_kills":
                if (!raid.isPresent())
                    return replacement;
                else
                    return raid.map(LargeRaid::getPlayerKills).map(map -> map.get(player.getUniqueId()))
                            .map(Object::toString).orElse("0");
            case "debug_total_registered":
                return String.valueOf(manager.getNumOfRegisteredRaids());
            default:
                return null;
        }
    }
}

package com.solarrabbit.largeraids.support;

import java.util.Optional;
import com.solarrabbit.largeraids.listener.RaidListener;
import com.solarrabbit.largeraids.raid.LargeRaid;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class Placeholder extends PlaceholderExpansion {

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
                .flatMap(loc -> RaidListener.matchingLargeRaid(loc));
        switch (params) {
            case "in_range":
                return String.valueOf(raid.isPresent());
            case "wave":
                return raid.map(r -> r.getCurrentWave()).map(Object::toString).orElse(null);
            case "total_waves":
                return raid.map(r -> r.getTotalWaves()).map(Object::toString).orElse(null);
            case "remaining_raiders":
                return raid.map(r -> r.getTotalRaidersAlive()).map(Object::toString).orElse(null);
            case "omen_level":
                return raid.map(r -> r.getBadOmenLevel()).map(Object::toString).orElse(null);
            default:
                return null;
        }
    }
}

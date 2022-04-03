---
layout: page
title: User Guide
---

## Configurations

Refer to the [Configuration Guide](configurations.html).

## Commands

- `/lrstart [player | center] [name]` - start a large raid at player location or given center
- `/lrskip` - skip the current wave if the large raid isn't in its last wave
- `/lrstop [player]` - stop a large raid in the vicinity (or the player's vicinity)
- `/lrcenters [add | remove | show | hide] [name]` - manage artificial villages
- `/lrgive <player> [amount]` - give player summoning items
- `/lrreload` - reload plugin configurations
- `/lrglow` - outline all raiders in the current raid

## Placeholders

> Note: You will require [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) to use these placeholders!

- `%largeraids_in_range%` - **true** if player is in vicinity of a large raid, **false** otherwise
- `%largeraids_wave%` - current wave number
- `%largeraids_total_waves%` - number of waves in total
- `%largeraids_remaining_raiders%` - number of remaining raiders
- `%largeraids_omen_level%` - accumulative bad omen level
- `%largeraids_player_kills%` - raiders killed by player

## Permissions

- `largeraids.*` - Allow access to all LargeRaids commands.
- `largeraids.reload` - Allow access to `lrreload` command.
- `largeraids.start` - Allow access to `lrstart` command.
- `largeraids.skip` - Allow access to `lrskip` command.
- `largeraids.stop` - Allow access to `lrskip` command.
- `largeraids.give` - Allow access to `lrgive` command.
- `largeraids.centers` - Allow access to `lrcenters` command.
- `largeraids.glow` - Allow access to `lrglow` command.

## Limitations

- **Mobs** - raiders are limited to entity type raiders

## FAQ

**1. I have Lands installed. Why ain't raids triggering?**

Disable the `land.combat.block-raids` option in Lands' configurations to use LargeRaids, as it triggers raids with fake players.

**2. Why does the boss bar decrease in health as soon as the wave spawns?**

The spawned waves suffered entity cramming. The only way around this is to increase the entity cramming limit for that world.

**3. Why does my raid farm stop working?**

The plugin is designed to prevent farms. Waves will not spawn if the designated spawn location is out of the 96 block radius from the raid center.

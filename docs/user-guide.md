---
layout: post
title: "User Guide"
---

## Configurations

Refer to the [Example Configuration](configurations.html).

## Commands

- `/lrstart [player]` - start a large raid for the village you are in (or the player's in)
- `/lrstop [player]` - stop a large raid in the vicinity (or the player's vicinity)
- `/lrcenters [add | remove] [name]` - manage artificial villages\*
- `/lrgive <player> [amount]` - give player summoning items
- `/lrreload` - reload plugin config

\* _The command requires villagers spawning to be enabled, villagers will despawn within 5 seconds. The command also has a chance of failing, re-run the command if the center isn't registered._

## Placeholders

> Note: You will require [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) to use these placeholders!

- `%largeraids_in_range%` - **true** if player is in vicinity of a large raid, **false** otherwise
- `%largeraids_wave%` - current wave number
- `%largeraids_total_waves%` - number of waves in total
- `%largeraids_remaining_raiders%` - number of remaining raiders
- `%largeraids_omen_level%` - accumulative bad omen level

## Permissions

- `largeraids.admin` - user able to perform plugin commands

## Limitations

- Mobs - raiders are limited to vanilla raiders, MythicMobs or other mobs plugins are not yet supported
- Weapons - whatever weapons used by raiders are not configurable

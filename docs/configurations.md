---
layout: page
title: Configurations
---

- [**Main Configurations**](#main-configurations)
  - [Raid (Mobs/Waves/Sounds)](#raid-mobswavessounds)
  - [MythicMobs](#mythicmobs)
  - [Rewards](#rewards)
- [**Trigger Mechanism**](#trigger-mechanism)
- [**Placeholder**](#placeholder)
- [**Miscellaneous**](#miscellaneous)
- [**Summary**](#summary)

The only configuration file available is the default `config.yml` generated the first time the plugin is loaded on the server. In the future versions to come, new configurable options may be added. The already generated `config.yml` does not get automatically updated. You can either backup the old configuration file in a different directory for the plugin to regenerate a new one, or update it manually according to the changes reflected below.

## Main Configurations

### Raid (Mobs/Waves/Sounds)

```yml
raid:
  # The number of waves must be at least 5. The length of the arrays under the `mobs`
  # configuration section below must also be at least the number of waves. Each wave
  # must have at least one raider.
  waves: 20
  # To disable sounds, leave the sound fields blank.
  sounds:
    summon: ITEM_TRIDENT_THUNDER
    victory: ENTITY_ENDER_DRAGON_DEATH
    defeat: ENTITY_ENDER_DRAGON_DEATH
  announce-waves:
    title:
      enabled: true
      default: "&6Wave %s"
      final: "&6Final Wave"
    message:
      enabled: false
      default: "&6Spawning wave %s..."
      final: "&6Spawning final wave..."
  # Do not add any other mobs that are not of any type listed below, they will not spawn.
  mobs:
    pillager: [5, 7, 7, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10]
    vindicator: [0, 3, 5, 5, 5, 5, 5, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 10]
    ravager: [0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3]
    witch: [0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3]
    evoker: [0, 0, 0, 0, 1, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4]
    illusioner: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 2, 3, 3, 3]
```

### MythicMobs

You can also add [**MythicMobs**](https://www.spigotmc.org/resources/%E2%9A%94-mythicmobs-free-version-%E2%96%BAthe-1-custom-mob-creator%E2%97%84.5702/) into your raids! Suppose you have the following mob configuration in the MythicMobs plugin.

```yml
SpeedRaider:
  Type: PILLAGER
  Display: "&a Speed Raider"
  Health: 50
  Damage: 2
  Options:
    MovementSpeed: 2
```

Then it can be added into the raids like so.

> 🚨 **IMPORTANT**: MythicMobs are case-sensitive. Make sure that the mobs listed are primarily of entity type Raider (any of the six original types listed).

```yml
mobs:
  pillager: [...]
  ...
  SpeedRaider: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 2, 3]
```

### Rewards

Similar to the Hero of the Village status effect, rewards are given to players, who **dealt damage** to any participating raiders, at the end of the raid assuming that the players emerge victorious. Rewards are optional. Players who do not have enough inventory space upon receiving rewards will have their share spawn on the floor instead.

```yml
rewards:
  message: "&aReceiving rewards..."
  # Minimum raiders a player must kill to obtain rewards.
  min-raider-kills: 1
  # Minimum damage a player must deal to raiders to obtain rewards.
  min-damage-deal: 0.0
  hero-of-the-village:
    # Level 12 is enough to grant a player a cost of 1 emerald for any default trades.
    # Players will receive the hero of the village effect with level of this value, or
    # the omen level of the large raid, whichever is lower.
    level: 12
    # In minutes.
    duration: 40
  # Leave this section blank to disable the feature.
  items:
    1:
      material: DIAMOND
      # Amount must be more than 0 but no more than the maximum stack size of the item
      amount: 1
      display-name: "&6Hero Diamond"
      lore:
        - "&5Thank you for saving the village!"
      custom-model-data:
      enchantments:
        1:
          type: MENDING
          level: 1
        2:
          type: DURABILITY
          level: 1
  # Leave this section blank to disable the feature. The plugin provides `<player>` as a
  # placeholder for the console to execute a command with the target player's name. Note
  # that every command listed here will be executed once per player.
  commands:
    # - lrgive <player> 1
    # - give <player> stick
```

## Trigger Mechanism

We present the following triggering mechanisms. It's advised that you only enable **one** of them at any point in time although they should technically work with each other.

```yml
trigger:
  # Players get bad omen of level higher than 5 if enabled. Raids will all be large
  # raids, number of waves will be 5 or the accumulative omen level of all players
  # entering the village, whichever is higher. Maximum number of waves will be the
  # number of waves mentioned above in `raid.waves`.
  omen:
    enabled: true
    max-level: 10
  # Dropping summoning items in lava will trigger a large raid if in vicinity of a
  # village.
  drop-item-in-lava:
    enabled: false
    # Note: The summoning item stated here will not lose its primary functionality. For
    # example, totems will still be consumed and revive the player holding it.
    item:
      # Totem by default if the input material is invalid/left blank. Instead of totems,
      # you can use rarer items for summoning (e.g NETHER_STAR).
      material: TOTEM_OF_UNDYING
      display-name: "&6Large Raid Summoner"
      lore:
        # - "&5Drop the item into lava in a village"
        # - "&5to summon a large raid!"
      custom-model-data:
      # Avoid using armor/tools for material if this is enabled. It applies mending to
      # the item for the glint.
      enchantment-glint: true
  # Raids will trigger for players in villages at the given ticks that are <192000. See Minecraft's
  # daylight cycle for more information.
  time-bomb:
    enabled: false
    ticks:
      - 114000

  # If enabled, normal raids can be triggered upon the absorption of player's bad omen.
  enable-normal-raids: false

  artificial-only:
    # If enabled, large raids can only be triggered for artificial village centers registered
    # by the plugin. Make sure that your artificial village centers are at least 128 blocks/
    # 8 chunks away from each other and other villager-claimed village blocks (e.g. job block,
    # bed, bell).
    enabled: false
    # Broadcast message to be sent to entire server, leave it blank to disable.
    broadcast-message: "&7<player> &btriggered a large raid at &l<center>&r&b!"
```

## Placeholder

Server administrators may wish to use the plugin's placeholders for scoreboards, but the plugin returns a `null` value for most placeholders of players who are not in range of any large raids by default. This section allows you to configure a replacement string for the `null` value.

```yml
placeholder:
  not-in-range-string: Not In Range
```

## Miscellaneous

```yml
miscellaneous:
  # Whether ringing of bell will outline every single raider in the raid, regardless of distance
  # from the bell. This will only work on players (not villagers) ringing the bell.
  bell-outline-raiders:
    normal-raid: false
    large-raid: false
    # Duration of glow (in seconds), put 0 to use default effect duration
    duration: 5
```

## Summary

This is a summary of what the full document should look like with proper indentation.

```yml
raid:
  waves: 20
  sounds:
    summon: ITEM_TRIDENT_THUNDER
    victory: ENTITY_ENDER_DRAGON_DEATH
    defeat: ENTITY_ENDER_DRAGON_DEATH
  announce-waves:
    title:
      enabled: true
      default: "&6Wave %s"
      final: "&6Final Wave"
    message:
      enabled: false
      default: "&6Spawning wave %s..."
      final: "&6Spawning final wave..."
  mobs:
    pillager: [5, 7, 7, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10]
    vindicator: [0, 3, 5, 5, 5, 5, 5, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 10]
    ravager: [0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3]
    witch: [0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3]
    evoker: [0, 0, 0, 0, 1, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4]
    illusioner: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 2, 3, 3, 3]

rewards:
  message: "&aReceiving rewards..."
  min-raider-kills: 1
  min-damage-deal: 0.0
  hero-of-the-village:
    level: 12
    duration: 40
  items:
    1:
      material: DIAMOND
      amount: 1
      display-name: "&6Hero Diamond"
      lore:
        - "&5Thank you for saving the village!"
  commands:
    # - lrgive <player> 1

trigger:
  omen:
    enabled: true
    max-level: 10
  drop-item-in-lava:
    enabled: false
    item:
      material: TOTEM_OF_UNDYING
      display-name: "&6Large Raid Summoner"
      lore:
        - "&5Drop the item into lava in a village"
        - "&5to summon a large raid!"
      custom-model-data:
      enchantment-glint: true
  time-bomb:
    enabled: false
    ticks:
      - 114000

  enable-normal-raids: false
  artificial-only:
    enabled: false
    broadcast-message: "&7<player> &btriggered a large raid at &l<center>&r&b!"

placeholder:
  not-in-range-string: Not In Range

miscellaneous:
  bell-outline-raiders:
    normal-raid: false
    large-raid: false
    duration: 5
```

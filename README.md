# LargeRaids

[![](https://jitpack.io/v/zhenghanlee/LargeRaids-API.svg)](https://github.com/zhenghanlee/LargeRaids-API)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/e2b8ef0d41e3404b91a62a35196c7e9e)](https://www.codacy.com/gh/zhenghanlee/LargeRaids/dashboard?utm_source=github.com&utm_medium=referral&utm_content=zhenghanlee/LargeRaids&utm_campaign=Badge_Grade)
[![License](https://img.shields.io/github/license/zhenghanlee/LargeRaids)](https://img.shields.io/github/license/zhenghanlee/LargeRaids)
[![Spigot Downloads](http://badge.henrya.org/spigotbukkit/downloads?spigot=95422&name=spigot_downloads)](https://www.spigotmc.org/resources/largeraids-1-14-x-1-17-x.95422/)
[![Commit Activity](https://img.shields.io/github/commit-activity/m/zhenghanlee/LargeRaids)](https://img.shields.io/github/commit-activity/m/zhenghanlee/LargeRaids)
[![Discord](https://img.shields.io/discord/846941711741222922.svg?logo=discord)](https://discord.gg/YSv7pptDjE)

**LargeRaids** is a vanilla Spigot game experience enhancement plugin for [raids](https://minecraft.fandom.com/wiki/Raid), which are added to the game in the _Village & Pillage Update_. It expands the raid's mechanism to accommodate for the multiplayer environment with higher difficulty, higher bad omen levels, more raiders, more waves and higher rewards.

## Server Requirements

- Version: 1.14.4, 1.15.2, 1.16.5, 1.17.1, 1.18.1

## Installation

If you wish to install the repository locally, you might want to first install the [remapped jars](https://www.spigotmc.org/threads/spigot-bungeecord-1-17-1-17-1.510208/#:~:text=In%20order%20to%20assist%20developers%20with%20the%20transition%20we%20have%20added%20an%20additional%20option%20to%20BuildTools%2C%20%2D%2Dremapped) for versions 1.17.1 and 1.18 via [BuildTools](https://hub.spigotmc.org/jenkins/job/BuildTools/). Afterwhich, follow these steps to ensure that the project builds correctly.

1. Open up Terminal
2. Change to any desired directories for cloning the repository
3. Run `git clone https://github.com/zhenghanlee/LargeRaids.git`
4. Run `cd ./LargeRaids` to head into the project directory
5. Run `mvn install` to install the project locally and build the jar file (named `LargeRaids.jar`) in `target` folder

## Using the API

The API for the plugin has a separate [repository](https://github.com/zhenghanlee/LargeRaids-API). The instructions are duplicated here for your convenience.

### Maven Repository

You can add the project as your dependency by including the JitPack repository in your `pom.xml`:

```xml
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```

Then after add the dependency like so (replace `VERSION` with the version provided by the jitpack badge located at the start of this document):

```xml
<dependency>
	<groupId>com.github.zhenghanlee</groupId>
	<artifactId>LargeRaids-API</artifactId>
	<version>VERSION</version>
</dependency>
```

### Gradle Repository

You can add the project as your dependency by including the JitPack repository:

```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Then after add the dependency like so (replace `VERSION` with the version provided by the jitpack badge located at the start of this document):

```gradle
dependencies {
	    implementation 'com.github.zhenghanlee:LargeRaids-API:VERSION'
}
```

### Example Usage

#### Getting the Plugin

```java
Plugin plugin = Bukkit.getPluginManager().getPlugin("LargeRaids");
if (plugin != null) {
    LargeRaids lr = (LargeRaids) plugin;
    // Rest of the operations here
}
```

#### Getting Corresponding LargeRaid

A LargeRaid object can be obtained from either a Bukkit's `Location` or Bukkit's `Raid` instance.

```java
RaidManager raidManager = lr.getRaidManager(); // where lr is a LargeRaids instance
Optional<LargeRaid> raid = raidManager.getLargeRaid(location);
```

#### Getting Player Kills

We can get the number of kills a player have in a large raid when it finishes (or any time of the raid) as follows:

```java
@EventHandler
public void onRaidFinish(RaidFinishEvent evt) {
    Raid raid = evt.getRaid(); // Vanilla raid
    if (raid.getStatus() != RaidStatus.VICTORY)
        return;
    Optional<LargeRaid> largeRaid = raidManager.getLargeRaid(raid);
    if (!largeRaid.isPresent()) // No corresponding LargeRaid instance
        return;
    Optional<Integer> killCount = largeRaid.map(LargeRaid::getPlayerKills)
            .map(map -> map.get(player.getUniqueId()));
    if (!killCount.isPresent()) // Player is not a hero of this raid
        return;
    // Perform operations with the kill count (e.g. rewarding players based on kill count)
}
```

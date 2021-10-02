---
layout: page
title: "Developer Guide"
---

- [**Introduction**](#introduction)
- [**Getting Started**](#getting-started)
  - [Installation](#installation)
  - [Configuration](#configuration)
- [**Trigger Listeners**](#trigger-listeners)
- [**NMS Usages**](#nms-usages)
  - [Creating/removing artificial village centers](#creatingremoving-artificial-village-centers)
  - [Triggering a large raid](#triggering-a-large-raid)
  - [Triggering subsequent waves](#triggering-subsequent-waves)
  - [Awarding of the Hero of the Village status effect](#awarding-of-the-hero-of-the-village-status-effect)
  - [Applying Bad Omen status effect](#applying-bad-omen-status-effect)

## Introduction

**LargeRaids** is a multi-module maven project with the following artifacts:

- `largeraids-plugin` - Main plugin module, where most of the code resides
- `largeraids-1_1*` - Deals with the interaction with NMS packages
- `largeraids_api` - Contains the API classes for `largeraids-1_1*`, most of which are existing classes in `largeraids-plugin` and are not shaded into the final jar

All classes are declared under the package `com.solarrabbit.largeraids.*`, regardless of the module.

## Getting Started

### Installation

To get started, simply

1. Open up Terminal
2. Change to any desired directories for cloning the repository
3. Run `git clone https://github.com/zhenghanlee/LargeRaids.git`
4. Run `cd ./LargeRaids` to head into the project directory
5. Run `mvn install` to install the project locally and build the jar file (named `LargeRaids.jar`) in `target` folder

### Configuration

There isn't much to change for the `pom.xml` files. However, I have included a build profile called `test` in the `largeraids-plugin` module's `pom.xml` that copies the final jar file into desired directories to aid the testing of the plugin.

```xml
<plugin>
    <artifactId>maven-resources-plugin</artifactId>
    <version>3.0.2</version>
    <executions>
        <execution>
            <id>copy-to-1_17</id>
            <phase>package</phase>
            <goals>
                <goal>copy-resources</goal>
            </goals>
            <configuration>
                <!-- Replace [PATH] with the desired relative path -->
                <outputDirectory>${project.basedir}/[PATH]</outputDirectory>
                ...
            </configuration>
        </execution>
        ...
    </executions>
</plugin>
```

To include the profile into your build cycle, simply add a `-Ptest` flag (e.g. `mvn clean build -Ptest`).

## Trigger Listeners

Trigger listeners are listeners that govern a certain type of large raid triggering mechanism. If multiple classes are required to complete a triggering mechanism, it will be given its own package (e.g. `com.solarrabbit.largeraids.listener.omen`).

## NMS Usages

LargeRaids leverages on the existing raid system, but there are unavoidable instances where direct interaction with NMS packages are required. All interaction with the NMS packages are separated out to modules dependent of the game version. These modules each contains the following classes:

- `CustomVillages` implementing `AbstractVillages` - Deals with the creation/removal of artificial village centers
- `LargeRaid` extending `AbstractLargeRaid` - Encapsulates the components of a large raid (deals with raid triggering, wave spawning etc.)
- `Raider` implementing `AbstractRaider` - Encapsulates a raider to extend on the bad omen mechanisms in vanilla settings
- `RaiderConfig` - An enum class for mapping raider's configurations in wave spawning

<div markdown="block" class="alert alert-info">
> Note: Since version 1.17, Mojang has yet again reverted the NMS packages back into its obfuscated form. To better aid the project's development, we will use the dependency under `remapped-mojang` classifier for modules dealing with 1.17+ NMS packages, and allow SpecialSource to obfuscate our code during the build cycle. Refer [here](https://www.spigotmc.org/threads/spigot-bungeecord-1-17-1-17-1.510208/#post-4184317) for instructions to install the additional files into your local maven repository to use the classifier.
</div>

### Creating/removing artificial village centers

This feature is added with the intent of allowing server owners to restrict large raids to certain arenas, where players cannot alter the landscape to their advantage.

This could be achieved by simply adding a command to save certain locations into the database, where players will only be able to trigger a raid when they are within some distance from these locations. It is also neater to remove the need of villagers for the game to register it as a village.

This is done via the use of the `CustomVillages` class. Two villagers are spawned - one with AI and one without. We register a "fake" jobs block using the village record manager of the world, and force this block into the memory of the villager without AI. However, this doesn't make the jobs block occupied. The second villager with AI then occupies the jobs block after some time to create a mismatch between the original owner and the occupant. Despawning these two villagers will not release the occupancy of the jobs block, and hence, still make up a village.

### Triggering a large raid

### Triggering subsequent waves

### Awarding of the Hero of the Village status effect

### Applying Bad Omen status effect

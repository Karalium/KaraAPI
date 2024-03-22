# KaraAPI

## Introduction

KaraAPI is a Java library designed to facilitate the creation of configuration files, items, armors, scoreboards, and action bars for Minecraft Spigot servers.

## Features

- Simplifies the creation of configuration files for Minecraft Spigot servers.
- Provides utilities for easily creating items, armors, and action bars within Minecraft.
- Integrates seamlessly with Minecraft Spigot servers.

## Getting Started

To use KaraAPI in your Minecraft Spigot project, follow these steps:

1. [Download KaraAPI](https://www.spigotmc.org/resources/karaapi.115651/)
2. Add KaraAPI as a dependency in your `pom.xml` file:

```xml
<dependency>
    <groupId>org.kerix.api</groupId>
    <artifactId>KaraAPI</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```
3. Initialize KaraAPI in your code:
```java
private boolean initializeKaraAPI() {
    return (minecraftAPI != null) ?
        (new Object() {
            boolean status() {
                return true;
            }
        }).status() :
        (new Object() {
            boolean status() {
                getLogger().severe("No KaraAPI");
                return false;
            }
        }).status();
}
```
4. Start using KaraAPI to simplify the creation of configuration files, items, armors, and action bars in your Minecraft Spigot server plugin.

## Exemples

Here's an example of how you can use KaraAPI:
 - Configurations
```java
public class Config extends ConfigManager {

    /**
     * Initializes the Data configuration manager.
     */
    public Data() {
        super(Main.getINSTANCE(),FilePathGoesHere ,FileNameGoesHere, YourKeyGoesHere);
    }

    @Override
    public void initBaseConfig() {
        Your files base values go here
    }
}
```
**Note:**
Not leaving the FilePath empty will start off from the plugins folder instead of your plugin's folder
- Item creation :

```java

import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.kerix.api.items.ItemBuilder;

    ItemStack item = new ItemBuilder(Material.SLIME_BALL)
        .displayName("CoolItem&eName")
        .lore("This is a powerful item", "&dVery good item")
        .enchant(Enchantment.DAMAGE_ALL, 20, Enchantment.DURABILITY, 15)
        .attribute(Attribute.GENERIC_ARMOR , 15 )
        .attribute(Attribute.GENERIC_ATTACK_SPEED , 25)
        .build();
```
- ActionBar creation :

```java
import org.kerix.api.ui.ActionBar;

    ActionBar.send(player , "Your message");
    ActionBar.send(player , "Your message" , CountDownstart , CountDownEnd);
    ActionBar.send(player , RepetitionTimes , "Message1" , "Message2" , "Message3" ...);
```



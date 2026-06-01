# KaraAPI

KaraAPI is a modern Paper plugin framework/library designed to make Minecraft plugin development cleaner, faster, and more reusable.

It provides a complete foundation for Paper plugins: runtime bootstrapping, lifecycle management, service containers, task scheduling, config files, commands, menus, UI helpers, message rendering, placeholders, item builders, custom items, storage, registries, profiles, requirements, regions, and general logic utilities.

> Full documentation: **https://Karalium.github.io/KaraAPI**

---

## Purpose

KaraAPI is meant to be used as a shared plugin API.

Instead of every plugin manually handling startup, shutdown, configs, task cleanup, command registration, menu listeners, placeholder rendering, custom items, and data storage, KaraAPI gives host plugins a single bootstrapping system and a set of reusable APIs.

A host plugin can simply boot through KaraAPI:

```java
this.handle = KaraAPI.boot(
        this,
        new CoreModule(),
        new CommandModule(),
        new ListenerModule()
);
```

KaraAPI manages the framework runtime while the host plugin keeps ownership of its own commands, listeners, services, configs, menus, messages, and scheduler tasks.

---

## Requirements

Recommended target:

```text
Java 21
Paper 1.21+
Non-Folia scheduler by default
```

Optional integrations:

```text
PlaceholderAPI
```

If PlaceholderAPI is not installed, KaraAPI local placeholders still work. PlaceholderAPI placeholders such as `%player_name%` only resolve when PlaceholderAPI is installed.

---

## Main Features

KaraAPI currently includes:

```text
runtime/bootstrap       plugin boot and shutdown management
service                 ServiceContainer for dependency/lifecycle services
lifecycle               Startable, Stoppable, Tickable
tick                    interval-based tick orchestration
task                    lifecycle-safe Bukkit/Paper task scheduling
config                  YAML config loading, defaults, typed config keys
command                 command tree API with subcommands and tab completion
menu                    inventory GUI and paginated menu API
ui                      chat, actionbar, title, bossbar, sidebar helpers
message                 MiniMessage + placeholders + gradient rendering
placeholder             local placeholders + optional PlaceholderAPI integration
item                    ItemBuilder, SkullBuilder, WrittenBookBuilder
item.custom             custom item registration and event hooks
registry                named registries for menus, abilities, regions, etc.
storage                 generic repository pattern and YAML storage
profile                 profile caches backed by storage repositories
requirement             reusable permission/world/predicate requirements
region                  cuboid and sphere regions
color                   hex colors and gradients
logic                   Result, Try, Lazy, Cooldowns, StateMachine, etc.
paper                   Bukkit/Paper adapter layer
internal                private utilities used by KaraAPI
```

---

## Repository Structure

Recommended structure:

```text
org.kerix.karaapi
├── KaraAPIPlugin.java
│
├── api
│   ├── KaraAPI.java
│   ├── PluginHandle.java
│   ├── bootstrap
│   ├── lifecycle
│   ├── service
│   ├── startup
│   ├── tick
│   ├── task
│   ├── config
│   ├── command
│   ├── menu
│   ├── item
│   ├── message
│   ├── placeholder
│   ├── registry
│   ├── storage
│   ├── profile
│   ├── requirement
│   ├── region
│   ├── ui
│   ├── color
│   └── logic
│
├── runtime
│   ├── KaraRuntime.java
│   └── PluginKernel.java
│
├── paper
│   ├── command
│   ├── listener
│   ├── scheduler
│   ├── inventory
│   ├── item
│   ├── placeholder
│   ├── scoreboard
│   └── text
│
└── internal
    ├── validate
    ├── util
    ├── collection
    └── debug
```

Package meaning:

```text
api      public classes host plugins can import
runtime  KaraAPI boot/shutdown engine
paper    Bukkit/Paper adapter layer
internal private KaraAPI-only helpers
root     KaraAPIPlugin only
```

---

## Installing KaraAPI on a Server

KaraAPI is intended to run as its own server plugin.

Put the compiled KaraAPI jar in:

```text
server/plugins/KaraAPI.jar
```

Then plugins that use KaraAPI should declare it as a dependency in their `plugin.yml`:

```yaml
depend:
  - KaraAPI
```

Most plugins should use `depend`, not `softdepend`, because they cannot boot without KaraAPI.

---

## Using KaraAPI in a Plugin

Host plugin `plugin.yml`:

```yaml
name: ExamplePlugin
version: 1.0.0
main: com.example.exampleplugin.ExamplePlugin
api-version: '1.21'

depend:
  - KaraAPI
```

If the host plugin wants PlaceholderAPI support:

```yaml
softdepend:
  - PlaceholderAPI
```

Main class:

```java
package com.example.exampleplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.KaraAPI;
import org.kerix.karaapi.api.PluginHandle;

public final class ExamplePlugin extends JavaPlugin {

    private PluginHandle handle;

    @Override
    public void onEnable() {
        this.handle = KaraAPI.boot(
                this,
                new CoreModule(),
                new CommandModule(),
                new ListenerModule()
        );
    }

    @Override
    public void onDisable() {
        if (handle != null) {
            handle.shutdown();
        }
    }
}
```

Example module:

```java
package com.example.exampleplugin;

import org.kerix.karaapi.api.bootstrap.BootstrapContext;
import org.kerix.karaapi.api.bootstrap.PluginModule;

public final class CoreModule implements PluginModule {

    @Override
    public void configure(BootstrapContext context) {
        context.hostPlugin().getLogger().info("Core module loaded.");

        context.services().bind(
                ExampleService.class,
                new ExampleService(context.tasks())
        );
    }
}
```

---

# Importing KaraAPI as a Dependency

KaraAPI can be consumed through JitPack once the repository has a Git tag or release.

The JitPack coordinates are:

```text
com.github.Karalium:KaraAPI:Tag
```

Replace `Tag` with the release tag you want to use, for example:

```text
v1.0.0
```

---

## Gradle Kotlin DSL

### Step 1. Add JitPack

In your `settings.gradle.kts`, add JitPack at the end of repositories:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2. Add the dependency

In your `build.gradle.kts`:

```kotlin
dependencies {
    compileOnly("com.github.Karalium:KaraAPI:Tag")
}
```

Example:

```kotlin
dependencies {
    compileOnly("com.github.Karalium:KaraAPI:v1.0.0")
}
```

Use `compileOnly` when KaraAPI is installed as a separate plugin on the server.

If you intentionally want to bundle KaraAPI inside your plugin jar, use:

```kotlin
dependencies {
    implementation("com.github.Karalium:KaraAPI:Tag")
}
```

For the recommended shared-plugin setup, use `compileOnly`.

---

## Gradle Groovy DSL

```groovy
repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    compileOnly "com.github.Karalium:KaraAPI:Tag"
}
```

Example:

```groovy
dependencies {
    compileOnly "com.github.Karalium:KaraAPI:v1.0.0"
}
```

---

## Maven

Add repositories:

```xml
<repositories>
    <repository>
        <id>papermc</id>
        <url>https://repo.papermc.io/repository/maven-public/</url>
    </repository>

    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add the dependency:

```xml
<dependencies>
    <dependency>
        <groupId>com.github.Karalium</groupId>
        <artifactId>KaraAPI</artifactId>
        <version>Tag</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

Example:

```xml
<dependency>
    <groupId>com.github.Karalium</groupId>
    <artifactId>KaraAPI</artifactId>
    <version>v1.0.0</version>
    <scope>provided</scope>
</dependency>
```

Use `provided` when KaraAPI is installed separately on the server.

---

# Making KaraAPI Importable Through JitPack

To publish KaraAPI through JitPack:

## 1. Make sure KaraAPI builds

Run:

```bash
./gradlew clean build publishToMavenLocal
```

On Windows PowerShell:

```powershell
.\gradlew clean build publishToMavenLocal
```

## 2. Add `maven-publish`

Your `build.gradle.kts` should include:

```kotlin
plugins {
    `java-library`
    `maven-publish`
}

group = "org.kerix"
version = "1.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "org.kerix"
            artifactId = "karaapi"
            version = project.version.toString()
        }
    }
}
```

## 3. Add `jitpack.yml`

Create `jitpack.yml` at the root of the repository:

```yaml
jdk:
  - openjdk21
```

## 4. Commit and push

```bash
git add .
git commit -m "build: configure JitPack publishing"
git push
```

## 5. Create a tag

```bash
git tag v1.0.0
git push origin v1.0.0
```

## 6. Build on JitPack

Open:

```text
https://jitpack.io/#Karalium/KaraAPI/v1.0.0
```

Click **Get it** and wait for a successful build.

After that, other projects can depend on:

```kotlin
compileOnly("com.github.Karalium:KaraAPI:v1.0.0")
```

---

# Quick Usage Examples

## Services

```java
context.services().bind(MyService.class, new MyService());

MyService service = context.services().get(MyService.class);
```

## Tasks

```java
context.tasks().later(20L, () -> {
    player.sendMessage("Ran one second later.");
});

context.tasks().timer(0L, 20L, () -> {
    // runs every second
});
```

## Messages, Placeholders, and Gradients

```java
context.ui().chat(
        player,
        "<green>Hello <player>, you have <coins> coins.</green>",
        PlaceholderSet.empty()
                .add("player", player.getName())
                .add("coins", 250)
);
```

```java
context.ui().actionBar(
        player,
        "<gradient:#00ffaa:#ff00ff>Hello <player>, online: %server_online%</gradient>",
        PlaceholderSet.empty().add("player", player.getName())
);
```

## Commands

```java
context.commands().register(
        KaraCommands.command("example")
                .permission("example.use")
                .then(KaraCommands.command("reload")
                        .permission("example.reload")
                        .executes(ctx -> {
                            ctx.sender().sendMessage("Reloaded.");
                            return CommandResult.success();
                        }))
                .build()
);
```

## Menus

```java
Menu menu = Menu.builder(KaraUI.gradient("Shop", "#00ffaa", "#00ffff"), 3)
        .slot(
                11,
                ItemBuilder.of(Material.DIAMOND_SWORD)
                        .name(KaraUI.gradient("Sword", "#00ffff", "#ff00ff"))
                        .build(),
                click -> {
                    click.player().sendMessage("Bought sword.");
                    click.closeNextTick();
                }
        )
        .build();

context.menus().open(player, menu);
```

## Custom Items

```java
context.customItems().register(new MagicSwordItem());

player.getInventory().addItem(
        context.customItems().create("magic_sword")
);
```

## Regions

```java
Region spawn = new CuboidRegion("spawn", firstLocation, secondLocation);

context.regions().register(spawn);

if (context.regions().contains("spawn", player.getLocation())) {
    player.sendMessage("You are in spawn.");
}
```

---

# Documentation

Full documentation is hosted at:

```text
https://Karalium.github.io/KaraAPI
```

The documentation site contains a more detailed guide for every package and system.

---

# Recommended Dependency Model

Recommended setup for plugins using KaraAPI:

```text
Gradle: compileOnly
Maven: provided
plugin.yml: depend: [KaraAPI]
server/plugins: KaraAPI.jar
```

Do not shade KaraAPI into every plugin unless you intentionally want each plugin to carry its own copy.

The intended design is:

```text
KaraAPI = one shared server plugin
Host plugins = compile against KaraAPI and depend on it at runtime
```

---

# License

Add a license before publishing the project publicly.

Recommended options:

```text
MIT
Apache-2.0
GPL-3.0
```

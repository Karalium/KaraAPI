# KaraAPI

KaraAPI is a modern Paper plugin framework/library made to reduce boilerplate when building Minecraft plugins.

It provides a reusable runtime, lifecycle system, service container, task scheduler, config utilities, command framework, menu API, UI helpers, message/placeholder rendering, item builders, custom item handling, storage, registries, profiles, requirements, and regions.

> Full documentation: [docs/index.html](docs/index.html)  
> If GitHub Pages is enabled from `main/docs`, the hosted documentation will be available at:
>
> `https://Karalium.github.io/KaraAPI/`

---

## Purpose

KaraAPI is designed to be installed as a shared Paper plugin and used by other plugins as a dependency.

Instead of every plugin manually creating its own startup system, services, task cleanup, config loading, command registration, GUI listeners, placeholder rendering, and utility classes, KaraAPI gives host plugins a clean bootstrap API:

```java
this.handle = KaraAPI.boot(
        this,
        new CoreModule(),
        new CommandModule(),
        new ListenerModule()
);
```

KaraAPI manages the runtime while the host plugin owns its own services, commands, listeners, scheduler tasks, messages, configs, menus, and lifecycle.

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

If PlaceholderAPI is not installed, KaraAPI local placeholders still work. PlaceholderAPI placeholders such as `%player_name%` only resolve when PlaceholderAPI is present.

---

## Documentation

The full HTML documentation is located at:

```text
docs/index.html
```

Open it locally in a browser or deploy it with GitHub Pages from the `main` branch and `/docs` folder.

---

## Project layout

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
api      = public classes host plugins are allowed to import
runtime  = KaraAPI boot/shutdown engine
paper    = Bukkit/Paper adapter layer
internal = private KaraAPI-only helpers
root     = KaraAPIPlugin only
```

---

## Installing KaraAPI on a server

KaraAPI should usually be used as a server plugin dependency.

Put the compiled KaraAPI jar in:

```text
server/plugins/KaraAPI.jar
```

Then host plugins that use KaraAPI should declare:

```yaml
depend:
  - KaraAPI
```

Most plugins using KaraAPI should use `depend`, not `softdepend`.

---

## Adding KaraAPI to another plugin

Host plugin `plugin.yml`:

```yaml
name: ExamplePlugin
version: 1.0.0
main: com.example.exampleplugin.ExamplePlugin
api-version: '1.21'

depend:
  - KaraAPI
```

If the host plugin also wants PlaceholderAPI placeholders:

```yaml
softdepend:
  - PlaceholderAPI
```

---

## Bootstrapping a plugin

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

---

## Creating a module

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

## Lifecycle services

```java
import org.kerix.karaapi.api.lifecycle.Startable;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.api.lifecycle.Tickable;

public final class ExampleService implements Startable, Stoppable, Tickable {

    @Override
    public void start() {
        System.out.println("Started.");
    }

    @Override
    public void stop() {
        System.out.println("Stopped.");
    }

    @Override
    public long tickInterval() {
        return 20L;
    }

    @Override
    public void tick() {
        System.out.println("Runs every second.");
    }
}
```

KaraAPI automatically starts, stops, and ticks bound services.

---

## Service container

The service container is the dependency/lifecycle container.

```java
context.services().bind(MyService.class, new MyService());

MyService service = context.services().get(MyService.class);
```

Do not confuse it with the registry system:

```text
ServiceContainer = dependency/lifecycle services
RegistryService  = named user registries
```

---

## Tasks

```java
context.tasks().sync(() -> {
    // main thread
});

context.tasks().later(20L, () -> {
    // 1 second later
});

context.tasks().timer(0L, 20L, () -> {
    // every second
});
```

Task groups:

```java
context.tasks().group("scoreboards")
        .timer("update-sidebar", 0L, 20L, () -> {
            // update UI
        });

context.tasks().cancelGroup("scoreboards");
```

---

## Config files

```java
YamlConfig config = context.configs().main();

String serverName = config.get(
        ConfigKey.string("server.name", "Kara Server")
);

config.set("debug", true);
config.save();
```

Multiple files:

```java
YamlConfig messages = context.configs().config("messages.yml");
YamlConfig arena = context.configs().config("arenas/test.yml");
```

---

## Messages and placeholders

KaraAPI has its own local placeholder system and can optionally hook into PlaceholderAPI.

Local placeholders:

```java
context.ui().chat(
        player,
        "<green>Hello <player>, you have <coins> coins.</green>",
        PlaceholderSet.empty()
                .add("player", player.getName())
                .add("coins", 250)
);
```

MiniMessage gradients and placeholders together:

```java
context.ui().actionBar(
        player,
        "<gradient:#00ffaa:#ff00ff>Hello <player>, online: %server_online%</gradient>",
        PlaceholderSet.empty().add("player", player.getName())
);
```

If PlaceholderAPI is absent:

```text
<player> and <coins> still work
%server_online% stays unresolved
```

---

## Creating PlaceholderAPI placeholders

If PlaceholderAPI is installed, host plugins can register their own expansion:

```java
context.placeholders().registerExpansion(
        KaraPlaceholderExpansion.of(
                context.hostPlugin(),
                "example",
                (player, params) -> {
                    if (player == null) {
                        return "";
                    }

                    return switch (params.toLowerCase()) {
                        case "name" -> player.getName();
                        case "coins" -> "250";
                        case "rank" -> "Admin";
                        default -> null;
                    };
                }
        )
);
```

This creates placeholders like:

```text
%example_name%
%example_coins%
%example_rank%
```

---

## Commands

Register commands from a module:

```java
context.commands().register(
        KaraCommands.command("example")
                .usage("/example <reload|info>")
                .permission("example.use")

                .then(KaraCommands.command("reload")
                        .permission("example.reload")
                        .executes(ctx -> {
                            ctx.sender().sendMessage("Reloaded.");
                            return CommandResult.success();
                        }))

                .then(KaraCommands.command("info")
                        .executes(ctx -> {
                            ctx.sender().sendMessage("ExamplePlugin using KaraAPI.");
                            return CommandResult.success();
                        }))

                .build()
);
```

The root command must exist in the host plugin `plugin.yml`:

```yaml
commands:
  example:
    description: Main example command
```

---

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
        .slot(
                15,
                ItemBuilder.of(Material.BARRIER)
                        .name(Component.text("Close"))
                        .build(),
                MenuClick::closeNextTick
        )
        .build();

context.menus().open(player, menu);
```

---

## UI

```java
context.ui().chat(
        player,
        "<green>Hello <player></green>",
        PlaceholderSet.empty().add("player", player.getName())
);

context.ui().actionBar(
        player,
        "<gradient:#ffaa00:#ffff00>Coins: <coins></gradient>",
        PlaceholderSet.empty().add("coins", 250)
);

context.ui().title(
        player,
        "<green>Welcome</green>",
        "<gray>Hello <player></gray>",
        PlaceholderSet.empty().add("player", player.getName())
);
```

---

## ItemBuilder

```java
ItemStack item = ItemBuilder.of(Material.DIAMOND_SWORD)
        .name(KaraUI.gradient("Magic Sword", "#00ffaa", "#ff00ff"))
        .lore(Component.text("A custom KaraAPI item."))
        .unbreakable()
        .hideAll()
        .build();
```

Written books:

```java
ItemStack book = WrittenBookBuilder.create()
        .title("Guide")
        .author("KaraAPI")
        .page(page -> page
                .append(KaraUI.gradient("Welcome to KaraAPI", "#00ffaa", "#ff00ff"))
                .newline()
                .command("Click to spawn", "/spawn", Component.text("Teleport to spawn"))
        )
        .build();
```

---

## Custom items

```java
public final class MagicSwordItem implements CustomItem {

    @Override
    public String id() {
        return "magic_sword";
    }

    @Override
    public ItemStack create() {
        return ItemBuilder.of(Material.DIAMOND_SWORD)
                .name(KaraUI.gradient("Magic Sword", "#00ffaa", "#ff00ff"))
                .build();
    }

    @Override
    public void onAttack(CustomItemAttack event) {
        event.damage(event.event().getDamage() + 4.0);
        event.player().sendMessage(Component.text("Magic hit!"));
    }
}
```

Register and give:

```java
context.customItems().register(new MagicSwordItem());

player.getInventory().addItem(
        context.customItems().create("magic_sword")
);
```

---

## Registries

Registries are for user-defined ID-to-object maps.

```java
MutableRegistry<Menu> menus = context.registries().getOrCreate("menus");

menus.register("shop", shopMenu);

Menu shop = menus.get("shop");
context.menus().open(player, shop);
```

---

## Storage

Create a repository:

```java
public record PlayerData(UUID id, int coins) {
}
```

```java
StorageCodec<PlayerData> codec = new StorageCodec<>() {

    @Override
    public void write(ConfigurationSection section, PlayerData value) {
        section.set("uuid", value.id().toString());
        section.set("coins", value.coins());
    }

    @Override
    public PlayerData read(ConfigurationSection section) {
        return new PlayerData(
                UUID.fromString(section.getString("uuid")),
                section.getInt("coins")
        );
    }
};

Repository<UUID, PlayerData> repository = context.storage().yamlRepository(
        "profiles",
        "profiles",
        UUID::toString,
        UUID::fromString,
        codec
);
```

Use it:

```java
repository.save(player.getUniqueId(), new PlayerData(player.getUniqueId(), 100));

PlayerData data = repository.load(player.getUniqueId())
        .orElse(new PlayerData(player.getUniqueId(), 0));
```

---

## Profiles

```java
ProfileCache<PlayerData> profiles = context.profiles().create(
        "players",
        repository,
        uuid -> new PlayerData(uuid, 0)
);

PlayerData data = profiles.getOrLoad(player.getUniqueId());

profiles.save(player.getUniqueId());
profiles.saveAndUnload(player.getUniqueId());
```

---

## Requirements

```java
Requirement<Player> canUse = Requirements.all(
        Requirements.world("world"),
        Requirements.predicate(
                player -> player.getLevel() >= 10,
                "You need level 10."
        )
);

RequirementResult result = canUse.check(player);

if (result.denied()) {
    player.sendMessage(result.message());
    return;
}
```

Reusable requirement:

```java
context.requirements().register(
        "level_10",
        Requirements.predicate(
                player -> player.getLevel() >= 10,
                "You need level 10."
        )
);
```

---

## Regions

```java
Region spawn = new CuboidRegion(
        "spawn",
        firstLocation,
        secondLocation
);

context.regions().register(spawn);
```

Check if a player is inside:

```java
if (context.regions().contains("spawn", player.getLocation())) {
    player.sendMessage("You are in spawn.");
}
```

Players inside a region:

```java
List<Player> players = context.regions().playersInside("spawn");
```

Random location:

```java
Location random = context.regions().randomLocation("spawn");
player.teleport(random);
```

---

# Using KaraAPI as an importable dependency

There are several ways to make this repository importable.

## Option 1: JitPack

This is the easiest option for GitHub-hosted projects.

### Publish through JitPack

1. Push KaraAPI to GitHub.
2. Make sure the project builds with Gradle or Maven.
3. Create a release/tag:

```bash
git tag v1.0.0
git push origin v1.0.0
```

4. Go to:

```text
https://jitpack.io/#<github-username>/<repository-name>/v1.0.0
```

### Gradle Kotlin DSL consumer

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.<github-username>:<repository-name>:v1.0.0")
}
```

### Gradle Groovy consumer

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.<github-username>:<repository-name>:v1.0.0'
}
```

### Maven consumer

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.&lt;github-username&gt;</groupId>
        <artifactId>&lt;repository-name&gt;</artifactId>
        <version>v1.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

Use `compileOnly` or Maven `provided` because KaraAPI should be installed as a plugin on the server.

---

## Option 2: Maven Local

Good for testing before publishing.

In KaraAPI:

```bash
./gradlew publishToMavenLocal
```

Consumer project with Gradle Kotlin DSL:

```kotlin
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("org.kerix:karaapi:1.0.0")
}
```

Consumer project with Gradle Groovy:

```groovy
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly 'org.kerix:karaapi:1.0.0'
}
```

Consumer project with Maven:

```xml
<dependency>
    <groupId>org.kerix</groupId>
    <artifactId>karaapi</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

---

## Option 3: GitHub Packages

Good for private or organization-owned packages.

Consumers will usually need GitHub authentication.

Gradle Kotlin DSL:

```kotlin
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/<github-username>/<repository-name>")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    compileOnly("org.kerix:karaapi:1.0.0")
}
```

Maven:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/&lt;github-username&gt;/&lt;repository-name&gt;</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.kerix</groupId>
        <artifactId>karaapi</artifactId>
        <version>1.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

---

## Option 4: Maven Central

Best long-term option for a public stable library, but it requires more setup:

```text
namespace ownership
artifact signing
metadata
publishing configuration
release validation
```

Use Maven Central once KaraAPI is stable.

---

# Publishing setup for KaraAPI

In `build.gradle.kts`:

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

Then test local publishing:

```bash
./gradlew publishToMavenLocal
```

---

# Recommended dependency model

Because KaraAPI is a Paper plugin dependency, host plugins should usually use:

```text
Gradle: compileOnly
Maven: provided
plugin.yml: depend: [KaraAPI]
server/plugins: KaraAPI.jar
```

Do not shade KaraAPI into every plugin unless you intentionally want each plugin to carry its own copy. The intended design is a shared server plugin runtime.

---

# GitHub Pages documentation

If GitHub Pages is configured to deploy from:

```text
main /docs
```

then your docs should be:

```text
docs/index.html
```

Your root `README.md` should link to it:

```md
[Full Documentation](docs/index.html)
```

After pushing, GitHub Pages will serve it at:

```text
https://<github-username>.github.io/<repository-name>/
```

---

# Development checklist

Before releasing:

```text
1. Make sure KaraAPI builds.
2. Make sure plugin.yml or paper-plugin.yml is correct.
3. Put docs/index.html in the repository.
4. Add this README.md at the repository root.
5. Create a GitHub release/tag.
6. Publish through JitPack, GitHub Packages, Maven Local, or Maven Central.
7. In host plugins, use compileOnly/provided and depend: [KaraAPI].
```

---

# License

Add a license before publishing publicly.

Recommended common options:

```text
MIT
Apache-2.0
GPL-3.0
```

Choose the one that matches how you want other people to use KaraAPI.

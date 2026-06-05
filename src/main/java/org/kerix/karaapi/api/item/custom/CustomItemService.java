package org.kerix.karaapi.api.item.custom;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.item.ItemKeys;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.api.logic.Cooldowns;
import org.kerix.karaapi.api.registry.MutableRegistry;
import org.kerix.karaapi.api.requirement.RequirementResult;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class CustomItemService implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final NamespacedKey itemIdKey;
    private final MutableRegistry<CustomItem> items = MutableRegistry.create("custom_items");
    private final Map<String, Cooldowns<UUID>> cooldowns = new HashMap<>();

    private boolean stopped;

    public CustomItemService(JavaPlugin hostPlugin) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.itemIdKey = new NamespacedKey(hostPlugin, "custom_item_id");
    }

    public void register(CustomItem item) {
        ensureRunning();
        Objects.requireNonNull(item, "item");

        items.register(normalize(item.id()), item);
    }

    public void register(CustomItemBuilder builder) {
        Objects.requireNonNull(builder, "builder");
        register(builder.build());
    }

    public CustomItem get(String id) {
        return items.get(normalize(id));
    }

    public Optional<CustomItem> find(String id) {
        return items.find(normalize(id));
    }

    public ItemStack create(String id) {
        ensureRunning();

        CustomItem item = get(id);
        return tag(item.build(), item.id());
    }

    public ItemStack tag(ItemStack item, String id) {
        Objects.requireNonNull(item, "item");

        ItemStack copy = item.clone();
        ItemMeta meta = copy.getItemMeta();

        if (meta == null) {
            throw new IllegalStateException("Item has no ItemMeta.");
        }

        meta.getPersistentDataContainer()
                .set(itemIdKey, PersistentDataType.STRING, normalize(id));

        copy.setItemMeta(meta);

        return copy;
    }

    public Optional<String> idOf(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return Optional.empty();
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return Optional.empty();
        }

        String id = meta.getPersistentDataContainer()
                .get(itemIdKey, PersistentDataType.STRING);

        return Optional.ofNullable(id);
    }

    public Optional<CustomItem> customItemOf(ItemStack item) {
        return idOf(item).flatMap(this::find);
    }

    public boolean isCustomItem(ItemStack item) {
        return customItemOf(item).isPresent();
    }

    public boolean is(ItemStack item, String id) {
        return idOf(item)
                .map(found -> found.equals(normalize(id)))
                .orElse(false);
    }

    public void dispatchInteract(CustomItemInteract event) {
        dispatch(event.customItem(), event.player(), () -> event.customItem().onInteract(event));
    }

    public void dispatchAttack(CustomItemAttack event) {
        dispatch(event.customItem(), event.player(), () -> event.customItem().onAttack(event));
    }

    public void dispatchInventoryClick(CustomItemInventoryClick event) {
        dispatch(event.customItem(), event.player(), () -> event.customItem().onInventoryClick(event));
    }

    public NamespacedKey itemIdKey() {
        return itemIdKey;
    }

    public MutableRegistry<CustomItem> registry() {
        return items;
    }

    public JavaPlugin hostPlugin() {
        return hostPlugin;
    }

    @Override
    public void stop() {
        if (stopped) {
            return;
        }

        stopped = true;
        items.clear();
        cooldowns.clear();
    }

    private void dispatch(CustomItem item, Player player, Runnable action) {
        ensureRunning();

        RequirementResult result = item.check(player);

        if (result.denied()) {
            return;
        }

        if (!consumeCooldown(item, player)) {
            return;
        }

        action.run();
    }

    private boolean consumeCooldown(CustomItem item, Player player) {
        Optional<java.time.Duration> cooldown = item.cooldown();

        if (cooldown.isEmpty()) {
            return true;
        }

        String id = normalize(item.id());

        Cooldowns<UUID> itemCooldowns = cooldowns.computeIfAbsent(
                id,
                ignored -> new Cooldowns<>()
        );

        return itemCooldowns.tryUse(player.getUniqueId(), cooldown.get());
    }

    private void ensureRunning() {
        if (stopped) {
            throw new IllegalStateException("CustomItemService has already stopped.");
        }
    }

    private static String normalize(String id) {
        Objects.requireNonNull(id, "id");

        String normalized = id.trim()
                .toLowerCase(Locale.ROOT)
                .replace(" ", "_")
                .replace("-", "_");

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Custom item id cannot be blank.");
        }

        return ItemKeys.normalize(normalized);
    }
}

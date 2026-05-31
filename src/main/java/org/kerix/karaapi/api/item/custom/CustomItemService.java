package org.kerix.karaapi.api.item.custom;

import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.karaapi.api.lifecycle.Stoppable;
import org.kerix.karaapi.api.registry.MutableRegistry;
import org.kerix.karaapi.paper.item.PaperCustomItemListener;
import org.kerix.karaapi.paper.listener.PaperListenerRegistrar;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class CustomItemService implements Stoppable {

    private final JavaPlugin hostPlugin;
    private final NamespacedKey itemIdKey;
    private final MutableRegistry<CustomItem> items =
            MutableRegistry.create("custom_items");

    public CustomItemService(JavaPlugin hostPlugin) {
        this.hostPlugin = Objects.requireNonNull(hostPlugin, "hostPlugin");
        this.itemIdKey = new NamespacedKey(hostPlugin, "custom_item_id");

        new PaperListenerRegistrar(hostPlugin)
                .register(new PaperCustomItemListener(this));
    }

    public void register(CustomItem item) {
        Objects.requireNonNull(item, "item");

        items.register(normalize(item.id()), item);
    }

    public CustomItem get(String id) {
        return items.get(normalize(id));
    }

    public Optional<CustomItem> find(String id) {
        return items.find(normalize(id));
    }

    public ItemStack create(String id) {
        CustomItem item = get(id);

        return tag(item.create(), item.id());
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
        return idOf(item).isPresent();
    }

    public boolean is(ItemStack item, String id) {
        return idOf(item)
                .map(found -> found.equals(normalize(id)))
                .orElse(false);
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
        items.clear();
    }

    private static String normalize(String id) {
        Objects.requireNonNull(id, "id");

        if (id.isBlank()) {
            throw new IllegalArgumentException("Custom item id cannot be blank.");
        }

        return id.trim()
                .toLowerCase(Locale.ROOT)
                .replace(" ", "_");
    }
}

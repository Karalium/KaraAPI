package org.kerix.karaapi.api.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ItemBuilder implements ItemProvider, Cloneable {

    protected ItemStack item;

    protected ItemBuilder(ItemStack item) {
        this.item = Objects.requireNonNull(item, "item");
    }

    public static ItemBuilder of(Material material) {
        return of(material, 1);
    }

    public static ItemBuilder of(Material material, int amount) {
        Objects.requireNonNull(material, "material");

        if (!material.isItem()) {
            throw new IllegalArgumentException(material + " is not an item material.");
        }

        if (amount < 1) {
            throw new IllegalArgumentException("Item amount must be at least 1.");
        }

        return new ItemBuilder(new ItemStack(material, amount));
    }

    public static ItemBuilder from(ItemStack item) {
        Objects.requireNonNull(item, "item");
        return new ItemBuilder(item.clone());
    }

    public ItemBuilder type(Material material) {
        Objects.requireNonNull(material, "material");

        if (!material.isItem()) {
            throw new IllegalArgumentException(material + " is not an item material.");
        }

        item.setType(material);
        return this;
    }

    public ItemBuilder amount(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Item amount must be at least 1.");
        }

        item.setAmount(amount);
        return this;
    }

    public ItemBuilder name(String name) {
        return name(ItemText.text(name));
    }

    public ItemBuilder name(Component name) {
        Objects.requireNonNull(name, "name");
        return meta(meta -> meta.displayName(name));
    }

    public ItemBuilder clearName() {
        return meta(meta -> meta.displayName(null));
    }

    public ItemBuilder lore(String... lines) {
        return lore(ItemText.lines(lines));
    }

    public ItemBuilder lore(Component... lines) {
        Objects.requireNonNull(lines, "lines");
        return lore(Arrays.asList(lines));
    }

    public ItemBuilder lore(List<Component> lore) {
        Objects.requireNonNull(lore, "lore");
        return meta(meta -> meta.lore(new ArrayList<>(lore)));
    }

    public ItemBuilder clearLore() {
        return meta(meta -> meta.lore(null));
    }

    public ItemBuilder appendLore(String line) {
        return appendLore(ItemText.text(line));
    }

    public ItemBuilder appendLore(Component line) {
        Objects.requireNonNull(line, "line");

        return meta(meta -> {
            List<Component> lore = meta.lore();

            if (lore == null) {
                lore = new ArrayList<>();
            } else {
                lore = new ArrayList<>(lore);
            }

            lore.add(line);
            meta.lore(lore);
        });
    }

    public ItemBuilder insertLore(int index, Component line) {
        Objects.requireNonNull(line, "line");

        return meta(meta -> {
            List<Component> lore = meta.lore();

            if (lore == null) {
                lore = new ArrayList<>();
            } else {
                lore = new ArrayList<>(lore);
            }

            lore.add(index, line);
            meta.lore(lore);
        });
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        return enchant(enchantment, level, true);
    }

    public ItemBuilder enchant(
            Enchantment enchantment,
            int level,
            boolean ignoreLevelRestriction
    ) {
        Objects.requireNonNull(enchantment, "enchantment");
        return meta(meta -> meta.addEnchant(enchantment, level, ignoreLevelRestriction));
    }

    public ItemBuilder removeEnchant(Enchantment enchantment) {
        Objects.requireNonNull(enchantment, "enchantment");
        return meta(meta -> meta.removeEnchant(enchantment));
    }

    public ItemBuilder flags(ItemFlag... flags) {
        Objects.requireNonNull(flags, "flags");
        return meta(meta -> meta.addItemFlags(flags));
    }

    public ItemBuilder removeFlags(ItemFlag... flags) {
        Objects.requireNonNull(flags, "flags");
        return meta(meta -> meta.removeItemFlags(flags));
    }

    public ItemBuilder hideAll() {
        return flags(ItemFlag.values());
    }

    public ItemBuilder unbreakable() {
        return unbreakable(true);
    }

    public ItemBuilder unbreakable(boolean value) {
        return meta(meta -> meta.setUnbreakable(value));
    }

    public ItemBuilder customModelData(Integer data) {
        return meta(meta -> meta.setCustomModelData(data));
    }

    public ItemBuilder damage(int damage) {
        return meta(Damageable.class, meta -> meta.setDamage(Math.max(0, damage)));
    }

    public ItemBuilder repairCost(int repairCost) {
        return meta(Repairable.class, meta -> meta.setRepairCost(Math.max(0, repairCost)));
    }

    public <P, C> ItemBuilder data(
            NamespacedKey key,
            PersistentDataType<P, C> type,
            C value
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(value, "value");

        return meta(meta -> meta.getPersistentDataContainer().set(key, type, value));
    }

    public ItemBuilder removeData(NamespacedKey key) {
        Objects.requireNonNull(key, "key");
        return meta(meta -> meta.getPersistentDataContainer().remove(key));
    }

    public <P, C> boolean hasData(
            NamespacedKey key,
            PersistentDataType<P, C> type
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(type, "type");

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return false;
        }

        return meta.getPersistentDataContainer().has(key, type);
    }

    public <P, C> C getData(
            NamespacedKey key,
            PersistentDataType<P, C> type
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(type, "type");

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return null;
        }

        return meta.getPersistentDataContainer().get(key, type);
    }

    public PersistentDataContainer dataContainer() {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            throw new IllegalStateException("Item has no ItemMeta.");
        }

        return meta.getPersistentDataContainer();
    }

    public ItemBuilder meta(Consumer<ItemMeta> editor) {
        Objects.requireNonNull(editor, "editor");

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            throw new IllegalStateException("Item has no ItemMeta.");
        }

        editor.accept(meta);
        item.setItemMeta(meta);

        return this;
    }

    public <M extends ItemMeta> ItemBuilder meta(
            Class<M> metaType,
            Consumer<M> editor
    ) {
        Objects.requireNonNull(metaType, "metaType");
        Objects.requireNonNull(editor, "editor");

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            throw new IllegalStateException("Item has no ItemMeta.");
        }

        if (!metaType.isInstance(meta)) {
            throw new IllegalStateException(
                    "Expected meta type "
                            + metaType.getSimpleName()
                            + " but got "
                            + meta.getClass().getSimpleName()
            );
        }

        M typedMeta = metaType.cast(meta);
        editor.accept(typedMeta);
        item.setItemMeta(typedMeta);

        return this;
    }

    public Material type() {
        return item.getType();
    }

    public int amount() {
        return item.getAmount();
    }

    public ItemStack copyRaw() {
        return item.clone();
    }

    @Override
    public ItemStack build() {
        return item.clone();
    }

    @Override
    public ItemBuilder clone() {
        return from(item);
    }
}

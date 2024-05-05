package org.kerix.api.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.kerix.api.items.nbt.NBTBuilder;

import java.util.ArrayList;
import java.util.UUID;

public class ItemBuilder extends ItemStack {

    private final ItemMeta meta;

    public ItemBuilder(Material type) {
        super(type);
        meta = super.getItemMeta();
    }

    public ItemBuilder lore(String @NotNull ... lore) {

        ArrayList<Component> l = new ArrayList<>();
        for (String s : lore) {
            l.add(Component.text(s.replace("&", "§")));
        }
        meta.lore(l);


        super.setItemMeta(meta);
        return this;
    }

    public ItemBuilder displayName(@NotNull String name) {
        meta.displayName(Component.text(name.replace("&", "§")));
        super.setItemMeta(meta);
        return this;
    }

    public ItemBuilder enchant(Object... enchantsAndLevels) {
        if (enchantsAndLevels.length % 2 != 0) {
            throw new IllegalArgumentException("You must provide pairs of enchantments and levels.");
        }

        for (int i = 0; i < enchantsAndLevels.length; i += 2) {
            if (!(enchantsAndLevels[i] instanceof Enchantment enchantment)) {
                throw new IllegalArgumentException("Expected Enchantment at index " + i + " of enchantsAndLevels.");
            }
            if (!(enchantsAndLevels[i + 1] instanceof Integer)) {
                throw new IllegalArgumentException("Expected Integer at index " + (i + 1) + " of enchantsAndLevels.");
            }

            int level = (int) enchantsAndLevels[i + 1];
            meta.addEnchant(enchantment, level, true);
        }

        super.setItemMeta(meta);

        return this;
    }

    public ItemBuilder customModelData(int model) {
        meta.setCustomModelData(model);
        super.setItemMeta(meta);
        return this;
    }

    public ItemBuilder flag(ItemFlag... flags) {
        meta.addItemFlags(flags);
        super.setItemMeta(meta);
        return this;
    }
    @Deprecated
    public ItemBuilder nbt(String key, String data) {
        NBTBuilder nbt = new NBTBuilder(key, data, meta);
        nbt.build();
        super.setItemMeta(meta);
        return this;
    }

    public ItemBuilder attribute(Attribute attribute, double amount) {
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "GenericAttribute", amount, AttributeModifier.Operation.ADD_NUMBER, getArmorType(this));
        if (meta.getAttributeModifiers() == null || meta.getAttributeModifiers().isEmpty())
            meta.setAttributeModifiers(getAttributes(this));
        meta.addAttributeModifier(attribute, modifier);
        super.setItemMeta(meta);
        return this;
    }


    public ItemBuilder amount(int amount) {
        setAmount(amount);
        super.setItemMeta(meta);
        return this;
    }

    public static Multimap<Attribute, AttributeModifier> getAttributes(ItemStack itemstack) {
        Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create();
        getArmorAttributes(itemstack).forEach(attributes::put);
        getItemAttributes(itemstack).forEach(attributes::put);

        return attributes;
    }

    public static ItemBuilder asBuilder(ItemStack item) {
        ItemBuilder builder = new ItemBuilder(item.getType());
        builder.setMeta(item.getItemMeta());

        return builder;
    }


    private void setMeta(ItemMeta meta) {
        super.setItemMeta(meta);
    }

    private static Multimap<Attribute, AttributeModifier> getItemAttributes(ItemStack item) {
        Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create();
        AttributeModifier damage;
        AttributeModifier speed;

        EquipmentSlot eq = getArmorType(item);


        speed = switch (item.getType()) {
            case WOODEN_AXE, STONE_AXE ->
                    new AttributeModifier(UUID.randomUUID(), "generic.speed", -3.2, AttributeModifier.Operation.ADD_NUMBER, eq);
            case WOODEN_SWORD, GOLDEN_SWORD, NETHERITE_SWORD, DIAMOND_SWORD, IRON_SWORD, STONE_SWORD ->
                    new AttributeModifier(UUID.randomUUID(), "generic.speed", -2.4, AttributeModifier.Operation.ADD_NUMBER, eq);
            case WOODEN_SHOVEL, GOLDEN_SHOVEL, NETHERITE_SHOVEL, NETHERITE_AXE, DIAMOND_SHOVEL, DIAMOND_AXE, IRON_SHOVEL, GOLDEN_AXE, WOODEN_HOE, GOLDEN_HOE, STONE_SHOVEL ->
                    new AttributeModifier(UUID.randomUUID(), "generic.speed", -3, AttributeModifier.Operation.ADD_NUMBER, eq);
            case WOODEN_PICKAXE, GOLDEN_PICKAXE, NETHERITE_PICKAXE, DIAMOND_PICKAXE, IRON_PICKAXE, STONE_PICKAXE ->
                    new AttributeModifier(UUID.randomUUID(), "generic.speed", -2.8, AttributeModifier.Operation.ADD_NUMBER, eq);
            case STONE_HOE ->
                    new AttributeModifier(UUID.randomUUID(), "generic.speed", -2, AttributeModifier.Operation.ADD_NUMBER, eq);
            case IRON_AXE ->
                    new AttributeModifier(UUID.randomUUID(), "generic.speed", -3.1, AttributeModifier.Operation.ADD_NUMBER, eq);
            case IRON_HOE ->
                    new AttributeModifier(UUID.randomUUID(), "generic.speed", -1, AttributeModifier.Operation.ADD_NUMBER, eq);
            default ->
                    new AttributeModifier(UUID.randomUUID(), "generic.speed", 0, AttributeModifier.Operation.ADD_NUMBER, eq);
        };

        damage = switch (item.getType()) {
            case WOODEN_AXE, DIAMOND_SWORD, GOLDEN_AXE ->
                    new AttributeModifier(UUID.randomUUID(), "generic.attack", 7, AttributeModifier.Operation.ADD_NUMBER, eq);
            case WOODEN_SWORD, GOLDEN_SWORD, IRON_PICKAXE ->
                    new AttributeModifier(UUID.randomUUID(), "generic.attack", 4, AttributeModifier.Operation.ADD_NUMBER, eq);
            case WOODEN_SHOVEL, GOLDEN_SHOVEL ->
                    new AttributeModifier(UUID.randomUUID(), "generic.attack", 2.5, AttributeModifier.Operation.ADD_NUMBER, eq);
            case WOODEN_HOE, GOLDEN_HOE, DIAMOND_HOE, NETHERITE_HOE, IRON_HOE, STONE_HOE ->
                    new AttributeModifier(UUID.randomUUID(), "generic.attack", 1, AttributeModifier.Operation.ADD_NUMBER, eq);
            case WOODEN_PICKAXE, GOLDEN_PICKAXE ->
                    new AttributeModifier(UUID.randomUUID(), "generic.attack", 2, AttributeModifier.Operation.ADD_NUMBER, eq);
            case STONE_AXE, DIAMOND_AXE, IRON_AXE ->
                    new AttributeModifier(UUID.randomUUID(), "generic.attack", 9, AttributeModifier.Operation.ADD_NUMBER, eq);
            case STONE_SWORD, DIAMOND_PICKAXE ->
                    new AttributeModifier(UUID.randomUUID(), "generic.attack", 5, AttributeModifier.Operation.ADD_NUMBER, eq);
            case STONE_SHOVEL ->
                    new AttributeModifier(UUID.randomUUID(), "generic.attack", 3.5, AttributeModifier.Operation.ADD_NUMBER, eq);
            case STONE_PICKAXE ->
                    new AttributeModifier(UUID.randomUUID(), "generic.attack", 3, AttributeModifier.Operation.ADD_NUMBER, eq);
            case IRON_SWORD, NETHERITE_PICKAXE ->
                    new AttributeModifier(UUID.randomUUID(), "generic.attack", 6, AttributeModifier.Operation.ADD_NUMBER, eq);
            case IRON_SHOVEL ->
                    new AttributeModifier(UUID.randomUUID(), "generic.attack", 4.5, AttributeModifier.Operation.ADD_NUMBER, eq);
            case DIAMOND_SHOVEL ->
                    new AttributeModifier(UUID.randomUUID(), "generic.attack", 5.5, AttributeModifier.Operation.ADD_NUMBER, eq);
            case NETHERITE_AXE ->
                    new AttributeModifier(UUID.randomUUID(), "generic.attack", 10, AttributeModifier.Operation.ADD_NUMBER, eq);
            case NETHERITE_SWORD ->
                    new AttributeModifier(UUID.randomUUID(), "generic.attack", 8, AttributeModifier.Operation.ADD_NUMBER, eq);
            case NETHERITE_SHOVEL ->
                    new AttributeModifier(UUID.randomUUID(), "generic.attack", 6.5, AttributeModifier.Operation.ADD_NUMBER, eq);
            default ->
                    new AttributeModifier(UUID.randomUUID(), "generic.attack", 0, AttributeModifier.Operation.ADD_NUMBER, eq);
        };

        attributes.put(Attribute.GENERIC_ATTACK_DAMAGE, damage);
        attributes.put(Attribute.GENERIC_ATTACK_SPEED, speed);

        return attributes;
    }


    private static Multimap<Attribute, AttributeModifier> getArmorAttributes(ItemStack item) {
        Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create();
        AttributeModifier armorModifier = null;
        AttributeModifier toughnessModifier = null;
        EquipmentSlot eq = getArmorType(item);
        switch (item.getType()) {
            case NETHERITE_HELMET, NETHERITE_BOOTS -> {
                armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 3, AttributeModifier.Operation.ADD_NUMBER, eq);
                toughnessModifier = new AttributeModifier(UUID.randomUUID(), "generic.tough", 3D, AttributeModifier.Operation.ADD_NUMBER, eq);
            }
            case NETHERITE_CHESTPLATE -> {
                armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 8, AttributeModifier.Operation.ADD_NUMBER, eq);
                toughnessModifier = new AttributeModifier(UUID.randomUUID(), "generic.tough", 3D, AttributeModifier.Operation.ADD_NUMBER, eq);
            }
            case NETHERITE_LEGGINGS -> {
                armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 6, AttributeModifier.Operation.ADD_NUMBER, eq);
                toughnessModifier = new AttributeModifier(UUID.randomUUID(), "generic.tough", 3D, AttributeModifier.Operation.ADD_NUMBER, eq);
            }
            case DIAMOND_HELMET, DIAMOND_BOOTS -> {
                armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 3, AttributeModifier.Operation.ADD_NUMBER, eq);
                toughnessModifier = new AttributeModifier(UUID.randomUUID(), "generic.tough", 2D, AttributeModifier.Operation.ADD_NUMBER, eq);
            }
            case DIAMOND_CHESTPLATE -> {
                armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 8, AttributeModifier.Operation.ADD_NUMBER, eq);
                toughnessModifier = new AttributeModifier(UUID.randomUUID(), "generic.tough", 2D, AttributeModifier.Operation.ADD_NUMBER, eq);
            }
            case DIAMOND_LEGGINGS -> {
                armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 6, AttributeModifier.Operation.ADD_NUMBER, eq);
                toughnessModifier = new AttributeModifier(UUID.randomUUID(), "generic.tough", 2D, AttributeModifier.Operation.ADD_NUMBER, eq);
            }
            case IRON_HELMET, IRON_BOOTS, LEATHER_LEGGINGS, GOLDEN_HELMET, CHAINMAIL_HELMET ->
                    armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 2, AttributeModifier.Operation.ADD_NUMBER, eq);
            case IRON_CHESTPLATE ->
                    armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 6, AttributeModifier.Operation.ADD_NUMBER, eq);
            case IRON_LEGGINGS, GOLDEN_CHESTPLATE, CHAINMAIL_CHESTPLATE ->
                    armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 5, AttributeModifier.Operation.ADD_NUMBER, eq);
            case GOLDEN_LEGGINGS, LEATHER_CHESTPLATE ->
                    armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 3, AttributeModifier.Operation.ADD_NUMBER, eq);
            case GOLDEN_BOOTS, LEATHER_BOOTS, LEATHER_HELMET, CHAINMAIL_BOOTS ->
                    armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 1, AttributeModifier.Operation.ADD_NUMBER, eq);
            case CHAINMAIL_LEGGINGS ->
                    armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 4, AttributeModifier.Operation.ADD_NUMBER, eq);
            default -> {
            }
        }
        attributes.put(Attribute.GENERIC_ARMOR, armorModifier);
        if (toughnessModifier != null) attributes.put(Attribute.GENERIC_ARMOR_TOUGHNESS, toughnessModifier);
        return attributes;
    }

    private static EquipmentSlot getArmorType(ItemStack item) {
        EquipmentSlot equipmentSlot;
        Material material = item.getType();
        if (material.name().toLowerCase().contains("chestplate")) equipmentSlot = EquipmentSlot.CHEST;
        else if (material.name().toLowerCase().contains("helmet")) equipmentSlot = EquipmentSlot.HEAD;
        else if (material.name().toLowerCase().contains("leggings")) equipmentSlot = EquipmentSlot.LEGS;
        else if (material.name().toLowerCase().contains("boots")) equipmentSlot = EquipmentSlot.FEET;
        else equipmentSlot = EquipmentSlot.HAND;
        return equipmentSlot;
    }

    private void createData(byte data) {
        super.setData(super.getType().getNewData(data));
    }

}

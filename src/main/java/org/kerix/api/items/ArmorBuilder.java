package org.kerix.api.items;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.kerix.api.items.nbt.NBTBuilder;

import java.util.UUID;

public class ArmorBuilder extends ItemBuilder{


    public ArmorBuilder(Material mat) {
        super(mat);
    }

    @Override
    public ArmorBuilder displayName(@NotNull String name) {
        super.displayName(name);
        return this;
    }

    @Override
    public ArmorBuilder lore(String @NotNull ... lore) {
        super.lore(lore);
        return this;
    }

    @Override
    public ItemBuilder customModelData(int model){
        super.customModelData(model);
        return this;
    }
    @Override
    public ItemBuilder flag(ItemFlag... flags){
        super.flag(flags);
        return this;
    }
    @Override
    public ItemBuilder nbt(String key , String data){
        super.nbt(key, data);
        return this;
    }
    @Override
    public ItemBuilder attribute(Attribute attribute, double amount) {
        super.attribute(attribute , amount);
        return this;
    }

    @Override
    public ItemBuilder amount(int amount){
        super.amount(amount);
        return this;
    }
    public ArmorBuilder leatherColor(Color color){
        if(!getType().name().toLowerCase().contains("leather") && getType()!=Material.LEATHER) return this;

        LeatherArmorMeta armorMeta = (LeatherArmorMeta) getItemMeta();
        armorMeta.setColor(color);


        setItemMeta(armorMeta);
        return this;
    }

}

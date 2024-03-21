package org.kerix.api.items;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

public class ArmorBuilder extends ItemBuilder{


    private Color leatherColor;

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

    public ArmorBuilder leatherColor(Color color){
        if(!getItem().getType().name().toLowerCase().contains("leather") && getItem().getType()!=Material.LEATHER) return this;

        leatherColor = color;
        return this;
    }

    @Override
    public ItemStack build() {
        ItemStack itemStack = super.build();
        if (itemStack.getType().name().toLowerCase().contains("leather") && leatherColor != null) {
            LeatherArmorMeta armorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            armorMeta.setColor(leatherColor);
            itemStack.setItemMeta(armorMeta);
        }
        return itemStack;
    }
}

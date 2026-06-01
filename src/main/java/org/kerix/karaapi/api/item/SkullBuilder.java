package org.kerix.karaapi.api.item;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.kerix.karaapi.paper.item.PaperItems;

import java.util.Objects;

public final class SkullBuilder extends ItemBuilder {

    private SkullBuilder(ItemStack item) {
        super(item);
    }

    public static SkullBuilder create() {
        return new SkullBuilder(PaperItems.create(Material.PLAYER_HEAD, 1));
    }

    public static SkullBuilder of(OfflinePlayer owner) {
        return create().owner(owner);
    }

    public SkullBuilder owner(OfflinePlayer owner) {
        Objects.requireNonNull(owner, "owner");

        meta(SkullMeta.class, meta -> meta.setOwningPlayer(owner));
        return this;
    }

    @Override
    public SkullBuilder amount(int amount) {
        super.amount(amount);
        return this;
    }

    @Override
    public SkullBuilder name(String name) {
        super.name(name);
        return this;
    }

    @Override
    public SkullBuilder lore(String... lines) {
        super.lore(lines);
        return this;
    }

    @Override
    public SkullBuilder unbreakable(boolean value) {
        super.unbreakable(value);
        return this;
    }

    @Override
    public SkullBuilder customModelData(Integer data) {
        super.customModelData(data);
        return this;
    }
}

package org.kerix.karaapi.api.menu;

import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public record PaginationControls(
        ItemStack previous,
        ItemStack next,
        ItemStack back,
        int previousSlot,
        int nextSlot,
        int backSlot
) {

    public PaginationControls {
        Objects.requireNonNull(previous, "previous");
        Objects.requireNonNull(next, "next");
        Objects.requireNonNull(back, "back");
    }
}
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

        previous = previous.clone();
        next = next.clone();
        back = back == null ? null : back.clone();

        validateSlot(previousSlot, "previousSlot");
        validateSlot(nextSlot, "nextSlot");

        if (back != null) {
            validateSlot(backSlot, "backSlot");
        }
    }

    public static PaginationControls of(
            ItemStack previous,
            ItemStack next,
            int previousSlot,
            int nextSlot
    ) {
        return new PaginationControls(previous, next, null, previousSlot, nextSlot, -1);
    }

    public static PaginationControls withBack(
            ItemStack previous,
            ItemStack next,
            ItemStack back,
            int previousSlot,
            int nextSlot,
            int backSlot
    ) {
        return new PaginationControls(previous, next, back, previousSlot, nextSlot, backSlot);
    }

    public boolean hasBack() {
        return back != null && backSlot >= 0;
    }

    private static void validateSlot(int slot, String name) {
        if (slot < 0 || slot >= 54) {
            throw new IllegalArgumentException(name + " must be between 0 and 53.");
        }
    }
}
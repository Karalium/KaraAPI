package org.kerix.karaapi.api.item.custom;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.kerix.karaapi.api.item.ItemProvider;
import org.kerix.karaapi.api.requirement.Requirement;
import org.kerix.karaapi.api.requirement.RequirementResult;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface CustomItem extends ItemProvider {

    String id();

    @Override
    ItemStack build();

    default ItemStack create() {
        return build();
    }

    default List<Requirement<Player>> requirements() {
        return List.of();
    }

    default Optional<Duration> cooldown() {
        return Optional.empty();
    }

    default RequirementResult check(Player player) {
        for (Requirement<Player> requirement : requirements()) {
            RequirementResult result = requirement.check(player);

            if (result.denied()) {
                return result;
            }
        }

        return RequirementResult.allow();
    }

    default void onInteract(CustomItemInteract event) {
    }

    default void onAttack(CustomItemAttack event) {
    }

    default void onInventoryClick(CustomItemInventoryClick event) {
    }

    static CustomItemBuilder item(String id) {
        return new CustomItemBuilder(id);
    }
}

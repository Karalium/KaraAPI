package org.kerix.karaapi.api.item.custom;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.kerix.karaapi.api.item.ItemProvider;
import org.kerix.karaapi.api.requirement.Requirement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class CustomItemBuilder {

    private final String id;
    private final List<Requirement<Player>> requirements = new ArrayList<>();

    private ItemProvider item;
    private Duration cooldown;

    private CustomItemAction<CustomItemInteract> interactAction = event -> {};
    private CustomItemAction<CustomItemAttack> attackAction = event -> {};
    private CustomItemAction<CustomItemInventoryClick> inventoryClickAction = event -> {};

    CustomItemBuilder(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public CustomItemBuilder item(ItemProvider item) {
        this.item = Objects.requireNonNull(item, "item");
        return this;
    }

    public CustomItemBuilder item(ItemStack item) {
        Objects.requireNonNull(item, "item");
        return item(() -> item.clone());
    }

    public CustomItemBuilder requires(Requirement<Player> requirement) {
        requirements.add(Objects.requireNonNull(requirement, "requirement"));
        return this;
    }

    public CustomItemBuilder cooldown(Duration cooldown) {
        this.cooldown = Objects.requireNonNull(cooldown, "cooldown");

        if (cooldown.isNegative() || cooldown.isZero()) {
            throw new IllegalArgumentException("Cooldown must be positive.");
        }

        return this;
    }

    public CustomItemBuilder onInteract(CustomItemAction<CustomItemInteract> action) {
        this.interactAction = Objects.requireNonNull(action, "action");
        return this;
    }

    public CustomItemBuilder onAttack(CustomItemAction<CustomItemAttack> action) {
        this.attackAction = Objects.requireNonNull(action, "action");
        return this;
    }

    public CustomItemBuilder onInventoryClick(CustomItemAction<CustomItemInventoryClick> action) {
        this.inventoryClickAction = Objects.requireNonNull(action, "action");
        return this;
    }

    public CustomItem build() {
        if (item == null) {
            throw new IllegalStateException("Custom item '" + id + "' has no item provider.");
        }

        return new BuiltCustomItem(
                id,
                item,
                List.copyOf(requirements),
                cooldown,
                interactAction,
                attackAction,
                inventoryClickAction
        );
    }

    private record BuiltCustomItem(
            String id,
            ItemProvider item,
            List<Requirement<Player>> requirements,
            Duration cooldownDuration,
            CustomItemAction<CustomItemInteract> interactAction,
            CustomItemAction<CustomItemAttack> attackAction,
            CustomItemAction<CustomItemInventoryClick> inventoryClickAction
    ) implements CustomItem {

        @Override
        public ItemStack build() {
            return item.build();
        }

        @Override
        public List<Requirement<Player>> requirements() {
            return requirements;
        }

        @Override
        public Optional<Duration> cooldown() {
            return Optional.ofNullable(cooldownDuration);
        }

        @Override
        public void onInteract(CustomItemInteract event) {
            interactAction.handle(event);
        }

        @Override
        public void onAttack(CustomItemAttack event) {
            attackAction.handle(event);
        }

        @Override
        public void onInventoryClick(CustomItemInventoryClick event) {
            inventoryClickAction.handle(event);
        }
    }
}

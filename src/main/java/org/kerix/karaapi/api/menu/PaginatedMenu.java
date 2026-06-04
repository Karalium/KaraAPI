package org.kerix.karaapi.api.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.kerix.karaapi.api.item.ItemProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;

public final class PaginatedMenu {

    private final Component title;
    private final int rows;
    private final List<MenuItem> content;
    private final int[] contentSlots;
    private final PaginationControls controls;
    private final IntFunction<Component> titleRenderer;

    private PaginatedMenu(
            Component title,
            int rows,
            List<MenuItem> content,
            int[] contentSlots,
            PaginationControls controls,
            IntFunction<Component> titleRenderer
    ) {
        this.title = Objects.requireNonNull(title, "title");
        this.rows = validateRows(rows);
        this.content = List.copyOf(content);
        this.contentSlots = contentSlots.clone();
        this.controls = Objects.requireNonNull(controls, "controls");
        this.titleRenderer = titleRenderer;

        if (contentSlots.length == 0) {
            throw new IllegalArgumentException("Paginated menu needs at least one content slot.");
        }
    }

    public static PaginatedMenu of(
            Component title,
            int rows,
            List<MenuItem> content,
            int[] contentSlots,
            PaginationControls controls
    ) {
        return new PaginatedMenu(
                title,
                rows,
                content == null ? List.of() : content,
                contentSlots,
                controls,
                null
        );
    }

    public static PaginatedMenu ofItems(
            Component title,
            int rows,
            List<ItemStack> content,
            int[] contentSlots,
            PaginationControls controls
    ) {
        List<MenuItem> items = new ArrayList<>();

        if (content != null) {
            for (ItemStack item : content) {
                items.add(MenuItem.of(item));
            }
        }

        return of(title, rows, items, contentSlots, controls);
    }

    public static PaginatedMenu ofProviders(
            Component title,
            int rows,
            List<? extends ItemProvider> content,
            int[] contentSlots,
            PaginationControls controls
    ) {
        List<MenuItem> items = new ArrayList<>();

        if (content != null) {
            for (ItemProvider provider : content) {
                items.add(MenuItem.of(provider.build()));
            }
        }

        return of(title, rows, items, contentSlots, controls);
    }

    public PaginatedMenu titleRenderer(IntFunction<Component> renderer) {
        return new PaginatedMenu(
                title,
                rows,
                content,
                contentSlots,
                controls,
                Objects.requireNonNull(renderer, "renderer")
        );
    }

    public Menu page(int page) {
        int safePage = Math.max(0, Math.min(page, maxPage()));
        Component renderedTitle = titleRenderer == null ? title : titleRenderer.apply(safePage);

        MenuBuilder builder = Menu.builder(renderedTitle, rows);

        int start = safePage * contentSlots.length;

        for (int index = 0; index < contentSlots.length; index++) {
            int contentIndex = start + index;

            if (contentIndex >= content.size()) {
                break;
            }

            builder.slot(contentSlots[index], content.get(contentIndex));
        }

        int currentPage = safePage;

        builder.slot(
                controls.previousSlot(),
                controls.previous(),
                click -> {
                    if (currentPage > 0) {
                        click.openNextTick(page(currentPage - 1));
                    }
                }
        );

        builder.slot(
                controls.nextSlot(),
                controls.next(),
                click -> {
                    if (currentPage < maxPage()) {
                        click.openNextTick(page(currentPage + 1));
                    }
                }
        );

        if (controls.hasBack()) {
            builder.slot(
                    controls.backSlot(),
                    controls.back(),
                    MenuClick::closeNextTick
            );
        }

        return builder.build();
    }

    public int maxPage() {
        if (content.isEmpty()) {
            return 0;
        }

        return Math.max(
                0,
                (int) Math.ceil(content.size() / (double) contentSlots.length) - 1
        );
    }

    public int pageCount() {
        return maxPage() + 1;
    }

    public List<MenuItem> content() {
        return List.copyOf(content);
    }

    public static int[] rectangularSlots(
            int startRow,
            int endRow,
            int startColumn,
            int endColumn
    ) {
        if (startRow < 0 || endRow > 5 || startRow > endRow) {
            throw new IllegalArgumentException("Invalid row range.");
        }

        if (startColumn < 0 || endColumn > 8 || startColumn > endColumn) {
            throw new IllegalArgumentException("Invalid column range.");
        }

        List<Integer> slots = new ArrayList<>();

        for (int row = startRow; row <= endRow; row++) {
            for (int column = startColumn; column <= endColumn; column++) {
                slots.add(row * 9 + column);
            }
        }

        return slots.stream().mapToInt(Integer::intValue).toArray();
    }

    private static int validateRows(int rows) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Menu rows must be between 1 and 6.");
        }

        return rows;
    }
}

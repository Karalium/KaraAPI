package org.kerix.karaapi.api.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.kerix.karaapi.paper.item.PaperItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class WrittenBookBuilder extends ItemBuilder {

    private final List<Component> pages = new ArrayList<>();

    private Component title = Component.text("Book");
    private Component author = Component.text("Unknown");
    private BookMeta.Generation generation = BookMeta.Generation.ORIGINAL;

    private WrittenBookBuilder(ItemStack item) {
        super(item);
    }

    public static WrittenBookBuilder create() {
        return new WrittenBookBuilder(PaperItems.create(Material.WRITTEN_BOOK, 1));
    }

    public static WrittenBookBuilder titled(String title) {
        return create().title(title);
    }

    public WrittenBookBuilder title(String title) {
        this.title = Component.text(title == null ? "" : title);
        return this;
    }

    public WrittenBookBuilder title(Component title) {
        this.title = Objects.requireNonNull(title, "title");
        return this;
    }

    public WrittenBookBuilder author(String author) {
        this.author = Component.text(author == null ? "" : author);
        return this;
    }

    public WrittenBookBuilder author(Component author) {
        this.author = Objects.requireNonNull(author, "author");
        return this;
    }

    public WrittenBookBuilder generation(BookMeta.Generation generation) {
        this.generation = generation;
        return this;
    }

    public WrittenBookBuilder page(Component page) {
        pages.add(Objects.requireNonNull(page, "page"));
        return this;
    }

    public WrittenBookBuilder page(Consumer<BookPageBuilder> editor) {
        Objects.requireNonNull(editor, "editor");

        BookPageBuilder builder = new BookPageBuilder();
        editor.accept(builder);

        return page(builder.build());
    }

    public WrittenBookBuilder pages(List<Component> pages) {
        Objects.requireNonNull(pages, "pages");

        this.pages.clear();
        this.pages.addAll(pages);

        return this;
    }

    public WrittenBookBuilder clearPages() {
        pages.clear();
        return this;
    }

    public List<Component> pages() {
        return List.copyOf(pages);
    }

    @Override
    public ItemStack build() {
        ItemStack built = item.clone();

        BookMeta meta = (BookMeta) built.getItemMeta();

        if (meta == null) {
            throw new IllegalStateException("Written book has no BookMeta.");
        }

        meta.title(title);
        meta.author(author);
        meta.setGeneration(generation);

        if (!pages.isEmpty()) {
            meta.addPages(pages.toArray(Component[]::new));
        }

        built.setItemMeta(meta);

        return built;
    }
}

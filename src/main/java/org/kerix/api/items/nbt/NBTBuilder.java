package org.kerix.api.items.nbt;


import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.kerix.api.KaraAPI;

@Deprecated
public class NBTBuilder {
    private final String key;
    private final String data;
    private final ItemMeta meta;

    public NBTBuilder(@NotNull String key, String data, ItemMeta meta) {
        this.key = key;
        this.data = data;
        this.meta = meta;
    }
    public void build() {
        String[] args = key.toLowerCase().split("\\.");
        PersistentDataContainer container = meta.getPersistentDataContainer();
        PersistentDataContainer current = container.getAdapterContext().newPersistentDataContainer();
        PersistentDataContainer newcontainer = container.getAdapterContext().newPersistentDataContainer();

        for (int i = args.length - 1; i >= 0; i--) {
            if (i == args.length - 1) {
                newcontainer.set(NamespacedKey.minecraft(args[i]), PersistentDataType.STRING, data);
            } else if (i > 0) {
                current.set(NamespacedKey.minecraft(args[i]), PersistentDataType.TAG_CONTAINER, newcontainer);
                newcontainer = current;
            } else {
                container.set(NamespacedKey.minecraft(args[i]), PersistentDataType.TAG_CONTAINER, current);
            }
        }
    }

}



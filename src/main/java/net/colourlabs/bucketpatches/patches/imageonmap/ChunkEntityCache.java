package net.colourlabs.bucketpatches.patches.imageonmap;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class ChunkEntityCache {
    private static final Map<Chunk, Entity[]> cache = new HashMap<>();

    public static Entity[] getEntities(Chunk chunk) {
        Entity[] entities = cache.get(chunk);
        if (entities == null) {
            entities = chunk.getEntities();
            cache.put(chunk, entities);
        }
        return entities;
    }

    public static void clear() {
        cache.clear();
    }
}

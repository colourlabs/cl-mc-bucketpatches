package net.colourlabs.bucketpatches.patches.imageonmap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AsyncIOService {
    private static final long SAVE_DELAY = 200L;
    private static Method IS_MODIFIED;
    private static Method SAVE;
    private static Field PLAYER_MAPS;
    private static Field AUTOSAVE_TASK;
    private static boolean cancelledOriginal = false;

    public static void start(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> tick(plugin), SAVE_DELAY, SAVE_DELAY);
    }

    private static void tick(Plugin plugin) {
        try {
            Class<?> mapManager = Class.forName("fr.moribus.imageonmap.map.MapManager");
            initReflection(mapManager);

            cancelOriginalTask(mapManager);

            List<?> playerMaps = (List<?>) PLAYER_MAPS.get(null);
            List<Object> modified = new ArrayList<>();

            synchronized (playerMaps) {
                for (Object store : playerMaps) {
                    if ((boolean) IS_MODIFIED.invoke(store)) {
                        modified.add(store);
                    }
                }
            }

            if (modified.isEmpty()) return;

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                for (Object store : modified) {
                    try {
                        SAVE.invoke(store);
                    } catch (Exception e) {
                        plugin.getLogger().warning("AsyncIO: Failed to save map store: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            plugin.getLogger().warning("AsyncIO: Autosave error: " + e.getMessage());
        }
    }

    private static void initReflection(Class<?> mapManager) throws Exception {
        if (IS_MODIFIED == null) {
            IS_MODIFIED = Class.forName("fr.moribus.imageonmap.map.PlayerMapStore")
                .getMethod("isModified");
        }
        if (SAVE == null) {
            Method save = Class.forName("fr.moribus.imageonmap.map.PlayerMapStore")
                .getDeclaredMethod("save");
            save.setAccessible(true);
            SAVE = save;
        }
        if (PLAYER_MAPS == null) {
            PLAYER_MAPS = mapManager.getDeclaredField("playerMaps");
            PLAYER_MAPS.setAccessible(true);
        }
        if (AUTOSAVE_TASK == null) {
            AUTOSAVE_TASK = mapManager.getDeclaredField("autosaveTask");
            AUTOSAVE_TASK.setAccessible(true);
        }
    }

    private static void cancelOriginalTask(Class<?> mapManager) throws Exception {
        if (cancelledOriginal) return;
        Object task = AUTOSAVE_TASK.get(null);
        if (task != null) {
            Method cancel = task.getClass().getMethod("cancel");
            cancel.invoke(task);
            AUTOSAVE_TASK.set(null, null);
        }
        cancelledOriginal = true;
    }
}

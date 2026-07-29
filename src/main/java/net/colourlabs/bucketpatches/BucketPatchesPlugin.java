package net.colourlabs.bucketpatches;

import net.colourlabs.bucketpatches.image.DitheredRenderer;
import net.colourlabs.bucketpatches.patches.advancedteleport.AdvancedTeleportPatches;
import net.colourlabs.bucketpatches.patches.advancedteleport.SQLManagerPatches;
import net.colourlabs.bucketpatches.patches.greentext.GreenTextPatches;
import net.colourlabs.bucketpatches.patches.coreprotect.ConnectionPool;
import net.colourlabs.bucketpatches.patches.coreprotect.DatabasePatches;
import net.colourlabs.bucketpatches.patches.coreprotect.LookupCommandPatches;
import net.colourlabs.bucketpatches.patches.coreprotect.PlayerListenerPatches;
import net.colourlabs.bucketpatches.patches.coreprotect.ProcessPatches;
import net.colourlabs.bucketpatches.patches.coreprotect.RollbackRestoreCommandPatches;
import net.colourlabs.bucketpatches.patches.coreprotect.ThreadPool;
import net.colourlabs.bucketpatches.patches.imageonmap.AsyncIOService;
import net.colourlabs.bucketpatches.patches.imageonmap.GettextPOTranslatorPatches;
import net.colourlabs.bucketpatches.patches.imageonmap.ImageOnMapPatches;
import net.colourlabs.bucketpatches.patches.imageonmap.PosterWallPatches;

import net.colourlabs.patchthebucket.api.PatchTheBucketAPI;
import net.colourlabs.patchthebucket.api.registry.PatchRegistry;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BiConsumer;

public class BucketPatchesPlugin extends JavaPlugin {
    private static final String[][] VERSION_CHECKS = {
            { "CoreProtect", "2.14.4" },
            { "ImageOnMap", "3.1" },
            { "AdvancedTeleport", "5.6.14" },
            { "GreenText", "1.0-SNAPSHOT" },
    };

    @Override
    public void onEnable() {
        PatchTheBucketAPI api = getServer().getServicesManager().load(PatchTheBucketAPI.class);

        if (api == null) {
            getLogger().severe("PatchTheBucket not found! Download it to use BucketPatches.");
            getPluginLoader().disablePlugin(this);
            return;
        }

        checkPluginVersions();

        PatchRegistry registry = api.getRegistry();

        registry.registerAnnotated(AdvancedTeleportPatches.class);
        registry.registerAnnotated(SQLManagerPatches.class);
        registry.registerAnnotated(GreenTextPatches.class);
        registry.registerAnnotated(ImageOnMapPatches.class);
        registry.registerAnnotated(PosterWallPatches.class);
        registry.registerAnnotated(GettextPOTranslatorPatches.class);
        registry.registerAnnotated(DatabasePatches.class);
        registry.registerAnnotated(ProcessPatches.class);
        registry.registerAnnotated(LookupCommandPatches.class);
        registry.registerAnnotated(RollbackRestoreCommandPatches.class);
        registry.registerAnnotated(PlayerListenerPatches.class);

        getServer().getServicesManager().register(
                BiConsumer.class,
                new DitheredRenderer(),
                this,
                ServicePriority.Normal);

        AsyncIOService.start(this);

        getLogger().info("BucketPatches initialized - all plugin patches registered.");
    }

    private void checkPluginVersions() {
        Server server = getServer();
        for (String[] check : VERSION_CHECKS) {
            String name = check[0];
            String expected = check[1];
            Plugin plugin = server.getPluginManager().getPlugin(name);
            if (plugin == null) {
                getLogger().warning(name + " not found, its patches will never apply.");
            } else if (!expected.equals(plugin.getDescription().getVersion())) {
                getLogger().warning(name + " version " + plugin.getDescription().getVersion()
                        + " does not match expected " + expected + " - patches may be stale.");
            }
        }
    }

    @Override
    public void onDisable() {
        ConnectionPool.shutdown();
        ThreadPool.shutdown();
    }
}

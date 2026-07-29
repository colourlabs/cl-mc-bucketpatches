# cl-mc-bucketpatches

These are some general patches for plugins running on `mc.colourlabs.net` (1.12.2) for some old plugins to work well on modern Java (21+) and general perforamance improvements. Requires [PatchTheBucket](https://github.com/colourlabs/patchthebucket) and specific plugins for use.

## plugins patched

* CoreProtect (VERSION: 2.14.4)
* ImageOnMap (VERSION: 3.1)
* AdvancedTeleport (VERSION: 5.6.14)
* GreenText (VERSION: 1.0-SNAPSHOT)

## patches

### CoreProtect

* `Database.getConnection` - remove `Class.forName(...).newInstance()` for Java 9+ compatibility
* `Database.loadUserID` - `user LIKE ?` -> `user = ?` for case-sensitive matching (fixes wrong rowid on case-colliding names)
* `Process.validateConnection` - `safeIsValid` catches `SQLFeatureNotSupportedException` on SQLite
* `PlayerListener`, `LookupCommand`, `RollbackRestoreCommand`, `new Thread(runnable).start()` -> shared `ThreadPool` executor

### ImageOnMap

* `Renderer.render` - full rewrite with Floyd-Steinberg dithering via `DitheredRenderer` service (basically makes rendered images more clearer due to MC's limited map color pallete)
* `GettextPOTranslator.getPluralIndex` - bypasses Nashorn `ScriptEngine` with `PluralEval` pure-Java evaluator (fixes JDK 15+ support)
* `PosterWall.isValid`, `getMatchingMapFrames`, `getMapFrameAt`, `getEmptyFrameAt` - `ChunkEntityCache` injection reduces redundant `chunk.getEntities()` calls
* `AsyncIOService` - scheduled task replaces broken autosave, runs saves off the main thread

### AdvancedTeleport

* `SQLManager.implementConnection` - `com.mysql.jdbc.Driver` → `com.mysql.cj.jdbc.Driver`
* `UpdateChecker.getURLResults` - full rewrite (broken in modern Java)
* `UpdateChecker.getInternalTimestamp` - full rewrite (broken in modern Java)

### GreenText

* `GreenText.onPlayerChat` - full rewrite to avoid `StringIndexOutOfBoundsException`
* added support for ">type<" text being orange.

## build

Get 1.12.2 SpigotAPI jars in MavenLocal via BuildTools and with a decently recent Java JDK version, run `./gradlew build` and place the built jar in the `plugins/` directory with PatchTheBucket and the plugins mentioneds

## license

MIT
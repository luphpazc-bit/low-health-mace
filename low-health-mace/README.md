# Low Health Mace (Fabric 1.21.11)

Shows a floating, spinning, 3D mace half a block above the head of any player below 10 hearts.

## Build
1. Install JDK 21.
2. Easiest way to get the Gradle wrapper: generate a blank 1.21.11 project at https://fabricmc.net/develop/template ,
   then copy `gradle/` and `gradlew*` from it into this folder. (Or run `gradle wrapper --gradle-version 9.2.1` if you have Gradle installed.)
3. `./gradlew build`  -> jar in `build/libs/`
4. Drop the jar (not the -sources jar) into `mods/` together with Fabric API.
5. Test: `./gradlew runClient`

## Tweak
Constants at the top of `LowHealthMaceFeatureRenderer.java`:
- `HEALTH_THRESHOLD` (20 = 10 hearts; strictly "below")
- `GAP_ABOVE_HEAD`, `MACE_SCALE`, `SPIN_DEGREES_PER_TICK`

package net.korin.colorfulwater;

import com.google.gson.JsonParser;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.registries.Registries;

import java.util.HashMap;
import java.util.Map;

public class WaterColorLoader extends SimplePreparableReloadListener<Void> {
    public static final Map<ResourceKey<Biome>, int[]> OVERRIDES = new HashMap<>();

    @Override
    protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        return null;
    }

    @Override
    protected void apply(Void prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        OVERRIDES.clear();

        var resource = resourceManager.getResource(Identifier.fromNamespaceAndPath("colorfulwater", "water_colors.json"));
        resource.ifPresent(res -> {
            try (var reader = res.openAsReader()) {
                var json = JsonParser.parseReader(reader).getAsJsonObject();

                for (var entry : json.entrySet()) {
                    String biomeName = entry.getKey();
                    var colorArray = entry.getValue().getAsJsonArray();

                    int r = colorArray.get(0).getAsInt();
                    int g = colorArray.get(1).getAsInt();
                    int b = colorArray.get(2).getAsInt();
                    int a = colorArray.size() > 3 ? colorArray.get(3).getAsInt() : 255;

                    ResourceKey<Biome> biomeKey = ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("minecraft", biomeName));
                    OVERRIDES.put(biomeKey, new int[]{r, g, b, a});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
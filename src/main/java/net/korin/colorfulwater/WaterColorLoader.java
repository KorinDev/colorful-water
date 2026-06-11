package net.korin.colorfulwater;

import com.google.gson.JsonObject;
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
    public static final Map<ResourceKey<Biome>, WaterColorData> OVERRIDES = new HashMap<>();

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
                    var value = entry.getValue();

                    WaterColorData data = new WaterColorData();

                    if (value.isJsonArray()) {
                        // Legacy format: [R, G, B] or [R, G, B, A]
                        var arr = value.getAsJsonArray();
                        data.r = arr.get(0).getAsInt();
                        data.g = arr.get(1).getAsInt();
                        data.b = arr.get(2).getAsInt();
                        data.a = arr.size() > 3 ? arr.get(3).getAsInt() : 255;
                        // Fog defaults to same as water
                        data.fogR = data.r;
                        data.fogG = data.g;
                        data.fogB = data.b;
                        data.fogDistance = -1; // Use vanilla default
                    } else {
                        // New format: { "water": [R,G,B,A], "fog": [R,G,B], "fog_distance": N }
                        var obj = value.getAsJsonObject();

                        if (obj.has("water")) {
                            var waterArr = obj.get("water").getAsJsonArray();
                            data.r = waterArr.get(0).getAsInt();
                            data.g = waterArr.get(1).getAsInt();
                            data.b = waterArr.get(2).getAsInt();
                            data.a = waterArr.size() > 3 ? waterArr.get(3).getAsInt() : 255;
                        }

                        if (obj.has("fog")) {
                            var fogArr = obj.get("fog").getAsJsonArray();
                            data.fogR = fogArr.get(0).getAsInt();
                            data.fogG = fogArr.get(1).getAsInt();
                            data.fogB = fogArr.get(2).getAsInt();
                        } else {
                            data.fogR = data.r;
                            data.fogG = data.g;
                            data.fogB = data.b;
                        }

                        if (obj.has("fog_distance")) {
                            data.fogDistance = obj.get("fog_distance").getAsInt();
                        } else {
                            data.fogDistance = -1;
                        }
                    }

                    ResourceKey<Biome> biomeKey = ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("minecraft", biomeName));
                    OVERRIDES.put(biomeKey, data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static class WaterColorData {
        public int r, g, b, a;
        public int fogR, fogG, fogB;
        public int fogDistance; // -1 means use vanilla default
    }
}
package net.korin.colorfulwater.mixin;

import net.korin.colorfulwater.WaterColorLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeColors.class)
public class BiomeColorsMixin {

    @Inject(method = "getAverageWaterColor", at = @At("HEAD"), cancellable = true)
    private static void onGetWaterColor(BlockAndTintGetter level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {

        Minecraft client = Minecraft.getInstance();
        int blendRadius = client.options.biomeBlendRadius().get();

        if (blendRadius == 0) {
            Holder<Biome> biomeHolder = level.getBiomeFabric(pos);
            var biomeKey = biomeHolder.unwrapKey().orElse(null);
            if (biomeKey != null) {
                int[] rgba = WaterColorLoader.OVERRIDES.get(biomeKey);
                if (rgba != null) {
                    cir.setReturnValue(ARGB.color(rgba[3], rgba[0], rgba[1], rgba[2]));
                }
            }
            return;
        }

        int r = 0, g = 0, b = 0;
        int samples = 0;

        for (int dx = -blendRadius; dx <= blendRadius; dx++) {
            for (int dz = -blendRadius; dz <= blendRadius; dz++) {
                BlockPos samplePos = pos.offset(dx, 0, dz);
                Holder<Biome> biomeHolder = level.getBiomeFabric(samplePos);
                var biomeKey = biomeHolder.unwrapKey().orElse(null);

                if (biomeKey != null) {
                    int[] rgba = WaterColorLoader.OVERRIDES.get(biomeKey);
                    if (rgba != null) {
                        r += rgba[0];
                        g += rgba[1];
                        b += rgba[2];
                    } else {
                        int vanilla = biomeHolder.value().getWaterColor();
                        r += ((vanilla >> 16) & 0xFF);
                        g += ((vanilla >> 8) & 0xFF);
                        b += (vanilla & 0xFF);
                    }
                    samples++;
                }
            }
        }

        if (samples > 0) {
            cir.setReturnValue(ARGB.color(255, r / samples, g / samples, b / samples));
        }
    }
}
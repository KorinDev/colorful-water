package net.korin.colorfulwater.mixin;

import net.korin.colorfulwater.WaterColorLoader;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.environment.WaterFogEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WaterFogEnvironment.class)
public class WaterFogEnvironmentMixin {

    @Inject(method = "getBaseColor", at = @At("RETURN"), cancellable = true)
    private void onGetBaseColor(ClientLevel level, Camera camera, int renderDistance, float partialTicks, CallbackInfoReturnable<Integer> cir) {
        BlockPos pos = camera.blockPosition();

        if (level == null || pos == null) {
            return;
        }

        int r = 0, g = 0, b = 0;
        int samples = 0;
        int blendRadius = Minecraft.getInstance().options.biomeBlendRadius().get();

        for (int dx = -blendRadius; dx <= blendRadius; dx++) {
            for (int dz = -blendRadius; dz <= blendRadius; dz++) {
                BlockPos samplePos = pos.offset(dx, 0, dz);
                Holder<Biome> biomeHolder = level.getBiome(samplePos);
                if (biomeHolder == null || biomeHolder.unwrapKey().isEmpty()) continue;

                var biomeKey = biomeHolder.unwrapKey().orElse(null);
                if (biomeKey != null) {
                    WaterColorLoader.WaterColorData data = WaterColorLoader.OVERRIDES.get(biomeKey);
                    if (data != null) {
                        r += data.fogR;
                        g += data.fogG;
                        b += data.fogB;
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
            cir.cancel();
        }
    }
}
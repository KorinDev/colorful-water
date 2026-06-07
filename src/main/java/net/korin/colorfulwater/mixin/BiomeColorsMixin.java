package net.korin.colorfulwater.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
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
        Holder<Biome> biomeHolder = level.getBiomeFabric(pos);
        String biomeName = Minecraft.getInstance().player.registryAccess()
                    .lookupOrThrow(Registries.BIOME)
                    .getKey(biomeHolder.value())
                    .getPath();


        if (biomeName.equals("mushroom_fields")) {
            cir.setReturnValue(ARGB.color(255, 255, 0, 255));
        }
    }
}

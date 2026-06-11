package net.korin.colorfulwater.mixin;

import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Biome.class)
public interface BiomeMixin {
    @Accessor("attributes")
    EnvironmentAttributeMap getAttributes();

    @Accessor("attributes")
    void setAttributes(EnvironmentAttributeMap attributes);
}

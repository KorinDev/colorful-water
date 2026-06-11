package net.korin.colorfulwater;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorfulWater implements ModInitializer {
	public static final String MOD_ID = "colorful-water";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {


        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
                Identifier.fromNamespaceAndPath("colorfulwater", "water_color_loader"),
                new WaterColorLoader());

        var modContainer = FabricLoader.getInstance().getModContainer(MOD_ID);
        if (modContainer.isPresent()) {
            ResourceLoader.registerBuiltinPack(
                    Identifier.fromNamespaceAndPath("colorfulwater", "bedrockwater"),
                    modContainer.get(),
                    Component.literal("Bedrock Water"),
                    PackActivationType.NORMAL
            );
        }

		LOGGER.info("Loaded.");


	}
}
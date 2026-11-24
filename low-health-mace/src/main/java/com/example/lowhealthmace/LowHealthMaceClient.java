package com.example.lowhealthmace;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;

public class LowHealthMaceClient implements ClientModInitializer {

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void onInitializeClient() {
		LivingEntityFeatureRendererRegistrationCallback.EVENT.register(
			(entityType, entityRenderer, registrationHelper, context) -> {
				// Only attach to player renderers (both the wide and slim skin renderers).
				if (entityRenderer instanceof PlayerEntityRenderer) {
					registrationHelper.register(new LowHealthMaceFeatureRenderer(
						(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel>) entityRenderer,
						context.getItemModelManager()
					));
				}
			}
		);
	}
}

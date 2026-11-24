package com.example.lowhealthmace;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;

/**
 * Draws a spinning, 3D mace floating above the head of every player whose
 * health is below the threshold.
 */
public class LowHealthMaceFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {

	/** 10 hearts = 20 health points. The mace shows while health is strictly below this. */
	private static final float HEALTH_THRESHOLD = 20.0f;

	/** Gap between the top of the head and the bottom of the mace, in blocks. */
	private static final float GAP_ABOVE_HEAD = 0.5f;

	/** Size multiplier for the mace (1.0 = the vanilla "ground item" size, about half a block). */
	private static final float MACE_SCALE = 1.0f;

	/** Degrees per tick of spin. Set to 0 to stop the spinning. */
	private static final float SPIN_DEGREES_PER_TICK = 4.0f;

	/** The player renderer applies a 0.9375 scale before feature renderers run. */
	private static final float PLAYER_MODEL_SCALE = 0.9375f;

	private final ItemModelManager itemModelManager;
	private final ItemStack maceStack = new ItemStack(Items.MACE);

	public LowHealthMaceFeatureRenderer(
		FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context,
		ItemModelManager itemModelManager
	) {
		super(context);
		this.itemModelManager = itemModelManager;
	}

	@Override
	public void render(
		MatrixStack matrices,
		OrderedRenderCommandQueue queue,
		int light,
		PlayerEntityRenderState state,
		float limbAngle,
		float limbDistance
	) {
		ClientWorld world = MinecraftClient.getInstance().world;
		if (world == null) {
			return;
		}

		Entity entity = world.getEntityById(state.id);
		if (!(entity instanceof PlayerEntity player)) {
			return;
		}
		if (player.isSpectator() || player.isInvisible() || player.getHealth() >= HEALTH_THRESHOLD) {
			return;
		}

		// Fresh state each frame: the render queue keeps references until it is flushed.
		ItemRenderState itemState = new ItemRenderState();
		this.itemModelManager.clearAndUpdate(
			itemState, this.maceStack, ItemDisplayContext.GROUND, world, null, player.getId()
		);
		if (itemState.isEmpty()) {
			return;
		}

		float bob = (float) Math.sin(state.age * 0.1f) * 0.05f;
		// The GROUND transform puts the item's centre ~0.125 above the origin, and the sprite is ~0.5 tall.
		float originHeight = player.getHeight() + GAP_ABOVE_HEAD + 0.125f * MACE_SCALE + bob;

		matrices.push();
		// Feature renderers draw in a flipped, scaled model space with the origin 1.501 units above the feet.
		matrices.translate(0.0, 1.501 - originHeight / PLAYER_MODEL_SCALE, 0.0);
		// Undo the (-1, -1, 1) flip and the player scale so the mace is upright and normal sized.
		float s = MACE_SCALE / PLAYER_MODEL_SCALE;
		matrices.scale(-s, -s, s);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.age * SPIN_DEGREES_PER_TICK));

		itemState.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, 0);
		matrices.pop();
	}
}

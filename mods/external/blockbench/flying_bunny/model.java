// Generated from flying_bunny/source/Flying Bunny.gltf as a Class A blockbench package source.
// Runtime texture: copy ./textures/gltf_embedded_0.png -> ./model.png unchanged.
// Important texture note: the PNG embedded inside the GLTF is vertically flipped relative to
// ./textures/gltf_embedded_0.png. Use the sidecar PNG in ./textures/ as the runtime texture.
// No model-anim.java is emitted because this source package has no animation clips.

package com.example.mod;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class model extends EntityModel<Entity> {
	private final ModelPart root;

	public model(ModelPart root) {
		this.root = root.getChild("root");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData bone14 = root.addChild("bone14", ModelPartBuilder.create(), ModelTransform.of(0.0F, -24.0F, -5.0F, 0.6545F, 0.0F, 0.0F));
		ModelPartData cube_0 = bone14.addChild("cube_0", ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(-4.0F, -0.504F, -1.134F, 8.0F, 0.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData bone13 = root.addChild("bone13", ModelPartBuilder.create(), ModelTransform.of(0.0F, -23.0F, -5.0F, 1.3963F, 0.0F, 0.0F));
		ModelPartData cube_1 = bone13.addChild("cube_1", ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(-4.0F, -0.504F, -1.134F, 8.0F, 0.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData bone12 = root.addChild("bone12", ModelPartBuilder.create(), ModelTransform.pivot(-2.0F, -24.0F, -3.0F));
		ModelPartData cube_2 = bone12.addChild("cube_2", ModelPartBuilder.create().uv(12, 48).mirrored().cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData bone11 = root.addChild("bone11", ModelPartBuilder.create(), ModelTransform.of(2.5373F, -19.0F, -3.1566F, 0.0F, -1.3526F, 0.0F));
		ModelPartData cube_3 = bone11.addChild("cube_3", ModelPartBuilder.create().uv(0, 16).cuboid(-1.5383F, -6.0F, -0.887F, 4.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData bone = root.addChild("bone", ModelPartBuilder.create(), ModelTransform.of(-2.5373F, -19.0F, -3.1566F, 0.0F, 1.3526F, 0.0F));
		ModelPartData cube_4 = bone.addChild("cube_4", ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.4617F, -6.0F, -0.887F, 4.0F, 8.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData bone10 = root.addChild("bone10", ModelPartBuilder.create(), ModelTransform.of(4.0F, -18.0F, 2.0F, 0.5236F, 0.0F, 0.0F));
		ModelPartData cube_5 = bone10.addChild("cube_5", ModelPartBuilder.create().uv(40, 28).cuboid(-3.0F, -3.866F, 0.2321F, 4.0F, 8.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData bone9 = root.addChild("bone9", ModelPartBuilder.create(), ModelTransform.of(-6.0F, -34.0F, 2.0F, -0.7854F, 0.9163F, 1.4399F));
		ModelPartData cube_6 = bone9.addChild("cube_6", ModelPartBuilder.create().uv(20, 20).cuboid(-4.7934F, -0.4345F, -5.5695F, 12.0F, 0.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData bone8 = root.addChild("bone8", ModelPartBuilder.create(), ModelTransform.of(-6.0F, -36.0F, 6.0F, 0.3491F, -0.3491F, -1.0908F));
		ModelPartData cube_7 = bone8.addChild("cube_7", ModelPartBuilder.create().uv(0, 12).cuboid(-4.3023F, -0.4199F, -4.9138F, 8.0F, 0.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData bone7 = root.addChild("bone7", ModelPartBuilder.create(), ModelTransform.of(0.0F, -30.0F, -7.0F, -0.7854F, 0.0F, 0.0F));
		ModelPartData cube_8 = bone7.addChild("cube_8", ModelPartBuilder.create().uv(44, 44).mirrored().cuboid(-2.0F, -3.2444F, -0.4495F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData bone5 = root.addChild("bone5", ModelPartBuilder.create(), ModelTransform.of(6.0F, -36.0F, 6.0F, 0.3491F, 0.3491F, 1.0908F));
		ModelPartData cube_9 = bone5.addChild("cube_9", ModelPartBuilder.create().uv(0, 12).mirrored().cuboid(-3.6977F, -0.4199F, -4.9138F, 8.0F, 0.0F, 12.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData bone3 = root.addChild("bone3", ModelPartBuilder.create(), ModelTransform.of(-4.0F, -18.0F, 2.0F, 0.5236F, 0.0F, 0.0F));
		ModelPartData cube_10 = bone3.addChild("cube_10", ModelPartBuilder.create().uv(40, 28).mirrored().cuboid(-1.0F, -3.866F, 0.2321F, 4.0F, 8.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData bone2 = root.addChild("bone2", ModelPartBuilder.create(), ModelTransform.of(6.0F, -34.0F, 2.0F, -0.7854F, -0.9163F, -1.4399F));
		ModelPartData cube_11 = bone2.addChild("cube_11", ModelPartBuilder.create().uv(20, 20).mirrored().cuboid(-7.2066F, -0.4345F, -5.5695F, 12.0F, 0.0F, 8.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData part_26 = root.addChild("part_26", ModelPartBuilder.create(), ModelTransform.pivot(1.0F, -4.0F, 0.0F));
		ModelPartData cube_12 = part_26.addChild("cube_12", ModelPartBuilder.create().uv(0, 8).cuboid(0.0F, -12.0F, 4.0F, 4.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData part_27 = root.addChild("part_27", ModelPartBuilder.create(), ModelTransform.pivot(-2.0F, -26.0F, 7.0F));
		ModelPartData cube_13 = part_27.addChild("cube_13", ModelPartBuilder.create().uv(44, 8).mirrored().cuboid(0.0F, -4.0F, 0.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData part_28 = root.addChild("part_28", ModelPartBuilder.create(), ModelTransform.pivot(-1.0F, -4.0F, 0.0F));
		ModelPartData cube_14 = part_28.addChild("cube_14", ModelPartBuilder.create().uv(0, 8).mirrored().cuboid(-4.0F, -12.0F, 4.0F, 4.0F, 8.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData part_29 = root.addChild("part_29", ModelPartBuilder.create(), ModelTransform.pivot(2.0F, -25.0F, -3.0F));
		ModelPartData cube_15 = part_29.addChild("cube_15", ModelPartBuilder.create().uv(28, 48).mirrored().cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData part_30 = root.addChild("part_30", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -32.0F, -3.0F));
		ModelPartData cube_16 = part_30.addChild("cube_16", ModelPartBuilder.create().uv(28, 8).mirrored().cuboid(-2.0F, -2.0F, -8.0F, 4.0F, 4.0F, 8.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData part_31 = root.addChild("part_31", ModelPartBuilder.create(), ModelTransform.pivot(-6.0F, -22.0F, -5.0F));
		ModelPartData cube_17 = part_31.addChild("cube_17", ModelPartBuilder.create().uv(24, 32).mirrored().cuboid(4.0F, -8.0F, 0.0F, 4.0F, 8.0F, 8.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData part_32 = root.addChild("part_32", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -20.0F, 0.0F));
		ModelPartData cube_18 = part_32.addChild("cube_18", ModelPartBuilder.create().uv(0, 24).mirrored().cuboid(-4.0F, -8.0F, 0.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}
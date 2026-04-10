// Generated for glare from source/model.gltf
// Runtime texture is produced by texture_fix.py as model.png
// Texture size after fix: 128x64
// The supplied textures/gltf_embedded_0.png is vertically flipped relative to the
// image embedded in source/model.gltf, so do not use it directly at runtime.

package com.example.mod;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;

public class model extends EntityModel<Entity> {
	private final ModelPart root;

	public model(ModelPart root) {
		this.root = root.getChild("root");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData node_31 = root.addChild("node_31", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, -0.0F, 0.0F));

		ModelPartData head = node_31.addChild("head", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, -19.0F, 0.0F));

		ModelPartData headtop = head.addChild("headtop", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, -0.0F, 0.0F));

		ModelPartData headtop_2 = headtop.addChild("headtop_2", ModelPartBuilder.create().uv(41, 20).mirrored().cuboid(-7.2F, -14.2F, -7.2F, 14.4F, 8.4F, 14.4F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(0.0F, 8.0F, 0.0F));

		ModelPartData headtop_3 = headtop.addChild("headtop_3", ModelPartBuilder.create().uv(41, 22).mirrored().cuboid(-7.0F, -11.001F, -7.0F, 14.0F, 0.001F, 14.0F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(0.0F, 8.0F, 0.0F));

		ModelPartData angry = head.addChild("angry", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, -1.0F, -6.0F));

		ModelPartData angry_2 = angry.addChild("angry_2", ModelPartBuilder.create().uv(95, 1).mirrored().cuboid(-3.0F, -12.0F, -6.11F, 5.0F, 4.0F, 0.001F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(3.0F, 11.0F, 6.0F));

		ModelPartData angry_3 = angry.addChild("angry_3", ModelPartBuilder.create().uv(100, 1).mirrored().cuboid(-6.0F, -12.0F, -6.11F, 5.0F, 4.0F, 0.001F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(1.0F, 11.0F, 6.0F));

		ModelPartData eye = head.addChild("eye", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, 1.0F, -6.0F));

		ModelPartData eye_2 = eye.addChild("eye_2", ModelPartBuilder.create().uv(126, 0).mirrored().cuboid(1.9F, -9.1F, -6.4F, 1.2F, 1.2F, 0.2F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(0.0F, 8.0F, 6.0F));

		ModelPartData eye_3 = eye.addChild("eye_3", ModelPartBuilder.create().uv(126, 0).mirrored().cuboid(-3.1F, -9.1F, -6.4F, 1.2F, 1.2F, 0.2F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(0.0F, 8.0F, 6.0F));

		ModelPartData Dancing = head.addChild("Dancing", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, 0.0F, 4.0F));

		ModelPartData cube = Dancing.addChild("cube", ModelPartBuilder.create().uv(95, 5).mirrored().cuboid(-4.05F, -2.05F, -1.15F, 5.1F, 2.1F, 0.1F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(-2.0F, 1.0F, -5.0F));

		ModelPartData cube_2 = Dancing.addChild("cube_2", ModelPartBuilder.create().uv(95, 5).mirrored().cuboid(-4.05F, -2.05F, -1.15F, 5.1F, 2.1F, 0.1F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(4.0F, 1.0F, -5.0F));

		ModelPartData Eyenormal = head.addChild("Eyenormal", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, 0.0F, 1.0F));

		ModelPartData cube_3 = Eyenormal.addChild("cube_3", ModelPartBuilder.create().uv(95, 7).mirrored().cuboid(-4.05F, -2.05F, 1.05F, 5.1F, 4.1F, 0.1F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.of(-4.0F, 0.0F, -4.0F, 3.1416F, -0.0F, 3.1416F));

		ModelPartData cube_4 = Eyenormal.addChild("cube_4", ModelPartBuilder.create().uv(95, 7).mirrored().cuboid(-4.05F, -2.05F, 2.85F, 5.1F, 4.1F, 0.1F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(4.0F, 0.0F, -8.0F));

		ModelPartData body = head.addChild("body", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, 4.0F, 0.0F));

		ModelPartData bone2 = body.addChild("bone2", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, -1.0F, 0.0F));

		ModelPartData bone3 = bone2.addChild("bone3", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, -0.0F, 0.0F));

		ModelPartData bone4 = bone3.addChild("bone4", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, -0.0F, 0.0F));

		ModelPartData bone4_2 = bone4.addChild("bone4_2", ModelPartBuilder.create().uv(0, 47).mirrored().cuboid(-3.2F, -1.2F, -3.2F, 6.4F, 11.4F, 6.4F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(0.0F, 5.0F, 0.0F));

		ModelPartData bone3_2 = bone3.addChild("bone3_2", ModelPartBuilder.create().uv(0, 45).mirrored().cuboid(-4.0F, -3.0F, -4.0F, 8.0F, 11.0F, 8.0F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(0.0F, 5.0F, 0.0F));

		ModelPartData bone2_2 = bone2.addChild("bone2_2", ModelPartBuilder.create().uv(0, 26).mirrored().cuboid(-5.0F, -4.0F, -5.0F, 10.0F, 9.0F, 10.0F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(0.0F, 5.0F, 0.0F));

		ModelPartData body_2 = body.addChild("body_2", ModelPartBuilder.create().uv(35, 42).mirrored().cuboid(-6.0F, -6.0F, -6.0F, 12.0F, 8.0F, 12.0F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(0.0F, 4.0F, 0.0F));

		ModelPartData head_2 = head.addChild("head_2", ModelPartBuilder.create().uv(56, 0).mirrored().cuboid(-6.0F, -11.0F, -6.1F, 12.0F, 7.0F, 13.0F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(0.0F, 8.0F, 0.0F));

		ModelPartData head_3 = head.addChild("head_3", ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(-7.0F, -14.0F, -7.0F, 14.0F, 13.0F, 14.0F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(0.0F, 8.0F, 0.0F));

		ModelPartData head_4 = head.addChild("head_4", ModelPartBuilder.create().uv(41, 22).mirrored().cuboid(-7.0F, -4.001F, -7.0F, 14.0F, 0.001F, 14.0F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(0.0F, 8.0F, 0.0F));

		ModelPartData particle = head.addChild("particle", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, 15.0F, 0.0F));

		ModelPartData particle_2 = particle.addChild("particle_2", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, -0.0F, 0.0F));

		ModelPartData angry_particle = head.addChild("angry_particle", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, 1.0F, 0.0F));

		ModelPartData angry_particle_2 = angry_particle.addChild("angry_particle_2", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, -0.0F, 0.0F));

		return TexturedModelData.of(modelData, 128, 64);
	}

	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}
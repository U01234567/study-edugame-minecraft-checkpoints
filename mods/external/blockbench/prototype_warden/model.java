// Generated for prototype_warden from source/model.gltf
// Runtime texture is produced by texture_fix.py as model.png
// Texture size after fix: 128x128

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
	private final ModelPart body;
	private final ModelPart torso;
	private final ModelPart head;
	private final ModelPart leftArm;
	private final ModelPart rightArm;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;

	public model(ModelPart root) {
		this.root = root.getChild("root");
		this.body = this.root.getChild("body");
		this.torso = this.body.getChild("torso");
		this.head = this.body.getChild("head");
		this.leftArm = this.body.getChild("leftArm");
		this.rightArm = this.body.getChild("rightArm");
		this.leftLeg = this.root.getChild("leftLeg");
		this.rightLeg = this.root.getChild("rightLeg");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData body = root.addChild("body", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, -10.0F, 4.0F));

		ModelPartData torso = body.addChild("torso",
			ModelPartBuilder.create()
				.uv(0, 0).cuboid(-9.0F, -29.0F, 4.0F, 18.0F, 22.0F, 17.0F, new Dilation(0.0F)),
			ModelTransform.of(0.0F, 10.0F, -4.0F, 0.3491F, 0.0F, 0.0F));

		ModelPartData head = body.addChild("head",
			ModelPartBuilder.create()
				.uv(70, 0).cuboid(-7.0F, -19.0F, -7.0F, 14.0F, 20.0F, 14.0F, new Dilation(0.0F))
				.uv(66, 77).cuboid(7.0F, -16.0F, 0.0F, 9.0F, 9.0F, 0.001F, new Dilation(0.0F))
				.uv(48, 77).cuboid(-16.0F, -16.0F, 0.0F, 9.0F, 9.0F, 0.001F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, -18.0F, -10.0F));

		ModelPartData leftArm = body.addChild("leftArm",
			ModelPartBuilder.create()
				.uv(28, 39).cuboid(-1.0F, -3.0F, -4.0F, 6.0F, 30.0F, 8.0F, new Dilation(0.0F)),
			ModelTransform.pivot(10.0F, -18.0F, -4.0F));

		ModelPartData rightArm = body.addChild("rightArm",
			ModelPartBuilder.create()
				.uv(0, 39).cuboid(-5.0F, -3.0F, -4.0F, 6.0F, 30.0F, 8.0F, new Dilation(0.0F)),
			ModelTransform.pivot(-10.0F, -18.0F, -4.0F));

		ModelPartData leftLeg = root.addChild("leftLeg",
			ModelPartBuilder.create()
				.uv(24, 77).cuboid(-3.0F, 0.0F, -2.0F, 7.0F, 10.0F, 5.0F, new Dilation(0.0F)),
			ModelTransform.pivot(4.0F, -10.0F, 3.0F));

		ModelPartData rightLeg = root.addChild("rightLeg",
			ModelPartBuilder.create()
				.uv(0, 77).cuboid(-4.0F, 0.0F, -2.0F, 7.0F, 10.0F, 5.0F, new Dilation(0.0F)),
			ModelTransform.pivot(-4.0F, -10.0F, 3.0F));

		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}
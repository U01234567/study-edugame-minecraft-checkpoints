// Generated for tv_man from source/model.gltf
// Runtime texture is produced by texture_fix.py as model.png
// Texture size after fix: 96x64

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
	private final ModelPart waist;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	public model(ModelPart root) {
		this.root = root.getChild("root");
		this.waist = this.root.getChild("waist");
		this.body = this.waist.getChild("body");
		this.head = this.body.getChild("head");
		this.rightArm = this.body.getChild("rightArm");
		this.leftArm = this.body.getChild("leftArm");
		this.rightLeg = this.body.getChild("rightLeg");
		this.leftLeg = this.body.getChild("leftLeg");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData waist = root.addChild("waist", ModelPartBuilder.create(),
			ModelTransform.pivot(0.0F, -12.0F, 0.0F));

		ModelPartData body = waist.addChild("body",
			ModelPartBuilder.create()
				// Opaque inner torso so the alpha-cut outer shell does not look hollow.
				.uv(66, 0).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(-0.05F))
				// Original outer torso shell.
				.uv(1, 44).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, -12.0F, 0.0F));

		ModelPartData head = body.addChild("head",
			ModelPartBuilder.create()
				.uv(1, 0).cuboid(-3.0F, -7.0F, 3.0F, 6.0F, 6.0F, 1.0F, new Dilation(0.0F))
				.uv(1, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 7.0F, new Dilation(0.0F))
				.uv(0, 3).cuboid(-4.0F, -8.0F, -5.0F, 1.0F, 8.0F, 2.0F, new Dilation(0.0F))
				.uv(1, 2).cuboid(3.0F, -8.0F, -5.0F, 1.0F, 8.0F, 2.0F, new Dilation(0.0F))
				.uv(1, 0).cuboid(-4.0F, -8.0F, -5.0F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F))
				.uv(1, 0).cuboid(-4.0F, -1.5F, -5.0F, 8.0F, 1.5F, 2.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData rightArm = body.addChild("rightArm",
			ModelPartBuilder.create()
				// Opaque inner arm to stop shoulder/arm shell transparency from reading as missing geometry.
				.uv(66, 20).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 14.0F, 4.0F, new Dilation(-0.05F))
				// Original outer arm shell.
				.uv(23, 17).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 14.0F, 4.0F, new Dilation(0.0F)),
			ModelTransform.pivot(5.0F, 2.0F, 0.0F));

		ModelPartData leftArm = body.addChild("leftArm",
			ModelPartBuilder.create()
				.uv(66, 20).mirrored().cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 14.0F, 4.0F, new Dilation(-0.05F)).mirrored(false)
				.uv(23, 17).mirrored().cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 14.0F, 4.0F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(-5.0F, 2.0F, 0.0F));

		ModelPartData rightLeg = body.addChild("rightLeg",
			ModelPartBuilder.create()
				// Opaque inner leg so the masked shell does not vanish from side/back angles.
				.uv(66, 40).cuboid(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(-0.05F))
				// Original outer leg shell.
				.uv(25, 44).cuboid(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)),
			ModelTransform.pivot(1.9F, 12.0F, 0.0F));

		ModelPartData leftLeg = body.addChild("leftLeg",
			ModelPartBuilder.create()
				.uv(66, 40).mirrored().cuboid(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(-0.05F)).mirrored(false)
				.uv(25, 44).mirrored().cuboid(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(-1.9F, 12.0F, 0.0F));

		return TexturedModelData.of(modelData, 96, 64);
	}

	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}
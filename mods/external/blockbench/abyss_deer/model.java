// Reconstructed from abyss_deer/source/model.gltf for Minecraft 1.17+ (Yarn).
// Uses the original 64x64 UV layout; final runtime texture should be model.png.
// The creature is static in the source GLTF, so no model-anim.java is included.

package com.example.mod;

public class model extends EntityModel<Entity> {
	private final ModelPart root;

	public model(ModelPart root) {
		this.root = root.getChild("root");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData body = root.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData body_main = body.addChild("body_main", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -15.0F, -8.0F, 6.0F, 7.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 0.0F));
		ModelPartData body_neck = body.addChild("body_neck", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -13.0F, -6.0F, 0.43633F, 0.0F, 0.0F));
		ModelPartData mob_tail = root.addChild("mob_tail", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData tail = mob_tail.addChild("tail", ModelPartBuilder.create().uv(22, 23).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -13.0F, 7.0F, -0.74176F, 0.0F, 0.0F));

		ModelPartData left_leg2 = root.addChild("left_leg2", ModelPartBuilder.create(), ModelTransform.pivot(-1.25F, -8.0F, 6.0F));
		ModelPartData left_leg2_upper = left_leg2.addChild("left_leg2_upper", ModelPartBuilder.create().uv(16, 38).cuboid(0.0F, -6.0F, 0.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 8.0F, 0.0F));
		ModelPartData left_leg2_hoof = left_leg2.addChild("left_leg2_hoof", ModelPartBuilder.create().uv(28, 8).cuboid(0.0F, -4.0F, -2.0F, 3.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.25F, 2.25F, 0.25F));

		ModelPartData right_leg2 = root.addChild("right_leg2", ModelPartBuilder.create(), ModelTransform.pivot(1.25F, -8.0F, 6.0F));
		ModelPartData right_leg2_upper = right_leg2.addChild("right_leg2_upper", ModelPartBuilder.create().uv(8, 33).cuboid(-2.0F, -6.0F, 0.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 8.0F, 0.0F));
		ModelPartData right_leg2_hoof = right_leg2.addChild("right_leg2_hoof", ModelPartBuilder.create().uv(28, 0).cuboid(-3.0F, -4.0F, -2.0F, 3.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(2.25F, 2.25F, 0.25F));

		ModelPartData left_leg1_group = root.addChild("left_leg1_group", ModelPartBuilder.create(), ModelTransform.pivot(-1.25F, -8.75F, -6.5F));
		ModelPartData left_leg1 = left_leg1_group.addChild("left_leg1", ModelPartBuilder.create().uv(0, 33).cuboid(-3.25F, -10.0F, -7.5F, 2.0F, 10.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(1.25F, 8.75F, 6.5F));

		ModelPartData right_leg1_group = root.addChild("right_leg1_group", ModelPartBuilder.create(), ModelTransform.pivot(1.25F, -8.75F, -6.5F));
		ModelPartData right_leg1 = right_leg1_group.addChild("right_leg1", ModelPartBuilder.create().uv(32, 31).cuboid(1.25F, -10.0F, -7.5F, 2.0F, 10.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.25F, 8.75F, 6.5F));

		ModelPartData mob_head = root.addChild("mob_head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -18.5F, -8.5F));

		ModelPartData horn_left = mob_head.addChild("horn_left", ModelPartBuilder.create(), ModelTransform.pivot(-3.02443F, -7.59268F, -1.2413F));
		ModelPartData horn_left_base = horn_left.addChild("horn_left_base", ModelPartBuilder.create().uv(40, 32).cuboid(-0.5F, -5.0F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.02443F, 4.09268F, 1.2413F, 0.30543F, 0.0F, -0.5236F));
		ModelPartData horn_left_branch_back = horn_left.addChild("horn_left_branch_back", ModelPartBuilder.create().uv(28, 38).cuboid(-0.77644F, -5.19724F, -0.62439F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.55227F, 0.10546F, -1.1188F));
		ModelPartData horn_left_branch_front = horn_left.addChild("horn_left_branch_front", ModelPartBuilder.create().uv(38, 26).cuboid(0.00801F, -5.01167F, -0.99201F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.59622F, -1.08264F, -2.31155F, 0.97401F, -0.16378F, -0.9501F));
		ModelPartData horn_left_tine_upper = horn_left.addChild("horn_left_tine_upper", ModelPartBuilder.create().uv(16, 23).cuboid(0.25213F, -2.97576F, -1.04212F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.04324F, -1.85538F, -2.1988F, 0.95402F, 0.24933F, -0.9422F));
		ModelPartData horn_left_tine_lower = horn_left.addChild("horn_left_tine_lower", ModelPartBuilder.create().uv(0, 14).cuboid(-0.19787F, -0.97576F, -1.04212F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.04324F, -1.85538F, -2.1988F, 0.95111F, -0.07351F, -0.51875F));

		ModelPartData horn_right = mob_head.addChild("horn_right", ModelPartBuilder.create(), ModelTransform.pivot(3.02443F, -7.59268F, -1.2413F));
		ModelPartData horn_right_base = horn_right.addChild("horn_right_base", ModelPartBuilder.create().uv(24, 38).cuboid(-0.5F, -5.0F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.02443F, 4.09268F, 1.2413F, 0.30543F, 0.0F, 0.5236F));
		ModelPartData horn_right_tine_upper = horn_right.addChild("horn_right_tine_upper", ModelPartBuilder.create().uv(0, 12).cuboid(-4.25213F, -2.97576F, -1.04212F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.04324F, -1.85538F, -2.1988F, 0.95402F, -0.24933F, 0.9422F));
		ModelPartData horn_right_branch_front = horn_right.addChild("horn_right_branch_front", ModelPartBuilder.create().uv(0, 23).cuboid(-1.00801F, -5.01167F, -0.99201F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.59622F, -1.08264F, -2.31155F, 0.97401F, 0.16378F, 0.9501F));
		ModelPartData horn_right_tine_lower = horn_right.addChild("horn_right_tine_lower", ModelPartBuilder.create().uv(0, 10).cuboid(-3.80213F, -0.97576F, -1.04212F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.04324F, -1.85538F, -2.1988F, 0.95111F, 0.07351F, 0.51875F));
		ModelPartData horn_right_branch_back = horn_right.addChild("horn_right_branch_back", ModelPartBuilder.create().uv(10, 10).cuboid(-0.22356F, -5.19724F, -0.62439F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.55227F, -0.10546F, 1.1188F));

		ModelPartData head_main = mob_head.addChild("head_main", ModelPartBuilder.create().uv(0, 23).cuboid(-2.5F, -10.0F, -3.0F, 5.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 5.5F, 2.5F, 0.43633F, 0.0F, 0.0F));
		ModelPartData snout = mob_head.addChild("snout", ModelPartBuilder.create().uv(18, 31).cuboid(-1.5F, -9.0F, -7.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 5.5F, 2.5F, 0.43633F, 0.0F, 0.0F));
		ModelPartData ear_left = mob_head.addChild("ear_left", ModelPartBuilder.create().uv(32, 23).cuboid(-2.25F, -1.2093F, -0.6941F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, -3.5F, 1.0F, 0.43633F, 0.0F, 0.56723F));
		ModelPartData ear_right = mob_head.addChild("ear_right", ModelPartBuilder.create().uv(16, 25).cuboid(-0.75F, -1.2093F, -0.6941F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.5F, -3.5F, 1.0F, 0.43633F, 0.0F, -0.56723F));

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
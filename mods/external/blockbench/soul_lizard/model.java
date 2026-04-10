// Made with Blockbench-style output reconstructed from GLTF
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package com.example.mod;
   
public class soul_lizard extends EntityModel<Entity> {
	private final ModelPart root;
	private final ModelPart soul_lizard;
	private final ModelPart body;
	private final ModelPart body_main;
	private final ModelPart body_sail_left;
	private final ModelPart body_sail_right;
	private final ModelPart mob_tail;
	private final ModelPart tail_base;
	private final ModelPart tail_tip;
	private final ModelPart tail_sail_left;
	private final ModelPart tail_sail_right;
	private final ModelPart mob_head;
	private final ModelPart head_main;
	private final ModelPart left_leg;
	private final ModelPart left_leg_seg1;
	private final ModelPart left_leg_seg2;
	private final ModelPart left_leg_seg3a;
	private final ModelPart left_leg_seg3b;
	private final ModelPart right_leg;
	private final ModelPart right_leg_seg1;
	private final ModelPart right_leg_seg2;
	private final ModelPart right_leg_seg3a;
	private final ModelPart right_leg_seg3b;
	private final ModelPart right_arm;
	private final ModelPart right_arm_seg1;
	private final ModelPart right_arm_seg2;
	private final ModelPart right_arm_seg3a;
	private final ModelPart right_arm_seg3b;
	private final ModelPart left_arm;
	private final ModelPart left_arm_seg1;
	private final ModelPart left_arm_seg2;
	private final ModelPart left_arm_seg3a;
	private final ModelPart left_arm_seg3b;

	public soul_lizard(ModelPart root) {
		this.root = root.getChild("root");
		this.soul_lizard = this.root.getChild("soul_lizard");
		this.body = this.soul_lizard.getChild("body");
		this.body_main = this.body.getChild("body_main");
		this.body_sail_left = this.body.getChild("body_sail_left");
		this.body_sail_right = this.body.getChild("body_sail_right");
		this.mob_tail = this.soul_lizard.getChild("mob_tail");
		this.tail_base = this.mob_tail.getChild("tail_base");
		this.tail_tip = this.mob_tail.getChild("tail_tip");
		this.tail_sail_left = this.mob_tail.getChild("tail_sail_left");
		this.tail_sail_right = this.mob_tail.getChild("tail_sail_right");
		this.mob_head = this.soul_lizard.getChild("mob_head");
		this.head_main = this.mob_head.getChild("head_main");
		this.left_leg = this.soul_lizard.getChild("left_leg");
		this.left_leg_seg1 = this.left_leg.getChild("left_leg_seg1");
		this.left_leg_seg2 = this.left_leg.getChild("left_leg_seg2");
		this.left_leg_seg3a = this.left_leg.getChild("left_leg_seg3a");
		this.left_leg_seg3b = this.left_leg.getChild("left_leg_seg3b");
		this.right_leg = this.soul_lizard.getChild("right_leg");
		this.right_leg_seg1 = this.right_leg.getChild("right_leg_seg1");
		this.right_leg_seg2 = this.right_leg.getChild("right_leg_seg2");
		this.right_leg_seg3a = this.right_leg.getChild("right_leg_seg3a");
		this.right_leg_seg3b = this.right_leg.getChild("right_leg_seg3b");
		this.right_arm = this.soul_lizard.getChild("right_arm");
		this.right_arm_seg1 = this.right_arm.getChild("right_arm_seg1");
		this.right_arm_seg2 = this.right_arm.getChild("right_arm_seg2");
		this.right_arm_seg3a = this.right_arm.getChild("right_arm_seg3a");
		this.right_arm_seg3b = this.right_arm.getChild("right_arm_seg3b");
		this.left_arm = this.soul_lizard.getChild("left_arm");
		this.left_arm_seg1 = this.left_arm.getChild("left_arm_seg1");
		this.left_arm_seg2 = this.left_arm.getChild("left_arm_seg2");
		this.left_arm_seg3a = this.left_arm.getChild("left_arm_seg3a");
		this.left_arm_seg3b = this.left_arm.getChild("left_arm_seg3b");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData soul_lizard = root.addChild("soul_lizard", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData body = soul_lizard.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.25F, 0.0F));

		ModelPartData body_main = body.addChild("body_main", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -4.0F, -6.0F, 4.0F, 3.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData body_sail_left = body.addChild("body_sail_left", ModelPartBuilder.create().uv(0, 3).cuboid(0.0F, -2.0F, -6.0F, 0.0F, 2.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.0349F));

		ModelPartData body_sail_right = body.addChild("body_sail_right", ModelPartBuilder.create().uv(0, 3).cuboid(0.0F, -2.0F, -6.0F, 0.0F, 2.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.0349F));

		ModelPartData mob_tail = soul_lizard.addChild("mob_tail", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -2.0F, 6.0F));

		ModelPartData tail_base = mob_tail.addChild("tail_base", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -2.975F, 5.75F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, -6.0F));

		ModelPartData tail_tip = mob_tail.addChild("tail_tip", ModelPartBuilder.create().uv(0, 6).cuboid(-0.5F, -0.475F, -0.15F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.25F, 3.75F, -0.1309F, 0.0F, 0.0F));

		ModelPartData tail_sail_left = mob_tail.addChild("tail_sail_left", ModelPartBuilder.create().uv(10, 13).cuboid(0.0F, -1.725F, -2.025F, 0.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, 2.0F, 0.0F, 0.0F, 0.0349F));

		ModelPartData tail_sail_right = mob_tail.addChild("tail_sail_right", ModelPartBuilder.create().uv(10, 13).cuboid(0.0F, -1.725F, -2.025F, 0.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, 2.0F, 0.0F, 0.0F, -0.0349F));

		ModelPartData mob_head = soul_lizard.addChild("mob_head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -2.25F, -6.0F));

		ModelPartData head_main = mob_head.addChild("head_main", ModelPartBuilder.create().uv(0, 17).cuboid(-1.5F, -1.0F, -3.525F, 3.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

		ModelPartData left_leg = soul_lizard.addChild("left_leg", ModelPartBuilder.create(), ModelTransform.pivot(-2.0F, -1.5F, 4.0F));

		ModelPartData left_leg_seg1 = left_leg.addChild("left_leg_seg1", ModelPartBuilder.create().uv(20, 0).cuboid(-2.0F, -0.4F, -1.0F, 3.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.3927F, -0.3054F));

		ModelPartData left_leg_seg2 = left_leg.addChild("left_leg_seg2", ModelPartBuilder.create().uv(11, 24).cuboid(-1.8081F, -0.2107F, -0.511F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-1.625F, 0.375F, 0.4F, 0.0F, 0.0F, -0.0436F));

		ModelPartData left_leg_seg3a = left_leg.addChild("left_leg_seg3a", ModelPartBuilder.create().uv(18, 23).cuboid(-2.2331F, -0.1607F, -0.586F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.875F, 0.375F, 0.4F, 0.2639F, -1.0222F, -0.3279F));

		ModelPartData left_leg_seg3b = left_leg.addChild("left_leg_seg3b", ModelPartBuilder.create().uv(6, 23).cuboid(-2.1331F, -0.0857F, -0.511F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.875F, 0.375F, 0.4F, 0.0219F, -0.126F, -0.1965F));

		ModelPartData right_leg = soul_lizard.addChild("right_leg", ModelPartBuilder.create(), ModelTransform.pivot(2.0F, -1.5F, 4.0F));

		ModelPartData right_leg_seg1 = right_leg.addChild("right_leg_seg1", ModelPartBuilder.create().uv(16, 17).cuboid(-1.0F, -0.4F, -1.0F, 3.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.3927F, 0.3054F));

		ModelPartData right_leg_seg2 = right_leg.addChild("right_leg_seg2", ModelPartBuilder.create().uv(0, 23).cuboid(-0.1919F, -0.2107F, -0.511F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.625F, 0.375F, 0.4F, 0.0F, 0.0F, 0.0436F));

		ModelPartData right_leg_seg3a = right_leg.addChild("right_leg_seg3a", ModelPartBuilder.create().uv(13, 22).cuboid(0.2331F, -0.1607F, -0.586F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.875F, 0.375F, 0.4F, 0.2639F, 1.0222F, 0.3279F));

		ModelPartData right_leg_seg3b = right_leg.addChild("right_leg_seg3b", ModelPartBuilder.create().uv(20, 21).cuboid(0.1331F, -0.0857F, -0.511F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.875F, 0.375F, 0.4F, 0.0219F, 0.126F, 0.1965F));

		ModelPartData right_arm = soul_lizard.addChild("right_arm", ModelPartBuilder.create(), ModelTransform.pivot(2.0F, -1.5F, -4.0F));

		ModelPartData right_arm_seg1 = right_arm.addChild("right_arm_seg1", ModelPartBuilder.create().uv(20, 5).cuboid(-1.0F, -0.4F, 0.0F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.025F, 0.0F, -0.675F, -0.1745F, -0.8126F, 0.2553F));

		ModelPartData right_arm_seg2 = right_arm.addChild("right_arm_seg2", ModelPartBuilder.create().uv(20, 9).cuboid(-0.4169F, -0.2107F, -0.689F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.3511F, 0.333F, 1.5638F, 0.0436F, 0.5236F, 0.0F));

		ModelPartData right_arm_seg3a = right_arm.addChild("right_arm_seg3a", ModelPartBuilder.create().uv(15, 20).cuboid(0.2331F, -0.1607F, -0.414F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.1F, 0.3F, 0.35F, 0.2544F, 0.9409F, 0.2891F));

		ModelPartData right_arm_seg3b = right_arm.addChild("right_arm_seg3b", ModelPartBuilder.create().uv(10, 19).cuboid(0.1331F, -0.0857F, -0.489F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.1F, 0.3F, 0.35F, 2.5976F, 1.208F, 2.5642F));

		ModelPartData left_arm = soul_lizard.addChild("left_arm", ModelPartBuilder.create(), ModelTransform.pivot(-2.0F, -1.5F, -4.0F));

		ModelPartData left_arm_seg1 = left_arm.addChild("left_arm_seg1", ModelPartBuilder.create().uv(20, 3).cuboid(-3.0F, -0.4F, 0.0F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.025F, 0.0F, -0.675F, -0.1745F, 0.8126F, -0.2553F));

		ModelPartData left_arm_seg2 = left_arm.addChild("left_arm_seg2", ModelPartBuilder.create().uv(20, 7).cuboid(-2.5831F, -0.2107F, -0.689F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-1.3511F, 0.333F, 1.5638F, 0.0436F, -0.5236F, 0.0F));

		ModelPartData left_arm_seg3a = left_arm.addChild("left_arm_seg3a", ModelPartBuilder.create().uv(6, 8).cuboid(-2.2331F, -0.1607F, -0.414F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-3.1F, 0.3F, 0.35F, 0.2544F, -0.9409F, -0.2891F));

		ModelPartData left_arm_seg3b = left_arm.addChild("left_arm_seg3b", ModelPartBuilder.create().uv(6, 6).cuboid(-2.1331F, -0.0857F, -0.489F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-3.1F, 0.3F, 0.35F, 2.5976F, -1.208F, -2.5642F));
		return TexturedModelData.of(modelData, 32, 32);
	}
	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}
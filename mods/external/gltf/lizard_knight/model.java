// Generated from lizard_knight/source/model.gltf
// Pair this with ./model.png produced by ./texture_fix.py
// Source texture input: ./textures/gltf_embedded_0.png
// Paste this class into your mod and generate all required imports

package com.example.mod;

public class model extends EntityModel<Entity> {
	private final ModelPart root;
	private final ModelPart body0;
	private final ModelPart rightarm;
	private final ModelPart rightarm2;
	private final ModelPart mesh_0;
	private final ModelPart mesh_1;
	private final ModelPart mesh_2;
	private final ModelPart mesh_3;
	private final ModelPart mesh_4;
	private final ModelPart mesh_5;
	private final ModelPart mesh_6;
	private final ModelPart mesh_7;
	private final ModelPart bone;
	private final ModelPart bone2;
	private final ModelPart mesh_8;
	private final ModelPart bone3;
	private final ModelPart mesh_9;
	private final ModelPart bone4;
	private final ModelPart mesh_10;
	private final ModelPart leftarm3;
	private final ModelPart mesh_11;
	private final ModelPart leftarm4;
	private final ModelPart mesh_12;
	private final ModelPart tail;
	private final ModelPart mesh_13;
	private final ModelPart tail2;
	private final ModelPart mesh_14;
	private final ModelPart tail3;
	private final ModelPart mesh_15;
	private final ModelPart head;
	private final ModelPart mesh_16;
	private final ModelPart mesh_17;
	private final ModelPart mesh_18;
	private final ModelPart leg1;
	private final ModelPart mesh_19;
	private final ModelPart leg0;
	private final ModelPart mesh_20;
	private final ModelPart leftarm4_2;
	private final ModelPart mesh_21;
	private final ModelPart leftarm5;
	private final ModelPart mesh_22;
	private final ModelPart leftarm6;
	private final ModelPart mesh_23;
	private final ModelPart leftarm7;
	private final ModelPart mesh_24;
	public model(ModelPart root) {
		this.root = root.getChild("root");
		this.body0 = this.root.getChild("body0");
		this.rightarm = this.body0.getChild("rightarm");
		this.rightarm2 = this.rightarm.getChild("rightarm2");
		this.mesh_0 = this.body0.getChild("mesh_0");
		this.mesh_1 = this.body0.getChild("mesh_1");
		this.mesh_2 = this.body0.getChild("mesh_2");
		this.mesh_3 = this.body0.getChild("mesh_3");
		this.mesh_4 = this.body0.getChild("mesh_4");
		this.mesh_5 = this.body0.getChild("mesh_5");
		this.mesh_6 = this.body0.getChild("mesh_6");
		this.mesh_7 = this.body0.getChild("mesh_7");
		this.bone = this.body0.getChild("bone");
		this.bone2 = this.bone.getChild("bone2");
		this.mesh_8 = this.bone.getChild("mesh_8");
		this.bone3 = this.body0.getChild("bone3");
		this.mesh_9 = this.bone3.getChild("mesh_9");
		this.bone4 = this.bone3.getChild("bone4");
		this.mesh_10 = this.bone4.getChild("mesh_10");
		this.leftarm3 = this.bone4.getChild("leftarm3");
		this.mesh_11 = this.leftarm3.getChild("mesh_11");
		this.leftarm4 = this.leftarm3.getChild("leftarm4");
		this.mesh_12 = this.leftarm4.getChild("mesh_12");
		this.tail = this.body0.getChild("tail");
		this.mesh_13 = this.tail.getChild("mesh_13");
		this.tail2 = this.tail.getChild("tail2");
		this.mesh_14 = this.tail2.getChild("mesh_14");
		this.tail3 = this.tail2.getChild("tail3");
		this.mesh_15 = this.tail3.getChild("mesh_15");
		this.head = this.body0.getChild("head");
		this.mesh_16 = this.head.getChild("mesh_16");
		this.mesh_17 = this.head.getChild("mesh_17");
		this.mesh_18 = this.head.getChild("mesh_18");
		this.leg1 = this.body0.getChild("leg1");
		this.mesh_19 = this.leg1.getChild("mesh_19");
		this.leg0 = this.body0.getChild("leg0");
		this.mesh_20 = this.leg0.getChild("mesh_20");
		this.leftarm4_2 = this.body0.getChild("leftarm4_2");
		this.mesh_21 = this.leftarm4_2.getChild("mesh_21");
		this.leftarm5 = this.leftarm4_2.getChild("leftarm5");
		this.mesh_22 = this.leftarm5.getChild("mesh_22");
		this.leftarm6 = this.leftarm5.getChild("leftarm6");
		this.mesh_23 = this.leftarm6.getChild("mesh_23");
		this.leftarm7 = this.leftarm6.getChild("leftarm7");
		this.mesh_24 = this.leftarm7.getChild("mesh_24");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData body0 = root.addChild("body0", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -23.0F, 0.0F));

		ModelPartData rightarm = body0.addChild("rightarm", ModelPartBuilder.create(), ModelTransform.of(6.5F, 5.0F, 0.5F, 0.0F, 0.0F, 0.1309F));

		ModelPartData rightarm2 = rightarm.addChild("rightarm2", ModelPartBuilder.create(), ModelTransform.of(2.3872F, 3.7042F, 0.0F, 0.0F, 0.0F, 0.1745F));

		ModelPartData mesh_0 = body0.addChild("mesh_0", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -23.0F, -3.0F, 10.0F, 8.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 23.0F, 0.0F));

		ModelPartData mesh_1 = body0.addChild("mesh_1", ModelPartBuilder.create().uv(34, 32).cuboid(-5.0F, 0.0F, 0.0F, 10.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.9F, -4.0F, 0.3491F, 0.0F, 0.0F));

		ModelPartData mesh_2 = body0.addChild("mesh_2", ModelPartBuilder.create().uv(19, 14).cuboid(-5.0F, 0.0F, 0.0F, 10.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -3.0F, -0.2007F, 0.0F, 0.0F));

		ModelPartData mesh_3 = body0.addChild("mesh_3", ModelPartBuilder.create().uv(21, 21).cuboid(-4.0F, -15.0F, -2.5F, 8.0F, 6.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 23.0F, 0.0F));

		ModelPartData mesh_4 = body0.addChild("mesh_4", ModelPartBuilder.create().uv(48, 6).cuboid(-2.0F, 0.0F, 0.0F, 4.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 14.0F, -2.5F, -0.0873F, 0.0F, 0.0F));

		ModelPartData mesh_5 = body0.addChild("mesh_5", ModelPartBuilder.create().uv(0, 14).cuboid(4.0F, -11.0F, -1.5F, 1.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 23.0F, 0.0F));

		ModelPartData mesh_6 = body0.addChild("mesh_6", ModelPartBuilder.create().uv(0, 14).cuboid(-5.0F, -11.0F, -1.5F, 1.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 23.0F, 0.0F));

		ModelPartData mesh_7 = body0.addChild("mesh_7", ModelPartBuilder.create().uv(0, 33).cuboid(-2.5F, -6.0F, -5.0F, 5.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 2.5F, 0.6109F, 0.0F, 0.0F));

		ModelPartData bone = body0.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(5.0F, 0.0F, 0.5F));

		ModelPartData bone2 = bone.addChild("bone2", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, 0.0F, 0.0F));

		ModelPartData mesh_8 = bone.addChild("mesh_8", ModelPartBuilder.create().uv(19, 16).cuboid(0.0F, -1.0F, -3.0F, 0.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.1309F));

		ModelPartData bone3 = body0.addChild("bone3", ModelPartBuilder.create(), ModelTransform.of(-5.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.6981F));

		ModelPartData mesh_9 = bone3.addChild("mesh_9", ModelPartBuilder.create().uv(36, 46).cuboid(-3.0F, 0.0F, -2.0F, 3.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData bone4 = bone3.addChild("bone4", ModelPartBuilder.create(), ModelTransform.of(-3.0F, 0.0F, 0.5F, 0.0F, 0.0F, -0.4363F));

		ModelPartData mesh_10 = bone4.addChild("mesh_10", ModelPartBuilder.create().uv(36, 46).cuboid(-11.0F, -23.0F, -2.0F, 3.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 23.0F, -0.5F));

		ModelPartData leftarm3 = bone4.addChild("leftarm3", ModelPartBuilder.create(), ModelTransform.of(-3.0F, 0.5F, 0.0F, 0.0F, 0.0F, 1.3526F));

		ModelPartData mesh_11 = leftarm3.addChild("mesh_11", ModelPartBuilder.create().uv(24, 45).cuboid(-2.0F, -1.0F, -2.0F, 3.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 1.0F, 0.0F));

		ModelPartData leftarm4 = leftarm3.addChild("leftarm4", ModelPartBuilder.create(), ModelTransform.of(0.5F, 5.5F, -0.5F, 0.0F, 0.0F, -0.1745F));

		ModelPartData mesh_12 = leftarm4.addChild("mesh_12", ModelPartBuilder.create().uv(14, 51).cuboid(-0.2F, -1.2F, -1.2F, 2.4F, 6.4F, 2.4F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.5F, 0.0F));

		ModelPartData tail = body0.addChild("tail", ModelPartBuilder.create(), ModelTransform.of(0.0F, 14.0F, 0.5F, -0.5236F, 0.0F, 0.0F));

		ModelPartData mesh_13 = tail.addChild("mesh_13", ModelPartBuilder.create().uv(48, 0).cuboid(-1.5F, -12.0F, 0.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 9.0F, -0.5F));

		ModelPartData tail2 = tail.addChild("tail2", ModelPartBuilder.create(), ModelTransform.of(0.0F, -0.5F, 3.0F, 0.3054F, 0.0F, 0.0F));

		ModelPartData mesh_14 = tail2.addChild("mesh_14", ModelPartBuilder.create().uv(0, 47).cuboid(-1.0F, -11.5F, 3.5F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 9.5F, -3.5F));

		ModelPartData tail3 = tail2.addChild("tail3", ModelPartBuilder.create(), ModelTransform.of(0.0F, -0.5F, 5.0F, 0.3054F, 0.0F, 0.0F));

		ModelPartData mesh_15 = tail3.addChild("mesh_15", ModelPartBuilder.create().uv(2, 50).cuboid(-0.7F, -11.2F, 8.3F, 1.4F, 1.4F, 3.4F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 10.0F, -8.5F));

		ModelPartData head = body0.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

		ModelPartData mesh_16 = head.addChild("mesh_16", ModelPartBuilder.create().uv(0, 14).cuboid(-3.0F, -28.5F, -7.0F, 6.0F, 4.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData mesh_17 = head.addChild("mesh_17", ModelPartBuilder.create().uv(0, 36).cuboid(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5F, -3.0F, -1.0036F, 0.0F, 0.0F));

		ModelPartData mesh_18 = head.addChild("mesh_18", ModelPartBuilder.create().uv(0, 25).cuboid(-3.0F, -24.5F, -7.0F, 6.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData leg1 = body0.addChild("leg1", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 23.0F, 0.0F));

		ModelPartData mesh_19 = leg1.addChild("mesh_19", ModelPartBuilder.create().uv(22, 32).cuboid(0.5F, -9.0F, -2.0F, 4.0F, 9.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData leg0 = body0.addChild("leg0", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 23.0F, 0.0F));

		ModelPartData mesh_20 = leg0.addChild("mesh_20", ModelPartBuilder.create().uv(22, 32).cuboid(-4.5F, -9.0F, -2.0F, 4.0F, 9.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData leftarm4_2 = body0.addChild("leftarm4_2", ModelPartBuilder.create(), ModelTransform.of(5.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6981F));

		ModelPartData mesh_21 = leftarm4_2.addChild("mesh_21", ModelPartBuilder.create().uv(36, 46).cuboid(0.0F, 0.0F, -2.0F, 3.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData leftarm5 = leftarm4_2.addChild("leftarm5", ModelPartBuilder.create(), ModelTransform.of(3.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.4363F));

		ModelPartData mesh_22 = leftarm5.addChild("mesh_22", ModelPartBuilder.create().uv(36, 46).cuboid(8.0F, -23.0F, -2.0F, 3.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, 23.0F, -0.5F));

		ModelPartData leftarm6 = leftarm5.addChild("leftarm6", ModelPartBuilder.create(), ModelTransform.of(3.0F, 0.5F, 0.0F, 0.0F, 0.0F, -1.3526F));

		ModelPartData mesh_23 = leftarm6.addChild("mesh_23", ModelPartBuilder.create().uv(24, 45).cuboid(-1.0F, -1.0F, -2.0F, 3.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 1.0F, 0.0F));

		ModelPartData leftarm7 = leftarm6.addChild("leftarm7", ModelPartBuilder.create(), ModelTransform.of(-0.5F, 5.5F, -0.5F, 0.0F, 0.0F, 0.1745F));

		ModelPartData mesh_24 = leftarm7.addChild("mesh_24", ModelPartBuilder.create().uv(14, 51).cuboid(-2.2F, -1.2F, -1.2F, 2.4F, 6.4F, 2.4F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.5F, 0.0F));

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
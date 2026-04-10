// Generated from Grassling Spreder.gltf for the /mods/external/blockbench/ pipeline
// This file is intentionally shaped like a Blockbench Yarn export so the existing parser can ingest it.
// Paste this class into your mod and generate all required imports if you want to compile it directly.

package com.example.mod;

public class model extends EntityModel<Entity> {
	private final ModelPart root;
	private final ModelPart node_44;
	private final ModelPart tuttoilcorpo;
	private final ModelPart testa;
	private final ModelPart cube;
	private final ModelPart fowerbomb;
	private final ModelPart flowerB1;
	private final ModelPart cube_2;
	private final ModelPart flowerB2;
	private final ModelPart cube_3;
	private final ModelPart flowerB3;
	private final ModelPart cube_4;
	private final ModelPart flowerB4;
	private final ModelPart cube_5;
	private final ModelPart flowerB5;
	private final ModelPart cube_6;
	private final ModelPart flowerB6;
	private final ModelPart cube_7;
	private final ModelPart flowerB7;
	private final ModelPart cube_8;
	private final ModelPart flowerB8;
	private final ModelPart cube_9;
	private final ModelPart flowerB9;
	private final ModelPart cube_10;
	private final ModelPart flowerB10;
	private final ModelPart cube_11;
	private final ModelPart flowerB11;
	private final ModelPart cube_12;
	private final ModelPart arms;
	private final ModelPart arm1;
	private final ModelPart cube_13;
	private final ModelPart cube_14;
	private final ModelPart arm2;
	private final ModelPart cube_15;
	private final ModelPart cube_16;
	private final ModelPart legs;
	private final ModelPart leg1;
	private final ModelPart cube_17;
	private final ModelPart leg2;
	private final ModelPart cube_18;
	private final ModelPart terrain;
	private final ModelPart cube_19;
	private final ModelPart floorflower;
	private final ModelPart cube_20;
	private final ModelPart explosion;
	private final ModelPart cube_21;

	public model(ModelPart root) {
		this.root = root.getChild("root");
		this.node_44 = this.root.getChild("node_44");
		this.tuttoilcorpo = this.node_44.getChild("tuttoilcorpo");
		this.testa = this.tuttoilcorpo.getChild("testa");
		this.cube = this.testa.getChild("cube");
		this.fowerbomb = this.testa.getChild("fowerbomb");
		this.flowerB1 = this.fowerbomb.getChild("flowerB1");
		this.cube_2 = this.flowerB1.getChild("cube_2");
		this.flowerB2 = this.fowerbomb.getChild("flowerB2");
		this.cube_3 = this.flowerB2.getChild("cube_3");
		this.flowerB3 = this.fowerbomb.getChild("flowerB3");
		this.cube_4 = this.flowerB3.getChild("cube_4");
		this.flowerB4 = this.fowerbomb.getChild("flowerB4");
		this.cube_5 = this.flowerB4.getChild("cube_5");
		this.flowerB5 = this.fowerbomb.getChild("flowerB5");
		this.cube_6 = this.flowerB5.getChild("cube_6");
		this.flowerB6 = this.fowerbomb.getChild("flowerB6");
		this.cube_7 = this.flowerB6.getChild("cube_7");
		this.flowerB7 = this.fowerbomb.getChild("flowerB7");
		this.cube_8 = this.flowerB7.getChild("cube_8");
		this.flowerB8 = this.fowerbomb.getChild("flowerB8");
		this.cube_9 = this.flowerB8.getChild("cube_9");
		this.flowerB9 = this.fowerbomb.getChild("flowerB9");
		this.cube_10 = this.flowerB9.getChild("cube_10");
		this.flowerB10 = this.fowerbomb.getChild("flowerB10");
		this.cube_11 = this.flowerB10.getChild("cube_11");
		this.flowerB11 = this.fowerbomb.getChild("flowerB11");
		this.cube_12 = this.flowerB11.getChild("cube_12");
		this.arms = this.testa.getChild("arms");
		this.arm1 = this.arms.getChild("arm1");
		this.cube_13 = this.arm1.getChild("cube_13");
		this.cube_14 = this.arm1.getChild("cube_14");
		this.arm2 = this.arms.getChild("arm2");
		this.cube_15 = this.arm2.getChild("cube_15");
		this.cube_16 = this.arm2.getChild("cube_16");
		this.legs = this.tuttoilcorpo.getChild("legs");
		this.leg1 = this.legs.getChild("leg1");
		this.cube_17 = this.leg1.getChild("cube_17");
		this.leg2 = this.legs.getChild("leg2");
		this.cube_18 = this.leg2.getChild("cube_18");
		this.terrain = this.node_44.getChild("terrain");
		this.cube_19 = this.terrain.getChild("cube_19");
		this.floorflower = this.terrain.getChild("floorflower");
		this.cube_20 = this.floorflower.getChild("cube_20");
		this.explosion = this.node_44.getChild("explosion");
		this.cube_21 = this.explosion.getChild("cube_21");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData node_44 = root.addChild("node_44", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData tuttoilcorpo = node_44.addChild("tuttoilcorpo", ModelPartBuilder.create(), ModelTransform.pivot(1.0F, 0.0F, -6.0F));
		ModelPartData testa = tuttoilcorpo.addChild("testa", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -15.0F, 0.0F));
		ModelPartData cube = testa.addChild("cube", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -25.0F, -9.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 15.0F, 0.0F, 3.098F, 0.0F, 3.1416F));
		ModelPartData fowerbomb = testa.addChild("fowerbomb", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 15.0F, 0.0F));
		ModelPartData flowerB1 = fowerbomb.addChild("flowerB1", ModelPartBuilder.create(), ModelTransform.pivot(8.0F, -22.0F, 6.0F));
		ModelPartData cube_2 = flowerB1.addChild("cube_2", ModelPartBuilder.create().uv(66, 0).cuboid(-9.0F, -26.0F, -10.0F, 7.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-9.0F, 22.0F, -6.0F, -3.1037F, -0.1327F, -3.0976F));
		ModelPartData flowerB2 = fowerbomb.addChild("flowerB2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -26.0F, 5.0F));
		ModelPartData cube_3 = flowerB2.addChild("cube_3", ModelPartBuilder.create().uv(96, 0).cuboid(-6.0F, -30.0F, -10.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 26.0F, -5.0F, -3.1416F, 0.0F, -3.1416F));
		ModelPartData flowerB3 = fowerbomb.addChild("flowerB3", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData cube_4 = flowerB3.addChild("cube_4", ModelPartBuilder.create().uv(138, 0).cuboid(-3.0F, -29.0F, -8.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 0.0F, 0.0F, -3.0107F, 0.0F, 2.9234F));
		ModelPartData flowerB4 = fowerbomb.addChild("flowerB4", ModelPartBuilder.create(), ModelTransform.pivot(5.0F, -23.0F, -7.0F));
		ModelPartData cube_5 = flowerB4.addChild("cube_5", ModelPartBuilder.create().uv(172, 0).cuboid(-7.0F, -27.0F, 3.0F, 7.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-6.0F, 23.0F, 7.0F, 3.0918F, -0.0019F, -3.0927F));
		ModelPartData flowerB5 = fowerbomb.addChild("flowerB5", ModelPartBuilder.create(), ModelTransform.pivot(-6.0F, -22.0F, -7.0F));
		ModelPartData cube_6 = flowerB5.addChild("cube_6", ModelPartBuilder.create().uv(202, 0).cuboid(-10.0F, -26.0F, 2.0F, 7.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, 22.0F, 7.0F, 3.1006F, -0.0283F, 2.6238F));
		ModelPartData flowerB6 = fowerbomb.addChild("flowerB6", ModelPartBuilder.create(), ModelTransform.pivot(-6.0F, -23.0F, 0.0F));
		ModelPartData cube_7 = flowerB6.addChild("cube_7", ModelPartBuilder.create().uv(232, 0).cuboid(-5.0F, -26.0F, -12.0F, 7.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, 23.0F, 0.0F, 2.4127F, 1.4168F, 2.5379F));
		ModelPartData flowerB7 = fowerbomb.addChild("flowerB7", ModelPartBuilder.create(), ModelTransform.pivot(-1.0F, -27.0F, -5.0F));
		ModelPartData cube_8 = flowerB7.addChild("cube_8", ModelPartBuilder.create().uv(262, 0).cuboid(-4.0F, -22.0F, -23.0F, 9.0F, 9.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 27.0F, 5.0F, 2.138F, 0.0F, -3.1416F));
		ModelPartData flowerB8 = fowerbomb.addChild("flowerB8", ModelPartBuilder.create(), ModelTransform.pivot(8.0F, -23.0F, -1.0F));
		ModelPartData cube_9 = flowerB8.addChild("cube_9", ModelPartBuilder.create().uv(300, 0).cuboid(-3.0F, -28.0F, -4.0F, 7.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-9.0F, 23.0F, 1.0F, 3.0923F, 0.0068F, -2.9184F));
		ModelPartData flowerB9 = fowerbomb.addChild("flowerB9", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData cube_10 = flowerB9.addChild("cube_10", ModelPartBuilder.create().uv(330, 0).cuboid(-2.0F, -13.0F, 12.0F, 7.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 0.0F, 0.0F, -1.7017F, 0.0F, 2.9234F));
		ModelPartData flowerB10 = fowerbomb.addChild("flowerB10", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData cube_11 = flowerB10.addChild("cube_11", ModelPartBuilder.create().uv(360, 0).cuboid(-5.0F, -13.0F, 12.0F, 7.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 0.0F, 0.0F, -1.7017F, 0.0F, -2.9234F));
		ModelPartData flowerB11 = fowerbomb.addChild("flowerB11", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData cube_12 = flowerB11.addChild("cube_12", ModelPartBuilder.create().uv(390, 0).cuboid(-4.0F, -12.0F, 13.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 0.0F, 0.0F, -1.6986F, 0.0283F, 3.1398F));
		ModelPartData arms = testa.addChild("arms", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -4.0F, 0.0F));
		ModelPartData arm1 = arms.addChild("arm1", ModelPartBuilder.create(), ModelTransform.pivot(8.0F, 2.0F, 1.0F));
		ModelPartData cube_13 = arm1.addChild("cube_13", ModelPartBuilder.create().uv(416, 0).cuboid(-14.0F, -5.7417F, -12.952F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-9.0F, 17.0F, -1.0F, 2.9671F, 0.0F, 3.1416F));
		ModelPartData cube_14 = arm1.addChild("cube_14", ModelPartBuilder.create().uv(442, 0).cuboid(-14.0F, -19.7417F, -6.952F, 6.0F, 20.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-9.0F, 17.0F, -1.0F, 2.9671F, 0.0F, 3.1416F));
		ModelPartData arm2 = arms.addChild("arm2", ModelPartBuilder.create(), ModelTransform.pivot(-8.0F, 2.0F, 1.0F));
		ModelPartData cube_15 = arm2.addChild("cube_15", ModelPartBuilder.create().uv(468, 0).cuboid(8.0F, -19.7417F, -6.952F, 6.0F, 20.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(7.0F, 17.0F, -1.0F, 2.9671F, 0.0F, 3.1416F));
		ModelPartData cube_16 = arm2.addChild("cube_16", ModelPartBuilder.create().uv(0, 34).cuboid(8.0F, -5.7417F, -12.952F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(7.0F, 17.0F, -1.0F, 2.9671F, 0.0F, 3.1416F));
		ModelPartData legs = tuttoilcorpo.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -9.0F, 0.0F));
		ModelPartData leg1 = legs.addChild("leg1", ModelPartBuilder.create(), ModelTransform.pivot(-4.0F, 0.0F, 0.0F));
		ModelPartData cube_17 = leg1.addChild("cube_17", ModelPartBuilder.create().uv(26, 34).cuboid(1.0F, -10.0F, -3.0F, 6.0F, 10.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, 9.0F, 0.0F, -3.1416F, 0.0F, -3.1416F));
		ModelPartData leg2 = legs.addChild("leg2", ModelPartBuilder.create(), ModelTransform.pivot(4.0F, 0.0F, 0.0F));
		ModelPartData cube_18 = leg2.addChild("cube_18", ModelPartBuilder.create().uv(52, 34).cuboid(-7.0F, -10.0F, -3.0F, 6.0F, 10.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, 9.0F, 0.0F, -3.1416F, 0.0F, -3.1416F));
		ModelPartData terrain = node_44.addChild("terrain", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData cube_19 = terrain.addChild("cube_19", ModelPartBuilder.create().uv(78, 34).cuboid(-47.0F, -1.001F, -47.0F, 94.0F, 0.001F, 94.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData floorflower = terrain.addChild("floorflower", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData cube_20 = floorflower.addChild("cube_20", ModelPartBuilder.create().uv(0, 131).cuboid(-47.0F, -0.001F, -47.0F, 94.0F, 0.001F, 94.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData explosion = node_44.addChild("explosion", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 2.0F));
		ModelPartData cube_21 = explosion.addChild("cube_21", ModelPartBuilder.create().uv(378, 131).cuboid(-25.0F, -26.0F, 1.0F, 49.0F, 47.0F, 0.001F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -2.0F, -1.5708F, 0.0F, 0.0F));

		return TexturedModelData.of(modelData, 512, 256);
	}

	@Override
	public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		// Source-only geometry class for the /external/blockbench/ pipeline.
		// Authored animation data lives in model-anim.java.
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		root.render(matrices, vertices, light, overlay, color);
	}
}
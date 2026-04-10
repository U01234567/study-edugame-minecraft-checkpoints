// Generated from ender_ape/source/model.gltf
// Target folder: mods/external/blockbench/ender_ape/
// This creature is Class B: geometry and animation are clean, but the original PNG cannot be
// used unchanged with direct box-UV Java cuboids. Run texture_fix.py once to build ./model.png
// from ./textures/gltf_embedded_0.png.

public class model extends EntityModel<Entity> {
	private final ModelPart root;

	public model(ModelPart root) {
		this.root = root.getChild("root");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData ender_ape = root.addChild("ender_ape", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

		ModelPartData body = ender_ape.addChild("body", ModelPartBuilder.create(), ModelTransform.of(0.0F, -3.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

		ModelPartData head = body.addChild("head", ModelPartBuilder.create(), ModelTransform.of(0.0F, -15.134F, 2.5F, -1.0472F, 0.0F, 0.0F));

		ModelPartData headshooter = head.addChild("headshooter", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 4.0F, 0.0F));
		ModelPartData headshooter_main = headshooter.addChild("headshooter_main", ModelPartBuilder.create().uv(72, 0).cuboid(-3.0F, -3.5F, -2.5F, 6.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData headshooter_core = headshooter.addChild("headshooter_core", ModelPartBuilder.create().uv(96, 0).cuboid(-1.0F, -7.5F, -5.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData ears = head.addChild("ears", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -1.5359F, -3.0F));
		ModelPartData left_ear = ears.addChild("left_ear", ModelPartBuilder.create().uv(104, 0).cuboid(-3.0F, -26.0F, -1.0F, 1.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 24.0F, -1.0F));
		ModelPartData right_ear = ears.addChild("right_ear", ModelPartBuilder.create().uv(104, 8).cuboid(6.0F, -26.0F, -1.0F, 1.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 24.0F, -1.0F));

		ModelPartData head_main = head.addChild("head_main", ModelPartBuilder.create().uv(40, 0).cuboid(-2.0F, -27.0F, -2.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 22.4641F, -4.0F));

		ModelPartData hands = body.addChild("hands", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -13.134F, 0.5F));

		ModelPartData lefthand = hands.addChild("lefthand", ModelPartBuilder.create(), ModelTransform.pivot(-6.0F, 1.134F, -0.5F));
		ModelPartData left_hand_main = lefthand.addChild("left_hand_main", ModelPartBuilder.create().uv(0, 24).cuboid(-12.0F, -3.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, 0.0F, 0.0F, -1.0472F, 0.0F, 0.0F));

		ModelPartData righthand = hands.addChild("righthand", ModelPartBuilder.create(), ModelTransform.pivot(6.0F, 1.134F, -0.5F));
		ModelPartData right_hand_main = righthand.addChild("right_hand_main", ModelPartBuilder.create().uv(16, 24).cuboid(12.0F, -3.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-12.0F, 0.0F, 0.0F, -1.0472F, 0.0F, 0.0F));

		ModelPartData body_only = body.addChild("body_only", ModelPartBuilder.create(), ModelTransform.pivot(-2.0F, 0.866F, 0.5F));
		ModelPartData body_main = body_only.addChild("body_main", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -16.0F, -2.0F, 12.0F, 16.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData feet = ender_ape.addChild("feet", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -3.0F, 0.0F));

		ModelPartData leftfoot = feet.addChild("leftfoot", ModelPartBuilder.create(), ModelTransform.pivot(-3.0F, 0.0F, 0.0F));
		ModelPartData left_foot_main = leftfoot.addChild("left_foot_main", ModelPartBuilder.create().uv(32, 24).cuboid(-8.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, 3.0F, 0.0F));

		ModelPartData rightfoot = feet.addChild("rightfoot", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, 0.0F, 0.0F));
		ModelPartData right_foot_main = rightfoot.addChild("right_foot_main", ModelPartBuilder.create().uv(40, 24).cuboid(6.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, 3.0F, 0.0F));

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
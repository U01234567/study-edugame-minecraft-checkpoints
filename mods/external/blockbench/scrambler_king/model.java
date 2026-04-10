// Generated from scrambler_king/source/model.gltf
// Target folder: mods/external/blockbench/scrambler_king/
// Uses original texture unchanged: copy ./textures/gltf_embedded_0.png -> ./model.png

public class model extends EntityModel<Entity> {
	private final ModelPart root;

	public model(ModelPart root) {
		this.root = root.getChild("root");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData Main = root.addChild("Main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -16.0F, -6.0F));
		ModelPartData Leg0 = Main.addChild("Leg0", ModelPartBuilder.create(), ModelTransform.of(-13.0F, -2.0F, -6.0F, 0.0F, -0.3927F, 0.0F));
		ModelPartData cube = Leg0.addChild("cube", ModelPartBuilder.create().uv(116, 69).mirrored().cuboid(-11.0F, -3.0F, -2.0F, 12.0F, 7.0F, 8.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-1.0F, 0.0F, -2.0F));
		ModelPartData cube_2 = Leg0.addChild("cube_2", ModelPartBuilder.create().uv(0, 118).mirrored().cuboid(-11.0F, -3.0F, -2.0F, 10.0F, 7.0F, 8.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-8.0F, 15.0F, -2.0F, 0.0F, 0.0F, 1.5708F));
		ModelPartData cube_3 = Leg0.addChild("cube_3", ModelPartBuilder.create().uv(104, 102).mirrored().cuboid(-11.0F, -4.0F, -3.0F, 8.0F, 9.0F, 10.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-8.0F, 21.0F, -2.0F, 0.0F, 0.0F, 1.5708F));
		ModelPartData Leg2 = Main.addChild("Leg2", ModelPartBuilder.create(), ModelTransform.of(-13.0F, -2.0F, 6.0F, 0.0F, 0.3927F, 0.0F));
		ModelPartData cube_4 = Leg2.addChild("cube_4", ModelPartBuilder.create().uv(116, 54).mirrored().cuboid(-11.0F, -3.0F, -2.0F, 12.0F, 7.0F, 8.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-1.0F, 0.0F, -2.0F));
		ModelPartData cube_5 = Leg2.addChild("cube_5", ModelPartBuilder.create().uv(0, 118).mirrored().cuboid(-11.0F, -3.0F, -2.0F, 10.0F, 7.0F, 8.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-8.0F, 15.0F, -2.0F, 0.0F, 0.0F, 1.5708F));
		ModelPartData cube_6 = Leg2.addChild("cube_6", ModelPartBuilder.create().uv(104, 102).mirrored().cuboid(-11.0F, -4.0F, -3.0F, 8.0F, 9.0F, 10.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-8.0F, 21.0F, -2.0F, 0.0F, 0.0F, 1.5708F));
		ModelPartData Leg1 = Main.addChild("Leg1", ModelPartBuilder.create(), ModelTransform.of(13.0F, -2.0F, -6.0F, 0.0F, 0.4363F, 0.0F));
		ModelPartData cube_7 = Leg1.addChild("cube_7", ModelPartBuilder.create().uv(116, 69).cuboid(-1.0F, -3.0F, -2.0F, 12.0F, 7.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, 0.0F, -2.0F));
		ModelPartData cube_8 = Leg1.addChild("cube_8", ModelPartBuilder.create().uv(0, 118).cuboid(1.0F, -3.0F, -2.0F, 10.0F, 7.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, 15.0F, -2.0F, 0.0F, 0.0F, -1.5708F));
		ModelPartData cube_9 = Leg1.addChild("cube_9", ModelPartBuilder.create().uv(104, 102).cuboid(3.0F, -4.0F, -3.0F, 8.0F, 9.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, 21.0F, -2.0F, 0.0F, 0.0F, -1.5708F));
		ModelPartData Leg3 = Main.addChild("Leg3", ModelPartBuilder.create(), ModelTransform.of(12.0F, -2.0F, 6.0F, 0.0F, -0.3927F, 0.0F));
		ModelPartData cube_10 = Leg3.addChild("cube_10", ModelPartBuilder.create().uv(116, 54).cuboid(-1.0F, -3.0F, -2.0F, 12.0F, 7.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, 0.0F, -2.0F));
		ModelPartData cube_11 = Leg3.addChild("cube_11", ModelPartBuilder.create().uv(0, 118).cuboid(1.0F, -3.0F, -2.0F, 10.0F, 7.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, 15.0F, -2.0F, 0.0F, 0.0F, -1.5708F));
		ModelPartData cube_12 = Leg3.addChild("cube_12", ModelPartBuilder.create().uv(104, 102).cuboid(3.0F, -4.0F, -3.0F, 8.0F, 9.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, 21.0F, -2.0F, 0.0F, 0.0F, -1.5708F));
		ModelPartData Body = Main.addChild("Body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -4.0F, 2.0F));
		ModelPartData Main_2 = Body.addChild("Main_2", ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(-12.0F, -30.0F, -13.0F, 26.0F, 25.0F, 21.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-1.0F, 16.0F, 0.0F));
		ModelPartData Main_3 = Body.addChild("Main_3", ModelPartBuilder.create().uv(94, 0).mirrored().cuboid(-11.0F, -29.0F, -13.0F, 24.0F, 23.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-1.0F, 16.0F, 21.0F));
		ModelPartData Tail = Body.addChild("Tail", ModelPartBuilder.create(), ModelTransform.pivot(1.0F, -4.0F, 8.0F));
		ModelPartData Tail_2 = Tail.addChild("Tail_2", ModelPartBuilder.create().uv(58, 58).mirrored().cuboid(-8.5F, -4.0F, -1.0F, 13.0F, 12.0F, 32.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(1.0F, -1.0F, 1.0F));
		ModelPartData Tail_3 = Tail.addChild("Tail_3", ModelPartBuilder.create().uv(64, 102).mirrored().cuboid(-8.5F, -4.0F, 16.0F, 13.0F, 12.0F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(1.0F, 3.0F, 55.0F, 3.1416F, 0.0F, 0.0F));
		ModelPartData Bristles0 = Body.addChild("Bristles0", ModelPartBuilder.create(), ModelTransform.pivot(-13.0F, -8.0F, -8.0F));
		ModelPartData Bristles0_2 = Bristles0.addChild("Bristles0_2", ModelPartBuilder.create().uv(0, 46).mirrored().cuboid(-12.0F, -0.001F, -2.0F, 12.0F, 0.001F, 8.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, -2.0F));
		ModelPartData BigMouthBasement = Body.addChild("BigMouthBasement", ModelPartBuilder.create(), ModelTransform.of(1.0F, -11.0F, -4.0F, 2.2689F, 0.0F, 0.0F));
		ModelPartData BigMouthBasement_2 = BigMouthBasement.addChild("BigMouthBasement_2", ModelPartBuilder.create().uv(0, 46).mirrored().cuboid(-8.5F, -4.0F, -1.0F, 13.0F, 12.0F, 32.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(1.0F, -2.0F, 1.0F));
		ModelPartData BigMouth = BigMouthBasement.addChild("BigMouth", ModelPartBuilder.create(), ModelTransform.of(-1.0F, 2.0F, 32.0F, 0.48F, 0.0F, 0.0F));
		ModelPartData BigMouth_2 = BigMouth.addChild("BigMouth_2", ModelPartBuilder.create().uv(0, 90).mirrored().cuboid(-11.5F, -8.0F, -1.0F, 19.0F, 15.0F, 13.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(2.0F, -2.0F, 1.0F));
		ModelPartData Bristles1 = Body.addChild("Bristles1", ModelPartBuilder.create(), ModelTransform.pivot(13.0F, -8.0F, -8.0F));
		ModelPartData Bristles0_3 = Bristles1.addChild("Bristles0_3", ModelPartBuilder.create().uv(0, 46).cuboid(0.0F, -0.001F, -2.0F, 12.0F, 0.001F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -2.0F));
		ModelPartData Mouthbasement3 = Body.addChild("Mouthbasement3", ModelPartBuilder.create(), ModelTransform.of(-8.0F, -13.0F, 1.0F, 0.4235F, 0.233F, -0.4733F));
		ModelPartData Mouthbasement = Mouthbasement3.addChild("Mouthbasement", ModelPartBuilder.create().uv(58, 48).mirrored().cuboid(-1.0F, -29.0F, -1.0F, 8.0F, 9.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-4.154F, 19.8735F, -3.4617F));
		ModelPartData Mouthbasement4 = Mouthbasement3.addChild("Mouthbasement4", ModelPartBuilder.create(), ModelTransform.of(-1.154F, -7.1265F, 0.5383F, 0.48F, 0.0F, 0.0F));
		ModelPartData Mouthbasement3_2 = Mouthbasement4.addChild("Mouthbasement3_2", ModelPartBuilder.create().uv(0, 54).mirrored().cuboid(-4.0F, -9.3F, -3.7F, 8.0F, 9.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -2.0F, -1.0F, 0.0F, -0.0436F, 0.0F));
		ModelPartData Mouth2 = Mouthbasement4.addChild("Mouth2", ModelPartBuilder.create(), ModelTransform.of(-3.0F, -7.3681F, -0.7588F, -0.7854F, 0.0F, 0.0F));
		ModelPartData Mouth = Mouth2.addChild("Mouth", ModelPartBuilder.create().uv(85, 37).mirrored().cuboid(-3.0F, -2.6319F, -7.9412F, 12.0F, 8.0F, 9.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -4.0F, -3.0F, -0.2182F, 0.0F, 0.0F));
		ModelPartData Mouthbasement2 = Body.addChild("Mouthbasement2", ModelPartBuilder.create(), ModelTransform.of(8.0F, -13.0F, 1.0F, 0.4235F, -0.233F, 0.4733F));
		ModelPartData Mouthbasement_2 = Mouthbasement2.addChild("Mouthbasement_2", ModelPartBuilder.create().uv(58, 48).cuboid(-7.0F, -29.0F, -1.0F, 8.0F, 9.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(4.154F, 19.8735F, -3.4617F));
		ModelPartData Mouthbasement6 = Mouthbasement2.addChild("Mouthbasement6", ModelPartBuilder.create(), ModelTransform.of(1.154F, -7.1265F, 0.5383F, 0.48F, 0.0F, 0.0F));
		ModelPartData Mouthbasement3_3 = Mouthbasement6.addChild("Mouthbasement3_3", ModelPartBuilder.create().uv(0, 54).cuboid(-4.0F, -9.3F, -3.7F, 8.0F, 9.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, -1.0F, 0.0F, 0.0436F, 0.0F));
		ModelPartData Mouth3 = Mouthbasement6.addChild("Mouth3", ModelPartBuilder.create(), ModelTransform.of(3.0F, -7.3681F, -0.7588F, -0.7854F, 0.0F, 0.0F));
		ModelPartData Mouth_2 = Mouth3.addChild("Mouth_2", ModelPartBuilder.create().uv(85, 37).cuboid(-9.0F, -2.6319F, -7.9412F, 12.0F, 8.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0F, -3.0F, -0.2182F, 0.0F, 0.0F));

		return TexturedModelData.of(modelData, 256, 256);
	}

	@Override
	public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		// Intentionally left blank. Pose helpers live in model-anim.java.
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}
}
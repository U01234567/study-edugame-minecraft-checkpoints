// Made from GLTF source for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package com.example.mod;

public class model extends EntityModel<Entity> {
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart leg3;
	private final ModelPart leg4;
	private final ModelPart chest;
	private final ModelPart leg1;
	private final ModelPart leg2;
	private final ModelPart tail;
	private final ModelPart head;
	private final ModelPart jaw;

	public model(ModelPart root) {
		this.root = root.getChild("root");
		this.body = this.root.getChild("body");
		this.leg3 = this.body.getChild("leg3");
		this.leg4 = this.body.getChild("leg4");
		this.chest = this.body.getChild("chest");
		this.leg1 = this.chest.getChild("leg1");
		this.leg2 = this.chest.getChild("leg2");
		this.tail = this.chest.getChild("tail");
		this.head = this.chest.getChild("head");
		this.jaw = this.head.getChild("jaw");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData body = root.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -4.0F, 0.0F));

		ModelPartData leg3 = body.addChild("leg3", ModelPartBuilder.create().uv(0, 0).cuboid(-5.4F, -3.0F, 0.0F, 5.0F, 9.0F, 6.0F, new Dilation(0.0F))
			.uv(0, 34).cuboid(-5.4F, 4.0F, 1.0F, 4.0F, 12.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -12.0F, 9.0F));

		ModelPartData leg4 = body.addChild("leg4", ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(0.4F, -3.0F, 0.0F, 5.0F, 9.0F, 6.0F, new Dilation(0.0F)).mirrored(false)
			.uv(0, 34).mirrored().cuboid(1.4F, 4.0F, 1.0F, 4.0F, 12.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, -12.0F, 9.0F));

		ModelPartData chest = body.addChild("chest", ModelPartBuilder.create().uv(41, 14).cuboid(0.0F, 3.0F, -18.0F, 0.0F, 4.0F, 20.0F, new Dilation(0.0F))
			.uv(0, 0).cuboid(-5.0F, -8.0F, -18.0F, 10.0F, 11.0F, 23.0F, new Dilation(0.0F))
			.uv(0, 34).cuboid(-5.5F, -8.2F, -19.0F, 11.0F, 14.0F, 19.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -12.0F, 9.0F));

		ModelPartData leg1 = chest.addChild("leg1", ModelPartBuilder.create().uv(65, 38).cuboid(-5.0F, 0.0F, -2.0F, 4.0F, 13.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 3.0F, -16.0F));

		ModelPartData leg2 = chest.addChild("leg2", ModelPartBuilder.create().uv(65, 38).mirrored().cuboid(1.0F, 0.0F, -2.0F, 4.0F, 13.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 3.0F, -16.0F));

		ModelPartData tail = chest.addChild("tail", ModelPartBuilder.create().uv(66, 16).cuboid(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 7.0F, new Dilation(0.0F))
			.uv(41, 38).cuboid(-2.0F, 0.0F, 5.0F, 4.0F, 4.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -8.0F, 5.0F, -0.5672F, 0.0F, 0.0F));

		ModelPartData head = chest.addChild("head", ModelPartBuilder.create().uv(0, 67).cuboid(-2.5F, -9.1471F, -3.6696F, 5.0F, 14.0F, 8.0F, new Dilation(0.0F))
			.uv(34, 73).cuboid(0.0F, -9.1471F, -7.6696F, 0.0F, 10.0F, 4.0F, new Dilation(0.0F))
			.uv(26, 63).cuboid(0.0F, -14.1471F, 4.3304F, 0.0F, 16.0F, 4.0F, new Dilation(0.0F))
			.uv(51, 58).cuboid(-3.5F, -15.6471F, -4.2696F, 7.0F, 15.0F, 9.0F, new Dilation(0.0F))
			.uv(43, 0).cuboid(-3.0F, -15.1471F, -5.6696F, 6.0F, 6.0F, 10.0F, new Dilation(0.0F))
			.uv(41, 38).cuboid(-2.5F, -20.1471F, 2.3304F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F))
			.uv(41, 38).mirrored().cuboid(0.5F, -20.1471F, 2.3304F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)).mirrored(false)
			.uv(65, 0).cuboid(-2.0F, -14.1471F, -10.6696F, 4.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0F, -16.0F, 0.5236F, 0.0F, 0.0F));

		ModelPartData jaw = head.addChild("jaw", ModelPartBuilder.create().uv(0, 15).cuboid(-2.0F, 0.0F, -4.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -10.1471F, -5.6696F));

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
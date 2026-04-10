// Generated from amethyst_scarab/source/model.gltf for mods/external/blockbench/amethyst_scarab/
// Runtime texture is produced by ./texture_fix.py as ./model.png

import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class model extends EntityModel<Entity> {
	private final ModelPart root;
	private final ModelPart node_24;
	private final ModelPart scarab;
	private final ModelPart torso;
	private final ModelPart cube;
	private final ModelPart right_wing;
	private final ModelPart cube_2;
	private final ModelPart left_wing;
	private final ModelPart cube_3;
	private final ModelPart head;
	private final ModelPart cube_4;
	private final ModelPart cube_5;
	private final ModelPart cube_6;
	private final ModelPart cube_7;
	private final ModelPart cube_8;
	private final ModelPart cube_9;
	private final ModelPart left_leg;
	private final ModelPart cube_10;
	private final ModelPart cube_11;
	private final ModelPart cube_12;
	private final ModelPart right_leg;
	private final ModelPart cube_13;
	private final ModelPart cube_14;
	private final ModelPart cube_15;

	public model(ModelPart root) {
		this.root = root.getChild("root");
		this.node_24 = this.root.getChild("node_24");
		this.scarab = this.node_24.getChild("scarab");
		this.torso = this.scarab.getChild("torso");
		this.cube = this.torso.getChild("cube");
		this.right_wing = this.torso.getChild("right_wing");
		this.cube_2 = this.right_wing.getChild("cube_2");
		this.left_wing = this.torso.getChild("left_wing");
		this.cube_3 = this.left_wing.getChild("cube_3");
		this.head = this.scarab.getChild("head");
		this.cube_4 = this.head.getChild("cube_4");
		this.cube_5 = this.head.getChild("cube_5");
		this.cube_6 = this.head.getChild("cube_6");
		this.cube_7 = this.head.getChild("cube_7");
		this.cube_8 = this.head.getChild("cube_8");
		this.cube_9 = this.head.getChild("cube_9");
		this.left_leg = this.scarab.getChild("left_leg");
		this.cube_10 = this.left_leg.getChild("cube_10");
		this.cube_11 = this.left_leg.getChild("cube_11");
		this.cube_12 = this.left_leg.getChild("cube_12");
		this.right_leg = this.scarab.getChild("right_leg");
		this.cube_13 = this.right_leg.getChild("cube_13");
		this.cube_14 = this.right_leg.getChild("cube_14");
		this.cube_15 = this.right_leg.getChild("cube_15");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		ModelPartData node_24 = root.addChild("node_24", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData scarab = node_24.addChild("scarab", ModelPartBuilder.create(), ModelTransform.of(0.1459F, -0.4689F, 0.0942F, 3.1416F, 0.0F, 3.1416F));

		ModelPartData torso = scarab.addChild("torso", ModelPartBuilder.create(), ModelTransform.pivot(0.1481F, -2.8911F, -3.2862F));
		ModelPartData cube = torso.addChild("cube", ModelPartBuilder.create().uv(0, 0).cuboid(-2.3719F, -4.5711F, -7.4862F, 5.04F, 5.04F, 8.4F, new Dilation(0.0F)), ModelTransform.pivot(-0.1481F, 2.0511F, 3.2862F));

		ModelPartData right_wing = torso.addChild("right_wing", ModelPartBuilder.create(), ModelTransform.pivot(-2.58F, -2.5192F, -0.84F));
		ModelPartData cube_2 = right_wing.addChild("cube_2", ModelPartBuilder.create().uv(28, 0).cuboid(-4.2F, -0.085F, -5.04F, 4.2F, 0.001F, 8.4F, new Dilation(0.0F)), ModelTransform.pivot(0.06F, 0.084F, 0.84F));

		ModelPartData left_wing = torso.addChild("left_wing", ModelPartBuilder.create(), ModelTransform.pivot(2.46F, -2.5192F, -0.84F));
		ModelPartData cube_3 = left_wing.addChild("cube_3", ModelPartBuilder.create().uv(54, 0).cuboid(0.0F, -0.001F, -5.04F, 4.2F, 0.001F, 8.4F, new Dilation(0.0F)), ModelTransform.pivot(0.06F, 0.0F, 0.84F));

		ModelPartData head = scarab.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.1341F, -2.1569F, 0.6499F));
		ModelPartData cube_4 = head.addChild("cube_4", ModelPartBuilder.create().uv(80, 0).cuboid(-1.5319F, -3.7311F, 0.9138F, 3.36F, 3.36F, 3.36F, new Dilation(0.0F)), ModelTransform.pivot(-0.1341F, 2.1569F, -0.6499F));
		ModelPartData cube_5 = head.addChild("cube_5", ModelPartBuilder.create().uv(94, 0).cuboid(-1.386F, -0.924F, -1.47F, 5.04F, 3.024F, 0.001F, new Dilation(0.0F)), ModelTransform.of(1.316F, -1.2214F, 3.2295F, 0.3927F, 0.0F, 0.0F));
		ModelPartData cube_6 = head.addChild("cube_6", ModelPartBuilder.create().uv(108, 0).cuboid(-6.258F, -0.924F, -1.47F, 5.04F, 3.024F, 0.001F, new Dilation(0.0F)), ModelTransform.of(1.232F, -1.2214F, 3.2295F, 0.3927F, 0.0F, 0.0F));
		ModelPartData cube_7 = head.addChild("cube_7", ModelPartBuilder.create().uv(122, 0).cuboid(-3.5479F, -1.6321F, 2.4258F, 7.392F, 0.001F, 4.368F, new Dilation(0.0F)), ModelTransform.pivot(-0.1341F, 2.9129F, 0.2501F));
		ModelPartData cube_8 = head.addChild("cube_8", ModelPartBuilder.create().uv(146, 0).cuboid(-0.4536F, -1.2516F, -0.1764F, 0.8232F, 2.5032F, 0.0168F, new Dilation(0.0F)), ModelTransform.of(1.316F, -2.8025F, 3.2969F, 0.3927F, 0.0F, 0.0F));
		ModelPartData cube_9 = head.addChild("cube_9", ModelPartBuilder.create().uv(152, 0).cuboid(-2.9736F, -1.2516F, -0.1764F, 0.8232F, 2.5032F, 0.0168F, new Dilation(0.0F)), ModelTransform.of(1.316F, -2.8025F, 3.2969F, 0.3927F, 0.0F, 0.0F));

		ModelPartData left_leg = scarab.addChild("left_leg", ModelPartBuilder.create(), ModelTransform.pivot(2.4581F, -1.9911F, -3.2862F));
		ModelPartData cube_10 = left_leg.addChild("cube_10", ModelPartBuilder.create().uv(158, 0).cuboid(1.4081F, -1.7151F, -1.1862F, 1.26F, 2.52F, 1.68F, new Dilation(0.0F)), ModelTransform.pivot(-2.0381F, 1.6551F, 3.2862F));
		ModelPartData cube_11 = left_leg.addChild("cube_11", ModelPartBuilder.create().uv(166, 0).cuboid(1.4081F, -1.7151F, -4.1262F, 1.26F, 2.52F, 1.68F, new Dilation(0.0F)), ModelTransform.pivot(-2.0381F, 1.6551F, 3.2862F));
		ModelPartData cube_12 = left_leg.addChild("cube_12", ModelPartBuilder.create().uv(174, 0).cuboid(1.4081F, -1.7151F, -7.0662F, 1.26F, 2.52F, 1.68F, new Dilation(0.0F)), ModelTransform.pivot(-2.0381F, 1.6551F, 3.2862F));

		ModelPartData right_leg = scarab.addChild("right_leg", ModelPartBuilder.create(), ModelTransform.pivot(-2.1619F, -1.9911F, -3.2862F));
		ModelPartData cube_13 = right_leg.addChild("cube_13", ModelPartBuilder.create().uv(182, 0).cuboid(-2.0801F, -1.7151F, -1.1862F, 1.26F, 2.52F, 1.68F, new Dilation(0.0F)), ModelTransform.pivot(1.4501F, 1.6551F, 3.2862F));
		ModelPartData cube_14 = right_leg.addChild("cube_14", ModelPartBuilder.create().uv(190, 0).cuboid(-2.0801F, -1.7151F, -4.1262F, 1.26F, 2.52F, 1.68F, new Dilation(0.0F)), ModelTransform.pivot(1.4501F, 1.6551F, 3.2862F));
		ModelPartData cube_15 = right_leg.addChild("cube_15", ModelPartBuilder.create().uv(198, 0).cuboid(-2.0801F, -1.7151F, -7.0662F, 1.26F, 2.52F, 1.68F, new Dilation(0.0F)), ModelTransform.pivot(1.4501F, 1.6551F, 3.2862F));

		return TexturedModelData.of(modelData, 512, 16);
	}

	@Override
	public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		root.render(matrices, vertices, light, overlay, color);
	}
}
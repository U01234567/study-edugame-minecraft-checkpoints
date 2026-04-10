// Generated from the_god GLTF source for the blockbench creature package
// Texture is produced by texture_fix.py as ./model.png
// Source sidecar texture: ./textures/gltf_embedded_0.png

package com.example.mod;

public class the_god extends EntityModel<Entity> {
	private final ModelPart root;
	public the_god(ModelPart root) {
		this.root = root.getChild("root");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		ModelPartData node_146 = root.addChild("node_146", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
		ModelPartData Body = node_146.addChild("Body", ModelPartBuilder.create(), ModelTransform.of(0.0F, -8.5F, 0.0F, 3.1416F, -0.0F, 3.1416F));
		ModelPartData bone49 = Body.addChild("bone49", ModelPartBuilder.create(), ModelTransform.pivot(0.1975F, 0.375F, 0.1731F));
		ModelPartData bone49_2 = bone49.addChild("bone49_2", ModelPartBuilder.create().uv(0, 0).cuboid(-1.365F, -9.6875F, -1.3894F, 3.125F, 3.125F, 3.125F, new Dilation(0.0F)), ModelTransform.pivot(-0.1975F, 8.125F, -0.1731F));
		ModelPartData bone = Body.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
		ModelPartData bone3 = bone.addChild("bone3", ModelPartBuilder.create(), ModelTransform.of(-0.1379F, -0.5F, -0.0886F, -0.9698F, 0.625F, -0.4296F));
		ModelPartData bone4 = bone3.addChild("bone4", ModelPartBuilder.create(), ModelTransform.pivot(-1.55F, 4.0234F, 0.0F));
		ModelPartData bone5 = bone4.addChild("bone5", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, -4.375F, 0.0F));
		ModelPartData bone6 = bone5.addChild("bone6", ModelPartBuilder.create(), ModelTransform.of(-6.125F, -0.4375F, -0.375F, 0.0F, 0.0F, 1.9635F));
		ModelPartData bone7 = bone5.addChild("bone7", ModelPartBuilder.create(), ModelTransform.of(-5.4375F, 2.25F, 0.0F, -0.0F, 0.0F, -3.1416F));
		ModelPartData bone8 = bone4.addChild("bone8", ModelPartBuilder.create(), ModelTransform.of(2.1392F, -5.0985F, 0.375F, -0.0F, 0.0F, -2.0944F));
		ModelPartData bone9 = bone8.addChild("bone9", ModelPartBuilder.create(), ModelTransform.of(-3.3892F, -1.4015F, -0.375F, -0.0F, 0.0F, -3.1416F));
		ModelPartData bone10 = bone3.addChild("bone10", ModelPartBuilder.create(), ModelTransform.of(-4.3625F, -3.7266F, 0.0F, 0.0F, 0.0F, 2.0071F));
		ModelPartData bone11 = bone10.addChild("bone11", ModelPartBuilder.create(), ModelTransform.pivot(-2.1466F, -1.6058F, 0.0F));
		ModelPartData bone11_2 = bone11.addChild("bone11_2", ModelPartBuilder.create().uv(14, 0).cuboid(-3.875F, -1.0F, -0.5F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(3.625F, 0.875F, 0.0F, -0.0F, 0.0F, -0.0873F));
		ModelPartData bone11_3 = bone11.addChild("bone11_3", ModelPartBuilder.create().uv(30, 0).cuboid(-3.875F, -1.0F, -0.5F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.0F, 0.0F, 0.9599F));
		ModelPartData bone12 = bone10.addChild("bone12", ModelPartBuilder.create(), ModelTransform.of(3.7806F, -2.2935F, 0.0F, 3.1416F, -0.0F, 2.7489F));
		ModelPartData bone12_2 = bone12.addChild("bone12_2", ModelPartBuilder.create().uv(46, 0).cuboid(-3.875F, -1.0F, -0.75F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(2.4389F, 1.2074F, 0.0F, -0.0F, 0.0F, -0.0873F));
		ModelPartData bone12_3 = bone12.addChild("bone12_3", ModelPartBuilder.create().uv(62, 0).cuboid(-3.875F, -1.0F, -0.75F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(-1.4986F, 1.0199F, 0.0F, 0.0F, 0.0F, 0.7418F));
		ModelPartData bone13 = bone3.addChild("bone13", ModelPartBuilder.create(), ModelTransform.of(5.825F, 0.9609F, 0.0F, 3.1416F, -0.0F, 2.0071F));
		ModelPartData bone14 = bone13.addChild("bone14", ModelPartBuilder.create(), ModelTransform.pivot(-2.1466F, -1.6058F, 0.0F));
		ModelPartData bone14_2 = bone14.addChild("bone14_2", ModelPartBuilder.create().uv(78, 0).cuboid(-3.875F, -1.0F, -0.75F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(3.625F, 0.875F, 0.0F, -0.0F, 0.0F, -0.0873F));
		ModelPartData bone14_3 = bone14.addChild("bone14_3", ModelPartBuilder.create().uv(94, 0).cuboid(-3.875F, -1.0F, -0.75F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(-2.0F, -3.25F, 0.0F, 0.0F, 0.0F, 1.7017F));
		ModelPartData bone14_4 = bone14.addChild("bone14_4", ModelPartBuilder.create().uv(110, 0).cuboid(-3.875F, -1.0F, -0.5F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(8.0625F, -5.4375F, 0.0F, 3.1416F, 0.0F, -1.5272F));
		ModelPartData bone14_5 = bone14.addChild("bone14_5", ModelPartBuilder.create().uv(126, 0).cuboid(-3.875F, -1.0F, -0.75F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.0F, 0.0F, 0.9599F));
		ModelPartData bone15 = bone13.addChild("bone15", ModelPartBuilder.create(), ModelTransform.of(3.7806F, -2.2935F, 0.0F, 3.1416F, -0.0F, 2.7489F));
		ModelPartData bone15_2 = bone15.addChild("bone15_2", ModelPartBuilder.create().uv(142, 0).cuboid(-3.875F, -1.0F, -0.5F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(2.4389F, 1.2074F, 0.0F, -0.0F, 0.0F, -0.0873F));
		ModelPartData bone15_3 = bone15.addChild("bone15_3", ModelPartBuilder.create().uv(158, 0).cuboid(-3.875F, -1.0F, -0.5F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(-1.4986F, 1.0199F, 0.0F, 0.0F, 0.0F, 0.7418F));
		ModelPartData bone16 = bone.addChild("bone16", ModelPartBuilder.create(), ModelTransform.pivot(2.5496F, -1.125F, -4.5886F));
		ModelPartData bone16_2 = bone16.addChild("bone16_2", ModelPartBuilder.create().uv(174, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-1.75F, -3.25F, 6.8125F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone16_3 = bone16.addChild("bone16_3", ModelPartBuilder.create().uv(180, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-4.25F, 4.1875F, 0.75F, -0.3054F, -0.48F, 0.7418F));
		ModelPartData bone16_4 = bone16.addChild("bone16_4", ModelPartBuilder.create().uv(186, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-6.875F, 3.125F, 5.5625F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone16_5 = bone16.addChild("bone16_5", ModelPartBuilder.create().uv(192, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-5.4375F, 0.0F, 7.6875F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone16_6 = bone16.addChild("bone16_6", ModelPartBuilder.create().uv(198, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(0.25F, -3.375F, 4.25F, 0.0F, -0.48F, 0.7418F));
		ModelPartData bone16_7 = bone16.addChild("bone16_7", ModelPartBuilder.create().uv(204, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone17 = bone.addChild("bone17", ModelPartBuilder.create(), ModelTransform.pivot(0.1698F, -0.5029F, 0.6214F));
		ModelPartData bone17_2 = bone17.addChild("bone17_2", ModelPartBuilder.create().uv(210, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(1.0673F, -3.2471F, 2.04F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone17_3 = bone17.addChild("bone17_3", ModelPartBuilder.create().uv(216, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-1.4327F, 4.0654F, -4.085F, -0.3054F, -0.48F, 0.7418F));
		ModelPartData bone17_4 = bone17.addChild("bone17_4", ModelPartBuilder.create().uv(222, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-3.9327F, 2.9404F, 0.79F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone17_5 = bone17.addChild("bone17_5", ModelPartBuilder.create().uv(228, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-2.6202F, -0.0596F, 2.915F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone17_6 = bone17.addChild("bone17_6", ModelPartBuilder.create().uv(234, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(2.9423F, -3.4971F, -0.46F, 0.0F, -0.48F, 0.7418F));
		ModelPartData bone17_7 = bone17.addChild("bone17_7", ModelPartBuilder.create().uv(240, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(2.8173F, 0.2529F, -5.0225F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone2 = Body.addChild("bone2", ModelPartBuilder.create(), ModelTransform.of(0.0F, -0.0F, 0.0F, -1.11F, -0.1393F, 1.3199F));
		ModelPartData bone18 = bone2.addChild("bone18", ModelPartBuilder.create(), ModelTransform.of(-0.1379F, -0.5F, -0.0886F, -0.9698F, 0.625F, -0.4296F));
		ModelPartData bone19 = bone18.addChild("bone19", ModelPartBuilder.create(), ModelTransform.pivot(-1.55F, 4.0234F, 0.0F));
		ModelPartData bone20 = bone19.addChild("bone20", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, -4.375F, 0.0F));
		ModelPartData bone21 = bone20.addChild("bone21", ModelPartBuilder.create(), ModelTransform.of(-6.125F, -0.4375F, -0.375F, 0.0F, 0.0F, 1.9635F));
		ModelPartData bone22 = bone20.addChild("bone22", ModelPartBuilder.create(), ModelTransform.of(-5.4375F, 2.25F, 0.0F, -0.0F, 0.0F, -3.1416F));
		ModelPartData bone23 = bone19.addChild("bone23", ModelPartBuilder.create(), ModelTransform.of(2.1392F, -5.0985F, 0.375F, -0.0F, 0.0F, -2.0944F));
		ModelPartData bone24 = bone23.addChild("bone24", ModelPartBuilder.create(), ModelTransform.of(-3.3892F, -1.4015F, -0.375F, -0.0F, 0.0F, -3.1416F));
		ModelPartData bone25 = bone18.addChild("bone25", ModelPartBuilder.create(), ModelTransform.of(-4.3625F, -3.7266F, 0.0F, 0.0F, 0.0F, 2.0071F));
		ModelPartData bone26 = bone25.addChild("bone26", ModelPartBuilder.create(), ModelTransform.pivot(-2.1466F, -1.6058F, 0.0F));
		ModelPartData bone26_2 = bone26.addChild("bone26_2", ModelPartBuilder.create().uv(246, 0).cuboid(-3.875F, -1.0F, -0.5F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(3.625F, 0.875F, 0.0F, -0.0F, 0.0F, -0.0873F));
		ModelPartData bone26_3 = bone26.addChild("bone26_3", ModelPartBuilder.create().uv(262, 0).cuboid(-3.875F, -1.0F, -0.5F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.0F, 0.0F, 0.9599F));
		ModelPartData bone27 = bone25.addChild("bone27", ModelPartBuilder.create(), ModelTransform.of(3.7806F, -2.2935F, 0.0F, 3.1416F, -0.0F, 2.7489F));
		ModelPartData bone27_2 = bone27.addChild("bone27_2", ModelPartBuilder.create().uv(278, 0).cuboid(-3.875F, -1.0F, -0.75F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(2.4389F, 1.2074F, 0.0F, -0.0F, 0.0F, -0.0873F));
		ModelPartData bone27_3 = bone27.addChild("bone27_3", ModelPartBuilder.create().uv(294, 0).cuboid(-3.875F, -1.0F, -0.75F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(-1.4986F, 1.0199F, 0.0F, 0.0F, 0.0F, 0.7418F));
		ModelPartData bone28 = bone18.addChild("bone28", ModelPartBuilder.create(), ModelTransform.of(5.825F, 0.9609F, 0.0F, 3.1416F, -0.0F, 2.0071F));
		ModelPartData bone29 = bone28.addChild("bone29", ModelPartBuilder.create(), ModelTransform.pivot(-2.1466F, -1.6058F, 0.0F));
		ModelPartData bone29_2 = bone29.addChild("bone29_2", ModelPartBuilder.create().uv(310, 0).cuboid(-3.875F, -1.0F, -0.75F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(3.625F, 0.875F, 0.0F, -0.0F, 0.0F, -0.0873F));
		ModelPartData bone29_3 = bone29.addChild("bone29_3", ModelPartBuilder.create().uv(326, 0).cuboid(-3.875F, -1.0F, -0.75F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(-2.0F, -3.25F, 0.0F, 0.0F, 0.0F, 1.7017F));
		ModelPartData bone29_4 = bone29.addChild("bone29_4", ModelPartBuilder.create().uv(342, 0).cuboid(-3.875F, -1.0F, -0.5F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(8.0625F, -5.4375F, 0.0F, 3.1416F, 0.0F, -1.5272F));
		ModelPartData bone29_5 = bone29.addChild("bone29_5", ModelPartBuilder.create().uv(358, 0).cuboid(-3.875F, -1.0F, -0.75F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.0F, 0.0F, 0.9599F));
		ModelPartData bone30 = bone28.addChild("bone30", ModelPartBuilder.create(), ModelTransform.of(3.7806F, -2.2935F, 0.0F, 3.1416F, -0.0F, 2.7489F));
		ModelPartData bone30_2 = bone30.addChild("bone30_2", ModelPartBuilder.create().uv(374, 0).cuboid(-3.875F, -1.0F, -0.5F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(2.4389F, 1.2074F, 0.0F, -0.0F, 0.0F, -0.0873F));
		ModelPartData bone30_3 = bone30.addChild("bone30_3", ModelPartBuilder.create().uv(390, 0).cuboid(-3.875F, -1.0F, -0.5F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(-1.4986F, 1.0199F, 0.0F, 0.0F, 0.0F, 0.7418F));
		ModelPartData bone31 = bone2.addChild("bone31", ModelPartBuilder.create(), ModelTransform.pivot(2.5496F, -1.125F, -4.5886F));
		ModelPartData bone31_2 = bone31.addChild("bone31_2", ModelPartBuilder.create().uv(406, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-1.75F, -3.25F, 6.8125F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone31_3 = bone31.addChild("bone31_3", ModelPartBuilder.create().uv(412, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-4.25F, 4.1875F, 0.75F, -0.3054F, -0.48F, 0.7418F));
		ModelPartData bone31_4 = bone31.addChild("bone31_4", ModelPartBuilder.create().uv(418, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-6.875F, 3.125F, 5.5625F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone31_5 = bone31.addChild("bone31_5", ModelPartBuilder.create().uv(424, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-5.4375F, 0.0F, 7.6875F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone31_6 = bone31.addChild("bone31_6", ModelPartBuilder.create().uv(430, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(0.25F, -3.375F, 4.25F, 0.0F, -0.48F, 0.7418F));
		ModelPartData bone31_7 = bone31.addChild("bone31_7", ModelPartBuilder.create().uv(436, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone32 = bone2.addChild("bone32", ModelPartBuilder.create(), ModelTransform.pivot(0.1698F, -0.5029F, 0.6214F));
		ModelPartData bone32_2 = bone32.addChild("bone32_2", ModelPartBuilder.create().uv(442, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(1.0673F, -3.2471F, 2.04F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone32_3 = bone32.addChild("bone32_3", ModelPartBuilder.create().uv(448, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-1.4327F, 4.0654F, -4.085F, -0.3054F, -0.48F, 0.7418F));
		ModelPartData bone32_4 = bone32.addChild("bone32_4", ModelPartBuilder.create().uv(454, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-3.9327F, 2.9404F, 0.79F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone32_5 = bone32.addChild("bone32_5", ModelPartBuilder.create().uv(460, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-2.6202F, -0.0596F, 2.915F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone32_6 = bone32.addChild("bone32_6", ModelPartBuilder.create().uv(466, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(2.9423F, -3.4971F, -0.46F, 0.0F, -0.48F, 0.7418F));
		ModelPartData bone32_7 = bone32.addChild("bone32_7", ModelPartBuilder.create().uv(472, 0).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(2.8173F, 0.2529F, -5.0225F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone33 = Body.addChild("bone33", ModelPartBuilder.create(), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.7983F, -0.9667F, 1.9353F));
		ModelPartData bone34 = bone33.addChild("bone34", ModelPartBuilder.create(), ModelTransform.of(-0.1379F, -0.5F, -0.0886F, -0.9698F, 0.625F, -0.4296F));
		ModelPartData bone35 = bone34.addChild("bone35", ModelPartBuilder.create(), ModelTransform.pivot(-1.55F, 4.0234F, 0.0F));
		ModelPartData bone36 = bone35.addChild("bone36", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, -4.375F, 0.0F));
		ModelPartData bone37 = bone36.addChild("bone37", ModelPartBuilder.create(), ModelTransform.of(-6.125F, -0.4375F, -0.375F, 0.0F, 0.0F, 1.9635F));
		ModelPartData bone38 = bone36.addChild("bone38", ModelPartBuilder.create(), ModelTransform.of(-5.4375F, 2.25F, 0.0F, -0.0F, 0.0F, -3.1416F));
		ModelPartData bone39 = bone35.addChild("bone39", ModelPartBuilder.create(), ModelTransform.of(2.1392F, -5.0985F, 0.375F, -0.0F, 0.0F, -2.0944F));
		ModelPartData bone40 = bone39.addChild("bone40", ModelPartBuilder.create(), ModelTransform.of(-3.3892F, -1.4015F, -0.375F, -0.0F, 0.0F, -3.1416F));
		ModelPartData bone41 = bone34.addChild("bone41", ModelPartBuilder.create(), ModelTransform.of(-4.3625F, -3.7266F, 0.0F, 0.0F, 0.0F, 2.0071F));
		ModelPartData bone42 = bone41.addChild("bone42", ModelPartBuilder.create(), ModelTransform.pivot(-2.1466F, -1.6058F, 0.0F));
		ModelPartData bone42_2 = bone42.addChild("bone42_2", ModelPartBuilder.create().uv(478, 0).cuboid(-3.875F, -1.0F, -0.5F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(3.625F, 0.875F, 0.0F, -0.0F, 0.0F, -0.0873F));
		ModelPartData bone42_3 = bone42.addChild("bone42_3", ModelPartBuilder.create().uv(494, 0).cuboid(-3.875F, -1.0F, -0.5F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.0F, 0.0F, 0.9599F));
		ModelPartData bone43 = bone41.addChild("bone43", ModelPartBuilder.create(), ModelTransform.of(3.7806F, -2.2935F, 0.0F, 3.1416F, -0.0F, 2.7489F));
		ModelPartData bone43_2 = bone43.addChild("bone43_2", ModelPartBuilder.create().uv(0, 8).cuboid(-3.875F, -1.0F, -0.75F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(2.4389F, 1.2074F, 0.0F, -0.0F, 0.0F, -0.0873F));
		ModelPartData bone43_3 = bone43.addChild("bone43_3", ModelPartBuilder.create().uv(16, 8).cuboid(-3.875F, -1.0F, -0.75F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(-1.4986F, 1.0199F, 0.0F, 0.0F, 0.0F, 0.7418F));
		ModelPartData bone44 = bone34.addChild("bone44", ModelPartBuilder.create(), ModelTransform.of(5.825F, 0.9609F, 0.0F, 3.1416F, -0.0F, 2.0071F));
		ModelPartData bone45 = bone44.addChild("bone45", ModelPartBuilder.create(), ModelTransform.pivot(-2.1466F, -1.6058F, 0.0F));
		ModelPartData bone45_2 = bone45.addChild("bone45_2", ModelPartBuilder.create().uv(32, 8).cuboid(-3.875F, -1.0F, -0.75F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(3.625F, 0.875F, 0.0F, -0.0F, 0.0F, -0.0873F));
		ModelPartData bone45_3 = bone45.addChild("bone45_3", ModelPartBuilder.create().uv(48, 8).cuboid(-3.875F, -1.0F, -0.75F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(-2.0F, -3.25F, 0.0F, 0.0F, 0.0F, 1.7017F));
		ModelPartData bone45_4 = bone45.addChild("bone45_4", ModelPartBuilder.create().uv(64, 8).cuboid(-3.875F, -1.0F, -0.5F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(8.0625F, -5.4375F, 0.0F, 3.1416F, 0.0F, -1.5272F));
		ModelPartData bone45_5 = bone45.addChild("bone45_5", ModelPartBuilder.create().uv(80, 8).cuboid(-3.875F, -1.0F, -0.75F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.0F, 0.0F, 0.9599F));
		ModelPartData bone46 = bone44.addChild("bone46", ModelPartBuilder.create(), ModelTransform.of(3.7806F, -2.2935F, 0.0F, 3.1416F, -0.0F, 2.7489F));
		ModelPartData bone46_2 = bone46.addChild("bone46_2", ModelPartBuilder.create().uv(96, 8).cuboid(-3.875F, -1.0F, -0.5F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(2.4389F, 1.2074F, 0.0F, -0.0F, 0.0F, -0.0873F));
		ModelPartData bone46_3 = bone46.addChild("bone46_3", ModelPartBuilder.create().uv(112, 8).cuboid(-3.875F, -1.0F, -0.5F, 5.6875F, 1.0F, 1.25F, new Dilation(0.0F)), ModelTransform.of(-1.4986F, 1.0199F, 0.0F, 0.0F, 0.0F, 0.7418F));
		ModelPartData bone47 = bone33.addChild("bone47", ModelPartBuilder.create(), ModelTransform.pivot(2.5496F, -1.125F, -4.5886F));
		ModelPartData bone47_2 = bone47.addChild("bone47_2", ModelPartBuilder.create().uv(128, 8).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-1.75F, -3.25F, 6.8125F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone47_3 = bone47.addChild("bone47_3", ModelPartBuilder.create().uv(134, 8).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-4.25F, 4.1875F, 0.75F, -0.3054F, -0.48F, 0.7418F));
		ModelPartData bone47_4 = bone47.addChild("bone47_4", ModelPartBuilder.create().uv(140, 8).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-6.875F, 3.125F, 5.5625F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone47_5 = bone47.addChild("bone47_5", ModelPartBuilder.create().uv(146, 8).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-5.4375F, 0.0F, 7.6875F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone47_6 = bone47.addChild("bone47_6", ModelPartBuilder.create().uv(152, 8).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(0.25F, -3.375F, 4.25F, 0.0F, -0.48F, 0.7418F));
		ModelPartData bone47_7 = bone47.addChild("bone47_7", ModelPartBuilder.create().uv(158, 8).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone48 = bone33.addChild("bone48", ModelPartBuilder.create(), ModelTransform.pivot(0.1698F, -0.5029F, 0.6214F));
		ModelPartData bone48_2 = bone48.addChild("bone48_2", ModelPartBuilder.create().uv(164, 8).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(1.0673F, -3.2471F, 2.04F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone48_3 = bone48.addChild("bone48_3", ModelPartBuilder.create().uv(170, 8).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-1.4327F, 4.0654F, -4.085F, -0.3054F, -0.48F, 0.7418F));
		ModelPartData bone48_4 = bone48.addChild("bone48_4", ModelPartBuilder.create().uv(176, 8).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-3.9327F, 2.9404F, 0.79F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone48_5 = bone48.addChild("bone48_5", ModelPartBuilder.create().uv(182, 8).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(-2.6202F, -0.0596F, 2.915F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone48_6 = bone48.addChild("bone48_6", ModelPartBuilder.create().uv(188, 8).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(2.9423F, -3.4971F, -0.46F, 0.0F, -0.48F, 0.7418F));
		ModelPartData bone48_7 = bone48.addChild("bone48_7", ModelPartBuilder.create().uv(194, 8).cuboid(-0.0625F, -0.875F, -0.0625F, 0.875F, 0.875F, 0.875F, new Dilation(0.0F)), ModelTransform.of(2.8173F, 0.2529F, -5.0225F, -0.6981F, -0.48F, 0.7418F));
		ModelPartData bone50 = Body.addChild("bone50", ModelPartBuilder.create(), ModelTransform.of(9.0312F, -8.3698F, -1.3125F, -0.0F, 0.0F, -2.3562F));
		ModelPartData bone55 = bone50.addChild("bone55", ModelPartBuilder.create(), ModelTransform.pivot(-5.0312F, -4.1146F, -0.0625F));
		ModelPartData bone55_2 = bone55.addChild("bone55_2", ModelPartBuilder.create().uv(200, 8).cuboid(3.9375F, -29.2344F, -1.3125F, 10.1875F, 8.25F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, 20.9844F, 1.375F));
		ModelPartData bone56 = bone50.addChild("bone56", ModelPartBuilder.create(), ModelTransform.pivot(-5.0312F, 4.1198F, -0.0625F));
		ModelPartData bone56_2 = bone56.addChild("bone56_2", ModelPartBuilder.create().uv(224, 8).cuboid(3.9375F, -21.0F, -1.3125F, 10.1875F, 8.25F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, 12.75F, 1.375F));
		ModelPartData bone57 = bone50.addChild("bone57", ModelPartBuilder.create(), ModelTransform.pivot(-5.0312F, 12.3698F, -0.0625F));
		ModelPartData bone57_2 = bone57.addChild("bone57_2", ModelPartBuilder.create().uv(248, 8).cuboid(3.9375F, -12.75F, -1.3125F, 10.1875F, 8.25F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, 4.5F, 1.375F));
		ModelPartData bone51 = Body.addChild("bone51", ModelPartBuilder.create(), ModelTransform.of(-7.9688F, -8.3698F, -1.3125F, 3.1416F, 0.0F, -0.7854F));
		ModelPartData bone58 = bone51.addChild("bone58", ModelPartBuilder.create(), ModelTransform.pivot(-5.0312F, -4.1146F, -0.0625F));
		ModelPartData bone58_2 = bone58.addChild("bone58_2", ModelPartBuilder.create().uv(272, 8).cuboid(-13.0625F, -29.2344F, -1.3125F, 10.1875F, 8.25F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(13.0F, 20.9844F, 1.375F));
		ModelPartData bone59 = bone51.addChild("bone59", ModelPartBuilder.create(), ModelTransform.pivot(-5.0312F, 4.1198F, -0.0625F));
		ModelPartData bone59_2 = bone59.addChild("bone59_2", ModelPartBuilder.create().uv(296, 8).cuboid(-13.0625F, -21.0F, -1.3125F, 10.1875F, 8.25F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(13.0F, 12.75F, 1.375F));
		ModelPartData bone60 = bone51.addChild("bone60", ModelPartBuilder.create(), ModelTransform.pivot(-5.0312F, 12.3698F, -0.0625F));
		ModelPartData bone60_2 = bone60.addChild("bone60_2", ModelPartBuilder.create().uv(320, 8).cuboid(-13.0625F, -12.75F, -1.3125F, 10.1875F, 8.25F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(13.0F, 4.5F, 1.375F));
		ModelPartData bone54 = Body.addChild("bone54", ModelPartBuilder.create(), ModelTransform.of(0.0F, -0.0F, 0.0F, 3.1416F, 0.0F, 0.0F));
		ModelPartData bone52 = bone54.addChild("bone52", ModelPartBuilder.create(), ModelTransform.of(9.0312F, -3.5573F, 1.6875F, -0.0F, 0.0F, -1.8326F));
		ModelPartData bone65 = bone52.addChild("bone65", ModelPartBuilder.create(), ModelTransform.pivot(-5.0312F, 4.1198F, -0.0625F));
		ModelPartData bone65_2 = bone65.addChild("bone65_2", ModelPartBuilder.create().uv(344, 8).cuboid(3.9375F, -24.4219F, 1.6875F, 10.1875F, 8.25F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, 7.9375F, -1.625F));
		ModelPartData bone64 = bone52.addChild("bone64", ModelPartBuilder.create(), ModelTransform.pivot(-5.0312F, 4.1198F, -0.0625F));
		ModelPartData bone64_2 = bone64.addChild("bone64_2", ModelPartBuilder.create().uv(368, 8).cuboid(3.9375F, -16.1875F, 1.6875F, 10.1875F, 8.25F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, 7.9375F, -1.625F));
		ModelPartData bone66 = bone52.addChild("bone66", ModelPartBuilder.create(), ModelTransform.pivot(-5.0312F, 12.3698F, -0.0625F));
		ModelPartData bone66_2 = bone66.addChild("bone66_2", ModelPartBuilder.create().uv(392, 8).cuboid(3.9375F, -7.9375F, 1.6875F, 10.1875F, 8.25F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, -0.3125F, -1.625F));
		ModelPartData bone53 = bone54.addChild("bone53", ModelPartBuilder.create(), ModelTransform.of(-7.9688F, -3.4948F, 1.5625F, 3.1416F, 0.0F, -1.309F));
		ModelPartData bone61 = bone53.addChild("bone61", ModelPartBuilder.create(), ModelTransform.pivot(-5.0312F, 4.1198F, -0.0625F));
		ModelPartData bone61_2 = bone61.addChild("bone61_2", ModelPartBuilder.create().uv(416, 8).cuboid(-13.0625F, -16.125F, 1.5625F, 10.1875F, 8.25F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(13.0F, 7.875F, -1.5F));
		ModelPartData bone63 = bone61.addChild("bone63", ModelPartBuilder.create(), ModelTransform.pivot(13.0F, -0.625F, -1.5F));
		ModelPartData bone63_2 = bone63.addChild("bone63_2", ModelPartBuilder.create().uv(440, 8).cuboid(-13.0625F, -24.3594F, 1.5625F, 10.1875F, 8.25F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.5F, 0.0F));
		ModelPartData bone62 = bone53.addChild("bone62", ModelPartBuilder.create(), ModelTransform.pivot(-5.0312F, 4.1198F, -0.0625F));
		ModelPartData bone62_2 = bone62.addChild("bone62_2", ModelPartBuilder.create().uv(464, 8).cuboid(-13.0625F, -7.875F, 1.5625F, 10.1875F, 8.25F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(13.0F, 7.875F, -1.5F));
		return TexturedModelData.of(modelData, 512, 32);
	}
	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
		root.render(matrices, vertexConsumer, light, overlay, color);
	}
}
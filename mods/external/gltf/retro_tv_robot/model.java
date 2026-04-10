// Generated from retro_tv_robot/source/model.gltf as a Class B blockbench package source.
// Runtime texture is produced by texture_fix.py as ./model.png from ./textures/gltf_embedded_0.png.
// This package keeps the GLTF hierarchy and animations, but normalizes the texture to Minecraft cuboid UV expectations.

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

	public model(ModelPart root) {
		this.root = root.getChild("root");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		ModelPartData hip = root.addChild("hip", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -19.5F, 0.0F));
		ModelPartData screen = hip.addChild("screen", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData left_antenna = screen.addChild("left_antenna", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -10.5F, 2.5F));
		ModelPartData part_1c3be9a4_1f34_8bfd_676b_67585bd4d830 = left_antenna.addChild("1c3be9a4-1f34-8bfd-676b-67585bd4d830", ModelPartBuilder.create().uv(26, 27).cuboid(-2.0F, -10.0F, 0.0F, 2.0F, 10.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData right_antenna = screen.addChild("right_antenna", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -10.5F, 2.5F));
		ModelPartData part_667e4006_6f1d_7b88_9a1b_975af1440d36 = right_antenna.addChild("667e4006-6f1d-7b88-9a1b-975af1440d36", ModelPartBuilder.create().uv(26, 27).cuboid(0.0F, -10.0F, 0.0F, 2.0F, 10.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData part_65d07b6b_9a99_1b55_d133_4f5ae859eb65 = screen.addChild("65d07b6b-9a99-1b55-d133-4f5ae859eb65", ModelPartBuilder.create().uv(0, 17).cuboid(-5.0F, -7.0F, 1.0F, 10.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 3.0F));
		ModelPartData add334ef_9e96_cd58_f57a_b1ebf1f89836 = screen.addChild("add334ef-9e96-cd58-f57a-b1ebf1f89836", ModelPartBuilder.create().uv(20, 27).cuboid(-4.0F, -9.0F, -4.0F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.5F, -1.0F, 0.0F));
		ModelPartData part_95de2277_d52e_9f17_f901_c241b8ce887b = screen.addChild("95de2277-d52e-9f17-f901-c241b8ce887b", ModelPartBuilder.create().uv(24, 17).cuboid(-4.0F, -1.0F, -3.0F, 9.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, 0.0F, -1.0F));
		ModelPartData ee74975d_5206_6d30_f83b_5ac8b4da6150 = screen.addChild("ee74975d-5206-6d30-f83b-5ac8b4da6150", ModelPartBuilder.create().uv(14, 25).cuboid(-4.0F, -9.0F, -4.0F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(8.5F, -1.0F, 0.0F));
		ModelPartData part_47837697_36a4_9f05_07e8_38dbc12f0631 = screen.addChild("47837697-36a4-9f05-07e8-38dbc12f0631", ModelPartBuilder.create().uv(23, 24).cuboid(-4.0F, -1.0F, -3.0F, 9.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, -9.0F, -1.0F));
		ModelPartData a7123190_ff3c_37b3_3716_60342d20bb7e = screen.addChild("a7123190-ff3c-37b3-3716-60342d20bb7e", ModelPartBuilder.create().uv(34, 36).cuboid(-1.0F, -1.0F, 2.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -9.5F, -0.5F));
		ModelPartData part_7682accd_4b25_8cf7_c426_dca0ca6fc323 = screen.addChild("7682accd-4b25-8cf7-c426-dca0ca6fc323", ModelPartBuilder.create().uv(0, 25).cuboid(-3.0F, -8.0F, -3.0F, 6.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -0.5F));
		ModelPartData cd5274bd_90fd_f51d_dfc6_3baeabe15416 = screen.addChild("cd5274bd-90fd-f51d-dfc6-3baeabe15416", ModelPartBuilder.create().uv(0, 33).cuboid(-2.0F, -6.0F, 0.0F, 2.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, -2.0F, -3.5F, 0.0F, 0.3054F, 0.0F));
		ModelPartData part_17b60945_aafa_2051_7481_87338f458565 = screen.addChild("17b60945-aafa-2051-7481-87338f458565", ModelPartBuilder.create().uv(30, 30).cuboid(0.0F, -6.0F, 0.0F, 2.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, -2.0F, -3.5F, 0.0F, -0.3054F, 0.0F));
		ModelPartData part_5a68c52d_c005_e87f_ddea_2bd866151e0c = screen.addChild("5a68c52d-c005-e87f-ddea-2bd866151e0c", ModelPartBuilder.create().uv(33, 20).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, 1.5F, 1.0F));
		ModelPartData part_01684ba1_bba8_45f9_6109_657a96e147d1 = screen.addChild("01684ba1-bba8-45f9-6109-657a96e147d1", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -10.0F, -3.0F, 12.0F, 10.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.5F, 0.0F));
		ModelPartData part_4944c56a_157d_c61a_1034_7c86dc669720 = screen.addChild("4944c56a-157d-c61a-1034-7c86dc669720", ModelPartBuilder.create().uv(33, 20).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, 1.5F, 1.0F));
		ModelPartData left_leg = hip.addChild("left_leg", ModelPartBuilder.create(), ModelTransform.pivot(-3.0F, 1.5F, 1.0F));
		ModelPartData left_knee = left_leg.addChild("left_knee", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 7.0F, 0.0F));
		ModelPartData left_foot = left_knee.addChild("left_foot", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 8.0F, 0.0F));
		ModelPartData part_55bd2d48_d3ae_1e1f_9562_d020724ce561 = left_foot.addChild("55bd2d48-d3ae-1e1f-9562-d020724ce561", ModelPartBuilder.create().uv(30, 27).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, 1.0F, -0.5F));
		ModelPartData ac1a6714_9407_5393_c544_dc6137b57543 = left_foot.addChild("ac1a6714-9407-5393-c544-dc6137b57543", ModelPartBuilder.create().uv(31, 4).cuboid(0.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 2.0F, 0.0F));
		ModelPartData c5024e01_c402_f177_fd80_3c49f8dc1046 = left_foot.addChild("c5024e01-c402-f177-fd80-3c49f8dc1046", ModelPartBuilder.create().uv(24, 20).cuboid(-1.0F, 0.0F, -2.0F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, 2.0F, -2.0F));
		ModelPartData ab049004_395a_b7e7_3396_58db6c3e6cb8 = left_foot.addChild("ab049004-395a-b7e7-3396-58db6c3e6cb8", ModelPartBuilder.create().uv(10, 33).cuboid(1.0F, 0.0F, -2.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, 2.0F, -3.0F));
		ModelPartData a1c8d766_a5ef_8924_a2e5_e0dbf9fd0d3d = left_knee.addChild("a1c8d766-a5ef-8924-a2e5-e0dbf9fd0d3d", ModelPartBuilder.create().uv(6, 33).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, 1.0F, -0.5F));
		ModelPartData ab2c2934_04a2_186b_2508_3efc16748daa = left_knee.addChild("ab2c2934-04a2-186b-2508-3efc16748daa", ModelPartBuilder.create().uv(31, 0).cuboid(-1.0F, 5.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 0.0F));
		ModelPartData part_74613ac6_2eed_5594_8cb5_008682551ef9 = left_leg.addChild("74613ac6-2eed-5594-8cb5-008682551ef9", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, 1.0F, -0.5F));
		ModelPartData part_9674e3ff_8017_384f_967c_e367c33567b0 = left_leg.addChild("9674e3ff-8017-384f-967c-e367c33567b0", ModelPartBuilder.create().uv(35, 27).cuboid(-1.0F, 5.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 0.0F));
		ModelPartData right_leg = hip.addChild("right_leg", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, 1.5F, 1.0F));
		ModelPartData right_knee = right_leg.addChild("right_knee", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 7.0F, 0.0F));
		ModelPartData right_foot = right_knee.addChild("right_foot", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 8.0F, 0.0F));
		ModelPartData part_80ee72a7_1720_7cfd_e7cf_ef5736a2d406 = right_foot.addChild("80ee72a7-1720-7cfd-e7cf-ef5736a2d406", ModelPartBuilder.create().uv(30, 27).cuboid(-1.0F, -1.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 1.0F, -0.5F));
		ModelPartData c295dcfa_8e14_c865_893e_76cefdf63acc = right_foot.addChild("c295dcfa-8e14-c865-893e-76cefdf63acc", ModelPartBuilder.create().uv(24, 20).cuboid(-2.0F, 0.0F, -2.0F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 2.0F, -2.0F));
		ModelPartData part_36722947_ad3a_e764_ca7f_96566ebb4d4d = right_foot.addChild("36722947-ad3a-e764-ca7f-96566ebb4d4d", ModelPartBuilder.create().uv(10, 33).cuboid(-2.0F, 0.0F, -2.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 2.0F, -3.0F));
		ModelPartData part_5301d843_2256_a798_7f8d_276248a36886 = right_foot.addChild("5301d843-2256-a798-7f8d-276248a36886", ModelPartBuilder.create().uv(31, 4).cuboid(-2.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, 2.0F, 0.0F));
		ModelPartData e9b2c144_93e3_c82d_9a1c_1f55b42c8855 = right_knee.addChild("e9b2c144-93e3-c82d-9a1c-1f55b42c8855", ModelPartBuilder.create().uv(6, 33).cuboid(-1.0F, -1.0F, 0.0F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 1.0F, -0.5F));
		ModelPartData part_0e7c951d_307d_96c0_8bdb_0662ab8be9b7 = right_knee.addChild("0e7c951d-307d-96c0-8bdb-0662ab8be9b7", ModelPartBuilder.create().uv(31, 0).cuboid(-1.0F, 5.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 0.0F));
		ModelPartData part_0a0b8bce_09f6_6341_2b25_26ad73ea9da2 = right_leg.addChild("0a0b8bce-09f6-6341-2b25-26ad73ea9da2", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -1.0F, 0.0F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 1.0F, -0.5F));
		ModelPartData a21df43f_b60c_a0ec_0c71_76277f91058d = right_leg.addChild("a21df43f-b60c-a0ec-0c71-76277f91058d", ModelPartBuilder.create().uv(35, 27).cuboid(-1.0F, 5.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

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
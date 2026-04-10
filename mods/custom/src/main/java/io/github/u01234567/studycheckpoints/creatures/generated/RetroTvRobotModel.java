package io.github.u01234567.studycheckpoints.creatures.generated;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/**
 * Generated from external creature assets.
 * Creature id: retro_tv_robot
 */
public class RetroTvRobotModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart hip;
    private final ModelPart screen;
    private final ModelPart left_antenna;
    private final ModelPart p_1c3be9a4_1f34_8bfd_676b_67585bd4d830;
    private final ModelPart right_antenna;
    private final ModelPart p_667e4006_6f1d_7b88_9a1b_975af1440d36;
    private final ModelPart p_65d07b6b_9a99_1b55_d133_4f5ae859eb65;
    private final ModelPart add334ef_9e96_cd58_f57a_b1ebf1f89836;
    private final ModelPart p_95de2277_d52e_9f17_f901_c241b8ce887b;
    private final ModelPart ee74975d_5206_6d30_f83b_5ac8b4da6150;
    private final ModelPart p_47837697_36a4_9f05_07e8_38dbc12f0631;
    private final ModelPart a7123190_ff3c_37b3_3716_60342d20bb7e;
    private final ModelPart p_7682accd_4b25_8cf7_c426_dca0ca6fc323;
    private final ModelPart cd5274bd_90fd_f51d_dfc6_3baeabe15416;
    private final ModelPart p_17b60945_aafa_2051_7481_87338f458565;
    private final ModelPart p_5a68c52d_c005_e87f_ddea_2bd866151e0c;
    private final ModelPart p_01684ba1_bba8_45f9_6109_657a96e147d1;
    private final ModelPart p_4944c56a_157d_c61a_1034_7c86dc669720;
    private final ModelPart left_leg;
    private final ModelPart left_knee;
    private final ModelPart left_foot;
    private final ModelPart p_55bd2d48_d3ae_1e1f_9562_d020724ce561;
    private final ModelPart ac1a6714_9407_5393_c544_dc6137b57543;
    private final ModelPart c5024e01_c402_f177_fd80_3c49f8dc1046;
    private final ModelPart ab049004_395a_b7e7_3396_58db6c3e6cb8;
    private final ModelPart a1c8d766_a5ef_8924_a2e5_e0dbf9fd0d3d;
    private final ModelPart ab2c2934_04a2_186b_2508_3efc16748daa;
    private final ModelPart p_74613ac6_2eed_5594_8cb5_008682551ef9;
    private final ModelPart p_9674e3ff_8017_384f_967c_e367c33567b0;
    private final ModelPart right_leg;
    private final ModelPart right_knee;
    private final ModelPart right_foot;
    private final ModelPart p_80ee72a7_1720_7cfd_e7cf_ef5736a2d406;
    private final ModelPart c295dcfa_8e14_c865_893e_76cefdf63acc;
    private final ModelPart p_36722947_ad3a_e764_ca7f_96566ebb4d4d;
    private final ModelPart p_5301d843_2256_a798_7f8d_276248a36886;
    private final ModelPart e9b2c144_93e3_c82d_9a1c_1f55b42c8855;
    private final ModelPart p_0e7c951d_307d_96c0_8bdb_0662ab8be9b7;
    private final ModelPart p_0a0b8bce_09f6_6341_2b25_26ad73ea9da2;
    private final ModelPart a21df43f_b60c_a0ec_0c71_76277f91058d;
    private static final float RUN_LENGTH = 0.8F;
    private static final float[] RUN_HIP_TRANSLATION_TIMES = new float[]{0.0F, 0.2F, 0.4F, 0.6F, 0.8F};
    private static final float[] RUN_HIP_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] RUN_HIP_ROTATION_TIMES = new float[]{0.0F, 0.4F, 0.8F};
    private static final float[] RUN_HIP_ROTATION_VALUES = new float[]{-0.0873F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F};
    private static final float[] RUN_LEFT_LEG_ROTATION_TIMES = new float[]{0.0F, 0.15F, 0.4F, 0.6F, 0.65F, 0.7F, 0.8F};
    private static final float[] RUN_LEFT_LEG_ROTATION_VALUES = new float[]{0.3927F, 0.0F, 0.0F, 0.6055F, 0.0F, 0.0F, -1.1345F, 0.0F, 0.0F, -0.5018F, 0.0F, 0.0F, -0.54F, 0.0F, 0.0F, -0.4037F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F};
    private static final float[] RUN_LEFT_KNEE_ROTATION_TIMES = new float[]{0.0F, 0.1F, 0.4F, 0.6F, 0.65F, 0.7F, 0.8F};
    private static final float[] RUN_LEFT_KNEE_ROTATION_VALUES = new float[]{0.0873F, 0.0F, 0.0F, 0.7418F, 0.0F, 0.0F, 1.6581F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F, 0.4145F, 0.0F, 0.0F, 0.6981F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F};
    private static final float[] RUN_LEFT_FOOT_ROTATION_TIMES = new float[]{0.0F, 0.1F, 0.3F, 0.4F, 0.6F, 0.65F, 0.7F, 0.75F, 0.8F};
    private static final float[] RUN_LEFT_FOOT_ROTATION_VALUES = new float[]{-0.1309F, 0.0F, 0.0F, 0.5018F, 0.0F, 0.0F, 0.6655F, 0.0F, 0.0F, -0.6545F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F, 0.1963F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F, -0.3054F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.0F};
    private static final float[] RUN_LEFT_ANTENNA_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] RUN_LEFT_ANTENNA_ROTATION_VALUES = new float[]{0.0F, -0.2618F, -0.2182F};
    private static final float[] RUN_RIGHT_LEG_ROTATION_TIMES = new float[]{0.0F, 0.2F, 0.25F, 0.3F, 0.4F, 0.55F, 0.8F};
    private static final float[] RUN_RIGHT_LEG_ROTATION_VALUES = new float[]{-1.1345F, 0.0F, 0.0F, -0.5018F, 0.0F, 0.0F, -0.54F, 0.0F, 0.0F, -0.4037F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F, 0.6055F, 0.0F, 0.0F, -1.1345F, 0.0F, 0.0F};
    private static final float[] RUN_RIGHT_KNEE_ROTATION_TIMES = new float[]{0.0F, 0.2F, 0.25F, 0.3F, 0.4F, 0.5F, 0.8F};
    private static final float[] RUN_RIGHT_KNEE_ROTATION_VALUES = new float[]{1.6581F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F, 0.4145F, 0.0F, 0.0F, 0.6981F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F, 0.7418F, 0.0F, 0.0F, 1.6581F, 0.0F, 0.0F};
    private static final float[] RUN_RIGHT_FOOT_ROTATION_TIMES = new float[]{0.0F, 0.2F, 0.25F, 0.3F, 0.35F, 0.4F, 0.5F, 0.7F, 0.8F};
    private static final float[] RUN_RIGHT_FOOT_ROTATION_VALUES = new float[]{-0.6545F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F, 0.1963F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F, -0.3054F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.0F, 0.5018F, 0.0F, 0.0F, 0.6655F, 0.0F, 0.0F, -0.6545F, 0.0F, 0.0F};
    private static final float[] RUN_SCREEN_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.4F, 0.65F, 0.8F};
    private static final float[] RUN_SCREEN_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, -0.0436F, 0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0436F, 0.0F, 0.0F, 0.0F};
    private static final float[] RUN_RIGHT_ANTENNA_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] RUN_RIGHT_ANTENNA_ROTATION_VALUES = new float[]{0.0F, 0.6109F, 0.3927F};
    private static final float WALK_LENGTH = 1.0F;
    private static final float[] WALK_HIP_TRANSLATION_TIMES = new float[]{0.0F, 0.25F, 0.5F, 0.75F, 1.0F};
    private static final float[] WALK_HIP_TRANSLATION_VALUES = new float[]{0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F};
    private static final float[] WALK_LEFT_LEG_ROTATION_TIMES = new float[]{0.0F, 0.15F, 0.35F, 0.5F, 0.55F, 0.6F, 0.65F, 0.7F, 0.75F, 1.0F};
    private static final float[] WALK_LEFT_LEG_ROTATION_VALUES = new float[]{0.4363F, 0.0F, 0.0F, 0.2007F, 0.0F, 0.0F, -1.2479F, 0.0F, 0.0F, -0.3491F, 0.0F, 0.0F, -0.4451F, 0.0F, 0.0F, -0.3908F, 0.0F, 0.0F, -0.3311F, 0.0F, 0.0F, -0.2033F, 0.0F, 0.0F, -0.0059F, 0.0F, 0.0F, 0.4363F, 0.0F, 0.0F};
    private static final float[] WALK_LEFT_KNEE_ROTATION_TIMES = new float[]{0.0F, 0.1F, 0.25F, 0.35F, 0.5F, 0.55F, 0.6F, 0.65F, 0.7F, 0.75F, 0.85F, 1.0F};
    private static final float[] WALK_LEFT_KNEE_ROTATION_VALUES = new float[]{0.1745F, 0.0F, 0.0F, 0.6632F, 0.0F, 0.0F, 1.2435F, 0.0F, 0.0F, 1.4879F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F, 0.2957F, 0.0F, 0.0F, 0.3241F, 0.0F, 0.0F, 0.2192F, 0.0F, 0.0F, 0.0445F, 0.0F, 0.0F, 0.1401F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F};
    private static final float[] WALK_LEFT_FOOT_ROTATION_TIMES = new float[]{0.0F, 0.1F, 0.3F, 0.45F, 0.5F, 0.55F, 0.6F, 0.65F, 0.75F, 0.85F, 1.0F};
    private static final float[] WALK_LEFT_FOOT_ROTATION_VALUES = new float[]{-0.3054F, 0.0F, 0.0F, 0.6283F, 0.0F, 0.0F, -0.6346F, 0.0F, 0.0F, -0.4363F, 0.0F, 0.0F, -0.4363F, 0.0F, 0.0F, 0.192F, 0.0F, 0.0F, 0.1004F, 0.0F, 0.0F, 0.0087F, 0.0F, 0.0F, -0.0312F, 0.0F, 0.0F, -0.2717F, 0.0F, 0.0F, -0.3054F, 0.0F, 0.0F};
    private static final float[] WALK_LEFT_ANTENNA_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] WALK_LEFT_ANTENNA_ROTATION_VALUES = new float[]{-0.0436F, -0.4363F, -0.1745F};
    private static final float[] WALK_RIGHT_LEG_ROTATION_TIMES = new float[]{0.0F, 0.05F, 0.1F, 0.15F, 0.2F, 0.25F, 0.5F, 0.65F, 0.85F, 1.0F};
    private static final float[] WALK_RIGHT_LEG_ROTATION_VALUES = new float[]{-0.3491F, 0.0F, 0.0F, -0.4451F, 0.0F, 0.0F, -0.3908F, 0.0F, 0.0F, -0.3311F, 0.0F, 0.0F, -0.2033F, 0.0F, 0.0F, -0.0059F, 0.0F, 0.0F, 0.4363F, 0.0F, 0.0F, 0.2007F, 0.0F, 0.0F, -1.2479F, 0.0F, 0.0F, -0.3491F, 0.0F, 0.0F};
    private static final float[] WALK_RIGHT_KNEE_ROTATION_TIMES = new float[]{0.0F, 0.05F, 0.1F, 0.15F, 0.2F, 0.25F, 0.35F, 0.5F, 0.6F, 0.75F, 0.85F, 1.0F};
    private static final float[] WALK_RIGHT_KNEE_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F, 0.2957F, 0.0F, 0.0F, 0.3241F, 0.0F, 0.0F, 0.2192F, 0.0F, 0.0F, 0.0445F, 0.0F, 0.0F, 0.1401F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F, 0.6632F, 0.0F, 0.0F, 1.2435F, 0.0F, 0.0F, 1.4879F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_RIGHT_FOOT_ROTATION_TIMES = new float[]{0.0F, 0.05F, 0.1F, 0.15F, 0.25F, 0.35F, 0.5F, 0.65F, 0.85F, 1.0F};
    private static final float[] WALK_RIGHT_FOOT_ROTATION_VALUES = new float[]{-0.4363F, 0.0F, 0.0F, 0.192F, 0.0F, 0.0F, 0.1004F, 0.0F, 0.0F, 0.0087F, 0.0F, 0.0F, -0.0312F, 0.0F, 0.0F, -0.2717F, 0.0F, 0.0F, -0.3054F, 0.0F, 0.0F, 0.6283F, 0.0F, 0.0F, -0.6346F, 0.0F, 0.0F, -0.4363F, 0.0F, 0.0F};
    private static final float[] WALK_SCREEN_ROTATION_TIMES = new float[]{0.0F, 0.1F, 0.25F, 0.5F, 0.6F, 0.75F, 1.0F};
    private static final float[] WALK_SCREEN_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, -0.0175F, 0.0F, 0.0F, -0.0436F, 0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0175F, 0.0F, 0.0F, 0.0436F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_RIGHT_ANTENNA_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] WALK_RIGHT_ANTENNA_ROTATION_VALUES = new float[]{0.2618F, 0.7854F, 0.5672F};

    public RetroTvRobotModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.hip = this.root.getChild("hip");
        this.screen = this.hip.getChild("screen");
        this.left_antenna = this.screen.getChild("left_antenna");
        this.p_1c3be9a4_1f34_8bfd_676b_67585bd4d830 = this.left_antenna.getChild("1c3be9a4-1f34-8bfd-676b-67585bd4d830");
        this.right_antenna = this.screen.getChild("right_antenna");
        this.p_667e4006_6f1d_7b88_9a1b_975af1440d36 = this.right_antenna.getChild("667e4006-6f1d-7b88-9a1b-975af1440d36");
        this.p_65d07b6b_9a99_1b55_d133_4f5ae859eb65 = this.screen.getChild("65d07b6b-9a99-1b55-d133-4f5ae859eb65");
        this.add334ef_9e96_cd58_f57a_b1ebf1f89836 = this.screen.getChild("add334ef-9e96-cd58-f57a-b1ebf1f89836");
        this.p_95de2277_d52e_9f17_f901_c241b8ce887b = this.screen.getChild("95de2277-d52e-9f17-f901-c241b8ce887b");
        this.ee74975d_5206_6d30_f83b_5ac8b4da6150 = this.screen.getChild("ee74975d-5206-6d30-f83b-5ac8b4da6150");
        this.p_47837697_36a4_9f05_07e8_38dbc12f0631 = this.screen.getChild("47837697-36a4-9f05-07e8-38dbc12f0631");
        this.a7123190_ff3c_37b3_3716_60342d20bb7e = this.screen.getChild("a7123190-ff3c-37b3-3716-60342d20bb7e");
        this.p_7682accd_4b25_8cf7_c426_dca0ca6fc323 = this.screen.getChild("7682accd-4b25-8cf7-c426-dca0ca6fc323");
        this.cd5274bd_90fd_f51d_dfc6_3baeabe15416 = this.screen.getChild("cd5274bd-90fd-f51d-dfc6-3baeabe15416");
        this.p_17b60945_aafa_2051_7481_87338f458565 = this.screen.getChild("17b60945-aafa-2051-7481-87338f458565");
        this.p_5a68c52d_c005_e87f_ddea_2bd866151e0c = this.screen.getChild("5a68c52d-c005-e87f-ddea-2bd866151e0c");
        this.p_01684ba1_bba8_45f9_6109_657a96e147d1 = this.screen.getChild("01684ba1-bba8-45f9-6109-657a96e147d1");
        this.p_4944c56a_157d_c61a_1034_7c86dc669720 = this.screen.getChild("4944c56a-157d-c61a-1034-7c86dc669720");
        this.left_leg = this.hip.getChild("left_leg");
        this.left_knee = this.left_leg.getChild("left_knee");
        this.left_foot = this.left_knee.getChild("left_foot");
        this.p_55bd2d48_d3ae_1e1f_9562_d020724ce561 = this.left_foot.getChild("55bd2d48-d3ae-1e1f-9562-d020724ce561");
        this.ac1a6714_9407_5393_c544_dc6137b57543 = this.left_foot.getChild("ac1a6714-9407-5393-c544-dc6137b57543");
        this.c5024e01_c402_f177_fd80_3c49f8dc1046 = this.left_foot.getChild("c5024e01-c402-f177-fd80-3c49f8dc1046");
        this.ab049004_395a_b7e7_3396_58db6c3e6cb8 = this.left_foot.getChild("ab049004-395a-b7e7-3396-58db6c3e6cb8");
        this.a1c8d766_a5ef_8924_a2e5_e0dbf9fd0d3d = this.left_knee.getChild("a1c8d766-a5ef-8924-a2e5-e0dbf9fd0d3d");
        this.ab2c2934_04a2_186b_2508_3efc16748daa = this.left_knee.getChild("ab2c2934-04a2-186b-2508-3efc16748daa");
        this.p_74613ac6_2eed_5594_8cb5_008682551ef9 = this.left_leg.getChild("74613ac6-2eed-5594-8cb5-008682551ef9");
        this.p_9674e3ff_8017_384f_967c_e367c33567b0 = this.left_leg.getChild("9674e3ff-8017-384f-967c-e367c33567b0");
        this.right_leg = this.hip.getChild("right_leg");
        this.right_knee = this.right_leg.getChild("right_knee");
        this.right_foot = this.right_knee.getChild("right_foot");
        this.p_80ee72a7_1720_7cfd_e7cf_ef5736a2d406 = this.right_foot.getChild("80ee72a7-1720-7cfd-e7cf-ef5736a2d406");
        this.c295dcfa_8e14_c865_893e_76cefdf63acc = this.right_foot.getChild("c295dcfa-8e14-c865-893e-76cefdf63acc");
        this.p_36722947_ad3a_e764_ca7f_96566ebb4d4d = this.right_foot.getChild("36722947-ad3a-e764-ca7f-96566ebb4d4d");
        this.p_5301d843_2256_a798_7f8d_276248a36886 = this.right_foot.getChild("5301d843-2256-a798-7f8d-276248a36886");
        this.e9b2c144_93e3_c82d_9a1c_1f55b42c8855 = this.right_knee.getChild("e9b2c144-93e3-c82d-9a1c-1f55b42c8855");
        this.p_0e7c951d_307d_96c0_8bdb_0662ab8be9b7 = this.right_knee.getChild("0e7c951d-307d-96c0-8bdb-0662ab8be9b7");
        this.p_0a0b8bce_09f6_6341_2b25_26ad73ea9da2 = this.right_leg.getChild("0a0b8bce-09f6-6341-2b25-26ad73ea9da2");
        this.a21df43f_b60c_a0ec_0c71_76277f91058d = this.right_leg.getChild("a21df43f-b60c-a0ec-0c71-76277f91058d");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition hip = root.addOrReplaceChild("hip", CubeListBuilder.create(), PartPose.offset(0.0F, -19.5F, 0.0F));
        PartDefinition screen = hip.addOrReplaceChild("screen", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_antenna = screen.addOrReplaceChild("left_antenna", CubeListBuilder.create(), PartPose.offset(0.0F, -10.5F, 2.5F));
        PartDefinition p_1c3be9a4_1f34_8bfd_676b_67585bd4d830 = left_antenna.addOrReplaceChild("1c3be9a4-1f34-8bfd-676b-67585bd4d830", CubeListBuilder.create().texOffs(26, 27).addBox(-2.0F, -10.0F, 0.0F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_antenna = screen.addOrReplaceChild("right_antenna", CubeListBuilder.create(), PartPose.offset(0.0F, -10.5F, 2.5F));
        PartDefinition p_667e4006_6f1d_7b88_9a1b_975af1440d36 = right_antenna.addOrReplaceChild("667e4006-6f1d-7b88-9a1b-975af1440d36", CubeListBuilder.create().texOffs(26, 27).addBox(0.0F, -10.0F, 0.0F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition p_65d07b6b_9a99_1b55_d133_4f5ae859eb65 = screen.addOrReplaceChild("65d07b6b-9a99-1b55-d133-4f5ae859eb65", CubeListBuilder.create().texOffs(0, 17).addBox(-5.0F, -7.0F, 1.0F, 10.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 3.0F));
        PartDefinition add334ef_9e96_cd58_f57a_b1ebf1f89836 = screen.addOrReplaceChild("add334ef-9e96-cd58-f57a-b1ebf1f89836", CubeListBuilder.create().texOffs(20, 27).addBox(-4.0F, -9.0F, -4.0F, 2.0F, 11.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, -1.0F, 0.0F));
        PartDefinition p_95de2277_d52e_9f17_f901_c241b8ce887b = screen.addOrReplaceChild("95de2277-d52e-9f17-f901-c241b8ce887b", CubeListBuilder.create().texOffs(24, 17).addBox(-4.0F, -1.0F, -3.0F, 9.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 0.0F, -1.0F));
        PartDefinition ee74975d_5206_6d30_f83b_5ac8b4da6150 = screen.addOrReplaceChild("ee74975d-5206-6d30-f83b-5ac8b4da6150", CubeListBuilder.create().texOffs(14, 25).addBox(-4.0F, -9.0F, -4.0F, 2.0F, 11.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.5F, -1.0F, 0.0F));
        PartDefinition p_47837697_36a4_9f05_07e8_38dbc12f0631 = screen.addOrReplaceChild("47837697-36a4-9f05-07e8-38dbc12f0631", CubeListBuilder.create().texOffs(23, 24).addBox(-4.0F, -1.0F, -3.0F, 9.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -9.0F, -1.0F));
        PartDefinition a7123190_ff3c_37b3_3716_60342d20bb7e = screen.addOrReplaceChild("a7123190-ff3c-37b3-3716-60342d20bb7e", CubeListBuilder.create().texOffs(34, 36).addBox(-1.0F, -1.0F, 2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.5F, -0.5F));
        PartDefinition p_7682accd_4b25_8cf7_c426_dca0ca6fc323 = screen.addOrReplaceChild("7682accd-4b25-8cf7-c426-dca0ca6fc323", CubeListBuilder.create().texOffs(0, 25).addBox(-3.0F, -8.0F, -3.0F, 6.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -0.5F));
        PartDefinition cd5274bd_90fd_f51d_dfc6_3baeabe15416 = screen.addOrReplaceChild("cd5274bd-90fd-f51d-dfc6-3baeabe15416", CubeListBuilder.create().texOffs(0, 33).addBox(-2.0F, -6.0F, 0.0F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -2.0F, -3.5F, 0.0F, 0.3054F, 0.0F));
        PartDefinition p_17b60945_aafa_2051_7481_87338f458565 = screen.addOrReplaceChild("17b60945-aafa-2051-7481-87338f458565", CubeListBuilder.create().texOffs(30, 30).addBox(0.0F, -6.0F, 0.0F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -2.0F, -3.5F, 0.0F, -0.3054F, 0.0F));
        PartDefinition p_5a68c52d_c005_e87f_ddea_2bd866151e0c = screen.addOrReplaceChild("5a68c52d-c005-e87f-ddea-2bd866151e0c", CubeListBuilder.create().texOffs(33, 20).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 1.5F, 1.0F));
        PartDefinition p_01684ba1_bba8_45f9_6109_657a96e147d1 = screen.addOrReplaceChild("01684ba1-bba8-45f9-6109-657a96e147d1", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -10.0F, -3.0F, 12.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 0.0F));
        PartDefinition p_4944c56a_157d_c61a_1034_7c86dc669720 = screen.addOrReplaceChild("4944c56a-157d-c61a-1034-7c86dc669720", CubeListBuilder.create().texOffs(33, 20).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 1.5F, 1.0F));
        PartDefinition left_leg = hip.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(-3.0F, 1.5F, 1.0F));
        PartDefinition left_knee = left_leg.addOrReplaceChild("left_knee", CubeListBuilder.create(), PartPose.offset(0.0F, 7.0F, 0.0F));
        PartDefinition left_foot = left_knee.addOrReplaceChild("left_foot", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));
        PartDefinition p_55bd2d48_d3ae_1e1f_9562_d020724ce561 = left_foot.addOrReplaceChild("55bd2d48-d3ae-1e1f-9562-d020724ce561", CubeListBuilder.create().texOffs(30, 27).addBox(0.0F, -1.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 1.0F, -0.5F));
        PartDefinition ac1a6714_9407_5393_c544_dc6137b57543 = left_foot.addOrReplaceChild("ac1a6714-9407-5393-c544-dc6137b57543", CubeListBuilder.create().texOffs(31, 4).addBox(0.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 2.0F, 0.0F));
        PartDefinition c5024e01_c402_f177_fd80_3c49f8dc1046 = left_foot.addOrReplaceChild("c5024e01-c402-f177-fd80-3c49f8dc1046", CubeListBuilder.create().texOffs(24, 20).addBox(-1.0F, 0.0F, -2.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 2.0F, -2.0F));
        PartDefinition ab049004_395a_b7e7_3396_58db6c3e6cb8 = left_foot.addOrReplaceChild("ab049004-395a-b7e7-3396-58db6c3e6cb8", CubeListBuilder.create().texOffs(10, 33).addBox(1.0F, 0.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 2.0F, -3.0F));
        PartDefinition a1c8d766_a5ef_8924_a2e5_e0dbf9fd0d3d = left_knee.addOrReplaceChild("a1c8d766-a5ef-8924-a2e5-e0dbf9fd0d3d", CubeListBuilder.create().texOffs(6, 33).addBox(0.0F, -1.0F, 0.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 1.0F, -0.5F));
        PartDefinition ab2c2934_04a2_186b_2508_3efc16748daa = left_knee.addOrReplaceChild("ab2c2934-04a2-186b-2508-3efc16748daa", CubeListBuilder.create().texOffs(31, 0).addBox(-1.0F, 5.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 0.0F));
        PartDefinition p_74613ac6_2eed_5594_8cb5_008682551ef9 = left_leg.addOrReplaceChild("74613ac6-2eed-5594-8cb5-008682551ef9", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -1.0F, 0.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 1.0F, -0.5F));
        PartDefinition p_9674e3ff_8017_384f_967c_e367c33567b0 = left_leg.addOrReplaceChild("9674e3ff-8017-384f-967c-e367c33567b0", CubeListBuilder.create().texOffs(35, 27).addBox(-1.0F, 5.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition right_leg = hip.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(3.0F, 1.5F, 1.0F));
        PartDefinition right_knee = right_leg.addOrReplaceChild("right_knee", CubeListBuilder.create(), PartPose.offset(0.0F, 7.0F, 0.0F));
        PartDefinition right_foot = right_knee.addOrReplaceChild("right_foot", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));
        PartDefinition p_80ee72a7_1720_7cfd_e7cf_ef5736a2d406 = right_foot.addOrReplaceChild("80ee72a7-1720-7cfd-e7cf-ef5736a2d406", CubeListBuilder.create().texOffs(30, 27).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 1.0F, -0.5F));
        PartDefinition c295dcfa_8e14_c865_893e_76cefdf63acc = right_foot.addOrReplaceChild("c295dcfa-8e14-c865-893e-76cefdf63acc", CubeListBuilder.create().texOffs(24, 20).addBox(-2.0F, 0.0F, -2.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 2.0F, -2.0F));
        PartDefinition p_36722947_ad3a_e764_ca7f_96566ebb4d4d = right_foot.addOrReplaceChild("36722947-ad3a-e764-ca7f-96566ebb4d4d", CubeListBuilder.create().texOffs(10, 33).addBox(-2.0F, 0.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 2.0F, -3.0F));
        PartDefinition p_5301d843_2256_a798_7f8d_276248a36886 = right_foot.addOrReplaceChild("5301d843-2256-a798-7f8d-276248a36886", CubeListBuilder.create().texOffs(31, 4).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 2.0F, 0.0F));
        PartDefinition e9b2c144_93e3_c82d_9a1c_1f55b42c8855 = right_knee.addOrReplaceChild("e9b2c144-93e3-c82d-9a1c-1f55b42c8855", CubeListBuilder.create().texOffs(6, 33).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 1.0F, -0.5F));
        PartDefinition p_0e7c951d_307d_96c0_8bdb_0662ab8be9b7 = right_knee.addOrReplaceChild("0e7c951d-307d-96c0-8bdb-0662ab8be9b7", CubeListBuilder.create().texOffs(31, 0).addBox(-1.0F, 5.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 0.0F));
        PartDefinition p_0a0b8bce_09f6_6341_2b25_26ad73ea9da2 = right_leg.addOrReplaceChild("0a0b8bce-09f6-6341-2b25-26ad73ea9da2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 1.0F, -0.5F));
        PartDefinition a21df43f_b60c_a0ec_0c71_76277f91058d = right_leg.addOrReplaceChild("a21df43f-b60c-a0ec-0c71-76277f91058d", CubeListBuilder.create().texOffs(35, 27).addBox(-1.0F, 5.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    private static float wrapAnimationTime(float time, float length) {
        if (length <= 0.0F) {
            return 0.0F;
        }
        float wrapped = time % length;
        return wrapped < 0.0F ? wrapped + length : wrapped;
    }

    private static float sampleChannel(float[] times, float[] values, int stride, int component, float time) {
        if (times.length == 0) {
            return 0.0F;
        }
        if (times.length == 1 || time <= times[0]) {
            return values[component];
        }
        for (int i = 0; i < times.length - 1; i++) {
            float start = times[i];
            float end = times[i + 1];
            if (time <= end) {
                int base = (i * stride) + component;
                if (end <= start + 1.0E-6F) {
                    return values[base + stride];
                }
                float delta = (time - start) / (end - start);
                return values[base] + ((values[base + stride] - values[base]) * delta);
            }
        }
        return values[((times.length - 1) * stride) + component];
    }

    private static void applyTranslationTrack(ModelPart part, float[] times, float[] values, float time) {
        if (times.length == 0) {
            return;
        }
        part.x += sampleChannel(times, values, 3, 0, time);
        part.y += sampleChannel(times, values, 3, 1, time);
        part.z += sampleChannel(times, values, 3, 2, time);
    }

    private static void applyRotationTrack(ModelPart part, float[] times, float[] values, float time) {
        if (times.length == 0) {
            return;
        }
        part.xRot += sampleChannel(times, values, 3, 0, time);
        part.yRot += sampleChannel(times, values, 3, 1, time);
        part.zRot += sampleChannel(times, values, 3, 2, time);
    }

    private static void applyScaleTrack(ModelPart part, float[] times, float[] values, float time) {
        if (times.length == 0) {
            return;
        }
        part.xScale *= sampleChannel(times, values, 3, 0, time);
        part.yScale *= sampleChannel(times, values, 3, 1, time);
        part.zScale *= sampleChannel(times, values, 3, 2, time);
    }

    private void applyGeneratedAnimation(LivingEntityRenderState state) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        // Blockbench animation sidecar merged from model-anim.java.
        float idleTimeSeconds = state.ageInTicks / 20.0F;
        float walkTimeSeconds = state.walkAnimationPos / 20.0F;
        applyClipRUN(idleTimeSeconds);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    public void setAngles(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    private void applyClipRUN(float time) {
        float wrappedTime = wrapAnimationTime(time, RUN_LENGTH);
        applyTranslationTrack(this.hip, RUN_HIP_TRANSLATION_TIMES, RUN_HIP_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.hip, RUN_HIP_ROTATION_TIMES, RUN_HIP_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_leg, RUN_LEFT_LEG_ROTATION_TIMES, RUN_LEFT_LEG_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_knee, RUN_LEFT_KNEE_ROTATION_TIMES, RUN_LEFT_KNEE_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_foot, RUN_LEFT_FOOT_ROTATION_TIMES, RUN_LEFT_FOOT_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_antenna, RUN_LEFT_ANTENNA_ROTATION_TIMES, RUN_LEFT_ANTENNA_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_leg, RUN_RIGHT_LEG_ROTATION_TIMES, RUN_RIGHT_LEG_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_knee, RUN_RIGHT_KNEE_ROTATION_TIMES, RUN_RIGHT_KNEE_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_foot, RUN_RIGHT_FOOT_ROTATION_TIMES, RUN_RIGHT_FOOT_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.screen, RUN_SCREEN_ROTATION_TIMES, RUN_SCREEN_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_antenna, RUN_RIGHT_ANTENNA_ROTATION_TIMES, RUN_RIGHT_ANTENNA_ROTATION_VALUES, wrappedTime);
    }

    private void applyClipWALK(float time) {
        float wrappedTime = wrapAnimationTime(time, WALK_LENGTH);
        applyTranslationTrack(this.hip, WALK_HIP_TRANSLATION_TIMES, WALK_HIP_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_leg, WALK_LEFT_LEG_ROTATION_TIMES, WALK_LEFT_LEG_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_knee, WALK_LEFT_KNEE_ROTATION_TIMES, WALK_LEFT_KNEE_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_foot, WALK_LEFT_FOOT_ROTATION_TIMES, WALK_LEFT_FOOT_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_antenna, WALK_LEFT_ANTENNA_ROTATION_TIMES, WALK_LEFT_ANTENNA_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_leg, WALK_RIGHT_LEG_ROTATION_TIMES, WALK_RIGHT_LEG_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_knee, WALK_RIGHT_KNEE_ROTATION_TIMES, WALK_RIGHT_KNEE_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_foot, WALK_RIGHT_FOOT_ROTATION_TIMES, WALK_RIGHT_FOOT_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.screen, WALK_SCREEN_ROTATION_TIMES, WALK_SCREEN_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_antenna, WALK_RIGHT_ANTENNA_ROTATION_TIMES, WALK_RIGHT_ANTENNA_ROTATION_VALUES, wrappedTime);
    }
}

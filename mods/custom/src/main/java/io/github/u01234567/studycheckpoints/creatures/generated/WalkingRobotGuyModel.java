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
 * Creature id: walking_robot_guy
 */
public class WalkingRobotGuyModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart hip;
    private final ModelPart hip_cube_1;
    private final ModelPart hip_cube_2;
    private final ModelPart spine;
    private final ModelPart spine_cube_1;
    private final ModelPart chest;
    private final ModelPart chest_cube_1;
    private final ModelPart chest_cube_2;
    private final ModelPart head;
    private final ModelPart head_cube_1;
    private final ModelPart head_cube_2;
    private final ModelPart head_cube_3;
    private final ModelPart left_arm;
    private final ModelPart left_arm_cube_1;
    private final ModelPart left_arm_cube_2;
    private final ModelPart left_arm_cube_3;
    private final ModelPart left_elbow;
    private final ModelPart left_elbow_cube_1;
    private final ModelPart left_elbow_cube_2;
    private final ModelPart left_elbow_cube_3;
    private final ModelPart left_hand;
    private final ModelPart left_hand_cube_1;
    private final ModelPart right_arm;
    private final ModelPart right_arm_cube_1;
    private final ModelPart right_arm_cube_2;
    private final ModelPart right_arm_cube_3;
    private final ModelPart right_elbow;
    private final ModelPart right_elbow_cube_1;
    private final ModelPart right_elbow_cube_2;
    private final ModelPart right_elbow_cube_3;
    private final ModelPart right_hand;
    private final ModelPart right_hand_cube_1;
    private final ModelPart left_leg;
    private final ModelPart left_leg_cube_1;
    private final ModelPart left_leg_cube_2;
    private final ModelPart left_leg_cube_3;
    private final ModelPart left_knee;
    private final ModelPart left_knee_cube_1;
    private final ModelPart left_knee_cube_2;
    private final ModelPart left_knee_cube_3;
    private final ModelPart left_knee_cube_4;
    private final ModelPart right_leg;
    private final ModelPart right_leg_cube_1;
    private final ModelPart right_leg_cube_2;
    private final ModelPart right_leg_cube_3;
    private final ModelPart right_knee;
    private final ModelPart right_knee_cube_1;
    private final ModelPart right_knee_cube_2;
    private final ModelPart right_knee_cube_3;
    private final ModelPart right_knee_cube_4;
    private static final float WALK_ANIMATION_LENGTH = 1.0F;
    private static final float[] WALK_ANIMATION_HIP_TRANSLATION_TIMES = new float[]{0.0F, 0.05F, 0.1F, 0.2F, 0.35F, 0.45F, 0.5F, 0.6F, 0.7F, 0.75F, 0.85F, 0.9F, 1.0F};
    private static final float[] WALK_ANIMATION_HIP_TRANSLATION_VALUES = new float[]{0.0F, 1.0F, 0.0F, 0.0F, 1.1209F, 0.0F, 0.0F, 1.3044F, 0.0F, 0.0F, 1.43F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6189F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.3669F, 0.0F, 0.0F, 1.43F, 0.0F, 0.0F, 1.0011F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1904F, 0.0F, 0.0F, 1.0F, 0.0F};
    private static final float[] WALK_ANIMATION_HIP_ROTATION_TIMES = new float[]{0.0F, 0.05F, 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.75F, 0.8F, 0.85F, 0.9F, 1.0F};
    private static final float[] WALK_ANIMATION_HIP_ROTATION_VALUES = new float[]{0.0F, -0.0873F, 0.0F, 0.0198F, -0.0755F, 0.004F, 0.0491F, -0.0589F, 0.0098F, 0.0873F, -0.0175F, 0.0175F, 0.0646F, 0.0213F, 0.0142F, 0.0226F, 0.064F, 0.0071F, 0.0F, 0.0873F, 0.0F, 0.0436F, 0.0654F, -0.0109F, 0.0873F, 0.0175F, -0.0175F, 0.0818F, 0.0008F, -0.0164F, 0.0679F, -0.0187F, -0.0136F, 0.0491F, -0.0393F, -0.0098F, 0.0291F, -0.0588F, -0.0058F, 0.0F, -0.0873F, 0.0F};
    private static final float[] WALK_ANIMATION_SPINE_ROTATION_TIMES = new float[]{0.0F, 0.05F, 0.1F, 0.2F, 0.35F, 0.45F, 0.5F, 0.6F, 0.7F, 0.75F, 0.85F, 0.9F, 1.0F};
    private static final float[] WALK_ANIMATION_SPINE_ROTATION_VALUES = new float[]{0.1309F, 0.0F, 0.0F, 0.1225F, 0.0F, -0.0094F, 0.1104F, 0.0F, -0.0232F, 0.0873F, 0.0F, -0.0436F, 0.0654F, 0.0F, -0.0218F, 0.1139F, 0.0F, -0.0089F, 0.1309F, 0.0F, 0.0F, 0.1145F, 0.0F, 0.0245F, 0.0873F, 0.0F, 0.0436F, 0.0751F, 0.0F, 0.0412F, 0.0654F, 0.0F, 0.0218F, 0.0832F, 0.0F, 0.0137F, 0.1309F, 0.0F, 0.0F};
    private static final float[] WALK_ANIMATION_CHEST_ROTATION_TIMES = new float[]{0.0F, 0.05F, 0.1F, 0.2F, 0.35F, 0.45F, 0.5F, 0.6F, 0.7F, 0.75F, 0.85F, 0.9F, 1.0F};
    private static final float[] WALK_ANIMATION_CHEST_ROTATION_VALUES = new float[]{0.1309F, 0.0F, 0.0F, 0.1132F, 0.0F, -0.0094F, 0.0873F, 0.0F, -0.0232F, 0.0436F, 0.0F, -0.0436F, 0.0436F, 0.0F, -0.0218F, 0.1115F, 0.0F, -0.0089F, 0.1309F, 0.0F, 0.0F, 0.0927F, 0.0F, 0.0245F, 0.0436F, 0.0F, 0.0436F, 0.0339F, 0.0F, 0.0412F, 0.0436F, 0.0F, 0.0218F, 0.0695F, 0.0F, 0.0137F, 0.1309F, 0.0F, 0.0F};
    private static final float[] WALK_ANIMATION_HEAD_ROTATION_TIMES = new float[]{0.0F, 0.05F, 0.1F, 0.15F, 0.25F, 0.35F, 0.45F, 0.5F, 0.6F, 0.65F, 0.7F, 0.75F, 0.85F, 0.9F, 1.0F};
    private static final float[] WALK_ANIMATION_HEAD_ROTATION_VALUES = new float[]{-0.2182F, 0.0873F, 0.0F, -0.1993F, 0.0794F, 0.0047F, -0.1708F, 0.0687F, 0.0118F, -0.1373F, 0.056F, 0.0202F, -0.0731F, 0.0275F, 0.0363F, -0.0436F, 0.0F, 0.0436F, -0.1729F, -0.0711F, 0.0178F, -0.2182F, -0.0873F, 0.0F, -0.1836F, -0.0751F, -0.015F, -0.1495F, -0.0621F, -0.0233F, -0.1123F, -0.0466F, -0.031F, -0.0782F, -0.03F, -0.0375F, -0.0436F, 0.0F, -0.0436F, -0.0824F, 0.0323F, -0.0339F, -0.2182F, 0.0873F, 0.0F};
    private static final float[] WALK_ANIMATION_LEFT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.05F, 0.1F, 0.15F, 0.25F, 0.35F, 0.45F, 0.5F, 0.6F, 0.65F, 0.7F, 0.75F, 0.8F, 0.85F, 0.9F, 1.0F};
    private static final float[] WALK_ANIMATION_LEFT_ARM_ROTATION_VALUES = new float[]{0.6109F, 0.2182F, 0.1745F, 0.5302F, 0.1763F, 0.1835F, 0.4129F, 0.1155F, 0.1965F, 0.2698F, 0.0413F, 0.2124F, -0.0518F, -0.1254F, 0.2482F, -0.3499F, -0.28F, 0.2813F, -0.5395F, -0.3783F, 0.3024F, -0.5672F, -0.3927F, 0.3054F, -0.4636F, -0.3389F, 0.2939F, -0.3499F, -0.28F, 0.2813F, -0.2091F, -0.207F, 0.2656F, -0.0518F, -0.1254F, 0.2482F, 0.1114F, -0.0408F, 0.23F, 0.2698F, 0.0413F, 0.2124F, 0.4129F, 0.1155F, 0.1965F, 0.6109F, 0.2182F, 0.1745F};
    private static final float[] WALK_ANIMATION_LEFT_ELBOW_ROTATION_TIMES = new float[]{0.0F, 0.05F, 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.75F, 0.8F, 0.85F, 0.9F, 1.0F};
    private static final float[] WALK_ANIMATION_LEFT_ELBOW_ROTATION_VALUES = new float[]{-0.5672F, 0.0F, 0.0F, -0.5036F, 0.0F, 0.0F, -0.4069F, 0.0F, 0.0F, -0.3403F, 0.0F, 0.0F, -0.5423F, 0.0F, 0.0F, -0.8529F, 0.0F, 0.0F, -1.0908F, 0.0F, 0.0F, -1.2245F, 0.0F, 0.0F, -1.1868F, 0.0F, 0.0F, -1.1178F, 0.0F, 0.0F, -1.0104F, 0.0F, 0.0F, -0.883F, 0.0F, 0.0F, -0.7544F, 0.0F, 0.0F, -0.5672F, 0.0F, 0.0F};
    private static final float[] WALK_ANIMATION_RIGHT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.05F, 0.1F, 0.15F, 0.25F, 0.35F, 0.45F, 0.5F, 0.6F, 0.65F, 0.7F, 0.75F, 0.8F, 0.85F, 0.9F, 1.0F};
    private static final float[] WALK_ANIMATION_RIGHT_ARM_ROTATION_VALUES = new float[]{-0.5672F, 0.3927F, -0.3054F, -0.4865F, 0.3509F, -0.2965F, -0.3693F, 0.2901F, -0.2834F, -0.2262F, 0.2159F, -0.2675F, 0.0954F, 0.0491F, -0.2318F, 0.3935F, -0.1055F, -0.1987F, 0.5832F, -0.2038F, -0.1776F, 0.6109F, -0.2182F, -0.1745F, 0.5072F, -0.1644F, -0.1861F, 0.3935F, -0.1055F, -0.1987F, 0.2527F, -0.0325F, -0.2143F, 0.0954F, 0.0491F, -0.2318F, -0.0677F, 0.1337F, -0.2499F, -0.2262F, 0.2159F, -0.2675F, -0.3693F, 0.2901F, -0.2834F, -0.5672F, 0.3927F, -0.3054F};
    private static final float[] WALK_ANIMATION_RIGHT_ELBOW_ROTATION_TIMES = new float[]{0.0F, 0.05F, 0.1F, 0.2F, 0.25F, 0.35F, 0.45F, 0.5F, 0.6F, 0.7F, 0.75F, 0.8F, 0.85F, 0.9F, 1.0F};
    private static final float[] WALK_ANIMATION_RIGHT_ELBOW_ROTATION_VALUES = new float[]{-1.0908F, 0.0F, 0.0F, -1.1249F, 0.0F, 0.0F, -1.1776F, 0.0F, 0.0F, -1.1868F, 0.0F, 0.0F, -1.1204F, 0.0F, 0.0F, -0.8972F, 0.0F, 0.0F, -0.6561F, 0.0F, 0.0F, -0.5672F, 0.0F, 0.0F, -0.3682F, 0.0F, 0.0F, -0.3403F, 0.0F, 0.0F, -0.4175F, 0.0F, 0.0F, -0.5459F, 0.0F, 0.0F, -0.7014F, 0.0F, 0.0F, -0.8601F, 0.0F, 0.0F, -1.0908F, 0.0F, 0.0F};
    private static final float[] WALK_ANIMATION_LEFT_LEG_ROTATION_TIMES = new float[]{0.0F, 0.05F, 0.15F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.65F, 0.7F, 0.75F, 0.8F, 0.85F, 0.9F, 1.0F};
    private static final float[] WALK_ANIMATION_LEFT_LEG_ROTATION_VALUES = new float[]{-1.0908F, 0.0F, 0.0F, -0.9683F, 0.0F, 0.0F, -0.5585F, 0.0F, 0.0F, -0.4233F, 0.0F, 0.0F, -0.0524F, 0.0F, 0.0F, 0.2862F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F, 0.2281F, 0.0F, 0.0F, 0.0799F, 0.0F, 0.0F, -0.0966F, 0.0F, 0.0F, -0.2896F, 0.0F, 0.0F, -0.4874F, 0.0F, 0.0F, -0.6781F, 0.0F, 0.0F, -0.8501F, 0.0F, 0.0F, -1.0908F, 0.0F, 0.0F};
    private static final float[] WALK_ANIMATION_LEFT_KNEE_ROTATION_TIMES = new float[]{0.0F, 0.05F, 0.15F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.65F, 0.7F, 0.75F, 0.8F, 0.85F, 0.9F, 1.0F};
    private static final float[] WALK_ANIMATION_LEFT_KNEE_ROTATION_VALUES = new float[]{0.9599F, 0.0F, 0.0F, 0.7187F, 0.0F, 0.0F, 0.144F, 0.0F, 0.0F, 0.092F, 0.0F, 0.0F, 0.0341F, 0.0F, 0.0F, 0.0444F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F, 0.7715F, 0.0F, 0.0F, 1.0341F, 0.0F, 0.0F, 1.0742F, 0.0F, 0.0F, 1.082F, 0.0F, 0.0F, 1.0668F, 0.0F, 0.0F, 1.0378F, 0.0F, 0.0F, 1.0042F, 0.0F, 0.0F, 0.9599F, 0.0F, 0.0F};
    private static final float[] WALK_ANIMATION_RIGHT_LEG_ROTATION_TIMES = new float[]{0.0F, 0.05F, 0.1F, 0.15F, 0.25F, 0.35F, 0.45F, 0.5F, 0.6F, 0.65F, 0.7F, 0.75F, 0.8F, 0.85F, 0.9F, 1.0F};
    private static final float[] WALK_ANIMATION_RIGHT_LEG_ROTATION_VALUES = new float[]{0.3927F, 0.0F, 0.0F, 0.2954F, 0.0F, 0.0F, 0.1587F, 0.0F, 0.0F, -0.0068F, 0.0F, 0.0F, -0.3823F, 0.0F, 0.0F, -0.7472F, 0.0F, 0.0F, -1.0174F, 0.0F, 0.0F, -1.0908F, 0.0F, 0.0F, -0.8416F, 0.0F, 0.0F, -0.5585F, 0.0F, 0.0F, -0.4363F, 0.0F, 0.0F, -0.2895F, 0.0F, 0.0F, -0.1302F, 0.0F, 0.0F, 0.0296F, 0.0F, 0.0F, 0.178F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F};
    private static final float[] WALK_ANIMATION_RIGHT_KNEE_ROTATION_TIMES = new float[]{0.0F, 0.05F, 0.15F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.65F, 0.7F, 0.75F, 0.8F, 0.85F, 0.9F, 1.0F};
    private static final float[] WALK_ANIMATION_RIGHT_KNEE_ROTATION_VALUES = new float[]{0.1309F, 0.0F, 0.0F, 0.4013F, 0.0F, 0.0F, 1.0341F, 0.0F, 0.0F, 1.0813F, 0.0F, 0.0F, 1.1096F, 0.0F, 0.0F, 1.0637F, 0.0F, 0.0F, 0.9599F, 0.0F, 0.0F, 0.384F, 0.0F, 0.0F, 0.144F, 0.0F, 0.0F, 0.0999F, 0.0F, 0.0F, 0.0814F, 0.0F, 0.0F, 0.0815F, 0.0F, 0.0F, 0.0935F, 0.0F, 0.0F, 0.1103F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F};

    public WalkingRobotGuyModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.hip = this.root.getChild("hip");
        this.hip_cube_1 = this.hip.getChild("hip_cube_1");
        this.hip_cube_2 = this.hip.getChild("hip_cube_2");
        this.spine = this.hip.getChild("spine");
        this.spine_cube_1 = this.spine.getChild("spine_cube_1");
        this.chest = this.spine.getChild("chest");
        this.chest_cube_1 = this.chest.getChild("chest_cube_1");
        this.chest_cube_2 = this.chest.getChild("chest_cube_2");
        this.head = this.chest.getChild("head");
        this.head_cube_1 = this.head.getChild("head_cube_1");
        this.head_cube_2 = this.head.getChild("head_cube_2");
        this.head_cube_3 = this.head.getChild("head_cube_3");
        this.left_arm = this.chest.getChild("left_arm");
        this.left_arm_cube_1 = this.left_arm.getChild("left_arm_cube_1");
        this.left_arm_cube_2 = this.left_arm.getChild("left_arm_cube_2");
        this.left_arm_cube_3 = this.left_arm.getChild("left_arm_cube_3");
        this.left_elbow = this.left_arm.getChild("left_elbow");
        this.left_elbow_cube_1 = this.left_elbow.getChild("left_elbow_cube_1");
        this.left_elbow_cube_2 = this.left_elbow.getChild("left_elbow_cube_2");
        this.left_elbow_cube_3 = this.left_elbow.getChild("left_elbow_cube_3");
        this.left_hand = this.left_elbow.getChild("left_hand");
        this.left_hand_cube_1 = this.left_hand.getChild("left_hand_cube_1");
        this.right_arm = this.chest.getChild("right_arm");
        this.right_arm_cube_1 = this.right_arm.getChild("right_arm_cube_1");
        this.right_arm_cube_2 = this.right_arm.getChild("right_arm_cube_2");
        this.right_arm_cube_3 = this.right_arm.getChild("right_arm_cube_3");
        this.right_elbow = this.right_arm.getChild("right_elbow");
        this.right_elbow_cube_1 = this.right_elbow.getChild("right_elbow_cube_1");
        this.right_elbow_cube_2 = this.right_elbow.getChild("right_elbow_cube_2");
        this.right_elbow_cube_3 = this.right_elbow.getChild("right_elbow_cube_3");
        this.right_hand = this.right_elbow.getChild("right_hand");
        this.right_hand_cube_1 = this.right_hand.getChild("right_hand_cube_1");
        this.left_leg = this.hip.getChild("left_leg");
        this.left_leg_cube_1 = this.left_leg.getChild("left_leg_cube_1");
        this.left_leg_cube_2 = this.left_leg.getChild("left_leg_cube_2");
        this.left_leg_cube_3 = this.left_leg.getChild("left_leg_cube_3");
        this.left_knee = this.left_leg.getChild("left_knee");
        this.left_knee_cube_1 = this.left_knee.getChild("left_knee_cube_1");
        this.left_knee_cube_2 = this.left_knee.getChild("left_knee_cube_2");
        this.left_knee_cube_3 = this.left_knee.getChild("left_knee_cube_3");
        this.left_knee_cube_4 = this.left_knee.getChild("left_knee_cube_4");
        this.right_leg = this.hip.getChild("right_leg");
        this.right_leg_cube_1 = this.right_leg.getChild("right_leg_cube_1");
        this.right_leg_cube_2 = this.right_leg.getChild("right_leg_cube_2");
        this.right_leg_cube_3 = this.right_leg.getChild("right_leg_cube_3");
        this.right_knee = this.right_leg.getChild("right_knee");
        this.right_knee_cube_1 = this.right_knee.getChild("right_knee_cube_1");
        this.right_knee_cube_2 = this.right_knee.getChild("right_knee_cube_2");
        this.right_knee_cube_3 = this.right_knee.getChild("right_knee_cube_3");
        this.right_knee_cube_4 = this.right_knee.getChild("right_knee_cube_4");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition hip = root.addOrReplaceChild("hip", CubeListBuilder.create(), PartPose.offset(0.0F, -22.0F, 0.0F));
        PartDefinition hip_cube_1 = hip.addOrReplaceChild("hip_cube_1", CubeListBuilder.create().texOffs(16, 38).addBox(-1.0F, -0.5F, -2.5F, 2.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 0.5F));
        PartDefinition hip_cube_2 = hip.addOrReplaceChild("hip_cube_2", CubeListBuilder.create().texOffs(19, 23).addBox(-3.0F, -1.0F, -2.5F, 6.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.5F));
        PartDefinition spine = hip.addOrReplaceChild("spine", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 1.5F));
        PartDefinition spine_cube_1 = spine.addOrReplaceChild("spine_cube_1", CubeListBuilder.create().texOffs(27, 44).addBox(-1.5F, -5.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition chest = spine.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, 0.0F));
        PartDefinition chest_cube_1 = chest.addOrReplaceChild("chest_cube_1", CubeListBuilder.create().texOffs(0, 16).addBox(-3.5F, -7.0F, -3.5F, 7.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition chest_cube_2 = chest.addOrReplaceChild("chest_cube_2", CubeListBuilder.create().texOffs(36, 0).addBox(-1.5F, -7.0F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.0F));
        PartDefinition head = chest.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));
        PartDefinition head_cube_1 = head.addOrReplaceChild("head_cube_1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.5F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition head_cube_2 = head.addOrReplaceChild("head_cube_2", CubeListBuilder.create().texOffs(27, 52).addBox(-4.0F, -8.0F, -4.5F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.0F));
        PartDefinition head_cube_3 = head.addOrReplaceChild("head_cube_3", CubeListBuilder.create().texOffs(3, 59).addBox(-4.0F, -5.0F, -4.5F, 8.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.9F, 8.0F));
        PartDefinition left_arm = chest.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(-3.5F, -4.5F, 0.0F));
        PartDefinition left_arm_cube_1 = left_arm.addOrReplaceChild("left_arm_cube_1", CubeListBuilder.create().texOffs(0, 38).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        PartDefinition left_arm_cube_2 = left_arm.addOrReplaceChild("left_arm_cube_2", CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, 1.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -1.0F, 0.0F));
        PartDefinition left_arm_cube_3 = left_arm.addOrReplaceChild("left_arm_cube_3", CubeListBuilder.create().texOffs(44, 27).addBox(-2.0F, 2.0F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 4.0F, -0.5F));
        PartDefinition left_elbow = left_arm.addOrReplaceChild("left_elbow", CubeListBuilder.create(), PartPose.offset(-2.0F, 10.0F, 0.0F));
        PartDefinition left_elbow_cube_1 = left_elbow.addOrReplaceChild("left_elbow_cube_1", CubeListBuilder.create().texOffs(0, 38).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        PartDefinition left_elbow_cube_2 = left_elbow.addOrReplaceChild("left_elbow_cube_2", CubeListBuilder.create().texOffs(30, 38).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition left_elbow_cube_3 = left_elbow.addOrReplaceChild("left_elbow_cube_3", CubeListBuilder.create().texOffs(0, 46).addBox(-2.0F, -3.0F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 6.0F, -0.5F));
        PartDefinition left_hand = left_elbow.addOrReplaceChild("left_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, 0.0F));
        PartDefinition left_hand_cube_1 = left_hand.addOrReplaceChild("left_hand_cube_1", CubeListBuilder.create().texOffs(16, 30).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_arm = chest.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(3.5F, -4.5F, 0.0F));
        PartDefinition right_arm_cube_1 = right_arm.addOrReplaceChild("right_arm_cube_1", CubeListBuilder.create().texOffs(0, 38).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        PartDefinition right_arm_cube_2 = right_arm.addOrReplaceChild("right_arm_cube_2", CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, 1.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -1.0F, 0.0F));
        PartDefinition right_arm_cube_3 = right_arm.addOrReplaceChild("right_arm_cube_3", CubeListBuilder.create().texOffs(44, 27).addBox(-1.0F, 2.0F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 4.0F, -0.5F));
        PartDefinition right_elbow = right_arm.addOrReplaceChild("right_elbow", CubeListBuilder.create(), PartPose.offset(2.0F, 10.0F, 0.0F));
        PartDefinition right_elbow_cube_1 = right_elbow.addOrReplaceChild("right_elbow_cube_1", CubeListBuilder.create().texOffs(0, 38).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        PartDefinition right_elbow_cube_2 = right_elbow.addOrReplaceChild("right_elbow_cube_2", CubeListBuilder.create().texOffs(30, 38).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition right_elbow_cube_3 = right_elbow.addOrReplaceChild("right_elbow_cube_3", CubeListBuilder.create().texOffs(0, 46).addBox(-1.0F, -3.0F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 6.0F, -0.5F));
        PartDefinition right_hand = right_elbow.addOrReplaceChild("right_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, 0.0F));
        PartDefinition right_hand_cube_1 = right_hand.addOrReplaceChild("right_hand_cube_1", CubeListBuilder.create().texOffs(16, 30).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_leg = hip.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(-2.0F, 2.0F, 0.0F));
        PartDefinition left_leg_cube_1 = left_leg.addOrReplaceChild("left_leg_cube_1", CubeListBuilder.create().texOffs(39, 46).addBox(-2.0F, 2.0F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 3.0F, -0.5F));
        PartDefinition left_leg_cube_2 = left_leg.addOrReplaceChild("left_leg_cube_2", CubeListBuilder.create().texOffs(0, 29).addBox(-2.0F, 1.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -1.0F, 0.0F));
        PartDefinition left_leg_cube_3 = left_leg.addOrReplaceChild("left_leg_cube_3", CubeListBuilder.create().texOffs(0, 38).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        PartDefinition left_knee = left_leg.addOrReplaceChild("left_knee", CubeListBuilder.create(), PartPose.offset(-1.0F, 9.0F, 0.0F));
        PartDefinition left_knee_cube_1 = left_knee.addOrReplaceChild("left_knee_cube_1", CubeListBuilder.create().texOffs(16, 30).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));
        PartDefinition left_knee_cube_2 = left_knee.addOrReplaceChild("left_knee_cube_2", CubeListBuilder.create().texOffs(44, 12).addBox(-2.0F, -3.0F, -1.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 6.0F, -0.5F));
        PartDefinition left_knee_cube_3 = left_knee.addOrReplaceChild("left_knee_cube_3", CubeListBuilder.create().texOffs(0, 38).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        PartDefinition left_knee_cube_4 = left_knee.addOrReplaceChild("left_knee_cube_4", CubeListBuilder.create().texOffs(30, 38).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition right_leg = hip.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(2.0F, 2.0F, 0.0F));
        PartDefinition right_leg_cube_1 = right_leg.addOrReplaceChild("right_leg_cube_1", CubeListBuilder.create().texOffs(0, 38).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        PartDefinition right_leg_cube_2 = right_leg.addOrReplaceChild("right_leg_cube_2", CubeListBuilder.create().texOffs(0, 29).addBox(-2.0F, 1.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -1.0F, 0.0F));
        PartDefinition right_leg_cube_3 = right_leg.addOrReplaceChild("right_leg_cube_3", CubeListBuilder.create().texOffs(39, 46).addBox(-1.0F, 2.0F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 3.0F, -0.5F));
        PartDefinition right_knee = right_leg.addOrReplaceChild("right_knee", CubeListBuilder.create(), PartPose.offset(1.0F, 9.0F, 0.0F));
        PartDefinition right_knee_cube_1 = right_knee.addOrReplaceChild("right_knee_cube_1", CubeListBuilder.create().texOffs(0, 38).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        PartDefinition right_knee_cube_2 = right_knee.addOrReplaceChild("right_knee_cube_2", CubeListBuilder.create().texOffs(16, 30).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));
        PartDefinition right_knee_cube_3 = right_knee.addOrReplaceChild("right_knee_cube_3", CubeListBuilder.create().texOffs(30, 38).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition right_knee_cube_4 = right_knee.addOrReplaceChild("right_knee_cube_4", CubeListBuilder.create().texOffs(44, 12).addBox(-1.0F, -3.0F, -1.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 6.0F, -0.5F));

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
        applyClipWALK_ANIMATION(idleTimeSeconds);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    public void setAngles(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    private void applyClipWALK_ANIMATION(float time) {
        float wrappedTime = wrapAnimationTime(time, WALK_ANIMATION_LENGTH);
        applyTranslationTrack(this.hip, WALK_ANIMATION_HIP_TRANSLATION_TIMES, WALK_ANIMATION_HIP_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.hip, WALK_ANIMATION_HIP_ROTATION_TIMES, WALK_ANIMATION_HIP_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.spine, WALK_ANIMATION_SPINE_ROTATION_TIMES, WALK_ANIMATION_SPINE_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.chest, WALK_ANIMATION_CHEST_ROTATION_TIMES, WALK_ANIMATION_CHEST_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.head, WALK_ANIMATION_HEAD_ROTATION_TIMES, WALK_ANIMATION_HEAD_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_arm, WALK_ANIMATION_LEFT_ARM_ROTATION_TIMES, WALK_ANIMATION_LEFT_ARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_elbow, WALK_ANIMATION_LEFT_ELBOW_ROTATION_TIMES, WALK_ANIMATION_LEFT_ELBOW_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_arm, WALK_ANIMATION_RIGHT_ARM_ROTATION_TIMES, WALK_ANIMATION_RIGHT_ARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_elbow, WALK_ANIMATION_RIGHT_ELBOW_ROTATION_TIMES, WALK_ANIMATION_RIGHT_ELBOW_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_leg, WALK_ANIMATION_LEFT_LEG_ROTATION_TIMES, WALK_ANIMATION_LEFT_LEG_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_knee, WALK_ANIMATION_LEFT_KNEE_ROTATION_TIMES, WALK_ANIMATION_LEFT_KNEE_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_leg, WALK_ANIMATION_RIGHT_LEG_ROTATION_TIMES, WALK_ANIMATION_RIGHT_LEG_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_knee, WALK_ANIMATION_RIGHT_KNEE_ROTATION_TIMES, WALK_ANIMATION_RIGHT_KNEE_ROTATION_VALUES, wrappedTime);
    }
}

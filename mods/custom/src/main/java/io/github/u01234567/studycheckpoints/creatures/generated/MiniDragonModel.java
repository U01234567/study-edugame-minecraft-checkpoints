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
 * Creature id: mini_dragon
 */
public class MiniDragonModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart node_91;
    private final ModelPart DRAGON;
    private final ModelPart torso;
    private final ModelPart cube;
    private final ModelPart cube_2;
    private final ModelPart waist;
    private final ModelPart cube_3;
    private final ModelPart cube_4;
    private final ModelPart tail;
    private final ModelPart segment_1;
    private final ModelPart cube_5;
    private final ModelPart cube_6;
    private final ModelPart segment_2;
    private final ModelPart cube_7;
    private final ModelPart cube_8;
    private final ModelPart segment_3;
    private final ModelPart cube_9;
    private final ModelPart cube_10;
    private final ModelPart segment_4;
    private final ModelPart cube_11;
    private final ModelPart left_rearleg;
    private final ModelPart cube_12;
    private final ModelPart rearleg2;
    private final ModelPart cube_13;
    private final ModelPart left_claws2;
    private final ModelPart index2;
    private final ModelPart cube_14;
    private final ModelPart middle2;
    private final ModelPart cube_15;
    private final ModelPart pinky2;
    private final ModelPart cube_16;
    private final ModelPart right_rearleg;
    private final ModelPart cube_17;
    private final ModelPart rearleg3;
    private final ModelPart cube_18;
    private final ModelPart right_claws2;
    private final ModelPart index4;
    private final ModelPart cube_19;
    private final ModelPart middle4;
    private final ModelPart cube_20;
    private final ModelPart pinky4;
    private final ModelPart cube_21;
    private final ModelPart neck;
    private final ModelPart neck_2;
    private final ModelPart head;
    private final ModelPart left_ear;
    private final ModelPart right_ear;
    private final ModelPart top;
    private final ModelPart right_side;
    private final ModelPart left_side;
    private final ModelPart down;
    private final ModelPart front;
    private final ModelPart back;
    private final ModelPart down_2;
    private final ModelPart jaw;
    private final ModelPart back_2;
    private final ModelPart front_2;
    private final ModelPart left_side_2;
    private final ModelPart right_side_2;
    private final ModelPart bottom;
    private final ModelPart right_forearm;
    private final ModelPart cube_22;
    private final ModelPart forearm3;
    private final ModelPart cube_23;
    private final ModelPart right_claws;
    private final ModelPart index3;
    private final ModelPart cube_24;
    private final ModelPart middle3;
    private final ModelPart cube_25;
    private final ModelPart pinky3;
    private final ModelPart cube_26;
    private final ModelPart left_forearm;
    private final ModelPart cube_27;
    private final ModelPart forearm2;
    private final ModelPart cube_28;
    private final ModelPart left_claws;
    private final ModelPart index;
    private final ModelPart cube_29;
    private final ModelPart middle;
    private final ModelPart cube_30;
    private final ModelPart pinky;
    private final ModelPart cube_31;
    private final ModelPart left_wing;
    private final ModelPart cube_32;
    private final ModelPart cube_33;
    private final ModelPart cube_34;
    private final ModelPart cube_35;
    private final ModelPart right_wing;
    private final ModelPart cube_36;
    private final ModelPart cube_37;
    private final ModelPart cube_38;
    private final ModelPart cube_39;
    private static final float POSE_LENGTH = 2.1667F;
    private static final float[] POSE_DRAGON_TRANSLATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_DRAGON_TRANSLATION_VALUES = new float[]{0.0F, -0.0F, 0.0F};
    private static final float[] POSE_NECK_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_NECK_ROTATION_VALUES = new float[]{0.2182F, 0.0F, 0.0F};
    private static final float[] POSE_HEAD_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_HEAD_ROTATION_VALUES = new float[]{0.48F, 0.0F, 0.0F};
    private static final float[] POSE_JAW_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_JAW_ROTATION_VALUES = new float[]{0.0436F, 0.0F, 0.0F};
    private static final float[] POSE_TORSO_TRANSLATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_TORSO_TRANSLATION_VALUES = new float[]{0.0F, -14.0F, 0.0F};
    private static final float[] POSE_TORSO_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_TORSO_ROTATION_VALUES = new float[]{-0.5236F, 0.0F, 0.0F};
    private static final float[] POSE_LEFT_FOREARM_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_LEFT_FOREARM_ROTATION_VALUES = new float[]{0.5236F, 0.0F, 0.0F};
    private static final float[] POSE_FOREARM2_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_FOREARM2_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] POSE_INDEX_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_INDEX_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] POSE_MIDDLE_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_MIDDLE_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] POSE_RIGHT_FOREARM_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_RIGHT_FOREARM_ROTATION_VALUES = new float[]{0.5236F, 0.0F, 0.0F};
    private static final float[] POSE_LEFT_WING_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_LEFT_WING_ROTATION_VALUES = new float[]{0.0F, 0.0F, -0.7854F};
    private static final float[] POSE_RIGHT_WING_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_RIGHT_WING_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.7854F};
    private static final float[] POSE_LEFT_REARLEG_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_LEFT_REARLEG_ROTATION_VALUES = new float[]{0.6545F, 0.0F, 0.0F};
    private static final float[] POSE_REARLEG2_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_REARLEG2_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] POSE_INDEX2_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_INDEX2_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] POSE_MIDDLE2_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_MIDDLE2_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] POSE_REARLEG3_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_REARLEG3_ROTATION_VALUES = new float[]{0.6545F, 0.0F, 0.0F};
    private static final float[] POSE_WAIST_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_WAIST_ROTATION_VALUES = new float[]{-0.3491F, 0.0F, 0.0F};
    private static final float[] POSE_SEGMENT_4_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_SEGMENT_4_ROTATION_VALUES = new float[]{-0.0873F, 0.0F, 0.0F};
    private static final float[] POSE_SEGMENT_1_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_SEGMENT_1_ROTATION_VALUES = new float[]{-0.2618F, 0.0F, 0.0F};
    private static final float[] POSE_SEGMENT_2_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_SEGMENT_2_ROTATION_VALUES = new float[]{-0.1745F, 0.0F, 0.0F};
    private static final float[] POSE_SEGMENT_3_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] POSE_SEGMENT_3_ROTATION_VALUES = new float[]{-0.1309F, 0.0F, 0.0F};

    public MiniDragonModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.node_91 = this.root.getChild("node_91");
        this.DRAGON = this.node_91.getChild("DRAGON");
        this.torso = this.DRAGON.getChild("torso");
        this.cube = this.torso.getChild("cube");
        this.cube_2 = this.torso.getChild("cube_2");
        this.waist = this.torso.getChild("waist");
        this.cube_3 = this.waist.getChild("cube_3");
        this.cube_4 = this.waist.getChild("cube_4");
        this.tail = this.waist.getChild("tail");
        this.segment_1 = this.tail.getChild("segment_1");
        this.cube_5 = this.segment_1.getChild("cube_5");
        this.cube_6 = this.segment_1.getChild("cube_6");
        this.segment_2 = this.segment_1.getChild("segment_2");
        this.cube_7 = this.segment_2.getChild("cube_7");
        this.cube_8 = this.segment_2.getChild("cube_8");
        this.segment_3 = this.segment_2.getChild("segment_3");
        this.cube_9 = this.segment_3.getChild("cube_9");
        this.cube_10 = this.segment_3.getChild("cube_10");
        this.segment_4 = this.segment_3.getChild("segment_4");
        this.cube_11 = this.segment_4.getChild("cube_11");
        this.left_rearleg = this.waist.getChild("left_rearleg");
        this.cube_12 = this.left_rearleg.getChild("cube_12");
        this.rearleg2 = this.left_rearleg.getChild("rearleg2");
        this.cube_13 = this.rearleg2.getChild("cube_13");
        this.left_claws2 = this.rearleg2.getChild("left_claws2");
        this.index2 = this.left_claws2.getChild("index2");
        this.cube_14 = this.index2.getChild("cube_14");
        this.middle2 = this.left_claws2.getChild("middle2");
        this.cube_15 = this.middle2.getChild("cube_15");
        this.pinky2 = this.left_claws2.getChild("pinky2");
        this.cube_16 = this.pinky2.getChild("cube_16");
        this.right_rearleg = this.waist.getChild("right_rearleg");
        this.cube_17 = this.right_rearleg.getChild("cube_17");
        this.rearleg3 = this.right_rearleg.getChild("rearleg3");
        this.cube_18 = this.rearleg3.getChild("cube_18");
        this.right_claws2 = this.rearleg3.getChild("right_claws2");
        this.index4 = this.right_claws2.getChild("index4");
        this.cube_19 = this.index4.getChild("cube_19");
        this.middle4 = this.right_claws2.getChild("middle4");
        this.cube_20 = this.middle4.getChild("cube_20");
        this.pinky4 = this.right_claws2.getChild("pinky4");
        this.cube_21 = this.pinky4.getChild("cube_21");
        this.neck = this.torso.getChild("neck");
        this.neck_2 = this.neck.getChild("neck_2");
        this.head = this.neck.getChild("head");
        this.left_ear = this.head.getChild("left_ear");
        this.right_ear = this.head.getChild("right_ear");
        this.top = this.head.getChild("top");
        this.right_side = this.head.getChild("right_side");
        this.left_side = this.head.getChild("left_side");
        this.down = this.head.getChild("down");
        this.front = this.head.getChild("front");
        this.back = this.head.getChild("back");
        this.down_2 = this.head.getChild("down_2");
        this.jaw = this.head.getChild("jaw");
        this.back_2 = this.jaw.getChild("back_2");
        this.front_2 = this.jaw.getChild("front_2");
        this.left_side_2 = this.jaw.getChild("left_side_2");
        this.right_side_2 = this.jaw.getChild("right_side_2");
        this.bottom = this.jaw.getChild("bottom");
        this.right_forearm = this.torso.getChild("right_forearm");
        this.cube_22 = this.right_forearm.getChild("cube_22");
        this.forearm3 = this.right_forearm.getChild("forearm3");
        this.cube_23 = this.forearm3.getChild("cube_23");
        this.right_claws = this.forearm3.getChild("right_claws");
        this.index3 = this.right_claws.getChild("index3");
        this.cube_24 = this.index3.getChild("cube_24");
        this.middle3 = this.right_claws.getChild("middle3");
        this.cube_25 = this.middle3.getChild("cube_25");
        this.pinky3 = this.right_claws.getChild("pinky3");
        this.cube_26 = this.pinky3.getChild("cube_26");
        this.left_forearm = this.torso.getChild("left_forearm");
        this.cube_27 = this.left_forearm.getChild("cube_27");
        this.forearm2 = this.left_forearm.getChild("forearm2");
        this.cube_28 = this.forearm2.getChild("cube_28");
        this.left_claws = this.forearm2.getChild("left_claws");
        this.index = this.left_claws.getChild("index");
        this.cube_29 = this.index.getChild("cube_29");
        this.middle = this.left_claws.getChild("middle");
        this.cube_30 = this.middle.getChild("cube_30");
        this.pinky = this.left_claws.getChild("pinky");
        this.cube_31 = this.pinky.getChild("cube_31");
        this.left_wing = this.torso.getChild("left_wing");
        this.cube_32 = this.left_wing.getChild("cube_32");
        this.cube_33 = this.left_wing.getChild("cube_33");
        this.cube_34 = this.left_wing.getChild("cube_34");
        this.cube_35 = this.left_wing.getChild("cube_35");
        this.right_wing = this.torso.getChild("right_wing");
        this.cube_36 = this.right_wing.getChild("cube_36");
        this.cube_37 = this.right_wing.getChild("cube_37");
        this.cube_38 = this.right_wing.getChild("cube_38");
        this.cube_39 = this.right_wing.getChild("cube_39");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition node_91 = root.addOrReplaceChild("node_91", CubeListBuilder.create(), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition DRAGON = node_91.addOrReplaceChild("DRAGON", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0F, 0.0F));
        PartDefinition torso = DRAGON.addOrReplaceChild("torso", CubeListBuilder.create(), PartPose.offset(0.0F, -3.5F, -1.0F));
        PartDefinition cube = torso.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.5F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -0.5F));
        PartDefinition cube_2 = torso.addOrReplaceChild("cube_2", CubeListBuilder.create().texOffs(30, 0).addBox(0.0F, -3.0F, -12.125F, 0.001F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.5F, 9.875F));
        PartDefinition waist = torso.addOrReplaceChild("waist", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 3.0F));
        PartDefinition cube_3 = waist.addOrReplaceChild("cube_3", CubeListBuilder.create().texOffs(42, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 3.0F));
        PartDefinition cube_4 = waist.addOrReplaceChild("cube_4", CubeListBuilder.create().texOffs(68, 0).addBox(0.0F, -1.5F, -6.125F, 0.001F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.5F, 6.875F));
        PartDefinition tail = waist.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 6.025F));
        PartDefinition segment_1 = tail.addOrReplaceChild("segment_1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -0.025F));
        PartDefinition cube_5 = segment_1.addOrReplaceChild("cube_5", CubeListBuilder.create().texOffs(80, 0).addBox(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.5F));
        PartDefinition cube_6 = segment_1.addOrReplaceChild("cube_6", CubeListBuilder.create().texOffs(102, 0).addBox(0.0F, -0.5F, -0.125F, 0.001F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.875F));
        PartDefinition segment_2 = segment_1.addOrReplaceChild("segment_2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 5.0F));
        PartDefinition cube_7 = segment_2.addOrReplaceChild("cube_7", CubeListBuilder.create().texOffs(112, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));
        PartDefinition cube_8 = segment_2.addOrReplaceChild("cube_8", CubeListBuilder.create().texOffs(130, 0).addBox(0.0F, -0.0F, 4.625F, 0.001F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, -4.125F));
        PartDefinition segment_3 = segment_2.addOrReplaceChild("segment_3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 4.0F));
        PartDefinition cube_9 = segment_3.addOrReplaceChild("cube_9", CubeListBuilder.create().texOffs(140, 0).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));
        PartDefinition cube_10 = segment_3.addOrReplaceChild("cube_10", CubeListBuilder.create().texOffs(156, 0).addBox(0.0F, 0.5F, 8.625F, 0.001F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, -8.125F));
        PartDefinition segment_4 = segment_3.addOrReplaceChild("segment_4", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 4.0F));
        PartDefinition cube_11 = segment_4.addOrReplaceChild("cube_11", CubeListBuilder.create().texOffs(166, 0).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));
        PartDefinition left_rearleg = waist.addOrReplaceChild("left_rearleg", CubeListBuilder.create(), PartPose.offset(-3.5F, 1.7071F, 5.75F));
        PartDefinition cube_12 = left_rearleg.addOrReplaceChild("cube_12", CubeListBuilder.create().texOffs(180, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        PartDefinition rearleg2 = left_rearleg.addOrReplaceChild("rearleg2", CubeListBuilder.create(), PartPose.offset(-0.5F, 0.7929F, 0.5F));
        PartDefinition cube_13 = rearleg2.addOrReplaceChild("cube_13", CubeListBuilder.create().texOffs(190, 0).addBox(-1.5F, -2.0F, -1.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition left_claws2 = rearleg2.addOrReplaceChild("left_claws2", CubeListBuilder.create(), PartPose.offsetAndRotation(7.75F, 1.0F, 4.75F, 0.0F, -1.5708F, 0.0F));
        PartDefinition index2 = left_claws2.addOrReplaceChild("index2", CubeListBuilder.create(), PartPose.offset(-5.25F, 1.5F, 6.5F));
        PartDefinition cube_14 = index2.addOrReplaceChild("cube_14", CubeListBuilder.create().texOffs(202, 0).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition middle2 = left_claws2.addOrReplaceChild("middle2", CubeListBuilder.create(), PartPose.offset(-5.25F, 1.5F, 7.75F));
        PartDefinition cube_15 = middle2.addOrReplaceChild("cube_15", CubeListBuilder.create().texOffs(208, 0).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, 0.0F));
        PartDefinition pinky2 = left_claws2.addOrReplaceChild("pinky2", CubeListBuilder.create(), PartPose.offset(-5.25F, 1.5F, 9.0F));
        PartDefinition cube_16 = pinky2.addOrReplaceChild("cube_16", CubeListBuilder.create().texOffs(214, 0).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition right_rearleg = waist.addOrReplaceChild("right_rearleg", CubeListBuilder.create(), PartPose.offset(3.5F, 1.7071F, 5.75F));
        PartDefinition cube_17 = right_rearleg.addOrReplaceChild("cube_17", CubeListBuilder.create().texOffs(220, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        PartDefinition rearleg3 = right_rearleg.addOrReplaceChild("rearleg3", CubeListBuilder.create(), PartPose.offset(0.5F, 1.7929F, 0.5F));
        PartDefinition cube_18 = rearleg3.addOrReplaceChild("cube_18", CubeListBuilder.create().texOffs(230, 0).addBox(-1.5F, -2.0F, -1.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition right_claws2 = rearleg3.addOrReplaceChild("right_claws2", CubeListBuilder.create(), PartPose.offsetAndRotation(-7.75F, 0.0F, 4.75F, -0.0F, 1.5708F, -0.0F));
        PartDefinition index4 = right_claws2.addOrReplaceChild("index4", CubeListBuilder.create(), PartPose.offset(5.25F, 2.5F, 6.5F));
        PartDefinition cube_19 = index4.addOrReplaceChild("cube_19", CubeListBuilder.create().texOffs(242, 0).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition middle4 = right_claws2.addOrReplaceChild("middle4", CubeListBuilder.create(), PartPose.offset(5.25F, 3.0F, 7.75F));
        PartDefinition cube_20 = middle4.addOrReplaceChild("cube_20", CubeListBuilder.create().texOffs(248, 0).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition pinky4 = right_claws2.addOrReplaceChild("pinky4", CubeListBuilder.create(), PartPose.offset(5.25F, 2.5F, 9.0F));
        PartDefinition cube_21 = pinky4.addOrReplaceChild("cube_21", CubeListBuilder.create().texOffs(254, 0).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition neck = torso.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, -4.0F));
        PartDefinition neck_2 = neck.addOrReplaceChild("neck_2", CubeListBuilder.create().texOffs(260, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -2.0F));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -4.0F));
        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(0, 64).addBox(0.0F, -1.5F, -2.0F, 0.001F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -0.5F, 2.0F));
        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(12, 64).addBox(0.0F, -1.5F, -2.0F, 0.001F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -0.5F, 2.0F));
        PartDefinition top = head.addOrReplaceChild("top", CubeListBuilder.create().texOffs(24, 64).addBox(-3.001F, -0.001F, -4.501F, 6.002F, 0.002F, 9.002F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -4.5F));
        PartDefinition right_side = head.addOrReplaceChild("right_side", CubeListBuilder.create().texOffs(56, 64).addBox(-6.0F, -6.0F, -4.0F, 0.001F, 6.0F, 9.0F, new CubeDeformation(0.0F)).texOffs(78, 64).addBox(-6.0F, -6.0F, -4.0F, 0.001F, 6.0F, 9.0F, new CubeDeformation(0.0F)).texOffs(100, 64).addBox(-6.0F, -6.0F, -4.0F, 0.001F, 6.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, 4.0F, -5.0F));
        PartDefinition left_side = head.addOrReplaceChild("left_side", CubeListBuilder.create().texOffs(106, 64).addBox(0.0F, -3.0F, -4.5F, 0.001F, 6.0F, 9.0F, new CubeDeformation(0.0F)).texOffs(128, 64).addBox(0.0F, -3.0F, -4.5F, 0.001F, 6.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 1.0F, -4.5F));
        PartDefinition down = head.addOrReplaceChild("down", CubeListBuilder.create().texOffs(150, 64).addBox(-6.0F, 0.999F, -4.0F, 6.0F, 0.001F, 9.0F, new CubeDeformation(0.0F)).texOffs(182, 64).addBox(-6.0F, 1.0F, -4.0F, 6.0F, 0.001F, 9.0F, new CubeDeformation(0.0F)).texOffs(214, 64).addBox(-6.0F, 0.999F, -4.0F, 6.0F, 0.001F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 3.0F, -5.0F));
        PartDefinition front = head.addOrReplaceChild("front", CubeListBuilder.create().texOffs(246, 64).addBox(-6.0F, -5.0F, -4.0F, 6.0F, 6.0F, 0.001F, new CubeDeformation(0.0F)).texOffs(262, 64).addBox(-6.0F, -5.0F, -4.0F, 6.0F, 6.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 3.0F, -5.0F));
        PartDefinition back = head.addOrReplaceChild("back", CubeListBuilder.create().texOffs(278, 64).addBox(-6.0F, -5.0F, -4.0F, 6.0F, 6.0F, 0.001F, new CubeDeformation(0.0F)).texOffs(294, 64).addBox(-6.0F, -5.0F, -3.999F, 6.0F, 6.0F, 0.001F, new CubeDeformation(0.0F)).texOffs(310, 64).addBox(-6.0F, -5.0F, -4.0F, 6.0F, 6.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 3.0F, 4.0F));
        PartDefinition down_2 = head.addOrReplaceChild("down_2", CubeListBuilder.create().texOffs(326, 64).addBox(-3.0F, -3.001F, -4.5F, 6.0F, 0.001F, 9.0F, new CubeDeformation(0.0F)).texOffs(358, 64).addBox(-3.0F, -3.001F, -4.5F, 6.0F, 0.001F, 9.0F, new CubeDeformation(0.0F)).texOffs(390, 64).addBox(-3.0F, -3.001F, -4.5F, 6.0F, 0.001F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, -4.5F));
        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 3.4F, -0.75F, -0.0873F, 0.0F, -0.0F));
        PartDefinition back_2 = jaw.addOrReplaceChild("back_2", CubeListBuilder.create().texOffs(422, 64).addBox(-2.0F, -3.0F, 4.5F, 5.0F, 4.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 0.9486F, -3.9848F));
        PartDefinition front_2 = jaw.addOrReplaceChild("front_2", CubeListBuilder.create().texOffs(436, 64).addBox(-2.0F, -3.0F, -4.5F, 5.0F, 4.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 0.9486F, -3.9848F));
        PartDefinition left_side_2 = jaw.addOrReplaceChild("left_side_2", CubeListBuilder.create().texOffs(450, 64).addBox(-3.0F, -3.0F, -4.5F, 0.001F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 0.9486F, -3.9848F));
        PartDefinition right_side_2 = jaw.addOrReplaceChild("right_side_2", CubeListBuilder.create().texOffs(472, 64).addBox(3.0F, -3.0F, -4.5F, 0.001F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 0.9486F, -3.9848F));
        PartDefinition bottom = jaw.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 81).addBox(-2.001F, 2.999F, -4.501F, 5.002F, 0.002F, 9.002F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -1.0514F, -3.9848F));
        PartDefinition right_forearm = torso.addOrReplaceChild("right_forearm", CubeListBuilder.create(), PartPose.offset(5.0F, 1.0F, -0.25F));
        PartDefinition cube_22 = right_forearm.addOrReplaceChild("cube_22", CubeListBuilder.create().texOffs(278, 0).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        PartDefinition forearm3 = right_forearm.addOrReplaceChild("forearm3", CubeListBuilder.create(), PartPose.offset(0.25F, 2.5F, 0.0F));
        PartDefinition cube_23 = forearm3.addOrReplaceChild("cube_23", CubeListBuilder.create().texOffs(292, 0).addBox(-1.0F, -2.0F, -1.5F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition right_claws = forearm3.addOrReplaceChild("right_claws", CubeListBuilder.create(), PartPose.offset(-6.75F, 0.0F, 1.25F));
        PartDefinition index3 = right_claws.addOrReplaceChild("index3", CubeListBuilder.create(), PartPose.offset(7.0F, 2.5F, -2.5F));
        PartDefinition cube_24 = index3.addOrReplaceChild("cube_24", CubeListBuilder.create().texOffs(304, 0).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition middle3 = right_claws.addOrReplaceChild("middle3", CubeListBuilder.create(), PartPose.offset(7.0F, 3.0F, -1.25F));
        PartDefinition cube_25 = middle3.addOrReplaceChild("cube_25", CubeListBuilder.create().texOffs(310, 0).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition pinky3 = right_claws.addOrReplaceChild("pinky3", CubeListBuilder.create(), PartPose.offset(7.0F, 2.5F, 0.0F));
        PartDefinition cube_26 = pinky3.addOrReplaceChild("cube_26", CubeListBuilder.create().texOffs(316, 0).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition left_forearm = torso.addOrReplaceChild("left_forearm", CubeListBuilder.create(), PartPose.offset(-5.0F, 1.0F, -0.25F));
        PartDefinition cube_27 = left_forearm.addOrReplaceChild("cube_27", CubeListBuilder.create().texOffs(322, 0).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        PartDefinition forearm2 = left_forearm.addOrReplaceChild("forearm2", CubeListBuilder.create(), PartPose.offset(-0.25F, 0.5F, 0.0F));
        PartDefinition cube_28 = forearm2.addOrReplaceChild("cube_28", CubeListBuilder.create().texOffs(336, 0).addBox(-1.0F, -2.0F, -1.5F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 0.0F));
        PartDefinition left_claws = forearm2.addOrReplaceChild("left_claws", CubeListBuilder.create(), PartPose.offset(6.75F, 2.0F, 1.25F));
        PartDefinition index = left_claws.addOrReplaceChild("index", CubeListBuilder.create(), PartPose.offset(-7.0F, 1.5F, -2.5F));
        PartDefinition cube_29 = index.addOrReplaceChild("cube_29", CubeListBuilder.create().texOffs(348, 0).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition middle = left_claws.addOrReplaceChild("middle", CubeListBuilder.create(), PartPose.offset(-7.0F, 2.0F, -1.25F));
        PartDefinition cube_30 = middle.addOrReplaceChild("cube_30", CubeListBuilder.create().texOffs(354, 0).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition pinky = left_claws.addOrReplaceChild("pinky", CubeListBuilder.create(), PartPose.offset(-7.0F, 1.5F, 0.0F));
        PartDefinition cube_31 = pinky.addOrReplaceChild("cube_31", CubeListBuilder.create().texOffs(360, 0).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition left_wing = torso.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.offset(-3.375F, -3.5F, -2.025F));
        PartDefinition cube_32 = left_wing.addOrReplaceChild("cube_32", CubeListBuilder.create().texOffs(366, 0).addBox(-5.0F, -4.0F, -5.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.875F, 1.0F, 4.025F));
        PartDefinition cube_33 = left_wing.addOrReplaceChild("cube_33", CubeListBuilder.create().texOffs(376, 0).addBox(-4.0F, -10.0F, -5.0F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(3.875F, -3.0F, 4.025F));
        PartDefinition cube_34 = left_wing.addOrReplaceChild("cube_34", CubeListBuilder.create().texOffs(382, 0).addBox(-4.0F, -1.0F, -5.0F, 1.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(3.875F, 0.0F, 6.025F));
        PartDefinition cube_35 = left_wing.addOrReplaceChild("cube_35", CubeListBuilder.create().texOffs(404, 0).addBox(-4.0F, -16.0F, -6.0F, 0.001F, 19.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(4.375F, -3.0F, 5.025F));
        PartDefinition right_wing = torso.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offset(3.125F, -3.5F, -2.025F));
        PartDefinition cube_36 = right_wing.addOrReplaceChild("cube_36", CubeListBuilder.create().texOffs(438, 0).addBox(3.0F, -4.0F, -5.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.625F, 1.0F, 4.025F));
        PartDefinition cube_37 = right_wing.addOrReplaceChild("cube_37", CubeListBuilder.create().texOffs(448, 0).addBox(3.0F, -10.0F, -5.0F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.625F, -3.0F, 4.025F));
        PartDefinition cube_38 = right_wing.addOrReplaceChild("cube_38", CubeListBuilder.create().texOffs(454, 0).addBox(3.0F, -1.0F, -5.0F, 1.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.625F, 0.0F, 6.025F));
        PartDefinition cube_39 = right_wing.addOrReplaceChild("cube_39", CubeListBuilder.create().texOffs(476, 0).addBox(4.0F, -16.0F, -6.0F, 0.001F, 19.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.125F, -3.0F, 5.025F));

        return LayerDefinition.create(meshDefinition, 512, 128);
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
        applyClipPOSE(idleTimeSeconds);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    public void setAngles(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    private void applyClipPOSE(float time) {
        float wrappedTime = wrapAnimationTime(time, POSE_LENGTH);
        applyTranslationTrack(this.DRAGON, POSE_DRAGON_TRANSLATION_TIMES, POSE_DRAGON_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.neck, POSE_NECK_ROTATION_TIMES, POSE_NECK_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.head, POSE_HEAD_ROTATION_TIMES, POSE_HEAD_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.jaw, POSE_JAW_ROTATION_TIMES, POSE_JAW_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.torso, POSE_TORSO_TRANSLATION_TIMES, POSE_TORSO_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.torso, POSE_TORSO_ROTATION_TIMES, POSE_TORSO_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_forearm, POSE_LEFT_FOREARM_ROTATION_TIMES, POSE_LEFT_FOREARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.forearm2, POSE_FOREARM2_ROTATION_TIMES, POSE_FOREARM2_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.index, POSE_INDEX_ROTATION_TIMES, POSE_INDEX_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.middle, POSE_MIDDLE_ROTATION_TIMES, POSE_MIDDLE_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_forearm, POSE_RIGHT_FOREARM_ROTATION_TIMES, POSE_RIGHT_FOREARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_wing, POSE_LEFT_WING_ROTATION_TIMES, POSE_LEFT_WING_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_wing, POSE_RIGHT_WING_ROTATION_TIMES, POSE_RIGHT_WING_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_rearleg, POSE_LEFT_REARLEG_ROTATION_TIMES, POSE_LEFT_REARLEG_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.rearleg2, POSE_REARLEG2_ROTATION_TIMES, POSE_REARLEG2_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.index2, POSE_INDEX2_ROTATION_TIMES, POSE_INDEX2_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.middle2, POSE_MIDDLE2_ROTATION_TIMES, POSE_MIDDLE2_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.rearleg3, POSE_REARLEG3_ROTATION_TIMES, POSE_REARLEG3_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.waist, POSE_WAIST_ROTATION_TIMES, POSE_WAIST_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.segment_4, POSE_SEGMENT_4_ROTATION_TIMES, POSE_SEGMENT_4_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.segment_1, POSE_SEGMENT_1_ROTATION_TIMES, POSE_SEGMENT_1_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.segment_2, POSE_SEGMENT_2_ROTATION_TIMES, POSE_SEGMENT_2_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.segment_3, POSE_SEGMENT_3_ROTATION_TIMES, POSE_SEGMENT_3_ROTATION_VALUES, wrappedTime);
    }
}

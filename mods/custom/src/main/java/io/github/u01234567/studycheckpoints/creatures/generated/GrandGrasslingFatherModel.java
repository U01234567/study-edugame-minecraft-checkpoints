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
 * Creature id: grand_grassling_father
 */
public class GrandGrasslingFatherModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart node_80;
    private final ModelPart body;
    private final ModelPart legs;
    private final ModelPart leg1;
    private final ModelPart cube;
    private final ModelPart leg2;
    private final ModelPart cube_2;
    private final ModelPart torso;
    private final ModelPart cube_3;
    private final ModelPart arms;
    private final ModelPart arm2;
    private final ModelPart cube_4;
    private final ModelPart arm1;
    private final ModelPart cube_5;
    private final ModelPart cube_6;
    private final ModelPart cube_7;
    private final ModelPart cube_8;
    private final ModelPart cube_9;
    private final ModelPart greens;
    private final ModelPart cube_10;
    private final ModelPart cube_11;
    private final ModelPart cube_12;
    private final ModelPart cube_13;
    private final ModelPart cube_14;
    private final ModelPart cube_15;
    private final ModelPart cube_16;
    private final ModelPart cube_17;
    private final ModelPart cube_18;
    private final ModelPart cube_19;
    private final ModelPart cube_20;
    private final ModelPart cube_21;
    private final ModelPart head;
    private final ModelPart cube_22;
    private final ModelPart cube_23;
    private final ModelPart cube_24;
    private final ModelPart cube_25;
    private final ModelPart cube_26;
    private final ModelPart cube_27;
    private final ModelPart terrain;
    private final ModelPart Flore;
    private final ModelPart cube_28;
    private final ModelPart Plants;
    private final ModelPart TallA;
    private final ModelPart TallA1;
    private final ModelPart cube_29;
    private final ModelPart cube_30;
    private final ModelPart TallA2;
    private final ModelPart cube_31;
    private final ModelPart cube_32;
    private final ModelPart TallA3;
    private final ModelPart cube_33;
    private final ModelPart cube_34;
    private final ModelPart TallB;
    private final ModelPart TallB1;
    private final ModelPart cube_35;
    private final ModelPart cube_36;
    private final ModelPart TallB2;
    private final ModelPart cube_37;
    private final ModelPart cube_38;
    private final ModelPart Small;
    private final ModelPart Small1;
    private final ModelPart cube_39;
    private final ModelPart cube_40;
    private final ModelPart Small2;
    private final ModelPart cube_41;
    private final ModelPart cube_42;
    private final ModelPart Small3;
    private final ModelPart cube_43;
    private final ModelPart cube_44;
    private final ModelPart Small4;
    private final ModelPart cube_45;
    private final ModelPart cube_46;
    private final ModelPart Small5;
    private final ModelPart cube_47;
    private final ModelPart cube_48;
    private final ModelPart Small6;
    private final ModelPart cube_49;
    private final ModelPart cube_50;
    private final ModelPart Small7;
    private final ModelPart cube_51;
    private final ModelPart cube_52;
    private static final float GREEN_MAGIC_ANIMATION_LENGTH = 24.5F;
    private static final float[] GREEN_MAGIC_ANIMATION_LEG2_TRANSLATION_TIMES = new float[]{0.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_LEG2_TRANSLATION_VALUES = new float[]{0.0F, -0.0F, 0.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_ARMS_TRANSLATION_TIMES = new float[]{0.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_ARMS_TRANSLATION_VALUES = new float[]{0.0F, -0.0F, 0.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_ARMS_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_ARMS_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_TORSO_TRANSLATION_TIMES = new float[]{0.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_TORSO_TRANSLATION_VALUES = new float[]{0.0F, -0.0F, 0.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_TORSO_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_TORSO_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_HEAD_TRANSLATION_TIMES = new float[]{0.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_HEAD_TRANSLATION_VALUES = new float[]{0.0F, -0.0F, 0.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_HEAD_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_HEAD_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_FLORE_TRANSLATION_TIMES = new float[]{14.0833F};
    private static final float[] GREEN_MAGIC_ANIMATION_FLORE_TRANSLATION_VALUES = new float[]{0.0F, -0.0F, 0.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_FLORE_SCALE_TIMES = new float[]{0.0F, 14.125F};
    private static final float[] GREEN_MAGIC_ANIMATION_FLORE_SCALE_VALUES = new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_TALLA1_SCALE_TIMES = new float[]{0.0F, 24.5F};
    private static final float[] GREEN_MAGIC_ANIMATION_TALLA1_SCALE_VALUES = new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_TALLA2_SCALE_TIMES = new float[]{0.0F, 24.5F};
    private static final float[] GREEN_MAGIC_ANIMATION_TALLA2_SCALE_VALUES = new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_TALLA3_SCALE_TIMES = new float[]{0.0F, 24.5F};
    private static final float[] GREEN_MAGIC_ANIMATION_TALLA3_SCALE_VALUES = new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_TALLB1_SCALE_TIMES = new float[]{0.0F, 24.5F};
    private static final float[] GREEN_MAGIC_ANIMATION_TALLB1_SCALE_VALUES = new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_TALLB2_SCALE_TIMES = new float[]{0.0F, 24.5F};
    private static final float[] GREEN_MAGIC_ANIMATION_TALLB2_SCALE_VALUES = new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_SMALL1_SCALE_TIMES = new float[]{0.0F, 24.5F};
    private static final float[] GREEN_MAGIC_ANIMATION_SMALL1_SCALE_VALUES = new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_SMALL2_SCALE_TIMES = new float[]{0.0F, 24.5F};
    private static final float[] GREEN_MAGIC_ANIMATION_SMALL2_SCALE_VALUES = new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_SMALL3_SCALE_TIMES = new float[]{0.0F, 24.5F};
    private static final float[] GREEN_MAGIC_ANIMATION_SMALL3_SCALE_VALUES = new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_SMALL4_SCALE_TIMES = new float[]{0.0F, 24.5F};
    private static final float[] GREEN_MAGIC_ANIMATION_SMALL4_SCALE_VALUES = new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_SMALL5_SCALE_TIMES = new float[]{0.0F, 24.5F};
    private static final float[] GREEN_MAGIC_ANIMATION_SMALL5_SCALE_VALUES = new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_SMALL6_SCALE_TIMES = new float[]{0.0F, 24.5F};
    private static final float[] GREEN_MAGIC_ANIMATION_SMALL6_SCALE_VALUES = new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_SMALL7_SCALE_TIMES = new float[]{0.0F, 24.5F};
    private static final float[] GREEN_MAGIC_ANIMATION_SMALL7_SCALE_VALUES = new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_GREENS_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] GREEN_MAGIC_ANIMATION_GREENS_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};

    public GrandGrasslingFatherModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.node_80 = this.root.getChild("node_80");
        this.body = this.node_80.getChild("body");
        this.legs = this.body.getChild("legs");
        this.leg1 = this.legs.getChild("leg1");
        this.cube = this.leg1.getChild("cube");
        this.leg2 = this.legs.getChild("leg2");
        this.cube_2 = this.leg2.getChild("cube_2");
        this.torso = this.body.getChild("torso");
        this.cube_3 = this.torso.getChild("cube_3");
        this.arms = this.torso.getChild("arms");
        this.arm2 = this.arms.getChild("arm2");
        this.cube_4 = this.arm2.getChild("cube_4");
        this.arm1 = this.arms.getChild("arm1");
        this.cube_5 = this.arm1.getChild("cube_5");
        this.cube_6 = this.arm1.getChild("cube_6");
        this.cube_7 = this.arm1.getChild("cube_7");
        this.cube_8 = this.arm1.getChild("cube_8");
        this.cube_9 = this.arm1.getChild("cube_9");
        this.greens = this.torso.getChild("greens");
        this.cube_10 = this.greens.getChild("cube_10");
        this.cube_11 = this.greens.getChild("cube_11");
        this.cube_12 = this.greens.getChild("cube_12");
        this.cube_13 = this.greens.getChild("cube_13");
        this.cube_14 = this.greens.getChild("cube_14");
        this.cube_15 = this.greens.getChild("cube_15");
        this.cube_16 = this.greens.getChild("cube_16");
        this.cube_17 = this.greens.getChild("cube_17");
        this.cube_18 = this.greens.getChild("cube_18");
        this.cube_19 = this.greens.getChild("cube_19");
        this.cube_20 = this.greens.getChild("cube_20");
        this.cube_21 = this.greens.getChild("cube_21");
        this.head = this.torso.getChild("head");
        this.cube_22 = this.head.getChild("cube_22");
        this.cube_23 = this.head.getChild("cube_23");
        this.cube_24 = this.head.getChild("cube_24");
        this.cube_25 = this.head.getChild("cube_25");
        this.cube_26 = this.head.getChild("cube_26");
        this.cube_27 = this.head.getChild("cube_27");
        this.terrain = this.node_80.getChild("terrain");
        this.Flore = this.terrain.getChild("Flore");
        this.cube_28 = this.Flore.getChild("cube_28");
        this.Plants = this.terrain.getChild("Plants");
        this.TallA = this.Plants.getChild("TallA");
        this.TallA1 = this.TallA.getChild("TallA1");
        this.cube_29 = this.TallA1.getChild("cube_29");
        this.cube_30 = this.TallA1.getChild("cube_30");
        this.TallA2 = this.TallA.getChild("TallA2");
        this.cube_31 = this.TallA2.getChild("cube_31");
        this.cube_32 = this.TallA2.getChild("cube_32");
        this.TallA3 = this.TallA.getChild("TallA3");
        this.cube_33 = this.TallA3.getChild("cube_33");
        this.cube_34 = this.TallA3.getChild("cube_34");
        this.TallB = this.Plants.getChild("TallB");
        this.TallB1 = this.TallB.getChild("TallB1");
        this.cube_35 = this.TallB1.getChild("cube_35");
        this.cube_36 = this.TallB1.getChild("cube_36");
        this.TallB2 = this.TallB.getChild("TallB2");
        this.cube_37 = this.TallB2.getChild("cube_37");
        this.cube_38 = this.TallB2.getChild("cube_38");
        this.Small = this.Plants.getChild("Small");
        this.Small1 = this.Small.getChild("Small1");
        this.cube_39 = this.Small1.getChild("cube_39");
        this.cube_40 = this.Small1.getChild("cube_40");
        this.Small2 = this.Small.getChild("Small2");
        this.cube_41 = this.Small2.getChild("cube_41");
        this.cube_42 = this.Small2.getChild("cube_42");
        this.Small3 = this.Small.getChild("Small3");
        this.cube_43 = this.Small3.getChild("cube_43");
        this.cube_44 = this.Small3.getChild("cube_44");
        this.Small4 = this.Small.getChild("Small4");
        this.cube_45 = this.Small4.getChild("cube_45");
        this.cube_46 = this.Small4.getChild("cube_46");
        this.Small5 = this.Small.getChild("Small5");
        this.cube_47 = this.Small5.getChild("cube_47");
        this.cube_48 = this.Small5.getChild("cube_48");
        this.Small6 = this.Small.getChild("Small6");
        this.cube_49 = this.Small6.getChild("cube_49");
        this.cube_50 = this.Small6.getChild("cube_50");
        this.Small7 = this.Small.getChild("Small7");
        this.cube_51 = this.Small7.getChild("cube_51");
        this.cube_52 = this.Small7.getChild("cube_52");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition node_80 = root.addOrReplaceChild("node_80", CubeListBuilder.create(), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition body = node_80.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition legs = body.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition leg1 = legs.addOrReplaceChild("leg1", CubeListBuilder.create(), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition cube = leg1.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 0).addBox(2.0F, -11.0F, -2.0F, 6.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition leg2 = legs.addOrReplaceChild("leg2", CubeListBuilder.create(), PartPose.offset(-5.0F, -9.0F, 0.0F));
        PartDefinition cube_2 = leg2.addOrReplaceChild("cube_2", CubeListBuilder.create().texOffs(26, 0).addBox(-8.0F, -11.0F, -2.0F, 6.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 9.0F, 0.0F));
        PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create(), PartPose.offset(0.0F, -19.0F, -1.0F));
        PartDefinition cube_3 = torso.addOrReplaceChild("cube_3", CubeListBuilder.create().texOffs(52, 0).addBox(-9.0F, -26.0F, -5.0F, 18.0F, 17.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 19.0F, 1.0F, 0.2618F, 0.0F, 0.0F));
        PartDefinition arms = torso.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 1.0F));
        PartDefinition arm2 = arms.addOrReplaceChild("arm2", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, 0.0F));
        PartDefinition cube_4 = arm2.addOrReplaceChild("cube_4", CubeListBuilder.create().texOffs(128, 0).addBox(9.0F, -22.0F, -3.0F, 6.0F, 20.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition arm1 = arms.addOrReplaceChild("arm1", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, 0.0F));
        PartDefinition cube_5 = arm1.addOrReplaceChild("cube_5", CubeListBuilder.create().texOffs(154, 0).addBox(-15.0F, -21.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition cube_6 = arm1.addOrReplaceChild("cube_6", CubeListBuilder.create().texOffs(180, 0).addBox(-15.0F, -13.0F, -3.0F, 6.0F, 11.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition cube_7 = arm1.addOrReplaceChild("cube_7", CubeListBuilder.create().texOffs(196, 0).addBox(-15.0F, -13.0F, -3.0F, 0.001F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition cube_8 = arm1.addOrReplaceChild("cube_8", CubeListBuilder.create().texOffs(212, 0).addBox(-15.0F, -13.0F, 3.0F, 6.0F, 11.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition cube_9 = arm1.addOrReplaceChild("cube_9", CubeListBuilder.create().texOffs(228, 0).addBox(-13.0F, -13.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition greens = torso.addOrReplaceChild("greens", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, -1.0F));
        PartDefinition cube_10 = greens.addOrReplaceChild("cube_10", CubeListBuilder.create().texOffs(238, 0).addBox(0.0F, -35.0F, -4.0F, 0.001F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.0F, 4.0F));
        PartDefinition cube_11 = greens.addOrReplaceChild("cube_11", CubeListBuilder.create().texOffs(254, 0).addBox(1.0F, -35.0F, -3.0F, 0.001F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 22.0F, 4.0F, -0.0F, 1.5708F, -0.0F));
        PartDefinition cube_12 = greens.addOrReplaceChild("cube_12", CubeListBuilder.create().texOffs(270, 0).addBox(0.0F, -39.0F, -4.0F, 0.001F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 30.0F, -3.0F));
        PartDefinition cube_13 = greens.addOrReplaceChild("cube_13", CubeListBuilder.create().texOffs(286, 0).addBox(1.0F, -39.0F, -3.0F, 0.001F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 30.0F, -3.0F, -0.0F, 1.5708F, -0.0F));
        PartDefinition cube_14 = greens.addOrReplaceChild("cube_14", CubeListBuilder.create().texOffs(302, 0).addBox(0.0F, -39.0F, -4.0F, 0.001F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 30.0F, -1.0F));
        PartDefinition cube_15 = greens.addOrReplaceChild("cube_15", CubeListBuilder.create().texOffs(318, 0).addBox(1.0F, -39.0F, -3.0F, 0.001F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 30.0F, -1.0F, -0.0F, 1.5708F, -0.0F));
        PartDefinition cube_16 = greens.addOrReplaceChild("cube_16", CubeListBuilder.create().texOffs(334, 0).addBox(0.0F, -31.0F, -2.0F, 0.001F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 23.0F, 4.0F));
        PartDefinition cube_17 = greens.addOrReplaceChild("cube_17", CubeListBuilder.create().texOffs(342, 0).addBox(1.0F, -31.0F, -1.0F, 0.001F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 23.0F, 4.0F, -0.0F, 1.5708F, -0.0F));
        PartDefinition cube_18 = greens.addOrReplaceChild("cube_18", CubeListBuilder.create().texOffs(350, 0).addBox(0.0F, -31.0F, -2.0F, 0.001F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 22.0F, 6.0F));
        PartDefinition cube_19 = greens.addOrReplaceChild("cube_19", CubeListBuilder.create().texOffs(358, 0).addBox(1.0F, -31.0F, -1.0F, 0.001F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 22.0F, 6.0F, -0.0F, 1.5708F, -0.0F));
        PartDefinition cube_20 = greens.addOrReplaceChild("cube_20", CubeListBuilder.create().texOffs(366, 0).addBox(0.0F, -31.0F, -2.0F, 0.001F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 26.0F, -5.0F));
        PartDefinition cube_21 = greens.addOrReplaceChild("cube_21", CubeListBuilder.create().texOffs(374, 0).addBox(1.0F, -31.0F, -1.0F, 0.001F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 26.0F, -5.0F, -0.0F, 1.5708F, -0.0F));
        PartDefinition head = torso.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, -7.0F));
        PartDefinition cube_22 = head.addOrReplaceChild("cube_22", CubeListBuilder.create().texOffs(382, 0).addBox(-4.0F, -22.0F, -15.0F, 8.0F, 8.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, 8.0F));
        PartDefinition cube_23 = head.addOrReplaceChild("cube_23", CubeListBuilder.create().texOffs(418, 0).addBox(4.0F, -14.0F, -15.0F, 0.001F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, 8.0F));
        PartDefinition cube_24 = head.addOrReplaceChild("cube_24", CubeListBuilder.create().texOffs(432, 0).addBox(-4.0F, -14.0F, -15.0F, 4.0F, 4.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, 8.0F));
        PartDefinition cube_25 = head.addOrReplaceChild("cube_25", CubeListBuilder.create().texOffs(444, 0).addBox(0.0F, -14.0F, -15.0F, 4.0F, 2.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, 8.0F));
        PartDefinition cube_26 = head.addOrReplaceChild("cube_26", CubeListBuilder.create().texOffs(456, 0).addBox(-4.0F, -14.0F, -15.0F, 0.001F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, 8.0F));
        PartDefinition cube_27 = head.addOrReplaceChild("cube_27", CubeListBuilder.create().texOffs(470, 0).addBox(-2.0F, -19.0F, -15.0F, 0.001F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, 8.0F));
        PartDefinition terrain = node_80.addOrReplaceChild("terrain", CubeListBuilder.create(), PartPose.offset(10.0F, 0.0F, 1.0F));
        PartDefinition Flore = terrain.addOrReplaceChild("Flore", CubeListBuilder.create(), PartPose.offset(-10.0F, 0.0F, -1.0F));
        PartDefinition cube_28 = Flore.addOrReplaceChild("cube_28", CubeListBuilder.create().texOffs(0, 38).addBox(-38.0F, -0.001F, -38.0F, 76.0F, 0.001F, 76.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition Plants = terrain.addOrReplaceChild("Plants", CubeListBuilder.create(), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition TallA = Plants.addOrReplaceChild("TallA", CubeListBuilder.create(), PartPose.offset(13.0F, 0.0F, 16.0F));
        PartDefinition TallA1 = TallA.addOrReplaceChild("TallA1", CubeListBuilder.create(), PartPose.offset(-47.0F, 0.0F, -16.0F));
        PartDefinition cube_29 = TallA1.addOrReplaceChild("cube_29", CubeListBuilder.create().texOffs(306, 38).addBox(-4.0F, -32.0F, 0.0F, 8.0F, 32.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition cube_30 = TallA1.addOrReplaceChild("cube_30", CubeListBuilder.create().texOffs(326, 38).addBox(-4.0F, -32.0F, 0.0F, 8.0F, 32.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0F, 0.0F, -0.0F, 1.5708F, -0.0F));
        PartDefinition TallA2 = TallA.addOrReplaceChild("TallA2", CubeListBuilder.create(), PartPose.offset(-39.0F, 0.0F, 0.0F));
        PartDefinition cube_31 = TallA2.addOrReplaceChild("cube_31", CubeListBuilder.create().texOffs(346, 38).addBox(-4.0F, -32.0F, 0.0F, 8.0F, 32.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition cube_32 = TallA2.addOrReplaceChild("cube_32", CubeListBuilder.create().texOffs(366, 38).addBox(-4.0F, -32.0F, 0.0F, 8.0F, 32.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0F, 0.0F, -0.0F, 1.5708F, -0.0F));
        PartDefinition TallA3 = TallA.addOrReplaceChild("TallA3", CubeListBuilder.create(), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition cube_33 = TallA3.addOrReplaceChild("cube_33", CubeListBuilder.create().texOffs(386, 38).addBox(-4.0F, -32.0F, 0.0F, 8.0F, 32.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition cube_34 = TallA3.addOrReplaceChild("cube_34", CubeListBuilder.create().texOffs(406, 38).addBox(-4.0F, -32.0F, 0.0F, 8.0F, 32.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0F, 0.0F, -0.0F, 1.5708F, -0.0F));
        PartDefinition TallB = Plants.addOrReplaceChild("TallB", CubeListBuilder.create(), PartPose.offset(1.0F, 0.0F, 25.0F));
        PartDefinition TallB1 = TallB.addOrReplaceChild("TallB1", CubeListBuilder.create(), PartPose.offset(12.0F, 0.0F, -38.0F));
        PartDefinition cube_35 = TallB1.addOrReplaceChild("cube_35", CubeListBuilder.create().texOffs(426, 38).addBox(-4.0F, -25.0F, 0.0F, 8.0F, 25.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition cube_36 = TallB1.addOrReplaceChild("cube_36", CubeListBuilder.create().texOffs(446, 38).addBox(-4.0F, -25.0F, 0.0F, 8.0F, 25.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0F, 0.0F, -0.0F, 1.5708F, -0.0F));
        PartDefinition TallB2 = TallB.addOrReplaceChild("TallB2", CubeListBuilder.create(), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition cube_37 = TallB2.addOrReplaceChild("cube_37", CubeListBuilder.create().texOffs(466, 38).addBox(-4.0F, -25.0F, 0.0F, 8.0F, 25.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition cube_38 = TallB2.addOrReplaceChild("cube_38", CubeListBuilder.create().texOffs(486, 38).addBox(-4.0F, -25.0F, 0.0F, 8.0F, 25.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0F, 0.0F, -0.0F, 1.5708F, -0.0F));
        PartDefinition Small = Plants.addOrReplaceChild("Small", CubeListBuilder.create(), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition Small1 = Small.addOrReplaceChild("Small1", CubeListBuilder.create(), PartPose.offset(-10.0F, 0.0F, 17.0F));
        PartDefinition cube_39 = Small1.addOrReplaceChild("cube_39", CubeListBuilder.create().texOffs(0, 117).addBox(-15.0F, -7.0F, -0.0F, 6.0F, 7.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, 0.0F, -1.0F, -3.1416F, 0.0436F, -3.1416F));
        PartDefinition cube_40 = Small1.addOrReplaceChild("cube_40", CubeListBuilder.create().texOffs(16, 117).addBox(-3.0F, -7.0F, -12.0F, 6.0F, 7.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, 0.0F, -1.0F, -3.1416F, -1.5272F, 3.1416F));
        PartDefinition Small2 = Small.addOrReplaceChild("Small2", CubeListBuilder.create(), PartPose.offset(12.0F, 0.0F, 1.0F));
        PartDefinition cube_41 = Small2.addOrReplaceChild("cube_41", CubeListBuilder.create().texOffs(32, 117).addBox(-15.0F, -7.0F, -0.0F, 6.0F, 7.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, 0.0F, -1.0F, -3.1416F, 0.0436F, -3.1416F));
        PartDefinition cube_42 = Small2.addOrReplaceChild("cube_42", CubeListBuilder.create().texOffs(48, 117).addBox(-3.0F, -7.0F, -12.0F, 6.0F, 7.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, 0.0F, -1.0F, -3.1416F, -1.5272F, 3.1416F));
        PartDefinition Small3 = Small.addOrReplaceChild("Small3", CubeListBuilder.create(), PartPose.offset(-36.0F, 0.0F, -21.0F));
        PartDefinition cube_43 = Small3.addOrReplaceChild("cube_43", CubeListBuilder.create().texOffs(64, 117).addBox(-7.0F, -7.0F, 15.0F, 6.0F, 7.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 0.0F, 15.0F, -3.1416F, 0.0436F, -3.1416F));
        PartDefinition cube_44 = Small3.addOrReplaceChild("cube_44", CubeListBuilder.create().texOffs(80, 117).addBox(-18.0F, -7.0F, -4.0F, 6.0F, 7.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 0.0F, 15.0F, -3.1416F, -1.5272F, 3.1416F));
        PartDefinition Small4 = Small.addOrReplaceChild("Small4", CubeListBuilder.create(), PartPose.offset(-7.0F, 0.0F, -21.0F));
        PartDefinition cube_45 = Small4.addOrReplaceChild("cube_45", CubeListBuilder.create().texOffs(96, 117).addBox(-15.0F, -7.0F, 12.0F, 6.0F, 7.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, 0.0F, 11.0F, -3.1416F, 0.0436F, -3.1416F));
        PartDefinition cube_46 = Small4.addOrReplaceChild("cube_46", CubeListBuilder.create().texOffs(112, 117).addBox(-15.0F, -7.0F, -12.0F, 6.0F, 7.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, 0.0F, 11.0F, -3.1416F, -1.5272F, 3.1416F));
        PartDefinition Small5 = Small.addOrReplaceChild("Small5", CubeListBuilder.create(), PartPose.offset(-15.0F, 0.0F, -25.0F));
        PartDefinition cube_47 = Small5.addOrReplaceChild("cube_47", CubeListBuilder.create().texOffs(128, 117).addBox(-7.0F, -7.0F, 15.0F, 6.0F, 7.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, 15.0F, -3.1416F, 0.0436F, -3.1416F));
        PartDefinition cube_48 = Small5.addOrReplaceChild("cube_48", CubeListBuilder.create().texOffs(144, 117).addBox(-18.0F, -7.0F, -4.0F, 6.0F, 7.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, 15.0F, -3.1416F, -1.5272F, 3.1416F));
        PartDefinition Small6 = Small.addOrReplaceChild("Small6", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, -18.0F));
        PartDefinition cube_49 = Small6.addOrReplaceChild("cube_49", CubeListBuilder.create().texOffs(160, 117).addBox(-30.0F, -7.0F, 9.0F, 6.0F, 7.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-27.0F, 0.0F, 8.0F, -3.1416F, 0.0436F, -3.1416F));
        PartDefinition cube_50 = Small6.addOrReplaceChild("cube_50", CubeListBuilder.create().texOffs(176, 117).addBox(-12.0F, -7.0F, -27.0F, 6.0F, 7.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-27.0F, 0.0F, 8.0F, -3.1416F, -1.5272F, 3.1416F));
        PartDefinition Small7 = Small.addOrReplaceChild("Small7", CubeListBuilder.create(), PartPose.offset(-24.0F, 0.0F, -15.0F));
        PartDefinition cube_51 = Small7.addOrReplaceChild("cube_51", CubeListBuilder.create().texOffs(192, 117).addBox(-7.0F, -7.0F, 15.0F, 6.0F, 7.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 0.0F, 15.0F, -3.1416F, 0.0436F, -3.1416F));
        PartDefinition cube_52 = Small7.addOrReplaceChild("cube_52", CubeListBuilder.create().texOffs(208, 117).addBox(-18.0F, -7.0F, -4.0F, 6.0F, 7.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 0.0F, 15.0F, -3.1416F, -1.5272F, 3.1416F));

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
        // Blockbench animation sidecar exists but is not auto-merged by this parser.
        // Blockbench animation sidecar merged from model-anim.java.
        float idleTimeSeconds = state.ageInTicks / 20.0F;
        float walkTimeSeconds = state.walkAnimationPos / 20.0F;
        applyClipGREEN_MAGIC_ANIMATION(idleTimeSeconds);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    public void setAngles(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    private void applyClipGREEN_MAGIC_ANIMATION(float time) {
        float wrappedTime = wrapAnimationTime(time, GREEN_MAGIC_ANIMATION_LENGTH);
        applyTranslationTrack(this.leg2, GREEN_MAGIC_ANIMATION_LEG2_TRANSLATION_TIMES, GREEN_MAGIC_ANIMATION_LEG2_TRANSLATION_VALUES, wrappedTime);
        applyTranslationTrack(this.arms, GREEN_MAGIC_ANIMATION_ARMS_TRANSLATION_TIMES, GREEN_MAGIC_ANIMATION_ARMS_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.arms, GREEN_MAGIC_ANIMATION_ARMS_ROTATION_TIMES, GREEN_MAGIC_ANIMATION_ARMS_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.torso, GREEN_MAGIC_ANIMATION_TORSO_TRANSLATION_TIMES, GREEN_MAGIC_ANIMATION_TORSO_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.torso, GREEN_MAGIC_ANIMATION_TORSO_ROTATION_TIMES, GREEN_MAGIC_ANIMATION_TORSO_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.head, GREEN_MAGIC_ANIMATION_HEAD_TRANSLATION_TIMES, GREEN_MAGIC_ANIMATION_HEAD_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.head, GREEN_MAGIC_ANIMATION_HEAD_ROTATION_TIMES, GREEN_MAGIC_ANIMATION_HEAD_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.Flore, GREEN_MAGIC_ANIMATION_FLORE_TRANSLATION_TIMES, GREEN_MAGIC_ANIMATION_FLORE_TRANSLATION_VALUES, wrappedTime);
        applyScaleTrack(this.Flore, GREEN_MAGIC_ANIMATION_FLORE_SCALE_TIMES, GREEN_MAGIC_ANIMATION_FLORE_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.TallA1, GREEN_MAGIC_ANIMATION_TALLA1_SCALE_TIMES, GREEN_MAGIC_ANIMATION_TALLA1_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.TallA2, GREEN_MAGIC_ANIMATION_TALLA2_SCALE_TIMES, GREEN_MAGIC_ANIMATION_TALLA2_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.TallA3, GREEN_MAGIC_ANIMATION_TALLA3_SCALE_TIMES, GREEN_MAGIC_ANIMATION_TALLA3_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.TallB1, GREEN_MAGIC_ANIMATION_TALLB1_SCALE_TIMES, GREEN_MAGIC_ANIMATION_TALLB1_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.TallB2, GREEN_MAGIC_ANIMATION_TALLB2_SCALE_TIMES, GREEN_MAGIC_ANIMATION_TALLB2_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.Small1, GREEN_MAGIC_ANIMATION_SMALL1_SCALE_TIMES, GREEN_MAGIC_ANIMATION_SMALL1_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.Small2, GREEN_MAGIC_ANIMATION_SMALL2_SCALE_TIMES, GREEN_MAGIC_ANIMATION_SMALL2_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.Small3, GREEN_MAGIC_ANIMATION_SMALL3_SCALE_TIMES, GREEN_MAGIC_ANIMATION_SMALL3_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.Small4, GREEN_MAGIC_ANIMATION_SMALL4_SCALE_TIMES, GREEN_MAGIC_ANIMATION_SMALL4_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.Small5, GREEN_MAGIC_ANIMATION_SMALL5_SCALE_TIMES, GREEN_MAGIC_ANIMATION_SMALL5_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.Small6, GREEN_MAGIC_ANIMATION_SMALL6_SCALE_TIMES, GREEN_MAGIC_ANIMATION_SMALL6_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.Small7, GREEN_MAGIC_ANIMATION_SMALL7_SCALE_TIMES, GREEN_MAGIC_ANIMATION_SMALL7_SCALE_VALUES, wrappedTime);
        applyRotationTrack(this.greens, GREEN_MAGIC_ANIMATION_GREENS_ROTATION_TIMES, GREEN_MAGIC_ANIMATION_GREENS_ROTATION_VALUES, wrappedTime);
    }
}

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
 * Creature id: cave_dweller
 */
public class CaveDwellerModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart node_51;
    private final ModelPart base;
    private final ModelPart lowerbody;
    private final ModelPart lowerbody_2;
    private final ModelPart upperbody;
    private final ModelPart upperbody_2;
    private final ModelPart neck;
    private final ModelPart neck_2;
    private final ModelPart neck_3;
    private final ModelPart head;
    private final ModelPart head_2;
    private final ModelPart head_3;
    private final ModelPart head_4;
    private final ModelPart head_5;
    private final ModelPart head_6;
    private final ModelPart head_7;
    private final ModelPart jaw;
    private final ModelPart jaw_2;
    private final ModelPart jaw_3;
    private final ModelPart jaw_4;
    private final ModelPart jaw_5;
    private final ModelPart jaw_6;
    private final ModelPart rightupperarm;
    private final ModelPart rightupperarm_2;
    private final ModelPart rightlowerarm;
    private final ModelPart rightlowerarm_2;
    private final ModelPart righthand;
    private final ModelPart righthand_2;
    private final ModelPart righthand_3;
    private final ModelPart righthand_4;
    private final ModelPart righthand_5;
    private final ModelPart leftupperarm;
    private final ModelPart leftupperarm_2;
    private final ModelPart leftlowerarm;
    private final ModelPart leftlowerarm_2;
    private final ModelPart lefthand;
    private final ModelPart lefthand_2;
    private final ModelPart lefthand_3;
    private final ModelPart lefthand_4;
    private final ModelPart lefthand_5;
    private final ModelPart upperleftleg;
    private final ModelPart upperleftleg_2;
    private final ModelPart lowerleftleg;
    private final ModelPart lowerleftleg_2;
    private final ModelPart leftfoot;
    private final ModelPart leftfoot_2;
    private final ModelPart upperrightleg;
    private final ModelPart upperrightleg_2;
    private final ModelPart lowerrightleg;
    private final ModelPart lowerrightleg_2;
    private final ModelPart rightfoot;
    private final ModelPart rightfoot_2;
    private static final float IDLE_LENGTH = 0.001F;
    private static final float RUN_LENGTH = 0.5F;
    private static final float[] RUN_UPPERBODY_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] RUN_UPPERBODY_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.1745F};
    private static final float[] RUN_NECK_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] RUN_NECK_ROTATION_VALUES = new float[]{0.0F, 0.0F, -0.2182F};
    private static final float[] RUN_HEAD_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] RUN_HEAD_ROTATION_VALUES = new float[]{0.0F, 0.0F, -0.0873F};
    private static final float[] RUN_JAW_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.5F};
    private static final float[] RUN_JAW_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.5236F, 0.0F, 0.0F, -0.0436F, 0.0F, 0.0F, 0.48F};
    private static final float[] RUN_RIGHTUPPERARM_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] RUN_RIGHTUPPERARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, -0.3054F};
    private static final float[] RUN_LEFTUPPERARM_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] RUN_LEFTUPPERARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, -0.1309F};
    private static final float[] RUN_UPPERRIGHTLEG_ROTATION_TIMES = new float[]{0.0F, 0.125F, 0.25F, 0.375F, 0.5F};
    private static final float[] RUN_UPPERRIGHTLEG_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.6109F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0036F, 0.0F, 0.0F, -0.1745F};
    private static final float[] RUN_LOWERRIGHTLEG_ROTATION_TIMES = new float[]{0.0F, 0.125F, 0.25F, 0.375F, 0.5F};
    private static final float[] RUN_LOWERRIGHTLEG_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3491F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F, 0.3927F};

    public CaveDwellerModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.node_51 = this.root.getChild("node_51");
        this.base = this.node_51.getChild("base");
        this.lowerbody = this.base.getChild("lowerbody");
        this.lowerbody_2 = this.lowerbody.getChild("lowerbody_2");
        this.upperbody = this.lowerbody.getChild("upperbody");
        this.upperbody_2 = this.upperbody.getChild("upperbody_2");
        this.neck = this.upperbody.getChild("neck");
        this.neck_2 = this.neck.getChild("neck_2");
        this.neck_3 = this.neck.getChild("neck_3");
        this.head = this.neck.getChild("head");
        this.head_2 = this.head.getChild("head_2");
        this.head_3 = this.head.getChild("head_3");
        this.head_4 = this.head.getChild("head_4");
        this.head_5 = this.head.getChild("head_5");
        this.head_6 = this.head.getChild("head_6");
        this.head_7 = this.head.getChild("head_7");
        this.jaw = this.head.getChild("jaw");
        this.jaw_2 = this.jaw.getChild("jaw_2");
        this.jaw_3 = this.jaw.getChild("jaw_3");
        this.jaw_4 = this.jaw.getChild("jaw_4");
        this.jaw_5 = this.jaw.getChild("jaw_5");
        this.jaw_6 = this.jaw.getChild("jaw_6");
        this.rightupperarm = this.upperbody.getChild("rightupperarm");
        this.rightupperarm_2 = this.rightupperarm.getChild("rightupperarm_2");
        this.rightlowerarm = this.rightupperarm.getChild("rightlowerarm");
        this.rightlowerarm_2 = this.rightlowerarm.getChild("rightlowerarm_2");
        this.righthand = this.rightlowerarm.getChild("righthand");
        this.righthand_2 = this.righthand.getChild("righthand_2");
        this.righthand_3 = this.righthand.getChild("righthand_3");
        this.righthand_4 = this.righthand.getChild("righthand_4");
        this.righthand_5 = this.righthand.getChild("righthand_5");
        this.leftupperarm = this.upperbody.getChild("leftupperarm");
        this.leftupperarm_2 = this.leftupperarm.getChild("leftupperarm_2");
        this.leftlowerarm = this.leftupperarm.getChild("leftlowerarm");
        this.leftlowerarm_2 = this.leftlowerarm.getChild("leftlowerarm_2");
        this.lefthand = this.leftlowerarm.getChild("lefthand");
        this.lefthand_2 = this.lefthand.getChild("lefthand_2");
        this.lefthand_3 = this.lefthand.getChild("lefthand_3");
        this.lefthand_4 = this.lefthand.getChild("lefthand_4");
        this.lefthand_5 = this.lefthand.getChild("lefthand_5");
        this.upperleftleg = this.lowerbody.getChild("upperleftleg");
        this.upperleftleg_2 = this.upperleftleg.getChild("upperleftleg_2");
        this.lowerleftleg = this.upperleftleg.getChild("lowerleftleg");
        this.lowerleftleg_2 = this.lowerleftleg.getChild("lowerleftleg_2");
        this.leftfoot = this.lowerleftleg.getChild("leftfoot");
        this.leftfoot_2 = this.leftfoot.getChild("leftfoot_2");
        this.upperrightleg = this.lowerbody.getChild("upperrightleg");
        this.upperrightleg_2 = this.upperrightleg.getChild("upperrightleg_2");
        this.lowerrightleg = this.upperrightleg.getChild("lowerrightleg");
        this.lowerrightleg_2 = this.lowerrightleg.getChild("lowerrightleg_2");
        this.rightfoot = this.lowerrightleg.getChild("rightfoot");
        this.rightfoot_2 = this.rightfoot.getChild("rightfoot_2");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition node_51 = root.addOrReplaceChild("node_51", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition base = node_51.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -15.0F, 0.0F, 0.0F, 1.5708F, 0.0F));
        PartDefinition lowerbody = base.addOrReplaceChild("lowerbody", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0873F));
        PartDefinition lowerbody_2 = lowerbody.addOrReplaceChild("lowerbody_2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -21.0F, -3.0F, 2.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0F, 0.0F));
        PartDefinition upperbody = lowerbody.addOrReplaceChild("upperbody", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.1309F));
        PartDefinition upperbody_2 = upperbody.addOrReplaceChild("upperbody_2", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -29.0F, -3.0F, 2.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, 0.0F));
        PartDefinition neck = upperbody.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.2182F));
        PartDefinition neck_2 = neck.addOrReplaceChild("neck_2", CubeListBuilder.create().texOffs(36, 0).addBox(-0.5F, -32.0F, -1.5F, 1.5F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 29.0F, 0.0F));
        PartDefinition neck_3 = neck.addOrReplaceChild("neck_3", CubeListBuilder.create().texOffs(48, 0).addBox(0.25F, -34.0F, -1.5F, 1.75F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 29.0F, 0.0F));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(1.5934F, -3.9167F, 0.0025F));
        PartDefinition head_2 = head.addOrReplaceChild("head_2", CubeListBuilder.create().texOffs(60, 0).addBox(1.0F, -37.0F, -2.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5934F, 32.9167F, -0.0025F));
        PartDefinition head_3 = head.addOrReplaceChild("head_3", CubeListBuilder.create().texOffs(76, 0).addBox(-0.375F, -0.5F, -0.25F, 0.25F, 1.0F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.2816F, 0.4167F, -2.0025F, 0.0F, 1.309F, 0.0F));
        PartDefinition head_4 = head.addOrReplaceChild("head_4", CubeListBuilder.create().texOffs(82, 0).addBox(0.125F, -0.5F, -1.0F, 0.25F, 1.0F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.2463F, 0.4167F, 1.8612F, 0.0F, -1.1345F, 0.0F));
        PartDefinition head_5 = head.addOrReplaceChild("head_5", CubeListBuilder.create().texOffs(88, 0).addBox(-0.125F, -0.5F, -0.25F, 0.25F, 1.0F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.2816F, 0.4167F, -1.0025F, 0.0F, -0.1745F, 0.0F));
        PartDefinition head_6 = head.addOrReplaceChild("head_6", CubeListBuilder.create().texOffs(94, 0).addBox(-0.125F, -0.5F, -0.25F, 0.25F, 1.0F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.2816F, 0.4167F, -0.0025F, 0.0F, -0.1309F, 0.0F));
        PartDefinition head_7 = head.addOrReplaceChild("head_7", CubeListBuilder.create().texOffs(100, 0).addBox(-0.125F, -0.5F, -0.25F, 0.25F, 1.0F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.2816F, 0.4167F, 0.9975F, 0.0F, 0.3054F, 0.0F));
        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create(), PartPose.offsetAndRotation(0.4066F, -0.0833F, -0.0025F, 0.0F, 0.0F, 0.829F));
        PartDefinition jaw_2 = jaw.addOrReplaceChild("jaw_2", CubeListBuilder.create().texOffs(106, 0).addBox(2.0F, -33.0F, -1.75F, 2.0F, 0.75F, 3.5F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 33.0F, 0.0F));
        PartDefinition jaw_3 = jaw.addOrReplaceChild("jaw_3", CubeListBuilder.create().texOffs(120, 0).addBox(-0.125F, -0.5F, -0.25F, 0.25F, 1.0F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.875F, -0.5F, -1.5F, 0.0F, 0.2182F, 0.0F));
        PartDefinition jaw_4 = jaw.addOrReplaceChild("jaw_4", CubeListBuilder.create().texOffs(126, 0).addBox(-0.125F, -0.5F, -0.25F, 0.25F, 1.0F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.875F, -0.5F, -0.5F, 0.0F, -0.1745F, 0.0F));
        PartDefinition jaw_5 = jaw.addOrReplaceChild("jaw_5", CubeListBuilder.create().texOffs(132, 0).addBox(-0.125F, -0.5F, -0.25F, 0.25F, 1.0F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.875F, -0.5F, 0.5F, 0.0F, 0.0436F, 0.0F));
        PartDefinition jaw_6 = jaw.addOrReplaceChild("jaw_6", CubeListBuilder.create().texOffs(138, 0).addBox(-0.125F, -0.5F, -0.25F, 0.25F, 1.0F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.875F, -0.5F, 1.5F, 0.0F, -0.2182F, 0.0F));
        PartDefinition rightupperarm = upperbody.addOrReplaceChild("rightupperarm", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -8.0F, 3.0F, 0.0873F, 0.0F, 0.0F));
        PartDefinition rightupperarm_2 = rightupperarm.addOrReplaceChild("rightupperarm_2", CubeListBuilder.create().texOffs(144, 0).addBox(-1.0F, -29.0F, 3.0F, 2.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 29.0F, -3.0F));
        PartDefinition rightlowerarm = rightupperarm.addOrReplaceChild("rightlowerarm", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 12.0F, 1.0F, -0.0873F, 0.0F, 0.0F));
        PartDefinition rightlowerarm_2 = rightlowerarm.addOrReplaceChild("rightlowerarm_2", CubeListBuilder.create().texOffs(152, 0).addBox(-1.0F, -17.0F, 3.0F, 2.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.0F, -4.0F));
        PartDefinition righthand = rightlowerarm.addOrReplaceChild("righthand", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));
        PartDefinition righthand_2 = righthand.addOrReplaceChild("righthand_2", CubeListBuilder.create().texOffs(160, 0).addBox(-1.0F, -5.0F, 3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, -4.0F));
        PartDefinition righthand_3 = righthand.addOrReplaceChild("righthand_3", CubeListBuilder.create().texOffs(168, 0).addBox(-0.25F, -4.0F, 3.0F, 0.5F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, -4.0F));
        PartDefinition righthand_4 = righthand.addOrReplaceChild("righthand_4", CubeListBuilder.create().texOffs(174, 0).addBox(0.5F, -4.0F, 3.0F, 0.5F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, -4.0F));
        PartDefinition righthand_5 = righthand.addOrReplaceChild("righthand_5", CubeListBuilder.create().texOffs(180, 0).addBox(-1.0F, -4.0F, 3.0F, 0.5F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, -4.0F));
        PartDefinition leftupperarm = upperbody.addOrReplaceChild("leftupperarm", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -8.0F, -3.0F, -0.0873F, 0.0F, 0.0F));
        PartDefinition leftupperarm_2 = leftupperarm.addOrReplaceChild("leftupperarm_2", CubeListBuilder.create().texOffs(186, 0).addBox(-1.0F, -29.0F, -4.0F, 2.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 29.0F, 3.0F));
        PartDefinition leftlowerarm = leftupperarm.addOrReplaceChild("leftlowerarm", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.0873F, 0.0F, 0.0F));
        PartDefinition leftlowerarm_2 = leftlowerarm.addOrReplaceChild("leftlowerarm_2", CubeListBuilder.create().texOffs(194, 0).addBox(-1.0F, -17.0F, -4.0F, 2.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.0F, 3.0F));
        PartDefinition lefthand = leftlowerarm.addOrReplaceChild("lefthand", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));
        PartDefinition lefthand_2 = lefthand.addOrReplaceChild("lefthand_2", CubeListBuilder.create().texOffs(202, 0).addBox(0.5F, -4.0F, -4.0F, 0.5F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 3.0F));
        PartDefinition lefthand_3 = lefthand.addOrReplaceChild("lefthand_3", CubeListBuilder.create().texOffs(208, 0).addBox(-1.0F, -4.0F, -4.0F, 0.5F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 3.0F));
        PartDefinition lefthand_4 = lefthand.addOrReplaceChild("lefthand_4", CubeListBuilder.create().texOffs(214, 0).addBox(-0.25F, -4.0F, -4.0F, 0.5F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 3.0F));
        PartDefinition lefthand_5 = lefthand.addOrReplaceChild("lefthand_5", CubeListBuilder.create().texOffs(220, 0).addBox(-1.0F, -5.0F, -4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 3.0F));
        PartDefinition upperleftleg = lowerbody.addOrReplaceChild("upperleftleg", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.0F, 0.0F, 0.0873F));
        PartDefinition upperleftleg_2 = upperleftleg.addOrReplaceChild("upperleftleg_2", CubeListBuilder.create().texOffs(228, 0).addBox(-1.0F, -15.0F, -3.0F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0F, 2.0F));
        PartDefinition lowerleftleg = upperleftleg.addOrReplaceChild("lowerleftleg", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 7.0F, 0.0F, 0.0F, 0.0F, 0.1745F));
        PartDefinition lowerleftleg_2 = lowerleftleg.addOrReplaceChild("lowerleftleg_2", CubeListBuilder.create().texOffs(236, 0).addBox(-1.0F, -8.0F, -3.0F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 2.0F));
        PartDefinition leftfoot = lowerleftleg.addOrReplaceChild("leftfoot", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, -0.1745F));
        PartDefinition leftfoot_2 = leftfoot.addOrReplaceChild("leftfoot_2", CubeListBuilder.create().texOffs(244, 0).addBox(0.0F, -1.0F, -3.5F, 3.0F, 1.0F, 1.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));
        PartDefinition upperrightleg = lowerbody.addOrReplaceChild("upperrightleg", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, 0.0F, 0.0873F));
        PartDefinition upperrightleg_2 = upperrightleg.addOrReplaceChild("upperrightleg_2", CubeListBuilder.create().texOffs(256, 0).addBox(-1.0F, -15.0F, 2.0F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0F, -2.0F));
        PartDefinition lowerrightleg = upperrightleg.addOrReplaceChild("lowerrightleg", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 7.0F, 0.0F, 0.0F, 0.0F, 0.1309F));
        PartDefinition lowerrightleg_2 = lowerrightleg.addOrReplaceChild("lowerrightleg_2", CubeListBuilder.create().texOffs(264, 0).addBox(-1.0F, -8.0F, 2.0F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, -2.0F));
        PartDefinition rightfoot = lowerrightleg.addOrReplaceChild("rightfoot", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, -0.1309F));
        PartDefinition rightfoot_2 = rightfoot.addOrReplaceChild("rightfoot_2", CubeListBuilder.create().texOffs(272, 0).addBox(0.0F, -1.0F, 2.0F, 3.0F, 1.0F, 1.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -2.0F));

        return LayerDefinition.create(meshDefinition, 512, 16);
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
        if (state.walkAnimationSpeed > 0.12F) {
            applyClipRUN(walkTimeSeconds);
        } else {
            applyClipIDLE(idleTimeSeconds);
        }
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    public void setAngles(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    private void applyClipIDLE(float time) {
        float wrappedTime = wrapAnimationTime(time, IDLE_LENGTH);
    }

    private void applyClipRUN(float time) {
        float wrappedTime = wrapAnimationTime(time, RUN_LENGTH);
        applyRotationTrack(this.upperbody, RUN_UPPERBODY_ROTATION_TIMES, RUN_UPPERBODY_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.neck, RUN_NECK_ROTATION_TIMES, RUN_NECK_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.head, RUN_HEAD_ROTATION_TIMES, RUN_HEAD_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.jaw, RUN_JAW_ROTATION_TIMES, RUN_JAW_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.rightupperarm, RUN_RIGHTUPPERARM_ROTATION_TIMES, RUN_RIGHTUPPERARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.leftupperarm, RUN_LEFTUPPERARM_ROTATION_TIMES, RUN_LEFTUPPERARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.upperrightleg, RUN_UPPERRIGHTLEG_ROTATION_TIMES, RUN_UPPERRIGHTLEG_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.lowerrightleg, RUN_LOWERRIGHTLEG_ROTATION_TIMES, RUN_LOWERRIGHTLEG_ROTATION_VALUES, wrappedTime);
    }
}

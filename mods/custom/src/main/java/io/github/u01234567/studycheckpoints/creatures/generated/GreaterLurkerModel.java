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
 * Creature id: greater_lurker
 */
public class GreaterLurkerModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart greaterlurker;
    private final ModelPart head;
    private final ModelPart head_2;
    private final ModelPart neck;
    private final ModelPart lowerjaw;
    private final ModelPart ljaw;
    private final ModelPart teeth;
    private final ModelPart tooth;
    private final ModelPart tooth_2;
    private final ModelPart tooth_3;
    private final ModelPart tooth_4;
    private final ModelPart teeth2;
    private final ModelPart tooth_5;
    private final ModelPart tooth_6;
    private final ModelPart tooth_7;
    private final ModelPart tooth_8;
    private final ModelPart tooth_9;
    private final ModelPart upperjaw;
    private final ModelPart ujaw;
    private final ModelPart ujaw_2;
    private final ModelPart cube;
    private final ModelPart cube_2;
    private final ModelPart cube_3;
    private final ModelPart teeth3;
    private final ModelPart cube_4;
    private final ModelPart cube_5;
    private final ModelPart cube_6;
    private final ModelPart cube_7;
    private final ModelPart cube_8;
    private final ModelPart cube_9;
    private final ModelPart cube_10;
    private final ModelPart cube_11;
    private final ModelPart teeth4;
    private final ModelPart cube_12;
    private final ModelPart cube_13;
    private final ModelPart cube_14;
    private final ModelPart cube_15;
    private final ModelPart cube_16;
    private final ModelPart cube_17;
    private final ModelPart cube_18;
    private final ModelPart legs;
    private final ModelPart rflipper;
    private final ModelPart cube_19;
    private final ModelPart lflipper;
    private final ModelPart cube_20;
    private final ModelPart rleg;
    private final ModelPart lbase;
    private final ModelPart flipper;
    private final ModelPart claw1;
    private final ModelPart claw2;
    private final ModelPart claw3;
    private final ModelPart lleg;
    private final ModelPart lbase_2;
    private final ModelPart flipper_2;
    private final ModelPart claw1_2;
    private final ModelPart claw2_2;
    private final ModelPart claw3_2;
    private final ModelPart body;
    private final ModelPart bodyfront;
    private final ModelPart body_2;
    private final ModelPart bodyback;
    private final ModelPart bonescales;
    private final ModelPart bonescale;
    private final ModelPart bonescale_2;
    private final ModelPart bonescale_3;
    private final ModelPart bonescale_4;
    private final ModelPart tail;
    private final ModelPart tail1;
    private final ModelPart skale;
    private final ModelPart tail2;
    private final ModelPart tail2_2;
    private final ModelPart skale_2;
    private final ModelPart tail3;
    private final ModelPart tail3_2;
    private final ModelPart skale_3;
    private final ModelPart tail4;
    private final ModelPart tail4_2;
    private final ModelPart fluke1;
    private final ModelPart fluke2;
    private final ModelPart fluke3;
    private final ModelPart skale_4;

    public GreaterLurkerModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.greaterlurker = this.root.getChild("greaterlurker");
        this.head = this.greaterlurker.getChild("head");
        this.head_2 = this.head.getChild("head_2");
        this.neck = this.head.getChild("neck");
        this.lowerjaw = this.head.getChild("lowerjaw");
        this.ljaw = this.lowerjaw.getChild("ljaw");
        this.teeth = this.lowerjaw.getChild("teeth");
        this.tooth = this.teeth.getChild("tooth");
        this.tooth_2 = this.teeth.getChild("tooth_2");
        this.tooth_3 = this.teeth.getChild("tooth_3");
        this.tooth_4 = this.teeth.getChild("tooth_4");
        this.teeth2 = this.teeth.getChild("teeth2");
        this.tooth_5 = this.teeth2.getChild("tooth_5");
        this.tooth_6 = this.teeth2.getChild("tooth_6");
        this.tooth_7 = this.teeth2.getChild("tooth_7");
        this.tooth_8 = this.teeth2.getChild("tooth_8");
        this.tooth_9 = this.teeth2.getChild("tooth_9");
        this.upperjaw = this.head.getChild("upperjaw");
        this.ujaw = this.upperjaw.getChild("ujaw");
        this.ujaw_2 = this.upperjaw.getChild("ujaw_2");
        this.cube = this.upperjaw.getChild("cube");
        this.cube_2 = this.upperjaw.getChild("cube_2");
        this.cube_3 = this.upperjaw.getChild("cube_3");
        this.teeth3 = this.upperjaw.getChild("teeth3");
        this.cube_4 = this.teeth3.getChild("cube_4");
        this.cube_5 = this.teeth3.getChild("cube_5");
        this.cube_6 = this.teeth3.getChild("cube_6");
        this.cube_7 = this.teeth3.getChild("cube_7");
        this.cube_8 = this.teeth3.getChild("cube_8");
        this.cube_9 = this.teeth3.getChild("cube_9");
        this.cube_10 = this.teeth3.getChild("cube_10");
        this.cube_11 = this.teeth3.getChild("cube_11");
        this.teeth4 = this.teeth3.getChild("teeth4");
        this.cube_12 = this.teeth4.getChild("cube_12");
        this.cube_13 = this.teeth4.getChild("cube_13");
        this.cube_14 = this.teeth4.getChild("cube_14");
        this.cube_15 = this.teeth4.getChild("cube_15");
        this.cube_16 = this.teeth4.getChild("cube_16");
        this.cube_17 = this.teeth4.getChild("cube_17");
        this.cube_18 = this.teeth4.getChild("cube_18");
        this.legs = this.greaterlurker.getChild("legs");
        this.rflipper = this.legs.getChild("rflipper");
        this.cube_19 = this.rflipper.getChild("cube_19");
        this.lflipper = this.legs.getChild("lflipper");
        this.cube_20 = this.lflipper.getChild("cube_20");
        this.rleg = this.legs.getChild("rleg");
        this.lbase = this.rleg.getChild("lbase");
        this.flipper = this.rleg.getChild("flipper");
        this.claw1 = this.rleg.getChild("claw1");
        this.claw2 = this.rleg.getChild("claw2");
        this.claw3 = this.rleg.getChild("claw3");
        this.lleg = this.legs.getChild("lleg");
        this.lbase_2 = this.lleg.getChild("lbase_2");
        this.flipper_2 = this.lleg.getChild("flipper_2");
        this.claw1_2 = this.lleg.getChild("claw1_2");
        this.claw2_2 = this.lleg.getChild("claw2_2");
        this.claw3_2 = this.lleg.getChild("claw3_2");
        this.body = this.greaterlurker.getChild("body");
        this.bodyfront = this.body.getChild("bodyfront");
        this.body_2 = this.body.getChild("body_2");
        this.bodyback = this.body.getChild("bodyback");
        this.bonescales = this.body.getChild("bonescales");
        this.bonescale = this.bonescales.getChild("bonescale");
        this.bonescale_2 = this.bonescales.getChild("bonescale_2");
        this.bonescale_3 = this.bonescales.getChild("bonescale_3");
        this.bonescale_4 = this.bonescales.getChild("bonescale_4");
        this.tail = this.greaterlurker.getChild("tail");
        this.tail1 = this.tail.getChild("tail1");
        this.skale = this.tail.getChild("skale");
        this.tail2 = this.tail.getChild("tail2");
        this.tail2_2 = this.tail2.getChild("tail2_2");
        this.skale_2 = this.tail2.getChild("skale_2");
        this.tail3 = this.tail2.getChild("tail3");
        this.tail3_2 = this.tail3.getChild("tail3_2");
        this.skale_3 = this.tail3.getChild("skale_3");
        this.tail4 = this.tail3.getChild("tail4");
        this.tail4_2 = this.tail4.getChild("tail4_2");
        this.fluke1 = this.tail4.getChild("fluke1");
        this.fluke2 = this.tail4.getChild("fluke2");
        this.fluke3 = this.tail4.getChild("fluke3");
        this.skale_4 = this.tail4.getChild("skale_4");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition greaterlurker = root.addOrReplaceChild("greaterlurker", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.0F, -3.0F, -0.0436F, 0.0F, 0.0F));
        PartDefinition head = greaterlurker.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, -11.0F, -17.0F, -0.1319F, -0.2613F, 0.0001F));
        PartDefinition head_2 = head.addOrReplaceChild("head_2", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -13.75F, 5.0F, 13.0F, 13.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, -15.0F));
        PartDefinition neck = head.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(44, 0).addBox(-6.5F, -7.0F, -11.25F, 12.0F, 14.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, -4.5F, 0.0349F, 0.0F, 0.0F));
        PartDefinition lowerjaw = head.addOrReplaceChild("lowerjaw", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, 0.1745F, 0.0F, 0.0F));
        PartDefinition ljaw = lowerjaw.addOrReplaceChild("ljaw", CubeListBuilder.create().texOffs(110, 0).addBox(-8.0F, -0.5F, -34.0F, 15.0F, 7.0F, 34.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 1.0F));
        PartDefinition teeth = lowerjaw.addOrReplaceChild("teeth", CubeListBuilder.create(), PartPose.offset(5.75F, -4.0F, -28.25F));
        PartDefinition tooth = teeth.addOrReplaceChild("tooth", CubeListBuilder.create().texOffs(210, 0).addBox(-1.0F, -1.5F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 2.25F, -4.0F, 0.3624F, -0.4623F, 0.0151F));
        PartDefinition tooth_2 = teeth.addOrReplaceChild("tooth_2", CubeListBuilder.create().texOffs(218, 0).addBox(0.5F, -1.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, 0.0F, 4.75F, 0.098F, -0.0146F, 0.2989F));
        PartDefinition tooth_3 = teeth.addOrReplaceChild("tooth_3", CubeListBuilder.create().texOffs(226, 0).addBox(0.25F, -1.25F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, 1.25F, 10.0F, -0.0344F, -0.0193F, 0.3861F));
        PartDefinition tooth_4 = teeth.addOrReplaceChild("tooth_4", CubeListBuilder.create().texOffs(234, 0).addBox(-0.5F, -4.0F, -1.25F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1924F, -0.0506F, 0.1657F));
        PartDefinition teeth2 = teeth.addOrReplaceChild("teeth2", CubeListBuilder.create(), PartPose.offset(-12.5F, 0.0F, 0.0F));
        PartDefinition tooth_5 = teeth2.addOrReplaceChild("tooth_5", CubeListBuilder.create().texOffs(244, 0).addBox(-1.0F, -2.5F, -0.5F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.25F, 2.0F, -3.75F, 0.3471F, 0.1312F, 0.0014F));
        PartDefinition tooth_6 = teeth2.addOrReplaceChild("tooth_6", CubeListBuilder.create().texOffs(0, 43).addBox(-1.5F, -1.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, 0.0F, 4.75F, 0.098F, 0.0146F, -0.2989F));
        PartDefinition tooth_7 = teeth2.addOrReplaceChild("tooth_7", CubeListBuilder.create().texOffs(8, 43).addBox(-1.25F, -1.25F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, 1.25F, 10.0F, -0.0344F, 0.0193F, -0.3861F));
        PartDefinition tooth_8 = teeth2.addOrReplaceChild("tooth_8", CubeListBuilder.create().texOffs(16, 43).addBox(-0.75F, -2.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, 3.0F, 14.75F, -0.008F, 0.1538F, -0.2141F));
        PartDefinition tooth_9 = teeth2.addOrReplaceChild("tooth_9", CubeListBuilder.create().texOffs(24, 43).addBox(-1.0F, -1.0F, -0.75F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 0.25F, 0.1874F, 0.067F, -0.2515F));
        PartDefinition upperjaw = head.addOrReplaceChild("upperjaw", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -5.25F, -10.0F, -0.2618F, 0.0F, 0.0F));
        PartDefinition ujaw = upperjaw.addOrReplaceChild("ujaw", CubeListBuilder.create().texOffs(34, 43).addBox(-6.5F, -3.0F, -10.5F, 12.0F, 6.0F, 25.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, -12.5F));
        PartDefinition ujaw_2 = upperjaw.addOrReplaceChild("ujaw_2", CubeListBuilder.create().texOffs(110, 43).addBox(-6.0F, -1.0F, -2.75F, 11.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -26.5F, 0.7854F, 0.0F, 0.0F));
        PartDefinition cube = upperjaw.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(148, 43).addBox(-1.5F, 0.25F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -3.0F, -18.5F, -0.0436F, 0.0F, 0.0F));
        PartDefinition cube_2 = upperjaw.addOrReplaceChild("cube_2", CubeListBuilder.create().texOffs(162, 43).addBox(-1.0F, 0.25F, 6.25F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -3.0F, -18.5F, -0.0436F, 0.0F, 0.0F));
        PartDefinition cube_3 = upperjaw.addOrReplaceChild("cube_3", CubeListBuilder.create().texOffs(172, 43).addBox(-0.5F, 0.0F, 14.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -3.0F, -18.5F, -0.0436F, 0.0F, 0.0F));
        PartDefinition teeth3 = upperjaw.addOrReplaceChild("teeth3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, -26.5F));
        PartDefinition cube_4 = teeth3.addOrReplaceChild("cube_4", CubeListBuilder.create().texOffs(178, 43).addBox(-0.75F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, 4.0F, 4.5F, -0.1309F, 0.0F, 0.0F));
        PartDefinition cube_5 = teeth3.addOrReplaceChild("cube_5", CubeListBuilder.create().texOffs(184, 43).addBox(-0.5F, -0.75F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, 4.0F, 6.5F, -0.1745F, 0.0F, 0.0F));
        PartDefinition cube_6 = teeth3.addOrReplaceChild("cube_6", CubeListBuilder.create().texOffs(190, 43).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, 4.25F, 8.5F, -0.1309F, 0.0F, 0.0F));
        PartDefinition cube_7 = teeth3.addOrReplaceChild("cube_7", CubeListBuilder.create().texOffs(196, 43).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, 3.5F, 10.5F, 0.0873F, 0.0F, 0.0F));
        PartDefinition cube_8 = teeth3.addOrReplaceChild("cube_8", CubeListBuilder.create().texOffs(202, 43).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, 4.25F, 12.5F, -0.0873F, 0.0F, 0.0F));
        PartDefinition cube_9 = teeth3.addOrReplaceChild("cube_9", CubeListBuilder.create().texOffs(208, 43).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, 3.5F, 14.5F, -0.0873F, 0.0F, 0.0F));
        PartDefinition cube_10 = teeth3.addOrReplaceChild("cube_10", CubeListBuilder.create().texOffs(214, 43).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, 4.0F, 16.5F, 0.0873F, 0.0F, 0.0F));
        PartDefinition cube_11 = teeth3.addOrReplaceChild("cube_11", CubeListBuilder.create().texOffs(220, 43).addBox(-0.75F, -2.0F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 5.5F, 1.5F, 0.2182F, 0.0F, 0.0F));
        PartDefinition teeth4 = teeth3.addOrReplaceChild("teeth4", CubeListBuilder.create(), PartPose.offset(-1.0F, 0.0F, 0.0F));
        PartDefinition cube_12 = teeth4.addOrReplaceChild("cube_12", CubeListBuilder.create().texOffs(226, 43).addBox(-0.25F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, 4.0F, 4.5F, -0.1309F, 0.0F, 0.0F));
        PartDefinition cube_13 = teeth4.addOrReplaceChild("cube_13", CubeListBuilder.create().texOffs(232, 43).addBox(-0.5F, -0.75F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, 4.0F, 6.5F, -0.1745F, 0.0F, 0.0F));
        PartDefinition cube_14 = teeth4.addOrReplaceChild("cube_14", CubeListBuilder.create().texOffs(238, 43).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, 3.5F, 10.5F, 0.0873F, 0.0F, 0.0F));
        PartDefinition cube_15 = teeth4.addOrReplaceChild("cube_15", CubeListBuilder.create().texOffs(244, 43).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, 4.25F, 12.5F, -0.0873F, 0.0F, 0.0F));
        PartDefinition cube_16 = teeth4.addOrReplaceChild("cube_16", CubeListBuilder.create().texOffs(250, 43).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, 3.5F, 14.5F, -0.0873F, 0.0F, 0.0F));
        PartDefinition cube_17 = teeth4.addOrReplaceChild("cube_17", CubeListBuilder.create().texOffs(0, 76).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, 4.0F, 16.5F, 0.0873F, 0.0F, 0.0F));
        PartDefinition cube_18 = teeth4.addOrReplaceChild("cube_18", CubeListBuilder.create().texOffs(6, 76).addBox(-0.25F, -2.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 5.5F, 1.5F, 0.2182F, 0.0F, 0.0F));
        PartDefinition legs = greaterlurker.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition rflipper = legs.addOrReplaceChild("rflipper", CubeListBuilder.create(), PartPose.offsetAndRotation(9.25F, -4.0F, 22.25F, 0.0F, 0.2618F, 0.0F));
        PartDefinition cube_19 = rflipper.addOrReplaceChild("cube_19", CubeListBuilder.create().texOffs(12, 76).addBox(-2.5F, -1.0F, -5.5F, 6.0F, 2.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 0.0F, 3.5F, -0.2182F, 0.4363F, 0.1745F));
        PartDefinition lflipper = legs.addOrReplaceChild("lflipper", CubeListBuilder.create(), PartPose.offsetAndRotation(-9.25F, -4.0F, 22.25F, 0.0F, 0.0873F, 0.0F));
        PartDefinition cube_20 = lflipper.addOrReplaceChild("cube_20", CubeListBuilder.create().texOffs(48, 76).addBox(-3.5F, -1.0F, -5.5F, 6.0F, 2.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 0.0F, 3.5F, -0.2182F, -0.4363F, -0.1745F));
        PartDefinition rleg = legs.addOrReplaceChild("rleg", CubeListBuilder.create(), PartPose.offsetAndRotation(9.0F, -6.0F, -7.0F, -0.0132F, -0.0875F, -0.0841F));
        PartDefinition lbase = rleg.addOrReplaceChild("lbase", CubeListBuilder.create().texOffs(84, 76).addBox(-6.0F, -2.0F, -3.0F, 8.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.75F, 1.25F, -2.0F, 0.0F, 0.2618F, 0.4363F));
        PartDefinition flipper = rleg.addOrReplaceChild("flipper", CubeListBuilder.create().texOffs(112, 76).addBox(-4.0F, -1.0F, -7.0F, 9.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.25F, 2.75F, 1.0F, 0.0F, 0.6109F, 0.4363F));
        PartDefinition claw1 = rleg.addOrReplaceChild("claw1", CubeListBuilder.create().texOffs(162, 76).addBox(-0.75F, -0.75F, -2.5F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.0F, 5.5F, 6.25F, -0.1542F, 0.4765F, 0.462F));
        PartDefinition claw2 = rleg.addOrReplaceChild("claw2", CubeListBuilder.create().texOffs(178, 76).addBox(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.75F, 4.0F, 7.75F, -0.1542F, 0.4765F, 0.462F));
        PartDefinition claw3 = rleg.addOrReplaceChild("claw3", CubeListBuilder.create().texOffs(192, 76).addBox(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, 3.0F, 8.75F, -0.1721F, 0.6487F, 0.4285F));
        PartDefinition lleg = legs.addOrReplaceChild("lleg", CubeListBuilder.create(), PartPose.offsetAndRotation(-9.0F, -6.0F, -7.0F, 0.0502F, -0.0902F, 0.1601F));
        PartDefinition lbase_2 = lleg.addOrReplaceChild("lbase_2", CubeListBuilder.create().texOffs(200, 76).addBox(-2.0F, -2.0F, -3.0F, 8.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.75F, 1.25F, -2.0F, 0.0F, -0.2618F, -0.4363F));
        PartDefinition flipper_2 = lleg.addOrReplaceChild("flipper_2", CubeListBuilder.create().texOffs(0, 95).addBox(-5.0F, -1.0F, -7.0F, 9.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.25F, 2.75F, 1.0F, 0.0F, -0.6109F, -0.4363F));
        PartDefinition claw1_2 = lleg.addOrReplaceChild("claw1_2", CubeListBuilder.create().texOffs(50, 95).addBox(-1.25F, -0.75F, -2.5F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.0F, 5.5F, 6.25F, -0.1542F, -0.4765F, -0.462F));
        PartDefinition claw2_2 = lleg.addOrReplaceChild("claw2_2", CubeListBuilder.create().texOffs(66, 95).addBox(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.75F, 4.0F, 7.75F, -0.1542F, -0.4765F, -0.462F));
        PartDefinition claw3_2 = lleg.addOrReplaceChild("claw3_2", CubeListBuilder.create().texOffs(80, 95).addBox(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.0F, 3.0F, 8.75F, -0.1721F, -0.6487F, -0.4285F));
        PartDefinition body = greaterlurker.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.5F, -11.25F, -2.0F));
        PartDefinition bodyfront = body.addOrReplaceChild("bodyfront", CubeListBuilder.create().texOffs(88, 95).addBox(-9.0F, -9.0F, -2.5F, 17.0F, 18.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.25F, -10.5F));
        PartDefinition body_2 = body.addOrReplaceChild("body_2", CubeListBuilder.create().texOffs(134, 95).addBox(-10.5F, -19.0F, -10.0F, 20.0F, 19.0F, 35.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.25F, 2.0F));
        PartDefinition bodyback = body.addOrReplaceChild("bodyback", CubeListBuilder.create().texOffs(0, 151).addBox(-9.0F, -8.75F, 27.0F, 17.0F, 18.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bonescales = body.addOrReplaceChild("bonescales", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bonescale = bonescales.addOrReplaceChild("bonescale", CubeListBuilder.create().texOffs(46, 151).addBox(-2.0F, -1.0F, -1.5F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -10.0F, -3.0F, -0.0436F, 0.0F, 0.0F));
        PartDefinition bonescale_2 = bonescales.addOrReplaceChild("bonescale_2", CubeListBuilder.create().texOffs(62, 151).addBox(-1.5F, -1.25F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -9.75F, 5.0F, -0.0436F, 0.0F, 0.0F));
        PartDefinition bonescale_3 = bonescales.addOrReplaceChild("bonescale_3", CubeListBuilder.create().texOffs(76, 151).addBox(-1.5F, -1.25F, 7.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -9.75F, 5.0F, -0.0436F, 0.0F, 0.0F));
        PartDefinition bonescale_4 = bonescales.addOrReplaceChild("bonescale_4", CubeListBuilder.create().texOffs(88, 151).addBox(-1.0F, -1.25F, 15.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -9.75F, 5.0F, -0.0436F, 0.0F, 0.0F));
        PartDefinition tail = greaterlurker.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -15.0F, 29.0F, 0.0F, 0.1309F, 0.0F));
        PartDefinition tail1 = tail.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(98, 151).addBox(-7.0F, -8.5F, 31.0F, 13.0F, 15.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 3.75F, -31.0F));
        PartDefinition skale = tail.addOrReplaceChild("skale", CubeListBuilder.create().texOffs(148, 151).addBox(-1.5F, -24.75F, 27.75F, 2.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -9.25F, -30.0F, -0.7854F, 0.0F, 0.0F));
        PartDefinition tail2 = tail.addOrReplaceChild("tail2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.5F, 11.0F, -0.0011F, 0.218F, -0.0097F));
        PartDefinition tail2_2 = tail2.addOrReplaceChild("tail2_2", CubeListBuilder.create().texOffs(164, 151).addBox(-5.0F, -6.0F, -5.5F, 10.0F, 12.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 4.5F));
        PartDefinition skale_2 = tail2.addOrReplaceChild("skale_2", CubeListBuilder.create().texOffs(208, 151).addBox(-1.5F, -31.25F, 33.5F, 2.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -8.75F, -41.0F, -0.7854F, 0.0F, 0.0F));
        PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.5F, 10.0F, -0.0015F, 0.2615F, -0.0117F));
        PartDefinition tail3_2 = tail3.addOrReplaceChild("tail3_2", CubeListBuilder.create().texOffs(0, 179).addBox(-4.5F, -8.0F, 51.0F, 8.0F, 9.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 4.75F, -52.0F));
        PartDefinition skale_3 = tail3.addOrReplaceChild("skale_3", CubeListBuilder.create().texOffs(42, 179).addBox(-1.5F, -38.75F, 40.25F, 2.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -8.25F, -51.0F, -0.7854F, 0.0F, 0.0F));
        PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 11.0F, 0.0F, 0.2618F, 0.0F));
        PartDefinition tail4_2 = tail4.addOrReplaceChild("tail4_2", CubeListBuilder.create().texOffs(60, 179).addBox(-3.5F, -8.0F, 54.0F, 6.0F, 7.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 5.0F, -55.0F));
        PartDefinition fluke1 = tail4.addOrReplaceChild("fluke1", CubeListBuilder.create().texOffs(112, 179).addBox(-1.5F, -8.5F, -3.5F, 2.0F, 17.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -7.0F, 20.25F, -0.6981F, 0.0F, 0.0F));
        PartDefinition fluke2 = tail4.addOrReplaceChild("fluke2", CubeListBuilder.create().texOffs(132, 179).addBox(-1.5F, -6.0F, -2.5F, 2.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 5.75F, 18.75F, 0.6981F, 0.0F, 0.0F));
        PartDefinition fluke3 = tail4.addOrReplaceChild("fluke3", CubeListBuilder.create().texOffs(148, 179).addBox(-1.5F, -3.5F, 0.0F, 2.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 5.5F, 7.25F, 0.5236F, 0.0F, 0.0F));
        PartDefinition skale_4 = tail4.addOrReplaceChild("skale_4", CubeListBuilder.create().texOffs(160, 179).addBox(-1.5F, -47.25F, 47.75F, 2.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -8.25F, -62.0F, -0.7854F, 0.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 256, 256);
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
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    public void setAngles(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

}

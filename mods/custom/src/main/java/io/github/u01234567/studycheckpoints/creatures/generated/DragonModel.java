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
 * Creature id: dragon
 */
public class DragonModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart wings3;
    private final ModelPart cube_0;
    private final ModelPart cube_1;
    private final ModelPart cube_2;
    private final ModelPart wings4;
    private final ModelPart cube_3;
    private final ModelPart cube_4;
    private final ModelPart cube_5;
    private final ModelPart wings5;
    private final ModelPart cube_6;
    private final ModelPart cube_7;
    private final ModelPart cube_8;
    private final ModelPart cube_9;
    private final ModelPart leg3;
    private final ModelPart cube_10;
    private final ModelPart leg4;
    private final ModelPart cube_11;
    private final ModelPart leg5;
    private final ModelPart cube_12;
    private final ModelPart head0;
    private final ModelPart cube_13;
    private final ModelPart cube_14;
    private final ModelPart cube_15;
    private final ModelPart cube_16;
    private final ModelPart cube_17;
    private final ModelPart cube_18;
    private final ModelPart cube_19;
    private final ModelPart wings0;
    private final ModelPart cube_20;
    private final ModelPart cube_21;
    private final ModelPart cube_22;
    private final ModelPart wings1;
    private final ModelPart cube_23;
    private final ModelPart cube_24;
    private final ModelPart cube_25;
    private final ModelPart wings2;
    private final ModelPart cube_26;
    private final ModelPart cube_27;
    private final ModelPart cube_28;
    private final ModelPart cube_29;
    private final ModelPart tail0;
    private final ModelPart cube_30;
    private final ModelPart tail1;
    private final ModelPart cube_31;
    private final ModelPart tail2;
    private final ModelPart cube_32;
    private final ModelPart leg9;
    private final ModelPart cube_33;
    private final ModelPart leg10;
    private final ModelPart cube_34;
    private final ModelPart leg11;
    private final ModelPart cube_35;
    private final ModelPart leg6;
    private final ModelPart cube_36;
    private final ModelPart leg7;
    private final ModelPart cube_37;
    private final ModelPart leg8;
    private final ModelPart cube_38;
    private final ModelPart leg0;
    private final ModelPart cube_39;
    private final ModelPart leg1;
    private final ModelPart cube_40;
    private final ModelPart leg2;
    private final ModelPart cube_41;
    private final ModelPart body0;
    private final ModelPart cube_42;
    private final ModelPart cube_43;
    private final ModelPart cube_44;

    public DragonModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.wings3 = this.root.getChild("wings3");
        this.cube_0 = this.wings3.getChild("cube_0");
        this.cube_1 = this.wings3.getChild("cube_1");
        this.cube_2 = this.wings3.getChild("cube_2");
        this.wings4 = this.wings3.getChild("wings4");
        this.cube_3 = this.wings4.getChild("cube_3");
        this.cube_4 = this.wings4.getChild("cube_4");
        this.cube_5 = this.wings4.getChild("cube_5");
        this.wings5 = this.wings4.getChild("wings5");
        this.cube_6 = this.wings5.getChild("cube_6");
        this.cube_7 = this.wings5.getChild("cube_7");
        this.cube_8 = this.wings5.getChild("cube_8");
        this.cube_9 = this.wings5.getChild("cube_9");
        this.leg3 = this.root.getChild("leg3");
        this.cube_10 = this.leg3.getChild("cube_10");
        this.leg4 = this.leg3.getChild("leg4");
        this.cube_11 = this.leg4.getChild("cube_11");
        this.leg5 = this.leg4.getChild("leg5");
        this.cube_12 = this.leg5.getChild("cube_12");
        this.head0 = this.root.getChild("head0");
        this.cube_13 = this.head0.getChild("cube_13");
        this.cube_14 = this.head0.getChild("cube_14");
        this.cube_15 = this.head0.getChild("cube_15");
        this.cube_16 = this.head0.getChild("cube_16");
        this.cube_17 = this.head0.getChild("cube_17");
        this.cube_18 = this.head0.getChild("cube_18");
        this.cube_19 = this.head0.getChild("cube_19");
        this.wings0 = this.root.getChild("wings0");
        this.cube_20 = this.wings0.getChild("cube_20");
        this.cube_21 = this.wings0.getChild("cube_21");
        this.cube_22 = this.wings0.getChild("cube_22");
        this.wings1 = this.wings0.getChild("wings1");
        this.cube_23 = this.wings1.getChild("cube_23");
        this.cube_24 = this.wings1.getChild("cube_24");
        this.cube_25 = this.wings1.getChild("cube_25");
        this.wings2 = this.wings1.getChild("wings2");
        this.cube_26 = this.wings2.getChild("cube_26");
        this.cube_27 = this.wings2.getChild("cube_27");
        this.cube_28 = this.wings2.getChild("cube_28");
        this.cube_29 = this.wings2.getChild("cube_29");
        this.tail0 = this.root.getChild("tail0");
        this.cube_30 = this.tail0.getChild("cube_30");
        this.tail1 = this.tail0.getChild("tail1");
        this.cube_31 = this.tail1.getChild("cube_31");
        this.tail2 = this.tail1.getChild("tail2");
        this.cube_32 = this.tail2.getChild("cube_32");
        this.leg9 = this.root.getChild("leg9");
        this.cube_33 = this.leg9.getChild("cube_33");
        this.leg10 = this.leg9.getChild("leg10");
        this.cube_34 = this.leg10.getChild("cube_34");
        this.leg11 = this.leg10.getChild("leg11");
        this.cube_35 = this.leg11.getChild("cube_35");
        this.leg6 = this.root.getChild("leg6");
        this.cube_36 = this.leg6.getChild("cube_36");
        this.leg7 = this.leg6.getChild("leg7");
        this.cube_37 = this.leg7.getChild("cube_37");
        this.leg8 = this.leg7.getChild("leg8");
        this.cube_38 = this.leg8.getChild("cube_38");
        this.leg0 = this.root.getChild("leg0");
        this.cube_39 = this.leg0.getChild("cube_39");
        this.leg1 = this.leg0.getChild("leg1");
        this.cube_40 = this.leg1.getChild("cube_40");
        this.leg2 = this.leg1.getChild("leg2");
        this.cube_41 = this.leg2.getChild("cube_41");
        this.body0 = this.root.getChild("body0");
        this.cube_42 = this.body0.getChild("cube_42");
        this.cube_43 = this.body0.getChild("cube_43");
        this.cube_44 = this.body0.getChild("cube_44");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition wings3 = root.addOrReplaceChild("wings3", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, -12.0F, -2.0F, 0.0F, 0.0F, 0.6109F));
        PartDefinition cube_0 = wings3.addOrReplaceChild("cube_0", CubeListBuilder.create().texOffs(24, 0).addBox(3.0F, -15.0F, -2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 12.0F, 2.0F));
        PartDefinition cube_1 = wings3.addOrReplaceChild("cube_1", CubeListBuilder.create().texOffs(36, 11).addBox(3.0F, -13.0F, -1.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 12.0F, 2.0F));
        PartDefinition cube_2 = wings3.addOrReplaceChild("cube_2", CubeListBuilder.create().texOffs(21, 13).addBox(3.0F, -15.0F, -1.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 12.0F, 2.0F));
        PartDefinition wings4 = wings3.addOrReplaceChild("wings4", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.5236F));
        PartDefinition cube_3 = wings4.addOrReplaceChild("cube_3", CubeListBuilder.create().texOffs(24, 0).addBox(3.0F, -18.0F, -2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 15.0F, 2.0F));
        PartDefinition cube_4 = wings4.addOrReplaceChild("cube_4", CubeListBuilder.create().texOffs(19, 10).addBox(3.0F, -18.0F, -1.0F, 0.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 15.0F, 2.0F));
        PartDefinition cube_5 = wings4.addOrReplaceChild("cube_5", CubeListBuilder.create().texOffs(35, 10).addBox(3.0F, -16.0F, -1.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 15.0F, 2.0F));
        PartDefinition wings5 = wings4.addOrReplaceChild("wings5", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.6109F));
        PartDefinition cube_6 = wings5.addOrReplaceChild("cube_6", CubeListBuilder.create().texOffs(48, 48).addBox(3.0F, -25.0F, -2.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 18.0F, 2.0F));
        PartDefinition cube_7 = wings5.addOrReplaceChild("cube_7", CubeListBuilder.create().texOffs(18, 8).addBox(3.0F, -24.0F, -1.0F, 0.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 18.0F, 2.0F));
        PartDefinition cube_8 = wings5.addOrReplaceChild("cube_8", CubeListBuilder.create().texOffs(26, 31).addBox(3.0F, -25.0F, -1.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 18.0F, 2.0F));
        PartDefinition cube_9 = wings5.addOrReplaceChild("cube_9", CubeListBuilder.create().texOffs(26, 31).addBox(3.0F, -19.0F, -1.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 18.0F, 2.0F));
        PartDefinition leg3 = root.addOrReplaceChild("leg3", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0F, -7.5F, -3.5F, 0.0F, 0.0F, -0.7854F));
        PartDefinition cube_10 = leg3.addOrReplaceChild("cube_10", CubeListBuilder.create().texOffs(45, 25).addBox(4.0F, -10.5F, -4.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 7.5F, 3.5F));
        PartDefinition leg4 = leg3.addOrReplaceChild("leg4", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3491F));
        PartDefinition cube_11 = leg4.addOrReplaceChild("cube_11", CubeListBuilder.create().texOffs(45, 25).addBox(4.1F, -7.4F, -4.4F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 7.4F, 3.5F));
        PartDefinition leg5 = leg4.addOrReplaceChild("leg5", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.3491F));
        PartDefinition cube_12 = leg5.addOrReplaceChild("cube_12", CubeListBuilder.create().texOffs(45, 25).addBox(4.0F, -4.5F, -4.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 4.3F, 3.5F));
        PartDefinition head0 = root.addOrReplaceChild("head0", CubeListBuilder.create(), PartPose.offset(0.0F, -14.0F, -5.0F));
        PartDefinition cube_13 = head0.addOrReplaceChild("cube_13", CubeListBuilder.create().texOffs(0, 14).addBox(-3.0F, -17.0F, -9.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, 5.0F));
        PartDefinition cube_14 = head0.addOrReplaceChild("cube_14", CubeListBuilder.create().texOffs(30, 47).addBox(-2.0F, -14.0F, -11.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, 5.0F));
        PartDefinition cube_15 = head0.addOrReplaceChild("cube_15", CubeListBuilder.create().texOffs(13, 5).addBox(-2.0F, -3.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.9F, -2.0F, 0.0F, 0.1745F, -0.2618F, -0.4363F));
        PartDefinition cube_16 = head0.addOrReplaceChild("cube_16", CubeListBuilder.create().texOffs(13, 5).addBox(0.0F, -3.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.9F, -2.0F, 0.0F, 0.1745F, 0.2618F, 0.4363F));
        PartDefinition cube_17 = head0.addOrReplaceChild("cube_17", CubeListBuilder.create().texOffs(14, 25).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -3.0F, 1.0F, -0.6109F, 0.0F, 0.0F));
        PartDefinition cube_18 = head0.addOrReplaceChild("cube_18", CubeListBuilder.create().texOffs(14, 25).addBox(-0.5F, -18.5F, -5.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, 5.0F));
        PartDefinition cube_19 = head0.addOrReplaceChild("cube_19", CubeListBuilder.create().texOffs(14, 25).addBox(-0.5F, -18.0F, -7.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, 5.0F));
        PartDefinition wings0 = root.addOrReplaceChild("wings0", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0F, -12.0F, -2.0F, 0.0F, 0.0F, -0.6109F));
        PartDefinition cube_20 = wings0.addOrReplaceChild("cube_20", CubeListBuilder.create().texOffs(24, 0).addBox(-4.0F, -15.0F, -2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 12.0F, 2.0F));
        PartDefinition cube_21 = wings0.addOrReplaceChild("cube_21", CubeListBuilder.create().texOffs(36, 11).addBox(-4.0F, -13.0F, -1.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 12.0F, 2.0F));
        PartDefinition cube_22 = wings0.addOrReplaceChild("cube_22", CubeListBuilder.create().texOffs(21, 13).addBox(-3.0F, -15.0F, -1.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 12.0F, 2.0F));
        PartDefinition wings1 = wings0.addOrReplaceChild("wings1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.5236F));
        PartDefinition cube_23 = wings1.addOrReplaceChild("cube_23", CubeListBuilder.create().texOffs(24, 0).addBox(-4.0F, -18.0F, -2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 15.0F, 2.0F));
        PartDefinition cube_24 = wings1.addOrReplaceChild("cube_24", CubeListBuilder.create().texOffs(19, 10).addBox(-3.0F, -18.0F, -1.0F, 0.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 15.0F, 2.0F));
        PartDefinition cube_25 = wings1.addOrReplaceChild("cube_25", CubeListBuilder.create().texOffs(35, 10).addBox(-4.0F, -16.0F, -1.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 15.0F, 2.0F));
        PartDefinition wings2 = wings1.addOrReplaceChild("wings2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.6109F));
        PartDefinition cube_26 = wings2.addOrReplaceChild("cube_26", CubeListBuilder.create().texOffs(48, 48).addBox(-4.0F, -25.0F, -2.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 18.0F, 2.0F));
        PartDefinition cube_27 = wings2.addOrReplaceChild("cube_27", CubeListBuilder.create().texOffs(18, 8).addBox(-3.0F, -24.0F, -1.0F, 0.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 18.0F, 2.0F));
        PartDefinition cube_28 = wings2.addOrReplaceChild("cube_28", CubeListBuilder.create().texOffs(26, 31).addBox(-4.0F, -25.0F, -1.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 18.0F, 2.0F));
        PartDefinition cube_29 = wings2.addOrReplaceChild("cube_29", CubeListBuilder.create().texOffs(26, 31).addBox(-4.0F, -19.0F, -1.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 18.0F, 2.0F));
        PartDefinition tail0 = root.addOrReplaceChild("tail0", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, -11.5F, 6.0F, -0.6109F, 0.0F, 0.0F));
        PartDefinition cube_30 = tail0.addOrReplaceChild("cube_30", CubeListBuilder.create().texOffs(24, 0).addBox(-1.5F, -11.5F, 6.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 11.5F, -6.0F));
        PartDefinition tail1 = tail0.addOrReplaceChild("tail1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, -0.3491F, 0.0F, 0.0F));
        PartDefinition cube_31 = tail1.addOrReplaceChild("cube_31", CubeListBuilder.create().texOffs(34, 9).addBox(-1.2F, -11.7F, 9.8F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 11.5F, -9.0F));
        PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.5F, 3.0F, -0.3491F, 0.0F, 0.0F));
        PartDefinition cube_32 = tail2.addOrReplaceChild("cube_32", CubeListBuilder.create().texOffs(35, 10).addBox(-0.7F, -12.2F, 14.8F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 12.0F, -12.0F));
        PartDefinition leg9 = root.addOrReplaceChild("leg9", CubeListBuilder.create(), PartPose.offsetAndRotation(2.5F, -8.5F, 4.5F, 0.0F, 0.0F, -0.5236F));
        PartDefinition cube_33 = leg9.addOrReplaceChild("cube_33", CubeListBuilder.create().texOffs(45, 25).addBox(1.5F, -10.0F, 2.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 8.5F, -4.5F));
        PartDefinition leg10 = leg9.addOrReplaceChild("leg10", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0F, 1.5F, 0.5F, 0.0F, 0.0F, 0.2618F));
        PartDefinition cube_34 = leg10.addOrReplaceChild("cube_34", CubeListBuilder.create().texOffs(45, 25).addBox(1.6F, -6.9F, 2.6F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.5F, 6.9F, -5.0F));
        PartDefinition leg11 = leg10.addOrReplaceChild("leg11", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 3.0F, -0.6F, 0.0F, 0.0F, 0.2618F));
        PartDefinition cube_35 = leg11.addOrReplaceChild("cube_35", CubeListBuilder.create().texOffs(16, 36).addBox(1.5F, -4.0F, 1.5F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.5F, 3.8F, -4.4F));
        PartDefinition leg6 = root.addOrReplaceChild("leg6", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.5F, -8.5F, 4.5F, 0.0F, 0.0F, 0.5236F));
        PartDefinition cube_36 = leg6.addOrReplaceChild("cube_36", CubeListBuilder.create().texOffs(45, 25).addBox(-4.5F, -10.0F, 2.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 8.5F, -4.5F));
        PartDefinition leg7 = leg6.addOrReplaceChild("leg7", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, 1.5F, 0.5F, 0.0F, 0.0F, -0.2618F));
        PartDefinition cube_37 = leg7.addOrReplaceChild("cube_37", CubeListBuilder.create().texOffs(45, 25).addBox(-4.4F, -6.9F, 2.6F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, 6.9F, -5.0F));
        PartDefinition leg8 = leg7.addOrReplaceChild("leg8", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 3.0F, -0.6F, 0.0F, 0.0F, -0.2618F));
        PartDefinition cube_38 = leg8.addOrReplaceChild("cube_38", CubeListBuilder.create().texOffs(16, 36).addBox(-4.5F, -4.0F, 1.5F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, 3.8F, -4.4F));
        PartDefinition leg0 = root.addOrReplaceChild("leg0", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, -7.5F, -3.5F, 0.0F, 0.0F, 0.7854F));
        PartDefinition cube_39 = leg0.addOrReplaceChild("cube_39", CubeListBuilder.create().texOffs(45, 25).addBox(-7.0F, -10.5F, -4.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 7.5F, 3.5F));
        PartDefinition leg1 = leg0.addOrReplaceChild("leg1", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3491F));
        PartDefinition cube_40 = leg1.addOrReplaceChild("cube_40", CubeListBuilder.create().texOffs(45, 25).addBox(-6.9F, -7.4F, -4.4F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 7.4F, 3.5F));
        PartDefinition leg2 = leg1.addOrReplaceChild("leg2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.3491F));
        PartDefinition cube_41 = leg2.addOrReplaceChild("cube_41", CubeListBuilder.create().texOffs(45, 25).addBox(-7.0F, -4.5F, -4.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 4.3F, 3.5F));
        PartDefinition body0 = root.addOrReplaceChild("body0", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition cube_42 = body0.addOrReplaceChild("cube_42", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -12.0F, -5.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition cube_43 = body0.addOrReplaceChild("cube_43", CubeListBuilder.create().texOffs(0, 28).addBox(-3.5F, -11.5F, 3.0F, 7.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition cube_44 = body0.addOrReplaceChild("cube_44", CubeListBuilder.create().texOffs(19, 20).addBox(-1.0F, -6.0F, 0.0F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -7.0F, -5.0F, 0.4363F, 0.0F, 0.0F));

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
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    public void setAngles(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

}

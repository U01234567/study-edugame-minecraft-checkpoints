// Made from GLTF source for the blockbench creature pipeline
// Source creature: jailer
// Texture pipeline: run texture_fix.py to build ./model.png from ./textures/gltf_embedded_0.png

package com.example.mod;

public class model extends EntityModel<Entity> {
    private final ModelPart root;

    public model(ModelPart root) {
        this.root = root.getChild("root");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        ModelPartData node_166 = root.addChild("node_166", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData jailer = node_166.addChild("jailer", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData body = jailer.addChild("body", ModelPartBuilder.create(), ModelTransform.of(0.0F, -24.0F, 0.0F, -0.0873F, 0.0F, -0.0F));
        ModelPartData cube = body.addChild("cube", ModelPartBuilder.create().uv(0, 0).cuboid(-27.0F, -40.0F, -8.0F, 54.0F, 16.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        ModelPartData back_ribs = body.addChild("back_ribs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -8.0F, 8.0F));
        ModelPartData backrib1 = back_ribs.addChild("backrib1", ModelPartBuilder.create(), ModelTransform.of(-21.0F, 0.0F, 0.0F, 0.2618F, -0.5236F, 0.0F));
        ModelPartData cube_2 = backrib1.addChild("cube_2", ModelPartBuilder.create().uv(142, 0).cuboid(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData backribend1 = backrib1.addChild("backribend1", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 12.0F, 0.2618F, -1.0472F, -0.0F));
        ModelPartData cube_3 = backribend1.addChild("cube_3", ModelPartBuilder.create().uv(176, 0).cuboid(-2.0F, -1.7412F, 11.9659F, 4.0F, 4.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.2588F, -11.9659F));
        ModelPartData backrib2 = back_ribs.addChild("backrib2", ModelPartBuilder.create(), ModelTransform.of(-13.0F, 0.0F, 0.0F, 0.1745F, -0.2618F, 0.0F));
        ModelPartData cube_4 = backrib2.addChild("cube_4", ModelPartBuilder.create().uv(210, 0).cuboid(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData backribend2 = backrib2.addChild("backribend2", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 12.0F, 0.1745F, -0.4363F, 0.0F));
        ModelPartData cube_5 = backribend2.addChild("cube_5", ModelPartBuilder.create().uv(244, 0).cuboid(-2.0F, -1.7412F, 11.9659F, 4.0F, 4.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.2588F, -11.9659F));
        ModelPartData backrib3 = back_ribs.addChild("backrib3", ModelPartBuilder.create(), ModelTransform.of(-5.0F, 0.0F, 0.0F, 0.0873F, -0.0873F, 0.0F));
        ModelPartData cube_6 = backrib3.addChild("cube_6", ModelPartBuilder.create().uv(278, 0).cuboid(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData backribend3 = backrib3.addChild("backribend3", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 12.0F, 0.0873F, -0.1745F, -0.0F));
        ModelPartData cube_7 = backribend3.addChild("cube_7", ModelPartBuilder.create().uv(312, 0).cuboid(-2.0F, -1.7412F, 11.9659F, 4.0F, 4.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.2588F, -11.9659F));
        ModelPartData backrib4 = back_ribs.addChild("backrib4", ModelPartBuilder.create(), ModelTransform.of(5.0F, 0.0F, 0.0F, 0.0873F, 0.0873F, 0.0F));
        ModelPartData cube_8 = backrib4.addChild("cube_8", ModelPartBuilder.create().uv(346, 0).cuboid(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData backribend4 = backrib4.addChild("backribend4", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 12.0F, 0.0873F, 0.1745F, 0.0F));
        ModelPartData cube_9 = backribend4.addChild("cube_9", ModelPartBuilder.create().uv(380, 0).cuboid(-2.0F, -1.7412F, 11.9659F, 4.0F, 4.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.2588F, -11.9659F));
        ModelPartData backrib5 = back_ribs.addChild("backrib5", ModelPartBuilder.create(), ModelTransform.of(13.0F, 0.0F, 0.0F, 0.1745F, 0.2618F, 0.0F));
        ModelPartData cube_10 = backrib5.addChild("cube_10", ModelPartBuilder.create().uv(414, 0).cuboid(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData backribend5 = backrib5.addChild("backribend5", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 12.0F, 0.1745F, 0.4363F, 0.0F));
        ModelPartData cube_11 = backribend5.addChild("cube_11", ModelPartBuilder.create().uv(448, 0).cuboid(-2.0F, -1.7412F, 11.9659F, 4.0F, 4.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.2588F, -11.9659F));
        ModelPartData backrib6 = back_ribs.addChild("backrib6", ModelPartBuilder.create(), ModelTransform.of(21.0F, 0.0F, 0.0F, 0.2618F, 0.5236F, -0.0F));
        ModelPartData cube_12 = backrib6.addChild("cube_12", ModelPartBuilder.create().uv(0, 34).cuboid(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData backribend6 = backrib6.addChild("backribend6", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 12.0F, 0.2618F, 1.0472F, 0.0F));
        ModelPartData cube_13 = backribend6.addChild("cube_13", ModelPartBuilder.create().uv(34, 34).cuboid(-2.0F, -1.7412F, 11.9659F, 4.0F, 4.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.2588F, -11.9659F));
        ModelPartData highbody = body.addChild("highbody", ModelPartBuilder.create(), ModelTransform.of(0.0F, -16.0F, 0.0F, 0.2618F, 0.0F, 0.0F));
        ModelPartData cube_14 = highbody.addChild("cube_14", ModelPartBuilder.create().uv(68, 34).cuboid(-27.0F, -72.0F, -8.0F, 54.0F, 32.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 40.0F, 0.0F));
        ModelPartData head = highbody.addChild("head", ModelPartBuilder.create(), ModelTransform.of(0.0F, -32.0F, 0.0F, 0.1745F, 0.0F, 0.0F));
        ModelPartData cube_15 = head.addChild("cube_15", ModelPartBuilder.create().uv(210, 34).cuboid(-16.0F, -96.0F, -9.0F, 32.0F, 24.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 72.0F, 0.0F));
        ModelPartData leftflop = head.addChild("leftflop", ModelPartBuilder.create(), ModelTransform.of(-1.0F, -24.0F, 0.0F, -0.0F, 0.0F, -0.1745F));
        ModelPartData cube_16 = leftflop.addChild("cube_16", ModelPartBuilder.create().uv(312, 34).cuboid(-15.0F, -102.0F, -7.0F, 14.0F, 10.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, 96.0F, 0.0F));
        ModelPartData leftflopper = leftflop.addChild("leftflopper", ModelPartBuilder.create(), ModelTransform.of(-1.0F, -6.0F, 0.0F, -0.0F, 0.0F, -0.1745F));
        ModelPartData cube_17 = leftflopper.addChild("cube_17", ModelPartBuilder.create().uv(370, 34).cuboid(-15.0F, -102.0F, -7.0F, 12.0F, 10.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, 98.0F, 1.0F));
        ModelPartData leftfloppie = leftflopper.addChild("leftfloppie", ModelPartBuilder.create(), ModelTransform.of(-1.0F, -4.0F, 0.0F, -0.0F, 0.0F, -0.1745F));
        ModelPartData cube_18 = leftfloppie.addChild("cube_18", ModelPartBuilder.create().uv(420, 34).cuboid(-15.1736F, -98.0152F, -7.0F, 10.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(5.1736F, 95.0152F, 2.0F));
        ModelPartData leftsculkie = leftfloppie.addChild("leftsculkie", ModelPartBuilder.create(), ModelTransform.of(-5.0F, -3.0F, 0.0F, -0.0F, 0.7854F, -0.0F));
        ModelPartData cube_19 = leftsculkie.addChild("cube_19", ModelPartBuilder.create().uv(462, 34).cuboid(-4.0F, -8.0F, 0.0F, 8.0F, 8.0F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData cube_20 = leftsculkie.addChild("cube_20", ModelPartBuilder.create().uv(482, 34).cuboid(0.0F, -8.0F, -4.0F, 0.001F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData rightflop = head.addChild("rightflop", ModelPartBuilder.create(), ModelTransform.of(1.0F, -24.0F, 0.0F, 0.0F, 0.0F, 0.1745F));
        ModelPartData cube_21 = rightflop.addChild("cube_21", ModelPartBuilder.create().uv(0, 84).cuboid(-15.0F, -102.0F, -7.0F, 14.0F, 10.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(15.0F, 96.0F, 0.0F));
        ModelPartData rightflopper = rightflop.addChild("rightflopper", ModelPartBuilder.create(), ModelTransform.of(1.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.1745F));
        ModelPartData cube_22 = rightflopper.addChild("cube_22", ModelPartBuilder.create().uv(58, 84).cuboid(-15.0F, -102.0F, -7.0F, 12.0F, 10.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(15.0F, 98.0F, 1.0F));
        ModelPartData rightfloppie = rightflopper.addChild("rightfloppie", ModelPartBuilder.create(), ModelTransform.of(1.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.1745F));
        ModelPartData cube_23 = rightfloppie.addChild("cube_23", ModelPartBuilder.create().uv(108, 84).cuboid(-15.1736F, -98.0152F, -7.0F, 10.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(15.1736F, 95.0152F, 2.0F));
        ModelPartData rightsculkie = rightfloppie.addChild("rightsculkie", ModelPartBuilder.create(), ModelTransform.of(5.0F, -3.0F, 0.0F, -0.0F, 0.7854F, -0.0F));
        ModelPartData cube_24 = rightsculkie.addChild("cube_24", ModelPartBuilder.create().uv(150, 84).cuboid(-4.0F, -8.0F, 0.0F, 8.0F, 8.0F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData cube_25 = rightsculkie.addChild("cube_25", ModelPartBuilder.create().uv(170, 84).cuboid(0.0F, -8.0F, -4.0F, 0.001F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData bones = head.addChild("bones", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -14.0F, 13.0F));
        ModelPartData leftheadbone = bones.addChild("leftheadbone", ModelPartBuilder.create(), ModelTransform.pivot(-4.0F, 4.0F, -4.0F));
        ModelPartData cube_26 = leftheadbone.addChild("cube_26", ModelPartBuilder.create().uv(190, 84).cuboid(2.0F, -83.0F, 7.0F, 3.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, 82.0F, -9.0F));
        ModelPartData cube_27 = leftheadbone.addChild("cube_27", ModelPartBuilder.create().uv(210, 84).cuboid(-2.0F, -0.0F, -1.0F, 3.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 5.0F, -0.6109F, 0.0F, 0.1745F));
        ModelPartData cube_28 = leftheadbone.addChild("cube_28", ModelPartBuilder.create().uv(230, 84).cuboid(-2.0F, -1.0F, 0.0F, 3.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 4.0F, 8.0F, -0.7854F, -0.3491F, 0.3491F));
        ModelPartData rightheadbone = bones.addChild("rightheadbone", ModelPartBuilder.create(), ModelTransform.pivot(5.0F, 4.0F, -4.0F));
        ModelPartData cube_29 = rightheadbone.addChild("cube_29", ModelPartBuilder.create().uv(250, 84).cuboid(2.0F, -83.0F, 7.0F, 3.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, 82.0F, -9.0F));
        ModelPartData cube_30 = rightheadbone.addChild("cube_30", ModelPartBuilder.create().uv(270, 84).cuboid(-1.0F, -0.0F, -1.0F, 3.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 0.0F, 5.0F, -0.6109F, 0.0F, -0.1745F));
        ModelPartData cube_31 = rightheadbone.addChild("cube_31", ModelPartBuilder.create().uv(290, 84).cuboid(-1.0F, -1.0F, 0.0F, 3.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.0F, 8.0F, -0.7854F, 0.3491F, -0.3491F));
        ModelPartData headbonering = bones.addChild("headbonering", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -3.0F, -4.0F));
        ModelPartData cube_32 = headbonering.addChild("cube_32", ModelPartBuilder.create().uv(310, 84).cuboid(8.0F, -91.0F, 9.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 89.0F, -9.0F));
        ModelPartData cube_33 = headbonering.addChild("cube_33", ModelPartBuilder.create().uv(326, 84).cuboid(-11.0F, -91.0F, 9.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 89.0F, -9.0F));
        ModelPartData cube_34 = headbonering.addChild("cube_34", ModelPartBuilder.create().uv(342, 84).cuboid(-1.0F, -2.0F, -1.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(9.0F, 0.0F, 4.0F, -0.1745F, -0.7854F, -0.0F));
        ModelPartData cube_35 = headbonering.addChild("cube_35", ModelPartBuilder.create().uv(358, 84).cuboid(-2.0F, -2.0F, -1.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-9.0F, 0.0F, 4.0F, -0.1745F, 0.7854F, 0.0F));
        ModelPartData cube_36 = headbonering.addChild("cube_36", ModelPartBuilder.create().uv(374, 84).cuboid(1.0F, -2.0F, 1.0F, 3.0F, 3.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(9.0F, 0.0F, 4.0F, -1.5708F, -1.1345F, 1.5708F));
        ModelPartData mouth = head.addChild("mouth", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -9.0F));
        ModelPartData cube_37 = mouth.addChild("cube_37", ModelPartBuilder.create().uv(414, 84).cuboid(-12.0F, -88.0F, -13.0F, 24.0F, 18.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 74.0F, 9.0F));
        ModelPartData ears = head.addChild("ears", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -17.0F, 0.0F));
        ModelPartData leftear = ears.addChild("leftear", ModelPartBuilder.create(), ModelTransform.pivot(-16.0F, 0.0F, 0.0F));
        ModelPartData cube_38 = leftear.addChild("cube_38", ModelPartBuilder.create().uv(0, 112).cuboid(-1.0F, -10.0F, -1.0F, 6.0F, 10.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.0F, 5.0F, -6.0F));
        ModelPartData rightear = ears.addChild("rightear", ModelPartBuilder.create(), ModelTransform.pivot(16.0F, 0.0F, 0.0F));
        ModelPartData cube_39 = rightear.addChild("cube_39", ModelPartBuilder.create().uv(42, 112).cuboid(-1.0F, -10.0F, -1.0F, 6.0F, 10.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, 5.0F, -6.0F));
        ModelPartData ribs = highbody.addChild("ribs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -10.0F, -8.0F));
        ModelPartData rib1 = ribs.addChild("rib1", ModelPartBuilder.create(), ModelTransform.of(23.0F, -11.0F, 0.0F, 0.1745F, 0.0F, -0.1745F));
        ModelPartData cube_40 = rib1.addChild("cube_40", ModelPartBuilder.create().uv(84, 112).cuboid(17.0F, -62.0F, -18.0F, 6.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(-20.0F, 59.0F, 8.0F));
        ModelPartData ribpart1 = rib1.addChild("ribpart1", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, -10.0F, 0.4363F, 0.0F, 0.0F));
        ModelPartData cube_41 = ribpart1.addChild("cube_41", ModelPartBuilder.create().uv(118, 112).cuboid(-3.0F, -3.0F, -10.0F, 6.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData ribpartend1 = ribpart1.addChild("ribpartend1", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -10.0F));
        ModelPartData cube_42 = ribpartend1.addChild("cube_42", ModelPartBuilder.create().uv(152, 112).cuboid(-3.0F, -3.0F, -8.0F, 6.0F, 6.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
        ModelPartData rib2 = ribs.addChild("rib2", ModelPartBuilder.create(), ModelTransform.of(17.0F, 0.0F, 0.0F, -0.3491F, 0.0F, -2.9671F));
        ModelPartData cube_43 = rib2.addChild("cube_43", ModelPartBuilder.create().uv(182, 112).cuboid(17.0F, -62.0F, -18.0F, 6.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(-20.0F, 59.0F, 8.0F));
        ModelPartData ribpart2 = rib2.addChild("ribpart2", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, -10.0F, 0.4363F, 0.0F, 0.0F));
        ModelPartData cube_44 = ribpart2.addChild("cube_44", ModelPartBuilder.create().uv(216, 112).cuboid(-3.0F, -3.0F, -10.0F, 6.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData ribpartend2 = ribpart2.addChild("ribpartend2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -10.0F));
        ModelPartData cube_45 = ribpartend2.addChild("cube_45", ModelPartBuilder.create().uv(250, 112).cuboid(-3.0F, -3.0F, -8.0F, 6.0F, 6.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
        ModelPartData rib3 = ribs.addChild("rib3", ModelPartBuilder.create(), ModelTransform.of(11.0F, -11.0F, 0.0F, 0.5236F, 0.0F, -0.1745F));
        ModelPartData cube_46 = rib3.addChild("cube_46", ModelPartBuilder.create().uv(280, 112).cuboid(17.0F, -62.0F, -18.0F, 6.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(-20.0F, 59.0F, 8.0F));
        ModelPartData ribpart3 = rib3.addChild("ribpart3", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, -10.0F, 0.4363F, 0.0F, 0.0F));
        ModelPartData cube_47 = ribpart3.addChild("cube_47", ModelPartBuilder.create().uv(314, 112).cuboid(-3.0F, -3.0F, -10.0F, 6.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData ribpartend3 = ribpart3.addChild("ribpartend3", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -10.0F));
        ModelPartData cube_48 = ribpartend3.addChild("cube_48", ModelPartBuilder.create().uv(348, 112).cuboid(-3.0F, -3.0F, -8.0F, 6.0F, 6.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
        ModelPartData rib4 = ribs.addChild("rib4", ModelPartBuilder.create(), ModelTransform.of(5.0F, 0.0F, 0.0F, -0.6981F, 0.0F, -2.9671F));
        ModelPartData cube_49 = rib4.addChild("cube_49", ModelPartBuilder.create().uv(378, 112).cuboid(17.0F, -62.0F, -18.0F, 6.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(-20.0F, 59.0F, 8.0F));
        ModelPartData ribpart4 = rib4.addChild("ribpart4", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, -10.0F, 0.4363F, 0.0F, 0.0F));
        ModelPartData cube_50 = ribpart4.addChild("cube_50", ModelPartBuilder.create().uv(412, 112).cuboid(-3.0F, -3.0F, -10.0F, 6.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData ribpartend4 = ribpart4.addChild("ribpartend4", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -10.0F));
        ModelPartData cube_51 = ribpartend4.addChild("cube_51", ModelPartBuilder.create().uv(446, 112).cuboid(-3.0F, -3.0F, -8.0F, 6.0F, 6.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
        ModelPartData rib5 = ribs.addChild("rib5", ModelPartBuilder.create(), ModelTransform.of(-5.0F, 0.0F, 0.0F, -0.6981F, 0.0F, 2.9671F));
        ModelPartData cube_52 = rib5.addChild("cube_52", ModelPartBuilder.create().uv(476, 112).cuboid(17.0F, -62.0F, -18.0F, 6.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(-20.0F, 59.0F, 8.0F));
        ModelPartData ribpart5 = rib5.addChild("ribpart5", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, -10.0F, 0.4363F, 0.0F, 0.0F));
        ModelPartData cube_53 = ribpart5.addChild("cube_53", ModelPartBuilder.create().uv(0, 138).cuboid(-3.0F, -3.0F, -10.0F, 6.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData ribpartend5 = ribpart5.addChild("ribpartend5", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -10.0F));
        ModelPartData cube_54 = ribpartend5.addChild("cube_54", ModelPartBuilder.create().uv(34, 138).cuboid(-3.0F, -3.0F, -8.0F, 6.0F, 6.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
        ModelPartData rib6 = ribs.addChild("rib6", ModelPartBuilder.create(), ModelTransform.of(-11.0F, -11.0F, 0.0F, 0.5236F, 0.0F, 0.1745F));
        ModelPartData cube_55 = rib6.addChild("cube_55", ModelPartBuilder.create().uv(64, 138).cuboid(17.0F, -62.0F, -18.0F, 6.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(-20.0F, 59.0F, 8.0F));
        ModelPartData ribpart6 = rib6.addChild("ribpart6", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, -10.0F, 0.4363F, 0.0F, 0.0F));
        ModelPartData cube_56 = ribpart6.addChild("cube_56", ModelPartBuilder.create().uv(98, 138).cuboid(-3.0F, -3.0F, -10.0F, 6.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData ribpartend6 = ribpart6.addChild("ribpartend6", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -10.0F));
        ModelPartData cube_57 = ribpartend6.addChild("cube_57", ModelPartBuilder.create().uv(132, 138).cuboid(-3.0F, -3.0F, -8.0F, 6.0F, 6.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
        ModelPartData rib7 = ribs.addChild("rib7", ModelPartBuilder.create(), ModelTransform.of(-17.0F, 0.0F, 0.0F, -0.3491F, 0.0F, 2.9671F));
        ModelPartData cube_58 = rib7.addChild("cube_58", ModelPartBuilder.create().uv(162, 138).cuboid(17.0F, -62.0F, -18.0F, 6.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(-20.0F, 59.0F, 8.0F));
        ModelPartData ribpart7 = rib7.addChild("ribpart7", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, -10.0F, 0.4363F, 0.0F, 0.0F));
        ModelPartData cube_59 = ribpart7.addChild("cube_59", ModelPartBuilder.create().uv(196, 138).cuboid(-3.0F, -3.0F, -10.0F, 6.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData ribpartend7 = ribpart7.addChild("ribpartend7", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -10.0F));
        ModelPartData cube_60 = ribpartend7.addChild("cube_60", ModelPartBuilder.create().uv(230, 138).cuboid(-3.0F, -3.0F, -8.0F, 6.0F, 6.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
        ModelPartData rib8 = ribs.addChild("rib8", ModelPartBuilder.create(), ModelTransform.of(-23.0F, -11.0F, 0.0F, 0.1745F, 0.0F, 0.1745F));
        ModelPartData cube_61 = rib8.addChild("cube_61", ModelPartBuilder.create().uv(260, 138).cuboid(17.0F, -62.0F, -18.0F, 6.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(-20.0F, 59.0F, 8.0F));
        ModelPartData ribpart8 = rib8.addChild("ribpart8", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, -10.0F, 0.4363F, 0.0F, 0.0F));
        ModelPartData cube_62 = ribpart8.addChild("cube_62", ModelPartBuilder.create().uv(294, 138).cuboid(-3.0F, -3.0F, -10.0F, 6.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData ribpartend8 = ribpart8.addChild("ribpartend8", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -10.0F));
        ModelPartData cube_63 = ribpartend8.addChild("cube_63", ModelPartBuilder.create().uv(328, 138).cuboid(-3.0F, -3.0F, -8.0F, 6.0F, 6.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.6981F, 0.0F, 0.0F));
        ModelPartData hands = highbody.addChild("hands", ModelPartBuilder.create(), ModelTransform.of(0.0F, -26.0F, 0.0F, -0.0873F, 0.0F, -0.0F));
        ModelPartData lefthand = hands.addChild("lefthand", ModelPartBuilder.create(), ModelTransform.of(-27.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));
        ModelPartData cube_64 = lefthand.addChild("cube_64", ModelPartBuilder.create().uv(358, 138).cuboid(0.0F, -72.0F, -6.0F, 12.0F, 56.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(-12.0F, 66.0F, 0.0F));
        ModelPartData leftplantything = lefthand.addChild("leftplantything", ModelPartBuilder.create(), ModelTransform.of(-6.0F, -6.0F, 0.0F, 0.0F, -0.7854F, 0.0F));
        ModelPartData cube_65 = leftplantything.addChild("cube_65", ModelPartBuilder.create().uv(408, 138).cuboid(0.0F, -84.0F, 0.0F, 12.0F, 12.0F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(-6.0F, 72.0F, 0.0F));
        ModelPartData cube_66 = leftplantything.addChild("cube_66", ModelPartBuilder.create().uv(436, 138).cuboid(6.0F, -84.0F, -6.0F, 0.001F, 12.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(-6.0F, 72.0F, 0.0F));
        ModelPartData righthand = hands.addChild("righthand", ModelPartBuilder.create(), ModelTransform.of(27.0F, 0.0F, 0.0F, -0.0F, 0.0F, -0.0873F));
        ModelPartData cube_67 = righthand.addChild("cube_67", ModelPartBuilder.create().uv(464, 138).cuboid(0.0F, -72.0F, -6.0F, 12.0F, 56.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 66.0F, 0.0F));
        ModelPartData rightplantything = righthand.addChild("rightplantything", ModelPartBuilder.create(), ModelTransform.of(6.0F, -6.0F, 0.0F, 0.0F, -0.7854F, 0.0F));
        ModelPartData cube_68 = rightplantything.addChild("cube_68", ModelPartBuilder.create().uv(0, 208).cuboid(0.0F, -84.0F, 0.0F, 12.0F, 12.0F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(-6.0F, 72.0F, 0.0F));
        ModelPartData cube_69 = rightplantything.addChild("cube_69", ModelPartBuilder.create().uv(28, 208).cuboid(6.0F, -84.0F, -6.0F, 0.001F, 12.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(-6.0F, 72.0F, 0.0F));
        ModelPartData feet = jailer.addChild("feet", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -24.0F, 0.0F));
        ModelPartData leftfoot = feet.addChild("leftfoot", ModelPartBuilder.create(), ModelTransform.pivot(-11.0F, 0.0F, 0.0F));
        ModelPartData cube_70 = leftfoot.addChild("cube_70", ModelPartBuilder.create().uv(56, 208).cuboid(-17.0F, -24.0F, -6.0F, 12.0F, 24.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(11.0F, 24.0F, 0.0F));
        ModelPartData leftfootbone = leftfoot.addChild("leftfootbone", ModelPartBuilder.create(), ModelTransform.of(0.0F, 14.0F, 6.0F, 0.5236F, 0.0F, 0.0F));
        ModelPartData cube_71 = leftfootbone.addChild("cube_71", ModelPartBuilder.create().uv(106, 208).cuboid(-13.0F, -12.0F, 6.0F, 4.0F, 4.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(11.0F, 10.0F, -6.0F));
        ModelPartData leftfootendbone = leftfootbone.addChild("leftfootendbone", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 8.0F, 0.3491F, 0.0F, 0.0F));
        ModelPartData cube_72 = leftfootendbone.addChild("cube_72", ModelPartBuilder.create().uv(132, 208).cuboid(-13.0F, -12.0F, 6.0F, 4.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(11.0F, 10.0F, -6.0F));
        ModelPartData rightfoot = feet.addChild("rightfoot", ModelPartBuilder.create(), ModelTransform.pivot(11.0F, 0.0F, 0.0F));
        ModelPartData cube_73 = rightfoot.addChild("cube_73", ModelPartBuilder.create().uv(154, 208).cuboid(5.0F, -24.0F, -6.0F, 12.0F, 24.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(-11.0F, 24.0F, 0.0F));
        ModelPartData rightfootbone = rightfoot.addChild("rightfootbone", ModelPartBuilder.create(), ModelTransform.of(0.0F, 14.0F, 6.0F, 0.5236F, 0.0F, 0.0F));
        ModelPartData cube_74 = rightfootbone.addChild("cube_74", ModelPartBuilder.create().uv(204, 208).cuboid(-13.0F, -12.0F, 6.0F, 4.0F, 4.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(11.0F, 10.0F, -6.0F));
        ModelPartData rightfootendbone = rightfootbone.addChild("rightfootendbone", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 8.0F, 0.3491F, 0.0F, 0.0F));
        ModelPartData cube_75 = rightfootendbone.addChild("cube_75", ModelPartBuilder.create().uv(230, 208).cuboid(-13.0F, -12.0F, 6.0F, 4.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(11.0F, 10.0F, -6.0F));
        ModelPartData flies = jailer.addChild("flies", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -0.0F, 0.0F));
        ModelPartData fly1 = flies.addChild("fly1", ModelPartBuilder.create(), ModelTransform.pivot(20.0F, -55.0F, -35.0F));
        ModelPartData cube_76 = fly1.addChild("cube_76", ModelPartBuilder.create().uv(252, 208).cuboid(-1.0F, -1.0F, -28.0F, 2.0F, 1.0F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 28.0F));
        ModelPartData fly2 = flies.addChild("fly2", ModelPartBuilder.create(), ModelTransform.pivot(-26.0F, -31.0F, -35.0F));
        ModelPartData cube_77 = fly2.addChild("cube_77", ModelPartBuilder.create().uv(260, 208).cuboid(-1.0F, -1.0F, -28.0F, 2.0F, 1.0F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 28.0F));
        ModelPartData fly3 = flies.addChild("fly3", ModelPartBuilder.create(), ModelTransform.pivot(-52.0F, -31.0F, -7.0F));
        ModelPartData cube_78 = fly3.addChild("cube_78", ModelPartBuilder.create().uv(268, 208).cuboid(-1.0F, -1.0F, -28.0F, 2.0F, 1.0F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 28.0F));
        ModelPartData fly4 = flies.addChild("fly4", ModelPartBuilder.create(), ModelTransform.pivot(-39.0F, -70.0F, 21.0F));
        ModelPartData cube_79 = fly4.addChild("cube_79", ModelPartBuilder.create().uv(276, 208).cuboid(-1.0F, -1.0F, -28.0F, 2.0F, 1.0F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 28.0F));
        ModelPartData fly5 = flies.addChild("fly5", ModelPartBuilder.create(), ModelTransform.pivot(-27.0F, -104.0F, -5.0F));
        ModelPartData cube_80 = fly5.addChild("cube_80", ModelPartBuilder.create().uv(284, 208).cuboid(-1.0F, -1.0F, -28.0F, 2.0F, 1.0F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 28.0F));
        ModelPartData fly6 = flies.addChild("fly6", ModelPartBuilder.create(), ModelTransform.pivot(7.0F, -52.0F, 33.0F));
        ModelPartData cube_81 = fly6.addChild("cube_81", ModelPartBuilder.create().uv(292, 208).cuboid(-1.0F, -1.0F, -28.0F, 2.0F, 1.0F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 28.0F));
        ModelPartData fly7 = flies.addChild("fly7", ModelPartBuilder.create(), ModelTransform.pivot(31.0F, -21.0F, 13.0F));
        ModelPartData cube_82 = fly7.addChild("cube_82", ModelPartBuilder.create().uv(300, 208).cuboid(-1.0F, -1.0F, -28.0F, 2.0F, 1.0F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 28.0F));
        ModelPartData fly8 = flies.addChild("fly8", ModelPartBuilder.create(), ModelTransform.pivot(52.0F, -61.0F, 15.0F));
        ModelPartData cube_83 = fly8.addChild("cube_83", ModelPartBuilder.create().uv(308, 208).cuboid(-1.0F, -1.0F, -28.0F, 2.0F, 1.0F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 28.0F));
        ModelPartData fly9 = flies.addChild("fly9", ModelPartBuilder.create(), ModelTransform.pivot(67.0F, -38.0F, -5.0F));
        ModelPartData cube_84 = fly9.addChild("cube_84", ModelPartBuilder.create().uv(316, 208).cuboid(-1.0F, -1.0F, -28.0F, 2.0F, 1.0F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 28.0F));
        ModelPartData fly10 = flies.addChild("fly10", ModelPartBuilder.create(), ModelTransform.pivot(6.0F, -9.0F, -20.0F));
        ModelPartData cube_85 = fly10.addChild("cube_85", ModelPartBuilder.create().uv(324, 208).cuboid(-1.0F, -1.0F, -28.0F, 2.0F, 1.0F, 0.001F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 28.0F));

        return TexturedModelData.of(modelData, 512, 256);
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        // Intentionally left blank. Pose helpers live in model-anim.java.
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
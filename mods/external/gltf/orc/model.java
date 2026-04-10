// Made from GLTF source for the blockbench creature pipeline
// Source creature: orc
// Texture pipeline: use texture_fix.py to build ./model.png from ./textures/gltf_embedded_0.png

package com.example.mod;
   
public class orc extends EntityModel<Entity> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart cube;
    private final ModelPart cube_2;
    private final ModelPart cube_3;
    private final ModelPart cube_4;
    private final ModelPart mouth;
    private final ModelPart cube_5;
    private final ModelPart cube_6;
    private final ModelPart cube_7;
    private final ModelPart chest;
    private final ModelPart chest_top;
    private final ModelPart cube_8;
    private final ModelPart chest_bottom;
    private final ModelPart cube_9;
    private final ModelPart cube_10;
    private final ModelPart cube_11;
    private final ModelPart cube_12;
    private final ModelPart cube_13;
    private final ModelPart right_arm;
    private final ModelPart cube_14;
    private final ModelPart cube_15;
    private final ModelPart cube_16;
    private final ModelPart cube_17;
    private final ModelPart left_arm;
    private final ModelPart cube_18;
    private final ModelPart cube_19;
    private final ModelPart cube_20;
    private final ModelPart cube_21;
    private final ModelPart right_leg;
    private final ModelPart cube_22;
    private final ModelPart cube_23;
    private final ModelPart cube_24;
    private final ModelPart left_leg;
    private final ModelPart cube_25;
    private final ModelPart cube_26;
    private final ModelPart cube_27;
    public orc(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
        this.cube = this.head.getChild("cube");
        this.cube_2 = this.head.getChild("cube_2");
        this.cube_3 = this.head.getChild("cube_3");
        this.cube_4 = this.head.getChild("cube_4");
        this.mouth = this.head.getChild("mouth");
        this.cube_5 = this.mouth.getChild("cube_5");
        this.cube_6 = this.mouth.getChild("cube_6");
        this.cube_7 = this.mouth.getChild("cube_7");
        this.chest = this.root.getChild("chest");
        this.chest_top = this.chest.getChild("chest_top");
        this.cube_8 = this.chest_top.getChild("cube_8");
        this.chest_bottom = this.chest.getChild("chest_bottom");
        this.cube_9 = this.chest_bottom.getChild("cube_9");
        this.cube_10 = this.chest_bottom.getChild("cube_10");
        this.cube_11 = this.chest_bottom.getChild("cube_11");
        this.cube_12 = this.chest_bottom.getChild("cube_12");
        this.cube_13 = this.chest_bottom.getChild("cube_13");
        this.right_arm = this.root.getChild("right_arm");
        this.cube_14 = this.right_arm.getChild("cube_14");
        this.cube_15 = this.right_arm.getChild("cube_15");
        this.cube_16 = this.right_arm.getChild("cube_16");
        this.cube_17 = this.right_arm.getChild("cube_17");
        this.left_arm = this.root.getChild("left_arm");
        this.cube_18 = this.left_arm.getChild("cube_18");
        this.cube_19 = this.left_arm.getChild("cube_19");
        this.cube_20 = this.left_arm.getChild("cube_20");
        this.cube_21 = this.left_arm.getChild("cube_21");
        this.right_leg = this.root.getChild("right_leg");
        this.cube_22 = this.right_leg.getChild("cube_22");
        this.cube_23 = this.right_leg.getChild("cube_23");
        this.cube_24 = this.right_leg.getChild("cube_24");
        this.left_leg = this.root.getChild("left_leg");
        this.cube_25 = this.left_leg.getChild("cube_25");
        this.cube_26 = this.left_leg.getChild("cube_26");
        this.cube_27 = this.left_leg.getChild("cube_27");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        ModelPartData head = root.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -26.6F, 0.0F));
        ModelPartData cube = head.addChild("cube", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -15.4F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 7.6F, 1.0F, 0.1745F, 0.0F, 0.0F));
        ModelPartData cube_2 = head.addChild("cube_2", ModelPartBuilder.create().uv(34, 0).cuboid(-1.2F, -12.8F, -4.6F, 2.2F, 2.0F, 3.6F, new Dilation(0.0F)), ModelTransform.of(0.0F, 7.6F, 1.0F, 0.1745F, 0.0F, 0.0F));
        ModelPartData cube_3 = head.addChild("cube_3", ModelPartBuilder.create().uv(48, 0).cuboid(-5.0453F, -5.9657F, 1.9939F, 3.0F, 3.0F, 0.6F, new Dilation(0.0F)), ModelTransform.of(-4.2F, -2.4F, -2.2F, 0.2533F, 0.7519F, 0.3622F));
        ModelPartData cube_4 = head.addChild("cube_4", ModelPartBuilder.create().uv(58, 0).cuboid(1.6453F, -6.0004F, 2.1969F, 3.0F, 3.0F, 0.6F, new Dilation(0.0F)), ModelTransform.of(4.6F, -2.4F, -2.0F, 0.2533F, -0.7519F, -0.3622F));
        ModelPartData mouth = head.addChild("mouth", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -1.2F, -3.0F));
        ModelPartData cube_5 = mouth.addChild("cube_5", ModelPartBuilder.create().uv(68, 0).cuboid(-4.4F, -1.4624F, -1.5693F, 8.8F, 3.4F, 3.8F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.2F, -1.0F, 0.1745F, 0.0F, 0.0F));
        ModelPartData cube_6 = mouth.addChild("cube_6", ModelPartBuilder.create().uv(96, 0).cuboid(2.0F, -2.4624F, -0.9693F, 1.0F, 1.0F, 0.4F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.2F, -1.0F, 0.1745F, 0.0F, 0.0F));
        ModelPartData cube_7 = mouth.addChild("cube_7", ModelPartBuilder.create().uv(102, 0).cuboid(-3.2F, -2.4624F, -0.9693F, 1.0F, 1.0F, 0.4F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.2F, -1.0F, 0.1745F, 0.0F, 0.0F));
        ModelPartData chest = root.addChild("chest", ModelPartBuilder.create(), ModelTransform.of(0.0F, -22.0F, 0.0F, 0.1745F, 0.0F, 0.0F));
        ModelPartData chest_top = chest.addChild("chest_top", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.0F, 0.0F));
        ModelPartData cube_8 = chest_top.addChild("cube_8", ModelPartBuilder.create().uv(0, 18).cuboid(-7.0F, -8.1909F, -2.8958F, 14.0F, 9.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.4F, 1.0F));
        ModelPartData chest_bottom = chest.addChild("chest_bottom", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 4.0F, 0.0F));
        ModelPartData cube_9 = chest_bottom.addChild("cube_9", ModelPartBuilder.create().uv(46, 18).cuboid(-7.0F, -20.1909F, -2.8958F, 14.0F, 6.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 18.4F, 0.0F));
        ModelPartData cube_10 = chest_bottom.addChild("cube_10", ModelPartBuilder.create().uv(0, 37).cuboid(-7.4F, -17.5909F, -2.2958F, 14.8F, 4.0F, 9.8F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 18.4F, -1.0F));
        ModelPartData cube_11 = chest_bottom.addChild("cube_11", ModelPartBuilder.create().uv(52, 37).cuboid(-2.4F, -19.5909F, -2.2958F, 4.8F, 2.0F, 0.6F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 18.4F, -1.0F));
        ModelPartData cube_12 = chest_bottom.addChild("cube_12", ModelPartBuilder.create().uv(66, 37).cuboid(-1.0F, -18.9909F, -2.6958F, 2.0F, 3.4F, 0.4F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 18.4F, -1.0F));
        ModelPartData cube_13 = chest_bottom.addChild("cube_13", ModelPartBuilder.create().uv(74, 37).cuboid(-1.4F, -18.9909F, -3.0958F, 2.8F, 2.0F, 0.8F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 18.4F, -1.0F));
        ModelPartData right_arm = root.addChild("right_arm", ModelPartBuilder.create(), ModelTransform.of(7.8F, -25.8F, 1.0F, -0.1745F, 0.0F, -0.0436F));
        ModelPartData cube_14 = right_arm.addChild("cube_14", ModelPartBuilder.create().uv(84, 37).cuboid(4.7812F, -4.9192F, -2.4189F, 4.8F, 6.0F, 4.8F, new Dilation(0.0F)), ModelTransform.of(-4.4F, 8.2F, 1.0F, 0.0F, 0.0F, -0.0873F));
        ModelPartData cube_15 = right_arm.addChild("cube_15", ModelPartBuilder.create().uv(0, 53).cuboid(4.1812F, 1.0605F, -2.8792F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-4.4F, 8.2F, 1.0F, 0.0F, 0.0F, -0.0873F));
        ModelPartData cube_16 = right_arm.addChild("cube_16", ModelPartBuilder.create().uv(26, 53).cuboid(3.7809F, 1.6142F, -3.2792F, 6.8F, 4.0F, 6.8F, new Dilation(0.0F)), ModelTransform.of(-4.4F, 8.2F, 1.0F, 0.0F, 0.0F, -0.0873F));
        ModelPartData cube_17 = right_arm.addChild("cube_17", ModelPartBuilder.create().uv(56, 53).cuboid(4.1812F, -10.5192F, -3.0189F, 6.0F, 5.6F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-4.4F, 8.2F, 1.0F, 0.0F, 0.0F, -0.0873F));
        ModelPartData left_arm = root.addChild("left_arm", ModelPartBuilder.create(), ModelTransform.of(-7.8F, -25.8F, 1.0F, -0.1745F, 0.0F, 0.1309F));
        ModelPartData cube_18 = left_arm.addChild("cube_18", ModelPartBuilder.create().uv(82, 53).cuboid(-9.5645F, -4.7199F, -2.4181F, 4.8F, 6.0F, 4.8F, new Dilation(0.0F)), ModelTransform.pivot(5.1243F, 7.6287F, 1.0F));
        ModelPartData cube_19 = left_arm.addChild("cube_19", ModelPartBuilder.create().uv(104, 53).cuboid(-10.1645F, 1.2597F, -2.8784F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(5.1243F, 7.6287F, 1.0F));
        ModelPartData cube_20 = left_arm.addChild("cube_20", ModelPartBuilder.create().uv(0, 67).cuboid(-10.5643F, 2.0134F, -3.2698F, 6.8F, 4.0F, 6.8F, new Dilation(0.0F)), ModelTransform.pivot(5.1243F, 7.6287F, 1.0F));
        ModelPartData cube_21 = left_arm.addChild("cube_21", ModelPartBuilder.create().uv(30, 67).cuboid(-10.1645F, -10.3199F, -3.0181F, 6.0F, 5.6F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(5.1243F, 7.6287F, 1.0F));
        ModelPartData right_leg = root.addChild("right_leg", ModelPartBuilder.create(), ModelTransform.of(3.4F, -12.0F, 0.0F, 0.0F, 0.0436F, -0.1309F));
        ModelPartData cube_22 = right_leg.addChild("cube_22", ModelPartBuilder.create().uv(56, 67).cuboid(-3.1733F, 2.4091F, -0.2017F, 6.8F, 4.6F, 6.8F, new Dilation(0.0F)), ModelTransform.of(-0.6611F, 4.9829F, 0.0F, 0.0F, -0.0436F, 0.1309F));
        ModelPartData cube_23 = right_leg.addChild("cube_23", ModelPartBuilder.create().uv(86, 67).cuboid(-1.7648F, -2.3758F, 0.1981F, 6.0F, 10.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-1.0611F, -0.0171F, 0.0F, 0.0F, -0.0436F, 0.1309F));
        ModelPartData cube_24 = right_leg.addChild("cube_24", ModelPartBuilder.create().uv(0, 85).cuboid(-3.1733F, -2.5909F, -0.2017F, 6.8F, 4.0F, 6.8F, new Dilation(0.0F)), ModelTransform.of(-0.6611F, 4.9829F, 0.0F, 0.0F, -0.0436F, 0.1309F));
        ModelPartData left_leg = root.addChild("left_leg", ModelPartBuilder.create(), ModelTransform.of(-3.2F, -12.0F, 0.0F, 0.0F, -0.0873F, 0.1309F));
        ModelPartData cube_25 = left_leg.addChild("cube_25", ModelPartBuilder.create().uv(30, 85).cuboid(-4.2361F, 1.0205F, -0.2032F, 6.8F, 4.6F, 6.8F, new Dilation(0.0F)), ModelTransform.of(1.2576F, 6.3307F, 0.0F, 0.0F, 0.0873F, -0.1309F));
        ModelPartData cube_26 = left_leg.addChild("cube_26", ModelPartBuilder.create().uv(60, 85).cuboid(-4.236F, -2.3644F, 0.198F, 6.0F, 10.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.8576F, -0.0693F, 0.0F, 0.0F, 0.0873F, -0.1309F));
        ModelPartData cube_27 = left_leg.addChild("cube_27", ModelPartBuilder.create().uv(86, 85).cuboid(-4.2361F, -4.0273F, -0.2019F, 6.8F, 4.0F, 6.8F, new Dilation(0.0F)), ModelTransform.of(1.2576F, 6.3307F, 0.0F, 0.0F, 0.0873F, -0.1309F));

        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }
}
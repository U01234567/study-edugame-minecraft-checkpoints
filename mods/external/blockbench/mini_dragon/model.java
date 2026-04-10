// Generated from mini_dragon GLTF source for the blockbench creature package
// Texture is produced by texture_fix.py as ./model.png
// Paste this class into your mod and generate all required imports

package com.example.mod;

public class mini_dragon extends EntityModel<Entity> {
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

	public mini_dragon(ModelPart root) {
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
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData node_91 = root.addChild("node_91", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -0.0F, 0.0F));

		ModelPartData DRAGON = node_91.addChild("DRAGON", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -7.0F, 0.0F));

		ModelPartData torso = DRAGON.addChild("torso", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -3.5F, -1.0F));

		ModelPartData cube = torso.addChild("cube", ModelPartBuilder.create().uv(0, 0).cuboid(-3.5F, -3.5F, -3.5F, 7.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -0.5F));

		ModelPartData cube_2 = torso.addChild("cube_2", ModelPartBuilder.create().uv(30, 0).cuboid(0.0F, -3.0F, -12.125F, 0.001F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -3.5F, 9.875F));

		ModelPartData waist = torso.addChild("waist", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 3.0F));

		ModelPartData cube_3 = waist.addChild("cube_3", ModelPartBuilder.create().uv(42, 0).cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 3.0F));

		ModelPartData cube_4 = waist.addChild("cube_4", ModelPartBuilder.create().uv(68, 0).cuboid(0.0F, -1.5F, -6.125F, 0.001F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -3.5F, 6.875F));

		ModelPartData tail = waist.addChild("tail", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.5F, 6.025F));

		ModelPartData segment_1 = tail.addChild("segment_1", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -0.025F));

		ModelPartData cube_5 = segment_1.addChild("cube_5", ModelPartBuilder.create().uv(80, 0).cuboid(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 2.5F));

		ModelPartData cube_6 = segment_1.addChild("cube_6", ModelPartBuilder.create().uv(102, 0).cuboid(0.0F, -0.5F, -0.125F, 0.001F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -4.0F, 0.875F));

		ModelPartData segment_2 = segment_1.addChild("segment_2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 5.0F));

		ModelPartData cube_7 = segment_2.addChild("cube_7", ModelPartBuilder.create().uv(112, 0).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

		ModelPartData cube_8 = segment_2.addChild("cube_8", ModelPartBuilder.create().uv(130, 0).cuboid(0.0F, -0.0F, 4.625F, 0.001F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -4.0F, -4.125F));

		ModelPartData segment_3 = segment_2.addChild("segment_3", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 4.0F));

		ModelPartData cube_9 = segment_3.addChild("cube_9", ModelPartBuilder.create().uv(140, 0).cuboid(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

		ModelPartData cube_10 = segment_3.addChild("cube_10", ModelPartBuilder.create().uv(156, 0).cuboid(0.0F, 0.5F, 8.625F, 0.001F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -4.0F, -8.125F));

		ModelPartData segment_4 = segment_3.addChild("segment_4", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 4.0F));

		ModelPartData cube_11 = segment_4.addChild("cube_11", ModelPartBuilder.create().uv(166, 0).cuboid(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

		ModelPartData left_rearleg = waist.addChild("left_rearleg", ModelPartBuilder.create(), ModelTransform.pivot(-3.5F, 1.7071F, 5.75F));

		ModelPartData cube_12 = left_rearleg.addChild("cube_12", ModelPartBuilder.create().uv(180, 0).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData rearleg2 = left_rearleg.addChild("rearleg2", ModelPartBuilder.create(), ModelTransform.pivot(-0.5F, 0.7929F, 0.5F));

		ModelPartData cube_13 = rearleg2.addChild("cube_13", ModelPartBuilder.create().uv(190, 0).cuboid(-1.5F, -2.0F, -1.0F, 3.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

		ModelPartData left_claws2 = rearleg2.addChild("left_claws2", ModelPartBuilder.create(), ModelTransform.of(7.75F, 1.0F, 4.75F, 0.0F, -1.5708F, 0.0F));

		ModelPartData index2 = left_claws2.addChild("index2", ModelPartBuilder.create(), ModelTransform.pivot(-5.25F, 1.5F, 6.5F));

		ModelPartData cube_14 = index2.addChild("cube_14", ModelPartBuilder.create().uv(202, 0).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

		ModelPartData middle2 = left_claws2.addChild("middle2", ModelPartBuilder.create(), ModelTransform.pivot(-5.25F, 1.5F, 7.75F));

		ModelPartData cube_15 = middle2.addChild("cube_15", ModelPartBuilder.create().uv(208, 0).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.5F, 0.0F));

		ModelPartData pinky2 = left_claws2.addChild("pinky2", ModelPartBuilder.create(), ModelTransform.pivot(-5.25F, 1.5F, 9.0F));

		ModelPartData cube_16 = pinky2.addChild("cube_16", ModelPartBuilder.create().uv(214, 0).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

		ModelPartData right_rearleg = waist.addChild("right_rearleg", ModelPartBuilder.create(), ModelTransform.pivot(3.5F, 1.7071F, 5.75F));

		ModelPartData cube_17 = right_rearleg.addChild("cube_17", ModelPartBuilder.create().uv(220, 0).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData rearleg3 = right_rearleg.addChild("rearleg3", ModelPartBuilder.create(), ModelTransform.pivot(0.5F, 1.7929F, 0.5F));

		ModelPartData cube_18 = rearleg3.addChild("cube_18", ModelPartBuilder.create().uv(230, 0).cuboid(-1.5F, -2.0F, -1.0F, 3.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));

		ModelPartData right_claws2 = rearleg3.addChild("right_claws2", ModelPartBuilder.create(), ModelTransform.of(-7.75F, 0.0F, 4.75F, -0.0F, 1.5708F, -0.0F));

		ModelPartData index4 = right_claws2.addChild("index4", ModelPartBuilder.create(), ModelTransform.pivot(5.25F, 2.5F, 6.5F));

		ModelPartData cube_19 = index4.addChild("cube_19", ModelPartBuilder.create().uv(242, 0).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));

		ModelPartData middle4 = right_claws2.addChild("middle4", ModelPartBuilder.create(), ModelTransform.pivot(5.25F, 3.0F, 7.75F));

		ModelPartData cube_20 = middle4.addChild("cube_20", ModelPartBuilder.create().uv(248, 0).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));

		ModelPartData pinky4 = right_claws2.addChild("pinky4", ModelPartBuilder.create(), ModelTransform.pivot(5.25F, 2.5F, 9.0F));

		ModelPartData cube_21 = pinky4.addChild("cube_21", ModelPartBuilder.create().uv(254, 0).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));

		ModelPartData neck = torso.addChild("neck", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -0.5F, -4.0F));

		ModelPartData neck_2 = neck.addChild("neck_2", ModelPartBuilder.create().uv(260, 0).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -2.0F));

		ModelPartData head = neck.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -4.0F));

		ModelPartData left_ear = head.addChild("left_ear", ModelPartBuilder.create().uv(0, 64).cuboid(0.0F, -1.5F, -2.0F, 0.001F, 3.0F, 4.0F, new Dilation(0.25F)), ModelTransform.pivot(-3.0F, -0.5F, 2.0F));

		ModelPartData right_ear = head.addChild("right_ear", ModelPartBuilder.create().uv(12, 64).cuboid(0.0F, -1.5F, -2.0F, 0.001F, 3.0F, 4.0F, new Dilation(0.25F)), ModelTransform.pivot(3.0F, -0.5F, 2.0F));

		ModelPartData top = head.addChild("top", ModelPartBuilder.create().uv(24, 64).cuboid(-3.001F, -0.001F, -4.501F, 6.002F, 0.002F, 9.002F, new Dilation(0.25F)), ModelTransform.pivot(0.0F, -2.0F, -4.5F));

		ModelPartData right_side = head.addChild("right_side", ModelPartBuilder.create().uv(56, 64).cuboid(-6.0F, -6.0F, -4.0F, 0.001F, 6.0F, 9.0F, new Dilation(0.25F)).uv(78, 64).cuboid(-6.0F, -6.0F, -4.0F, 0.001F, 6.0F, 9.0F, new Dilation(0.25F)).uv(100, 64).cuboid(-6.0F, -6.0F, -4.0F, 0.001F, 6.0F, 0.001F, new Dilation(0.25F)), ModelTransform.pivot(9.0F, 4.0F, -5.0F));

		ModelPartData left_side = head.addChild("left_side", ModelPartBuilder.create().uv(106, 64).cuboid(0.0F, -3.0F, -4.5F, 0.001F, 6.0F, 9.0F, new Dilation(0.25F)).uv(128, 64).cuboid(0.0F, -3.0F, -4.5F, 0.001F, 6.0F, 9.0F, new Dilation(0.25F)), ModelTransform.pivot(-3.0F, 1.0F, -4.5F));

		ModelPartData down = head.addChild("down", ModelPartBuilder.create().uv(150, 64).cuboid(-6.0F, 0.999F, -4.0F, 6.0F, 0.001F, 9.0F, new Dilation(0.25F)).uv(182, 64).cuboid(-6.0F, 1.0F, -4.0F, 6.0F, 0.001F, 9.0F, new Dilation(0.25F)).uv(214, 64).cuboid(-6.0F, 0.999F, -4.0F, 6.0F, 0.001F, 9.0F, new Dilation(0.25F)), ModelTransform.pivot(3.0F, 3.0F, -5.0F));

		ModelPartData front = head.addChild("front", ModelPartBuilder.create().uv(246, 64).cuboid(-6.0F, -5.0F, -4.0F, 6.0F, 6.0F, 0.001F, new Dilation(0.25F)).uv(262, 64).cuboid(-6.0F, -5.0F, -4.0F, 6.0F, 6.0F, 0.001F, new Dilation(0.25F)), ModelTransform.pivot(3.0F, 3.0F, -5.0F));

		ModelPartData back = head.addChild("back", ModelPartBuilder.create().uv(278, 64).cuboid(-6.0F, -5.0F, -4.0F, 6.0F, 6.0F, 0.001F, new Dilation(0.25F)).uv(294, 64).cuboid(-6.0F, -5.0F, -3.999F, 6.0F, 6.0F, 0.001F, new Dilation(0.25F)).uv(310, 64).cuboid(-6.0F, -5.0F, -4.0F, 6.0F, 6.0F, 0.001F, new Dilation(0.25F)), ModelTransform.pivot(3.0F, 3.0F, 4.0F));

		ModelPartData down_2 = head.addChild("down_2", ModelPartBuilder.create().uv(326, 64).cuboid(-3.0F, -3.001F, -4.5F, 6.0F, 0.001F, 9.0F, new Dilation(0.25F)).uv(358, 64).cuboid(-3.0F, -3.001F, -4.5F, 6.0F, 0.001F, 9.0F, new Dilation(0.25F)).uv(390, 64).cuboid(-3.0F, -3.001F, -4.5F, 6.0F, 0.001F, 9.0F, new Dilation(0.25F)), ModelTransform.pivot(0.0F, 7.0F, -4.5F));

		ModelPartData jaw = head.addChild("jaw", ModelPartBuilder.create(), ModelTransform.of(0.0F, 3.4F, -0.75F, -0.0873F, 0.0F, -0.0F));

		ModelPartData back_2 = jaw.addChild("back_2", ModelPartBuilder.create().uv(422, 64).cuboid(-2.0F, -3.0F, 4.5F, 5.0F, 4.0F, 0.001F, new Dilation(0.25F)), ModelTransform.pivot(-0.5F, 0.9486F, -3.9848F));

		ModelPartData front_2 = jaw.addChild("front_2", ModelPartBuilder.create().uv(436, 64).cuboid(-2.0F, -3.0F, -4.5F, 5.0F, 4.0F, 0.001F, new Dilation(0.25F)), ModelTransform.pivot(-0.5F, 0.9486F, -3.9848F));

		ModelPartData left_side_2 = jaw.addChild("left_side_2", ModelPartBuilder.create().uv(450, 64).cuboid(-3.0F, -3.0F, -4.5F, 0.001F, 4.0F, 9.0F, new Dilation(0.25F)), ModelTransform.pivot(0.5F, 0.9486F, -3.9848F));

		ModelPartData right_side_2 = jaw.addChild("right_side_2", ModelPartBuilder.create().uv(472, 64).cuboid(3.0F, -3.0F, -4.5F, 0.001F, 4.0F, 9.0F, new Dilation(0.25F)), ModelTransform.pivot(-0.5F, 0.9486F, -3.9848F));

		ModelPartData bottom = jaw.addChild("bottom", ModelPartBuilder.create().uv(0, 81).cuboid(-2.001F, 2.999F, -4.501F, 5.002F, 0.002F, 9.002F, new Dilation(0.25F)), ModelTransform.pivot(-0.5F, -1.0514F, -3.9848F));

		ModelPartData right_forearm = torso.addChild("right_forearm", ModelPartBuilder.create(), ModelTransform.pivot(5.0F, 1.0F, -0.25F));

		ModelPartData cube_22 = right_forearm.addChild("cube_22", ModelPartBuilder.create().uv(278, 0).cuboid(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData forearm3 = right_forearm.addChild("forearm3", ModelPartBuilder.create(), ModelTransform.pivot(0.25F, 2.5F, 0.0F));

		ModelPartData cube_23 = forearm3.addChild("cube_23", ModelPartBuilder.create().uv(292, 0).cuboid(-1.0F, -2.0F, -1.5F, 2.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));

		ModelPartData right_claws = forearm3.addChild("right_claws", ModelPartBuilder.create(), ModelTransform.pivot(-6.75F, 0.0F, 1.25F));

		ModelPartData index3 = right_claws.addChild("index3", ModelPartBuilder.create(), ModelTransform.pivot(7.0F, 2.5F, -2.5F));

		ModelPartData cube_24 = index3.addChild("cube_24", ModelPartBuilder.create().uv(304, 0).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));

		ModelPartData middle3 = right_claws.addChild("middle3", ModelPartBuilder.create(), ModelTransform.pivot(7.0F, 3.0F, -1.25F));

		ModelPartData cube_25 = middle3.addChild("cube_25", ModelPartBuilder.create().uv(310, 0).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));

		ModelPartData pinky3 = right_claws.addChild("pinky3", ModelPartBuilder.create(), ModelTransform.pivot(7.0F, 2.5F, 0.0F));

		ModelPartData cube_26 = pinky3.addChild("cube_26", ModelPartBuilder.create().uv(316, 0).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.0F, 0.0F));

		ModelPartData left_forearm = torso.addChild("left_forearm", ModelPartBuilder.create(), ModelTransform.pivot(-5.0F, 1.0F, -0.25F));

		ModelPartData cube_27 = left_forearm.addChild("cube_27", ModelPartBuilder.create().uv(322, 0).cuboid(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData forearm2 = left_forearm.addChild("forearm2", ModelPartBuilder.create(), ModelTransform.pivot(-0.25F, 0.5F, 0.0F));

		ModelPartData cube_28 = forearm2.addChild("cube_28", ModelPartBuilder.create().uv(336, 0).cuboid(-1.0F, -2.0F, -1.5F, 2.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 0.0F));

		ModelPartData left_claws = forearm2.addChild("left_claws", ModelPartBuilder.create(), ModelTransform.pivot(6.75F, 2.0F, 1.25F));

		ModelPartData index = left_claws.addChild("index", ModelPartBuilder.create(), ModelTransform.pivot(-7.0F, 1.5F, -2.5F));

		ModelPartData cube_29 = index.addChild("cube_29", ModelPartBuilder.create().uv(348, 0).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

		ModelPartData middle = left_claws.addChild("middle", ModelPartBuilder.create(), ModelTransform.pivot(-7.0F, 2.0F, -1.25F));

		ModelPartData cube_30 = middle.addChild("cube_30", ModelPartBuilder.create().uv(354, 0).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

		ModelPartData pinky = left_claws.addChild("pinky", ModelPartBuilder.create(), ModelTransform.pivot(-7.0F, 1.5F, 0.0F));

		ModelPartData cube_31 = pinky.addChild("cube_31", ModelPartBuilder.create().uv(360, 0).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

		ModelPartData left_wing = torso.addChild("left_wing", ModelPartBuilder.create(), ModelTransform.pivot(-3.375F, -3.5F, -2.025F));

		ModelPartData cube_32 = left_wing.addChild("cube_32", ModelPartBuilder.create().uv(366, 0).cuboid(-5.0F, -4.0F, -5.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(3.875F, 1.0F, 4.025F));

		ModelPartData cube_33 = left_wing.addChild("cube_33", ModelPartBuilder.create().uv(376, 0).cuboid(-4.0F, -10.0F, -5.0F, 1.0F, 10.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(3.875F, -3.0F, 4.025F));

		ModelPartData cube_34 = left_wing.addChild("cube_34", ModelPartBuilder.create().uv(382, 0).cuboid(-4.0F, -1.0F, -5.0F, 1.0F, 1.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(3.875F, 0.0F, 6.025F));

		ModelPartData cube_35 = left_wing.addChild("cube_35", ModelPartBuilder.create().uv(404, 0).cuboid(-4.0F, -16.0F, -6.0F, 0.001F, 19.0F, 15.0F, new Dilation(0.0F)), ModelTransform.pivot(4.375F, -3.0F, 5.025F));

		ModelPartData right_wing = torso.addChild("right_wing", ModelPartBuilder.create(), ModelTransform.pivot(3.125F, -3.5F, -2.025F));

		ModelPartData cube_36 = right_wing.addChild("cube_36", ModelPartBuilder.create().uv(438, 0).cuboid(3.0F, -4.0F, -5.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.625F, 1.0F, 4.025F));

		ModelPartData cube_37 = right_wing.addChild("cube_37", ModelPartBuilder.create().uv(448, 0).cuboid(3.0F, -10.0F, -5.0F, 1.0F, 10.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.625F, -3.0F, 4.025F));

		ModelPartData cube_38 = right_wing.addChild("cube_38", ModelPartBuilder.create().uv(454, 0).cuboid(3.0F, -1.0F, -5.0F, 1.0F, 1.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.625F, 0.0F, 6.025F));

		ModelPartData cube_39 = right_wing.addChild("cube_39", ModelPartBuilder.create().uv(476, 0).cuboid(4.0F, -16.0F, -6.0F, 0.001F, 19.0F, 15.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.125F, -3.0F, 5.025F));

		return TexturedModelData.of(modelData, 512, 128);
	}

	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
		root.render(matrices, vertexConsumer, light, overlay, color);
	}
}
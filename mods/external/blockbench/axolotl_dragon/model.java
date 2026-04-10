// Generated from axolotl_dragon/source/model.gltf
// Target folder: mods/external/blockbench/axolotl_dragon/
// Uses original texture unchanged: copy ./textures/gltf_embedded_0.png -> ./model.png

public class model extends EntityModel<Entity> {
	private final ModelPart root;

	public model(ModelPart root) {
		this.root = root.getChild("root");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData body = root.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData cube = body.addChild("cube", ModelPartBuilder.create().uv(0, 98).cuboid(-9.0F, -10.0F, -22.0F, 18.0F, 11.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData cube_2 = body.addChild("cube_2", ModelPartBuilder.create().uv(96, 34).cuboid(0.0F, -14.0F, -14.0F, 0.0F, 4.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData neck = body.addChild("neck", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -4.5F, -21.5F));
		ModelPartData cube_3 = neck.addChild("cube_3", ModelPartBuilder.create().uv(24, 158).cuboid(-5.0F, -9.0F, -28.0F, 10.0F, 9.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 4.5F, 22.0F));
		ModelPartData head = neck.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.5F, -6.0F));
		ModelPartData cube_4 = head.addChild("cube_4", ModelPartBuilder.create().uv(54, 110).cuboid(-9.0F, -12.0F, -44.0F, 18.0F, 7.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 28.0F));
		ModelPartData jaw = head.addChild("jaw", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -3.0F, 0.0F));
		ModelPartData cube_5 = jaw.addChild("cube_5", ModelPartBuilder.create().uv(108, 118).cuboid(-9.0F, -3.0F, -44.0F, 18.0F, 6.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 3.0F, 28.0F));
		ModelPartData teeth_bottom = jaw.addChild("teeth_bottom", ModelPartBuilder.create(), ModelTransform.of(0.0F, 2.0F, -8.0F, -0.0436F, 0.0F, 0.0F));
		ModelPartData cube_6 = teeth_bottom.addChild("cube_6", ModelPartBuilder.create().uv(48, 140).cuboid(-8.0F, -6.0F, -43.0F, 16.0F, 2.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 5.0F, 36.0F));
		ModelPartData teeth_top = head.addChild("teeth_top", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.0F, -14.0F, 0.0436F, 0.0F, 0.0F));
		ModelPartData cube_7 = teeth_top.addChild("cube_7", ModelPartBuilder.create().uv(48, 140).cuboid(-8.0F, -10.0F, -37.0F, 16.0F, 2.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 5.0F, 36.0F));
		ModelPartData right_gills = head.addChild("right_gills", ModelPartBuilder.create(), ModelTransform.of(7.0F, -6.0F, -1.0F, -0.2921F, -0.0905F, -0.0423F));
		ModelPartData right_top_gill = right_gills.addChild("right_top_gill", ModelPartBuilder.create(), ModelTransform.of(-4.0F, -4.0F, 0.0F, -0.2564F, 0.0665F, 0.1615F));
		ModelPartData cube_8 = right_top_gill.addChild("cube_8", ModelPartBuilder.create().uv(66, 192).cuboid(-2.0F, -7.5F, 0.0F, 10.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.5F, 1.5F, 0.0F));
		ModelPartData cube_9 = right_top_gill.addChild("cube_9", ModelPartBuilder.create().uv(190, 178).cuboid(-5.0F, -10.0F, 0.0F, 10.0F, 10.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, -6.0F, 0.0F, -0.3054F, 0.0F, 0.0F));
		ModelPartData right_mid_gill = right_gills.addChild("right_mid_gill", ModelPartBuilder.create(), ModelTransform.of(2.0F, -4.0F, 0.5F, -0.4858F, -0.0345F, 0.775F));
		ModelPartData cube_10 = right_mid_gill.addChild("cube_10", ModelPartBuilder.create().uv(162, 124).cuboid(-2.0F, -3.5F, 0.0F, 10.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, -1.0F, 0.0F));
		ModelPartData cube_11 = right_mid_gill.addChild("cube_11", ModelPartBuilder.create().uv(44, 186).cuboid(-5.0F, -10.0F, 0.0F, 10.0F, 10.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5F, 0.0F, -0.2618F, 0.0F, 0.0F));
		ModelPartData right_bot_gill = right_gills.addChild("right_bot_gill", ModelPartBuilder.create(), ModelTransform.of(2.0F, 1.0F, 1.0F, 0.4815F, -0.573F, -0.2744F));
		ModelPartData cube_12 = right_bot_gill.addChild("cube_12", ModelPartBuilder.create().uv(170, 6).cuboid(0.0F, -5.0F, 0.0F, 10.0F, 10.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, 0.0F, 0.0F, 0.0F, -0.3491F, 0.0F));
		ModelPartData cube_13 = right_bot_gill.addChild("cube_13", ModelPartBuilder.create().uv(0, 126).cuboid(0.0F, -7.5F, 0.0F, 8.0F, 10.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 2.5F, 0.0F));
		ModelPartData left_gills = head.addChild("left_gills", ModelPartBuilder.create(), ModelTransform.of(-7.0F, -6.0F, -1.0F, -0.2921F, 0.0905F, 0.0423F));
		ModelPartData left_top_gill = left_gills.addChild("left_top_gill", ModelPartBuilder.create(), ModelTransform.of(4.0F, -4.0F, 0.0F, -0.2564F, -0.0665F, -0.1615F));
		ModelPartData cube_14 = left_top_gill.addChild("cube_14", ModelPartBuilder.create().uv(66, 192).cuboid(-8.0F, -7.5F, 0.0F, 10.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(2.5F, 1.5F, 0.0F));
		ModelPartData cube_15 = left_top_gill.addChild("cube_15", ModelPartBuilder.create().uv(190, 178).cuboid(-5.0F, -10.0F, 0.0F, 10.0F, 10.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -6.0F, 0.0F, -0.3054F, 0.0F, 0.0F));
		ModelPartData left_mid_gill = left_gills.addChild("left_mid_gill", ModelPartBuilder.create(), ModelTransform.of(-2.0F, -4.0F, 0.5F, -0.4858F, 0.0345F, -0.775F));
		ModelPartData cube_16 = left_mid_gill.addChild("cube_16", ModelPartBuilder.create().uv(162, 124).cuboid(-8.0F, -3.5F, 0.0F, 10.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, -1.0F, 0.0F));
		ModelPartData cube_17 = left_mid_gill.addChild("cube_17", ModelPartBuilder.create().uv(44, 186).cuboid(-5.0F, -10.0F, 0.0F, 10.0F, 10.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5F, 0.0F, -0.2618F, 0.0F, 0.0F));
		ModelPartData left_bot_gill = left_gills.addChild("left_bot_gill", ModelPartBuilder.create(), ModelTransform.of(-2.0F, 1.0F, 1.0F, 0.4815F, 0.573F, 0.2744F));
		ModelPartData cube_18 = left_bot_gill.addChild("cube_18", ModelPartBuilder.create().uv(170, 6).cuboid(-10.0F, -5.0F, 0.0F, 10.0F, 10.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-6.0F, 0.0F, 0.0F, 0.0F, 0.3491F, 0.0F));
		ModelPartData cube_19 = left_bot_gill.addChild("cube_19", ModelPartBuilder.create().uv(0, 126).cuboid(-8.0F, -7.5F, 0.0F, 8.0F, 10.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 2.5F, 0.0F));
		ModelPartData left_wing = body.addChild("left_wing", ModelPartBuilder.create(), ModelTransform.pivot(-5.0F, -10.5F, -16.0F));
		ModelPartData cube_20 = left_wing.addChild("cube_20", ModelPartBuilder.create().uv(0, 34).cuboid(-35.0F, -10.002F, -16.0F, 34.0F, 0.0F, 26.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, 10.0F, 16.0F));
		ModelPartData cube_21 = left_wing.addChild("cube_21", ModelPartBuilder.create().uv(86, 62).cuboid(-35.0F, -11.0F, -16.0F, 34.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, 10.0F, 15.5F));
		ModelPartData left_fwing = left_wing.addChild("left_fwing", ModelPartBuilder.create(), ModelTransform.pivot(-32.0F, 0.0F, -0.55F));
		ModelPartData cube_22 = left_fwing.addChild("cube_22", ModelPartBuilder.create().uv(0, 0).cuboid(-49.0F, -10.002F, -16.0F, 48.0F, 0.0F, 32.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, 10.0F, 16.55F));
		ModelPartData cube_23 = left_fwing.addChild("cube_23", ModelPartBuilder.create().uv(178, 34).cuboid(-19.0F, -11.0F, -16.0F, 18.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, 10.0F, 16.05F));
		ModelPartData cube_24 = left_fwing.addChild("cube_24", ModelPartBuilder.create().uv(86, 68).cuboid(-32.0F, -1.0F, 0.0F, 32.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-18.0F, 0.0F, 0.05F, 0.0F, 0.3054F, 0.0F));
		ModelPartData cube_25 = left_fwing.addChild("cube_25", ModelPartBuilder.create().uv(170, 0).cuboid(-24.0F, -1.0F, 0.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 0.0F, 0.55F, 0.0F, 0.6981F, 0.0F));
		ModelPartData cube_26 = left_fwing.addChild("cube_26", ModelPartBuilder.create().uv(172, 148).cuboid(-20.0F, -1.2F, 0.0F, 20.0F, 2.2F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-19.3851F, 0.0F, 15.9769F, 0.0F, 0.9599F, 0.0F));
		ModelPartData cube_27 = left_fwing.addChild("cube_27", ModelPartBuilder.create().uv(44, 134).cuboid(-26.5F, -1.2F, -1.5F, 26.0F, 2.2F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.05F, 0.0F, 1.5708F, 0.0F));
		ModelPartData right_wing = body.addChild("right_wing", ModelPartBuilder.create(), ModelTransform.pivot(5.0F, -10.5F, -16.0F));
		ModelPartData cube_28 = right_wing.addChild("cube_28", ModelPartBuilder.create().uv(0, 34).cuboid(1.0F, -10.002F, -16.0F, 34.0F, 0.0F, 26.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, 10.0F, 16.0F));
		ModelPartData cube_29 = right_wing.addChild("cube_29", ModelPartBuilder.create().uv(86, 62).cuboid(1.0F, -11.0F, -16.0F, 34.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, 10.0F, 15.5F));
		ModelPartData right_fwing = right_wing.addChild("right_fwing", ModelPartBuilder.create(), ModelTransform.pivot(32.0F, 0.0F, -0.55F));
		ModelPartData cube_30 = right_fwing.addChild("cube_30", ModelPartBuilder.create().uv(0, 0).cuboid(1.0F, -10.002F, -16.0F, 48.0F, 0.0F, 32.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 10.0F, 16.55F));
		ModelPartData cube_31 = right_fwing.addChild("cube_31", ModelPartBuilder.create().uv(178, 34).cuboid(1.0F, -11.0F, -16.0F, 18.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 10.0F, 16.05F));
		ModelPartData cube_32 = right_fwing.addChild("cube_32", ModelPartBuilder.create().uv(170, 0).cuboid(0.0F, -1.0F, 0.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 0.0F, 0.55F, 0.0F, -0.6981F, 0.0F));
		ModelPartData cube_33 = right_fwing.addChild("cube_33", ModelPartBuilder.create().uv(172, 148).cuboid(0.0F, -1.2F, 0.0F, 20.0F, 2.2F, 2.0F, new Dilation(0.0F)), ModelTransform.of(19.3851F, 0.0F, 15.9769F, 0.0F, -0.9599F, 0.0F));
		ModelPartData cube_34 = right_fwing.addChild("cube_34", ModelPartBuilder.create().uv(44, 134).cuboid(0.5F, -1.2F, -1.5F, 26.0F, 2.2F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.05F, 0.0F, -1.5708F, 0.0F));
		ModelPartData cube_35 = right_fwing.addChild("cube_35", ModelPartBuilder.create().uv(86, 68).cuboid(0.0F, -1.0F, 0.0F, 32.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(18.0F, 0.0F, 0.05F, 0.0F, -0.3054F, 0.0F));
		ModelPartData right_foreleg = body.addChild("right_foreleg", ModelPartBuilder.create(), ModelTransform.of(8.0F, -1.0F, -16.0F, 0.3368F, 0.2118F, -0.5779F));
		ModelPartData cube_36 = right_foreleg.addChild("cube_36", ModelPartBuilder.create().uv(22, 182).cuboid(-0.5F, -3.0F, -4.5F, 5.0F, 14.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 0.0F));
		ModelPartData right_knee = right_foreleg.addChild("right_knee", ModelPartBuilder.create(), ModelTransform.of(2.0F, 12.0F, 2.0F, -0.7854F, 0.0F, 0.0F));
		ModelPartData cube_37 = right_knee.addChild("cube_37", ModelPartBuilder.create().uv(0, 182).cuboid(0.0F, -3.0F, -4.0F, 4.0F, 14.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 3.0F, -2.0F));
		ModelPartData right_foot = right_knee.addChild("right_foot", ModelPartBuilder.create(), ModelTransform.of(1.0F, 14.0F, -3.0F, 0.0F, 0.0F, 0.1309F));
		ModelPartData cube_38 = right_foot.addChild("cube_38", ModelPartBuilder.create().uv(184, 52).cuboid(0.5F, -3.0F, -5.0F, 3.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, 3.0F, 1.0F));
		ModelPartData left_foreleg = body.addChild("left_foreleg", ModelPartBuilder.create(), ModelTransform.of(-8.0F, -1.0F, -16.0F, 0.3368F, -0.2118F, 0.5779F));
		ModelPartData cube_39 = left_foreleg.addChild("cube_39", ModelPartBuilder.create().uv(22, 182).cuboid(-4.5F, -3.0F, -4.5F, 5.0F, 14.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 0.0F));
		ModelPartData left_knee = left_foreleg.addChild("left_knee", ModelPartBuilder.create(), ModelTransform.of(-2.0F, 12.0F, 2.0F, -0.7854F, 0.0F, 0.0F));
		ModelPartData cube_40 = left_knee.addChild("cube_40", ModelPartBuilder.create().uv(0, 182).cuboid(-4.0F, -3.0F, -4.0F, 4.0F, 14.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 3.0F, -2.0F));
		ModelPartData left_foot = left_knee.addChild("left_foot", ModelPartBuilder.create(), ModelTransform.of(-1.0F, 14.0F, -3.0F, 0.0F, 0.0F, -0.1309F));
		ModelPartData cube_41 = left_foot.addChild("cube_41", ModelPartBuilder.create().uv(184, 52).cuboid(-3.5F, -3.0F, -5.0F, 3.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, 3.0F, 1.0F));
		ModelPartData body_back = body.addChild("body_back", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -10.0F, -6.0F));
		ModelPartData cube_42 = body_back.addChild("cube_42", ModelPartBuilder.create().uv(84, 76).cuboid(-8.0F, -10.0F, -6.0F, 16.0F, 10.0F, 22.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 10.0F, 6.0F));
		ModelPartData cube_43 = body_back.addChild("cube_43", ModelPartBuilder.create().uv(46, 158).cuboid(0.0F, -14.0F, -6.0F, 0.0F, 4.0F, 22.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 10.0F, 6.0F));
		ModelPartData cube_44 = body_back.addChild("cube_44", ModelPartBuilder.create().uv(0, 154).cuboid(0.0F, -14.0F, -6.0F, 0.0F, 4.0F, 22.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 6.0F));
		ModelPartData right_hindleg = body_back.addChild("right_hindleg", ModelPartBuilder.create(), ModelTransform.of(6.5F, 9.0F, 16.0F, -0.1769F, 0.1491F, -0.3077F));
		ModelPartData cube_45 = right_hindleg.addChild("cube_45", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -3.0F, -4.5F, 6.0F, 14.0F, 9.5F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, -1.0F, 2.0F));
		ModelPartData bone3 = right_hindleg.addChild("bone3", ModelPartBuilder.create(), ModelTransform.of(1.0F, 10.0F, -2.5F, 1.2654F, 0.0F, 0.0F));
		ModelPartData cube_46 = bone3.addChild("cube_46", ModelPartBuilder.create().uv(70, 158).cuboid(-0.5F, -3.0F, -4.5F, 5.0F, 14.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 3.0F, 4.5F));
		ModelPartData right_knee_back = bone3.addChild("right_knee_back", ModelPartBuilder.create(), ModelTransform.of(2.0F, 14.0F, 6.5F, -0.9599F, 0.0F, 0.0F));
		ModelPartData cube_47 = right_knee_back.addChild("cube_47", ModelPartBuilder.create().uv(0, 154).cuboid(0.0F, -3.0F, -4.0F, 4.0F, 14.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 3.0F, -2.0F));
		ModelPartData right_foot_back = right_knee_back.addChild("right_foot_back", ModelPartBuilder.create(), ModelTransform.of(1.0F, 14.0F, -3.0F, 0.0F, 0.0F, 0.2182F));
		ModelPartData cube_48 = right_foot_back.addChild("cube_48", ModelPartBuilder.create().uv(132, 168).cuboid(0.5F, -3.0F, -5.0F, 3.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, 3.0F, 1.0F));
		ModelPartData left_hindleg = body_back.addChild("left_hindleg", ModelPartBuilder.create(), ModelTransform.of(-6.5F, 9.0F, 16.0F, -0.1769F, -0.1491F, 0.3077F));
		ModelPartData cube_49 = left_hindleg.addChild("cube_49", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -3.0F, -4.5F, 6.0F, 14.0F, 9.5F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, -1.0F, 2.0F));
		ModelPartData bone2 = left_hindleg.addChild("bone2", ModelPartBuilder.create(), ModelTransform.of(-1.0F, 10.0F, -2.5F, 1.2654F, 0.0F, 0.0F));
		ModelPartData cube_50 = bone2.addChild("cube_50", ModelPartBuilder.create().uv(70, 158).cuboid(-4.5F, -3.0F, -4.5F, 5.0F, 14.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 3.0F, 4.5F));
		ModelPartData left_knee_back = bone2.addChild("left_knee_back", ModelPartBuilder.create(), ModelTransform.of(-2.0F, 14.0F, 6.5F, -0.9599F, 0.0F, 0.0F));
		ModelPartData cube_51 = left_knee_back.addChild("cube_51", ModelPartBuilder.create().uv(0, 154).cuboid(-4.0F, -3.0F, -4.0F, 4.0F, 14.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 3.0F, -2.0F));
		ModelPartData left_foot_back = left_knee_back.addChild("left_foot_back", ModelPartBuilder.create(), ModelTransform.of(-1.0F, 14.0F, -3.0F, 0.0F, 0.0F, -0.2182F));
		ModelPartData cube_52 = left_foot_back.addChild("cube_52", ModelPartBuilder.create().uv(132, 168).cuboid(-3.5F, -3.0F, -5.0F, 3.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, 3.0F, 1.0F));
		ModelPartData tail = body_back.addChild("tail", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.5F, 22.0F));
		ModelPartData cube_53 = tail.addChild("cube_53", ModelPartBuilder.create().uv(0, 126).cuboid(-6.0F, -9.5F, 16.0F, 12.0F, 9.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 9.5F, -16.0F));
		ModelPartData cube_54 = tail.addChild("cube_54", ModelPartBuilder.create().uv(176, 100).cuboid(0.0F, -13.5F, 16.0F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 9.5F, -16.0F));
		ModelPartData cube_55 = tail.addChild("cube_55", ModelPartBuilder.create().uv(150, 172).cuboid(0.0F, 9.5F, 16.0F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.5F, -16.0F));
		ModelPartData tail2 = tail.addChild("tail2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.5F, 18.0F));
		ModelPartData cube_56 = tail2.addChild("cube_56", ModelPartBuilder.create().uv(130, 0).cuboid(-5.0F, -9.0F, 16.0F, 10.0F, 8.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 9.0F, -16.0F));
		ModelPartData cube_57 = tail2.addChild("cube_57", ModelPartBuilder.create().uv(178, 10).cuboid(0.0F, -13.0F, 16.0F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 9.0F, -16.0F));
		ModelPartData cube_58 = tail2.addChild("cube_58", ModelPartBuilder.create().uv(172, 124).cuboid(0.0F, 9.0F, 16.0F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -1.0F, -16.0F));
		ModelPartData tail3 = tail2.addChild("tail3", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.5F, 18.0F));
		ModelPartData cube_59 = tail3.addChild("cube_59", ModelPartBuilder.create().uv(92, 142).cuboid(-4.0F, -8.5F, 16.0F, 8.0F, 7.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.5F, -16.0F));
		ModelPartData cube_60 = tail3.addChild("cube_60", ModelPartBuilder.create().uv(170, 178).cuboid(0.0F, -12.5F, 16.0F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.5F, -16.0F));
		ModelPartData cube_61 = tail3.addChild("cube_61", ModelPartBuilder.create().uv(112, 168).cuboid(0.0F, 8.5F, 16.0F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -1.5F, -16.0F));
		ModelPartData tail4 = tail3.addChild("tail4", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.5F, 18.0F));
		ModelPartData cube_62 = tail4.addChild("cube_62", ModelPartBuilder.create().uv(144, 92).cuboid(-3.0F, -8.0F, 16.0F, 6.0F, 6.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.0F, -16.0F));
		ModelPartData cube_63 = tail4.addChild("cube_63", ModelPartBuilder.create().uv(176, 76).cuboid(0.0F, -12.0F, 16.0F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.0F, -16.0F));
		ModelPartData cube_64 = tail4.addChild("cube_64", ModelPartBuilder.create().uv(74, 168).cuboid(0.0F, 8.0F, 16.0F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -2.0F, -16.0F));
		ModelPartData tail5 = tail4.addChild("tail5", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.5F, 18.0F));
		ModelPartData cube_65 = tail5.addChild("cube_65", ModelPartBuilder.create().uv(150, 28).cuboid(-2.0F, -7.5F, 16.0F, 4.0F, 5.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 7.5F, -16.0F));
		ModelPartData cube_66 = tail5.addChild("cube_66", ModelPartBuilder.create().uv(174, 154).cuboid(0.0F, -11.5F, 16.0F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 7.5F, -16.0F));
		ModelPartData cube_67 = tail5.addChild("cube_67", ModelPartBuilder.create().uv(164, 52).cuboid(0.0F, 7.5F, 16.0F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -2.5F, -16.0F));
		ModelPartData tail6 = tail5.addChild("tail6", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.5F, 18.0F));
		ModelPartData cube_68 = tail6.addChild("cube_68", ModelPartBuilder.create().uv(94, 168).cuboid(-1.0F, -7.0F, 16.0F, 2.0F, 4.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 7.0F, -16.0F));
		ModelPartData cube_69 = tail6.addChild("cube_69", ModelPartBuilder.create().uv(140, 56).cuboid(0.0F, -10.5F, 16.0F, 0.0F, 11.0F, 22.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 7.0F, -16.0F));

		return TexturedModelData.of(modelData, 256, 256);
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
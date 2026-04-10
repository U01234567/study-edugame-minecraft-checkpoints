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
 * Creature id: grassling_spreder
 */
public class GrasslingSprederModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart node_44;
    private final ModelPart tuttoilcorpo;
    private final ModelPart testa;
    private final ModelPart cube;
    private final ModelPart fowerbomb;
    private final ModelPart flowerB1;
    private final ModelPart cube_2;
    private final ModelPart flowerB2;
    private final ModelPart cube_3;
    private final ModelPart flowerB3;
    private final ModelPart cube_4;
    private final ModelPart flowerB4;
    private final ModelPart cube_5;
    private final ModelPart flowerB5;
    private final ModelPart cube_6;
    private final ModelPart flowerB6;
    private final ModelPart cube_7;
    private final ModelPart flowerB7;
    private final ModelPart cube_8;
    private final ModelPart flowerB8;
    private final ModelPart cube_9;
    private final ModelPart flowerB9;
    private final ModelPart cube_10;
    private final ModelPart flowerB10;
    private final ModelPart cube_11;
    private final ModelPart flowerB11;
    private final ModelPart cube_12;
    private final ModelPart arms;
    private final ModelPart arm1;
    private final ModelPart cube_13;
    private final ModelPart cube_14;
    private final ModelPart arm2;
    private final ModelPart cube_15;
    private final ModelPart cube_16;
    private final ModelPart legs;
    private final ModelPart leg1;
    private final ModelPart cube_17;
    private final ModelPart leg2;
    private final ModelPart cube_18;
    private final ModelPart terrain;
    private final ModelPart cube_19;
    private final ModelPart floorflower;
    private final ModelPart cube_20;
    private final ModelPart explosion;
    private final ModelPart cube_21;
    private static final float STAND_BY_ANIMATION_LENGTH = 12.0833F;
    private static final float[] STAND_BY_ANIMATION_TESTA_TRANSLATION_TIMES = new float[]{0.0F};
    private static final float[] STAND_BY_ANIMATION_TESTA_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] STAND_BY_ANIMATION_TESTA_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] STAND_BY_ANIMATION_TESTA_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] STAND_BY_ANIMATION_FLOWERB1_SCALE_TIMES = new float[]{0.0F};
    private static final float[] STAND_BY_ANIMATION_FLOWERB1_SCALE_VALUES = new float[]{1.0F, 1.0F, 1.0F};
    private static final float[] STAND_BY_ANIMATION_FLOWERB2_SCALE_TIMES = new float[]{0.0F};
    private static final float[] STAND_BY_ANIMATION_FLOWERB2_SCALE_VALUES = new float[]{1.0F, 1.0F, 1.0F};
    private static final float[] STAND_BY_ANIMATION_FLOWERB4_SCALE_TIMES = new float[]{0.0F};
    private static final float[] STAND_BY_ANIMATION_FLOWERB4_SCALE_VALUES = new float[]{1.0F, 1.0F, 1.0F};
    private static final float[] STAND_BY_ANIMATION_FLOWERB5_SCALE_TIMES = new float[]{0.0F};
    private static final float[] STAND_BY_ANIMATION_FLOWERB5_SCALE_VALUES = new float[]{1.0F, 1.0F, 1.0F};
    private static final float[] STAND_BY_ANIMATION_FLOWERB7_SCALE_TIMES = new float[]{0.0F};
    private static final float[] STAND_BY_ANIMATION_FLOWERB7_SCALE_VALUES = new float[]{1.0F, 1.0F, 1.0F};
    private static final float[] STAND_BY_ANIMATION_FLOWERB8_SCALE_TIMES = new float[]{0.0F};
    private static final float[] STAND_BY_ANIMATION_FLOWERB8_SCALE_VALUES = new float[]{1.0F, 1.0F, 1.0F};
    private static final float[] STAND_BY_ANIMATION_ARMS_TRANSLATION_TIMES = new float[]{0.0F};
    private static final float[] STAND_BY_ANIMATION_ARMS_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] STAND_BY_ANIMATION_ARMS_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] STAND_BY_ANIMATION_ARMS_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float WALKING_ANIMATION_LENGTH = 5.0F;
    private static final float[] WALKING_ANIMATION_TERRAIN_SCALE_TIMES = new float[]{0.0F};
    private static final float[] WALKING_ANIMATION_TERRAIN_SCALE_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] WALKING_ANIMATION_TESTA_TRANSLATION_TIMES = new float[]{0.0F};
    private static final float[] WALKING_ANIMATION_TESTA_TRANSLATION_VALUES = new float[]{0.0F, 1.0F, 0.0F};
    private static final float[] WALKING_ANIMATION_TESTA_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] WALKING_ANIMATION_TESTA_ROTATION_VALUES = new float[]{-0.1309F, 0.0F, 0.0F};
    private static final float[] WALKING_ANIMATION_LEG1_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] WALKING_ANIMATION_LEG1_ROTATION_VALUES = new float[]{-0.3491F, 0.0F, 0.0F};
    private static final float[] WALKING_ANIMATION_LEG2_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] WALKING_ANIMATION_LEG2_ROTATION_VALUES = new float[]{0.3491F, 0.0F, 0.0F};
    private static final float[] WALKING_ANIMATION_ARMS_TRANSLATION_TIMES = new float[]{0.0F, 0.125F, 0.25F, 0.375F, 0.5F, 0.625F, 0.75F, 0.875F, 1.0F, 1.125F, 1.25F, 1.375F, 1.5F, 1.625F, 1.75F, 1.875F, 2.0F, 2.125F, 2.25F, 2.625F, 2.75F, 2.875F, 3.0F, 3.125F, 3.25F, 3.375F, 3.5F, 3.625F, 3.75F, 3.875F, 4.0F, 4.125F, 4.25F, 4.375F, 4.5F, 4.625F, 4.75F, 5.0F};
    private static final float[] WALKING_ANIMATION_ARMS_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F};
    private static final float[] WALKING_ANIMATION_ARM1_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] WALKING_ANIMATION_ARM1_ROTATION_VALUES = new float[]{0.2618F, 0.0F, 0.0F};
    private static final float[] WALKING_ANIMATION_ARM2_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] WALKING_ANIMATION_ARM2_ROTATION_VALUES = new float[]{0.2618F, 0.0F, 0.0F};
    private static final float SPORE_ATTACK_ANIMATION_LENGTH = 10.4583F;
    private static final float[] SPORE_ATTACK_ANIMATION_TESTA_TRANSLATION_TIMES = new float[]{0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_TESTA_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_TESTA_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_TESTA_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_FLOWERB1_SCALE_TIMES = new float[]{0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_FLOWERB1_SCALE_VALUES = new float[]{1.0F, 1.0F, 1.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_FLOWERB2_TRANSLATION_TIMES = new float[]{0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_FLOWERB2_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_FLOWERB2_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_FLOWERB2_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_FLOWERB2_SCALE_TIMES = new float[]{0.0F, 8.9583F, 9.0417F, 9.125F, 9.2083F, 9.25F, 9.3333F, 9.4167F, 9.5F, 9.5833F, 9.6667F, 9.75F, 9.8333F, 9.9167F, 10.0F, 10.0833F, 10.1667F, 10.2083F, 10.3333F, 10.4167F, 10.4583F};
    private static final float[] SPORE_ATTACK_ANIMATION_FLOWERB2_SCALE_VALUES = new float[]{1.0F, 1.0F, 1.0F, 0.9375F, 0.9375F, 0.9375F, 0.8125F, 0.8125F, 0.8125F, 0.6875F, 0.6875F, 0.6875F, 0.5625F, 0.5625F, 0.5625F, 0.5F, 0.5F, 0.5F, 0.375F, 0.375F, 0.375F, 0.25F, 0.25F, 0.25F, 0.125F, 0.125F, 0.125F, 0.0F, 0.0F, 0.0F, -0.052F, -0.052F, -0.052F, -0.0885F, -0.0885F, -0.0885F, -0.099F, -0.099F, -0.099F, -0.073F, -0.073F, -0.073F, 0.0F, 0.0F, 0.0F, 0.3789F, 0.3789F, 0.3789F, 0.8518F, 0.8518F, 0.8518F, 1.0F, 1.0F, 1.0F, 0.94F, 0.94F, 0.94F, 0.8393F, 0.8393F, 0.8393F, 0.8F, 0.8F, 0.8F};
    private static final float[] SPORE_ATTACK_ANIMATION_FLOWERB4_SCALE_TIMES = new float[]{0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_FLOWERB4_SCALE_VALUES = new float[]{1.0F, 1.0F, 1.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_FLOWERB5_SCALE_TIMES = new float[]{0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_FLOWERB5_SCALE_VALUES = new float[]{1.0F, 1.0F, 1.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_FLOWERB7_SCALE_TIMES = new float[]{0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_FLOWERB7_SCALE_VALUES = new float[]{1.0F, 1.0F, 1.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_FLOWERB8_SCALE_TIMES = new float[]{0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_FLOWERB8_SCALE_VALUES = new float[]{1.0F, 1.0F, 1.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_ARMS_TRANSLATION_TIMES = new float[]{0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_ARMS_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_ARMS_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_ARMS_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_ARM1_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_ARM1_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_ARM2_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_ARM2_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_EXPLOSION_TRANSLATION_TIMES = new float[]{0.0F, 0.2083F, 0.4583F, 0.7917F, 1.0833F, 1.2917F, 1.4583F, 1.625F, 1.8333F, 2.0F, 2.3333F, 2.75F, 3.0833F, 3.4167F, 3.8333F, 4.125F, 4.2917F, 4.5F, 4.6667F, 4.8333F, 5.0417F, 5.2083F, 5.375F, 5.5833F, 5.75F, 5.9167F, 6.125F, 6.2917F, 6.4583F, 6.6667F, 6.8333F, 7.0F, 7.2083F, 7.375F, 7.5417F, 7.75F, 7.9167F, 8.25F, 8.625F, 8.9583F};
    private static final float[] SPORE_ATTACK_ANIMATION_EXPLOSION_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.0003F, 0.0003F, -1.2349F, -0.0015F, 0.0015F, -2.9349F, -0.0042F, 0.0042F, -5.5468F, -0.0075F, 0.0075F, -8.1299F, -0.0103F, 0.0103F, -10.1308F, -0.0129F, 0.0129F, -11.8183F, -0.0156F, 0.0156F, -13.5781F, -0.0192F, 0.0192F, -15.8724F, -0.0223F, 0.0223F, -17.7781F, -0.0287F, 0.0287F, -21.7575F, -0.037F, 0.037F, -27.0F, -0.0437F, 0.0437F, -31.3645F, -0.0502F, 0.0502F, -35.8414F, -0.0578F, 0.0578F, -41.5403F, -0.0625F, 0.0625F, -45.5625F, -0.0649F, 0.0649F, -47.8621F, -0.0676F, 0.0676F, -50.7295F, -0.0695F, 0.0695F, -53.0122F, -0.0711F, 0.0711F, -55.2799F, -0.0726F, 0.0726F, -58.0867F, -0.0735F, 0.0735F, -60.3043F, -0.074F, 0.074F, -62.4923F, -0.074F, 0.074F, -65.1787F, -0.0736F, 0.0736F, -67.2835F, -0.0727F, 0.0727F, -69.3439F, -0.071F, 0.071F, -71.8503F, -0.069F, 0.069F, -73.7944F, -0.0665F, 0.0665F, -75.6795F, -0.0627F, 0.0627F, -77.9459F, -0.0589F, 0.0589F, -79.6816F, -0.0545F, 0.0545F, -81.3435F, -0.0482F, 0.0482F, -83.3103F, -0.0424F, 0.0424F, -84.7897F, -0.0359F, 0.0359F, -86.1807F, -0.0267F, 0.0267F, -87.788F, -0.0186F, 0.0186F, -88.9634F, 0.0F, 0.0F, -91.0F, 0.5367F, -0.5367F, -95.7977F, 1.0F, -1.0F, -90.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_EXPLOSION_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_EXPLOSION_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_EXPLOSION_SCALE_TIMES = new float[]{0.0F};
    private static final float[] SPORE_ATTACK_ANIMATION_EXPLOSION_SCALE_VALUES = new float[]{0.0F, 0.0F, 0.0F};

    public GrasslingSprederModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.node_44 = this.root.getChild("node_44");
        this.tuttoilcorpo = this.node_44.getChild("tuttoilcorpo");
        this.testa = this.tuttoilcorpo.getChild("testa");
        this.cube = this.testa.getChild("cube");
        this.fowerbomb = this.testa.getChild("fowerbomb");
        this.flowerB1 = this.fowerbomb.getChild("flowerB1");
        this.cube_2 = this.flowerB1.getChild("cube_2");
        this.flowerB2 = this.fowerbomb.getChild("flowerB2");
        this.cube_3 = this.flowerB2.getChild("cube_3");
        this.flowerB3 = this.fowerbomb.getChild("flowerB3");
        this.cube_4 = this.flowerB3.getChild("cube_4");
        this.flowerB4 = this.fowerbomb.getChild("flowerB4");
        this.cube_5 = this.flowerB4.getChild("cube_5");
        this.flowerB5 = this.fowerbomb.getChild("flowerB5");
        this.cube_6 = this.flowerB5.getChild("cube_6");
        this.flowerB6 = this.fowerbomb.getChild("flowerB6");
        this.cube_7 = this.flowerB6.getChild("cube_7");
        this.flowerB7 = this.fowerbomb.getChild("flowerB7");
        this.cube_8 = this.flowerB7.getChild("cube_8");
        this.flowerB8 = this.fowerbomb.getChild("flowerB8");
        this.cube_9 = this.flowerB8.getChild("cube_9");
        this.flowerB9 = this.fowerbomb.getChild("flowerB9");
        this.cube_10 = this.flowerB9.getChild("cube_10");
        this.flowerB10 = this.fowerbomb.getChild("flowerB10");
        this.cube_11 = this.flowerB10.getChild("cube_11");
        this.flowerB11 = this.fowerbomb.getChild("flowerB11");
        this.cube_12 = this.flowerB11.getChild("cube_12");
        this.arms = this.testa.getChild("arms");
        this.arm1 = this.arms.getChild("arm1");
        this.cube_13 = this.arm1.getChild("cube_13");
        this.cube_14 = this.arm1.getChild("cube_14");
        this.arm2 = this.arms.getChild("arm2");
        this.cube_15 = this.arm2.getChild("cube_15");
        this.cube_16 = this.arm2.getChild("cube_16");
        this.legs = this.tuttoilcorpo.getChild("legs");
        this.leg1 = this.legs.getChild("leg1");
        this.cube_17 = this.leg1.getChild("cube_17");
        this.leg2 = this.legs.getChild("leg2");
        this.cube_18 = this.leg2.getChild("cube_18");
        this.terrain = this.node_44.getChild("terrain");
        this.cube_19 = this.terrain.getChild("cube_19");
        this.floorflower = this.terrain.getChild("floorflower");
        this.cube_20 = this.floorflower.getChild("cube_20");
        this.explosion = this.node_44.getChild("explosion");
        this.cube_21 = this.explosion.getChild("cube_21");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition node_44 = root.addOrReplaceChild("node_44", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition tuttoilcorpo = node_44.addOrReplaceChild("tuttoilcorpo", CubeListBuilder.create(), PartPose.offset(1.0F, 0.0F, -6.0F));
        PartDefinition testa = tuttoilcorpo.addOrReplaceChild("testa", CubeListBuilder.create(), PartPose.offset(0.0F, -15.0F, 0.0F));
        PartDefinition cube = testa.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -25.0F, -9.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 15.0F, 0.0F, 3.098F, 0.0F, 3.1416F));
        PartDefinition fowerbomb = testa.addOrReplaceChild("fowerbomb", CubeListBuilder.create(), PartPose.offset(0.0F, 15.0F, 0.0F));
        PartDefinition flowerB1 = fowerbomb.addOrReplaceChild("flowerB1", CubeListBuilder.create(), PartPose.offset(8.0F, -22.0F, 6.0F));
        PartDefinition cube_2 = flowerB1.addOrReplaceChild("cube_2", CubeListBuilder.create().texOffs(66, 0).addBox(-9.0F, -26.0F, -10.0F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, 22.0F, -6.0F, -3.1037F, -0.1327F, -3.0976F));
        PartDefinition flowerB2 = fowerbomb.addOrReplaceChild("flowerB2", CubeListBuilder.create(), PartPose.offset(0.0F, -26.0F, 5.0F));
        PartDefinition cube_3 = flowerB2.addOrReplaceChild("cube_3", CubeListBuilder.create().texOffs(96, 0).addBox(-6.0F, -30.0F, -10.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 26.0F, -5.0F, -3.1416F, 0.0F, -3.1416F));
        PartDefinition flowerB3 = fowerbomb.addOrReplaceChild("flowerB3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition cube_4 = flowerB3.addOrReplaceChild("cube_4", CubeListBuilder.create().texOffs(138, 0).addBox(-3.0F, -29.0F, -8.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, -3.0107F, 0.0F, 2.9234F));
        PartDefinition flowerB4 = fowerbomb.addOrReplaceChild("flowerB4", CubeListBuilder.create(), PartPose.offset(5.0F, -23.0F, -7.0F));
        PartDefinition cube_5 = flowerB4.addOrReplaceChild("cube_5", CubeListBuilder.create().texOffs(172, 0).addBox(-7.0F, -27.0F, 3.0F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 23.0F, 7.0F, 3.0918F, -0.0019F, -3.0927F));
        PartDefinition flowerB5 = fowerbomb.addOrReplaceChild("flowerB5", CubeListBuilder.create(), PartPose.offset(-6.0F, -22.0F, -7.0F));
        PartDefinition cube_6 = flowerB5.addOrReplaceChild("cube_6", CubeListBuilder.create().texOffs(202, 0).addBox(-10.0F, -26.0F, 2.0F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 22.0F, 7.0F, 3.1006F, -0.0283F, 2.6238F));
        PartDefinition flowerB6 = fowerbomb.addOrReplaceChild("flowerB6", CubeListBuilder.create(), PartPose.offset(-6.0F, -23.0F, 0.0F));
        PartDefinition cube_7 = flowerB6.addOrReplaceChild("cube_7", CubeListBuilder.create().texOffs(232, 0).addBox(-5.0F, -26.0F, -12.0F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 23.0F, 0.0F, 2.4127F, 1.4168F, 2.5379F));
        PartDefinition flowerB7 = fowerbomb.addOrReplaceChild("flowerB7", CubeListBuilder.create(), PartPose.offset(-1.0F, -27.0F, -5.0F));
        PartDefinition cube_8 = flowerB7.addOrReplaceChild("cube_8", CubeListBuilder.create().texOffs(262, 0).addBox(-4.0F, -22.0F, -23.0F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 27.0F, 5.0F, 2.138F, 0.0F, -3.1416F));
        PartDefinition flowerB8 = fowerbomb.addOrReplaceChild("flowerB8", CubeListBuilder.create(), PartPose.offset(8.0F, -23.0F, -1.0F));
        PartDefinition cube_9 = flowerB8.addOrReplaceChild("cube_9", CubeListBuilder.create().texOffs(300, 0).addBox(-3.0F, -28.0F, -4.0F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, 23.0F, 1.0F, 3.0923F, 0.0068F, -2.9184F));
        PartDefinition flowerB9 = fowerbomb.addOrReplaceChild("flowerB9", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition cube_10 = flowerB9.addOrReplaceChild("cube_10", CubeListBuilder.create().texOffs(330, 0).addBox(-2.0F, -13.0F, 12.0F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, -1.7017F, 0.0F, 2.9234F));
        PartDefinition flowerB10 = fowerbomb.addOrReplaceChild("flowerB10", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition cube_11 = flowerB10.addOrReplaceChild("cube_11", CubeListBuilder.create().texOffs(360, 0).addBox(-5.0F, -13.0F, 12.0F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, -1.7017F, 0.0F, -2.9234F));
        PartDefinition flowerB11 = fowerbomb.addOrReplaceChild("flowerB11", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition cube_12 = flowerB11.addOrReplaceChild("cube_12", CubeListBuilder.create().texOffs(390, 0).addBox(-4.0F, -12.0F, 13.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, -1.6986F, 0.0283F, 3.1398F));
        PartDefinition arms = testa.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, 0.0F));
        PartDefinition arm1 = arms.addOrReplaceChild("arm1", CubeListBuilder.create(), PartPose.offset(8.0F, 2.0F, 1.0F));
        PartDefinition cube_13 = arm1.addOrReplaceChild("cube_13", CubeListBuilder.create().texOffs(416, 0).addBox(-14.0F, -5.7417F, -12.952F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, 17.0F, -1.0F, 2.9671F, 0.0F, 3.1416F));
        PartDefinition cube_14 = arm1.addOrReplaceChild("cube_14", CubeListBuilder.create().texOffs(442, 0).addBox(-14.0F, -19.7417F, -6.952F, 6.0F, 20.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, 17.0F, -1.0F, 2.9671F, 0.0F, 3.1416F));
        PartDefinition arm2 = arms.addOrReplaceChild("arm2", CubeListBuilder.create(), PartPose.offset(-8.0F, 2.0F, 1.0F));
        PartDefinition cube_15 = arm2.addOrReplaceChild("cube_15", CubeListBuilder.create().texOffs(468, 0).addBox(8.0F, -19.7417F, -6.952F, 6.0F, 20.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 17.0F, -1.0F, 2.9671F, 0.0F, 3.1416F));
        PartDefinition cube_16 = arm2.addOrReplaceChild("cube_16", CubeListBuilder.create().texOffs(0, 34).addBox(8.0F, -5.7417F, -12.952F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 17.0F, -1.0F, 2.9671F, 0.0F, 3.1416F));
        PartDefinition legs = tuttoilcorpo.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0.0F, -9.0F, 0.0F));
        PartDefinition leg1 = legs.addOrReplaceChild("leg1", CubeListBuilder.create(), PartPose.offset(-4.0F, 0.0F, 0.0F));
        PartDefinition cube_17 = leg1.addOrReplaceChild("cube_17", CubeListBuilder.create().texOffs(26, 34).addBox(1.0F, -10.0F, -3.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 9.0F, 0.0F, -3.1416F, 0.0F, -3.1416F));
        PartDefinition leg2 = legs.addOrReplaceChild("leg2", CubeListBuilder.create(), PartPose.offset(4.0F, 0.0F, 0.0F));
        PartDefinition cube_18 = leg2.addOrReplaceChild("cube_18", CubeListBuilder.create().texOffs(52, 34).addBox(-7.0F, -10.0F, -3.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 9.0F, 0.0F, -3.1416F, 0.0F, -3.1416F));
        PartDefinition terrain = node_44.addOrReplaceChild("terrain", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition cube_19 = terrain.addOrReplaceChild("cube_19", CubeListBuilder.create().texOffs(78, 34).addBox(-47.0F, -1.001F, -47.0F, 94.0F, 0.001F, 94.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition floorflower = terrain.addOrReplaceChild("floorflower", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition cube_20 = floorflower.addOrReplaceChild("cube_20", CubeListBuilder.create().texOffs(0, 131).addBox(-47.0F, -0.001F, -47.0F, 94.0F, 0.001F, 94.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition explosion = node_44.addOrReplaceChild("explosion", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 2.0F));
        PartDefinition cube_21 = explosion.addOrReplaceChild("cube_21", CubeListBuilder.create().texOffs(378, 131).addBox(-25.0F, -26.0F, 1.0F, 49.0F, 47.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, -1.5708F, 0.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 512, 256);
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
        if (state.walkAnimationSpeed > 0.12F) {
            applyClipWALKING_ANIMATION(walkTimeSeconds);
        } else {
            applyClipSTAND_BY_ANIMATION(idleTimeSeconds);
        }
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    public void setAngles(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    private void applyClipSTAND_BY_ANIMATION(float time) {
        float wrappedTime = wrapAnimationTime(time, STAND_BY_ANIMATION_LENGTH);
        applyTranslationTrack(this.testa, STAND_BY_ANIMATION_TESTA_TRANSLATION_TIMES, STAND_BY_ANIMATION_TESTA_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.testa, STAND_BY_ANIMATION_TESTA_ROTATION_TIMES, STAND_BY_ANIMATION_TESTA_ROTATION_VALUES, wrappedTime);
        applyScaleTrack(this.flowerB1, STAND_BY_ANIMATION_FLOWERB1_SCALE_TIMES, STAND_BY_ANIMATION_FLOWERB1_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.flowerB2, STAND_BY_ANIMATION_FLOWERB2_SCALE_TIMES, STAND_BY_ANIMATION_FLOWERB2_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.flowerB4, STAND_BY_ANIMATION_FLOWERB4_SCALE_TIMES, STAND_BY_ANIMATION_FLOWERB4_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.flowerB5, STAND_BY_ANIMATION_FLOWERB5_SCALE_TIMES, STAND_BY_ANIMATION_FLOWERB5_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.flowerB7, STAND_BY_ANIMATION_FLOWERB7_SCALE_TIMES, STAND_BY_ANIMATION_FLOWERB7_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.flowerB8, STAND_BY_ANIMATION_FLOWERB8_SCALE_TIMES, STAND_BY_ANIMATION_FLOWERB8_SCALE_VALUES, wrappedTime);
        applyTranslationTrack(this.arms, STAND_BY_ANIMATION_ARMS_TRANSLATION_TIMES, STAND_BY_ANIMATION_ARMS_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.arms, STAND_BY_ANIMATION_ARMS_ROTATION_TIMES, STAND_BY_ANIMATION_ARMS_ROTATION_VALUES, wrappedTime);
    }

    private void applyClipWALKING_ANIMATION(float time) {
        float wrappedTime = wrapAnimationTime(time, WALKING_ANIMATION_LENGTH);
        applyScaleTrack(this.terrain, WALKING_ANIMATION_TERRAIN_SCALE_TIMES, WALKING_ANIMATION_TERRAIN_SCALE_VALUES, wrappedTime);
        applyTranslationTrack(this.testa, WALKING_ANIMATION_TESTA_TRANSLATION_TIMES, WALKING_ANIMATION_TESTA_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.testa, WALKING_ANIMATION_TESTA_ROTATION_TIMES, WALKING_ANIMATION_TESTA_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.leg1, WALKING_ANIMATION_LEG1_ROTATION_TIMES, WALKING_ANIMATION_LEG1_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.leg2, WALKING_ANIMATION_LEG2_ROTATION_TIMES, WALKING_ANIMATION_LEG2_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.arms, WALKING_ANIMATION_ARMS_TRANSLATION_TIMES, WALKING_ANIMATION_ARMS_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.arm1, WALKING_ANIMATION_ARM1_ROTATION_TIMES, WALKING_ANIMATION_ARM1_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.arm2, WALKING_ANIMATION_ARM2_ROTATION_TIMES, WALKING_ANIMATION_ARM2_ROTATION_VALUES, wrappedTime);
    }

    private void applyClipSPORE_ATTACK_ANIMATION(float time) {
        float wrappedTime = wrapAnimationTime(time, SPORE_ATTACK_ANIMATION_LENGTH);
        applyTranslationTrack(this.testa, SPORE_ATTACK_ANIMATION_TESTA_TRANSLATION_TIMES, SPORE_ATTACK_ANIMATION_TESTA_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.testa, SPORE_ATTACK_ANIMATION_TESTA_ROTATION_TIMES, SPORE_ATTACK_ANIMATION_TESTA_ROTATION_VALUES, wrappedTime);
        applyScaleTrack(this.flowerB1, SPORE_ATTACK_ANIMATION_FLOWERB1_SCALE_TIMES, SPORE_ATTACK_ANIMATION_FLOWERB1_SCALE_VALUES, wrappedTime);
        applyTranslationTrack(this.flowerB2, SPORE_ATTACK_ANIMATION_FLOWERB2_TRANSLATION_TIMES, SPORE_ATTACK_ANIMATION_FLOWERB2_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.flowerB2, SPORE_ATTACK_ANIMATION_FLOWERB2_ROTATION_TIMES, SPORE_ATTACK_ANIMATION_FLOWERB2_ROTATION_VALUES, wrappedTime);
        applyScaleTrack(this.flowerB2, SPORE_ATTACK_ANIMATION_FLOWERB2_SCALE_TIMES, SPORE_ATTACK_ANIMATION_FLOWERB2_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.flowerB4, SPORE_ATTACK_ANIMATION_FLOWERB4_SCALE_TIMES, SPORE_ATTACK_ANIMATION_FLOWERB4_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.flowerB5, SPORE_ATTACK_ANIMATION_FLOWERB5_SCALE_TIMES, SPORE_ATTACK_ANIMATION_FLOWERB5_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.flowerB7, SPORE_ATTACK_ANIMATION_FLOWERB7_SCALE_TIMES, SPORE_ATTACK_ANIMATION_FLOWERB7_SCALE_VALUES, wrappedTime);
        applyScaleTrack(this.flowerB8, SPORE_ATTACK_ANIMATION_FLOWERB8_SCALE_TIMES, SPORE_ATTACK_ANIMATION_FLOWERB8_SCALE_VALUES, wrappedTime);
        applyTranslationTrack(this.arms, SPORE_ATTACK_ANIMATION_ARMS_TRANSLATION_TIMES, SPORE_ATTACK_ANIMATION_ARMS_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.arms, SPORE_ATTACK_ANIMATION_ARMS_ROTATION_TIMES, SPORE_ATTACK_ANIMATION_ARMS_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.arm1, SPORE_ATTACK_ANIMATION_ARM1_ROTATION_TIMES, SPORE_ATTACK_ANIMATION_ARM1_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.arm2, SPORE_ATTACK_ANIMATION_ARM2_ROTATION_TIMES, SPORE_ATTACK_ANIMATION_ARM2_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.explosion, SPORE_ATTACK_ANIMATION_EXPLOSION_TRANSLATION_TIMES, SPORE_ATTACK_ANIMATION_EXPLOSION_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.explosion, SPORE_ATTACK_ANIMATION_EXPLOSION_ROTATION_TIMES, SPORE_ATTACK_ANIMATION_EXPLOSION_ROTATION_VALUES, wrappedTime);
        applyScaleTrack(this.explosion, SPORE_ATTACK_ANIMATION_EXPLOSION_SCALE_TIMES, SPORE_ATTACK_ANIMATION_EXPLOSION_SCALE_VALUES, wrappedTime);
    }
}

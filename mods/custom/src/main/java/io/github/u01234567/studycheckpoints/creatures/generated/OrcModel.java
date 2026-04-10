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
 * Creature id: orc
 */
public class OrcModel extends EntityModel<LivingEntityRenderState> {
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
    private static final float IDLE_LENGTH = 2.0F;
    private static final float[] IDLE_HEAD_ROTATION_TIMES = new float[]{0.0F, 1.0F, 2.0F};
    private static final float[] IDLE_HEAD_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] IDLE_RIGHT_ARM_ROTATION_TIMES = new float[]{0.0F, 1.0F, 2.0F};
    private static final float[] IDLE_RIGHT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F, 0.0F};
    private static final float[] IDLE_LEFT_ARM_ROTATION_TIMES = new float[]{0.0F, 1.0F, 2.0F};
    private static final float[] IDLE_LEFT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F, 0.0F};
    private static final float[] IDLE_CHEST_TOP_ROTATION_TIMES = new float[]{0.0F, 1.0F, 2.0F};
    private static final float[] IDLE_CHEST_TOP_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] IDLE_CHEST_BOTTOM_ROTATION_TIMES = new float[]{0.0F, 1.0F, 2.0F};
    private static final float[] IDLE_CHEST_BOTTOM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float WALK_LENGTH = 2.0F;
    private static final float[] WALK_RIGHT_ARM_TRANSLATION_TIMES = new float[]{0.0F, 0.6667F, 1.375F, 2.0F};
    private static final float[] WALK_RIGHT_ARM_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_RIGHT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.6667F, 1.375F, 2.0F};
    private static final float[] WALK_RIGHT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.6977F, -0.028F, -0.0102F, -0.6977F, 0.028F, -0.0102F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_LEFT_ARM_TRANSLATION_TIMES = new float[]{0.0F, 0.6667F, 1.375F, 2.0F};
    private static final float[] WALK_LEFT_ARM_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_LEFT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.6667F, 1.375F, 2.0F};
    private static final float[] WALK_LEFT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.6939F, -0.084F, 0.0304F, 0.6939F, 0.084F, 0.0304F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_RIGHT_LEG_ROTATION_TIMES = new float[]{0.0F, 0.6667F, 1.375F, 2.0F};
    private static final float[] WALK_RIGHT_LEG_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.3456F, 0.0472F, 0.0071F, 0.3463F, -0.042F, -0.0227F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_LEFT_LEG_ROTATION_TIMES = new float[]{0.0F, 0.6667F, 1.375F, 2.0F};
    private static final float[] WALK_LEFT_LEG_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.3456F, 0.0393F, 0.0376F, -0.3443F, -0.0497F, -0.0221F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_HEAD_TRANSLATION_TIMES = new float[]{0.0F, 0.6667F, 1.375F, 2.0F};
    private static final float[] WALK_HEAD_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_HEAD_ROTATION_TIMES = new float[]{0.0F, 0.6667F, 1.375F, 2.0F};
    private static final float[] WALK_HEAD_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_CHEST_TOP_ROTATION_TIMES = new float[]{0.0F, 0.6667F, 1.375F, 2.0F};
    private static final float[] WALK_CHEST_TOP_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.0436F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_CHEST_BOTTOM_ROTATION_TIMES = new float[]{0.0F, 0.6667F, 1.375F, 2.0F};
    private static final float[] WALK_CHEST_BOTTOM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_CHEST_TRANSLATION_TIMES = new float[]{0.0F, 0.6667F, 1.375F, 2.0F};
    private static final float[] WALK_CHEST_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float SPRINT_LENGTH = 1.5F;
    private static final float[] SPRINT_RIGHT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.5F, 1.0F, 1.5F};
    private static final float[] SPRINT_RIGHT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.6977F, -0.028F, -0.3593F, -0.6977F, 0.028F, -0.3593F, 0.0F, 0.0F, 0.0F};
    private static final float[] SPRINT_LEFT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.5F, 1.0F, 1.5F};
    private static final float[] SPRINT_LEFT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.6939F, -0.084F, 0.3795F, 0.6939F, 0.084F, 0.3795F, 0.0F, 0.0F, 0.0F};
    private static final float[] SPRINT_RIGHT_LEG_ROTATION_TIMES = new float[]{0.0F, 0.5F, 1.0F, 1.5F};
    private static final float[] SPRINT_RIGHT_LEG_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.3456F, 0.0472F, 0.0071F, 0.3463F, -0.042F, -0.0227F, 0.0F, 0.0F, 0.0F};
    private static final float[] SPRINT_LEFT_LEG_ROTATION_TIMES = new float[]{0.0F, 0.5F, 1.0F, 1.5F};
    private static final float[] SPRINT_LEFT_LEG_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.3456F, 0.0393F, 0.0376F, -0.3443F, -0.0497F, -0.0221F, 0.0F, 0.0F, 0.0F};
    private static final float[] SPRINT_HEAD_ROTATION_TIMES = new float[]{0.0F, 0.5F, 1.0F, 1.5F};
    private static final float[] SPRINT_HEAD_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] SPRINT_CHEST_TOP_ROTATION_TIMES = new float[]{0.0F, 0.5F, 1.0F, 1.5F};
    private static final float[] SPRINT_CHEST_TOP_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.0436F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] SPRINT_CHEST_BOTTOM_ROTATION_TIMES = new float[]{0.0F, 0.5F, 1.0F, 1.5F};
    private static final float[] SPRINT_CHEST_BOTTOM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float ATTACK_LENGTH = 0.5F;
    private static final float[] ATTACK_HEAD_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.5F};
    private static final float[] ATTACK_HEAD_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] ATTACK_CHEST_TOP_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.5F};
    private static final float[] ATTACK_CHEST_TOP_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] ATTACK_RIGHT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.5F};
    private static final float[] ATTACK_RIGHT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -2.6184F, 0.0218F, -0.0814F, 0.0F, 0.0F, 0.0F};
    private static final float[] ATTACK_LEFT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.5F};
    private static final float[] ATTACK_LEFT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -2.6217F, -0.0653F, 0.2444F, 0.0F, 0.0F, 0.0F};
    private static final float[] ATTACK_CHEST_BOTTOM_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.5F};
    private static final float[] ATTACK_CHEST_BOTTOM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] ATTACK_MOUTH_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.5F};
    private static final float[] ATTACK_MOUTH_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.3491F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float HURT_LENGTH = 0.5F;
    private static final float[] HURT_HEAD_ROTATION_TIMES = new float[]{0.0F, 0.0833F, 0.1667F, 0.25F, 0.3333F, 0.4167F, 0.5F};
    private static final float[] HURT_HEAD_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0873F, 0.2618F, 0.0F, 0.0873F, -0.2618F, 0.0F, 0.0873F, 0.2618F, 0.0F, 0.0873F, -0.2618F, 0.0F, 0.0873F, 0.2618F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] HURT_CHEST_TOP_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.5F};
    private static final float[] HURT_CHEST_TOP_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] HURT_CHEST_BOTTOM_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.5F};
    private static final float[] HURT_CHEST_BOTTOM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] HURT_RIGHT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.5F};
    private static final float[] HURT_RIGHT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0436F, -0.0019F, -0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] HURT_LEFT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.5F};
    private static final float[] HURT_LEFT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0433F, 0.0057F, 0.0001F, 0.0F, 0.0F, 0.0F};
    private static final float DEATH_LENGTH = 0.5F;
    private static final float[] DEATH_HEAD_TRANSLATION_TIMES = new float[]{0.0F, 0.5F};
    private static final float[] DEATH_HEAD_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 22.0F, 26.0F};
    private static final float[] DEATH_HEAD_ROTATION_TIMES = new float[]{0.0F, 0.5F};
    private static final float[] DEATH_HEAD_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.5236F};
    private static final float[] DEATH_CHEST_TRANSLATION_TIMES = new float[]{0.0F, 0.5F};
    private static final float[] DEATH_CHEST_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 16.0F, 24.0F};
    private static final float[] DEATH_CHEST_ROTATION_TIMES = new float[]{0.0F, 0.5F};
    private static final float[] DEATH_CHEST_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F};
    private static final float[] DEATH_RIGHT_ARM_TRANSLATION_TIMES = new float[]{0.0F, 0.5F};
    private static final float[] DEATH_RIGHT_ARM_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 20.0F, 26.0F};
    private static final float[] DEATH_RIGHT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.5F};
    private static final float[] DEATH_RIGHT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -1.4022F, -0.6551F, -0.0341F};
    private static final float[] DEATH_LEFT_ARM_TRANSLATION_TIMES = new float[]{0.0F, 0.5F};
    private static final float[] DEATH_LEFT_ARM_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 20.0F, 26.0F};
    private static final float[] DEATH_LEFT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.5F};
    private static final float[] DEATH_LEFT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -1.4122F, 0.569F, 0.104F};
    private static final float[] DEATH_RIGHT_LEG_TRANSLATION_TIMES = new float[]{0.0F, 0.5F};
    private static final float[] DEATH_RIGHT_LEG_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 6.0F, 14.0F};
    private static final float[] DEATH_RIGHT_LEG_ROTATION_TIMES = new float[]{0.0F, 0.5F};
    private static final float[] DEATH_RIGHT_LEG_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -1.4275F, -0.3576F, -0.0612F};
    private static final float[] DEATH_LEFT_LEG_TRANSLATION_TIMES = new float[]{0.0F, 0.5F};
    private static final float[] DEATH_LEFT_LEG_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 6.0F, 14.0F};
    private static final float[] DEATH_LEFT_LEG_ROTATION_TIMES = new float[]{0.0F, 0.5F};
    private static final float[] DEATH_LEFT_LEG_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -1.4441F, 0.3199F, 0.0171F};
    private static final float JUMP_LENGTH = 0.5F;
    private static final float[] JUMP_HEAD_ROTATION_TIMES = new float[]{0.0F, 0.1667F, 0.3333F, 0.5F};
    private static final float[] JUMP_HEAD_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] JUMP_CHEST_ROTATION_TIMES = new float[]{0.0F, 0.1667F, 0.3333F, 0.5F};
    private static final float[] JUMP_CHEST_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] JUMP_RIGHT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.1667F, 0.3333F, 0.5F};
    private static final float[] JUMP_RIGHT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.2616F, -0.0113F, -0.0015F, -0.8722F, 0.0334F, -0.0156F, 0.0F, 0.0F, 0.0F};
    private static final float[] JUMP_LEFT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.1667F, 0.3333F, 0.5F};
    private static final float[] JUMP_LEFT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.2597F, 0.0338F, 0.0044F, -0.8684F, -0.1002F, 0.0465F, 0.0F, 0.0F, 0.0F};
    private static final float[] JUMP_RIGHT_LEG_ROTATION_TIMES = new float[]{0.0F, 0.1667F, 0.3333F, 0.5F};
    private static final float[] JUMP_RIGHT_LEG_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.0864F, 0.0115F, 0.0033F, 0.0432F, -0.0056F, -0.002F, 0.0F, 0.0F, 0.0F};
    private static final float[] JUMP_LEFT_LEG_ROTATION_TIMES = new float[]{0.0F, 0.1667F, 0.3333F, 0.5F};
    private static final float[] JUMP_LEFT_LEG_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.0862F, -0.0117F, -0.0071F, 0.0431F, 0.0056F, 0.0039F, 0.0F, 0.0F, 0.0F};
    private static final float RIGHTCLICK_LENGTH = 0.5F;
    private static final float[] RIGHTCLICK_HEAD_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.5F};
    private static final float[] RIGHTCLICK_HEAD_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] RIGHTCLICK_MOUTH_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.5F};
    private static final float[] RIGHTCLICK_MOUTH_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] RIGHTCLICK_CHEST_TOP_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.5F};
    private static final float[] RIGHTCLICK_CHEST_TOP_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] RIGHTCLICK_CHEST_BOTTOM_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.5F};
    private static final float[] RIGHTCLICK_CHEST_BOTTOM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0436F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] RIGHTCLICK_RIGHT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.5F};
    private static final float[] RIGHTCLICK_RIGHT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F, 0.0F};
    private static final float[] RIGHTCLICK_LEFT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.5F};
    private static final float[] RIGHTCLICK_LEFT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2182F, 0.0F, 0.0F, 0.0F};

    public OrcModel(ModelPart root) {
        super(root);
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

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -26.6F, 0.0F));
        PartDefinition cube = head.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -15.4F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.6F, 1.0F, 0.1745F, 0.0F, 0.0F));
        PartDefinition cube_2 = head.addOrReplaceChild("cube_2", CubeListBuilder.create().texOffs(34, 0).addBox(-1.2F, -12.8F, -4.6F, 2.2F, 2.0F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.6F, 1.0F, 0.1745F, 0.0F, 0.0F));
        PartDefinition cube_3 = head.addOrReplaceChild("cube_3", CubeListBuilder.create().texOffs(48, 0).addBox(-5.0453F, -5.9657F, 1.9939F, 3.0F, 3.0F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.2F, -2.4F, -2.2F, 0.2533F, 0.7519F, 0.3622F));
        PartDefinition cube_4 = head.addOrReplaceChild("cube_4", CubeListBuilder.create().texOffs(58, 0).addBox(1.6453F, -6.0004F, 2.1969F, 3.0F, 3.0F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.6F, -2.4F, -2.0F, 0.2533F, -0.7519F, -0.3622F));
        PartDefinition mouth = head.addOrReplaceChild("mouth", CubeListBuilder.create(), PartPose.offset(0.0F, -1.2F, -3.0F));
        PartDefinition cube_5 = mouth.addOrReplaceChild("cube_5", CubeListBuilder.create().texOffs(68, 0).addBox(-4.4F, -1.4624F, -1.5693F, 8.8F, 3.4F, 3.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.2F, -1.0F, 0.1745F, 0.0F, 0.0F));
        PartDefinition cube_6 = mouth.addOrReplaceChild("cube_6", CubeListBuilder.create().texOffs(96, 0).addBox(2.0F, -2.4624F, -0.9693F, 1.0F, 1.0F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.2F, -1.0F, 0.1745F, 0.0F, 0.0F));
        PartDefinition cube_7 = mouth.addOrReplaceChild("cube_7", CubeListBuilder.create().texOffs(102, 0).addBox(-3.2F, -2.4624F, -0.9693F, 1.0F, 1.0F, 0.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.2F, -1.0F, 0.1745F, 0.0F, 0.0F));
        PartDefinition chest = root.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -22.0F, 0.0F, 0.1745F, 0.0F, 0.0F));
        PartDefinition chest_top = chest.addOrReplaceChild("chest_top", CubeListBuilder.create(), PartPose.offset(0.0F, 2.0F, 0.0F));
        PartDefinition cube_8 = chest_top.addOrReplaceChild("cube_8", CubeListBuilder.create().texOffs(0, 18).addBox(-7.0F, -8.1909F, -2.8958F, 14.0F, 9.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.4F, 1.0F));
        PartDefinition chest_bottom = chest.addOrReplaceChild("chest_bottom", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 0.0F));
        PartDefinition cube_9 = chest_bottom.addOrReplaceChild("cube_9", CubeListBuilder.create().texOffs(46, 18).addBox(-7.0F, -20.1909F, -2.8958F, 14.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.4F, 0.0F));
        PartDefinition cube_10 = chest_bottom.addOrReplaceChild("cube_10", CubeListBuilder.create().texOffs(0, 37).addBox(-7.4F, -17.5909F, -2.2958F, 14.8F, 4.0F, 9.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.4F, -1.0F));
        PartDefinition cube_11 = chest_bottom.addOrReplaceChild("cube_11", CubeListBuilder.create().texOffs(52, 37).addBox(-2.4F, -19.5909F, -2.2958F, 4.8F, 2.0F, 0.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.4F, -1.0F));
        PartDefinition cube_12 = chest_bottom.addOrReplaceChild("cube_12", CubeListBuilder.create().texOffs(66, 37).addBox(-1.0F, -18.9909F, -2.6958F, 2.0F, 3.4F, 0.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.4F, -1.0F));
        PartDefinition cube_13 = chest_bottom.addOrReplaceChild("cube_13", CubeListBuilder.create().texOffs(74, 37).addBox(-1.4F, -18.9909F, -3.0958F, 2.8F, 2.0F, 0.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.4F, -1.0F));
        PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(7.8F, -25.8F, 1.0F, -0.1745F, 0.0F, -0.0436F));
        PartDefinition cube_14 = right_arm.addOrReplaceChild("cube_14", CubeListBuilder.create().texOffs(84, 37).addBox(4.7812F, -4.9192F, -2.4189F, 4.8F, 6.0F, 4.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, 8.2F, 1.0F, 0.0F, 0.0F, -0.0873F));
        PartDefinition cube_15 = right_arm.addOrReplaceChild("cube_15", CubeListBuilder.create().texOffs(0, 53).addBox(4.1812F, 1.0605F, -2.8792F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, 8.2F, 1.0F, 0.0F, 0.0F, -0.0873F));
        PartDefinition cube_16 = right_arm.addOrReplaceChild("cube_16", CubeListBuilder.create().texOffs(26, 53).addBox(3.7809F, 1.6142F, -3.2792F, 6.8F, 4.0F, 6.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, 8.2F, 1.0F, 0.0F, 0.0F, -0.0873F));
        PartDefinition cube_17 = right_arm.addOrReplaceChild("cube_17", CubeListBuilder.create().texOffs(56, 53).addBox(4.1812F, -10.5192F, -3.0189F, 6.0F, 5.6F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, 8.2F, 1.0F, 0.0F, 0.0F, -0.0873F));
        PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(-7.8F, -25.8F, 1.0F, -0.1745F, 0.0F, 0.1309F));
        PartDefinition cube_18 = left_arm.addOrReplaceChild("cube_18", CubeListBuilder.create().texOffs(82, 53).addBox(-9.5645F, -4.7199F, -2.4181F, 4.8F, 6.0F, 4.8F, new CubeDeformation(0.0F)), PartPose.offset(5.1243F, 7.6287F, 1.0F));
        PartDefinition cube_19 = left_arm.addOrReplaceChild("cube_19", CubeListBuilder.create().texOffs(104, 53).addBox(-10.1645F, 1.2597F, -2.8784F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.1243F, 7.6287F, 1.0F));
        PartDefinition cube_20 = left_arm.addOrReplaceChild("cube_20", CubeListBuilder.create().texOffs(0, 67).addBox(-10.5643F, 2.0134F, -3.2698F, 6.8F, 4.0F, 6.8F, new CubeDeformation(0.0F)), PartPose.offset(5.1243F, 7.6287F, 1.0F));
        PartDefinition cube_21 = left_arm.addOrReplaceChild("cube_21", CubeListBuilder.create().texOffs(30, 67).addBox(-10.1645F, -10.3199F, -3.0181F, 6.0F, 5.6F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.1243F, 7.6287F, 1.0F));
        PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(3.4F, -12.0F, 0.0F, 0.0F, 0.0436F, -0.1309F));
        PartDefinition cube_22 = right_leg.addOrReplaceChild("cube_22", CubeListBuilder.create().texOffs(56, 67).addBox(-3.1733F, 2.4091F, -0.2017F, 6.8F, 4.6F, 6.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6611F, 4.9829F, 0.0F, 0.0F, -0.0436F, 0.1309F));
        PartDefinition cube_23 = right_leg.addOrReplaceChild("cube_23", CubeListBuilder.create().texOffs(86, 67).addBox(-1.7648F, -2.3758F, 0.1981F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0611F, -0.0171F, 0.0F, 0.0F, -0.0436F, 0.1309F));
        PartDefinition cube_24 = right_leg.addOrReplaceChild("cube_24", CubeListBuilder.create().texOffs(0, 85).addBox(-3.1733F, -2.5909F, -0.2017F, 6.8F, 4.0F, 6.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6611F, 4.9829F, 0.0F, 0.0F, -0.0436F, 0.1309F));
        PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.2F, -12.0F, 0.0F, 0.0F, -0.0873F, 0.1309F));
        PartDefinition cube_25 = left_leg.addOrReplaceChild("cube_25", CubeListBuilder.create().texOffs(30, 85).addBox(-4.2361F, 1.0205F, -0.2032F, 6.8F, 4.6F, 6.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.2576F, 6.3307F, 0.0F, 0.0F, 0.0873F, -0.1309F));
        PartDefinition cube_26 = left_leg.addOrReplaceChild("cube_26", CubeListBuilder.create().texOffs(60, 85).addBox(-4.236F, -2.3644F, 0.198F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8576F, -0.0693F, 0.0F, 0.0F, 0.0873F, -0.1309F));
        PartDefinition cube_27 = left_leg.addOrReplaceChild("cube_27", CubeListBuilder.create().texOffs(86, 85).addBox(-4.2361F, -4.0273F, -0.2019F, 6.8F, 4.0F, 6.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.2576F, 6.3307F, 0.0F, 0.0F, 0.0873F, -0.1309F));

        return LayerDefinition.create(meshDefinition, 128, 128);
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
            applyClipWALK(walkTimeSeconds);
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
        applyRotationTrack(this.head, IDLE_HEAD_ROTATION_TIMES, IDLE_HEAD_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_arm, IDLE_RIGHT_ARM_ROTATION_TIMES, IDLE_RIGHT_ARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_arm, IDLE_LEFT_ARM_ROTATION_TIMES, IDLE_LEFT_ARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.chest_top, IDLE_CHEST_TOP_ROTATION_TIMES, IDLE_CHEST_TOP_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.chest_bottom, IDLE_CHEST_BOTTOM_ROTATION_TIMES, IDLE_CHEST_BOTTOM_ROTATION_VALUES, wrappedTime);
    }

    private void applyClipWALK(float time) {
        float wrappedTime = wrapAnimationTime(time, WALK_LENGTH);
        applyTranslationTrack(this.right_arm, WALK_RIGHT_ARM_TRANSLATION_TIMES, WALK_RIGHT_ARM_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_arm, WALK_RIGHT_ARM_ROTATION_TIMES, WALK_RIGHT_ARM_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.left_arm, WALK_LEFT_ARM_TRANSLATION_TIMES, WALK_LEFT_ARM_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_arm, WALK_LEFT_ARM_ROTATION_TIMES, WALK_LEFT_ARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_leg, WALK_RIGHT_LEG_ROTATION_TIMES, WALK_RIGHT_LEG_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_leg, WALK_LEFT_LEG_ROTATION_TIMES, WALK_LEFT_LEG_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.head, WALK_HEAD_TRANSLATION_TIMES, WALK_HEAD_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.head, WALK_HEAD_ROTATION_TIMES, WALK_HEAD_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.chest_top, WALK_CHEST_TOP_ROTATION_TIMES, WALK_CHEST_TOP_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.chest_bottom, WALK_CHEST_BOTTOM_ROTATION_TIMES, WALK_CHEST_BOTTOM_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.chest, WALK_CHEST_TRANSLATION_TIMES, WALK_CHEST_TRANSLATION_VALUES, wrappedTime);
    }

    private void applyClipSPRINT(float time) {
        float wrappedTime = wrapAnimationTime(time, SPRINT_LENGTH);
        applyRotationTrack(this.right_arm, SPRINT_RIGHT_ARM_ROTATION_TIMES, SPRINT_RIGHT_ARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_arm, SPRINT_LEFT_ARM_ROTATION_TIMES, SPRINT_LEFT_ARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_leg, SPRINT_RIGHT_LEG_ROTATION_TIMES, SPRINT_RIGHT_LEG_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_leg, SPRINT_LEFT_LEG_ROTATION_TIMES, SPRINT_LEFT_LEG_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.head, SPRINT_HEAD_ROTATION_TIMES, SPRINT_HEAD_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.chest_top, SPRINT_CHEST_TOP_ROTATION_TIMES, SPRINT_CHEST_TOP_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.chest_bottom, SPRINT_CHEST_BOTTOM_ROTATION_TIMES, SPRINT_CHEST_BOTTOM_ROTATION_VALUES, wrappedTime);
    }

    private void applyClipATTACK(float time) {
        float wrappedTime = wrapAnimationTime(time, ATTACK_LENGTH);
        applyRotationTrack(this.head, ATTACK_HEAD_ROTATION_TIMES, ATTACK_HEAD_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.chest_top, ATTACK_CHEST_TOP_ROTATION_TIMES, ATTACK_CHEST_TOP_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_arm, ATTACK_RIGHT_ARM_ROTATION_TIMES, ATTACK_RIGHT_ARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_arm, ATTACK_LEFT_ARM_ROTATION_TIMES, ATTACK_LEFT_ARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.chest_bottom, ATTACK_CHEST_BOTTOM_ROTATION_TIMES, ATTACK_CHEST_BOTTOM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.mouth, ATTACK_MOUTH_ROTATION_TIMES, ATTACK_MOUTH_ROTATION_VALUES, wrappedTime);
    }

    private void applyClipHURT(float time) {
        float wrappedTime = wrapAnimationTime(time, HURT_LENGTH);
        applyRotationTrack(this.head, HURT_HEAD_ROTATION_TIMES, HURT_HEAD_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.chest_top, HURT_CHEST_TOP_ROTATION_TIMES, HURT_CHEST_TOP_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.chest_bottom, HURT_CHEST_BOTTOM_ROTATION_TIMES, HURT_CHEST_BOTTOM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_arm, HURT_RIGHT_ARM_ROTATION_TIMES, HURT_RIGHT_ARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_arm, HURT_LEFT_ARM_ROTATION_TIMES, HURT_LEFT_ARM_ROTATION_VALUES, wrappedTime);
    }

    private void applyClipDEATH(float time) {
        float wrappedTime = wrapAnimationTime(time, DEATH_LENGTH);
        applyTranslationTrack(this.head, DEATH_HEAD_TRANSLATION_TIMES, DEATH_HEAD_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.head, DEATH_HEAD_ROTATION_TIMES, DEATH_HEAD_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.chest, DEATH_CHEST_TRANSLATION_TIMES, DEATH_CHEST_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.chest, DEATH_CHEST_ROTATION_TIMES, DEATH_CHEST_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.right_arm, DEATH_RIGHT_ARM_TRANSLATION_TIMES, DEATH_RIGHT_ARM_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_arm, DEATH_RIGHT_ARM_ROTATION_TIMES, DEATH_RIGHT_ARM_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.left_arm, DEATH_LEFT_ARM_TRANSLATION_TIMES, DEATH_LEFT_ARM_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_arm, DEATH_LEFT_ARM_ROTATION_TIMES, DEATH_LEFT_ARM_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.right_leg, DEATH_RIGHT_LEG_TRANSLATION_TIMES, DEATH_RIGHT_LEG_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_leg, DEATH_RIGHT_LEG_ROTATION_TIMES, DEATH_RIGHT_LEG_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.left_leg, DEATH_LEFT_LEG_TRANSLATION_TIMES, DEATH_LEFT_LEG_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_leg, DEATH_LEFT_LEG_ROTATION_TIMES, DEATH_LEFT_LEG_ROTATION_VALUES, wrappedTime);
    }

    private void applyClipJUMP(float time) {
        float wrappedTime = wrapAnimationTime(time, JUMP_LENGTH);
        applyRotationTrack(this.head, JUMP_HEAD_ROTATION_TIMES, JUMP_HEAD_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.chest, JUMP_CHEST_ROTATION_TIMES, JUMP_CHEST_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_arm, JUMP_RIGHT_ARM_ROTATION_TIMES, JUMP_RIGHT_ARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_arm, JUMP_LEFT_ARM_ROTATION_TIMES, JUMP_LEFT_ARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_leg, JUMP_RIGHT_LEG_ROTATION_TIMES, JUMP_RIGHT_LEG_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_leg, JUMP_LEFT_LEG_ROTATION_TIMES, JUMP_LEFT_LEG_ROTATION_VALUES, wrappedTime);
    }

    private void applyClipRIGHTCLICK(float time) {
        float wrappedTime = wrapAnimationTime(time, RIGHTCLICK_LENGTH);
        applyRotationTrack(this.head, RIGHTCLICK_HEAD_ROTATION_TIMES, RIGHTCLICK_HEAD_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.mouth, RIGHTCLICK_MOUTH_ROTATION_TIMES, RIGHTCLICK_MOUTH_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.chest_top, RIGHTCLICK_CHEST_TOP_ROTATION_TIMES, RIGHTCLICK_CHEST_TOP_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.chest_bottom, RIGHTCLICK_CHEST_BOTTOM_ROTATION_TIMES, RIGHTCLICK_CHEST_BOTTOM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_arm, RIGHTCLICK_RIGHT_ARM_ROTATION_TIMES, RIGHTCLICK_RIGHT_ARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_arm, RIGHTCLICK_LEFT_ARM_ROTATION_TIMES, RIGHTCLICK_LEFT_ARM_ROTATION_VALUES, wrappedTime);
    }
}

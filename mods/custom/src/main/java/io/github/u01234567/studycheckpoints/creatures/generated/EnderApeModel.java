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
 * Creature id: ender_ape
 */
public class EnderApeModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart ender_ape;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart headshooter;
    private final ModelPart headshooter_main;
    private final ModelPart headshooter_core;
    private final ModelPart ears;
    private final ModelPart left_ear;
    private final ModelPart right_ear;
    private final ModelPart head_main;
    private final ModelPart hands;
    private final ModelPart lefthand;
    private final ModelPart left_hand_main;
    private final ModelPart righthand;
    private final ModelPart right_hand_main;
    private final ModelPart body_only;
    private final ModelPart body_main;
    private final ModelPart feet;
    private final ModelPart leftfoot;
    private final ModelPart left_foot_main;
    private final ModelPart rightfoot;
    private final ModelPart right_foot_main;
    private static final float SPAWN_LENGTH = 0.125F;
    private static final float[] SPAWN_HEAD_TRANSLATION_TIMES = new float[]{0.0F, 0.0833F};
    private static final float[] SPAWN_HEAD_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 2.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] SPAWN_HEAD_ROTATION_TIMES = new float[]{0.0F, 0.0833F};
    private static final float[] SPAWN_HEAD_ROTATION_VALUES = new float[]{-0.7854F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] SPAWN_HANDS_TRANSLATION_TIMES = new float[]{0.0F, 0.125F};
    private static final float[] SPAWN_HANDS_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F};
    private static final float DANCING_LENGTH = 0.5F;
    private static final float[] DANCING_BODY_ROTATION_TIMES = new float[]{0.0833F, 0.2083F, 0.3333F, 0.4583F};
    private static final float[] DANCING_BODY_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] DANCING_HEAD_TRANSLATION_TIMES = new float[]{0.0F, 0.125F, 0.375F, 0.5F};
    private static final float[] DANCING_HEAD_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.0F, 0.0F, 0.0F, 2.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] DANCING_EARS_TRANSLATION_TIMES = new float[]{0.0F, 0.125F, 0.375F, 0.5F};
    private static final float[] DANCING_EARS_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] DANCING_HANDS_TRANSLATION_TIMES = new float[]{0.0833F, 0.2083F, 0.3333F, 0.4583F};
    private static final float[] DANCING_HANDS_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, -2.0F, 2.0F, 0.0F, -2.0F, 2.0F, 0.0F, 0.0F, 0.0F};
    private static final float WALKING_LENGTH = 0.5F;
    private static final float[] WALKING_LEFTHAND_ROTATION_TIMES = new float[]{0.0F, 0.125F, 0.25F, 0.375F, 0.5F};
    private static final float[] WALKING_LEFTHAND_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.5236F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALKING_RIGHTHAND_ROTATION_TIMES = new float[]{0.0F, 0.125F, 0.25F, 0.375F, 0.5F};
    private static final float[] WALKING_RIGHTHAND_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALKING_LEFTFOOT_ROTATION_TIMES = new float[]{0.0F, 0.0417F, 0.0833F, 0.125F, 0.1667F, 0.2083F, 0.25F, 0.2917F, 0.3333F, 0.375F, 0.4167F, 0.4583F, 0.5F};
    private static final float[] WALKING_LEFTFOOT_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.5236F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALKING_RIGHTFOOT_ROTATION_TIMES = new float[]{0.0F, 0.0417F, 0.0833F, 0.125F, 0.1667F, 0.2083F, 0.25F, 0.2917F, 0.3333F, 0.375F, 0.4167F, 0.4583F, 0.5F};
    private static final float[] WALKING_RIGHTFOOT_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float TURNING_FALL_DAMAGE_TO_ATTACK_LENGTH = 0.5F;
    private static final float[] TURNING_FALL_DAMAGE_TO_ATTACK_BODY_ROTATION_TIMES = new float[]{0.0F, 0.125F, 0.3125F, 0.5F};
    private static final float[] TURNING_FALL_DAMAGE_TO_ATTACK_BODY_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F, -2.5525F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] TURNING_FALL_DAMAGE_TO_ATTACK_HEAD_ROTATION_TIMES = new float[]{0.0F, 0.25F, 0.4167F, 0.5F};
    private static final float[] TURNING_FALL_DAMAGE_TO_ATTACK_HEAD_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.7418F, 0.0F, 0.0F, -0.7418F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] TURNING_FALL_DAMAGE_TO_ATTACK_HANDS_ROTATION_TIMES = new float[]{0.0417F, 0.1667F, 0.3333F, 0.5F};
    private static final float[] TURNING_FALL_DAMAGE_TO_ATTACK_HANDS_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -3.098F, 0.0F, 0.0F, -3.098F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] TURNING_FALL_DAMAGE_TO_ATTACK_FEET_ROTATION_TIMES = new float[]{0.0F, 0.0833F, 0.2917F, 0.5F};
    private static final float[] TURNING_FALL_DAMAGE_TO_ATTACK_FEET_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 2.138F, 0.0F, 0.0F, -2.0726F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float ATTACK_LENGTH = 0.5F;
    private static final float[] ATTACK_BODY_ROTATION_TIMES = new float[]{0.0F, 0.1667F, 0.5F};
    private static final float[] ATTACK_BODY_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] ATTACK_HANDS_ROTATION_TIMES = new float[]{0.0F, 0.1667F, 0.5F};
    private static final float[] ATTACK_HANDS_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -1.9635F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] ATTACK_LEFTHAND_ROTATION_TIMES = new float[]{0.1667F, 0.3333F, 0.5F};
    private static final float[] ATTACK_LEFTHAND_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] ATTACK_RIGHTHAND_ROTATION_TIMES = new float[]{0.1667F, 0.3333F, 0.5F};
    private static final float[] ATTACK_RIGHTHAND_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float RANGED_ATTACK_LENGTH = 0.75F;
    private static final float[] RANGED_ATTACK_HEAD_TRANSLATION_TIMES = new float[]{0.0F, 0.0833F, 0.2083F, 0.375F, 0.4583F, 0.5417F, 0.6667F};
    private static final float[] RANGED_ATTACK_HEAD_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] RANGED_ATTACK_HANDS_ROTATION_TIMES = new float[]{0.0833F, 0.25F, 0.5F, 0.6667F, 0.75F};
    private static final float[] RANGED_ATTACK_HANDS_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.48F, 0.0F, 0.0F, -0.48F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] RANGED_ATTACK_HEADSHOOTER_TRANSLATION_TIMES = new float[]{0.0F, 0.125F, 0.625F, 0.75F};
    private static final float[] RANGED_ATTACK_HEADSHOOTER_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, -4.0F, 0.0F, 0.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.0F};

    public EnderApeModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.ender_ape = this.root.getChild("ender_ape");
        this.body = this.ender_ape.getChild("body");
        this.head = this.body.getChild("head");
        this.headshooter = this.head.getChild("headshooter");
        this.headshooter_main = this.headshooter.getChild("headshooter_main");
        this.headshooter_core = this.headshooter.getChild("headshooter_core");
        this.ears = this.head.getChild("ears");
        this.left_ear = this.ears.getChild("left_ear");
        this.right_ear = this.ears.getChild("right_ear");
        this.head_main = this.head.getChild("head_main");
        this.hands = this.body.getChild("hands");
        this.lefthand = this.hands.getChild("lefthand");
        this.left_hand_main = this.lefthand.getChild("left_hand_main");
        this.righthand = this.hands.getChild("righthand");
        this.right_hand_main = this.righthand.getChild("right_hand_main");
        this.body_only = this.body.getChild("body_only");
        this.body_main = this.body_only.getChild("body_main");
        this.feet = this.ender_ape.getChild("feet");
        this.leftfoot = this.feet.getChild("leftfoot");
        this.left_foot_main = this.leftfoot.getChild("left_foot_main");
        this.rightfoot = this.feet.getChild("rightfoot");
        this.right_foot_main = this.rightfoot.getChild("right_foot_main");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition ender_ape = root.addOrReplaceChild("ender_ape", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 2.0F));
        PartDefinition body = ender_ape.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 1.0472F, 0.0F, 0.0F));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -15.134F, 2.5F, -1.0472F, 0.0F, 0.0F));
        PartDefinition headshooter = head.addOrReplaceChild("headshooter", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 0.0F));
        PartDefinition headshooter_main = headshooter.addOrReplaceChild("headshooter_main", CubeListBuilder.create().texOffs(72, 0).addBox(-3.0F, -3.5F, -2.5F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition headshooter_core = headshooter.addOrReplaceChild("headshooter_core", CubeListBuilder.create().texOffs(96, 0).addBox(-1.0F, -7.5F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition ears = head.addOrReplaceChild("ears", CubeListBuilder.create(), PartPose.offset(0.0F, -1.5359F, -3.0F));
        PartDefinition left_ear = ears.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(104, 0).addBox(-3.0F, -26.0F, -1.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 24.0F, -1.0F));
        PartDefinition right_ear = ears.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(104, 8).addBox(6.0F, -26.0F, -1.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 24.0F, -1.0F));
        PartDefinition head_main = head.addOrReplaceChild("head_main", CubeListBuilder.create().texOffs(40, 0).addBox(-2.0F, -27.0F, -2.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 22.4641F, -4.0F));
        PartDefinition hands = body.addOrReplaceChild("hands", CubeListBuilder.create(), PartPose.offset(0.0F, -13.134F, 0.5F));
        PartDefinition lefthand = hands.addOrReplaceChild("lefthand", CubeListBuilder.create(), PartPose.offset(-6.0F, 1.134F, -0.5F));
        PartDefinition left_hand_main = lefthand.addOrReplaceChild("left_hand_main", CubeListBuilder.create().texOffs(0, 24).addBox(-12.0F, -3.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 0.0F, 0.0F, -1.0472F, 0.0F, 0.0F));
        PartDefinition righthand = hands.addOrReplaceChild("righthand", CubeListBuilder.create(), PartPose.offset(6.0F, 1.134F, -0.5F));
        PartDefinition right_hand_main = righthand.addOrReplaceChild("right_hand_main", CubeListBuilder.create().texOffs(16, 24).addBox(12.0F, -3.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, 0.0F, 0.0F, -1.0472F, 0.0F, 0.0F));
        PartDefinition body_only = body.addOrReplaceChild("body_only", CubeListBuilder.create(), PartPose.offset(-2.0F, 0.866F, 0.5F));
        PartDefinition body_main = body_only.addOrReplaceChild("body_main", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -16.0F, -2.0F, 12.0F, 16.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition feet = ender_ape.addOrReplaceChild("feet", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, 0.0F));
        PartDefinition leftfoot = feet.addOrReplaceChild("leftfoot", CubeListBuilder.create(), PartPose.offset(-3.0F, 0.0F, 0.0F));
        PartDefinition left_foot_main = leftfoot.addOrReplaceChild("left_foot_main", CubeListBuilder.create().texOffs(32, 24).addBox(-8.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 3.0F, 0.0F));
        PartDefinition rightfoot = feet.addOrReplaceChild("rightfoot", CubeListBuilder.create(), PartPose.offset(3.0F, 0.0F, 0.0F));
        PartDefinition right_foot_main = rightfoot.addOrReplaceChild("right_foot_main", CubeListBuilder.create().texOffs(40, 24).addBox(6.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 3.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 128, 64);
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
            applyClipWALKING(walkTimeSeconds);
        } else {
            applyClipSPAWN(idleTimeSeconds);
        }
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    public void setAngles(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    private void applyClipSPAWN(float time) {
        float wrappedTime = wrapAnimationTime(time, SPAWN_LENGTH);
        applyTranslationTrack(this.head, SPAWN_HEAD_TRANSLATION_TIMES, SPAWN_HEAD_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.head, SPAWN_HEAD_ROTATION_TIMES, SPAWN_HEAD_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.hands, SPAWN_HANDS_TRANSLATION_TIMES, SPAWN_HANDS_TRANSLATION_VALUES, wrappedTime);
    }

    private void applyClipDANCING(float time) {
        float wrappedTime = wrapAnimationTime(time, DANCING_LENGTH);
        applyRotationTrack(this.body, DANCING_BODY_ROTATION_TIMES, DANCING_BODY_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.head, DANCING_HEAD_TRANSLATION_TIMES, DANCING_HEAD_TRANSLATION_VALUES, wrappedTime);
        applyTranslationTrack(this.ears, DANCING_EARS_TRANSLATION_TIMES, DANCING_EARS_TRANSLATION_VALUES, wrappedTime);
        applyTranslationTrack(this.hands, DANCING_HANDS_TRANSLATION_TIMES, DANCING_HANDS_TRANSLATION_VALUES, wrappedTime);
    }

    private void applyClipWALKING(float time) {
        float wrappedTime = wrapAnimationTime(time, WALKING_LENGTH);
        applyRotationTrack(this.lefthand, WALKING_LEFTHAND_ROTATION_TIMES, WALKING_LEFTHAND_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.righthand, WALKING_RIGHTHAND_ROTATION_TIMES, WALKING_RIGHTHAND_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.leftfoot, WALKING_LEFTFOOT_ROTATION_TIMES, WALKING_LEFTFOOT_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.rightfoot, WALKING_RIGHTFOOT_ROTATION_TIMES, WALKING_RIGHTFOOT_ROTATION_VALUES, wrappedTime);
    }

    private void applyClipTURNING_FALL_DAMAGE_TO_ATTACK(float time) {
        float wrappedTime = wrapAnimationTime(time, TURNING_FALL_DAMAGE_TO_ATTACK_LENGTH);
        applyRotationTrack(this.body, TURNING_FALL_DAMAGE_TO_ATTACK_BODY_ROTATION_TIMES, TURNING_FALL_DAMAGE_TO_ATTACK_BODY_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.head, TURNING_FALL_DAMAGE_TO_ATTACK_HEAD_ROTATION_TIMES, TURNING_FALL_DAMAGE_TO_ATTACK_HEAD_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.hands, TURNING_FALL_DAMAGE_TO_ATTACK_HANDS_ROTATION_TIMES, TURNING_FALL_DAMAGE_TO_ATTACK_HANDS_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.feet, TURNING_FALL_DAMAGE_TO_ATTACK_FEET_ROTATION_TIMES, TURNING_FALL_DAMAGE_TO_ATTACK_FEET_ROTATION_VALUES, wrappedTime);
    }

    private void applyClipATTACK(float time) {
        float wrappedTime = wrapAnimationTime(time, ATTACK_LENGTH);
        applyRotationTrack(this.body, ATTACK_BODY_ROTATION_TIMES, ATTACK_BODY_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.hands, ATTACK_HANDS_ROTATION_TIMES, ATTACK_HANDS_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.lefthand, ATTACK_LEFTHAND_ROTATION_TIMES, ATTACK_LEFTHAND_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.righthand, ATTACK_RIGHTHAND_ROTATION_TIMES, ATTACK_RIGHTHAND_ROTATION_VALUES, wrappedTime);
    }

    private void applyClipRANGED_ATTACK(float time) {
        float wrappedTime = wrapAnimationTime(time, RANGED_ATTACK_LENGTH);
        applyTranslationTrack(this.head, RANGED_ATTACK_HEAD_TRANSLATION_TIMES, RANGED_ATTACK_HEAD_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.hands, RANGED_ATTACK_HANDS_ROTATION_TIMES, RANGED_ATTACK_HANDS_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.headshooter, RANGED_ATTACK_HEADSHOOTER_TRANSLATION_TIMES, RANGED_ATTACK_HEADSHOOTER_TRANSLATION_VALUES, wrappedTime);
    }
}

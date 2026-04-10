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
 * Creature id: mushroom_bup
 */
public class MushroomBupModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart bup;
    private final ModelPart body;
    private final ModelPart torso;
    private final ModelPart left_arm;
    private final ModelPart left_arm_cube;
    private final ModelPart right_arm;
    private final ModelPart right_arm_cube;
    private final ModelPart right_leg;
    private final ModelPart left_leg;
    private final ModelPart head;
    private final ModelPart hat;
    private static final float IDLE_LENGTH = 1.04F;
    private static final float[] IDLE_LEFT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.8F, 1.04F};
    private static final float[] IDLE_LEFT_ARM_ROTATION_VALUES = new float[]{0.0379F, -0.0007F, 0.0216F, -0.0436F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] IDLE_RIGHT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.32F, 1.04F};
    private static final float[] IDLE_RIGHT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.0436F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] IDLE_HAT_ROTATION_TIMES = new float[]{0.0F, 0.48F, 1.04F};
    private static final float[] IDLE_HAT_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0291F, 0.0F, 0.0146F, 0.0F, 0.0F, 0.0F};
    private static final float DEATH_LENGTH = 0.72F;
    private static final float[] DEATH_BUP_TRANSLATION_TIMES = new float[]{0.0F, 0.04F, 0.12F, 0.72F};
    private static final float[] DEATH_BUP_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.0F, -1.25F, 0.0F, 2.0F, -1.25F};
    private static final float[] DEATH_BUP_ROTATION_TIMES = new float[]{0.0F, 0.04F, 0.12F, 0.72F};
    private static final float[] DEATH_BUP_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7418F, 0.0F, 0.0F, 0.7418F, 0.0F, 0.0F};
    private static final float[] DEATH_HAT_TRANSLATION_TIMES = new float[]{0.0F, 0.04F};
    private static final float[] DEATH_HAT_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 5.0F, 0.0F};
    private static final float WALK_LENGTH = 1.04F;
    private static final float[] WALK_HEAD_TRANSLATION_TIMES = new float[]{0.0F, 0.28F, 0.52F, 0.76F, 1.04F};
    private static final float[] WALK_HEAD_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_HEAD_ROTATION_TIMES = new float[]{0.0F, 0.28F, 0.52F, 0.76F, 1.04F};
    private static final float[] WALK_HEAD_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0436F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0436F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_LEFT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.24F, 0.52F, 0.84F, 1.04F};
    private static final float[] WALK_LEFT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0623F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_LEFT_LEG_TRANSLATION_TIMES = new float[]{0.0F, 0.24F, 0.52F, 0.76F, 1.04F};
    private static final float[] WALK_LEFT_LEG_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_LEFT_LEG_ROTATION_TIMES = new float[]{0.0F, 0.24F, 0.52F, 0.76F, 1.04F};
    private static final float[] WALK_LEFT_LEG_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.0623F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_TORSO_ROTATION_TIMES = new float[]{0.0F, 0.2F, 1.04F};
    private static final float[] WALK_TORSO_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0436F, 0.0019F, -0.0436F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_TORSO_SCALE_TIMES = new float[]{0.0F};
    private static final float[] WALK_TORSO_SCALE_VALUES = new float[]{0.9F, 1.0F, 1.0F};
    private static final float[] WALK_RIGHT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.28F, 0.52F, 0.76F, 1.04F};
    private static final float[] WALK_RIGHT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.0623F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_RIGHT_LEG_TRANSLATION_TIMES = new float[]{0.0F, 0.24F, 0.52F, 0.84F, 1.04F};
    private static final float[] WALK_RIGHT_LEG_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_RIGHT_LEG_ROTATION_TIMES = new float[]{0.0F, 0.24F, 0.52F, 0.84F, 1.04F};
    private static final float[] WALK_RIGHT_LEG_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0623F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] WALK_HAT_ROTATION_TIMES = new float[]{0.0F, 0.2F, 0.48F, 0.72F, 1.04F};
    private static final float[] WALK_HAT_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0436F, 0.0019F, -0.0436F, 0.0291F, 0.0F, 0.0146F, -0.027F, 0.0F, 0.0083F, 0.0F, 0.0F, 0.0F};
    private static final float HIT_LENGTH = 0.52F;
    private static final float[] HIT_HEAD_TRANSLATION_TIMES = new float[]{0.0F, 0.16F, 0.48F};
    private static final float[] HIT_HEAD_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.5F, 0.75F, 0.0F, 0.0F, 0.0F};
    private static final float[] HIT_HEAD_ROTATION_TIMES = new float[]{0.0F, 0.16F, 0.48F};
    private static final float[] HIT_HEAD_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] HIT_LEFT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.16F, 0.52F};
    private static final float[] HIT_LEFT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] HIT_TORSO_TRANSLATION_TIMES = new float[]{0.0F, 0.16F, 0.48F};
    private static final float[] HIT_TORSO_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] HIT_TORSO_ROTATION_TIMES = new float[]{0.0F, 0.16F, 0.48F};
    private static final float[] HIT_TORSO_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] HIT_RIGHT_ARM_ROTATION_TIMES = new float[]{0.0F, 0.16F, 0.52F};
    private static final float[] HIT_RIGHT_ARM_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] HIT_HAT_TRANSLATION_TIMES = new float[]{0.0F, 0.16F, 0.48F};
    private static final float[] HIT_HAT_TRANSLATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, 0.0F, -5.5F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] HIT_HAT_ROTATION_TIMES = new float[]{0.0F, 0.16F, 0.48F};
    private static final float[] HIT_HAT_ROTATION_VALUES = new float[]{0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};

    public MushroomBupModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.bup = this.root.getChild("bup");
        this.body = this.bup.getChild("body");
        this.torso = this.body.getChild("torso");
        this.left_arm = this.torso.getChild("left_arm");
        this.left_arm_cube = this.left_arm.getChild("left_arm_cube");
        this.right_arm = this.torso.getChild("right_arm");
        this.right_arm_cube = this.right_arm.getChild("right_arm_cube");
        this.right_leg = this.body.getChild("right_leg");
        this.left_leg = this.body.getChild("left_leg");
        this.head = this.bup.getChild("head");
        this.hat = this.head.getChild("hat");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition bup = root.addOrReplaceChild("bup", CubeListBuilder.create(), PartPose.offset(0.0F, -10.0F, 0.0F));
        PartDefinition body = bup.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, 0.0F));
        PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 20).addBox(-4.0F, -3.0F, -3.5F, 8.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 0.0F));
        PartDefinition left_arm = torso.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(3.5F, -1.0F, 0.0F));
        PartDefinition left_arm_cube = left_arm.addOrReplaceChild("left_arm_cube", CubeListBuilder.create().texOffs(60, 20).addBox(-1.0F, -1.25F, -1.375F, 1.0F, 2.5F, 2.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0008F, 0.8255F, 0.1246F, 0.0F, 0.0F, -0.3927F));
        PartDefinition right_arm = torso.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-3.7F, -1.0F, 0.0F));
        PartDefinition right_arm_cube = right_arm.addOrReplaceChild("right_arm_cube", CubeListBuilder.create().texOffs(70, 20).addBox(0.0F, -1.25F, -1.375F, 1.0F, 2.5F, 2.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8F, 0.75F, 0.125F, 0.0F, 0.0F, 0.3927F));
        PartDefinition right_leg = body.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(46, 20).addBox(-3.5F, -2.0F, -1.25F, 2.5F, 2.0F, 2.75F, new CubeDeformation(0.0F)).texOffs(108, 20).addBox(-3.5F, -1.0F, -2.0F, 2.5F, 1.0F, 0.75F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, 0.0F));
        PartDefinition left_leg = body.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(32, 20).addBox(1.0F, -2.0F, -1.25F, 2.5F, 2.0F, 2.75F, new CubeDeformation(0.0F)).texOffs(98, 20).addBox(1.0F, -1.0F, -2.0F, 2.5F, 1.0F, 0.75F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, 0.0F));
        PartDefinition head = bup.addOrReplaceChild("head", CubeListBuilder.create().texOffs(100, 0).addBox(-3.0F, 1.0F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));
        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -3.0F, -7.0F, 14.0F, 4.0F, 14.0F, new CubeDeformation(0.0F)).texOffs(58, 0).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(80, 20).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 128, 32);
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
        applyRotationTrack(this.left_arm, IDLE_LEFT_ARM_ROTATION_TIMES, IDLE_LEFT_ARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_arm, IDLE_RIGHT_ARM_ROTATION_TIMES, IDLE_RIGHT_ARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.hat, IDLE_HAT_ROTATION_TIMES, IDLE_HAT_ROTATION_VALUES, wrappedTime);
    }

    private void applyClipDEATH(float time) {
        float wrappedTime = wrapAnimationTime(time, DEATH_LENGTH);
        applyTranslationTrack(this.bup, DEATH_BUP_TRANSLATION_TIMES, DEATH_BUP_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.bup, DEATH_BUP_ROTATION_TIMES, DEATH_BUP_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.hat, DEATH_HAT_TRANSLATION_TIMES, DEATH_HAT_TRANSLATION_VALUES, wrappedTime);
    }

    private void applyClipWALK(float time) {
        float wrappedTime = wrapAnimationTime(time, WALK_LENGTH);
        applyTranslationTrack(this.head, WALK_HEAD_TRANSLATION_TIMES, WALK_HEAD_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.head, WALK_HEAD_ROTATION_TIMES, WALK_HEAD_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_arm, WALK_LEFT_ARM_ROTATION_TIMES, WALK_LEFT_ARM_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.left_leg, WALK_LEFT_LEG_TRANSLATION_TIMES, WALK_LEFT_LEG_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_leg, WALK_LEFT_LEG_ROTATION_TIMES, WALK_LEFT_LEG_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.torso, WALK_TORSO_ROTATION_TIMES, WALK_TORSO_ROTATION_VALUES, wrappedTime);
        applyScaleTrack(this.torso, WALK_TORSO_SCALE_TIMES, WALK_TORSO_SCALE_VALUES, wrappedTime);
        applyRotationTrack(this.right_arm, WALK_RIGHT_ARM_ROTATION_TIMES, WALK_RIGHT_ARM_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.right_leg, WALK_RIGHT_LEG_TRANSLATION_TIMES, WALK_RIGHT_LEG_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_leg, WALK_RIGHT_LEG_ROTATION_TIMES, WALK_RIGHT_LEG_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.hat, WALK_HAT_ROTATION_TIMES, WALK_HAT_ROTATION_VALUES, wrappedTime);
    }

    private void applyClipHIT(float time) {
        float wrappedTime = wrapAnimationTime(time, HIT_LENGTH);
        applyTranslationTrack(this.head, HIT_HEAD_TRANSLATION_TIMES, HIT_HEAD_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.head, HIT_HEAD_ROTATION_TIMES, HIT_HEAD_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.left_arm, HIT_LEFT_ARM_ROTATION_TIMES, HIT_LEFT_ARM_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.torso, HIT_TORSO_TRANSLATION_TIMES, HIT_TORSO_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.torso, HIT_TORSO_ROTATION_TIMES, HIT_TORSO_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.right_arm, HIT_RIGHT_ARM_ROTATION_TIMES, HIT_RIGHT_ARM_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.hat, HIT_HAT_TRANSLATION_TIMES, HIT_HAT_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.hat, HIT_HAT_ROTATION_TIMES, HIT_HAT_ROTATION_VALUES, wrappedTime);
    }
}

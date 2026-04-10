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
 * Creature id: tv_man
 */
public class TvManModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart waist;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private static final float WALK_LENGTH = 2.0F;
    private static final float[] WALK_BODY_ROTATION_TIMES = new float[]{0.0F, 2.0F};
    private static final float[] WALK_BODY_ROTATION_VALUES = new float[]{0.0873F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F};
    private static final float[] WALK_RIGHTARM_ROTATION_TIMES = new float[]{0.0F, 1.0F, 2.0F};
    private static final float[] WALK_RIGHTARM_ROTATION_VALUES = new float[]{-0.2618F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F, -0.2618F, 0.0F, 0.0F};
    private static final float[] WALK_LEFTARM_ROTATION_TIMES = new float[]{0.0F, 1.0F, 2.0F};
    private static final float[] WALK_LEFTARM_ROTATION_VALUES = new float[]{0.3056F, 0.0872F, 0.0038F, -0.2616F, 0.0872F, 0.0038F, 0.2622F, 0.0014F, 0.0151F};
    private static final float[] WALK_RIGHTLEG_ROTATION_TIMES = new float[]{0.0F, 1.0F, 2.0F};
    private static final float[] WALK_RIGHTLEG_ROTATION_VALUES = new float[]{0.2618F, 0.0F, 0.0F, -0.2618F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F};
    private static final float[] WALK_LEFTLEG_ROTATION_TIMES = new float[]{0.0F, 1.0F, 2.0F};
    private static final float[] WALK_LEFTLEG_ROTATION_VALUES = new float[]{-0.2618F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F, -0.2618F, 0.0F, 0.0F};

    public TvManModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.waist = this.root.getChild("waist");
        this.body = this.waist.getChild("body");
        this.head = this.body.getChild("head");
        this.rightArm = this.body.getChild("rightArm");
        this.leftArm = this.body.getChild("leftArm");
        this.rightLeg = this.body.getChild("rightLeg");
        this.leftLeg = this.body.getChild("leftLeg");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition waist = root.addOrReplaceChild("waist", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, 0.0F));
        PartDefinition body = waist.addOrReplaceChild("body", CubeListBuilder.create().texOffs(66, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(1, 44).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(1, 0).addBox(-3.0F, -7.0F, 3.0F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(1, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(0, 3).addBox(-4.0F, -8.0F, -5.0F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(1, 2).addBox(3.0F, -8.0F, -5.0F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(1, 0).addBox(-4.0F, -8.0F, -5.0F, 8.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(1, 0).addBox(-4.0F, -1.5F, -5.0F, 8.0F, 1.5F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition rightArm = body.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(66, 20).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(23, 17).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));
        PartDefinition leftArm = body.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(66, 20).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(23, 17).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition rightLeg = body.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(66, 40).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(25, 44).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        PartDefinition leftLeg = body.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(66, 40).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(25, 44).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 96, 64);
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
        applyClipWALK(idleTimeSeconds);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    public void setAngles(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    private void applyClipWALK(float time) {
        float wrappedTime = wrapAnimationTime(time, WALK_LENGTH);
        applyRotationTrack(this.body, WALK_BODY_ROTATION_TIMES, WALK_BODY_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.rightArm, WALK_RIGHTARM_ROTATION_TIMES, WALK_RIGHTARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.leftArm, WALK_LEFTARM_ROTATION_TIMES, WALK_LEFTARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.rightLeg, WALK_RIGHTLEG_ROTATION_TIMES, WALK_RIGHTLEG_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.leftLeg, WALK_LEFTLEG_ROTATION_TIMES, WALK_LEFTLEG_ROTATION_VALUES, wrappedTime);
    }
}

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
 * Creature id: prototype_warden
 */
public class PrototypeWardenModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart torso;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private static final float IDLE_LENGTH = 3.5F;
    private static final float RUN_LENGTH = 1.1667F;
    private static final float[] RUN_HEAD_ROTATION_TIMES = new float[]{0.0F, 0.0417F, 0.0833F, 0.125F, 0.2083F, 0.25F, 0.3333F, 0.375F, 0.4583F, 0.5F, 0.5833F, 0.6667F, 0.75F, 0.7917F, 0.875F, 0.9167F, 1.0F, 1.0417F, 1.125F, 1.1667F};
    private static final float[] RUN_HEAD_ROTATION_VALUES = new float[]{0.3491F, 0.0F, 0.0F, 0.2708F, 0.0018F, -0.029F, 0.1605F, 0.0041F, -0.0677F, 0.0609F, 0.0053F, -0.0871F, -0.1309F, 0.0F, 0.0F, -0.1635F, 0.0071F, 0.0431F, -0.2242F, 0.0024F, 0.0144F, -0.2618F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F, -0.4117F, -0.0099F, -0.0239F, -0.3914F, -0.0334F, -0.0807F, -0.1296F, -0.0334F, -0.0807F, 0.1672F, -0.08F, 0.098F, 0.2462F, -0.0997F, 0.1732F, 0.0435F, -0.1079F, 0.1959F, -0.0593F, -0.0997F, 0.1732F, -0.084F, -0.0445F, 0.0475F, -0.037F, -0.0213F, -0.0054F, 0.2402F, -0.0184F, -0.0121F, 0.3557F, -0.0213F, -0.0054F};
    private static final float[] RUN_BODY_ROTATION_TIMES = new float[]{0.0F, 0.0417F, 0.0833F, 0.125F, 0.2083F, 0.25F, 0.3333F, 0.375F, 0.4167F, 0.5F, 0.5833F, 0.625F, 0.7083F, 0.75F, 0.8333F, 0.9167F, 1.0F, 1.0417F, 1.0833F, 1.125F};
    private static final float[] RUN_BODY_ROTATION_VALUES = new float[]{-0.1739F, -0.0151F, -0.0859F, -0.0775F, -0.0185F, -0.0603F, 0.0527F, -0.0231F, -0.025F, 0.1297F, -0.0265F, 0.0006F, 0.0185F, -0.0275F, -0.0091F, -0.0448F, -0.0265F, 0.0006F, -0.0487F, -0.0183F, 0.175F, 0.0043F, -0.0111F, 0.1566F, 0.0823F, -0.0016F, 0.1113F, 0.2131F, 0.0187F, 0.0044F, 0.2404F, 0.0491F, -0.1379F, 0.2067F, 0.0551F, -0.1664F, -0.0132F, 0.0252F, -0.0129F, -0.0918F, 0.0094F, 0.0471F, -0.0343F, 0.0015F, -0.0048F, 0.0387F, -0.0026F, -0.0833F, -0.0173F, -0.0092F, -0.0924F, -0.0636F, -0.0127F, -0.0891F, -0.1067F, -0.0157F, -0.0848F, -0.1352F, -0.0178F, -0.0826F};
    private static final float[] RUN_RIGHTARM_ROTATION_TIMES = new float[]{0.0F, 0.0417F, 0.0833F, 0.125F, 0.2083F, 0.25F, 0.3333F, 0.375F, 0.4583F, 0.5F, 0.5833F, 0.625F, 0.7083F, 0.75F, 0.8333F, 0.875F, 0.9583F, 1.0417F, 1.0833F};
    private static final float[] RUN_RIGHTARM_ROTATION_VALUES = new float[]{0.2615F, 0.0118F, 0.1392F, 0.1547F, 0.0067F, 0.1007F, 0.0042F, -0.0003F, 0.0479F, -0.1317F, -0.0054F, 0.0094F, -0.3308F, -0.011F, -0.0073F, -0.3935F, -0.0054F, 0.0094F, -0.3889F, 0.0613F, 0.1708F, -0.2174F, 0.0666F, 0.1812F, 0.2219F, 0.0613F, 0.1708F, 0.3411F, 0.0607F, 0.1822F, 0.5145F, 0.0578F, 0.2129F, 0.4861F, 0.0516F, 0.2134F, -0.1817F, 0.0032F, 0.1216F, -0.4763F, -0.0083F, 0.097F, -0.5603F, 0.0496F, 0.2111F, -0.4714F, 0.0715F, 0.2523F, -0.2081F, 0.057F, 0.2175F, 0.1217F, 0.0271F, 0.1509F, 0.228F, 0.0177F, 0.1298F};
    private static final float[] RUN_LEFTARM_ROTATION_TIMES = new float[]{0.0F, 0.0417F, 0.0833F, 0.125F, 0.2083F, 0.25F, 0.3333F, 0.375F, 0.4583F, 0.5F, 0.5833F, 0.625F, 0.7083F, 0.75F, 0.8333F, 0.875F, 0.9583F, 1.0417F, 1.0833F};
    private static final float[] RUN_LEFTARM_ROTATION_VALUES = new float[]{-0.3054F, 0.0F, -0.1745F, -0.1437F, -0.0013F, -0.1698F, 0.081F, -0.0025F, -0.1652F, 0.2618F, 0.0F, -0.1745F, 0.442F, 0.0221F, -0.2829F, 0.4342F, 0.0338F, -0.301F, 0.2777F, 0.0454F, -0.2015F, 0.1584F, 0.0504F, -0.1285F, -0.0777F, 0.0558F, -0.0399F, -0.2297F, 0.0542F, -0.0698F, -0.5297F, 0.0435F, -0.2194F, -0.5678F, 0.0377F, -0.2575F, -0.1107F, 0.0182F, -0.1378F, 0.1348F, 0.0145F, -0.0845F, 0.3886F, 0.0436F, -0.1362F, 0.3941F, 0.0482F, -0.165F, 0.1831F, 0.0185F, -0.1638F, -0.1197F, -0.0236F, -0.1504F, -0.2166F, -0.0371F, -0.1463F};

    public PrototypeWardenModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.torso = this.body.getChild("torso");
        this.head = this.body.getChild("head");
        this.leftArm = this.body.getChild("leftArm");
        this.rightArm = this.body.getChild("rightArm");
        this.leftLeg = this.root.getChild("leftLeg");
        this.rightLeg = this.root.getChild("rightLeg");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -10.0F, 4.0F));
        PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -29.0F, 4.0F, 18.0F, 22.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.0F, -4.0F, 0.3491F, 0.0F, 0.0F));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(70, 0).addBox(-7.0F, -19.0F, -7.0F, 14.0F, 20.0F, 14.0F, new CubeDeformation(0.0F)).texOffs(66, 77).addBox(7.0F, -16.0F, 0.0F, 9.0F, 9.0F, 0.001F, new CubeDeformation(0.0F)).texOffs(48, 77).addBox(-16.0F, -16.0F, 0.0F, 9.0F, 9.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.0F, -10.0F));
        PartDefinition leftArm = body.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(28, 39).addBox(-1.0F, -3.0F, -4.0F, 6.0F, 30.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(10.0F, -18.0F, -4.0F));
        PartDefinition rightArm = body.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(0, 39).addBox(-5.0F, -3.0F, -4.0F, 6.0F, 30.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-10.0F, -18.0F, -4.0F));
        PartDefinition leftLeg = root.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(24, 77).addBox(-3.0F, 0.0F, -2.0F, 7.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -10.0F, 3.0F));
        PartDefinition rightLeg = root.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(0, 77).addBox(-4.0F, 0.0F, -2.0F, 7.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -10.0F, 3.0F));

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
            applyClipRUN(walkTimeSeconds);
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
    }

    private void applyClipRUN(float time) {
        float wrappedTime = wrapAnimationTime(time, RUN_LENGTH);
        applyRotationTrack(this.head, RUN_HEAD_ROTATION_TIMES, RUN_HEAD_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.body, RUN_BODY_ROTATION_TIMES, RUN_BODY_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.rightArm, RUN_RIGHTARM_ROTATION_TIMES, RUN_RIGHTARM_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.leftArm, RUN_LEFTARM_ROTATION_TIMES, RUN_LEFTARM_ROTATION_VALUES, wrappedTime);
    }
}

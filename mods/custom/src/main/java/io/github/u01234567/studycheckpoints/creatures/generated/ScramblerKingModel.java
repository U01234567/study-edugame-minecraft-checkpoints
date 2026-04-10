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
 * Creature id: scrambler_king
 */
public class ScramblerKingModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart Main;
    private final ModelPart Leg0;
    private final ModelPart cube;
    private final ModelPart cube_2;
    private final ModelPart cube_3;
    private final ModelPart Leg2;
    private final ModelPart cube_4;
    private final ModelPart cube_5;
    private final ModelPart cube_6;
    private final ModelPart Leg1;
    private final ModelPart cube_7;
    private final ModelPart cube_8;
    private final ModelPart cube_9;
    private final ModelPart Leg3;
    private final ModelPart cube_10;
    private final ModelPart cube_11;
    private final ModelPart cube_12;
    private final ModelPart Body;
    private final ModelPart Main_2;
    private final ModelPart Main_3;
    private final ModelPart Tail;
    private final ModelPart Tail_2;
    private final ModelPart Tail_3;
    private final ModelPart Bristles0;
    private final ModelPart Bristles0_2;
    private final ModelPart BigMouthBasement;
    private final ModelPart BigMouthBasement_2;
    private final ModelPart BigMouth;
    private final ModelPart BigMouth_2;
    private final ModelPart Bristles1;
    private final ModelPart Bristles0_3;
    private final ModelPart Mouthbasement3;
    private final ModelPart Mouthbasement;
    private final ModelPart Mouthbasement4;
    private final ModelPart Mouthbasement3_2;
    private final ModelPart Mouth2;
    private final ModelPart Mouth;
    private final ModelPart Mouthbasement2;
    private final ModelPart Mouthbasement_2;
    private final ModelPart Mouthbasement6;
    private final ModelPart Mouthbasement3_3;
    private final ModelPart Mouth3;
    private final ModelPart Mouth_2;
    private static final float WALK_TO_TARGET_LENGTH = 0.5F;
    private static final float[] WALK_TO_TARGET_LEG1_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] WALK_TO_TARGET_LEG1_ROTATION_VALUES = new float[]{0.0F, -0.6109F, 0.0F};
    private static final float[] WALK_TO_TARGET_LEG3_TRANSLATION_TIMES = new float[]{0.0F};
    private static final float[] WALK_TO_TARGET_LEG3_TRANSLATION_VALUES = new float[]{0.0F, -3.0F, 0.0F};
    private static final float[] WALK_TO_TARGET_LEG3_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] WALK_TO_TARGET_LEG3_ROTATION_VALUES = new float[]{-0.2716F, 0.5437F, -0.5019F};
    private static final float[] WALK_TO_TARGET_LEG0_TRANSLATION_TIMES = new float[]{0.0F};
    private static final float[] WALK_TO_TARGET_LEG0_TRANSLATION_VALUES = new float[]{0.0F, -3.0F, 0.0F};
    private static final float[] WALK_TO_TARGET_LEG0_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] WALK_TO_TARGET_LEG0_ROTATION_VALUES = new float[]{-0.3158F, 0.0401F, 0.2342F};
    private static final float[] WALK_TO_TARGET_LEG2_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] WALK_TO_TARGET_LEG2_ROTATION_VALUES = new float[]{0.0F, -0.0873F, 0.0F};
    private static final float[] WALK_TO_TARGET_BODY_TRANSLATION_TIMES = new float[]{0.0F};
    private static final float[] WALK_TO_TARGET_BODY_TRANSLATION_VALUES = new float[]{0.0F, -2.0F, 0.0F};
    private static final float[] WALK_TO_TARGET_BODY_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] WALK_TO_TARGET_BODY_ROTATION_VALUES = new float[]{-0.0456F, -0.0066F, -0.087F};
    private static final float[] WALK_TO_TARGET_TAIL_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] WALK_TO_TARGET_TAIL_ROTATION_VALUES = new float[]{0.0F, -0.2182F, 0.0F};
    private static final float[] WALK_TO_TARGET_BIGMOUTH_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] WALK_TO_TARGET_BIGMOUTH_ROTATION_VALUES = new float[]{0.5236F, 0.0F, 0.0F};
    private static final float[] WALK_TO_TARGET_MOUTHBASEMENT3_ROTATION_TIMES = new float[]{0.0F};
    private static final float[] WALK_TO_TARGET_MOUTHBASEMENT3_ROTATION_VALUES = new float[]{-0.0053F, 0.0174F, -0.0821F};

    public ScramblerKingModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.Main = this.root.getChild("Main");
        this.Leg0 = this.Main.getChild("Leg0");
        this.cube = this.Leg0.getChild("cube");
        this.cube_2 = this.Leg0.getChild("cube_2");
        this.cube_3 = this.Leg0.getChild("cube_3");
        this.Leg2 = this.Main.getChild("Leg2");
        this.cube_4 = this.Leg2.getChild("cube_4");
        this.cube_5 = this.Leg2.getChild("cube_5");
        this.cube_6 = this.Leg2.getChild("cube_6");
        this.Leg1 = this.Main.getChild("Leg1");
        this.cube_7 = this.Leg1.getChild("cube_7");
        this.cube_8 = this.Leg1.getChild("cube_8");
        this.cube_9 = this.Leg1.getChild("cube_9");
        this.Leg3 = this.Main.getChild("Leg3");
        this.cube_10 = this.Leg3.getChild("cube_10");
        this.cube_11 = this.Leg3.getChild("cube_11");
        this.cube_12 = this.Leg3.getChild("cube_12");
        this.Body = this.Main.getChild("Body");
        this.Main_2 = this.Body.getChild("Main_2");
        this.Main_3 = this.Body.getChild("Main_3");
        this.Tail = this.Body.getChild("Tail");
        this.Tail_2 = this.Tail.getChild("Tail_2");
        this.Tail_3 = this.Tail.getChild("Tail_3");
        this.Bristles0 = this.Body.getChild("Bristles0");
        this.Bristles0_2 = this.Bristles0.getChild("Bristles0_2");
        this.BigMouthBasement = this.Body.getChild("BigMouthBasement");
        this.BigMouthBasement_2 = this.BigMouthBasement.getChild("BigMouthBasement_2");
        this.BigMouth = this.BigMouthBasement.getChild("BigMouth");
        this.BigMouth_2 = this.BigMouth.getChild("BigMouth_2");
        this.Bristles1 = this.Body.getChild("Bristles1");
        this.Bristles0_3 = this.Bristles1.getChild("Bristles0_3");
        this.Mouthbasement3 = this.Body.getChild("Mouthbasement3");
        this.Mouthbasement = this.Mouthbasement3.getChild("Mouthbasement");
        this.Mouthbasement4 = this.Mouthbasement3.getChild("Mouthbasement4");
        this.Mouthbasement3_2 = this.Mouthbasement4.getChild("Mouthbasement3_2");
        this.Mouth2 = this.Mouthbasement4.getChild("Mouth2");
        this.Mouth = this.Mouth2.getChild("Mouth");
        this.Mouthbasement2 = this.Body.getChild("Mouthbasement2");
        this.Mouthbasement_2 = this.Mouthbasement2.getChild("Mouthbasement_2");
        this.Mouthbasement6 = this.Mouthbasement2.getChild("Mouthbasement6");
        this.Mouthbasement3_3 = this.Mouthbasement6.getChild("Mouthbasement3_3");
        this.Mouth3 = this.Mouthbasement6.getChild("Mouth3");
        this.Mouth_2 = this.Mouth3.getChild("Mouth_2");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition Main = root.addOrReplaceChild("Main", CubeListBuilder.create(), PartPose.offset(0.0F, -16.0F, -6.0F));
        PartDefinition Leg0 = Main.addOrReplaceChild("Leg0", CubeListBuilder.create(), PartPose.offsetAndRotation(-13.0F, -2.0F, -6.0F, 0.0F, -0.3927F, 0.0F));
        PartDefinition cube = Leg0.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(116, 69).addBox(-11.0F, -3.0F, -2.0F, 12.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 0.0F, -2.0F));
        PartDefinition cube_2 = Leg0.addOrReplaceChild("cube_2", CubeListBuilder.create().texOffs(0, 118).addBox(-11.0F, -3.0F, -2.0F, 10.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 15.0F, -2.0F, 0.0F, 0.0F, 1.5708F));
        PartDefinition cube_3 = Leg0.addOrReplaceChild("cube_3", CubeListBuilder.create().texOffs(104, 102).addBox(-11.0F, -4.0F, -3.0F, 8.0F, 9.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 21.0F, -2.0F, 0.0F, 0.0F, 1.5708F));
        PartDefinition Leg2 = Main.addOrReplaceChild("Leg2", CubeListBuilder.create(), PartPose.offsetAndRotation(-13.0F, -2.0F, 6.0F, 0.0F, 0.3927F, 0.0F));
        PartDefinition cube_4 = Leg2.addOrReplaceChild("cube_4", CubeListBuilder.create().texOffs(116, 54).addBox(-11.0F, -3.0F, -2.0F, 12.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 0.0F, -2.0F));
        PartDefinition cube_5 = Leg2.addOrReplaceChild("cube_5", CubeListBuilder.create().texOffs(0, 118).addBox(-11.0F, -3.0F, -2.0F, 10.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 15.0F, -2.0F, 0.0F, 0.0F, 1.5708F));
        PartDefinition cube_6 = Leg2.addOrReplaceChild("cube_6", CubeListBuilder.create().texOffs(104, 102).addBox(-11.0F, -4.0F, -3.0F, 8.0F, 9.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 21.0F, -2.0F, 0.0F, 0.0F, 1.5708F));
        PartDefinition Leg1 = Main.addOrReplaceChild("Leg1", CubeListBuilder.create(), PartPose.offsetAndRotation(13.0F, -2.0F, -6.0F, 0.0F, 0.4363F, 0.0F));
        PartDefinition cube_7 = Leg1.addOrReplaceChild("cube_7", CubeListBuilder.create().texOffs(116, 69).addBox(-1.0F, -3.0F, -2.0F, 12.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 0.0F, -2.0F));
        PartDefinition cube_8 = Leg1.addOrReplaceChild("cube_8", CubeListBuilder.create().texOffs(0, 118).addBox(1.0F, -3.0F, -2.0F, 10.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 15.0F, -2.0F, 0.0F, 0.0F, -1.5708F));
        PartDefinition cube_9 = Leg1.addOrReplaceChild("cube_9", CubeListBuilder.create().texOffs(104, 102).addBox(3.0F, -4.0F, -3.0F, 8.0F, 9.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 21.0F, -2.0F, 0.0F, 0.0F, -1.5708F));
        PartDefinition Leg3 = Main.addOrReplaceChild("Leg3", CubeListBuilder.create(), PartPose.offsetAndRotation(12.0F, -2.0F, 6.0F, 0.0F, -0.3927F, 0.0F));
        PartDefinition cube_10 = Leg3.addOrReplaceChild("cube_10", CubeListBuilder.create().texOffs(116, 54).addBox(-1.0F, -3.0F, -2.0F, 12.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 0.0F, -2.0F));
        PartDefinition cube_11 = Leg3.addOrReplaceChild("cube_11", CubeListBuilder.create().texOffs(0, 118).addBox(1.0F, -3.0F, -2.0F, 10.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 15.0F, -2.0F, 0.0F, 0.0F, -1.5708F));
        PartDefinition cube_12 = Leg3.addOrReplaceChild("cube_12", CubeListBuilder.create().texOffs(104, 102).addBox(3.0F, -4.0F, -3.0F, 8.0F, 9.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 21.0F, -2.0F, 0.0F, 0.0F, -1.5708F));
        PartDefinition Body = Main.addOrReplaceChild("Body", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, 2.0F));
        PartDefinition Main_2 = Body.addOrReplaceChild("Main_2", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -30.0F, -13.0F, 26.0F, 25.0F, 21.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 16.0F, 0.0F));
        PartDefinition Main_3 = Body.addOrReplaceChild("Main_3", CubeListBuilder.create().texOffs(94, 0).addBox(-11.0F, -29.0F, -13.0F, 24.0F, 23.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 16.0F, 21.0F));
        PartDefinition Tail = Body.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(1.0F, -4.0F, 8.0F));
        PartDefinition Tail_2 = Tail.addOrReplaceChild("Tail_2", CubeListBuilder.create().texOffs(58, 58).addBox(-8.5F, -4.0F, -1.0F, 13.0F, 12.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -1.0F, 1.0F));
        PartDefinition Tail_3 = Tail.addOrReplaceChild("Tail_3", CubeListBuilder.create().texOffs(64, 102).addBox(-8.5F, -4.0F, 16.0F, 13.0F, 12.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 3.0F, 55.0F, 3.1416F, 0.0F, 0.0F));
        PartDefinition Bristles0 = Body.addOrReplaceChild("Bristles0", CubeListBuilder.create(), PartPose.offset(-13.0F, -8.0F, -8.0F));
        PartDefinition Bristles0_2 = Bristles0.addOrReplaceChild("Bristles0_2", CubeListBuilder.create().texOffs(0, 46).addBox(-12.0F, -0.001F, -2.0F, 12.0F, 0.001F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -2.0F));
        PartDefinition BigMouthBasement = Body.addOrReplaceChild("BigMouthBasement", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0F, -11.0F, -4.0F, 2.2689F, 0.0F, 0.0F));
        PartDefinition BigMouthBasement_2 = BigMouthBasement.addOrReplaceChild("BigMouthBasement_2", CubeListBuilder.create().texOffs(0, 46).addBox(-8.5F, -4.0F, -1.0F, 13.0F, 12.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -2.0F, 1.0F));
        PartDefinition BigMouth = BigMouthBasement.addOrReplaceChild("BigMouth", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 2.0F, 32.0F, 0.48F, 0.0F, 0.0F));
        PartDefinition BigMouth_2 = BigMouth.addOrReplaceChild("BigMouth_2", CubeListBuilder.create().texOffs(0, 90).addBox(-11.5F, -8.0F, -1.0F, 19.0F, 15.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -2.0F, 1.0F));
        PartDefinition Bristles1 = Body.addOrReplaceChild("Bristles1", CubeListBuilder.create(), PartPose.offset(13.0F, -8.0F, -8.0F));
        PartDefinition Bristles0_3 = Bristles1.addOrReplaceChild("Bristles0_3", CubeListBuilder.create().texOffs(0, 46).addBox(0.0F, -0.001F, -2.0F, 12.0F, 0.001F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -2.0F));
        PartDefinition Mouthbasement3 = Body.addOrReplaceChild("Mouthbasement3", CubeListBuilder.create(), PartPose.offsetAndRotation(-8.0F, -13.0F, 1.0F, 0.4235F, 0.233F, -0.4733F));
        PartDefinition Mouthbasement = Mouthbasement3.addOrReplaceChild("Mouthbasement", CubeListBuilder.create().texOffs(58, 48).addBox(-1.0F, -29.0F, -1.0F, 8.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.154F, 19.8735F, -3.4617F));
        PartDefinition Mouthbasement4 = Mouthbasement3.addOrReplaceChild("Mouthbasement4", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.154F, -7.1265F, 0.5383F, 0.48F, 0.0F, 0.0F));
        PartDefinition Mouthbasement3_2 = Mouthbasement4.addOrReplaceChild("Mouthbasement3_2", CubeListBuilder.create().texOffs(0, 54).addBox(-4.0F, -9.3F, -3.7F, 8.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -1.0F, 0.0F, -0.0436F, 0.0F));
        PartDefinition Mouth2 = Mouthbasement4.addOrReplaceChild("Mouth2", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0F, -7.3681F, -0.7588F, -0.7854F, 0.0F, 0.0F));
        PartDefinition Mouth = Mouth2.addOrReplaceChild("Mouth", CubeListBuilder.create().texOffs(85, 37).addBox(-3.0F, -2.6319F, -7.9412F, 12.0F, 8.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, -3.0F, -0.2182F, 0.0F, 0.0F));
        PartDefinition Mouthbasement2 = Body.addOrReplaceChild("Mouthbasement2", CubeListBuilder.create(), PartPose.offsetAndRotation(8.0F, -13.0F, 1.0F, 0.4235F, -0.233F, 0.4733F));
        PartDefinition Mouthbasement_2 = Mouthbasement2.addOrReplaceChild("Mouthbasement_2", CubeListBuilder.create().texOffs(58, 48).addBox(-7.0F, -29.0F, -1.0F, 8.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.154F, 19.8735F, -3.4617F));
        PartDefinition Mouthbasement6 = Mouthbasement2.addOrReplaceChild("Mouthbasement6", CubeListBuilder.create(), PartPose.offsetAndRotation(1.154F, -7.1265F, 0.5383F, 0.48F, 0.0F, 0.0F));
        PartDefinition Mouthbasement3_3 = Mouthbasement6.addOrReplaceChild("Mouthbasement3_3", CubeListBuilder.create().texOffs(0, 54).addBox(-4.0F, -9.3F, -3.7F, 8.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -1.0F, 0.0F, 0.0436F, 0.0F));
        PartDefinition Mouth3 = Mouthbasement6.addOrReplaceChild("Mouth3", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, -7.3681F, -0.7588F, -0.7854F, 0.0F, 0.0F));
        PartDefinition Mouth_2 = Mouth3.addOrReplaceChild("Mouth_2", CubeListBuilder.create().texOffs(85, 37).addBox(-9.0F, -2.6319F, -7.9412F, 12.0F, 8.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, -3.0F, -0.2182F, 0.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 256, 256);
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
        applyClipWALK_TO_TARGET(idleTimeSeconds);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    public void setAngles(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    private void applyClipWALK_TO_TARGET(float time) {
        float wrappedTime = wrapAnimationTime(time, WALK_TO_TARGET_LENGTH);
        applyRotationTrack(this.Leg1, WALK_TO_TARGET_LEG1_ROTATION_TIMES, WALK_TO_TARGET_LEG1_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.Leg3, WALK_TO_TARGET_LEG3_TRANSLATION_TIMES, WALK_TO_TARGET_LEG3_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.Leg3, WALK_TO_TARGET_LEG3_ROTATION_TIMES, WALK_TO_TARGET_LEG3_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.Leg0, WALK_TO_TARGET_LEG0_TRANSLATION_TIMES, WALK_TO_TARGET_LEG0_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.Leg0, WALK_TO_TARGET_LEG0_ROTATION_TIMES, WALK_TO_TARGET_LEG0_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.Leg2, WALK_TO_TARGET_LEG2_ROTATION_TIMES, WALK_TO_TARGET_LEG2_ROTATION_VALUES, wrappedTime);
        applyTranslationTrack(this.Body, WALK_TO_TARGET_BODY_TRANSLATION_TIMES, WALK_TO_TARGET_BODY_TRANSLATION_VALUES, wrappedTime);
        applyRotationTrack(this.Body, WALK_TO_TARGET_BODY_ROTATION_TIMES, WALK_TO_TARGET_BODY_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.Tail, WALK_TO_TARGET_TAIL_ROTATION_TIMES, WALK_TO_TARGET_TAIL_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.BigMouth, WALK_TO_TARGET_BIGMOUTH_ROTATION_TIMES, WALK_TO_TARGET_BIGMOUTH_ROTATION_VALUES, wrappedTime);
        applyRotationTrack(this.Mouthbasement3, WALK_TO_TARGET_MOUTHBASEMENT3_ROTATION_TIMES, WALK_TO_TARGET_MOUTHBASEMENT3_ROTATION_VALUES, wrappedTime);
    }
}

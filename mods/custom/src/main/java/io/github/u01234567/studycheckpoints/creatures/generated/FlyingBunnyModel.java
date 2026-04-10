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
 * Creature id: flying_bunny
 */
public class FlyingBunnyModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart bone14;
    private final ModelPart cube_0;
    private final ModelPart bone13;
    private final ModelPart cube_1;
    private final ModelPart bone12;
    private final ModelPart cube_2;
    private final ModelPart bone11;
    private final ModelPart cube_3;
    private final ModelPart bone;
    private final ModelPart cube_4;
    private final ModelPart bone10;
    private final ModelPart cube_5;
    private final ModelPart bone9;
    private final ModelPart cube_6;
    private final ModelPart bone8;
    private final ModelPart cube_7;
    private final ModelPart bone7;
    private final ModelPart cube_8;
    private final ModelPart bone5;
    private final ModelPart cube_9;
    private final ModelPart bone3;
    private final ModelPart cube_10;
    private final ModelPart bone2;
    private final ModelPart cube_11;
    private final ModelPart part_26;
    private final ModelPart cube_12;
    private final ModelPart part_27;
    private final ModelPart cube_13;
    private final ModelPart part_28;
    private final ModelPart cube_14;
    private final ModelPart part_29;
    private final ModelPart cube_15;
    private final ModelPart part_30;
    private final ModelPart cube_16;
    private final ModelPart part_31;
    private final ModelPart cube_17;
    private final ModelPart part_32;
    private final ModelPart cube_18;

    public FlyingBunnyModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.bone14 = this.root.getChild("bone14");
        this.cube_0 = this.bone14.getChild("cube_0");
        this.bone13 = this.root.getChild("bone13");
        this.cube_1 = this.bone13.getChild("cube_1");
        this.bone12 = this.root.getChild("bone12");
        this.cube_2 = this.bone12.getChild("cube_2");
        this.bone11 = this.root.getChild("bone11");
        this.cube_3 = this.bone11.getChild("cube_3");
        this.bone = this.root.getChild("bone");
        this.cube_4 = this.bone.getChild("cube_4");
        this.bone10 = this.root.getChild("bone10");
        this.cube_5 = this.bone10.getChild("cube_5");
        this.bone9 = this.root.getChild("bone9");
        this.cube_6 = this.bone9.getChild("cube_6");
        this.bone8 = this.root.getChild("bone8");
        this.cube_7 = this.bone8.getChild("cube_7");
        this.bone7 = this.root.getChild("bone7");
        this.cube_8 = this.bone7.getChild("cube_8");
        this.bone5 = this.root.getChild("bone5");
        this.cube_9 = this.bone5.getChild("cube_9");
        this.bone3 = this.root.getChild("bone3");
        this.cube_10 = this.bone3.getChild("cube_10");
        this.bone2 = this.root.getChild("bone2");
        this.cube_11 = this.bone2.getChild("cube_11");
        this.part_26 = this.root.getChild("part_26");
        this.cube_12 = this.part_26.getChild("cube_12");
        this.part_27 = this.root.getChild("part_27");
        this.cube_13 = this.part_27.getChild("cube_13");
        this.part_28 = this.root.getChild("part_28");
        this.cube_14 = this.part_28.getChild("cube_14");
        this.part_29 = this.root.getChild("part_29");
        this.cube_15 = this.part_29.getChild("cube_15");
        this.part_30 = this.root.getChild("part_30");
        this.cube_16 = this.part_30.getChild("cube_16");
        this.part_31 = this.root.getChild("part_31");
        this.cube_17 = this.part_31.getChild("cube_17");
        this.part_32 = this.root.getChild("part_32");
        this.cube_18 = this.part_32.getChild("cube_18");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition bone14 = root.addOrReplaceChild("bone14", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -24.0F, -5.0F, 0.6545F, 0.0F, 0.0F));
        PartDefinition cube_0 = bone14.addOrReplaceChild("cube_0", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -0.504F, -1.134F, 8.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bone13 = root.addOrReplaceChild("bone13", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -23.0F, -5.0F, 1.3963F, 0.0F, 0.0F));
        PartDefinition cube_1 = bone13.addOrReplaceChild("cube_1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -0.504F, -1.134F, 8.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bone12 = root.addOrReplaceChild("bone12", CubeListBuilder.create(), PartPose.offset(-2.0F, -24.0F, -3.0F));
        PartDefinition cube_2 = bone12.addOrReplaceChild("cube_2", CubeListBuilder.create().texOffs(12, 48).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bone11 = root.addOrReplaceChild("bone11", CubeListBuilder.create(), PartPose.offsetAndRotation(2.5373F, -19.0F, -3.1566F, 0.0F, -1.3526F, 0.0F));
        PartDefinition cube_3 = bone11.addOrReplaceChild("cube_3", CubeListBuilder.create().texOffs(0, 16).addBox(-1.5383F, -6.0F, -0.887F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bone = root.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.5373F, -19.0F, -3.1566F, 0.0F, 1.3526F, 0.0F));
        PartDefinition cube_4 = bone.addOrReplaceChild("cube_4", CubeListBuilder.create().texOffs(0, 16).addBox(-2.4617F, -6.0F, -0.887F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bone10 = root.addOrReplaceChild("bone10", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0F, -18.0F, 2.0F, 0.5236F, 0.0F, 0.0F));
        PartDefinition cube_5 = bone10.addOrReplaceChild("cube_5", CubeListBuilder.create().texOffs(40, 28).addBox(-3.0F, -3.866F, 0.2321F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bone9 = root.addOrReplaceChild("bone9", CubeListBuilder.create(), PartPose.offsetAndRotation(-6.0F, -34.0F, 2.0F, -0.7854F, 0.9163F, 1.4399F));
        PartDefinition cube_6 = bone9.addOrReplaceChild("cube_6", CubeListBuilder.create().texOffs(20, 20).addBox(-4.7934F, -0.4345F, -5.5695F, 12.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bone8 = root.addOrReplaceChild("bone8", CubeListBuilder.create(), PartPose.offsetAndRotation(-6.0F, -36.0F, 6.0F, 0.3491F, -0.3491F, -1.0908F));
        PartDefinition cube_7 = bone8.addOrReplaceChild("cube_7", CubeListBuilder.create().texOffs(0, 12).addBox(-4.3023F, -0.4199F, -4.9138F, 8.0F, 0.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bone7 = root.addOrReplaceChild("bone7", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -30.0F, -7.0F, -0.7854F, 0.0F, 0.0F));
        PartDefinition cube_8 = bone7.addOrReplaceChild("cube_8", CubeListBuilder.create().texOffs(44, 44).addBox(-2.0F, -3.2444F, -0.4495F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bone5 = root.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offsetAndRotation(6.0F, -36.0F, 6.0F, 0.3491F, 0.3491F, 1.0908F));
        PartDefinition cube_9 = bone5.addOrReplaceChild("cube_9", CubeListBuilder.create().texOffs(0, 12).addBox(-3.6977F, -0.4199F, -4.9138F, 8.0F, 0.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bone3 = root.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, -18.0F, 2.0F, 0.5236F, 0.0F, 0.0F));
        PartDefinition cube_10 = bone3.addOrReplaceChild("cube_10", CubeListBuilder.create().texOffs(40, 28).addBox(-1.0F, -3.866F, 0.2321F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bone2 = root.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offsetAndRotation(6.0F, -34.0F, 2.0F, -0.7854F, -0.9163F, -1.4399F));
        PartDefinition cube_11 = bone2.addOrReplaceChild("cube_11", CubeListBuilder.create().texOffs(20, 20).addBox(-7.2066F, -0.4345F, -5.5695F, 12.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition part_26 = root.addOrReplaceChild("part_26", CubeListBuilder.create(), PartPose.offset(1.0F, -4.0F, 0.0F));
        PartDefinition cube_12 = part_26.addOrReplaceChild("cube_12", CubeListBuilder.create().texOffs(0, 8).addBox(0.0F, -12.0F, 4.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition part_27 = root.addOrReplaceChild("part_27", CubeListBuilder.create(), PartPose.offset(-2.0F, -26.0F, 7.0F));
        PartDefinition cube_13 = part_27.addOrReplaceChild("cube_13", CubeListBuilder.create().texOffs(44, 8).addBox(0.0F, -4.0F, 0.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition part_28 = root.addOrReplaceChild("part_28", CubeListBuilder.create(), PartPose.offset(-1.0F, -4.0F, 0.0F));
        PartDefinition cube_14 = part_28.addOrReplaceChild("cube_14", CubeListBuilder.create().texOffs(0, 8).addBox(-4.0F, -12.0F, 4.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition part_29 = root.addOrReplaceChild("part_29", CubeListBuilder.create(), PartPose.offset(2.0F, -25.0F, -3.0F));
        PartDefinition cube_15 = part_29.addOrReplaceChild("cube_15", CubeListBuilder.create().texOffs(28, 48).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition part_30 = root.addOrReplaceChild("part_30", CubeListBuilder.create(), PartPose.offset(0.0F, -32.0F, -3.0F));
        PartDefinition cube_16 = part_30.addOrReplaceChild("cube_16", CubeListBuilder.create().texOffs(28, 8).addBox(-2.0F, -2.0F, -8.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition part_31 = root.addOrReplaceChild("part_31", CubeListBuilder.create(), PartPose.offset(-6.0F, -22.0F, -5.0F));
        PartDefinition cube_17 = part_31.addOrReplaceChild("cube_17", CubeListBuilder.create().texOffs(24, 32).addBox(4.0F, -8.0F, 0.0F, 4.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition part_32 = root.addOrReplaceChild("part_32", CubeListBuilder.create(), PartPose.offset(0.0F, -20.0F, 0.0F));
        PartDefinition cube_18 = part_32.addOrReplaceChild("cube_18", CubeListBuilder.create().texOffs(0, 24).addBox(-4.0F, -8.0F, 0.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
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
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    public void setAngles(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

}

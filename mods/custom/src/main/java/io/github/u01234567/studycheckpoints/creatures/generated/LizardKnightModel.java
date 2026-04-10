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
 * Creature id: lizard_knight
 */
public class LizardKnightModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart body0;
    private final ModelPart rightarm;
    private final ModelPart rightarm2;
    private final ModelPart mesh_0;
    private final ModelPart mesh_1;
    private final ModelPart mesh_2;
    private final ModelPart mesh_3;
    private final ModelPart mesh_4;
    private final ModelPart mesh_5;
    private final ModelPart mesh_6;
    private final ModelPart mesh_7;
    private final ModelPart bone;
    private final ModelPart bone2;
    private final ModelPart mesh_8;
    private final ModelPart bone3;
    private final ModelPart mesh_9;
    private final ModelPart bone4;
    private final ModelPart mesh_10;
    private final ModelPart leftarm3;
    private final ModelPart mesh_11;
    private final ModelPart leftarm4;
    private final ModelPart mesh_12;
    private final ModelPart tail;
    private final ModelPart mesh_13;
    private final ModelPart tail2;
    private final ModelPart mesh_14;
    private final ModelPart tail3;
    private final ModelPart mesh_15;
    private final ModelPart head;
    private final ModelPart mesh_16;
    private final ModelPart mesh_17;
    private final ModelPart mesh_18;
    private final ModelPart leg1;
    private final ModelPart mesh_19;
    private final ModelPart leg0;
    private final ModelPart mesh_20;
    private final ModelPart leftarm4_2;
    private final ModelPart mesh_21;
    private final ModelPart leftarm5;
    private final ModelPart mesh_22;
    private final ModelPart leftarm6;
    private final ModelPart mesh_23;
    private final ModelPart leftarm7;
    private final ModelPart mesh_24;

    public LizardKnightModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.body0 = this.root.getChild("body0");
        this.rightarm = this.body0.getChild("rightarm");
        this.rightarm2 = this.rightarm.getChild("rightarm2");
        this.mesh_0 = this.body0.getChild("mesh_0");
        this.mesh_1 = this.body0.getChild("mesh_1");
        this.mesh_2 = this.body0.getChild("mesh_2");
        this.mesh_3 = this.body0.getChild("mesh_3");
        this.mesh_4 = this.body0.getChild("mesh_4");
        this.mesh_5 = this.body0.getChild("mesh_5");
        this.mesh_6 = this.body0.getChild("mesh_6");
        this.mesh_7 = this.body0.getChild("mesh_7");
        this.bone = this.body0.getChild("bone");
        this.bone2 = this.bone.getChild("bone2");
        this.mesh_8 = this.bone.getChild("mesh_8");
        this.bone3 = this.body0.getChild("bone3");
        this.mesh_9 = this.bone3.getChild("mesh_9");
        this.bone4 = this.bone3.getChild("bone4");
        this.mesh_10 = this.bone4.getChild("mesh_10");
        this.leftarm3 = this.bone4.getChild("leftarm3");
        this.mesh_11 = this.leftarm3.getChild("mesh_11");
        this.leftarm4 = this.leftarm3.getChild("leftarm4");
        this.mesh_12 = this.leftarm4.getChild("mesh_12");
        this.tail = this.body0.getChild("tail");
        this.mesh_13 = this.tail.getChild("mesh_13");
        this.tail2 = this.tail.getChild("tail2");
        this.mesh_14 = this.tail2.getChild("mesh_14");
        this.tail3 = this.tail2.getChild("tail3");
        this.mesh_15 = this.tail3.getChild("mesh_15");
        this.head = this.body0.getChild("head");
        this.mesh_16 = this.head.getChild("mesh_16");
        this.mesh_17 = this.head.getChild("mesh_17");
        this.mesh_18 = this.head.getChild("mesh_18");
        this.leg1 = this.body0.getChild("leg1");
        this.mesh_19 = this.leg1.getChild("mesh_19");
        this.leg0 = this.body0.getChild("leg0");
        this.mesh_20 = this.leg0.getChild("mesh_20");
        this.leftarm4_2 = this.body0.getChild("leftarm4_2");
        this.mesh_21 = this.leftarm4_2.getChild("mesh_21");
        this.leftarm5 = this.leftarm4_2.getChild("leftarm5");
        this.mesh_22 = this.leftarm5.getChild("mesh_22");
        this.leftarm6 = this.leftarm5.getChild("leftarm6");
        this.mesh_23 = this.leftarm6.getChild("mesh_23");
        this.leftarm7 = this.leftarm6.getChild("leftarm7");
        this.mesh_24 = this.leftarm7.getChild("mesh_24");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition body0 = root.addOrReplaceChild("body0", CubeListBuilder.create(), PartPose.offset(0.0F, -23.0F, 0.0F));
        PartDefinition rightarm = body0.addOrReplaceChild("rightarm", CubeListBuilder.create(), PartPose.offsetAndRotation(6.5F, 5.0F, 0.5F, 0.0F, 0.0F, 0.1309F));
        PartDefinition rightarm2 = rightarm.addOrReplaceChild("rightarm2", CubeListBuilder.create(), PartPose.offsetAndRotation(2.3872F, 3.7042F, 0.0F, 0.0F, 0.0F, 0.1745F));
        PartDefinition mesh_0 = body0.addOrReplaceChild("mesh_0", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -23.0F, -3.0F, 10.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.0F, 0.0F));
        PartDefinition mesh_1 = body0.addOrReplaceChild("mesh_1", CubeListBuilder.create().texOffs(34, 32).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.9F, -4.0F, 0.3491F, 0.0F, 0.0F));
        PartDefinition mesh_2 = body0.addOrReplaceChild("mesh_2", CubeListBuilder.create().texOffs(19, 14).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.2007F, 0.0F, 0.0F));
        PartDefinition mesh_3 = body0.addOrReplaceChild("mesh_3", CubeListBuilder.create().texOffs(21, 21).addBox(-4.0F, -15.0F, -2.5F, 8.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.0F, 0.0F));
        PartDefinition mesh_4 = body0.addOrReplaceChild("mesh_4", CubeListBuilder.create().texOffs(48, 6).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.0F, -2.5F, -0.0873F, 0.0F, 0.0F));
        PartDefinition mesh_5 = body0.addOrReplaceChild("mesh_5", CubeListBuilder.create().texOffs(0, 14).addBox(4.0F, -11.0F, -1.5F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.0F, 0.0F));
        PartDefinition mesh_6 = body0.addOrReplaceChild("mesh_6", CubeListBuilder.create().texOffs(0, 14).addBox(-5.0F, -11.0F, -1.5F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.0F, 0.0F));
        PartDefinition mesh_7 = body0.addOrReplaceChild("mesh_7", CubeListBuilder.create().texOffs(0, 33).addBox(-2.5F, -6.0F, -5.0F, 5.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.5F, 0.6109F, 0.0F, 0.0F));
        PartDefinition bone = body0.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(5.0F, 0.0F, 0.5F));
        PartDefinition bone2 = bone.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offset(3.0F, 0.0F, 0.0F));
        PartDefinition mesh_8 = bone.addOrReplaceChild("mesh_8", CubeListBuilder.create().texOffs(19, 16).addBox(0.0F, -1.0F, -3.0F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.1309F));
        PartDefinition bone3 = body0.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.6981F));
        PartDefinition mesh_9 = bone3.addOrReplaceChild("mesh_9", CubeListBuilder.create().texOffs(36, 46).addBox(-3.0F, 0.0F, -2.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition bone4 = bone3.addOrReplaceChild("bone4", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0F, 0.0F, 0.5F, 0.0F, 0.0F, -0.4363F));
        PartDefinition mesh_10 = bone4.addOrReplaceChild("mesh_10", CubeListBuilder.create().texOffs(36, 46).addBox(-11.0F, -23.0F, -2.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 23.0F, -0.5F));
        PartDefinition leftarm3 = bone4.addOrReplaceChild("leftarm3", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0F, 0.5F, 0.0F, 0.0F, 0.0F, 1.3526F));
        PartDefinition mesh_11 = leftarm3.addOrReplaceChild("mesh_11", CubeListBuilder.create().texOffs(24, 45).addBox(-2.0F, -1.0F, -2.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 1.0F, 0.0F));
        PartDefinition leftarm4 = leftarm3.addOrReplaceChild("leftarm4", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, 5.5F, -0.5F, 0.0F, 0.0F, -0.1745F));
        PartDefinition mesh_12 = leftarm4.addOrReplaceChild("mesh_12", CubeListBuilder.create().texOffs(14, 51).addBox(-0.2F, -1.2F, -1.2F, 2.4F, 6.4F, 2.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, 0.0F));
        PartDefinition tail = body0.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 14.0F, 0.5F, -0.5236F, 0.0F, 0.0F));
        PartDefinition mesh_13 = tail.addOrReplaceChild("mesh_13", CubeListBuilder.create().texOffs(48, 0).addBox(-1.5F, -12.0F, 0.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, -0.5F));
        PartDefinition tail2 = tail.addOrReplaceChild("tail2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.5F, 3.0F, 0.3054F, 0.0F, 0.0F));
        PartDefinition mesh_14 = tail2.addOrReplaceChild("mesh_14", CubeListBuilder.create().texOffs(0, 47).addBox(-1.0F, -11.5F, 3.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.5F, -3.5F));
        PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.5F, 5.0F, 0.3054F, 0.0F, 0.0F));
        PartDefinition mesh_15 = tail3.addOrReplaceChild("mesh_15", CubeListBuilder.create().texOffs(2, 50).addBox(-0.7F, -11.2F, 8.3F, 1.4F, 1.4F, 3.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, -8.5F));
        PartDefinition head = body0.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 0.0F));
        PartDefinition mesh_16 = head.addOrReplaceChild("mesh_16", CubeListBuilder.create().texOffs(0, 14).addBox(-3.0F, -28.5F, -7.0F, 6.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition mesh_17 = head.addOrReplaceChild("mesh_17", CubeListBuilder.create().texOffs(0, 36).addBox(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.5F, -3.0F, -1.0036F, 0.0F, 0.0F));
        PartDefinition mesh_18 = head.addOrReplaceChild("mesh_18", CubeListBuilder.create().texOffs(0, 25).addBox(-3.0F, -24.5F, -7.0F, 6.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition leg1 = body0.addOrReplaceChild("leg1", CubeListBuilder.create(), PartPose.offset(0.0F, 23.0F, 0.0F));
        PartDefinition mesh_19 = leg1.addOrReplaceChild("mesh_19", CubeListBuilder.create().texOffs(22, 32).addBox(0.5F, -9.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition leg0 = body0.addOrReplaceChild("leg0", CubeListBuilder.create(), PartPose.offset(0.0F, 23.0F, 0.0F));
        PartDefinition mesh_20 = leg0.addOrReplaceChild("mesh_20", CubeListBuilder.create().texOffs(22, 32).addBox(-4.5F, -9.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition leftarm4_2 = body0.addOrReplaceChild("leftarm4_2", CubeListBuilder.create(), PartPose.offsetAndRotation(5.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6981F));
        PartDefinition mesh_21 = leftarm4_2.addOrReplaceChild("mesh_21", CubeListBuilder.create().texOffs(36, 46).addBox(0.0F, 0.0F, -2.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition leftarm5 = leftarm4_2.addOrReplaceChild("leftarm5", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.4363F));
        PartDefinition mesh_22 = leftarm5.addOrReplaceChild("mesh_22", CubeListBuilder.create().texOffs(36, 46).addBox(8.0F, -23.0F, -2.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 23.0F, -0.5F));
        PartDefinition leftarm6 = leftarm5.addOrReplaceChild("leftarm6", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, 0.5F, 0.0F, 0.0F, 0.0F, -1.3526F));
        PartDefinition mesh_23 = leftarm6.addOrReplaceChild("mesh_23", CubeListBuilder.create().texOffs(24, 45).addBox(-1.0F, -1.0F, -2.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 1.0F, 0.0F));
        PartDefinition leftarm7 = leftarm6.addOrReplaceChild("leftarm7", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.5F, 5.5F, -0.5F, 0.0F, 0.0F, 0.1745F));
        PartDefinition mesh_24 = leftarm7.addOrReplaceChild("mesh_24", CubeListBuilder.create().texOffs(14, 51).addBox(-2.2F, -1.2F, -1.2F, 2.4F, 6.4F, 2.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, 0.0F));

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
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    public void setAngles(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

}

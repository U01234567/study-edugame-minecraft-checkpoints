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
 * Creature id: glare
 */
public class GlareModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart node_31;
    private final ModelPart head;
    private final ModelPart headtop;
    private final ModelPart headtop_2;
    private final ModelPart headtop_3;
    private final ModelPart angry;
    private final ModelPart angry_2;
    private final ModelPart angry_3;
    private final ModelPart eye;
    private final ModelPart eye_2;
    private final ModelPart eye_3;
    private final ModelPart Dancing;
    private final ModelPart cube;
    private final ModelPart cube_2;
    private final ModelPart Eyenormal;
    private final ModelPart cube_3;
    private final ModelPart cube_4;
    private final ModelPart body;
    private final ModelPart bone2;
    private final ModelPart bone3;
    private final ModelPart bone4;
    private final ModelPart bone4_2;
    private final ModelPart bone3_2;
    private final ModelPart bone2_2;
    private final ModelPart body_2;
    private final ModelPart head_2;
    private final ModelPart head_3;
    private final ModelPart head_4;
    private final ModelPart particle;
    private final ModelPart particle_2;
    private final ModelPart angry_particle;
    private final ModelPart angry_particle_2;

    public GlareModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.node_31 = this.root.getChild("node_31");
        this.head = this.node_31.getChild("head");
        this.headtop = this.head.getChild("headtop");
        this.headtop_2 = this.headtop.getChild("headtop_2");
        this.headtop_3 = this.headtop.getChild("headtop_3");
        this.angry = this.head.getChild("angry");
        this.angry_2 = this.angry.getChild("angry_2");
        this.angry_3 = this.angry.getChild("angry_3");
        this.eye = this.head.getChild("eye");
        this.eye_2 = this.eye.getChild("eye_2");
        this.eye_3 = this.eye.getChild("eye_3");
        this.Dancing = this.head.getChild("Dancing");
        this.cube = this.Dancing.getChild("cube");
        this.cube_2 = this.Dancing.getChild("cube_2");
        this.Eyenormal = this.head.getChild("Eyenormal");
        this.cube_3 = this.Eyenormal.getChild("cube_3");
        this.cube_4 = this.Eyenormal.getChild("cube_4");
        this.body = this.head.getChild("body");
        this.bone2 = this.body.getChild("bone2");
        this.bone3 = this.bone2.getChild("bone3");
        this.bone4 = this.bone3.getChild("bone4");
        this.bone4_2 = this.bone4.getChild("bone4_2");
        this.bone3_2 = this.bone3.getChild("bone3_2");
        this.bone2_2 = this.bone2.getChild("bone2_2");
        this.body_2 = this.body.getChild("body_2");
        this.head_2 = this.head.getChild("head_2");
        this.head_3 = this.head.getChild("head_3");
        this.head_4 = this.head.getChild("head_4");
        this.particle = this.head.getChild("particle");
        this.particle_2 = this.particle.getChild("particle_2");
        this.angry_particle = this.head.getChild("angry_particle");
        this.angry_particle_2 = this.angry_particle.getChild("angry_particle_2");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition node_31 = root.addOrReplaceChild("node_31", CubeListBuilder.create(), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition head = node_31.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -19.0F, 0.0F));
        PartDefinition headtop = head.addOrReplaceChild("headtop", CubeListBuilder.create(), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition headtop_2 = headtop.addOrReplaceChild("headtop_2", CubeListBuilder.create().texOffs(41, 20).addBox(-7.2F, -14.2F, -7.2F, 14.4F, 8.4F, 14.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));
        PartDefinition headtop_3 = headtop.addOrReplaceChild("headtop_3", CubeListBuilder.create().texOffs(41, 22).addBox(-7.0F, -11.001F, -7.0F, 14.0F, 0.001F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));
        PartDefinition angry = head.addOrReplaceChild("angry", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, -6.0F));
        PartDefinition angry_2 = angry.addOrReplaceChild("angry_2", CubeListBuilder.create().texOffs(95, 1).addBox(-3.0F, -12.0F, -6.11F, 5.0F, 4.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 11.0F, 6.0F));
        PartDefinition angry_3 = angry.addOrReplaceChild("angry_3", CubeListBuilder.create().texOffs(100, 1).addBox(-6.0F, -12.0F, -6.11F, 5.0F, 4.0F, 0.001F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 11.0F, 6.0F));
        PartDefinition eye = head.addOrReplaceChild("eye", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, -6.0F));
        PartDefinition eye_2 = eye.addOrReplaceChild("eye_2", CubeListBuilder.create().texOffs(126, 0).addBox(1.9F, -9.1F, -6.4F, 1.2F, 1.2F, 0.2F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 6.0F));
        PartDefinition eye_3 = eye.addOrReplaceChild("eye_3", CubeListBuilder.create().texOffs(126, 0).addBox(-3.1F, -9.1F, -6.4F, 1.2F, 1.2F, 0.2F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 6.0F));
        PartDefinition Dancing = head.addOrReplaceChild("Dancing", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 4.0F));
        PartDefinition cube = Dancing.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(95, 5).addBox(-4.05F, -2.05F, -1.15F, 5.1F, 2.1F, 0.1F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 1.0F, -5.0F));
        PartDefinition cube_2 = Dancing.addOrReplaceChild("cube_2", CubeListBuilder.create().texOffs(95, 5).addBox(-4.05F, -2.05F, -1.15F, 5.1F, 2.1F, 0.1F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 1.0F, -5.0F));
        PartDefinition Eyenormal = head.addOrReplaceChild("Eyenormal", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 1.0F));
        PartDefinition cube_3 = Eyenormal.addOrReplaceChild("cube_3", CubeListBuilder.create().texOffs(95, 7).addBox(-4.05F, -2.05F, 1.05F, 5.1F, 4.1F, 0.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, -4.0F, 3.1416F, -0.0F, 3.1416F));
        PartDefinition cube_4 = Eyenormal.addOrReplaceChild("cube_4", CubeListBuilder.create().texOffs(95, 7).addBox(-4.05F, -2.05F, 2.85F, 5.1F, 4.1F, 0.1F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 0.0F, -8.0F));
        PartDefinition body = head.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 0.0F));
        PartDefinition bone2 = body.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 0.0F));
        PartDefinition bone3 = bone2.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition bone4 = bone3.addOrReplaceChild("bone4", CubeListBuilder.create(), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition bone4_2 = bone4.addOrReplaceChild("bone4_2", CubeListBuilder.create().texOffs(0, 47).addBox(-3.2F, -1.2F, -3.2F, 6.4F, 11.4F, 6.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));
        PartDefinition bone3_2 = bone3.addOrReplaceChild("bone3_2", CubeListBuilder.create().texOffs(0, 45).addBox(-4.0F, -3.0F, -4.0F, 8.0F, 11.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));
        PartDefinition bone2_2 = bone2.addOrReplaceChild("bone2_2", CubeListBuilder.create().texOffs(0, 26).addBox(-5.0F, -4.0F, -5.0F, 10.0F, 9.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));
        PartDefinition body_2 = body.addOrReplaceChild("body_2", CubeListBuilder.create().texOffs(35, 42).addBox(-6.0F, -6.0F, -6.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));
        PartDefinition head_2 = head.addOrReplaceChild("head_2", CubeListBuilder.create().texOffs(56, 0).addBox(-6.0F, -11.0F, -6.1F, 12.0F, 7.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));
        PartDefinition head_3 = head.addOrReplaceChild("head_3", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -14.0F, -7.0F, 14.0F, 13.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));
        PartDefinition head_4 = head.addOrReplaceChild("head_4", CubeListBuilder.create().texOffs(41, 22).addBox(-7.0F, -4.001F, -7.0F, 14.0F, 0.001F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));
        PartDefinition particle = head.addOrReplaceChild("particle", CubeListBuilder.create(), PartPose.offset(0.0F, 15.0F, 0.0F));
        PartDefinition particle_2 = particle.addOrReplaceChild("particle_2", CubeListBuilder.create(), PartPose.offset(0.0F, -0.0F, 0.0F));
        PartDefinition angry_particle = head.addOrReplaceChild("angry_particle", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition angry_particle_2 = angry_particle.addOrReplaceChild("angry_particle_2", CubeListBuilder.create(), PartPose.offset(0.0F, -0.0F, 0.0F));

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
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    public void setAngles(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

}

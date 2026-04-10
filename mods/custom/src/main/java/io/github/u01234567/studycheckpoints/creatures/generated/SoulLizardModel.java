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
 * Creature id: soul_lizard
 */
public class SoulLizardModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart soul_lizard;
    private final ModelPart body;
    private final ModelPart body_main;
    private final ModelPart body_sail_left;
    private final ModelPart body_sail_right;
    private final ModelPart mob_tail;
    private final ModelPart tail_base;
    private final ModelPart tail_tip;
    private final ModelPart tail_sail_left;
    private final ModelPart tail_sail_right;
    private final ModelPart mob_head;
    private final ModelPart head_main;
    private final ModelPart left_leg;
    private final ModelPart left_leg_seg1;
    private final ModelPart left_leg_seg2;
    private final ModelPart left_leg_seg3a;
    private final ModelPart left_leg_seg3b;
    private final ModelPart right_leg;
    private final ModelPart right_leg_seg1;
    private final ModelPart right_leg_seg2;
    private final ModelPart right_leg_seg3a;
    private final ModelPart right_leg_seg3b;
    private final ModelPart right_arm;
    private final ModelPart right_arm_seg1;
    private final ModelPart right_arm_seg2;
    private final ModelPart right_arm_seg3a;
    private final ModelPart right_arm_seg3b;
    private final ModelPart left_arm;
    private final ModelPart left_arm_seg1;
    private final ModelPart left_arm_seg2;
    private final ModelPart left_arm_seg3a;
    private final ModelPart left_arm_seg3b;

    public SoulLizardModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.soul_lizard = this.root.getChild("soul_lizard");
        this.body = this.soul_lizard.getChild("body");
        this.body_main = this.body.getChild("body_main");
        this.body_sail_left = this.body.getChild("body_sail_left");
        this.body_sail_right = this.body.getChild("body_sail_right");
        this.mob_tail = this.soul_lizard.getChild("mob_tail");
        this.tail_base = this.mob_tail.getChild("tail_base");
        this.tail_tip = this.mob_tail.getChild("tail_tip");
        this.tail_sail_left = this.mob_tail.getChild("tail_sail_left");
        this.tail_sail_right = this.mob_tail.getChild("tail_sail_right");
        this.mob_head = this.soul_lizard.getChild("mob_head");
        this.head_main = this.mob_head.getChild("head_main");
        this.left_leg = this.soul_lizard.getChild("left_leg");
        this.left_leg_seg1 = this.left_leg.getChild("left_leg_seg1");
        this.left_leg_seg2 = this.left_leg.getChild("left_leg_seg2");
        this.left_leg_seg3a = this.left_leg.getChild("left_leg_seg3a");
        this.left_leg_seg3b = this.left_leg.getChild("left_leg_seg3b");
        this.right_leg = this.soul_lizard.getChild("right_leg");
        this.right_leg_seg1 = this.right_leg.getChild("right_leg_seg1");
        this.right_leg_seg2 = this.right_leg.getChild("right_leg_seg2");
        this.right_leg_seg3a = this.right_leg.getChild("right_leg_seg3a");
        this.right_leg_seg3b = this.right_leg.getChild("right_leg_seg3b");
        this.right_arm = this.soul_lizard.getChild("right_arm");
        this.right_arm_seg1 = this.right_arm.getChild("right_arm_seg1");
        this.right_arm_seg2 = this.right_arm.getChild("right_arm_seg2");
        this.right_arm_seg3a = this.right_arm.getChild("right_arm_seg3a");
        this.right_arm_seg3b = this.right_arm.getChild("right_arm_seg3b");
        this.left_arm = this.soul_lizard.getChild("left_arm");
        this.left_arm_seg1 = this.left_arm.getChild("left_arm_seg1");
        this.left_arm_seg2 = this.left_arm.getChild("left_arm_seg2");
        this.left_arm_seg3a = this.left_arm.getChild("left_arm_seg3a");
        this.left_arm_seg3b = this.left_arm.getChild("left_arm_seg3b");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition soul_lizard = root.addOrReplaceChild("soul_lizard", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition body = soul_lizard.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.25F, 0.0F));
        PartDefinition body_main = body.addOrReplaceChild("body_main", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -4.0F, -6.0F, 4.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition body_sail_left = body.addOrReplaceChild("body_sail_left", CubeListBuilder.create().texOffs(0, 3).addBox(0.0F, -2.0F, -6.0F, 0.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.0349F));
        PartDefinition body_sail_right = body.addOrReplaceChild("body_sail_right", CubeListBuilder.create().texOffs(0, 3).addBox(0.0F, -2.0F, -6.0F, 0.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.0349F));
        PartDefinition mob_tail = soul_lizard.addOrReplaceChild("mob_tail", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 6.0F));
        PartDefinition tail_base = mob_tail.addOrReplaceChild("tail_base", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.975F, 5.75F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, -6.0F));
        PartDefinition tail_tip = mob_tail.addOrReplaceChild("tail_tip", CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, -0.475F, -0.15F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.25F, 3.75F, -0.1309F, 0.0F, 0.0F));
        PartDefinition tail_sail_left = mob_tail.addOrReplaceChild("tail_sail_left", CubeListBuilder.create().texOffs(10, 13).addBox(0.0F, -1.725F, -2.025F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 2.0F, 0.0F, 0.0F, 0.0349F));
        PartDefinition tail_sail_right = mob_tail.addOrReplaceChild("tail_sail_right", CubeListBuilder.create().texOffs(10, 13).addBox(0.0F, -1.725F, -2.025F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 2.0F, 0.0F, 0.0F, -0.0349F));
        PartDefinition mob_head = soul_lizard.addOrReplaceChild("mob_head", CubeListBuilder.create(), PartPose.offset(0.0F, -2.25F, -6.0F));
        PartDefinition head_main = mob_head.addOrReplaceChild("head_main", CubeListBuilder.create().texOffs(0, 17).addBox(-1.5F, -1.0F, -3.525F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));
        PartDefinition left_leg = soul_lizard.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(-2.0F, -1.5F, 4.0F));
        PartDefinition left_leg_seg1 = left_leg.addOrReplaceChild("left_leg_seg1", CubeListBuilder.create().texOffs(20, 0).addBox(-2.0F, -0.4F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.3927F, -0.3054F));
        PartDefinition left_leg_seg2 = left_leg.addOrReplaceChild("left_leg_seg2", CubeListBuilder.create().texOffs(11, 24).addBox(-1.8081F, -0.2107F, -0.511F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.625F, 0.375F, 0.4F, 0.0F, 0.0F, -0.0436F));
        PartDefinition left_leg_seg3a = left_leg.addOrReplaceChild("left_leg_seg3a", CubeListBuilder.create().texOffs(18, 23).addBox(-2.2331F, -0.1607F, -0.586F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.875F, 0.375F, 0.4F, 0.2639F, -1.0222F, -0.3279F));
        PartDefinition left_leg_seg3b = left_leg.addOrReplaceChild("left_leg_seg3b", CubeListBuilder.create().texOffs(6, 23).addBox(-2.1331F, -0.0857F, -0.511F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.875F, 0.375F, 0.4F, 0.0219F, -0.126F, -0.1965F));
        PartDefinition right_leg = soul_lizard.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(2.0F, -1.5F, 4.0F));
        PartDefinition right_leg_seg1 = right_leg.addOrReplaceChild("right_leg_seg1", CubeListBuilder.create().texOffs(16, 17).addBox(-1.0F, -0.4F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.3927F, 0.3054F));
        PartDefinition right_leg_seg2 = right_leg.addOrReplaceChild("right_leg_seg2", CubeListBuilder.create().texOffs(0, 23).addBox(-0.1919F, -0.2107F, -0.511F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.625F, 0.375F, 0.4F, 0.0F, 0.0F, 0.0436F));
        PartDefinition right_leg_seg3a = right_leg.addOrReplaceChild("right_leg_seg3a", CubeListBuilder.create().texOffs(13, 22).addBox(0.2331F, -0.1607F, -0.586F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.875F, 0.375F, 0.4F, 0.2639F, 1.0222F, 0.3279F));
        PartDefinition right_leg_seg3b = right_leg.addOrReplaceChild("right_leg_seg3b", CubeListBuilder.create().texOffs(20, 21).addBox(0.1331F, -0.0857F, -0.511F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.875F, 0.375F, 0.4F, 0.0219F, 0.126F, 0.1965F));
        PartDefinition right_arm = soul_lizard.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(2.0F, -1.5F, -4.0F));
        PartDefinition right_arm_seg1 = right_arm.addOrReplaceChild("right_arm_seg1", CubeListBuilder.create().texOffs(20, 5).addBox(-1.0F, -0.4F, 0.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.025F, 0.0F, -0.675F, -0.1745F, -0.8126F, 0.2553F));
        PartDefinition right_arm_seg2 = right_arm.addOrReplaceChild("right_arm_seg2", CubeListBuilder.create().texOffs(20, 9).addBox(-0.4169F, -0.2107F, -0.689F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.3511F, 0.333F, 1.5638F, 0.0436F, 0.5236F, 0.0F));
        PartDefinition right_arm_seg3a = right_arm.addOrReplaceChild("right_arm_seg3a", CubeListBuilder.create().texOffs(15, 20).addBox(0.2331F, -0.1607F, -0.414F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.1F, 0.3F, 0.35F, 0.2544F, 0.9409F, 0.2891F));
        PartDefinition right_arm_seg3b = right_arm.addOrReplaceChild("right_arm_seg3b", CubeListBuilder.create().texOffs(10, 19).addBox(0.1331F, -0.0857F, -0.489F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.1F, 0.3F, 0.35F, 2.5976F, 1.208F, 2.5642F));
        PartDefinition left_arm = soul_lizard.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(-2.0F, -1.5F, -4.0F));
        PartDefinition left_arm_seg1 = left_arm.addOrReplaceChild("left_arm_seg1", CubeListBuilder.create().texOffs(20, 3).addBox(-3.0F, -0.4F, 0.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.025F, 0.0F, -0.675F, -0.1745F, 0.8126F, -0.2553F));
        PartDefinition left_arm_seg2 = left_arm.addOrReplaceChild("left_arm_seg2", CubeListBuilder.create().texOffs(20, 7).addBox(-2.5831F, -0.2107F, -0.689F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.3511F, 0.333F, 1.5638F, 0.0436F, -0.5236F, 0.0F));
        PartDefinition left_arm_seg3a = left_arm.addOrReplaceChild("left_arm_seg3a", CubeListBuilder.create().texOffs(6, 8).addBox(-2.2331F, -0.1607F, -0.414F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1F, 0.3F, 0.35F, 0.2544F, -0.9409F, -0.2891F));
        PartDefinition left_arm_seg3b = left_arm.addOrReplaceChild("left_arm_seg3b", CubeListBuilder.create().texOffs(6, 6).addBox(-2.1331F, -0.0857F, -0.489F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1F, 0.3F, 0.35F, 2.5976F, -1.208F, -2.5642F));

        return LayerDefinition.create(meshDefinition, 32, 32);
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

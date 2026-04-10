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
 * Creature id: abyss_deer
 */
public class AbyssDeerModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart body_main;
    private final ModelPart body_neck;
    private final ModelPart mob_tail;
    private final ModelPart tail;
    private final ModelPart left_leg2;
    private final ModelPart left_leg2_upper;
    private final ModelPart left_leg2_hoof;
    private final ModelPart right_leg2;
    private final ModelPart right_leg2_upper;
    private final ModelPart right_leg2_hoof;
    private final ModelPart left_leg1_group;
    private final ModelPart left_leg1;
    private final ModelPart right_leg1_group;
    private final ModelPart right_leg1;
    private final ModelPart mob_head;
    private final ModelPart horn_left;
    private final ModelPart horn_left_base;
    private final ModelPart horn_left_branch_back;
    private final ModelPart horn_left_branch_front;
    private final ModelPart horn_left_tine_upper;
    private final ModelPart horn_left_tine_lower;
    private final ModelPart horn_right;
    private final ModelPart horn_right_base;
    private final ModelPart horn_right_tine_upper;
    private final ModelPart horn_right_branch_front;
    private final ModelPart horn_right_tine_lower;
    private final ModelPart horn_right_branch_back;
    private final ModelPart head_main;
    private final ModelPart snout;
    private final ModelPart ear_left;
    private final ModelPart ear_right;

    public AbyssDeerModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.body_main = this.body.getChild("body_main");
        this.body_neck = this.body.getChild("body_neck");
        this.mob_tail = this.root.getChild("mob_tail");
        this.tail = this.mob_tail.getChild("tail");
        this.left_leg2 = this.root.getChild("left_leg2");
        this.left_leg2_upper = this.left_leg2.getChild("left_leg2_upper");
        this.left_leg2_hoof = this.left_leg2.getChild("left_leg2_hoof");
        this.right_leg2 = this.root.getChild("right_leg2");
        this.right_leg2_upper = this.right_leg2.getChild("right_leg2_upper");
        this.right_leg2_hoof = this.right_leg2.getChild("right_leg2_hoof");
        this.left_leg1_group = this.root.getChild("left_leg1_group");
        this.left_leg1 = this.left_leg1_group.getChild("left_leg1");
        this.right_leg1_group = this.root.getChild("right_leg1_group");
        this.right_leg1 = this.right_leg1_group.getChild("right_leg1");
        this.mob_head = this.root.getChild("mob_head");
        this.horn_left = this.mob_head.getChild("horn_left");
        this.horn_left_base = this.horn_left.getChild("horn_left_base");
        this.horn_left_branch_back = this.horn_left.getChild("horn_left_branch_back");
        this.horn_left_branch_front = this.horn_left.getChild("horn_left_branch_front");
        this.horn_left_tine_upper = this.horn_left.getChild("horn_left_tine_upper");
        this.horn_left_tine_lower = this.horn_left.getChild("horn_left_tine_lower");
        this.horn_right = this.mob_head.getChild("horn_right");
        this.horn_right_base = this.horn_right.getChild("horn_right_base");
        this.horn_right_tine_upper = this.horn_right.getChild("horn_right_tine_upper");
        this.horn_right_branch_front = this.horn_right.getChild("horn_right_branch_front");
        this.horn_right_tine_lower = this.horn_right.getChild("horn_right_tine_lower");
        this.horn_right_branch_back = this.horn_right.getChild("horn_right_branch_back");
        this.head_main = this.mob_head.getChild("head_main");
        this.snout = this.mob_head.getChild("snout");
        this.ear_left = this.mob_head.getChild("ear_left");
        this.ear_right = this.mob_head.getChild("ear_right");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition body_main = body.addOrReplaceChild("body_main", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -15.0F, -8.0F, 6.0F, 7.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition body_neck = body.addOrReplaceChild("body_neck", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -13.0F, -6.0F, 0.4363F, 0.0F, 0.0F));
        PartDefinition mob_tail = root.addOrReplaceChild("mob_tail", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition tail = mob_tail.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(22, 23).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -13.0F, 7.0F, -0.7418F, 0.0F, 0.0F));
        PartDefinition left_leg2 = root.addOrReplaceChild("left_leg2", CubeListBuilder.create(), PartPose.offset(-1.25F, -8.0F, 6.0F));
        PartDefinition left_leg2_upper = left_leg2.addOrReplaceChild("left_leg2_upper", CubeListBuilder.create().texOffs(16, 38).addBox(0.0F, -6.0F, 0.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 8.0F, 0.0F));
        PartDefinition left_leg2_hoof = left_leg2.addOrReplaceChild("left_leg2_hoof", CubeListBuilder.create().texOffs(28, 8).addBox(0.0F, -4.0F, -2.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.25F, 2.25F, 0.25F));
        PartDefinition right_leg2 = root.addOrReplaceChild("right_leg2", CubeListBuilder.create(), PartPose.offset(1.25F, -8.0F, 6.0F));
        PartDefinition right_leg2_upper = right_leg2.addOrReplaceChild("right_leg2_upper", CubeListBuilder.create().texOffs(8, 33).addBox(-2.0F, -6.0F, 0.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 8.0F, 0.0F));
        PartDefinition right_leg2_hoof = right_leg2.addOrReplaceChild("right_leg2_hoof", CubeListBuilder.create().texOffs(28, 0).addBox(-3.0F, -4.0F, -2.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.25F, 2.25F, 0.25F));
        PartDefinition left_leg1_group = root.addOrReplaceChild("left_leg1_group", CubeListBuilder.create(), PartPose.offset(-1.25F, -8.75F, -6.5F));
        PartDefinition left_leg1 = left_leg1_group.addOrReplaceChild("left_leg1", CubeListBuilder.create().texOffs(0, 33).addBox(-3.25F, -10.0F, -7.5F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.25F, 8.75F, 6.5F));
        PartDefinition right_leg1_group = root.addOrReplaceChild("right_leg1_group", CubeListBuilder.create(), PartPose.offset(1.25F, -8.75F, -6.5F));
        PartDefinition right_leg1 = right_leg1_group.addOrReplaceChild("right_leg1", CubeListBuilder.create().texOffs(32, 31).addBox(1.25F, -10.0F, -7.5F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.25F, 8.75F, 6.5F));
        PartDefinition mob_head = root.addOrReplaceChild("mob_head", CubeListBuilder.create(), PartPose.offset(0.0F, -18.5F, -8.5F));
        PartDefinition horn_left = mob_head.addOrReplaceChild("horn_left", CubeListBuilder.create(), PartPose.offset(-3.0244F, -7.5927F, -1.2413F));
        PartDefinition horn_left_base = horn_left.addOrReplaceChild("horn_left_base", CubeListBuilder.create().texOffs(40, 32).addBox(-0.5F, -5.0F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0244F, 4.0927F, 1.2413F, 0.3054F, 0.0F, -0.5236F));
        PartDefinition horn_left_branch_back = horn_left.addOrReplaceChild("horn_left_branch_back", CubeListBuilder.create().texOffs(28, 38).addBox(-0.7764F, -5.1972F, -0.6244F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5523F, 0.1055F, -1.1188F));
        PartDefinition horn_left_branch_front = horn_left.addOrReplaceChild("horn_left_branch_front", CubeListBuilder.create().texOffs(38, 26).addBox(0.008F, -5.0117F, -0.992F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5962F, -1.0826F, -2.3115F, 0.974F, -0.1638F, -0.9501F));
        PartDefinition horn_left_tine_upper = horn_left.addOrReplaceChild("horn_left_tine_upper", CubeListBuilder.create().texOffs(16, 23).addBox(0.2521F, -2.9758F, -1.0421F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0432F, -1.8554F, -2.1988F, 0.954F, 0.2493F, -0.9422F));
        PartDefinition horn_left_tine_lower = horn_left.addOrReplaceChild("horn_left_tine_lower", CubeListBuilder.create().texOffs(0, 14).addBox(-0.1979F, -0.9758F, -1.0421F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0432F, -1.8554F, -2.1988F, 0.9511F, -0.0735F, -0.5188F));
        PartDefinition horn_right = mob_head.addOrReplaceChild("horn_right", CubeListBuilder.create(), PartPose.offset(3.0244F, -7.5927F, -1.2413F));
        PartDefinition horn_right_base = horn_right.addOrReplaceChild("horn_right_base", CubeListBuilder.create().texOffs(24, 38).addBox(-0.5F, -5.0F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0244F, 4.0927F, 1.2413F, 0.3054F, 0.0F, 0.5236F));
        PartDefinition horn_right_tine_upper = horn_right.addOrReplaceChild("horn_right_tine_upper", CubeListBuilder.create().texOffs(0, 12).addBox(-4.2521F, -2.9758F, -1.0421F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0432F, -1.8554F, -2.1988F, 0.954F, -0.2493F, 0.9422F));
        PartDefinition horn_right_branch_front = horn_right.addOrReplaceChild("horn_right_branch_front", CubeListBuilder.create().texOffs(0, 23).addBox(-1.008F, -5.0117F, -0.992F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5962F, -1.0826F, -2.3115F, 0.974F, 0.1638F, 0.9501F));
        PartDefinition horn_right_tine_lower = horn_right.addOrReplaceChild("horn_right_tine_lower", CubeListBuilder.create().texOffs(0, 10).addBox(-3.8021F, -0.9758F, -1.0421F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0432F, -1.8554F, -2.1988F, 0.9511F, 0.0735F, 0.5188F));
        PartDefinition horn_right_branch_back = horn_right.addOrReplaceChild("horn_right_branch_back", CubeListBuilder.create().texOffs(10, 10).addBox(-0.2236F, -5.1972F, -0.6244F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5523F, -0.1055F, 1.1188F));
        PartDefinition head_main = mob_head.addOrReplaceChild("head_main", CubeListBuilder.create().texOffs(0, 23).addBox(-2.5F, -10.0F, -3.0F, 5.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.5F, 2.5F, 0.4363F, 0.0F, 0.0F));
        PartDefinition snout = mob_head.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(18, 31).addBox(-1.5F, -9.0F, -7.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.5F, 2.5F, 0.4363F, 0.0F, 0.0F));
        PartDefinition ear_left = mob_head.addOrReplaceChild("ear_left", CubeListBuilder.create().texOffs(32, 23).addBox(-2.25F, -1.2093F, -0.6941F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -3.5F, 1.0F, 0.4363F, 0.0F, 0.5672F));
        PartDefinition ear_right = mob_head.addOrReplaceChild("ear_right", CubeListBuilder.create().texOffs(16, 25).addBox(-0.75F, -1.2093F, -0.6941F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -3.5F, 1.0F, 0.4363F, 0.0F, -0.5672F));

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

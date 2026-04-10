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
 * Creature id: hell_horse_yitrium
 */
public class HellHorseYitriumModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart leg3;
    private final ModelPart leg4;
    private final ModelPart chest;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart tail;
    private final ModelPart head;
    private final ModelPart jaw;

    public HellHorseYitriumModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.leg3 = this.body.getChild("leg3");
        this.leg4 = this.body.getChild("leg4");
        this.chest = this.body.getChild("chest");
        this.leg1 = this.chest.getChild("leg1");
        this.leg2 = this.chest.getChild("leg2");
        this.tail = this.chest.getChild("tail");
        this.head = this.chest.getChild("head");
        this.jaw = this.head.getChild("jaw");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, 0.0F));
        PartDefinition leg3 = body.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(0, 0).addBox(-5.4F, -3.0F, 0.0F, 5.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 34).addBox(-5.4F, 4.0F, 1.0F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 9.0F));
        PartDefinition leg4 = body.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(0, 0).addBox(0.4F, -3.0F, 0.0F, 5.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 34).addBox(1.4F, 4.0F, 1.0F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 9.0F));
        PartDefinition chest = body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(41, 14).addBox(0.0F, 3.0F, -18.0F, 0.0F, 4.0F, 20.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-5.0F, -8.0F, -18.0F, 10.0F, 11.0F, 23.0F, new CubeDeformation(0.0F)).texOffs(0, 34).addBox(-5.5F, -8.2F, -19.0F, 11.0F, 14.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 9.0F));
        PartDefinition leg1 = chest.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(65, 38).addBox(-5.0F, 0.0F, -2.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, -16.0F));
        PartDefinition leg2 = chest.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(65, 38).addBox(1.0F, 0.0F, -2.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, -16.0F));
        PartDefinition tail = chest.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(66, 16).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(41, 38).addBox(-2.0F, 0.0F, 5.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, 5.0F, -0.5672F, 0.0F, 0.0F));
        PartDefinition head = chest.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 67).addBox(-2.5F, -9.1471F, -3.6696F, 5.0F, 14.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(34, 73).addBox(0.0F, -9.1471F, -7.6696F, 0.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(26, 63).addBox(0.0F, -14.1471F, 4.3304F, 0.0F, 16.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(51, 58).addBox(-3.5F, -15.6471F, -4.2696F, 7.0F, 15.0F, 9.0F, new CubeDeformation(0.0F)).texOffs(43, 0).addBox(-3.0F, -15.1471F, -5.6696F, 6.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(41, 38).addBox(-2.5F, -20.1471F, 2.3304F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(41, 38).addBox(0.5F, -20.1471F, 2.3304F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(65, 0).addBox(-2.0F, -14.1471F, -10.6696F, 4.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, -16.0F, 0.5236F, 0.0F, 0.0F));
        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 15).addBox(-2.0F, 0.0F, -4.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.1471F, -5.6696F));

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
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

    public void setAngles(LivingEntityRenderState state) {
        applyGeneratedAnimation(state);
    }

}

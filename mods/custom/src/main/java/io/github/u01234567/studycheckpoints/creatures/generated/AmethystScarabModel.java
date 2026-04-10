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
 * Creature id: amethyst_scarab
 */
public class AmethystScarabModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart node_24;
    private final ModelPart scarab;
    private final ModelPart torso;
    private final ModelPart cube;
    private final ModelPart right_wing;
    private final ModelPart cube_2;
    private final ModelPart left_wing;
    private final ModelPart cube_3;
    private final ModelPart head;
    private final ModelPart cube_4;
    private final ModelPart cube_5;
    private final ModelPart cube_6;
    private final ModelPart cube_7;
    private final ModelPart cube_8;
    private final ModelPart cube_9;
    private final ModelPart left_leg;
    private final ModelPart cube_10;
    private final ModelPart cube_11;
    private final ModelPart cube_12;
    private final ModelPart right_leg;
    private final ModelPart cube_13;
    private final ModelPart cube_14;
    private final ModelPart cube_15;

    public AmethystScarabModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.node_24 = this.root.getChild("node_24");
        this.scarab = this.node_24.getChild("scarab");
        this.torso = this.scarab.getChild("torso");
        this.cube = this.torso.getChild("cube");
        this.right_wing = this.torso.getChild("right_wing");
        this.cube_2 = this.right_wing.getChild("cube_2");
        this.left_wing = this.torso.getChild("left_wing");
        this.cube_3 = this.left_wing.getChild("cube_3");
        this.head = this.scarab.getChild("head");
        this.cube_4 = this.head.getChild("cube_4");
        this.cube_5 = this.head.getChild("cube_5");
        this.cube_6 = this.head.getChild("cube_6");
        this.cube_7 = this.head.getChild("cube_7");
        this.cube_8 = this.head.getChild("cube_8");
        this.cube_9 = this.head.getChild("cube_9");
        this.left_leg = this.scarab.getChild("left_leg");
        this.cube_10 = this.left_leg.getChild("cube_10");
        this.cube_11 = this.left_leg.getChild("cube_11");
        this.cube_12 = this.left_leg.getChild("cube_12");
        this.right_leg = this.scarab.getChild("right_leg");
        this.cube_13 = this.right_leg.getChild("cube_13");
        this.cube_14 = this.right_leg.getChild("cube_14");
        this.cube_15 = this.right_leg.getChild("cube_15");
    }

    public static LayerDefinition getLayerDefinition() {
        return getTexturedModelData();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition node_24 = root.addOrReplaceChild("node_24", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition scarab = node_24.addOrReplaceChild("scarab", CubeListBuilder.create(), PartPose.offsetAndRotation(0.1459F, -0.4689F, 0.0942F, 3.1416F, 0.0F, 3.1416F));
        PartDefinition torso = scarab.addOrReplaceChild("torso", CubeListBuilder.create(), PartPose.offset(0.1481F, -2.8911F, -3.2862F));
        PartDefinition cube = torso.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 0).addBox(-2.3719F, -4.5711F, -7.4862F, 5.04F, 5.04F, 8.4F, new CubeDeformation(0.0F)), PartPose.offset(-0.1481F, 2.0511F, 3.2862F));
        PartDefinition right_wing = torso.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offset(-2.58F, -2.5192F, -0.84F));
        PartDefinition cube_2 = right_wing.addOrReplaceChild("cube_2", CubeListBuilder.create().texOffs(28, 0).addBox(-4.2F, -0.085F, -5.04F, 4.2F, 0.001F, 8.4F, new CubeDeformation(0.0F)), PartPose.offset(0.06F, 0.084F, 0.84F));
        PartDefinition left_wing = torso.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.offset(2.46F, -2.5192F, -0.84F));
        PartDefinition cube_3 = left_wing.addOrReplaceChild("cube_3", CubeListBuilder.create().texOffs(54, 0).addBox(0.0F, -0.001F, -5.04F, 4.2F, 0.001F, 8.4F, new CubeDeformation(0.0F)), PartPose.offset(0.06F, 0.0F, 0.84F));
        PartDefinition head = scarab.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.1341F, -2.1569F, 0.6499F));
        PartDefinition cube_4 = head.addOrReplaceChild("cube_4", CubeListBuilder.create().texOffs(80, 0).addBox(-1.5319F, -3.7311F, 0.9138F, 3.36F, 3.36F, 3.36F, new CubeDeformation(0.0F)), PartPose.offset(-0.1341F, 2.1569F, -0.6499F));
        PartDefinition cube_5 = head.addOrReplaceChild("cube_5", CubeListBuilder.create().texOffs(94, 0).addBox(-1.386F, -0.924F, -1.47F, 5.04F, 3.024F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.316F, -1.2214F, 3.2295F, 0.3927F, 0.0F, 0.0F));
        PartDefinition cube_6 = head.addOrReplaceChild("cube_6", CubeListBuilder.create().texOffs(108, 0).addBox(-6.258F, -0.924F, -1.47F, 5.04F, 3.024F, 0.001F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.232F, -1.2214F, 3.2295F, 0.3927F, 0.0F, 0.0F));
        PartDefinition cube_7 = head.addOrReplaceChild("cube_7", CubeListBuilder.create().texOffs(122, 0).addBox(-3.5479F, -1.6321F, 2.4258F, 7.392F, 0.001F, 4.368F, new CubeDeformation(0.0F)), PartPose.offset(-0.1341F, 2.9129F, 0.2501F));
        PartDefinition cube_8 = head.addOrReplaceChild("cube_8", CubeListBuilder.create().texOffs(146, 0).addBox(-0.4536F, -1.2516F, -0.1764F, 0.8232F, 2.5032F, 0.0168F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.316F, -2.8025F, 3.2969F, 0.3927F, 0.0F, 0.0F));
        PartDefinition cube_9 = head.addOrReplaceChild("cube_9", CubeListBuilder.create().texOffs(152, 0).addBox(-2.9736F, -1.2516F, -0.1764F, 0.8232F, 2.5032F, 0.0168F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.316F, -2.8025F, 3.2969F, 0.3927F, 0.0F, 0.0F));
        PartDefinition left_leg = scarab.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(2.4581F, -1.9911F, -3.2862F));
        PartDefinition cube_10 = left_leg.addOrReplaceChild("cube_10", CubeListBuilder.create().texOffs(158, 0).addBox(1.4081F, -1.7151F, -1.1862F, 1.26F, 2.52F, 1.68F, new CubeDeformation(0.0F)), PartPose.offset(-2.0381F, 1.6551F, 3.2862F));
        PartDefinition cube_11 = left_leg.addOrReplaceChild("cube_11", CubeListBuilder.create().texOffs(166, 0).addBox(1.4081F, -1.7151F, -4.1262F, 1.26F, 2.52F, 1.68F, new CubeDeformation(0.0F)), PartPose.offset(-2.0381F, 1.6551F, 3.2862F));
        PartDefinition cube_12 = left_leg.addOrReplaceChild("cube_12", CubeListBuilder.create().texOffs(174, 0).addBox(1.4081F, -1.7151F, -7.0662F, 1.26F, 2.52F, 1.68F, new CubeDeformation(0.0F)), PartPose.offset(-2.0381F, 1.6551F, 3.2862F));
        PartDefinition right_leg = scarab.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-2.1619F, -1.9911F, -3.2862F));
        PartDefinition cube_13 = right_leg.addOrReplaceChild("cube_13", CubeListBuilder.create().texOffs(182, 0).addBox(-2.0801F, -1.7151F, -1.1862F, 1.26F, 2.52F, 1.68F, new CubeDeformation(0.0F)), PartPose.offset(1.4501F, 1.6551F, 3.2862F));
        PartDefinition cube_14 = right_leg.addOrReplaceChild("cube_14", CubeListBuilder.create().texOffs(190, 0).addBox(-2.0801F, -1.7151F, -4.1262F, 1.26F, 2.52F, 1.68F, new CubeDeformation(0.0F)), PartPose.offset(1.4501F, 1.6551F, 3.2862F));
        PartDefinition cube_15 = right_leg.addOrReplaceChild("cube_15", CubeListBuilder.create().texOffs(198, 0).addBox(-2.0801F, -1.7151F, -7.0662F, 1.26F, 2.52F, 1.68F, new CubeDeformation(0.0F)), PartPose.offset(1.4501F, 1.6551F, 3.2862F));

        return LayerDefinition.create(meshDefinition, 512, 16);
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

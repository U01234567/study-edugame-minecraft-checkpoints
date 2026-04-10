// Generated from dragon/source/model.gltf as a Class A blockbench package source.
// Original runtime texture: copy ./textures/gltf_embedded_0.png to ./model.png unchanged.
// Export target: Minecraft 1.17+ for Yarn.

package com.example.mod;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.model.EntityModel;

public class model extends EntityModel<Entity> {
    private final ModelPart root;

    public model(ModelPart root) {
        this.root = root.getChild("root");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData wings3 = root.addChild("wings3", ModelPartBuilder.create(), ModelTransform.of(3.0F, -12.0F, -2.0F, 0.0F, 0.0F, 0.6109F));
        ModelPartData cube_0 = wings3.addChild("cube_0", ModelPartBuilder.create().uv(24, 0).mirrored().cuboid(3.0F, -15.0F, -2.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-3.0F, 12.0F, 2.0F));
        ModelPartData cube_1 = wings3.addChild("cube_1", ModelPartBuilder.create().uv(36, 11).mirrored().cuboid(3.0F, -13.0F, -1.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-3.0F, 12.0F, 2.0F));
        ModelPartData cube_2 = wings3.addChild("cube_2", ModelPartBuilder.create().uv(21, 13).mirrored().cuboid(3.0F, -15.0F, -1.0F, 0.0F, 2.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-3.0F, 12.0F, 2.0F));
        ModelPartData wings4 = wings3.addChild("wings4", ModelPartBuilder.create(), ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.5236F));
        ModelPartData cube_3 = wings4.addChild("cube_3", ModelPartBuilder.create().uv(24, 0).mirrored().cuboid(3.0F, -18.0F, -2.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-3.0F, 15.0F, 2.0F));
        ModelPartData cube_4 = wings4.addChild("cube_4", ModelPartBuilder.create().uv(19, 10).mirrored().cuboid(3.0F, -18.0F, -1.0F, 0.0F, 2.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-3.0F, 15.0F, 2.0F));
        ModelPartData cube_5 = wings4.addChild("cube_5", ModelPartBuilder.create().uv(35, 10).mirrored().cuboid(3.0F, -16.0F, -1.0F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-3.0F, 15.0F, 2.0F));
        ModelPartData wings5 = wings4.addChild("wings5", ModelPartBuilder.create(), ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.6109F));
        ModelPartData cube_6 = wings5.addChild("cube_6", ModelPartBuilder.create().uv(48, 48).mirrored().cuboid(3.0F, -25.0F, -2.0F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-3.0F, 18.0F, 2.0F));
        ModelPartData cube_7 = wings5.addChild("cube_7", ModelPartBuilder.create().uv(18, 8).mirrored().cuboid(3.0F, -24.0F, -1.0F, 0.0F, 5.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-3.0F, 18.0F, 2.0F));
        ModelPartData cube_8 = wings5.addChild("cube_8", ModelPartBuilder.create().uv(26, 31).mirrored().cuboid(3.0F, -25.0F, -1.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-3.0F, 18.0F, 2.0F));
        ModelPartData cube_9 = wings5.addChild("cube_9", ModelPartBuilder.create().uv(26, 31).mirrored().cuboid(3.0F, -19.0F, -1.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-3.0F, 18.0F, 2.0F));
        ModelPartData leg3 = root.addChild("leg3", ModelPartBuilder.create(), ModelTransform.of(4.0F, -7.5F, -3.5F, 0.0F, 0.0F, -0.7854F));
        ModelPartData cube_10 = leg3.addChild("cube_10", ModelPartBuilder.create().uv(45, 25).cuboid(4.0F, -10.5F, -4.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, 7.5F, 3.5F));
        ModelPartData leg4 = leg3.addChild("leg4", ModelPartBuilder.create(), ModelTransform.of(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3491F));
        ModelPartData cube_11 = leg4.addChild("cube_11", ModelPartBuilder.create().uv(45, 25).cuboid(4.1F, -7.4F, -4.4F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-7.0F, 7.4F, 3.5F));
        ModelPartData leg5 = leg4.addChild("leg5", ModelPartBuilder.create(), ModelTransform.of(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.3491F));
        ModelPartData cube_12 = leg5.addChild("cube_12", ModelPartBuilder.create().uv(45, 25).cuboid(4.0F, -4.5F, -4.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-7.0F, 4.3F, 3.5F));
        ModelPartData head0 = root.addChild("head0", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -14.0F, -5.0F));
        ModelPartData cube_13 = head0.addChild("cube_13", ModelPartBuilder.create().uv(0, 14).mirrored().cuboid(-3.0F, -17.0F, -9.0F, 6.0F, 5.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 14.0F, 5.0F));
        ModelPartData cube_14 = head0.addChild("cube_14", ModelPartBuilder.create().uv(30, 47).mirrored().cuboid(-2.0F, -14.0F, -11.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 14.0F, 5.0F));
        ModelPartData cube_15 = head0.addChild("cube_15", ModelPartBuilder.create().uv(13, 5).cuboid(-2.0F, -3.0F, 0.0F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.9F, -2.0F, 0.0F, 0.1745F, -0.2618F, -0.4363F));
        ModelPartData cube_16 = head0.addChild("cube_16", ModelPartBuilder.create().uv(13, 5).mirrored().cuboid(0.0F, -3.0F, 0.0F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.9F, -2.0F, 0.0F, 0.1745F, 0.2618F, 0.4363F));
        ModelPartData cube_17 = head0.addChild("cube_17", ModelPartBuilder.create().uv(14, 25).mirrored().cuboid(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-0.5F, -3.0F, 1.0F, -0.6109F, 0.0F, 0.0F));
        ModelPartData cube_18 = head0.addChild("cube_18", ModelPartBuilder.create().uv(14, 25).mirrored().cuboid(-0.5F, -18.5F, -5.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 14.0F, 5.0F));
        ModelPartData cube_19 = head0.addChild("cube_19", ModelPartBuilder.create().uv(14, 25).mirrored().cuboid(-0.5F, -18.0F, -7.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 14.0F, 5.0F));
        ModelPartData wings0 = root.addChild("wings0", ModelPartBuilder.create(), ModelTransform.of(-3.0F, -12.0F, -2.0F, 0.0F, 0.0F, -0.6109F));
        ModelPartData cube_20 = wings0.addChild("cube_20", ModelPartBuilder.create().uv(24, 0).mirrored().cuboid(-4.0F, -15.0F, -2.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(3.0F, 12.0F, 2.0F));
        ModelPartData cube_21 = wings0.addChild("cube_21", ModelPartBuilder.create().uv(36, 11).mirrored().cuboid(-4.0F, -13.0F, -1.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(3.0F, 12.0F, 2.0F));
        ModelPartData cube_22 = wings0.addChild("cube_22", ModelPartBuilder.create().uv(21, 13).cuboid(-3.0F, -15.0F, -1.0F, 0.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, 12.0F, 2.0F));
        ModelPartData wings1 = wings0.addChild("wings1", ModelPartBuilder.create(), ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.5236F));
        ModelPartData cube_23 = wings1.addChild("cube_23", ModelPartBuilder.create().uv(24, 0).mirrored().cuboid(-4.0F, -18.0F, -2.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(3.0F, 15.0F, 2.0F));
        ModelPartData cube_24 = wings1.addChild("cube_24", ModelPartBuilder.create().uv(19, 10).mirrored().cuboid(-3.0F, -18.0F, -1.0F, 0.0F, 2.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(3.0F, 15.0F, 2.0F));
        ModelPartData cube_25 = wings1.addChild("cube_25", ModelPartBuilder.create().uv(35, 10).mirrored().cuboid(-4.0F, -16.0F, -1.0F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(3.0F, 15.0F, 2.0F));
        ModelPartData wings2 = wings1.addChild("wings2", ModelPartBuilder.create(), ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.6109F));
        ModelPartData cube_26 = wings2.addChild("cube_26", ModelPartBuilder.create().uv(48, 48).mirrored().cuboid(-4.0F, -25.0F, -2.0F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(3.0F, 18.0F, 2.0F));
        ModelPartData cube_27 = wings2.addChild("cube_27", ModelPartBuilder.create().uv(18, 8).cuboid(-3.0F, -24.0F, -1.0F, 0.0F, 5.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, 18.0F, 2.0F));
        ModelPartData cube_28 = wings2.addChild("cube_28", ModelPartBuilder.create().uv(26, 31).mirrored().cuboid(-4.0F, -25.0F, -1.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(3.0F, 18.0F, 2.0F));
        ModelPartData cube_29 = wings2.addChild("cube_29", ModelPartBuilder.create().uv(26, 31).mirrored().cuboid(-4.0F, -19.0F, -1.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(3.0F, 18.0F, 2.0F));
        ModelPartData tail0 = root.addChild("tail0", ModelPartBuilder.create(), ModelTransform.of(0.5F, -11.5F, 6.0F, -0.6109F, 0.0F, 0.0F));
        ModelPartData cube_30 = tail0.addChild("cube_30", ModelPartBuilder.create().uv(24, 0).mirrored().cuboid(-1.5F, -11.5F, 6.0F, 3.0F, 3.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-0.5F, 11.5F, -6.0F));
        ModelPartData tail1 = tail0.addChild("tail1", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 3.0F, -0.3491F, 0.0F, 0.0F));
        ModelPartData cube_31 = tail1.addChild("cube_31", ModelPartBuilder.create().uv(34, 9).mirrored().cuboid(-1.2F, -11.7F, 9.8F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-0.5F, 11.5F, -9.0F));
        ModelPartData tail2 = tail1.addChild("tail2", ModelPartBuilder.create(), ModelTransform.of(0.0F, -0.5F, 3.0F, -0.3491F, 0.0F, 0.0F));
        ModelPartData cube_32 = tail2.addChild("cube_32", ModelPartBuilder.create().uv(35, 10).mirrored().cuboid(-0.7F, -12.2F, 14.8F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-0.5F, 12.0F, -12.0F));
        ModelPartData leg9 = root.addChild("leg9", ModelPartBuilder.create(), ModelTransform.of(2.5F, -8.5F, 4.5F, 0.0F, 0.0F, -0.5236F));
        ModelPartData cube_33 = leg9.addChild("cube_33", ModelPartBuilder.create().uv(45, 25).cuboid(1.5F, -10.0F, 2.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.5F, 8.5F, -4.5F));
        ModelPartData leg10 = leg9.addChild("leg10", ModelPartBuilder.create(), ModelTransform.of(2.0F, 1.5F, 0.5F, 0.0F, 0.0F, 0.2618F));
        ModelPartData cube_34 = leg10.addChild("cube_34", ModelPartBuilder.create().uv(45, 25).cuboid(1.6F, -6.9F, 2.6F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.5F, 6.9F, -5.0F));
        ModelPartData leg11 = leg10.addChild("leg11", ModelPartBuilder.create(), ModelTransform.of(0.0F, 3.0F, -0.6F, 0.0F, 0.0F, 0.2618F));
        ModelPartData cube_35 = leg11.addChild("cube_35", ModelPartBuilder.create().uv(16, 36).mirrored().cuboid(1.5F, -4.0F, 1.5F, 3.0F, 2.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-4.5F, 3.8F, -4.4F));
        ModelPartData leg6 = root.addChild("leg6", ModelPartBuilder.create(), ModelTransform.of(-2.5F, -8.5F, 4.5F, 0.0F, 0.0F, 0.5236F));
        ModelPartData cube_36 = leg6.addChild("cube_36", ModelPartBuilder.create().uv(45, 25).mirrored().cuboid(-4.5F, -10.0F, 2.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(2.5F, 8.5F, -4.5F));
        ModelPartData leg7 = leg6.addChild("leg7", ModelPartBuilder.create(), ModelTransform.of(-2.0F, 1.5F, 0.5F, 0.0F, 0.0F, -0.2618F));
        ModelPartData cube_37 = leg7.addChild("cube_37", ModelPartBuilder.create().uv(45, 25).mirrored().cuboid(-4.4F, -6.9F, 2.6F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(4.5F, 6.9F, -5.0F));
        ModelPartData leg8 = leg7.addChild("leg8", ModelPartBuilder.create(), ModelTransform.of(0.0F, 3.0F, -0.6F, 0.0F, 0.0F, -0.2618F));
        ModelPartData cube_38 = leg8.addChild("cube_38", ModelPartBuilder.create().uv(16, 36).mirrored().cuboid(-4.5F, -4.0F, 1.5F, 3.0F, 2.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(4.5F, 3.8F, -4.4F));
        ModelPartData leg0 = root.addChild("leg0", ModelPartBuilder.create(), ModelTransform.of(-4.0F, -7.5F, -3.5F, 0.0F, 0.0F, 0.7854F));
        ModelPartData cube_39 = leg0.addChild("cube_39", ModelPartBuilder.create().uv(45, 25).mirrored().cuboid(-7.0F, -10.5F, -4.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(4.0F, 7.5F, 3.5F));
        ModelPartData leg1 = leg0.addChild("leg1", ModelPartBuilder.create(), ModelTransform.of(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3491F));
        ModelPartData cube_40 = leg1.addChild("cube_40", ModelPartBuilder.create().uv(45, 25).mirrored().cuboid(-6.9F, -7.4F, -4.4F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(7.0F, 7.4F, 3.5F));
        ModelPartData leg2 = leg1.addChild("leg2", ModelPartBuilder.create(), ModelTransform.of(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.3491F));
        ModelPartData cube_41 = leg2.addChild("cube_41", ModelPartBuilder.create().uv(45, 25).mirrored().cuboid(-7.0F, -4.5F, -4.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(7.0F, 4.3F, 3.5F));
        ModelPartData body0 = root.addChild("body0", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        ModelPartData cube_42 = body0.addChild("cube_42", ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(-4.0F, -12.0F, -5.0F, 8.0F, 6.0F, 8.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        ModelPartData cube_43 = body0.addChild("cube_43", ModelPartBuilder.create().uv(0, 28).cuboid(-3.5F, -11.5F, 3.0F, 7.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        ModelPartData cube_44 = body0.addChild("cube_44", ModelPartBuilder.create().uv(19, 20).mirrored().cuboid(-1.0F, -6.0F, 0.0F, 5.0F, 6.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-1.5F, -7.0F, -5.0F, 0.4363F, 0.0F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}
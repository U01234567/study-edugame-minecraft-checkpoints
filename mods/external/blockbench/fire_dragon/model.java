// Generated from fire_dragon/source/model.gltf as a Class B blockbench package source.
// Runtime texture is produced by texture_fix.py as ./model.png from ./textures/gltf_embedded_0.png.
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
        ModelPartData node_161 = root.addChild("node_161", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        ModelPartData bone2 = node_161.addChild("bone2", ModelPartBuilder.create(), ModelTransform.pivot(6.0F, -7.0F, 18.0F));
        ModelPartData body = bone2.addChild("body", ModelPartBuilder.create(), ModelTransform.of(-6.0F, -10.0F, -18.0F, -0.2182F, 0.0F, 0.0F));
        ModelPartData cola0 = body.addChild("cola0", ModelPartBuilder.create(), ModelTransform.of(0.0F, -8.0F, 8.0F, -0.3927F, 0.0F, 0.0F));
        ModelPartData cola1 = cola0.addChild("cola1", ModelPartBuilder.create(), ModelTransform.of(0.0F, 8.0F, 19.0F, -0.3927F, 0.0F, 0.0F));
        ModelPartData cola2 = cola1.addChild("cola2", ModelPartBuilder.create(), ModelTransform.of(0.0F, -0.8578F, 16.7014F, 0.4363F, 0.0F, 0.0F));
        ModelPartData cola3 = cola2.addChild("cola3", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 15.0F, 0.0873F, 0.0F, 0.0F));
        ModelPartData cola4 = cola3.addChild("cola4", ModelPartBuilder.create(), ModelTransform.of(0.0F, 1.0F, 15.0F, -0.3491F, 0.0F, 0.0F));
        ModelPartData cososcola = cola4.addChild("cososcola", ModelPartBuilder.create(), ModelTransform.of(0.0F, -3.0F, 13.5F, 0.5672F, 0.0F, 0.0F));
        ModelPartData SSS = cososcola.addChild("SSS", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -7.0F, -3.5F, 3.0F, 8.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.4582F, 1.3036F, -1.006F, -0.437F, 2.2116F));
        ModelPartData SSS_2 = cososcola.addChild("SSS_2", ModelPartBuilder.create().uv(16, 0).cuboid(-1.5F, -7.0F, -3.5F, 3.0F, 8.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.4582F, 1.3036F, -1.006F, 0.437F, -2.2116F));
        ModelPartData SSS_3 = cososcola.addChild("SSS_3", ModelPartBuilder.create().uv(32, 0).cuboid(-8.7209F, -1.2694F, -4.1461F, 3.0F, 8.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.4582F, 1.3036F, -1.006F, -0.437F, 2.2116F));
        ModelPartData SSS_4 = cososcola.addChild("SSS_4", ModelPartBuilder.create().uv(48, 0).cuboid(5.7209F, -1.2694F, -4.1461F, 3.0F, 8.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.4582F, 1.3036F, -1.006F, 0.437F, -2.2116F));
        ModelPartData cube = cola4.addChild("cube", ModelPartBuilder.create().uv(64, 0).cuboid(-1.0F, 5.0F, 16.0F, 2.0F, 4.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -7.9658F, -16.5229F));
        ModelPartData cube_2 = cola3.addChild("cube_2", ModelPartBuilder.create().uv(102, 0).cuboid(-2.0F, 3.0F, 0.0F, 4.0F, 5.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, 0.0F));
        ModelPartData cube_3 = cola3.addChild("cube_3", ModelPartBuilder.create().uv(144, 0).cuboid(-11.3137F, 0.0F, 0.1F, 2.8284F, 2.8284F, 16.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.7854F));
        ModelPartData cube_4 = cola3.addChild("cube_4", ModelPartBuilder.create().uv(184, 0).cuboid(-2.0F, 19.8163F, 16.7476F, 4.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -30.0F, 0.7854F, 0.0F, 0.0F));
        ModelPartData cube_5 = cola2.addChild("cube_5", ModelPartBuilder.create().uv(206, 0).cuboid(-3.0F, 2.0F, 0.0F, 6.0F, 7.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, 0.0F));
        ModelPartData cube_6 = cola2.addChild("cube_6", ModelPartBuilder.create().uv(252, 0).cuboid(-12.7279F, 0.0F, 0.1F, 4.2426F, 4.2426F, 16.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.7854F));
        ModelPartData cube_7 = cola2.addChild("cube_7", ModelPartBuilder.create().uv(294, 0).cuboid(-2.0F, 13.0337F, 12.1302F, 4.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -15.0F, 0.7854F, 0.0F, 0.0F));
        ModelPartData cube_8 = cola1.addChild("cube_8", ModelPartBuilder.create().uv(316, 0).cuboid(-13.435F, -0.7071F, 0.1F, 5.6569F, 5.6569F, 19.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -6.8578F, -1.2986F, 0.0F, 0.0F, -0.7854F));
        ModelPartData cube_9 = cola1.addChild("cube_9", ModelPartBuilder.create().uv(368, 0).cuboid(-4.0F, 1.0F, 0.0F, 8.0F, 8.0F, 19.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.8578F, -1.2986F));
        ModelPartData cube_10 = cola1.addChild("cube_10", ModelPartBuilder.create().uv(424, 0).cuboid(-2.0F, 4.0F, 4.0F, 4.0F, 5.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.8578F, -6.2986F, 0.7854F, 0.0F, 0.0F));
        ModelPartData cube_11 = cola1.addChild("cube_11", ModelPartBuilder.create().uv(448, 0).cuboid(-2.0F, 5.0F, 5.0F, 4.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.8578F, 1.7014F, 0.7854F, 0.0F, 0.0F));
        ModelPartData patas = cola0.addChild("patas", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 8.0F, -8.0F));
        ModelPartData leftPata = patas.addChild("leftPata", ModelPartBuilder.create(), ModelTransform.of(4.0F, -3.9176F, 14.3954F, 0.3054F, 0.0F, -0.3054F));
        ModelPartData pata1 = leftPata.addChild("pata1", ModelPartBuilder.create(), ModelTransform.of(2.0F, 11.0F, 2.0F, -0.387F, 0.0505F, 0.2995F));
        ModelPartData pata2 = pata1.addChild("pata2", ModelPartBuilder.create(), ModelTransform.pivot(4.0F, 4.0F, 12.0F));
        ModelPartData pata3 = pata2.addChild("pata3", ModelPartBuilder.create(), ModelTransform.of(-5.0F, 5.0F, 7.0F, -0.3054F, 0.0F, 0.0F));
        ModelPartData dedos = pata3.addChild("dedos", ModelPartBuilder.create(), ModelTransform.of(3.0F, 8.0F, 0.0F, -0.3927F, 0.0F, 0.0F));
        ModelPartData cube_12 = dedos.addChild("cube_12", ModelPartBuilder.create().uv(470, 0).cuboid(1.0F, 5.0F, -2.0F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -5.0F, -1.0F));
        ModelPartData cube_13 = dedos.addChild("cube_13", ModelPartBuilder.create().uv(482, 0).cuboid(-2.5F, -1.25F, 0.0F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 0.0F, -3.0F, 0.0F, 0.0F, -0.3491F));
        ModelPartData cube_14 = dedos.addChild("cube_14", ModelPartBuilder.create().uv(494, 0).cuboid(19.3307F, -8.1006F, -0.1472F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-24.0F, 0.0F, -3.0F, 0.0F, 0.0F, 0.3491F));
        ModelPartData cube_15 = dedos.addChild("cube_15", ModelPartBuilder.create().uv(0, 29).cuboid(-1.5F, 4.75F, 0.0F, 0.001F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 0.0F, -3.0F, 0.0F, 0.0F, -0.3491F));
        ModelPartData cube_16 = dedos.addChild("cube_16", ModelPartBuilder.create().uv(10, 29).cuboid(2.0F, 11.0F, -2.0F, 0.001F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -5.0F, -1.0F));
        ModelPartData cube_17 = dedos.addChild("cube_17", ModelPartBuilder.create().uv(20, 29).cuboid(20.3307F, -2.1006F, -0.1472F, 0.001F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-24.0F, 0.0F, -3.0F, 0.0F, 0.0F, 0.3491F));
        ModelPartData cube_18 = pata3.addChild("cube_18", ModelPartBuilder.create().uv(30, 29).cuboid(-1.0F, -3.0F, -3.0F, 6.0F, 8.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, 3.0F, -1.0F));
        ModelPartData cube_19 = pata2.addChild("cube_19", ModelPartBuilder.create().uv(52, 29).cuboid(8.0F, 8.0F, 32.0F, 4.0F, 8.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-12.0F, -8.0F, -32.0F));
        ModelPartData cube_20 = pata1.addChild("cube_20", ModelPartBuilder.create().uv(74, 29).cuboid(-7.0F, -4.0F, -1.0F, 6.0F, 8.0F, 19.0F, new Dilation(0.0F)), ModelTransform.pivot(6.0F, 0.0F, 0.0F));
        ModelPartData cube_21 = pata1.addChild("cube_21", ModelPartBuilder.create().uv(126, 29).cuboid(2.3718F, -18.6569F, -5.9246F, 8.0F, 17.0F, 0.001F, new Dilation(0.0F)), ModelTransform.of(-5.9241F, 2.9515F, -0.0849F, -0.5335F, -1.1618F, 1.1858F));
        ModelPartData cube_22 = leftPata.addChild("cube_22", ModelPartBuilder.create().uv(146, 29).cuboid(-1.0F, -1.0F, -1.0F, 9.0F, 15.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        ModelPartData cube_23 = leftPata.addChild("cube_23", ModelPartBuilder.create().uv(190, 29).cuboid(-4.373F, -53.0051F, 19.1542F, 8.0F, 17.0F, 0.001F, new Dilation(0.0F)), ModelTransform.of(-4.0F, -0.0824F, -43.3954F, -0.84F, 0.1279F, 1.7275F));
        ModelPartData rightPata = patas.addChild("rightPata", ModelPartBuilder.create(), ModelTransform.of(-4.0F, -3.9176F, 14.3954F, 0.3054F, 0.0F, 0.3054F));
        ModelPartData pata4 = rightPata.addChild("pata4", ModelPartBuilder.create(), ModelTransform.of(-2.0F, 11.0F, 2.0F, -0.387F, -0.0505F, -0.2995F));
        ModelPartData pata5 = pata4.addChild("pata5", ModelPartBuilder.create(), ModelTransform.pivot(-4.0F, 4.0F, 12.0F));
        ModelPartData pata6 = pata5.addChild("pata6", ModelPartBuilder.create(), ModelTransform.of(5.0F, 5.0F, 7.0F, -0.3054F, 0.0F, 0.0F));
        ModelPartData dedos4 = pata6.addChild("dedos4", ModelPartBuilder.create(), ModelTransform.of(-3.0F, 8.0F, 0.0F, -0.3927F, 0.0F, 0.0F));
        ModelPartData cube_24 = dedos4.addChild("cube_24", ModelPartBuilder.create().uv(210, 29).cuboid(-3.0F, 5.0F, -2.0F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, -5.0F, -1.0F));
        ModelPartData cube_25 = dedos4.addChild("cube_25", ModelPartBuilder.create().uv(222, 29).cuboid(0.5F, -1.25F, 0.0F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 0.0F, -3.0F, 0.0F, 0.0F, 0.3491F));
        ModelPartData cube_26 = dedos4.addChild("cube_26", ModelPartBuilder.create().uv(234, 29).cuboid(-21.3307F, -8.1006F, -0.1472F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(24.0F, 0.0F, -3.0F, 0.0F, 0.0F, -0.3491F));
        ModelPartData cube_27 = dedos4.addChild("cube_27", ModelPartBuilder.create().uv(246, 29).cuboid(-20.3307F, -2.1006F, -0.1472F, 0.001F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(24.0F, 0.0F, -3.0F, 0.0F, 0.0F, -0.3491F));
        ModelPartData cube_28 = dedos4.addChild("cube_28", ModelPartBuilder.create().uv(256, 29).cuboid(-2.0F, 11.0F, -2.0F, 0.001F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, -5.0F, -1.0F));
        ModelPartData cube_29 = dedos4.addChild("cube_29", ModelPartBuilder.create().uv(266, 29).cuboid(1.5F, 4.75F, 0.0F, 0.001F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 0.0F, -3.0F, 0.0F, 0.0F, 0.3491F));
        ModelPartData cube_30 = pata6.addChild("cube_30", ModelPartBuilder.create().uv(276, 29).cuboid(-5.0F, -3.0F, -3.0F, 6.0F, 8.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 3.0F, -1.0F));
        ModelPartData cube_31 = pata5.addChild("cube_31", ModelPartBuilder.create().uv(298, 29).cuboid(-12.0F, 8.0F, 32.0F, 4.0F, 8.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(12.0F, -8.0F, -32.0F));
        ModelPartData cube_32 = pata4.addChild("cube_32", ModelPartBuilder.create().uv(320, 29).cuboid(1.0F, -4.0F, -1.0F, 6.0F, 8.0F, 19.0F, new Dilation(0.0F)), ModelTransform.pivot(-6.0F, 0.0F, 0.0F));
        ModelPartData cube_33 = pata4.addChild("cube_33", ModelPartBuilder.create().uv(372, 29).cuboid(-10.3719F, -18.6569F, -5.9246F, 8.0F, 17.0F, 0.001F, new Dilation(0.0F)), ModelTransform.of(5.9241F, 2.9515F, -0.0849F, -0.5335F, 1.1618F, -1.1858F));
        ModelPartData cube_34 = rightPata.addChild("cube_34", ModelPartBuilder.create().uv(392, 29).cuboid(-8.0F, -1.0F, -1.0F, 9.0F, 15.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        ModelPartData cube_35 = rightPata.addChild("cube_35", ModelPartBuilder.create().uv(436, 29).cuboid(-3.627F, -53.0051F, 19.1542F, 8.0F, 17.0F, 0.001F, new Dilation(0.0F)), ModelTransform.of(4.0F, -0.0824F, -43.3954F, -0.84F, -0.1279F, -1.7275F));
        ModelPartData cube_36 = cola0.addChild("cube_36", ModelPartBuilder.create().uv(0, 58).cuboid(-6.0F, -1.0F, 0.0F, 12.0F, 10.0F, 19.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 0.0F));
        ModelPartData cube_37 = cola0.addChild("cube_37", ModelPartBuilder.create().uv(64, 58).cuboid(-14.8492F, -2.1213F, 0.0F, 8.4853F, 8.4853F, 19.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.7854F));
        ModelPartData cube_38 = cola0.addChild("cube_38", ModelPartBuilder.create().uv(120, 58).cuboid(-2.0F, 4.0F, 2.0F, 4.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, 5.0F, 1.1345F, 0.0F, 0.0F));
        ModelPartData cube_39 = cola0.addChild("cube_39", ModelPartBuilder.create().uv(140, 58).cuboid(-7.6721F, 2.8897F, 3.9942F, 4.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -9.0F, -1.0F, -0.2635F, 0.2081F, -0.2362F));
        ModelPartData cube_40 = cola0.addChild("cube_40", ModelPartBuilder.create().uv(162, 58).cuboid(3.6721F, 2.8897F, 3.9942F, 4.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -9.0F, -1.0F, -0.2635F, -0.2081F, 0.2362F));
        ModelPartData cuello1 = body.addChild("cuello1", ModelPartBuilder.create(), ModelTransform.of(0.0F, -3.0F, -9.0F, 0.1309F, 0.0F, 0.0F));
        ModelPartData cuello2 = cuello1.addChild("cuello2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -8.0F));
        ModelPartData HEAD = cuello2.addChild("HEAD", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        ModelPartData mandibula = HEAD.addChild("mandibula", ModelPartBuilder.create(), ModelTransform.of(0.0F, -0.75F, -8.75F, 0.3491F, 0.0F, 0.0F));
        ModelPartData cube_41 = mandibula.addChild("cube_41", ModelPartBuilder.create().uv(184, 58).cuboid(-3.0F, 6.0F, -1.0F, 6.0F, 4.0F, 13.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, -11.0F));
        ModelPartData cube_42 = mandibula.addChild("cube_42", ModelPartBuilder.create().uv(224, 58).cuboid(-1.0F, 0.0F, -5.0F, 4.0F, 4.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 0.0F, -3.0F, 0.1147F, 0.386F, 0.0637F));
        ModelPartData cube_43 = mandibula.addChild("cube_43", ModelPartBuilder.create().uv(256, 58).cuboid(-3.0F, 0.0F, -5.0F, 4.0F, 4.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, 0.0F, -3.0F, 0.1147F, -0.386F, -0.0637F));
        ModelPartData cube_44 = mandibula.addChild("cube_44", ModelPartBuilder.create().uv(288, 58).cuboid(-1.4196F, -3.2373F, -8.1743F, 4.6569F, 4.6569F, 9.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 5.0F, 2.0F, -0.2194F, -0.2143F, -0.7617F));
        ModelPartData cosoflotante = HEAD.addChild("cosoflotante", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -17.9F, -3.225F));
        ModelPartData cube_45 = cosoflotante.addChild("cube_45", ModelPartBuilder.create().uv(318, 58).cuboid(-3.0F, -12.6813F, -2.9902F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 9.6689F, -0.0077F));
        ModelPartData cube_46 = cosoflotante.addChild("cube_46", ModelPartBuilder.create().uv(344, 58).cuboid(-4.0F, -13.6813F, -3.9902F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 9.6689F, -0.0077F));
        ModelPartData cube_47 = cosoflotante.addChild("cube_47", ModelPartBuilder.create().uv(378, 58).cuboid(-3.5F, -13.1813F, -3.4902F, 7.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 9.6689F, -0.0077F));
        ModelPartData cube_48 = HEAD.addChild("cube_48", ModelPartBuilder.create().uv(408, 58).cuboid(-1.8F, 1.0F, -1.0F, 4.0F, 4.0F, 13.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, -19.0F));
        ModelPartData cube_49 = HEAD.addChild("cube_49", ModelPartBuilder.create().uv(444, 58).cuboid(-3.0F, -15.8944F, -1.1358F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, -11.0F, -0.5197F, -0.2667F, 1.0296F));
        ModelPartData cube_50 = HEAD.addChild("cube_50", ModelPartBuilder.create().uv(462, 58).cuboid(-2.0F, 0.0F, 0.0F, 4.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -18.0F, 0.3927F, 0.0F, 0.0F));
        ModelPartData cube_51 = HEAD.addChild("cube_51", ModelPartBuilder.create().uv(486, 58).cuboid(-1.0F, -5.0F, -5.0F, 4.0F, 4.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, -1.0F, -10.0F, 0.2875F, 0.4205F, 0.1201F));
        ModelPartData cube_52 = HEAD.addChild("cube_52", ModelPartBuilder.create().uv(0, 89).cuboid(0.0F, -2.0872F, 0.0019F, 3.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.25F, -4.6634F, -11.6306F, 0.7911F, 0.3036F, 0.428F));
        ModelPartData cube_53 = HEAD.addChild("cube_53", ModelPartBuilder.create().uv(20, 89).cuboid(-1.0F, 1.0F, 0.0F, 2.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -22.0F, 0.0873F, 0.0F, 0.0F));
        ModelPartData cube_54 = HEAD.addChild("cube_54", ModelPartBuilder.create().uv(32, 89).cuboid(-5.0F, -18.8944F, 0.3642F, 8.0F, 17.0F, 0.001F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, -12.0F, -0.84F, 0.1279F, 1.7275F));
        ModelPartData cube_55 = HEAD.addChild("cube_55", ModelPartBuilder.create().uv(52, 89).cuboid(-3.0F, -5.0F, -5.0F, 4.0F, 4.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, -1.0F, -10.0F, 0.2875F, -0.4205F, -0.1201F));
        ModelPartData cube_56 = HEAD.addChild("cube_56", ModelPartBuilder.create().uv(80, 89).cuboid(-4.4387F, -9.0733F, -1.2914F, 4.0F, 9.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-6.0F, -1.0F, -8.0F, -1.8486F, -0.1457F, -1.7809F));
        ModelPartData cube_57 = HEAD.addChild("cube_57", ModelPartBuilder.create().uv(96, 89).cuboid(-4.0F, -10.0F, 0.0F, 4.0F, 10.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(11.8668F, -10.1314F, -4.073F, -0.8822F, -0.7011F, 0.694F));
        ModelPartData cube_58 = HEAD.addChild("cube_58", ModelPartBuilder.create().uv(114, 89).cuboid(-1.0F, -15.8944F, -1.1358F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, -11.0F, -0.5197F, 0.2667F, -1.0296F));
        ModelPartData cube_59 = HEAD.addChild("cube_59", ModelPartBuilder.create().uv(132, 89).cuboid(0.0F, -10.0F, 0.0F, 4.0F, 10.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-11.8668F, -10.1314F, -4.073F, -0.8822F, 0.7011F, -0.694F));
        ModelPartData cube_60 = HEAD.addChild("cube_60", ModelPartBuilder.create().uv(150, 89).cuboid(0.4387F, -9.0733F, -1.2914F, 4.0F, 9.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -1.0F, -8.0F, -1.8486F, 0.1457F, 1.7809F));
        ModelPartData cube_61 = HEAD.addChild("cube_61", ModelPartBuilder.create().uv(166, 89).cuboid(-1.0F, -1.9537F, -0.3007F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.9575F, -21.6121F, -1.1781F, 0.0F, 0.0F));
        ModelPartData cube_62 = HEAD.addChild("cube_62", ModelPartBuilder.create().uv(176, 89).cuboid(-3.0F, -18.8944F, 0.3642F, 8.0F, 17.0F, 0.001F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, -12.0F, -0.84F, -0.1279F, -1.7275F));
        ModelPartData cube_63 = HEAD.addChild("cube_63", ModelPartBuilder.create().uv(196, 89).cuboid(-3.0F, -2.0872F, 0.0019F, 3.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-0.25F, -4.6634F, -11.6306F, 0.7911F, -0.3036F, -0.428F));
        ModelPartData cube_64 = HEAD.addChild("cube_64", ModelPartBuilder.create().uv(216, 89).cuboid(-4.0F, 0.0F, 0.0F, 4.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, -7.6788F, -11.5328F, -0.0873F, 0.0F, 0.0F));
        ModelPartData cube_65 = HEAD.addChild("cube_65", ModelPartBuilder.create().uv(240, 89).cuboid(0.2296F, -19.1687F, 0.6457F, 8.0F, 17.0F, 0.001F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, -12.0F, -0.7129F, -0.505F, 1.1035F));
        ModelPartData cube_66 = HEAD.addChild("cube_66", ModelPartBuilder.create().uv(260, 89).cuboid(-8.2296F, -19.1687F, 0.6457F, 8.0F, 17.0F, 0.001F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, -12.0F, -0.7129F, 0.505F, -1.1035F));
        ModelPartData cube_67 = cuello2.addChild("cube_67", ModelPartBuilder.create().uv(280, 89).cuboid(-4.0F, 0.0F, 12.0F, 8.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, -19.0F));
        ModelPartData cube_68 = cuello2.addChild("cube_68", ModelPartBuilder.create().uv(312, 89).cuboid(-12.0262F, -2.1159F, 11.8257F, 5.6569F, 5.6569F, 7.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -6.0F, -19.0F, 0.0F, 0.0F, -0.7854F));
        ModelPartData cube_69 = cuello1.addChild("cube_69", ModelPartBuilder.create().uv(340, 89).cuboid(-5.0F, 0.0F, 11.0F, 10.0F, 9.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, -19.0F));
        ModelPartData cube_70 = cuello1.addChild("cube_70", ModelPartBuilder.create().uv(378, 89).cuboid(-13.435F, -0.7071F, 10.75F, 5.6569F, 5.6569F, 8.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -6.0F, -19.0F, 0.0F, 0.0F, -0.7854F));
        ModelPartData cube_71 = cuello1.addChild("cube_71", ModelPartBuilder.create().uv(408, 89).cuboid(-2.0F, -5.0F, -4.0F, 4.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -7.0F, 0.9599F, 0.0F, 0.0F));
        ModelPartData cube_72 = cuello1.addChild("cube_72", ModelPartBuilder.create().uv(428, 89).cuboid(-2.0F, -6.0F, -4.0F, 3.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -5.0F, -1.0F, 2.1309F, 0.4109F, 0.7103F));
        ModelPartData cube_73 = cuello1.addChild("cube_73", ModelPartBuilder.create().uv(450, 89).cuboid(-2.0F, 4.0F, 11.0F, 4.0F, 4.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -6.0F, -19.0F, 0.3491F, 0.0F, 0.0F));
        ModelPartData cube_74 = cuello1.addChild("cube_74", ModelPartBuilder.create().uv(480, 89).cuboid(-1.0F, -6.0F, -4.0F, 3.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-6.0F, -5.0F, -1.0F, 2.1309F, -0.4109F, -0.7103F));
        ModelPartData manos = body.addChild("manos", ModelPartBuilder.create(), ModelTransform.pivot(6.0F, 10.0F, 18.0F));
        ModelPartData mano_r = manos.addChild("mano_r", ModelPartBuilder.create(), ModelTransform.of(-2.0F, -13.1466F, -26.3159F, 0.4002F, 0.2763F, -0.4879F));
        ModelPartData mano1 = mano_r.addChild("mano1", ModelPartBuilder.create(), ModelTransform.of(5.0F, 13.0F, 9.0F, -0.6944F, 0.0653F, 0.3753F));
        ModelPartData mano2 = mano1.addChild("mano2", ModelPartBuilder.create(), ModelTransform.of(-2.0F, 13.229F, -3.2887F, 0.1782F, -0.6966F, 0.0662F));
        ModelPartData dedos3 = mano2.addChild("dedos3", ModelPartBuilder.create(), ModelTransform.of(-0.4858F, 4.6083F, 0.9829F, 0.3054F, 0.0F, 0.0F));
        ModelPartData dedoese = dedos3.addChild("dedoese", ModelPartBuilder.create(), ModelTransform.of(-3.0F, -4.0F, 0.0F, -0.5236F, 0.0F, -1.789F));
        ModelPartData cube_75 = dedoese.addChild("cube_75", ModelPartBuilder.create().uv(502, 89).cuboid(1.0F, -5.0F, 1.0F, 0.001F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.8576F, -1.8457F, -2.1472F));
        ModelPartData cube_76 = dedoese.addChild("cube_76", ModelPartBuilder.create().uv(0, 109).cuboid(0.0F, -2.0F, 1.0F, 2.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.8576F, -1.8457F, -2.1472F));
                ModelPartData cube_77 = dedos3.addChild("cube_77", ModelPartBuilder.create().uv(12, 109).cuboid(0.5F, 4.75F, -2.0F, 2.0F, 4.5F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -5.0F, 0.0F));
        ModelPartData cube_78 = dedos3.addChild("cube_78", ModelPartBuilder.create().uv(24, 109).cuboid(-3.25F, -1.75F, 0.0F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 0.0F, -2.0F, 0.0F, 0.0F, -0.2182F));
        ModelPartData cube_79 = dedos3.addChild("cube_79", ModelPartBuilder.create().uv(36, 109).cuboid(19.3307F, -8.1006F, -0.1472F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-24.0F, 0.0F, -2.0F, 0.0F, 0.0F, 0.3054F));
        ModelPartData cube_80 = dedos3.addChild("cube_80", ModelPartBuilder.create().uv(48, 109).cuboid(-2.25F, 3.25F, 0.0F, 0.001F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 0.0F, -2.0F, 0.0F, 0.0F, -0.2182F));
        ModelPartData cube_81 = dedos3.addChild("cube_81", ModelPartBuilder.create().uv(58, 109).cuboid(1.5F, 9.25F, -2.0F, 0.001F, 3.5F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -5.0F, 0.0F));
        ModelPartData cube_82 = dedos3.addChild("cube_82", ModelPartBuilder.create().uv(68, 109).cuboid(20.3307F, -3.1006F, -0.1472F, 0.001F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-24.0F, 0.0F, -2.0F, 0.0F, 0.0F, 0.3054F));
        ModelPartData cube_83 = mano2.addChild("cube_83", ModelPartBuilder.create().uv(78, 109).cuboid(-2.0F, -4.0F, -3.0F, 6.0F, 7.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.9858F, 2.6083F, 0.9829F));
        ModelPartData cube_84 = mano1.addChild("cube_84", ModelPartBuilder.create().uv(100, 109).cuboid(0.0F, 4.8534F, -24.3159F, 5.0F, 11.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.0F, -2.8534F, 16.3159F));
        ModelPartData cube_85 = mano_r.addChild("cube_85", ModelPartBuilder.create().uv(124, 109).cuboid(-1.0F, -12.1466F, -25.3159F, 7.0F, 13.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 13.1466F, 26.3159F));
        ModelPartData mano_r2 = manos.addChild("mano_r2", ModelPartBuilder.create(), ModelTransform.of(-10.0F, -13.1466F, -26.3159F, 0.4002F, -0.2763F, 0.4879F));
        ModelPartData mano3 = mano_r2.addChild("mano3", ModelPartBuilder.create(), ModelTransform.of(-5.0F, 13.0F, 9.0F, -0.6944F, -0.0653F, -0.3753F));
        ModelPartData mano4 = mano3.addChild("mano4", ModelPartBuilder.create(), ModelTransform.of(2.0F, 13.229F, -3.2887F, 0.1782F, 0.6966F, -0.0662F));
        ModelPartData dedos2 = mano4.addChild("dedos2", ModelPartBuilder.create(), ModelTransform.of(0.4858F, 4.6083F, 0.9829F, 0.3054F, 0.0F, 0.0F));
        ModelPartData dedoese2 = dedos2.addChild("dedoese2", ModelPartBuilder.create(), ModelTransform.of(3.0F, -4.0F, 0.0F, -0.5236F, 0.0F, 1.789F));
        ModelPartData cube_86 = dedoese2.addChild("cube_86", ModelPartBuilder.create().uv(156, 109).cuboid(-1.0F, -5.0F, 1.0F, 0.001F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(1.8576F, -1.8457F, -2.1472F));
        ModelPartData cube_87 = dedoese2.addChild("cube_87", ModelPartBuilder.create().uv(166, 109).cuboid(-2.0F, -2.0F, 1.0F, 2.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(1.8576F, -1.8457F, -2.1472F));
        ModelPartData cube_88 = dedos2.addChild("cube_88", ModelPartBuilder.create().uv(178, 109).cuboid(-2.5F, 4.75F, -2.0F, 2.0F, 4.5F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, -5.0F, 0.0F));
        ModelPartData cube_89 = dedos2.addChild("cube_89", ModelPartBuilder.create().uv(190, 109).cuboid(-1.5F, 9.25F, -2.0F, 0.001F, 3.5F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, -5.0F, 0.0F));
        ModelPartData cube_90 = dedos2.addChild("cube_90", ModelPartBuilder.create().uv(200, 109).cuboid(1.25F, -1.75F, 0.0F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 0.0F, -2.0F, 0.0F, 0.0F, 0.2182F));
        ModelPartData cube_91 = dedos2.addChild("cube_91", ModelPartBuilder.create().uv(212, 109).cuboid(2.25F, 3.25F, 0.0F, 0.001F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 0.0F, -2.0F, 0.0F, 0.0F, 0.2182F));
        ModelPartData cube_92 = dedos2.addChild("cube_92", ModelPartBuilder.create().uv(222, 109).cuboid(-21.3307F, -8.1006F, -0.1472F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(24.0F, 0.0F, -2.0F, 0.0F, 0.0F, -0.3054F));
        ModelPartData cube_93 = dedos2.addChild("cube_93", ModelPartBuilder.create().uv(234, 109).cuboid(-20.3307F, -3.1006F, -0.1472F, 0.001F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(24.0F, 0.0F, -2.0F, 0.0F, 0.0F, -0.3054F));
        ModelPartData cube_94 = mano4.addChild("cube_94", ModelPartBuilder.create().uv(244, 109).cuboid(-4.0F, -4.0F, -3.0F, 6.0F, 7.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(1.9858F, 2.6083F, 0.9829F));
        ModelPartData cube_95 = mano3.addChild("cube_95", ModelPartBuilder.create().uv(266, 109).cuboid(-5.0F, 4.8534F, -24.3159F, 5.0F, 11.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, -2.8534F, 16.3159F));
        ModelPartData cube_96 = mano_r2.addChild("cube_96", ModelPartBuilder.create().uv(290, 109).cuboid(-6.0F, -12.1466F, -25.3159F, 7.0F, 13.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 13.1466F, 26.3159F));
        ModelPartData alas = body.addChild("alas", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        ModelPartData leftAla = alas.addChild("leftAla", ModelPartBuilder.create(), ModelTransform.of(7.0F, -13.0F, -6.0F, -0.48F, 0.0F, -0.4363F));
        ModelPartData ala1 = leftAla.addChild("ala1", ModelPartBuilder.create(), ModelTransform.pivot(29.0F, 2.0F, 2.0F));
        ModelPartData ala2 = ala1.addChild("ala2", ModelPartBuilder.create(), ModelTransform.pivot(29.0F, 0.0F, -1.0F));
        ModelPartData cube_97 = ala2.addChild("cube_97", ModelPartBuilder.create().uv(322, 109).cuboid(52.0F, -10.501F, -6.0F, 44.0F, 0.001F, 51.0F, new Dilation(0.0F)), ModelTransform.pivot(-66.0F, 11.0F, 1.0F));
        ModelPartData cube_98 = ala2.addChild("cube_98", ModelPartBuilder.create().uv(0, 163).cuboid(49.0F, -10.501F, -7.0F, 21.0F, 0.001F, 21.0F, new Dilation(0.0F)), ModelTransform.of(-66.0F, 11.0F, 1.0F, 0.0F, 0.2182F, 0.0F));
        ModelPartData cube_99 = ala1.addChild("cube_99", ModelPartBuilder.create().uv(86, 163).cuboid(37.0F, -12.0F, -1.0F, 29.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-37.0F, 11.0F, 0.0F));
        ModelPartData cube_100 = ala1.addChild("cube_100", ModelPartBuilder.create().uv(150, 163).cuboid(30.0F, -10.501F, -7.0F, 36.0F, 0.001F, 43.0F, new Dilation(0.0F)), ModelTransform.pivot(-37.0F, 11.0F, 0.0F));
        ModelPartData cube_101 = leftAla.addChild("cube_101", ModelPartBuilder.create().uv(310, 163).cuboid(4.0F, -13.0F, -2.0F, 33.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, 13.0F, 2.0F));
        ModelPartData cube_102 = leftAla.addChild("cube_102", ModelPartBuilder.create().uv(0, 209).cuboid(8.5728F, 6.1634F, -2.7639F, 35.0F, 0.001F, 31.0F, new Dilation(0.0F)), ModelTransform.of(-12.3233F, -4.5909F, 6.4245F, 0.0794F, 0.032F, 0.0183F));
        ModelPartData righAla = alas.addChild("righAla", ModelPartBuilder.create(), ModelTransform.of(-7.0F, -13.0F, -6.0F, -0.48F, 0.0F, 0.4363F));
        ModelPartData ala3 = righAla.addChild("ala3", ModelPartBuilder.create(), ModelTransform.pivot(-29.0F, 2.0F, 2.0F));
        ModelPartData ala4 = ala3.addChild("ala4", ModelPartBuilder.create(), ModelTransform.pivot(-29.0F, 0.0F, -1.0F));
        ModelPartData cube_103 = ala4.addChild("cube_103", ModelPartBuilder.create().uv(134, 209).cuboid(-96.0F, -10.501F, -6.0F, 44.0F, 0.001F, 51.0F, new Dilation(0.0F)), ModelTransform.pivot(66.0F, 11.0F, 1.0F));
        ModelPartData cube_104 = ala4.addChild("cube_104", ModelPartBuilder.create().uv(326, 209).cuboid(-70.0F, -10.501F, -7.0F, 21.0F, 0.001F, 21.0F, new Dilation(0.0F)), ModelTransform.of(66.0F, 11.0F, 1.0F, 0.0F, -0.2182F, 0.0F));
        ModelPartData cube_105 = ala3.addChild("cube_105", ModelPartBuilder.create().uv(412, 209).cuboid(-66.0F, -12.0F, -1.0F, 29.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(37.0F, 11.0F, 0.0F));
        ModelPartData cube_106 = ala3.addChild("cube_106", ModelPartBuilder.create().uv(0, 263).cuboid(-66.0F, -10.501F, -7.0F, 36.0F, 0.001F, 43.0F, new Dilation(0.0F)), ModelTransform.pivot(37.0F, 11.0F, 0.0F));
        ModelPartData cube_107 = righAla.addChild("cube_107", ModelPartBuilder.create().uv(160, 263).cuboid(-37.0F, -13.0F, -2.0F, 33.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 13.0F, 2.0F));
        ModelPartData cube_108 = righAla.addChild("cube_108", ModelPartBuilder.create().uv(236, 263).cuboid(-32.9262F, 6.1634F, -2.7639F, 35.0F, 0.001F, 31.0F, new Dilation(0.0F)), ModelTransform.of(1.6767F, -4.5909F, 6.4245F, 0.0794F, -0.032F, -0.0183F));
        ModelPartData cube_109 = body.addChild("cube_109", ModelPartBuilder.create().uv(370, 263).cuboid(-8.0F, 3.8505F, -12.3025F, 4.0F, 14.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -25.0F, 7.0F, -0.2618F, 0.0F, 0.0F));
        ModelPartData cube_110 = body.addChild("cube_110", ModelPartBuilder.create().uv(392, 263).cuboid(-6.0F, 4.0F, -3.0F, 5.0F, 4.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, -17.5F, -7.0F, -0.0896F, 0.4212F, -0.6761F));
        ModelPartData cube_111 = body.addChild("cube_111", ModelPartBuilder.create().uv(426, 263).cuboid(-15.0F, -3.0F, -18.0F, 18.0F, 18.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(6.0F, -7.0F, 8.0F));
        ModelPartData cube_112 = body.addChild("cube_112", ModelPartBuilder.create().uv(0, 309).cuboid(1.0F, 4.0F, -3.0F, 5.0F, 4.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, -17.5F, -7.0F, -0.0896F, -0.4212F, 0.6761F));
        ModelPartData cube_113 = body.addChild("cube_113", ModelPartBuilder.create().uv(34, 309).cuboid(-0.0937F, -9.9982F, -2.8405F, 4.0F, 14.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -9.8168F, 3.1961F, -0.5741F, 0.6723F, 0.4052F));
        ModelPartData cube_114 = body.addChild("cube_114", ModelPartBuilder.create().uv(56, 309).cuboid(-3.9063F, -9.9982F, -2.8405F, 4.0F, 14.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -9.8168F, 3.1961F, -0.5741F, -0.6723F, -0.4052F));
        ModelPartData cube_115 = body.addChild("cube_115", ModelPartBuilder.create().uv(78, 309).cuboid(3.8298F, 1.2782F, 3.4039F, 4.0F, 14.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -24.4566F, 9.169F, -0.9171F, -0.6271F, 0.6537F));

        return TexturedModelData.of(modelData, 512, 512);
    }

    @Override
    public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}

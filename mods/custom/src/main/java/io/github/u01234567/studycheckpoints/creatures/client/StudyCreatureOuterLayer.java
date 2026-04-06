package io.github.u01234567.studycheckpoints.creatures.client;

import io.github.u01234567.studycheckpoints.StudyCheckpoints;
import io.github.u01234567.studycheckpoints.creatures.StudyEntities;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;

public final class StudyCreatureOuterLayer
        extends RenderLayer<LivingEntityRenderState, EntityModel<LivingEntityRenderState>> {

    private final EntityModel<LivingEntityRenderState> outerModel;
    private final Identifier outerTexture;

    public StudyCreatureOuterLayer(
            RenderLayerParent<LivingEntityRenderState, EntityModel<LivingEntityRenderState>> parent,
            EntityRendererProvider.Context context,
            StudyEntities.CustomCreatureDefinition definition
    ) {
        super(parent);
        this.outerModel = StudyCreatureGeneratedFactory.createModel(definition.outerModelClassName(), context);
        this.outerTexture = Identifier.fromNamespaceAndPath(
                StudyCheckpoints.MOD_ID,
                definition.outerTextureResourcePath()
        );
    }

    @Override
    public void submit(
            PoseStack matrices,
            SubmitNodeCollector nodeCollector,
            int light,
            LivingEntityRenderState state,
            float yRot,
            float xRot
    ) {
        this.outerModel.setupAnim(state);
        RenderLayer.renderColoredCutoutModel(
                this.outerModel,
                this.outerTexture,
                matrices,
                nodeCollector,
                light,
                state,
                0xFFFFFFFF,
                0
        );
    }
}
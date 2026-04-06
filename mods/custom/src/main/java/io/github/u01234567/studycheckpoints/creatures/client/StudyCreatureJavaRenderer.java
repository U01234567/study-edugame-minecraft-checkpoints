package io.github.u01234567.studycheckpoints.creatures.client;

import io.github.u01234567.studycheckpoints.StudyCheckpoints;
import io.github.u01234567.studycheckpoints.creatures.StudyCreatureEntity;
import io.github.u01234567.studycheckpoints.creatures.StudyEntities;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class StudyCreatureJavaRenderer
        extends MobRenderer<StudyCreatureEntity, LivingEntityRenderState, EntityModel<LivingEntityRenderState>> {

    private final Identifier textureLocation;

    public StudyCreatureJavaRenderer(
            EntityRendererProvider.Context context,
            StudyEntities.CustomCreatureDefinition definition
    ) {
        super(context, StudyCreatureGeneratedFactory.createModel(definition, context), 0.6F);
        this.textureLocation = Identifier.fromNamespaceAndPath(
                StudyCheckpoints.MOD_ID,
                definition.textureResourcePath()
        );

        if (definition.outerModelClassName() != null
                && !definition.outerModelClassName().isBlank()
                && definition.outerTextureResourcePath() != null
                && !definition.outerTextureResourcePath().isBlank()) {
            this.addLayer(new StudyCreatureOuterLayer(this, context, definition));
        }
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState renderState) {
        return this.textureLocation;
    }
}
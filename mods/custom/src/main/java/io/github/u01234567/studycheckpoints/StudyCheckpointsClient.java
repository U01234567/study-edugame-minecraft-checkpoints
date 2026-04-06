package io.github.u01234567.studycheckpoints;

import io.github.u01234567.studycheckpoints.creatures.StudyEntities;
import io.github.u01234567.studycheckpoints.creatures.client.StudyCreatureJavaRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class StudyCheckpointsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        StudyEntities.initialise();

        for (StudyEntities.CustomCreatureDefinition definition : StudyEntities.customCreatureDefinitions()) {
            EntityRenderers.register(
                    StudyEntities.byId(definition.id()),
                    context -> new StudyCreatureJavaRenderer(context, definition)
            );
        }

        StudyFlowController.initializeClient();
        StudyInteractionController.initializeClient();
    }
}

package io.github.u01234567.studycheckpoints.entity.client;

import com.geckolib.model.DefaultedEntityGeoModel;
import io.github.u01234567.studycheckpoints.StudyCheckpoints;
import io.github.u01234567.studycheckpoints.entity.custom.StudyCreatureEntity;
import net.minecraft.resources.Identifier;

/**
 * Reusable GeckoLib model wrapper for study creatures.
 *
 * For a creature id like "mantis", this looks for generated runtime assets:
 * - assets/study-checkpoints/geckolib/models/entity/mantis.geo.json
 * - assets/study-checkpoints/geckolib/animations/entity/mantis.animation.json
 * - assets/study-checkpoints/textures/entity/mantis.png
 *
 * These files are copied into generated build resources from
 * ../external/blockbench/{id}/ during the Gradle build.
 */
public class StudyCreatureGeoModel extends DefaultedEntityGeoModel<StudyCreatureEntity> {
    public StudyCreatureGeoModel(String assetName) {
        super(Identifier.fromNamespaceAndPath(StudyCheckpoints.MOD_ID, assetName));
    }
}
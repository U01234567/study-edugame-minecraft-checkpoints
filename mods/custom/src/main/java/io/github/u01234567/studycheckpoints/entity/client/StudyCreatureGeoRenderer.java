package io.github.u01234567.studycheckpoints.entity.client;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import io.github.u01234567.studycheckpoints.entity.custom.StudyCreatureEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

// Reusable GeckoLib renderer for custom study creatures.
public class StudyCreatureGeoRenderer<R extends LivingEntityRenderState & GeoRenderState>
        extends GeoEntityRenderer<StudyCreatureEntity, R> {

    public StudyCreatureGeoRenderer(EntityRendererProvider.Context context, String assetName) {
        super(context, new StudyCreatureGeoModel(assetName));
    }
}
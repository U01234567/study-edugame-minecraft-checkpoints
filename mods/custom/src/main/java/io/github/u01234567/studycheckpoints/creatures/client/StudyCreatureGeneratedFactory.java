package io.github.u01234567.studycheckpoints.creatures.client;

import io.github.u01234567.studycheckpoints.creatures.StudyEntities;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class StudyCreatureGeneratedFactory {
    private StudyCreatureGeneratedFactory() {
    }

    @SuppressWarnings("unchecked")
    public static EntityModel<LivingEntityRenderState> createModel(
            StudyEntities.CustomCreatureDefinition definition,
            EntityRendererProvider.Context context
    ) {
        return createModel(definition.modelClassName(), context);
    }

    @SuppressWarnings("unchecked")
    public static EntityModel<LivingEntityRenderState> createModel(
            String modelClassName,
            EntityRendererProvider.Context context
    ) {
        try {
            Class<?> modelClass = Class.forName(modelClassName);
            Constructor<?> constructor = modelClass.getDeclaredConstructor(ModelPart.class);
            ModelPart bakedRoot = createLayerDefinition(modelClassName).bakeRoot();
            return (EntityModel<LivingEntityRenderState>) constructor.newInstance(bakedRoot);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Could not instantiate generated study creature model class: " + modelClassName,
                    e
            );
        }
    }

    public static LayerDefinition createLayerDefinition(StudyEntities.CustomCreatureDefinition definition) {
        return createLayerDefinition(definition.modelClassName());
    }

    public static LayerDefinition createLayerDefinition(String modelClassName) {
        try {
            Class<?> modelClass = Class.forName(modelClassName);
            Method method = findLayerMethod(modelClass);
            return (LayerDefinition) method.invoke(null);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Could not load generated layer definition for study creature model class: " + modelClassName,
                    e
            );
        }
    }

    private static Method findLayerMethod(Class<?> modelClass) throws NoSuchMethodException {
        try {
            return modelClass.getMethod("getLayerDefinition");
        } catch (NoSuchMethodException ignored) {
            return modelClass.getMethod("getTexturedModelData");
        }
    }
}
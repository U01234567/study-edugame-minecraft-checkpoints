// Generated for /mods/external/blockbench/cave_dweller/
// Kept intentionally narrow: idle + run only from the source GLTF.

package com.example.mod;

public class cave_dwellerAnimation {
    public static final AnimationDefinition IDLE = AnimationDefinition.Builder.withLength(0.0010F)
        .build();

    public static final AnimationDefinition RUN = AnimationDefinition.Builder.withLength(0.5000F)
        .addAnimation("upperbody", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, 10.0000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("neck", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, -12.5000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, -5.0000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("jaw", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, 30.0000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, -2.5000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, 27.5000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("rightupperarm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, -17.5000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("leftupperarm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, -7.5000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("upperrightleg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, 0.0000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, -35.0000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, 0.0000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3750F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, 57.5000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, -10.0000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("lowerrightleg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, 0.0000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, -20.0000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, 0.0000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3750F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, 22.5000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000F, KeyframeAnimations.degreeVec(0.0000F, 0.0000F, 22.5000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .build();

}
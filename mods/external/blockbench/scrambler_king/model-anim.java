// Generated from scrambler_king/source/model.gltf
// Source clip animation.scramblerking.walk_to_target simplifies to a posed helper clip.
// Current runtime should not be assumed to auto-consume this sidecar.

final class model_anim {
	private model_anim() {}

	public static final AnimationDefinition WALK_TO_TARGET = AnimationDefinition.Builder.withLength(0.5F)
		.addAnimation("Leg1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -35.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Leg3", new AnimationChannel(AnimationChannel.Targets.POSITION,
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -3.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Leg3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(-15.5639F, 31.1499F, -28.7572F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Leg0", new AnimationChannel(AnimationChannel.Targets.POSITION,
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -3.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Leg0", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(-18.0919F, 2.3001F, 13.4195F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Leg2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -5.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Body", new AnimationChannel(AnimationChannel.Targets.POSITION,
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(-2.614F, -0.3807F, -4.9838F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Tail", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -12.5F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("BigMouth", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(30.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Mouthbasement3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(-0.3013F, 0.9992F, -4.7026F), AnimationChannel.Interpolations.LINEAR)
		))
		.build();
}
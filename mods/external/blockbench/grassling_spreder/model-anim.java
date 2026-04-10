// Generated from Grassling Spreder.gltf for the /mods/external/blockbench/ pipeline
// Rotation tracks are emitted as degrees because generate_study_creatures.py converts them back to radians.

package com.example.mod;

public class model_animations {
	public static final AnimationDefinition STAND_BY_ANIMATION = AnimationDefinition.Builder.withLength(12.0833F).looping()
		.addAnimation("testa", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("testa", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("flowerB1", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("flowerB2", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("flowerB4", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("flowerB5", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("flowerB7", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("flowerB8", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("arms", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("arms", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.build();

	public static final AnimationDefinition WALKING_ANIMATION = AnimationDefinition.Builder.withLength(5.0F).looping()
		.addAnimation("terrain", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("testa", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("testa", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(-7.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("leg1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("leg2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("arms", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(0.125F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(0.375F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(0.625F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(0.75F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(0.875F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.125F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.25F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.375F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.625F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.75F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.875F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.125F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.25F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.625F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.75F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.875F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.125F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.25F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.375F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.625F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.75F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.875F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.125F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.25F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.375F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.625F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.75F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.0F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("arm1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("arm2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.build();

	public static final AnimationDefinition SPORE_ATTACK_ANIMATION = AnimationDefinition.Builder.withLength(10.4583F).looping()
		.addAnimation("testa", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("testa", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("flowerB1", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("flowerB2", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("flowerB2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("flowerB2", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(8.9583F, KeyframeAnimations.scaleVec(0.9375F, 0.9375F, 0.9375F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(9.0417F, KeyframeAnimations.scaleVec(0.8125F, 0.8125F, 0.8125F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(9.125F, KeyframeAnimations.scaleVec(0.6875F, 0.6875F, 0.6875F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(9.2083F, KeyframeAnimations.scaleVec(0.5625F, 0.5625F, 0.5625F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(9.25F, KeyframeAnimations.scaleVec(0.5F, 0.5F, 0.5F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(9.3333F, KeyframeAnimations.scaleVec(0.375F, 0.375F, 0.375F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(9.4167F, KeyframeAnimations.scaleVec(0.25F, 0.25F, 0.25F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(9.5F, KeyframeAnimations.scaleVec(0.125F, 0.125F, 0.125F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(9.5833F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(9.6667F, KeyframeAnimations.scaleVec(-0.052F, -0.052F, -0.052F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(9.75F, KeyframeAnimations.scaleVec(-0.0885F, -0.0885F, -0.0885F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(9.8333F, KeyframeAnimations.scaleVec(-0.099F, -0.099F, -0.099F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(9.9167F, KeyframeAnimations.scaleVec(-0.073F, -0.073F, -0.073F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(10.0F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(10.0833F, KeyframeAnimations.scaleVec(0.3789F, 0.3789F, 0.3789F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(10.1667F, KeyframeAnimations.scaleVec(0.8518F, 0.8518F, 0.8518F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(10.2083F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(10.3333F, KeyframeAnimations.scaleVec(0.94F, 0.94F, 0.94F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(10.4167F, KeyframeAnimations.scaleVec(0.8393F, 0.8393F, 0.8393F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(10.4583F, KeyframeAnimations.scaleVec(0.8F, 0.8F, 0.8F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("flowerB4", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("flowerB5", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("flowerB7", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("flowerB8", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("arms", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("arms", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("arm1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("arm2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("explosion", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(0.2083F, KeyframeAnimations.posVec(-0.0003F, 0.0003F, -1.2349F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(0.4583F, KeyframeAnimations.posVec(-0.0015F, 0.0015F, -2.9349F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(0.7917F, KeyframeAnimations.posVec(-0.0042F, 0.0042F, -5.5468F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.0833F, KeyframeAnimations.posVec(-0.0075F, 0.0075F, -8.1299F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.2917F, KeyframeAnimations.posVec(-0.0103F, 0.0103F, -10.1308F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.4583F, KeyframeAnimations.posVec(-0.0129F, 0.0129F, -11.8183F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.625F, KeyframeAnimations.posVec(-0.0156F, 0.0156F, -13.5781F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.8333F, KeyframeAnimations.posVec(-0.0192F, 0.0192F, -15.8724F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.0F, KeyframeAnimations.posVec(-0.0223F, 0.0223F, -17.7781F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.3333F, KeyframeAnimations.posVec(-0.0287F, 0.0287F, -21.7575F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.75F, KeyframeAnimations.posVec(-0.037F, 0.037F, -27.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.0833F, KeyframeAnimations.posVec(-0.0437F, 0.0437F, -31.3645F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.4167F, KeyframeAnimations.posVec(-0.0502F, 0.0502F, -35.8414F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.8333F, KeyframeAnimations.posVec(-0.0578F, 0.0578F, -41.5403F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.125F, KeyframeAnimations.posVec(-0.0625F, 0.0625F, -45.5625F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.2917F, KeyframeAnimations.posVec(-0.0649F, 0.0649F, -47.8621F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.5F, KeyframeAnimations.posVec(-0.0676F, 0.0676F, -50.7295F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.6667F, KeyframeAnimations.posVec(-0.0695F, 0.0695F, -53.0122F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.8333F, KeyframeAnimations.posVec(-0.0711F, 0.0711F, -55.2799F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.0417F, KeyframeAnimations.posVec(-0.0726F, 0.0726F, -58.0867F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.2083F, KeyframeAnimations.posVec(-0.0735F, 0.0735F, -60.3043F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.375F, KeyframeAnimations.posVec(-0.074F, 0.074F, -62.4923F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.5833F, KeyframeAnimations.posVec(-0.074F, 0.074F, -65.1787F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.75F, KeyframeAnimations.posVec(-0.0736F, 0.0736F, -67.2835F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.9167F, KeyframeAnimations.posVec(-0.0727F, 0.0727F, -69.3439F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.125F, KeyframeAnimations.posVec(-0.071F, 0.071F, -71.8503F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.2917F, KeyframeAnimations.posVec(-0.069F, 0.069F, -73.7944F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.4583F, KeyframeAnimations.posVec(-0.0665F, 0.0665F, -75.6795F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.6667F, KeyframeAnimations.posVec(-0.0627F, 0.0627F, -77.9459F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.8333F, KeyframeAnimations.posVec(-0.0589F, 0.0589F, -79.6816F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(7.0F, KeyframeAnimations.posVec(-0.0545F, 0.0545F, -81.3435F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(7.2083F, KeyframeAnimations.posVec(-0.0482F, 0.0482F, -83.3103F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(7.375F, KeyframeAnimations.posVec(-0.0424F, 0.0424F, -84.7897F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(7.5417F, KeyframeAnimations.posVec(-0.0359F, 0.0359F, -86.1807F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(7.75F, KeyframeAnimations.posVec(-0.0267F, 0.0267F, -87.788F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(7.9167F, KeyframeAnimations.posVec(-0.0186F, 0.0186F, -88.9634F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(8.25F, KeyframeAnimations.posVec(0.0F, 0.0F, -91.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(8.625F, KeyframeAnimations.posVec(0.5367F, -0.5367F, -95.7977F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(8.9583F, KeyframeAnimations.posVec(1.0F, -1.0F, -90.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("explosion", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.addAnimation("explosion", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
		.build();

}
package io.github.u01234567.studycheckpoints.entity.custom;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

// Generic study creature entity.
public class StudyCreatureEntity extends PathfinderMob implements GeoEntity {
    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIMATION = RawAnimation.begin().thenLoop("Walk");
    private static final RawAnimation ASCEND_ANIMATION = RawAnimation.begin().thenLoop("Ascend");
    private static final RawAnimation FALLING_ANIMATION = RawAnimation.begin().thenLoop("Falling");

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public StudyCreatureEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 0;
    }

    // Shared base attributes for study creatures.
    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.18D)
                .add(Attributes.FOLLOW_RANGE, 12.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    // Keep study creatures persistent even when no player is nearby.
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    // Register the main GeckoLib animation controller.
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<StudyCreatureEntity>(
                "main_controller",
                5,
                this::mainAnimationController
        ));
    }

    private PlayState mainAnimationController(AnimationTest<StudyCreatureEntity> state) {
        if (!this.onGround()) {
            double verticalSpeed = this.getDeltaMovement().y;

            if (verticalSpeed > 0.05D) {
                return state.setAndContinue(ASCEND_ANIMATION);
            }

            if (verticalSpeed < -0.05D) {
                return state.setAndContinue(FALLING_ANIMATION);
            }
        }

        if (state.isMoving()) {
            return state.setAndContinue(WALK_ANIMATION);
        }

        return state.setAndContinue(IDLE_ANIMATION);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animationCache;
    }
}
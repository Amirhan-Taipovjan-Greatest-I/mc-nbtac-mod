package com.mt1006.nbt_ac.mixin.constructors;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
abstract public class EntityTypeMixin<T extends Entity> implements FeatureElement, EntityTypeTest<Entity, T>
{
	@Shadow @Final private EntityType.EntityFactory<T> factory;

	@Inject(method = "create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;", at = @At(value = "HEAD"), cancellable = true)
	public void create(Level level, CallbackInfoReturnable<T> callbackInfo)
	{
		if (level == null)
		{
			// Bypass "enabled feature" check in 1.19.3 and later
			callbackInfo.setReturnValue(this.factory.create((EntityType<T>)(Object)this, level));
			callbackInfo.cancel();
		}
	}
}

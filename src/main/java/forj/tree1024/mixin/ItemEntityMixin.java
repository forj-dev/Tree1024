package forj.tree1024.mixin;

import forj.tree1024.entity.rb_tree.ItemEntityAccessor;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin implements ItemEntityAccessor {
    @Unique
    public int treeInsertCooldown = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (treeInsertCooldown > 0) treeInsertCooldown--;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    @Unique
    public boolean isTreeInsertCoolingDown() {
        return treeInsertCooldown > 0;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    @Unique
    public void setTreeInsertCooldown(int treeInsertCooldown) {
        this.treeInsertCooldown = treeInsertCooldown;
    }
}

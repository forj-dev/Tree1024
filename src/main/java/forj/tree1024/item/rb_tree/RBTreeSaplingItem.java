package forj.tree1024.item.rb_tree;

import forj.tree1024.entity.Entities;
import forj.tree1024.entity.rb_tree.RBTreeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.Objects;

public class RBTreeSaplingItem extends Item {
    public RBTreeSaplingItem() {
        super(new Settings().group(ItemGroup.MISC));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        if (context.getWorld().getBlockState(pos).getBlock().getHardness() == -1)
            return super.useOnBlock(context);
        pos = pos.add(0, 1, 0);
        if (!context.getWorld().getBlockState(pos).isAir())
            return super.useOnBlock(context);
        List<RBTreeEntity> trees = context.getWorld()
                .getEntitiesByClass(RBTreeEntity.class, new Box(pos), e -> true);
        if (!trees.isEmpty())
            return super.useOnBlock(context);
        if (context.getWorld().isClient) return ActionResult.SUCCESS;
        RBTreeEntity tree = new RBTreeEntity(Entities.RB_TREE_ENTITY, context.getWorld());
        tree.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        context.getWorld().spawnEntity(tree);
        if (!Objects.requireNonNull(context.getPlayer()).isCreative())
            context.getStack().decrement(1);
        return ActionResult.SUCCESS;
    }
}

package forj.tree1024.item;

import forj.tree1024.item.rb_tree.RBTreeSaplingItem;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class Items {
    public static final Item RB_TREE_SAPLING = new RBTreeSaplingItem();
    public static final Block RED_BRANCH = new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, (state) -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? MapColor.SPRUCE_BROWN : MapColor.BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD));
    public static final Block BLACK_BRANCH = new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, (state) -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? MapColor.SPRUCE_BROWN : MapColor.BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD));
    public static final BlockItem RED_BRANCH_ITEM = new BlockItem(RED_BRANCH, new Item.Settings().group(ItemGroup.DECORATIONS));
    public static final BlockItem BLACK_BRANCH_ITEM = new BlockItem(BLACK_BRANCH, new Item.Settings().group(ItemGroup.DECORATIONS));

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier("tree1024", "rb_tree_sapling"), RB_TREE_SAPLING);
        Registry.register(Registry.BLOCK, new Identifier("tree1024", "red_branch"), RED_BRANCH);
        Registry.register(Registry.BLOCK, new Identifier("tree1024", "black_branch"), BLACK_BRANCH);
        Registry.register(Registry.ITEM, new Identifier("tree1024", "red_branch"), RED_BRANCH_ITEM);
        Registry.register(Registry.ITEM, new Identifier("tree1024", "black_branch"), BLACK_BRANCH_ITEM);
    }
}

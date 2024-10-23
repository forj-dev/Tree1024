package forj.tree1024.entity.rb_tree;

import net.minecraft.item.ItemStack;

public class RenderingTreeNode {
    public final long id;
    public final int key;
    public final RBTreeNode<Integer, ItemStack> parent;
    public ItemStack value;
    public boolean isBlack;
    public Pose pose;
    public float  roll, size;

    public RenderingTreeNode(Pose pose, float roll, float size, long id, ItemStack value, RBTreeNode<Integer, ItemStack> parent, boolean isBlack, int key) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.parent = parent;
        this.isBlack = isBlack;
        this.pose = pose.copy();
        this.roll = roll;
        this.size = size;
    }
}

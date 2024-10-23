package forj.tree1024.entity.rb_tree;

import net.minecraft.item.ItemStack;

public class AnimatedTreeNode {
    public ItemStack value;
    public boolean isBlack;
    public double x1, y1, z1, x2, y2, z2;
    public float pitch1, yaw1, roll1, pitch2, yaw2, roll2, size1, size2;

    public AnimatedTreeNode(ItemStack value, boolean isBlack, double x1, double y1, double z1, double x2, double y2, double z2, float pitch1, float yaw1, float roll1, float pitch2, float yaw2, float roll2, float size1, float size2) {
        this.value = value;
        this.isBlack = isBlack;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.pitch1 = pitch1;
        this.yaw1 = yaw1;
        this.roll1 = roll1;
        this.pitch2 = pitch2;
        this.yaw2 = yaw2;
        this.roll2 = roll2;
        this.size1 = size1;
        this.size2 = size2;
    }
}

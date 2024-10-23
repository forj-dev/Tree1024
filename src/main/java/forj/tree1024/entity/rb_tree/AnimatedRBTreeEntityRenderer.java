package forj.tree1024.entity.rb_tree;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

import java.util.List;

import static forj.tree1024.entity.rb_tree.RBTreeEntityRenderer.*;

public class AnimatedRBTreeEntityRenderer {
    static final int ANIMATION_LENGTH = 20; // in ticks
    private final List<AnimatedTreeNode> animation;
    private final ItemRenderer itemRenderer;
    public boolean isAnimating = true;
    private final int startAge;

    public AnimatedRBTreeEntityRenderer(List<AnimatedTreeNode> animation, int startAge, ItemRenderer itemRenderer) {
        this.animation = animation;
        this.startAge = startAge;
        this.itemRenderer = itemRenderer;
    }

    private float getAnimationProgress(int age, float tickDelta) {
        float time = age - startAge + tickDelta;
        return time / ANIMATION_LENGTH;
    }

    public void render(float tickDelta, int age, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        float progress = getAnimationProgress(age, tickDelta);
        if (progress >= 1.0f) {
            isAnimating = false;
            progress = 1.0f;
        }
        matrices.push();
        matrices.translate(0, 1.1, 0);
        for (AnimatedTreeNode node : animation) {
            renderAnimatingNode(node, progress, matrices, vertexConsumers, light);
        }
        matrices.pop();
    }

    private void renderAnimatingNode(AnimatedTreeNode node, float progress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        double x = MathHelper.lerp(progress, node.x1, node.x2);
        double y = MathHelper.lerp(progress, node.y1, node.y2);
        double z = MathHelper.lerp(progress, node.z1, node.z2);
        float pitch = MathHelper.lerp(progress, node.pitch1, node.pitch2);
        float yaw = MathHelper.lerp(progress, node.yaw1, node.yaw2);
        float roll = MathHelper.lerp(progress, node.roll1, node.roll2);
        float size = MathHelper.lerp(progress, node.size1, node.size2);
        matrices.push();
        matrices.translate(x, y, z);
        matrices.multiply(Quaternion.fromEulerXyz(pitch - (float) Math.PI / 2, yaw, 0));
        matrices.multiply(Quaternion.fromEulerXyz(0, roll, 0));
        matrices.scale(size, size, size);
        renderNode(node, matrices, vertexConsumers, light);
        matrices.pop();
    }

    private void renderNode(AnimatedTreeNode node, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        for (int i = 0; i < 4; i++) {
            matrices.push();
            matrices.translate(0, 0, 0.5f * BRANCH_SIZE);
            matrices.scale(BRANCH_SIZE, BRANCH_WH_RATIO * BRANCH_SIZE, 0.1f * BRANCH_SIZE);
            matrices.scale(ICON_SIZE, ICON_SIZE, ICON_SIZE);
            itemRenderer.renderItem(node.value, ModelTransformation.Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 1);
            matrices.scale(1f / ICON_SIZE, 1f / ICON_SIZE, 0);
            itemRenderer.renderItem(node.isBlack ? BLACK_BRANCH : RED_BRANCH, ModelTransformation.Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);
            matrices.pop();
            matrices.multiply(ROTATE90);
        }
        matrices.pop();
    }
}

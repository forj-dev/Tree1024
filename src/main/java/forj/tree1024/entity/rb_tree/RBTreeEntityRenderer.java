package forj.tree1024.entity.rb_tree;

import forj.tree1024.item.Items;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RBTreeEntityRenderer extends EntityRenderer<RBTreeEntity> {
    private final ItemRenderer itemRenderer;

    public RBTreeEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public Identifier getTexture(RBTreeEntity entity) {
        return new Identifier("tree1024", "textures/entity/rb_tree.png");
    }

    @Override
    public void render(RBTreeEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        renderRoot(matrices, vertexConsumers, light);
        if (entity.animatedRenderer != null && !entity.animatedRenderer.isAnimating) {
            entity.animatedRenderer = null;
        }
        if (entity.animatedRenderer != null) {
            matrices.push();
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.rotation));
            entity.animatedRenderer.render(tickDelta, entity.age, matrices, vertexConsumers, light);
            matrices.pop();
            return;
        }
        if (entity.tree == null || entity.tree.getRoot() == null) return;
        if (isAnimating(entity)) {
            Map<Integer, RenderingTreeNode> nodes = new HashMap<>();
            final Pose identity = new Pose(0, 0, 0, (float) Math.PI / 2, 0);
            renderTree(entity.tree.getRoot(), 1f, identity, nodes);
            List<AnimatedTreeNode> animation = makeAnimation(nodes, entity);
            entity.lastNodes = nodes;
            entity.animatedRenderer = new AnimatedRBTreeEntityRenderer(animation, entity.age, itemRenderer);
            entity.animatedRenderer.render(tickDelta, entity.age, matrices, vertexConsumers, light);
            return;
        }
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.rotation));
        render(entity.lastNodes, matrices, vertexConsumers, light);
        matrices.pop();
    }

    private void renderRoot(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0, 0.05, 0);
        final ItemStack sapling = new ItemStack(forj.tree1024.item.Items.RB_TREE_SAPLING);
        final Quaternion rotate45 = Vec3f.POSITIVE_Y.getDegreesQuaternion(45);
        final Quaternion rotate90 = Vec3f.POSITIVE_Y.getDegreesQuaternion(90);
        matrices.multiply(rotate45);
        matrices.push();
        matrices.scale(0.65f, 0.22f, 0f);
        itemRenderer.renderItem(sapling, ModelTransformation.Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 1);
        matrices.pop();
        matrices.multiply(rotate90);
        matrices.push();
        matrices.scale(0.65f, 0.22f, 0f);
        itemRenderer.renderItem(sapling, ModelTransformation.Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 1);
        matrices.pop();
        matrices.pop();
    }

    public static final float BRANCH_WH_RATIO = 4.5f;
    public static final float BRANCH_SIZE = 0.6f;


    // pitch, yaw, roll: radians
    private void renderTree(RBTreeNode<Integer, ItemStack> node, float size, Pose pose, Map<Integer, RenderingTreeNode> result) {
        if (node == null) return;
        result.put(node.key, new RenderingTreeNode(
                pose, node.roll, size, node.id,
                node.value, node.parent, node.color == RBTreeMap.BLACK, node.key
        ));
        final float branchingAngle = (float) Math.toRadians(20);
        float newSize = size - (float) Math.log10(size + 1);
        Pose jointPose = pose.forward(size * BRANCH_WH_RATIO * BRANCH_SIZE / 2);
        if (node.left != null && node.right != null) {
            // left branch
            Pose leftPose = jointPose.rotate(node.left.pitch + branchingAngle, node.left.yaw);
            renderTree(node.left, newSize,
                    leftPose.forward(newSize * BRANCH_WH_RATIO * BRANCH_SIZE / 2),
                    result);
            // right branch
            Pose rightPose = jointPose.rotate(node.right.pitch - branchingAngle, node.right.yaw);
            renderTree(node.right, newSize,
                    rightPose.forward(newSize * BRANCH_WH_RATIO * BRANCH_SIZE / 2),
                    result);
        } else if (node.left != null) {
            // left branch
            Pose leftPose = jointPose.rotate(node.left.pitch, node.left.yaw);
            renderTree(node.left, newSize,
                    leftPose.forward(newSize * BRANCH_WH_RATIO * BRANCH_SIZE / 2),
                    result);
        } else if (node.right != null) {
            // right branch
            Pose rightPose = jointPose.rotate(node.right.pitch, node.right.yaw);
            renderTree(node.right, newSize,
                    rightPose.forward(newSize * BRANCH_WH_RATIO * BRANCH_SIZE / 2),
                    result);
        }
    }

    private void render(Map<Integer, RenderingTreeNode> nodes, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0, 1.1, 0);
        for (RenderingTreeNode node : nodes.values()) {
            matrices.push();
            node.pose.apply(matrices);
            matrices.multiply(Quaternion.fromEulerXyz(0, node.roll, 0));
            matrices.scale(node.size, node.size, node.size);
            renderNode(matrices, vertexConsumers, node.value, node.isBlack, light);
            matrices.pop();
        }
        matrices.pop();
    }

    static final Quaternion ROTATE90 = Vec3f.POSITIVE_Y.getDegreesQuaternion(90);
    static final ItemStack BLACK_BRANCH = new ItemStack(Items.BLACK_BRANCH_ITEM);
    static final ItemStack RED_BRANCH = new ItemStack(Items.RED_BRANCH_ITEM);
    static final float ICON_SIZE = 0.78f;

    private void renderNode(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack item, boolean isBlack, int light) {
        matrices.push();
        for (int i = 0; i < 4; i++) {
            matrices.push();
            matrices.translate(0, 0, 0.5f * BRANCH_SIZE);
            matrices.scale(BRANCH_SIZE, BRANCH_WH_RATIO * BRANCH_SIZE, 0.1f * BRANCH_SIZE);
            matrices.scale(ICON_SIZE, ICON_SIZE, ICON_SIZE);
            itemRenderer.renderItem(item, ModelTransformation.Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 1);
            matrices.scale(1f / ICON_SIZE, 1f / ICON_SIZE, 0);
            itemRenderer.renderItem(isBlack ? BLACK_BRANCH : RED_BRANCH, ModelTransformation.Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);
            matrices.pop();
            matrices.multiply(ROTATE90);
        }
        matrices.pop();
    }

    private boolean isAnimating(RBTreeNode<Integer, ItemStack> node, RBTreeEntity entity) {
        if (!entity.lastNodes.containsKey(node.key) || entity.lastNodes.get(node.key).parent != node.parent ||
                entity.lastNodes.get(node.key).value != node.value) return true;
        if (node.left != null && isAnimating(node.left, entity)) return true;
        return node.right != null && isAnimating(node.right, entity);
    }

    private boolean isAnimating(RBTreeEntity entity) {
        if (entity.tree == null) return false;
        if (entity.tree.getRoot() == null) return !entity.lastNodes.isEmpty();
        return entity.tree.size() != entity.lastNodes.size() || isAnimating(entity.tree.getRoot(), entity);
    }

    private List<AnimatedTreeNode> makeAnimation(Map<Integer, RenderingTreeNode> nodes, RBTreeEntity entity) {
        List<AnimatedTreeNode> animation = new ArrayList<>();
        for (RenderingTreeNode node : nodes.values()) {
            if (!entity.lastNodes.containsKey(node.key) || !entity.lastNodes.get(node.key).value.getItem().equals(node.value.getItem())) {
                // new node
                Pose prevPose = node.pose.forward(-node.size * BRANCH_WH_RATIO * BRANCH_SIZE / 2);
                animation.add(new AnimatedTreeNode(
                        node.value, node.isBlack,
                        prevPose.x, prevPose.y, prevPose.z,
                        node.pose.x, node.pose.y, node.pose.z,
                        node.pose.pitch, node.pose.yaw, node.roll,
                        node.pose.pitch, node.pose.yaw, node.roll,
                        0f, node.size
                ));
                continue;
            }
            // moving node
            RenderingTreeNode lastNode = entity.lastNodes.get(node.key);
            animation.add(new AnimatedTreeNode(
                    node.value, lastNode.isBlack,
                    lastNode.pose.x, lastNode.pose.y, lastNode.pose.z,
                    node.pose.x, node.pose.y, node.pose.z,
                    lastNode.pose.pitch, lastNode.pose.yaw, lastNode.roll,
                    node.pose.pitch, node.pose.yaw, node.roll,
                    lastNode.size, node.size
            ));
            entity.lastNodes.remove(node.key);
        }
        for (RenderingTreeNode node : entity.lastNodes.values()) {
            // removed node
            Pose prevPose = node.pose.forward(-node.size * BRANCH_WH_RATIO * BRANCH_SIZE / 2);
            animation.add(new AnimatedTreeNode(
                    node.value, node.isBlack,
                    node.pose.x, node.pose.y, node.pose.z,
                    prevPose.x, prevPose.y, prevPose.z,
                    node.pose.pitch, node.pose.yaw, node.roll,
                    node.pose.pitch, node.pose.yaw, node.roll,
                    node.size, 0f
            ));
        }
        entity.lastNodes.clear();
        return animation;
    }
}

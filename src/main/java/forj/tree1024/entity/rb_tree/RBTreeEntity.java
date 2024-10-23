package forj.tree1024.entity.rb_tree;

import forj.tree1024.network.RBTreeNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.*;

public class RBTreeEntity extends Entity {
    public final RBTreeMap<Integer, ItemStack> tree = new RBTreeMap<>();
    private boolean dirty = false;
    private int cooldown = 0;
    AnimatedRBTreeEntityRenderer animatedRenderer = null;
    Map<Integer, RenderingTreeNode> lastNodes = new HashMap<>();
    public float rotation = RBTreeNode.RANDOM.nextFloat(0f, 360f);

    public RBTreeEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
    }

    public ItemStack put(int key, ItemStack value) {
        dirty = true;
        return tree.put(key, value);
    }

    public ItemStack remove(int key) {
        dirty = true;
        return tree.remove(key);
    }

    private static final Random random = new Random();

    @Override
    public void tick() {
        super.tick();
        if (world.isClient) return;
        Block block = world.getBlockState(getBlockPos().down()).getBlock();
        if (block == Blocks.AIR || block.getHardness() == -1) {
            this.dropAll();
            this.discard();
            return;
        }
        if (cooldown > 0) {
            cooldown--;
        } else {
            processItems();
        }
        if (dirty) {
            dirty = false;
            RBTreeNetworking.sendUpdate(this);
        }
    }

    private void dropAll() {
        for (ItemStack stack : tree.values()) {
            ItemEntity item = new ItemEntity(
                    world, getX(), getY(), getZ(), stack,
                    random.nextDouble(-THROW_RANGE, THROW_RANGE),
                    THROW_HEIGHT,
                    random.nextDouble(-THROW_RANGE, THROW_RANGE)
            );
            ((ItemEntityAccessor) item).setTreeInsertCooldown(140);
            world.spawnEntity(item);
        }
        tree.clear();
        dirty = true;
        cooldown = 0;
    }

    public static final Set<Item> invalidItems = Set.of(Items.AIR, Items.FIRE_CHARGE, Items.SNOWBALL, Items.TORCH, Items.SOUL_TORCH, Items.DRAGON_EGG);

    private static final double THROW_RANGE = 0.25;
    private static final double THROW_HEIGHT = 0.2;

    private void processItems() {
        List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, new Box(this.getBlockPos()), e -> true);
        for (ItemEntity item : items) {
            if (((ItemEntityAccessor) item).isTreeInsertCoolingDown()) continue;
            ItemStack stack = item.getStack();
            item.discard();
            if (invalidItems.contains(stack.getItem())) {
                ItemStack oldStack = remove(stack.getCount());
                ItemEntity newItem1 = new ItemEntity(
                        world, getX(), getY(), getZ(), stack,
                        random.nextDouble(-THROW_RANGE, THROW_RANGE),
                        THROW_HEIGHT,
                        random.nextDouble(-THROW_RANGE, THROW_RANGE)
                );
                ((ItemEntityAccessor) newItem1).setTreeInsertCooldown(140);
                world.spawnEntity(newItem1);
                if (oldStack == null) return;
                ItemEntity newItem2 = new ItemEntity(
                        world, getX(), getY(), getZ(), oldStack,
                        random.nextDouble(-THROW_RANGE, THROW_RANGE),
                        THROW_HEIGHT,
                        random.nextDouble(-THROW_RANGE, THROW_RANGE)
                );
                ((ItemEntityAccessor) newItem2).setTreeInsertCooldown(140);
                world.spawnEntity(newItem2);
            } else {
                ItemStack oldStack = put(stack.getCount(), stack);
                if (oldStack != null) {
                    ItemEntity newItem = new ItemEntity(
                            world, getX(), getY(), getZ(), oldStack,
                            random.nextDouble(-THROW_RANGE, THROW_RANGE),
                            THROW_HEIGHT,
                            random.nextDouble(-THROW_RANGE, THROW_RANGE)
                    );
                    ((ItemEntityAccessor) newItem).setTreeInsertCooldown(140);
                    world.spawnEntity(newItem);
                }
            }
            cooldown = AnimatedRBTreeEntityRenderer.ANIMATION_LENGTH;
            break;
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("tree")) {
            tree.clear();
            RBTreeNode<Integer, ItemStack> root = new RBTreeNode<>(nbt.getCompound("tree"));
            tree.setRoot(root);
            tree.size = 0;
            for (Integer ignored : tree.keySet()) tree.size++;
        } else {
            tree.setRoot(null);
        }
        if (nbt.contains("rotation")) {
            rotation = nbt.getFloat("rotation");
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (tree.getRoot() != null) {
            nbt.put("tree", tree.getRoot().save());
            nbt.putFloat("rotation", rotation);
        }
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

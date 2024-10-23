package forj.tree1024.entity.rb_tree;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class RBTreeNode<K, V> implements Map.Entry<K, V> {
    // Rendering fields
    static final Random RANDOM = new Random();
    private static final AtomicLong COUNTER = new AtomicLong(0);
    public final long id;
    public float roll, pitch, yaw;

    // Logic fields
    public K key;
    public V value;
    public RBTreeNode<K, V> left;
    public RBTreeNode<K, V> right;
    public RBTreeNode<K, V> parent;
    public boolean color = RBTreeMap.BLACK;

    private static final float MaxPitchRotation = (float) Math.toRadians(9);
    private static final float MaxYawRotation = (float) Math.toRadians(10);

    public RBTreeNode(K key, V value, RBTreeNode<K, V> parent) {
        this.id = COUNTER.incrementAndGet();
        this.key = key;
        this.value = value;
        this.parent = parent;
        this.roll = RANDOM.nextFloat(0, (float) Math.PI * 2);
        this.pitch = RANDOM.nextFloat((float) Math.PI / 2 - MaxPitchRotation, (float) Math.PI / 2);
        this.yaw = RANDOM.nextFloat(-MaxYawRotation, MaxYawRotation);
    }

    @SuppressWarnings("unchecked")
    protected RBTreeNode(NbtCompound nbt, RBTreeNode<K, V> parent) {
        this.id = COUNTER.incrementAndGet();
        this.roll = nbt.getFloat("roll");
        this.pitch = nbt.getFloat("pitch");
        this.yaw = nbt.getFloat("yaw");
        this.color = nbt.getBoolean("isBlack");
        try {
            this.key = (K) (Integer) nbt.getInt("key");
            this.value = (V) ItemStack.fromNbt(nbt.getCompound("value"));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid key or value type");
        }
        this.parent = parent;
        if (nbt.contains("left")) this.left = new RBTreeNode<>(nbt.getCompound("left"), this);
        if (nbt.contains("right")) this.right = new RBTreeNode<>(nbt.getCompound("right"), this);
    }

    public RBTreeNode(NbtCompound nbt) {
        this(nbt, null);
    }

    protected RBTreeNode(RBTreeNode<K, V> node) {
        this.id = node.id;
        this.roll = node.roll;
        this.pitch = node.pitch;
        this.yaw = node.yaw;
        this.color = node.color;
        this.key = node.key;
        this.value = node.value;
        this.parent = node.parent;
        this.left = node.left;
        this.right = node.right;
    }

    public RBTreeNode<K, V> copy() {
        return new RBTreeNode<>(this);
    }

    public NbtCompound save() {
        NbtCompound nbt = new NbtCompound();
        nbt.putFloat("roll", roll);
        nbt.putFloat("pitch", pitch);
        nbt.putFloat("yaw", yaw);
        nbt.putBoolean("isBlack", color);
        try {
            nbt.putInt("key", (Integer) key);
            nbt.put("value", Objects.requireNonNullElseGet((ItemStack) value,
                            () -> new ItemStack(Items.AIR))
                    .writeNbt(new NbtCompound()));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid key or value type");
        }
        if (left != null) nbt.put("left", left.save());
        if (right != null) nbt.put("right", right.save());
        return nbt;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public V setValue(V value) {
        V oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    public boolean equals(Object o) {
        return o instanceof Map.Entry<?, ?> e
                && RBTreeMap.valEquals(key, e.getKey())
                && RBTreeMap.valEquals(value, e.getValue());
    }

    public int hashCode() {
        int keyHash = (key == null ? 0 : key.hashCode());
        int valueHash = (value == null ? 0 : value.hashCode());
        return keyHash ^ valueHash;
    }

    public String toString() {
        return key + "=" + value;
    }
}

package forj.tree1024.network;

import forj.tree1024.entity.Entities;
import forj.tree1024.entity.rb_tree.RBTreeEntity;
import forj.tree1024.entity.rb_tree.RBTreeNode;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.List;

public class RBTreeNetworking {
    public static final Identifier UPDATE_PACKET = new Identifier("tree1024", "rb_tree_update");
    public static final Identifier REQUEST_PACKET = new Identifier("tree1024", "rb_tree_request");

    public static void sendUpdate(RBTreeEntity tree) {
        if (tree.getWorld().isClient()) return;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(tree.getId());
        if (tree.tree.getRoot() != null) {
            buf.writeBoolean(true);
            buf.writeNbt(tree.tree.getRoot().save());
            buf.writeFloat(tree.rotation);
        } else {
            buf.writeBoolean(false);
        }
        for (ServerPlayerEntity player : PlayerLookup.world((ServerWorld) tree.getWorld())) {
            ServerPlayNetworking.send(player, UPDATE_PACKET, buf);
        }
    }

    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_PACKET,
                (client, handler, buf, responseSender) -> {
                    int id = buf.readVarInt();
                    boolean hasRoot = buf.readBoolean();
                    final NbtCompound rootNBT;
                    final float rotation;
                    if (hasRoot) {
                        rootNBT = buf.readNbt();
                        rotation = buf.readFloat();
                    } else {
                        rootNBT = null;
                        rotation = 0.0f;
                    }
                    client.execute(() -> {
                        if (client.player == null) return;
                        Entity entity = client.player.world.getEntityById(id);
                        if (!(entity instanceof RBTreeEntity tree)) return;
                        if (hasRoot) {
                            tree.rotation = rotation;
                            tree.tree.setRoot(new RBTreeNode<>(rootNBT));
                            tree.tree.size = 0;
                            for (Integer ignored : tree.tree.keySet()) tree.tree.size++;
                        } else {
                            tree.tree.setRoot(null);
                            tree.tree.size = 0;
                        }
                    });
                });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
                ClientPlayNetworking.send(REQUEST_PACKET, PacketByteBufs.empty())
        );
    }

    public static void initServer() {
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(
                (player, oldWorld, newWorld) -> {
                    List<? extends RBTreeEntity> trees = newWorld
                            .getEntitiesByType(Entities.RB_TREE_ENTITY, entity -> true);
                    for (RBTreeEntity tree : trees) {
                        if (tree.getWorld().isClient()) return;
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeVarInt(tree.getId());
                        if (tree.tree.getRoot() != null) {
                            buf.writeBoolean(true);
                            buf.writeNbt(tree.tree.getRoot().save());
                            buf.writeFloat(tree.rotation);
                        } else {
                            buf.writeBoolean(false);
                        }
                        ServerPlayNetworking.send(player, UPDATE_PACKET, buf);
                    }
                }
        );
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_PACKET,
                (server, player, handler, buf, responseSender) -> {
                    List<? extends RBTreeEntity> trees = ((ServerWorld) (player.world))
                            .getEntitiesByType(Entities.RB_TREE_ENTITY, entity -> true);
                    for (RBTreeEntity tree : trees) {
                        PacketByteBuf buf2 = PacketByteBufs.create();
                        buf2.writeVarInt(tree.getId());
                        if (tree.tree.getRoot() != null) {
                            buf2.writeBoolean(true);
                            buf2.writeNbt(tree.tree.getRoot().save());
                            buf2.writeFloat(tree.rotation);
                        } else {
                            buf2.writeBoolean(false);
                        }
                        ServerPlayNetworking.send(player, UPDATE_PACKET, buf2);
                    }
                });
    }
}

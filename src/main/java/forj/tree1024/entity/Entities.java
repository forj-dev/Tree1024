package forj.tree1024.entity;

import forj.tree1024.entity.rb_tree.RBTreeEntity;
import forj.tree1024.entity.rb_tree.RBTreeEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Entities {
    public static final EntityType<RBTreeEntity> RB_TREE_ENTITY = Registry.register(Registry.ENTITY_TYPE, new Identifier("tree1024", "rb_tree"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, RBTreeEntity::new).dimensions(EntityDimensions.fixed(1, 1)).build());

    public static void init() {
    }

    public static void initClient() {
        EntityRendererRegistry.register(RB_TREE_ENTITY, RBTreeEntityRenderer::new);
    }
}

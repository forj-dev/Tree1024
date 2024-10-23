package forj.tree1024;

import forj.tree1024.entity.Entities;
import forj.tree1024.item.Items;
import forj.tree1024.network.RBTreeNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;

public class Tree1024 implements ModInitializer {

    @Override
    public void onInitialize() {
        Entities.init();
        Items.register();
        RBTreeNetworking.initServer();
    }
}

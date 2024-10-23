package forj.tree1024;

import forj.tree1024.entity.Entities;
import forj.tree1024.network.RBTreeNetworking;
import net.fabricmc.api.ClientModInitializer;

public class Tree1024Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Entities.initClient();
        RBTreeNetworking.initClient();
    }
}

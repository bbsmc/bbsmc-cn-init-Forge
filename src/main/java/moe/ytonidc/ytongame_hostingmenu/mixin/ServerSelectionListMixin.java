package moe.ytonidc.ytongame_hostingmenu.mixin;

import moe.ytonidc.ytongame_hostingmenu.client.MultiPlayerAdEntry;
import moe.ytonidc.ytongame_hostingmenu.client.RegionDetector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerSelectionList.class)
public class ServerSelectionListMixin extends ObjectSelectionList<ServerSelectionList.Entry> {
    public ServerSelectionListMixin(Minecraft p_94442_, int p_94443_, int p_94444_, int p_94445_, int p_94446_, int p_94447_) {
        super(p_94442_, p_94443_, p_94444_, p_94445_, p_94446_, p_94447_);
    }

    @Inject(
            method = "refreshEntries",
            at = @At("TAIL")
    )
    private void addAdEntry(CallbackInfo ci) {
        // 仅对中国大陆用户显示广告
        if (RegionDetector.shouldShowAds()) {
            addEntry(new MultiPlayerAdEntry(this.minecraft));
        }
    }
}
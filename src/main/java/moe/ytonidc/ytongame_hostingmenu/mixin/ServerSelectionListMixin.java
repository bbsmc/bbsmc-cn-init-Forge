package moe.ytonidc.ytongame_hostingmenu.mixin;

import moe.ytonidc.ytongame_hostingmenu.client.MultiPlayerAdEntry;
import moe.ytonidc.ytongame_hostingmenu.client.RegionDetector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.ServerSelectionList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerSelectionList.class)
public abstract class ServerSelectionListMixin {

    @Shadow @Final private List<GuiListExtended.IGuiListEntry> serverListInternet;

    @Inject(method = "updateOnlineServers", at = @At("TAIL"))
    private void addAdEntry(CallbackInfo ci) {
        // 仅对中国大陆用户显示广告
        if (RegionDetector.shouldShowAds()) {
            serverListInternet.add(new MultiPlayerAdEntry(Minecraft.getMinecraft()));
        }
    }
}

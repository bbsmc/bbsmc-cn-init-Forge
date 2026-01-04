package moe.ytonidc.ytongame_hostingmenu.mixin;

import moe.ytonidc.ytongame_hostingmenu.client.MultiPlayerAdEntry;
import moe.ytonidc.ytongame_hostingmenu.client.RegionDetector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ServerSelectionList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerSelectionList.class)
public class ServerSelectionListMixin extends ExtendedList<ServerSelectionList.Entry> {
    public ServerSelectionListMixin(Minecraft p_93403_, int p_93404_, int p_93405_, int p_93406_, int p_93407_, int p_93408_) {
        super(p_93403_, p_93404_, p_93405_, p_93406_, p_93407_, p_93408_);
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

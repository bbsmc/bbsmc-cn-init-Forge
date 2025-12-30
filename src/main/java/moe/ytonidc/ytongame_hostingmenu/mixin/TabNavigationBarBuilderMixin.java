package moe.ytonidc.ytongame_hostingmenu.mixin;

import moe.ytonidc.ytongame_hostingmenu.client.HostingTab;
import moe.ytonidc.ytongame_hostingmenu.client.RegionDetector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TabNavigationBar.Builder.class)
public class TabNavigationBarBuilderMixin {

    @Shadow @Final private TabManager tabManager;
    @Shadow @Final private List<Tab> tabs;
    @Shadow private int width;

    @Inject(method = "build", at = @At("HEAD"))
    private void onBuild(CallbackInfoReturnable<TabNavigationBar> cir) {
        // 仅对中国大陆用户显示 Hosting 标签
        if (!RegionDetector.shouldShowAds()) {
            return;
        }

        // 检查当前屏幕是否为 CreateWorldScreen
        if (!(Minecraft.getInstance().screen instanceof CreateWorldScreen screen)) {
            return;
        }

        // 检查是否已有 HostingTab
        for (Tab tab : tabs) {
            if (tab instanceof HostingTab) {
                return;
            }
        }

        // 添加 HostingTab
        tabs.add(new HostingTab(screen));
    }
}

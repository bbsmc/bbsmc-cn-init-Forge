package moe.ytonidc.ytongame_hostingmenu.mixin;

import moe.ytonidc.ytongame_hostingmenu.client.HostingTab;
import moe.ytonidc.ytongame_hostingmenu.client.RegionDetector;
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

        boolean hasGameTab = false;
        CreateWorldScreen screen = null;

        for (Tab tab : tabs) {
            String className = tab.getClass().getName();
            if (className.contains("CreateWorldScreen$GameTab")) {
                hasGameTab = true;
                try {
                    var field = tab.getClass().getDeclaredField("this$0");
                    field.setAccessible(true);
                    screen = (CreateWorldScreen) field.get(tab);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        if (hasGameTab && screen != null) {
            boolean hasHostingTab = false;
            for (Tab tab : tabs) {
                if (tab instanceof HostingTab) {
                    hasHostingTab = true;
                    break;
                }
            }

            if (!hasHostingTab) {
                tabs.add(new HostingTab(screen));
            }
        }
    }
}

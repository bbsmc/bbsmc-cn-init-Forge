package moe.ytonidc.ytongame_hostingmenu.mixin;

import moe.ytonidc.ytongame_hostingmenu.client.HostingScreen;
import moe.ytonidc.ytongame_hostingmenu.client.RegionDetector;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiCreateWorld.class)
public abstract class GuiCreateWorldMixin extends GuiScreen {

    @Unique
    private GuiButton ytongame$hostingButton;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        // 仅对中国大陆用户显示联机开服按钮
        if (!RegionDetector.shouldShowAds()) {
            return;
        }

        // 在标题右侧添加"联机开服"按钮
        int buttonWidth = 60;
        int buttonHeight = 20;
        int buttonX = this.width / 2 + 80;
        int buttonY = 8;

        ytongame$hostingButton = new GuiButton(999, buttonX, buttonY, buttonWidth, buttonHeight, "联机开服(广告)");
        this.buttonList.add(ytongame$hostingButton);
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    private void onActionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == 999) {
            this.mc.displayGuiScreen(new HostingScreen(this));
            ci.cancel();
        }
    }
}

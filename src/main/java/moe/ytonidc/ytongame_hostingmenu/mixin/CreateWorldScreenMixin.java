package moe.ytonidc.ytongame_hostingmenu.mixin;

import moe.ytonidc.ytongame_hostingmenu.client.HostingScreen;
import moe.ytonidc.ytongame_hostingmenu.client.RegionDetector;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {

    protected CreateWorldScreenMixin(Component title) {
        super(title);
    }

    @Unique
    private Button ytongame$hostingButton;

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        // 仅对中国大陆用户显示联机开服按钮
        if (!RegionDetector.shouldShowAds()) {
            return;
        }

        // 在标题右侧添加"联机开服"按钮
        int buttonWidth = 60;
        int buttonHeight = 20;
        int buttonX = this.width / 2 + 80;  // 标题右侧
        int buttonY = 8;  // 标题位置

        ytongame$hostingButton = new Button(buttonX, buttonY, buttonWidth, buttonHeight,
            Component.literal("联机开服"),
            button -> this.minecraft.setScreen(new HostingScreen(this)));

        this.addRenderableWidget(ytongame$hostingButton);
    }
}

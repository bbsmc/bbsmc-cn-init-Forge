package moe.ytonidc.ytongame_hostingmenu.mixin;

import moe.ytonidc.ytongame_hostingmenu.client.HostingScreen;
import moe.ytonidc.ytongame_hostingmenu.client.RegionDetector;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SelectWorldScreen.class)
public abstract class SelectWorldScreenMixin extends Screen {

    @Shadow private EditBox searchBox;

    protected SelectWorldScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        // 仅对中国大陆用户显示 Hosting 按钮
        if (!RegionDetector.shouldShowAds()) {
            return;
        }

        if (searchBox != null) {
            int buttonWidth = 60;
            int buttonX = searchBox.x + searchBox.getWidth() + 4;
            int buttonY = searchBox.y;
            int buttonHeight = searchBox.getHeight();

            Button hostingButton = new Button(buttonX, buttonY, buttonWidth, buttonHeight,
                new TextComponent("联机开服"),
                button -> this.minecraft.setScreen(new HostingScreen(this)));

            this.addRenderableWidget(hostingButton);
        }
    }
}

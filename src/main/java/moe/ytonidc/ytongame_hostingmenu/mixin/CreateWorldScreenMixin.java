package moe.ytonidc.ytongame_hostingmenu.mixin;

import moe.ytonidc.ytongame_hostingmenu.Config;
import moe.ytonidc.ytongame_hostingmenu.client.HostingTab;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {

    protected CreateWorldScreenMixin(Component title) {
        super(title);
    }

    @Shadow @Final private TabManager tabManager;

    @Unique
    private static final Component SUBSCRIBE_TEXT = Component.literal("订阅服务器");

    @Unique
    private Component ytongame$originalCreateButtonText = null;

    @Unique
    private Button ytongame$findCreateButton() {
        for (var child : this.children()) {
            if (child instanceof Button button) {
                String msg = button.getMessage().getString();
                if (msg.contains("创建") || msg.contains("Create") || msg.equals("订阅服务器")) {
                    return button;
                }
            }
        }
        return null;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Tab currentTab = tabManager.getCurrentTab();

        Button createButton = ytongame$findCreateButton();
        if (createButton != null) {
            if (currentTab instanceof HostingTab) {
                if (ytongame$originalCreateButtonText == null) {
                    ytongame$originalCreateButtonText = createButton.getMessage();
                }
                if (!createButton.getMessage().getString().equals("订阅服务器")) {
                    createButton.setMessage(SUBSCRIBE_TEXT);
                }
            } else {
                if (ytongame$originalCreateButtonText != null &&
                    createButton.getMessage().getString().equals("订阅服务器")) {
                    createButton.setMessage(ytongame$originalCreateButtonText);
                }
            }
        }

        if (currentTab instanceof HostingTab hostingTab) {
            hostingTab.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        Tab currentTab = tabManager.getCurrentTab();
        if (currentTab instanceof HostingTab hostingTab) {
            var list = hostingTab.getPackageList();
            if (list != null && list.mouseScrolled(mouseX, mouseY, delta)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Tab currentTab = tabManager.getCurrentTab();
        if (currentTab instanceof HostingTab hostingTab) {
            // 检查是否点击了订阅服务器按钮
            Button createButton = ytongame$findCreateButton();
            if (createButton != null && button == 0) {
                if (mouseX >= createButton.getX() && mouseX <= createButton.getX() + createButton.getWidth() &&
                    mouseY >= createButton.getY() && mouseY <= createButton.getY() + createButton.getHeight()) {
                    ytongame$openPurchaseLink();
                    return true;
                }
            }

            var list = hostingTab.getPackageList();
            if (list != null && list.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Unique
    private void ytongame$openPurchaseLink() {
        try {
            Util.getPlatform().openUri(new URI(Config.getPurchaseUrl()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Tab currentTab = tabManager.getCurrentTab();
        if (currentTab instanceof HostingTab hostingTab) {
            var list = hostingTab.getPackageList();
            if (list != null && list.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        Tab currentTab = tabManager.getCurrentTab();
        if (currentTab instanceof HostingTab hostingTab) {
            var list = hostingTab.getPackageList();
            if (list != null && list.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
}

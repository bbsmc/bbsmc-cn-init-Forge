package moe.ytonidc.ytongame_hostingmenu.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class HostingTab implements Tab {
    private static final Component TITLE = Component.literal("Hosting");

    private final CreateWorldScreen screen;
    private HostingPackageList packageList;
    private ScreenRectangle tabArea;

    public HostingTab(CreateWorldScreen screen) {
        this.screen = screen;
    }

    @Override
    public Component getTabTitle() {
        return TITLE;
    }

    @Override
    public void tick() {
    }

    @Override
    public void visitChildren(Consumer<net.minecraft.client.gui.components.AbstractWidget> consumer) {
    }

    @Override
    public void doLayout(ScreenRectangle rectangle) {
        this.tabArea = rectangle;

        Minecraft minecraft = Minecraft.getInstance();
        int listY0 = rectangle.top();
        int listY1 = rectangle.top() + rectangle.height() - 25;

        this.packageList = new HostingPackageList(minecraft, rectangle.width(), rectangle.height() - 25, listY0, listY1, 52);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (packageList != null) {
            packageList.render(graphics, mouseX, mouseY, partialTick);
        }

        if (tabArea != null) {
            var font = Minecraft.getInstance().font;
            String footerText = "* 所有套餐默认30G存储，濒临满可免费升级至50G";
            int footerY = tabArea.top() + tabArea.height() - 18;
            graphics.drawCenteredString(font, footerText,
                tabArea.left() + tabArea.width() / 2, footerY, 0xFF888888);
        }
    }

    public HostingPackageList getPackageList() {
        return packageList;
    }
}

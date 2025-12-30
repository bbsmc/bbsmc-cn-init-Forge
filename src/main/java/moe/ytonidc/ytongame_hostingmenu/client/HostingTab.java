package moe.ytonidc.ytongame_hostingmenu.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class HostingTab implements Tab {
    private static final Component TITLE = Component.literal("联机开服");

    // 用于从选择世界页面跳转时自动切换到 Hosting 标签
    public static boolean shouldOpenHostingTab = false;

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
            String footerText = "* 致力为您提供稳定、流畅、24小时不断联的服务器，打造更优、更稳、更好的游戏体验！无人值守也可玩！";
            int footerY = tabArea.top() + tabArea.height() - 21;

            // 绿色渐变：从浅绿色到深绿色再回到浅绿色
            int textWidth = font.width(footerText);
            int startX = tabArea.left() + (tabArea.width() - textWidth) / 2;

            int charX = startX;
            int textLength = footerText.length();
            for (int i = 0; i < textLength; i++) {
                String ch = String.valueOf(footerText.charAt(i));

                // 计算渐变颜色 (从亮绿到深绿再到亮绿)
                float progress = (float) i / (textLength - 1);
                int color = getGradientColor(progress);

                graphics.drawString(font, ch, charX, footerY, color);
                charX += font.width(ch);
            }
        }
    }

    private int getGradientColor(float progress) {
        // 从亮黄绿 (0xAAFF55) 到青绿 (0x00FF88) 到深蓝绿 (0x00AAAA) 的大跨度渐变
        int startR = 0xAA, startG = 0xFF, startB = 0x55;  // 亮黄绿
        int midR = 0x00, midG = 0xFF, midB = 0x88;        // 青绿
        int endR = 0x00, endG = 0xAA, endB = 0xCC;        // 蓝绿

        int r, g, b;
        if (progress < 0.5f) {
            // 前半段：亮黄绿 -> 青绿
            float t = progress * 2;
            r = (int) (startR + (midR - startR) * t);
            g = (int) (startG + (midG - startG) * t);
            b = (int) (startB + (midB - startB) * t);
        } else {
            // 后半段：青绿 -> 蓝绿
            float t = (progress - 0.5f) * 2;
            r = (int) (midR + (endR - midR) * t);
            g = (int) (midG + (endG - midG) * t);
            b = (int) (midB + (endB - midB) * t);
        }

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public HostingPackageList getPackageList() {
        return packageList;
    }
}

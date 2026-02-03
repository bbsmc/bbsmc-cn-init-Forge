package moe.ytonidc.ytongame_hostingmenu.client;

import moe.ytonidc.ytongame_hostingmenu.Ytongame_hostingmenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;

public class MultiPlayerAdEntry implements GuiListExtended.IGuiListEntry {
    private final Minecraft minecraft;

    public MultiPlayerAdEntry(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(Ytongame_hostingmenu.hostingLogo);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, slotHeight, slotHeight, slotHeight, slotHeight);
        
        this.minecraft.fontRenderer.drawString("如果您需要24H不间断的服务器? 点击我跳转详情!", x + 32 + 3, y + 1, 16777215);

        // 渐变色渲染描述文字（两行）
        String line1 = "推荐选用昱通游戏，我们收录且支持数百种整合包一键联机（仍在更新）";
        String line2 = "致力为您提供稳定、流畅的服务器，打造优、稳、快的游戏体验";
        int textStartX = x + 32 + 3;

        // 渲染第一行（绿色渐变）
        renderGradientText(line1, textStartX, y + 12, true);
        // 渲染第二行（黄橙渐变）
        renderGradientText(line2, textStartX, y + 12 + 9, false);
    }

    private void renderGradientText(String text, int startX, int y, boolean useGreen) {
        int charX = startX;
        int textLength = text.length();

        for (int i = 0; i < textLength; i++) {
            String ch = String.valueOf(text.charAt(i));
            float progress = (float) i / (textLength - 1);
            int color = useGreen ? getGreenGradientColor(progress) : getYellowOrangeGradientColor(progress);

            this.minecraft.fontRenderer.drawString(ch, charX, y, color);
            charX += this.minecraft.fontRenderer.getStringWidth(ch);
        }
    }

    private int getGreenGradientColor(float progress) {
        int startR = 0xAA, startG = 0xFF, startB = 0x55;
        int midR = 0x00, midG = 0xFF, midB = 0x88;
        int endR = 0x00, endG = 0xAA, endB = 0xCC;

        int r, g, b;
        if (progress < 0.5f) {
            float t = progress * 2;
            r = (int) (startR + (midR - startR) * t);
            g = (int) (startG + (midG - startG) * t);
            b = (int) (startB + (midB - startB) * t);
        } else {
            float t = (progress - 0.5f) * 2;
            r = (int) (midR + (endR - midR) * t);
            g = (int) (midG + (endG - midG) * t);
            b = (int) (midB + (endB - midB) * t);
        }

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private int getYellowOrangeGradientColor(float progress) {
        int startR = 0xFF, startG = 0xFF, startB = 0x55;
        int midR = 0xFF, midG = 0xAA, midB = 0x00;
        int endR = 0xFF, endG = 0x66, endB = 0x00;

        int r, g, b;
        if (progress < 0.5f) {
            float t = progress * 2;
            r = (int) (startR + (midR - startR) * t);
            g = (int) (startG + (midG - startG) * t);
            b = (int) (startB + (midB - startB) * t);
        } else {
            float t = (progress - 0.5f) * 2;
            r = (int) (midR + (endR - midR) * t);
            g = (int) (midG + (endG - midG) * t);
            b = (int) (midB + (endB - midB) * t);
        }

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    @Override
    public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
        if (mouseEvent == 0) {
            this.minecraft.displayGuiScreen(new HostingScreen(this.minecraft.currentScreen));
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
    }
}

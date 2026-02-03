package moe.ytonidc.ytongame_hostingmenu.client;

import moe.ytonidc.ytongame_hostingmenu.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class HostingScreen extends GuiScreen {
    private static final String TITLE = "联机开服";
    private static final String SUBSCRIBE_TEXT = "订阅服务器";

    private final GuiScreen lastScreen;
    private HostingPackageList packageList;
    private GuiButton subscribeButton;

    public HostingScreen(GuiScreen lastScreen) {
        this.lastScreen = lastScreen;
    }

    @Override
    public void initGui() {
        super.initGui();

        // 创建套餐列表，留出顶部和底部空间
        int listY0 = 32;
        int listY1 = this.height - 32;

        this.packageList = new HostingPackageList(
            this.mc,
            this.width,
            this.height - 64,
            listY0,
            listY1,
            52
        );

        // 在标题右侧添加"订阅服务器"按钮
        int buttonWidth = 80;
        int buttonHeight = 20;
        int buttonX = this.width / 2 + 50;
        int buttonY = 8;

        this.subscribeButton = new GuiButton(0, buttonX, buttonY, buttonWidth, buttonHeight, SUBSCRIBE_TEXT);
        this.buttonList.add(subscribeButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            openPurchaseLink();
        }
    }

    private void openPurchaseLink() {
        try {
            Desktop.getDesktop().browse(new URI(Config.getPurchaseUrl()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        if (packageList != null) {
            packageList.drawScreen(mouseX, mouseY, partialTicks);
        }

        // 绘制底部背景遮挡
        int footerTop = this.height - 32;
        drawRect(0, footerTop, this.width, this.height, 0xC0101010);

        super.drawScreen(mouseX, mouseY, partialTicks);

        // 绘制标题
        this.drawCenteredString(this.fontRenderer, TITLE, this.width / 2, 16, 0xFFFFFF);

        // 绘制底部文字
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        String footerText = "* 致力为您提供稳定、流畅、24小时不断联的服务器，打造更优、更稳、更好的游戏体验！无人值守也可玩！";
        int footerY = this.height - 20;

        // 绿色渐变
        int textWidth = font.getStringWidth(footerText);
        int startX = (this.width - textWidth) / 2;

        int charX = startX;
        int textLength = footerText.length();
        for (int i = 0; i < textLength; i++) {
            String ch = String.valueOf(footerText.charAt(i));
            float progress = (float) i / (textLength - 1);
            int color = getGradientColor(progress);
            font.drawString(ch, charX, footerY, color);
            charX += font.getStringWidth(ch);
        }
    }

    private int getGradientColor(float progress) {
        // 从亮黄绿到青绿到深蓝绿的渐变
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

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if (packageList != null) {
            packageList.handleMouseInput();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) { // ESC
            this.mc.displayGuiScreen(lastScreen);
        }
        super.keyTyped(typedChar, keyCode);
    }
}

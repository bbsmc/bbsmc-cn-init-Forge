package moe.ytonidc.ytongame_hostingmenu.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.ytonidc.ytongame_hostingmenu.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.net.URI;

public class HostingScreen extends Screen {
    private static final ITextComponent TITLE = new StringTextComponent("联机开服");
    private static final ITextComponent SUBSCRIBE_TEXT = new StringTextComponent("订阅服务器");

    private final Screen lastScreen;
    private HostingPackageList packageList;
    private Button subscribeButton;

    public HostingScreen(Screen lastScreen) {
        super(TITLE);
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        super.init();

        // 创建套餐列表，留出顶部和底部空间
        int listY0 = 32;
        int listY1 = this.height - 32;

        this.packageList = new HostingPackageList(
            this.minecraft,
            this.width,
            this.height - 64,  // 总高度减去顶部和底部空间
            listY0,
            listY1,
            52
        );

        // 在标题右侧添加"订阅服务器"按钮
        int buttonWidth = 80;
        int buttonHeight = 20;
        int buttonX = this.width / 2 + 50;  // 标题右侧
        int buttonY = 8;  // 标题位置

        this.subscribeButton = new Button(buttonX, buttonY, buttonWidth, buttonHeight,
            SUBSCRIBE_TEXT,
            button -> openPurchaseLink());

        this.addButton(subscribeButton);
    }

    private void openPurchaseLink() {
        try {
            Util.getPlatform().openUri(new URI(Config.getPurchaseUrl()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(matrixStack);

        if (packageList != null) {
            packageList.render(matrixStack, mouseX, mouseY, partialTick);
        }

        // 绘制底部背景遮挡，确保文字区域在最上层
        int footerTop = this.height - 32;
        fill(matrixStack, 0, footerTop, this.width, this.height, 0xC0101010);

        super.render(matrixStack, mouseX, mouseY, partialTick);

        // 绘制标题
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 16, 0xFFFFFF);

        // 绘制底部文字
        FontRenderer font = Minecraft.getInstance().font;
        String footerText = "* 致力为您提供稳定、流畅、24小时不断联的服务器，打造更优、更稳、更好的游戏体验！无人值守也可玩！";
        int footerY = this.height - 20;

        // 绿色渐变：从浅绿色到深绿色再回到浅绿色
        int textWidth = font.width(footerText);
        int startX = (this.width - textWidth) / 2;

        int charX = startX;
        int textLength = footerText.length();
        for (int i = 0; i < textLength; i++) {
            String ch = String.valueOf(footerText.charAt(i));

            // 计算渐变颜色 (从亮绿到深绿再到亮绿)
            float progress = (float) i / (textLength - 1);
            int color = getGradientColor(progress);

            font.draw(matrixStack, ch, charX, footerY, color);
            charX += font.width(ch);
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (packageList != null && packageList.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (packageList != null && packageList.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (packageList != null && packageList.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (packageList != null && packageList.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(lastScreen);
    }
}

package moe.ytonidc.ytongame_hostingmenu.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSlot;

public class HostingPackageList extends GuiSlot {
    private final Minecraft mc;
    private final java.util.List<HostingPackage> packages;

    public HostingPackageList(Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
        super(mc, width, height, top, bottom, slotHeight);
        this.mc = mc;
        this.packages = HostingPackage.getAllPackages();
    }

    @Override
    protected int getSize() {
        return packages.size();
    }

    @Override
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
        // 可以在这里添加点击事件处理
    }

    @Override
    protected boolean isSelected(int slotIndex) {
        return false;
    }

    @Override
    protected void drawBackground() {
        // 不绘制背景
    }

    @Override
    protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseX, int mouseY, float partialTicks) {
        if (slotIndex < 0 || slotIndex >= packages.size()) {
            return;
        }

        HostingPackage pkg = packages.get(slotIndex);
        FontRenderer font = mc.fontRenderer;

        int left = xPos;
        int top = yPos;
        int width = getListWidth();
        int height = this.slotHeight - 4;

        // 悬停效果
        boolean hovering = mouseX >= left && mouseX <= left + width && mouseY >= top && mouseY <= top + height;
        if (hovering) {
            Gui.drawRect(left - 2, top - 2, left + width + 2, top + height + 2, 0x80808080);
        }

        // 左边彩色边框
        int borderColor = pkg.getColor();
        Gui.drawRect(left, top, left + 4, top + height, borderColor);

        int textLeft = left + 12;
        int line1Y = top + 4;
        int line2Y = top + 18;
        int line3Y = top + 32;

        // 套餐名称
        font.drawString(pkg.getName(), textLeft, line1Y, pkg.getColor());

        // 渲染标签
        String tag = pkg.getTag();
        if (tag != null && !tag.isEmpty()) {
            int nameWidth = font.getStringWidth(pkg.getName());
            int tagX = textLeft + nameWidth + 6;
            int tagY = line1Y;
            int tagWidth = font.getStringWidth(tag) + 6;
            int tagHeight = 10;
            int tagBgColor = tag.equals("热销") ? 0xFFFF5555 : pkg.getColor();
            Gui.drawRect(tagX, tagY - 1, tagX + tagWidth, tagY + tagHeight, tagBgColor);
            font.drawString(tag, tagX + 3, tagY, 0xFFFFFFFF);
        }

        // 价格
        String priceText = "¥" + pkg.getPrice() + "/月";
        int priceWidth = font.getStringWidth(priceText);
        font.drawString(priceText, left + width - priceWidth - 10, line1Y, 0xFFFFFF55);

        // CPU
        String cpuLabel = "CPU: ";
        font.drawString(cpuLabel, textLeft, line2Y, 0xFFAAAAAA);
        int cpuLabelWidth = font.getStringWidth(cpuLabel);
        font.drawString(pkg.getProcessor(), textLeft + cpuLabelWidth, line2Y, 0xFFFFAA00);

        // 其他信息
        String memoryText = "内存: " + pkg.getMemory();
        font.drawString(memoryText, textLeft, line3Y, 0xFFAAAAAA);

        String backupText = "备份: " + pkg.getDefaultBackupSlots() + "/" + pkg.getMaxBackupSlots();
        font.drawString(backupText, textLeft + 80, line3Y, 0xFFAAAAAA);

        String storageText = "存储: " + pkg.getStorage();
        font.drawString(storageText, textLeft + 160, line3Y, 0xFFAAAAAA);

        String playersText = "推荐: " + pkg.getRecommendedPlayers();
        font.drawString(playersText, textLeft + 250, line3Y, 0xFFAAAAAA);
    }

    @Override
    public int getListWidth() {
        return this.width - 40;
    }

    @Override
    protected int getScrollBarX() {
        return this.width - 6;
    }
}

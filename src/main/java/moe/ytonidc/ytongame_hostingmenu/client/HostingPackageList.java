package moe.ytonidc.ytongame_hostingmenu.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class HostingPackageList extends ExtendedList<HostingPackageList.Entry> {

    public HostingPackageList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
        super(minecraft, width, height, y0, y1, itemHeight);

        for (HostingPackage pkg : HostingPackage.getAllPackages()) {
            this.addEntry(new Entry(pkg));
        }
    }

    @Override
    public int getRowWidth() {
        return this.width - 40;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 6;
    }

    public class Entry extends ExtendedList.AbstractListEntry<Entry> {
        private final HostingPackage pkg;

        public Entry(HostingPackage pkg) {
            this.pkg = pkg;
        }

        @Override
        public void render(MatrixStack matrixStack, int index, int top, int left, int width, int height,
                          int mouseX, int mouseY, boolean hovering, float partialTick) {
            FontRenderer font = minecraft.font;

            if (hovering) {
                AbstractGui.fill(matrixStack, left - 2, top - 2, left + width + 2, top + height + 2, 0x80808080);
            }

            int borderColor = pkg.getColor();
            AbstractGui.fill(matrixStack, left, top, left + 4, top + height, borderColor);

            int textLeft = left + 12;
            int line1Y = top + 4;
            int line2Y = top + 18;
            int line3Y = top + 32;

            font.draw(matrixStack, pkg.getName(), textLeft, line1Y, pkg.getColor());

            // 渲染标签（从 JSON 获取）
            String tag = pkg.getTag();
            if (tag != null && !tag.isEmpty()) {
                int nameWidth = font.width(pkg.getName());
                int tagX = textLeft + nameWidth + 6;
                int tagY = line1Y;
                int tagWidth = font.width(tag) + 6;
                int tagHeight = 10;
                // 使用套餐颜色作为标签背景，或使用红色作为默认
                int tagBgColor = tag.equals("热销") ? 0xFFFF5555 : pkg.getColor();
                AbstractGui.fill(matrixStack, tagX, tagY - 1, tagX + tagWidth, tagY + tagHeight, tagBgColor);
                font.draw(matrixStack, tag, tagX + 3, tagY, 0xFFFFFFFF);
            }

            String priceText = "¥" + pkg.getPrice() + "/月";
            int priceWidth = font.width(priceText);
            font.draw(matrixStack, priceText, left + width - priceWidth - 10, line1Y, 0xFFFFFF55);

            String cpuLabel = "CPU: ";
            font.draw(matrixStack, cpuLabel, textLeft, line2Y, 0xFFAAAAAA);  // 灰色
            int cpuLabelWidth = font.width(cpuLabel);
            font.draw(matrixStack, pkg.getProcessor(), textLeft + cpuLabelWidth, line2Y, 0xFFFFAA00);  // 金色

            String memoryText = "内存: " + pkg.getMemory();
            font.draw(matrixStack, memoryText, textLeft, line3Y, 0xFFAAAAAA);

            String backupText = "备份: " + pkg.getDefaultBackupSlots() + "/" + pkg.getMaxBackupSlots();
            font.draw(matrixStack, backupText, textLeft + 80, line3Y, 0xFFAAAAAA);

            String storageText = "存储: " + pkg.getStorage();
            font.draw(matrixStack, storageText, textLeft + 160, line3Y, 0xFFAAAAAA);

            String playersText = "推荐: " + pkg.getRecommendedPlayers();
            font.draw(matrixStack, playersText, textLeft + 250, line3Y, 0xFFAAAAAA);
        }

        public ITextComponent getNarration() {
            return new StringTextComponent(pkg.getName() + " - ¥" + pkg.getPrice() + "/月");
        }

        public HostingPackage getPackage() {
            return pkg;
        }
    }
}

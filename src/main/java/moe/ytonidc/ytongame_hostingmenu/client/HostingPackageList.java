package moe.ytonidc.ytongame_hostingmenu.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class HostingPackageList extends ObjectSelectionList<HostingPackageList.Entry> {

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

    public class Entry extends ObjectSelectionList.Entry<Entry> {
        private final HostingPackage pkg;

        public Entry(HostingPackage pkg) {
            this.pkg = pkg;
        }

        @Override
        public void render(PoseStack poseStack, int index, int top, int left, int width, int height,
                          int mouseX, int mouseY, boolean hovering, float partialTick) {
            var font = minecraft.font;

            if (hovering) {
                GuiComponent.fill(poseStack, left - 2, top - 2, left + width + 2, top + height + 2, 0x80808080);
            }

            int borderColor = pkg.getColor();
            GuiComponent.fill(poseStack, left, top, left + 4, top + height, borderColor);

            int textLeft = left + 12;
            int line1Y = top + 4;
            int line2Y = top + 18;
            int line3Y = top + 32;

            font.draw(poseStack, pkg.getName(), textLeft, line1Y, pkg.getColor());

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
                GuiComponent.fill(poseStack, tagX, tagY - 1, tagX + tagWidth, tagY + tagHeight, tagBgColor);
                font.draw(poseStack, tag, tagX + 3, tagY, 0xFFFFFFFF);
            }

            String priceText = "¥" + pkg.getPrice() + "/月";
            int priceWidth = font.width(priceText);
            font.draw(poseStack, priceText, left + width - priceWidth - 10, line1Y, 0xFFFFFF55);

            String cpuLabel = "CPU: ";
            font.draw(poseStack, cpuLabel, textLeft, line2Y, 0xFFAAAAAA);  // 灰色
            int cpuLabelWidth = font.width(cpuLabel);
            font.draw(poseStack, pkg.getProcessor(), textLeft + cpuLabelWidth, line2Y, 0xFFFFAA00);  // 金色

            String memoryText = "内存: " + pkg.getMemory();
            font.draw(poseStack, memoryText, textLeft, line3Y, 0xFFAAAAAA);

            String backupText = "备份: " + pkg.getDefaultBackupSlots() + "/" + pkg.getMaxBackupSlots();
            font.draw(poseStack, backupText, textLeft + 80, line3Y, 0xFFAAAAAA);

            String storageText = "存储: " + pkg.getStorage();
            font.draw(poseStack, storageText, textLeft + 160, line3Y, 0xFFAAAAAA);

            String playersText = "推荐: " + pkg.getRecommendedPlayers();
            font.draw(poseStack, playersText, textLeft + 250, line3Y, 0xFFAAAAAA);
        }

        @Override
        public Component getNarration() {
            return new TextComponent(pkg.getName() + " - ¥" + pkg.getPrice() + "/月");
        }

        public HostingPackage getPackage() {
            return pkg;
        }
    }
}

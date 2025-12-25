package moe.ytonidc.ytongame_hostingmenu.client;

import moe.ytonidc.ytongame_hostingmenu.Config;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;

import java.net.URI;

public class HostingPackageList extends ObjectSelectionList<HostingPackageList.Entry> {

    public HostingPackageList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
        super(minecraft, width, height, y0, y1, itemHeight);

        for (HostingPackage pkg : HostingPackage.ALL_PACKAGES) {
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
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height,
                          int mouseX, int mouseY, boolean hovering, float partialTick) {
            var font = minecraft.font;

            if (hovering) {
                graphics.fill(left - 2, top - 2, left + width + 2, top + height + 2, 0x80808080);
            }

            int borderColor = pkg.getColor();
            graphics.fill(left, top, left + 4, top + height, borderColor);

            int textLeft = left + 12;
            int line1Y = top + 4;
            int line2Y = top + 18;
            int line3Y = top + 32;

            graphics.drawString(font, pkg.getName(), textLeft, line1Y, pkg.getColor());

            String priceText = "¥" + pkg.getPrice() + "/月";
            int priceWidth = font.width(priceText);
            graphics.drawString(font, priceText, left + width - priceWidth - 70, line1Y, 0xFFFFFF55);

            String cpuLabel = "CPU: ";
            graphics.drawString(font, cpuLabel, textLeft, line2Y, 0xFFAAAAAA);  // 灰色
            int cpuLabelWidth = font.width(cpuLabel);
            graphics.drawString(font, pkg.getProcessor(), textLeft + cpuLabelWidth, line2Y, 0xFFFFAA00);  // 金色

            String memoryText = "内存: " + pkg.getMemory();
            graphics.drawString(font, memoryText, textLeft, line3Y, 0xFFAAAAAA);

            String backupText = "备份: " + pkg.getDefaultBackupSlots() + "/" + pkg.getMaxBackupSlots();
            graphics.drawString(font, backupText, textLeft + 80, line3Y, 0xFFAAAAAA);

            String storageText = "存储: " + pkg.getStorage();
            graphics.drawString(font, storageText, textLeft + 160, line3Y, 0xFFAAAAAA);

            String playersText = "推荐: " + pkg.getRecommendedPlayers();
            graphics.drawString(font, playersText, textLeft + 250, line3Y, 0xFFAAAAAA);

            int buttonX = left + width - 60;
            int buttonY = top + (height - 20) / 2;
            boolean buttonHovered = mouseX >= buttonX && mouseX <= buttonX + 55 &&
                                   mouseY >= buttonY && mouseY <= buttonY + 20;

            int buttonColor = buttonHovered ? 0xFF4080FF : 0xFF2060D0;
            graphics.fill(buttonX, buttonY, buttonX + 55, buttonY + 20, buttonColor);
            graphics.fill(buttonX, buttonY, buttonX + 55, buttonY + 1, 0xFF6090FF);
            graphics.fill(buttonX, buttonY + 19, buttonX + 55, buttonY + 20, 0xFF1040A0);

            String buyText = "购买";
            int buyTextWidth = font.width(buyText);
            graphics.drawString(font, buyText, buttonX + (55 - buyTextWidth) / 2, buttonY + 6, 0xFFFFFFFF);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                int left = HostingPackageList.this.getRowLeft();
                int width = HostingPackageList.this.getRowWidth();
                int top = 0;

                for (int i = 0; i < HostingPackageList.this.children().size(); i++) {
                    if (HostingPackageList.this.children().get(i) == this) {
                        break;
                    }
                }

                int buttonX = left + width - 60;

                if (mouseX >= buttonX && mouseX <= buttonX + 55) {
                    openPurchaseLink();
                    return true;
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        private void openPurchaseLink() {
            try {
                Util.getPlatform().openUri(new URI(Config.getPurchaseUrl()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Component getNarration() {
            return Component.literal(pkg.getName() + " - ¥" + pkg.getPrice() + "/月");
        }

        public HostingPackage getPackage() {
            return pkg;
        }
    }
}

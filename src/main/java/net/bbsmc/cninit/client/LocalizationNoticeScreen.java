package net.bbsmc.cninit.client;

import com.google.gson.JsonObject;
import net.bbsmc.cninit.BbsmcCnInit;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocalizationNoticeScreen extends GuiScreen {

    private static final String TITLE_TEXT = "\u00a7l\u00a7eBBSMC汉化包使用须知";
    private static final String[] NOTICE_LINES = {
        "\u00a7f感谢您选择BBSMC汉化包，在正式使用BBSMC汉化包进行游戏前，我们希望您能阅读以下内容。",
        "",
        "\u00a76\u00a7l一、汉化内容",
        "\u00a7fBBSMC汉化包基于AI翻译+人工精校生成，部分翻译文本可能仍带有机翻的味道，我们正在持续优化调整翻译质量。如果您发现了任何翻译不自然、不准确的地方，恳请您积极向我们反馈，我们会及时修正并重新发布更新后的汉化包。",
        "\u00a7f若您当前游玩的整合包已经有完整的人工翻译，我们也相当欢迎您使用更精准更优质的人工翻译。您可以前往VM汉化组官网：§n§bhttps://vmct-cn.top§r§f 查看更多高质量的人工精品翻译。",
        "",
        "\u00a76\u00a7l二、KubeJS 翻译覆盖",
        "\u00a7f对于包含KubeJS的整合包，我们会自动提取KubeJS脚本中的文本并进行翻译覆盖。但由于该功能目前仍处于开发阶段，系统可能不够成熟，\u00a7c\u00a7l可能会导致部分合成配方缺失等问题\u00a7r\u00a7f。",
        "\u00a7f如果您遇到此类问题，请尽快通过以下方式联系我们，我们会在几个小时内为您解决并协助修复您客户端的问题：",
        "\u00a7f - 反馈QQ群：\u00a7b\u00a7l1073724937",
        "\u00a7f - 官方网站：\u00a7n\u00a7bhttps://bbsmc.net\u00a7r\u00a7f（可获取最新的反馈QQ群号）",
        ""
    };
    private static final String AGREE_TEXT = "同意并继续";
    private static final String DECLINE_TEXT = "拒绝并退出";

    private final JsonObject modpackJson;
    private final List<String> languagePacks;
    private final File configFile;

    private final List<String> wrappedLines = new ArrayList<>();

    public LocalizationNoticeScreen(JsonObject modpackJson, List<String> languagePacks, File configFile) {
        this.modpackJson = modpackJson;
        this.languagePacks = languagePacks;
        this.configFile = configFile;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();

        int buttonWidth = 120;
        int buttonHeight = 20;
        int gap = 10;
        int totalWidth = buttonWidth * 2 + gap;
        int startX = (this.width - totalWidth) / 2;
        int buttonY = this.height - 40;

        this.buttonList.add(new GuiButton(0, startX, buttonY, buttonWidth, buttonHeight, AGREE_TEXT));
        this.buttonList.add(new GuiButton(1, startX + buttonWidth + gap, buttonY, buttonWidth, buttonHeight, DECLINE_TEXT));

        // 预计算自动换行（手动实现，避免 ProjectE ManualFontRenderer 无限递归 bug）
        wrappedLines.clear();
        int maxWidth = this.width - 60;
        for (String line : NOTICE_LINES) {
            if (line.isEmpty()) {
                wrappedLines.add("");
            } else {
                wrapLine(line, maxWidth);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            onAgree();
        } else if (button.id == 1) {
            onDecline();
        }
    }

    private void onAgree() {
        // 写回 JSON，设置 user_agreement = true
        try {
            modpackJson.addProperty("user_agreement", true);
            BbsmcCnInit.writeJsonToFile(configFile, modpackJson);
            BbsmcCnInit.LOGGER.info("User agreed to localization notice, user_agreement set to true");
        } catch (Exception e) {
            BbsmcCnInit.LOGGER.error("Failed to write modpack_info.json", e);
        }

        BbsmcCnInit.markAgreed();
        BbsmcCnInit.setupLanguageAndPacks(this.mc, languagePacks);
        this.mc.displayGuiScreen(new GuiMainMenu());
    }

    private void onDecline() {
        BbsmcCnInit.LOGGER.info("User declined localization notice, shutting down");
        this.mc.shutdown();
    }

    private void wrapLine(String text, int maxWidth) {
        StringBuilder current = new StringBuilder();
        StringBuilder formatting = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            if (text.charAt(i) == '\u00a7' && i + 1 < text.length()) {
                char code = text.charAt(i + 1);
                if (code == 'r' || code == 'R') {
                    formatting.setLength(0);
                } else {
                    formatting.append('\u00a7').append(code);
                }
                current.append('\u00a7').append(code);
                i += 2;
            } else {
                current.append(text.charAt(i));
                if (this.fontRenderer.getStringWidth(current.toString()) > maxWidth) {
                    current.deleteCharAt(current.length() - 1);
                    wrappedLines.add(current.toString());
                    current = new StringBuilder(formatting.toString());
                    current.append(text.charAt(i));
                }
                i++;
            }
        }
        if (current.length() > 0) {
            wrappedLines.add(current.toString());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        // 标题
        this.drawCenteredString(this.fontRenderer, TITLE_TEXT, this.width / 2, 15, 0xFFFFFF);

        // 须知文案
        int textX = 30;
        int textY = 40;
        int lineHeight = 11;

        for (String line : wrappedLines) {
            if (!line.isEmpty()) {
                this.fontRenderer.drawStringWithShadow(line, textX, textY, 0xDDDDDD);
            }
            textY += lineHeight;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) { // ESC
            onDecline();
            return;
        }
        // 不调用 super，阻止其他方式关闭界面
    }
}

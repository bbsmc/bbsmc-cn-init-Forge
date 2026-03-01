package moe.ytonidc.ytongame_hostingmenu.client;

import com.google.gson.JsonObject;
import moe.ytonidc.ytongame_hostingmenu.Ytongame_hostingmenu;
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
        "\u00a7fBBSMC汉化包通过AI翻译+人工精校生成，汉化内容势必存在少量问题，如果您发现了任何汉化质量问题，您可以前往我们的网站：\u00a7n\u00a7bhttps://bbsmc.net\u00a7r\u00a7f，加入我们的玩家QQ群进行反馈，我们会及时处理您的反馈，并重新发布修改后的汉化包。",
        "\u00a7f若您当前游玩的整合包已经有完整的人工翻译，我们也相当欢迎您使用更精准更优质的人工翻译。",
        "",
        "\u00a76\u00a7l二、广告内容",
        "\u00a7fBBSMC汉化包含有仅出现在服务器多人列表和创建世界导航标签页面的服务器广告，不会对游戏体验造成影响。我们需要一定的收入来支撑汉化服务器的运转。\u00a7c\u00a7l如果觉得广告影响游戏体验，请先点击\"拒绝并退出\"关闭游戏，然后手动删除mods文件夹内的YTGame-HostingMenu.jar文件，再重新启动游戏即可。",
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

        // 预计算自动换行
        wrappedLines.clear();
        int maxWidth = this.width - 60;
        for (String line : NOTICE_LINES) {
            if (line.isEmpty()) {
                wrappedLines.add("");
            } else {
                wrappedLines.addAll(this.fontRenderer.listFormattedStringToWidth(line, maxWidth));
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
            Ytongame_hostingmenu.writeJsonToFile(configFile, modpackJson);
            Ytongame_hostingmenu.LOGGER.info("User agreed to localization notice, user_agreement set to true");
        } catch (Exception e) {
            Ytongame_hostingmenu.LOGGER.error("Failed to write modpack_info.json", e);
        }

        Ytongame_hostingmenu.markAgreed();
        Ytongame_hostingmenu.setupLanguageAndPacks(this.mc, languagePacks);
        this.mc.displayGuiScreen(new GuiMainMenu());
    }

    private void onDecline() {
        Ytongame_hostingmenu.LOGGER.info("User declined localization notice, shutting down");
        this.mc.shutdown();
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

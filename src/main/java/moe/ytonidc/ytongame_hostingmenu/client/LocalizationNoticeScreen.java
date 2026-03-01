package moe.ytonidc.ytongame_hostingmenu.client;

import com.google.gson.JsonObject;
import moe.ytonidc.ytongame_hostingmenu.Ytongame_hostingmenu;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocalizationNoticeScreen extends GuiScreen {

    private static final String TITLE_KEY = "ytongame_hostingmenu.notice.title";
    private static final String[] NOTICE_KEYS = {
        "ytongame_hostingmenu.notice.intro",
        "",
        "ytongame_hostingmenu.notice.section1_title",
        "ytongame_hostingmenu.notice.section1_line1",
        "ytongame_hostingmenu.notice.section1_line2",
        "",
        "ytongame_hostingmenu.notice.section2_title",
        "ytongame_hostingmenu.notice.section2_line1",
        ""
    };

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

        this.buttonList.add(new GuiButton(0, startX, buttonY, buttonWidth, buttonHeight,
                I18n.format("ytongame_hostingmenu.notice.agree")));
        this.buttonList.add(new GuiButton(1, startX + buttonWidth + gap, buttonY, buttonWidth, buttonHeight,
                I18n.format("ytongame_hostingmenu.notice.decline")));

        // 预计算自动换行
        wrappedLines.clear();
        int maxWidth = this.width - 60;
        for (String key : NOTICE_KEYS) {
            if (key.isEmpty()) {
                wrappedLines.add("");
            } else {
                String line = I18n.format(key);
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
        this.drawCenteredString(this.fontRenderer, I18n.format(TITLE_KEY), this.width / 2, 15, 0xFFFFFF);

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

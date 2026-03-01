package moe.ytonidc.ytongame_hostingmenu.client;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.ytonidc.ytongame_hostingmenu.Ytongame_hostingmenu;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalizationNoticeScreen extends Screen {

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

    private final List<IReorderingProcessor> wrappedLines = new ArrayList<>();

    public LocalizationNoticeScreen(JsonObject modpackJson, List<String> languagePacks, File configFile) {
        super(new StringTextComponent(I18n.get(TITLE_KEY)));
        this.modpackJson = modpackJson;
        this.languagePacks = languagePacks;
        this.configFile = configFile;
    }

    @Override
    protected void init() {
        super.init();

        int buttonWidth = 120;
        int buttonHeight = 20;
        int gap = 10;
        int totalWidth = buttonWidth * 2 + gap;
        int startX = (this.width - totalWidth) / 2;
        int buttonY = this.height - 40;

        this.addButton(new Button(startX, buttonY, buttonWidth, buttonHeight,
                new StringTextComponent(I18n.get("ytongame_hostingmenu.notice.agree")),
                btn -> onAgree()));
        this.addButton(new Button(startX + buttonWidth + gap, buttonY, buttonWidth, buttonHeight,
                new StringTextComponent(I18n.get("ytongame_hostingmenu.notice.decline")),
                btn -> onDecline()));

        // 预计算自动换行
        wrappedLines.clear();
        int maxWidth = this.width - 60;
        for (String key : NOTICE_KEYS) {
            if (key.isEmpty()) {
                wrappedLines.add(IReorderingProcessor.EMPTY);
            } else {
                String line = I18n.get(key);
                wrappedLines.addAll(this.font.split(ITextProperties.of(line), maxWidth));
            }
        }
    }

    private void onAgree() {
        try {
            modpackJson.addProperty("user_agreement", true);
            Ytongame_hostingmenu.writeJsonToFile(configFile, modpackJson);
            Ytongame_hostingmenu.LOGGER.info("User agreed to localization notice, user_agreement set to true");
        } catch (Exception e) {
            Ytongame_hostingmenu.LOGGER.error("Failed to write modpack_info.json", e);
        }

        Ytongame_hostingmenu.setupLanguageAndPacks(this.minecraft, languagePacks);
        this.minecraft.setScreen(null);
    }

    private void onDecline() {
        Ytongame_hostingmenu.LOGGER.info("User declined localization notice, shutting down");
        this.minecraft.stop();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        // 标题
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 15, 0xFFFFFF);

        // 须知文案
        int textX = 30;
        int textY = 40;
        int lineHeight = 11;

        for (IReorderingProcessor line : wrappedLines) {
            if (line != IReorderingProcessor.EMPTY) {
                this.font.drawShadow(matrixStack, line, textX, textY, 0xDDDDDD);
            }
            textY += lineHeight;
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC
            onDecline();
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

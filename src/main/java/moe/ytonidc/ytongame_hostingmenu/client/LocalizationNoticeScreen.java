package moe.ytonidc.ytongame_hostingmenu.client;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.ytonidc.ytongame_hostingmenu.Ytongame_hostingmenu;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalizationNoticeScreen extends Screen {

    private static final String TITLE_TEXT = "\u00a7l\u00a7eBBSMC汉化包使用须知";
    private static final String[] NOTICE_LINES = {
        "\u00a7f感谢您选择BBSMC汉化包，在正式使用BBSMC汉化包进行游戏前，我们希望您能阅读以下内容。",
        "",
        "\u00a76\u00a7l一、汉化内容",
        "\u00a7fBBSMC汉化包通过AI翻译+人工精校生成，汉化内容势必存在少量问题，如果您发现了任何汉化质量问题，您可以前往我们的网站：\u00a7n\u00a7bhttps://bbsmc.net\u00a7r\u00a7f，加入我们的玩家QQ群进行反馈，我们会及时处理您的反馈，并重新发布修改后的汉化包。",
        "\u00a7f若您当前游玩的整合包已经有完整的人工翻译，我们也相当欢迎您使用更精准更优质的人工翻译。",
        "",
        "\u00a76\u00a7l二、广告内容",
        "\u00a7fBBSMC汉化包含有仅出现在服务器多人列表和创建世界导航标签页面的服务器广告，不会对游戏体验造成影响。我们需要一定的收入来支撑汉化服务器的运转。\u00a7c\u00a7l如果觉得广告影响游戏体验，请先点击关闭游戏，然后删除YTGame-HostingMenu模组，再重新启动游戏即可。",
        ""
    };
    private static final String AGREE_TEXT = "同意并继续";
    private static final String DECLINE_TEXT = "拒绝并退出";

    private final JsonObject modpackJson;
    private final List<String> languagePacks;
    private final File configFile;

    private final List<IReorderingProcessor> wrappedLines = new ArrayList<>();

    public LocalizationNoticeScreen(JsonObject modpackJson, List<String> languagePacks, File configFile) {
        super(new StringTextComponent(TITLE_TEXT));
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
                new StringTextComponent(AGREE_TEXT),
                btn -> onAgree()));
        this.addButton(new Button(startX + buttonWidth + gap, buttonY, buttonWidth, buttonHeight,
                new StringTextComponent(DECLINE_TEXT),
                btn -> onDecline()));

        // 预计算自动换行
        wrappedLines.clear();
        int maxWidth = this.width - 60;
        for (String line : NOTICE_LINES) {
            if (line.isEmpty()) {
                wrappedLines.add(IReorderingProcessor.EMPTY);
            } else {
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

        Ytongame_hostingmenu.markAgreed();
        Ytongame_hostingmenu.setupLanguageAndPacks(this.minecraft, languagePacks);
        this.minecraft.setScreen(new MainMenuScreen());
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

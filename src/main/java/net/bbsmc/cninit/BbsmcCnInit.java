package net.bbsmc.cninit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.bbsmc.cninit.client.LocalizationNoticeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mod(BbsmcCnInit.MODID)
public class BbsmcCnInit {
    public static final String MODID = "bbsmc_cn_init";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON = new Gson();
    public static final Gson GSON_PRETTY = new GsonBuilder().setPrettyPrinting().create();

    private static boolean configLoaded = false;
    private static boolean userAgreement = false;
    private static List<String> languagePacks = new ArrayList<>();
    private static JsonObject modpackJson = null;
    private static File configFile = null;

    public BbsmcCnInit() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }

    public static void markAgreed() {
        userAgreement = true;
    }

    public static Screen interceptScreen() {
        if (!configLoaded || userAgreement) {
            return null;
        }
        return new LocalizationNoticeScreen(modpackJson, languagePacks, configFile);
    }

    private static void loadConfig() {
        if (configLoaded) return;

        Minecraft mc = Minecraft.getInstance();
        configFile = new File(mc.gameDirectory, "config/modpack_info.json");
        if (!configFile.exists()) {
            LOGGER.debug("modpack_info.json not found, skipping auto setup");
            configLoaded = true;
            userAgreement = true;
            return;
        }

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
            modpackJson = GSON.fromJson(reader, JsonObject.class);

            if (modpackJson.has("user_agreement")) {
                userAgreement = modpackJson.get("user_agreement").getAsBoolean();
            }

            JsonArray packsArray = modpackJson.getAsJsonArray("language_packs");
            if (packsArray != null) {
                for (int i = 0; i < packsArray.size(); i++) {
                    languagePacks.add(packsArray.get(i).getAsString());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to read modpack_info.json", e);
            userAgreement = true;
        }

        configLoaded = true;
    }

    public static void writeJsonToFile(File file, JsonObject json) throws Exception {
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
            GSON_PRETTY.toJson(json, writer);
        }
    }

    public static void setupLanguageAndPacks(Minecraft mc, List<String> languagePacks) {
        String currentLang = mc.getLanguageManager().getSelected().getCode();
        String targetLang = "zh_cn";
        boolean languageChanged = false;
        if (!targetLang.equals(currentLang)) {
            LOGGER.info("Current language is '{}', switching to zh_cn", currentLang);
            mc.getLanguageManager().getLanguages().stream()
                .filter(lang -> targetLang.equals(lang.getCode()))
                .findFirst()
                .ifPresent(lang -> {
                    mc.getLanguageManager().setSelected(lang);
                    mc.options.languageCode = targetLang;
                    mc.options.save();
                    LOGGER.info("Language set to '{}'", targetLang);
                });
            languageChanged = true;
        }

        boolean packsChanged = false;
        if (!languagePacks.isEmpty()) {
            File resourcePacksDir = new File(mc.gameDirectory, "resourcepacks");
            ResourcePackList rpList = mc.getResourcePackRepository();
            rpList.reload();

            List<String> packsToEnable = new ArrayList<>();
            for (String packName : languagePacks) {
                File packFile = new File(resourcePacksDir, packName);
                if (packFile.exists()) {
                    packsToEnable.add("file/" + packName);
                } else {
                    LOGGER.warn("Resource pack not found: {}", packName);
                }
            }

            Collection<String> selected = new ArrayList<>(rpList.getSelectedIds());
            for (String packId : packsToEnable) {
                ResourcePackInfo packInfo = rpList.getPack(packId);
                if (packInfo != null && !selected.contains(packId)) {
                    selected.add(packId);
                    packsChanged = true;
                    LOGGER.info("Auto-enabled resource pack: {}", packId);
                }
            }

            if (packsChanged) {
                rpList.setSelected(selected);
            }
        }

        if (languageChanged || packsChanged) {
            mc.reloadResourcePacks();
        }
    }

    public static class ClientEventHandler {
        private boolean setupDone = false;

        @SubscribeEvent
        public void onGuiOpen(GuiOpenEvent event) {
            Screen screen = event.getGui();
            if (screen instanceof WorldSelectionScreen || screen instanceof MultiplayerScreen) {
                loadConfig();
                Screen redirect = interceptScreen();
                if (redirect != null) {
                    event.setGui(redirect);
                }
            }
        }

        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END || setupDone) {
                return;
            }

            Minecraft mc = Minecraft.getInstance();
            if (mc.screen == null && mc.level == null) {
                return;
            }

            setupDone = true;
            loadConfig();

            if (userAgreement && configLoaded) {
                setupLanguageAndPacks(mc, languagePacks);
            }
        }
    }
}

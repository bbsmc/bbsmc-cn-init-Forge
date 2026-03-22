package net.bbsmc.cninit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.bbsmc.cninit.client.LocalizationNoticeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = BbsmcCnInit.MODID, name = BbsmcCnInit.NAME, version = BbsmcCnInit.VERSION, clientSideOnly = true)
public class BbsmcCnInit {
    public static final String MODID = "bbsmc_cn_init";
    public static final String NAME = "BBSMC-CN-Init";
    public static final String VERSION = "1.0.9";

    public static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON = new Gson();
    public static final Gson GSON_PRETTY = new GsonBuilder().setPrettyPrinting().create();

    private static boolean configLoaded = false;
    private static boolean userAgreement = false;
    private static List<String> languagePacks = new ArrayList<>();
    private static JsonObject modpackJson = null;
    private static File configFile = null;

    @Mod.Instance(MODID)
    public static BbsmcCnInit instance;

    /**
     * FMLConstructionEvent — 最早的阶段，在资源加载之前执行。
     * 和 i18n 一样，直接设置 gameSettings.language 字段，
     * MC 后续初始化时自然会用 zh_cn 加载资源，无需 refreshResources()。
     */
    @Mod.EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        // 在资源加载前设置语言和资源包（和 i18n 一样）
        setupEarly();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * 早期设置：在资源加载前直接修改 gameSettings，无需 refreshResources()。
     * MC 后续初始化时自然会用这些设置加载资源。
     */
    private static void setupEarly() {
        Minecraft mc = Minecraft.getMinecraft();

        // 1. 设置语言为中文
        if (!"zh_cn".equals(mc.gameSettings.language)) {
            mc.gameSettings.language = "zh_cn";
            LOGGER.info("Language pre-set to zh_cn");
        }

        // 2. 读取 modpack_info.json 并添加资源包
        try {
            File configFile = new File(mc.mcDataDir, "config/modpack_info.json");
            if (configFile.exists()) {
                try (InputStreamReader reader = new InputStreamReader(
                        new FileInputStream(configFile), StandardCharsets.UTF_8)) {
                    JsonObject config = GSON.fromJson(reader, JsonObject.class);
                    JsonArray packsArray = config.getAsJsonArray("language_packs");
                    if (packsArray != null) {
                        for (int i = 0; i < packsArray.size(); i++) {
                            String packName = packsArray.get(i).getAsString();
                            String packId = "file/" + packName;
                            if (!mc.gameSettings.resourcePacks.contains(packId)) {
                                File rpFile = new File(mc.mcDataDir, "resourcepacks/" + packName);
                                if (rpFile.exists()) {
                                    mc.gameSettings.resourcePacks.add(packId);
                                    LOGGER.info("Resource pack pre-added: {}", packId);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to pre-add resource packs: {}", e.getMessage());
        }
    }

    public static void markAgreed() {
        userAgreement = true;
    }

    public static GuiScreen interceptScreen() {
        if (!configLoaded || userAgreement) {
            return null;
        }
        return new LocalizationNoticeScreen(modpackJson, languagePacks, configFile);
    }

    private static void loadConfig() {
        if (configLoaded) return;

        Minecraft mc = Minecraft.getMinecraft();
        configFile = new File(mc.mcDataDir, "config/modpack_info.json");
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

    /**
     * 完整的语言和资源包设置（用于 LocalizationNoticeScreen 点击同意后）
     */
    public static void setupLanguageAndPacks(Minecraft mc, List<String> languagePacks) {
        String currentLang = mc.getLanguageManager().getCurrentLanguage().getLanguageCode();
        String targetLang = "zh_cn";
        boolean languageChanged = false;
        if (!targetLang.equals(currentLang)) {
            LOGGER.info("Current language is '{}', switching to zh_cn", currentLang);
            mc.getLanguageManager().getLanguages().stream()
                .filter(lang -> targetLang.equals(lang.getLanguageCode()))
                .findFirst()
                .ifPresent(lang -> {
                    mc.getLanguageManager().setCurrentLanguage(lang);
                    mc.gameSettings.language = targetLang;
                    mc.gameSettings.saveOptions();
                    LOGGER.info("Language set to '{}'", targetLang);
                });
            languageChanged = true;
        }

        boolean packsChanged = false;
        if (!languagePacks.isEmpty()) {
            File resourcePacksDir = new File(mc.mcDataDir, "resourcepacks");
            ResourcePackRepository rpRepo = mc.getResourcePackRepository();
            rpRepo.updateRepositoryEntriesAll();

            List<ResourcePackRepository.Entry> currentSelected = new ArrayList<>(rpRepo.getRepositoryEntries());

            for (String packName : languagePacks) {
                File packFile = new File(resourcePacksDir, packName);
                if (!packFile.exists()) {
                    LOGGER.warn("Resource pack not found: {}", packName);
                    continue;
                }

                for (ResourcePackRepository.Entry entry : rpRepo.getRepositoryEntriesAll()) {
                    String entryName = entry.getResourcePackName();
                    if (entryName.equals(packName) || entryName.equals("file/" + packName)) {
                        if (!currentSelected.contains(entry)) {
                            currentSelected.add(entry);
                            packsChanged = true;
                            LOGGER.info("Auto-enabled resource pack: {}", packName);
                        }
                        break;
                    }
                }
            }

            if (packsChanged) {
                List<String> packNames = new ArrayList<>();
                for (ResourcePackRepository.Entry entry : currentSelected) {
                    packNames.add(entry.getResourcePackName());
                }
                mc.gameSettings.resourcePacks.clear();
                mc.gameSettings.resourcePacks.addAll(packNames);
                mc.gameSettings.saveOptions();

                rpRepo.setRepositories(currentSelected);
            }
        }

        if (languageChanged || packsChanged) {
            mc.refreshResources();
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        GuiScreen screen = event.getGui();
        if (screen instanceof GuiWorldSelection || screen instanceof GuiMultiplayer) {
            loadConfig();
            GuiScreen redirect = interceptScreen();
            if (redirect != null) {
                event.setGui(redirect);
            }
        }
    }

    private boolean setupDone = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || setupDone) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null && mc.world == null) {
            return;
        }

        setupDone = true;
        loadConfig();
    }
}

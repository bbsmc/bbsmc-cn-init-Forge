package moe.ytonidc.ytongame_hostingmenu;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import moe.ytonidc.ytongame_hostingmenu.client.HostingPackage;
import moe.ytonidc.ytongame_hostingmenu.client.RegionDetector;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mod(Ytongame_hostingmenu.MODID)
@Mod.EventBusSubscriber(modid = Ytongame_hostingmenu.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Ytongame_hostingmenu {
    public static final String MODID = "ytongame_hostingmenu";
    public static final ResourceLocation hostingLogo = new ResourceLocation(MODID, "textures/gui/logo_ytongame.png");
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Gson GSON = new Gson();

    private static boolean languageSetupDone = false;

    public Ytongame_hostingmenu() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);

        // 异步加载套餐数据（优先远程，失败则本地）
        HostingPackage.loadAsync();
        
        // 注册Forge事件总线
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            PackRepository packRepository = mc.getResourcePackRepository();

            File configFile = new File(mc.gameDirectory, "config/modpack_info.json");
            if (!configFile.exists()) {
                LOGGER.debug("modpack_info.json not found, skipping auto setup");
                return;
            }

            List<String> languagePacks = new ArrayList<>();
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);

                JsonObject resourcePackInstall = json.getAsJsonObject("resource_pack_install");
                if (resourcePackInstall != null && resourcePackInstall.has("auto_install_enabled")) {
                    boolean autoInstallEnabled = resourcePackInstall.get("auto_install_enabled").getAsBoolean();
                    if (autoInstallEnabled) {
                        LOGGER.debug("Auto install already enabled by other means, skipping");
                        return;
                    }
                }

                JsonArray packsArray = json.getAsJsonArray("language_packs");
                if (packsArray != null) {
                    for (int i = 0; i < packsArray.size(); i++) {
                        languagePacks.add(packsArray.get(i).getAsString());
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Failed to read modpack_info.json", e);
                return;
            }

            if (languagePacks.isEmpty()) {
                return;
            }

            File resourcePacksDir = new File(mc.gameDirectory, "resourcepacks");
            List<String> packsToEnable = new ArrayList<>();
            for (String packName : languagePacks) {
                File packFile = new File(resourcePacksDir, packName);
                if (packFile.exists()) {
                    packsToEnable.add("file/" + packName);
                } else {
                    LOGGER.warn("Resource pack not found: {}", packName);
                }
            }

            if (packsToEnable.isEmpty()) {
                return;
            }

            packRepository.reload();

            Collection<String> selected = new ArrayList<>(packRepository.getSelectedIds());
            boolean changed = false;
            for (String packId : packsToEnable) {
                Pack pack = packRepository.getPack(packId);
                if (pack != null && !selected.contains(packId)) {
                    selected.add(packId);
                    changed = true;
                    LOGGER.info("Auto-enabled resource pack: {}", packId);
                }
            }

            if (changed) {
                packRepository.setSelected(selected);
                mc.reloadResourcePacks();
            }
        });
    }

    // 客户端事件处理器 - 在游戏完全加载后设置语言
    public static class ClientEventHandler {
        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END || languageSetupDone) {
                return;
            }

            Minecraft mc = Minecraft.getInstance();
            // 等待游戏完全加载（主菜单出现）
            if (mc.screen == null && mc.level == null) {
                return;
            }

            languageSetupDone = true;

            File configFile = new File(mc.gameDirectory, "config/modpack_info.json");
            if (!configFile.exists()) {
                return;
            }

            // 检查并设置语言为简体中文
            String currentLang = mc.getLanguageManager().getSelected().getCode();
            String targetLang = "zh_cn";
            if (!targetLang.equals(currentLang)) {
                LOGGER.info("Current language is '{}', switching to zh_cn", currentLang);
                mc.getLanguageManager().getLanguages().stream()
                    .filter(lang -> targetLang.equals(lang.getCode()))
                    .findFirst()
                    .ifPresent(lang -> {
                        mc.getLanguageManager().setSelected(lang);
                        mc.options.languageCode = targetLang;
                        mc.options.save();
                        LOGGER.info("Language set to '{}', reloading resources", targetLang);
                        RegionDetector.refreshLanguage(targetLang);
                        mc.reloadResourcePacks();
                    });
            }
        }
    }
}

package moe.ytonidc.ytongame_hostingmenu;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import moe.ytonidc.ytongame_hostingmenu.client.HostingPackage;
import moe.ytonidc.ytongame_hostingmenu.client.RegionDetector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = Ytongame_hostingmenu.MODID, name = Ytongame_hostingmenu.NAME, version = Ytongame_hostingmenu.VERSION, clientSideOnly = true)
public class Ytongame_hostingmenu {
    public static final String MODID = "ytongame_hostingmenu";
    public static final String NAME = "YtonGame-HostingMenu";
    public static final String VERSION = "1.0.7";
    
    public static final ResourceLocation hostingLogo = new ResourceLocation(MODID, "textures/gui/logo_ytongame.png");
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON = new Gson();

    private static boolean setupDone = false;

    @Mod.Instance(MODID)
    public static Ytongame_hostingmenu instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // 异步加载套餐数据（优先远程，失败则本地）
        HostingPackage.loadAsync();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    // 在游戏完全加载后设置语言和资源包
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || setupDone) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        // 等待游戏完全加载（主菜单出现）
        if (mc.currentScreen == null && mc.world == null) {
            return;
        }

        setupDone = true;

        File configFile = new File(mc.mcDataDir, "config/modpack_info.json");
        if (!configFile.exists()) {
            LOGGER.debug("modpack_info.json not found, skipping auto setup");
            return;
        }

        List<String> languagePacks = new ArrayList<>();
        boolean autoInstallEnabled = false;
        
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            
            JsonObject resourcePackInstall = json.getAsJsonObject("resource_pack_install");
            if (resourcePackInstall != null && resourcePackInstall.has("auto_install_enabled")) {
                autoInstallEnabled = resourcePackInstall.get("auto_install_enabled").getAsBoolean();
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

        // 检查并设置语言为简体中文
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
                    RegionDetector.refreshLanguage(targetLang);
                });
            languageChanged = true;
        }

        // 如果已经通过其他方式启用了自动安装，跳过资源包设置
        if (autoInstallEnabled) {
            LOGGER.debug("Auto install already enabled by other means, skipping resource pack setup");
            if (languageChanged) {
                mc.refreshResources();
            }
            return;
        }

        if (languagePacks.isEmpty()) {
            if (languageChanged) {
                mc.refreshResources();
            }
            return;
        }

        // 启用资源包
        File resourcePacksDir = new File(mc.mcDataDir, "resourcepacks");
        ResourcePackRepository rpRepo = mc.getResourcePackRepository();
        rpRepo.updateRepositoryEntriesAll();
        
        List<ResourcePackRepository.Entry> currentSelected = new ArrayList<>(rpRepo.getRepositoryEntries());
        boolean packsChanged = false;
        
        for (String packName : languagePacks) {
            File packFile = new File(resourcePacksDir, packName);
            if (!packFile.exists()) {
                LOGGER.warn("Resource pack not found: {}", packName);
                continue;
            }
            
            // 查找对应的资源包条目
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
            // 更新选中的资源包列表
            List<String> packNames = new ArrayList<>();
            for (ResourcePackRepository.Entry entry : currentSelected) {
                packNames.add(entry.getResourcePackName());
            }
            mc.gameSettings.resourcePacks.clear();
            mc.gameSettings.resourcePacks.addAll(packNames);
            mc.gameSettings.saveOptions();
            
            rpRepo.setRepositories(currentSelected);
        }

        if (languageChanged || packsChanged) {
            mc.refreshResources();
        }
    }
}

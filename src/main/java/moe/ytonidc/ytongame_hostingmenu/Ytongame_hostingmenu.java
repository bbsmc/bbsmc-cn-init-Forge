package moe.ytonidc.ytongame_hostingmenu;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import moe.ytonidc.ytongame_hostingmenu.client.HostingPackage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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
    public static final String VERSION = "1.0.6";
    
    public static final ResourceLocation hostingLogo = new ResourceLocation(MODID, "textures/gui/logo_ytongame.png");
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON = new Gson();

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
        
        Minecraft mc = Minecraft.getMinecraft();
        
        File configFile = new File(mc.mcDataDir, "config/modpack_info.json");
        if (!configFile.exists()) {
            LOGGER.debug("modpack_info.json not found, skipping auto setup");
            return;
        }

        // 检查并设置语言为简体中文
        String currentLang = mc.getLanguageManager().getCurrentLanguage().getLanguageCode();
        String targetLang = "zh_cn";
        if (!targetLang.equals(currentLang)) {
            LOGGER.info("Current language is '{}', switching to zh_cn", currentLang);
            mc.getLanguageManager().getLanguages().stream()
                .filter(lang -> targetLang.equals(lang.getLanguageCode()))
                .findFirst()
                .ifPresent(lang -> {
                    mc.getLanguageManager().setCurrentLanguage(lang);
                    mc.gameSettings.language = targetLang;
                    mc.gameSettings.saveOptions();
                    LOGGER.info("Saving language '{}' to options", targetLang);
                });
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

        File resourcePacksDir = new File(mc.mcDataDir, "resourcepacks");
        List<String> packsToEnable = new ArrayList<>();
        for (String packName : languagePacks) {
            File packFile = new File(resourcePacksDir, packName);
            if (packFile.exists()) {
                packsToEnable.add(packName);
            } else {
                LOGGER.warn("Resource pack not found: {}", packName);
            }
        }

        if (!packsToEnable.isEmpty()) {
            LOGGER.info("Found {} resource packs to potentially enable", packsToEnable.size());
        }
    }
}

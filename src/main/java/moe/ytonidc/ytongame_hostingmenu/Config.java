package moe.ytonidc.ytongame_hostingmenu;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@net.minecraftforge.common.config.Config(modid = Ytongame_hostingmenu.MODID)
public class Config {
    
    @Comment("购买页面链接")
    public static String purchaseUrl = "https://bbsmc.net/server?aff=LaotouY";

    @Comment("是否启用广告功能（Hosting 标签页、多人游戏广告等）")
    public static boolean enableAds = true;

    @Comment("是否仅对简体中文用户显示广告")
    public static boolean chineseOnly = true;

    public static String getPurchaseUrl() {
        return purchaseUrl;
    }

    public static boolean isAdsEnabled() {
        return enableAds;
    }

    public static boolean isChineseOnly() {
        return chineseOnly;
    }

    @Mod.EventBusSubscriber(modid = Ytongame_hostingmenu.MODID)
    public static class ConfigEventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Ytongame_hostingmenu.MODID)) {
                ConfigManager.sync(Ytongame_hostingmenu.MODID, net.minecraftforge.common.config.Config.Type.INSTANCE);
            }
        }
    }
}

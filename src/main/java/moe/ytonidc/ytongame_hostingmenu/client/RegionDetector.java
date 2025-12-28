package moe.ytonidc.ytongame_hostingmenu.client;

import moe.ytonidc.ytongame_hostingmenu.Config;
import moe.ytonidc.ytongame_hostingmenu.Ytongame_hostingmenu;
import net.minecraft.client.Minecraft;

/**
 * 地区检测工具类
 * 通过检测用户本地语言设置判断是否为中国大陆用户
 */
public class RegionDetector {

    private static Boolean isMainlandChina = null;

    /**
     * 检测用户语言设置
     */
    public static void detect() {
        try {
            // 获取 Minecraft 语言设置
            String mcLanguage = Minecraft.getInstance().options.languageCode;

            // 简体中文语言代码
            isMainlandChina = "zh_cn".equalsIgnoreCase(mcLanguage);

            Ytongame_hostingmenu.LOGGER.info("Language detection: {} -> isMainlandChina = {}", mcLanguage, isMainlandChina);
        } catch (Exception e) {
            // Minecraft 实例可能尚未初始化，使用系统语言作为备选
            String systemLanguage = System.getProperty("user.language", "");
            String systemCountry = System.getProperty("user.country", "");

            isMainlandChina = "zh".equalsIgnoreCase(systemLanguage) && "CN".equalsIgnoreCase(systemCountry);

            Ytongame_hostingmenu.LOGGER.info("System language detection: {}_{} -> isMainlandChina = {}",
                    systemLanguage, systemCountry, isMainlandChina);
        }
    }

    /**
     * 判断是否应该显示广告
     * 需要同时满足：配置启用 + 语言为简体中文
     */
    public static boolean shouldShowAds() {
        // 检查配置是否启用广告
        if (!Config.isAdsEnabled()) {
            return false;
        }

        // 延迟检测，确保 Minecraft 实例已初始化
        if (isMainlandChina == null) {
            detect();
        }
        return Boolean.TRUE.equals(isMainlandChina);
    }
}

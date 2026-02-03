package moe.ytonidc.ytongame_hostingmenu.client;

import moe.ytonidc.ytongame_hostingmenu.Config;
import net.minecraft.client.Minecraft;

/**
 * 地区检测工具类
 * 通过检测用户本地语言设置判断是否为中国大陆用户
 */
public class RegionDetector {

    // 缓存的语言代码，用于在语言切换后保持同步
    private static String cachedLanguage = null;

    /**
     * 刷新缓存的语言代码
     * 应在语言切换后调用此方法
     */
    public static void refreshLanguage(String newLanguage) {
        cachedLanguage = newLanguage;
    }

    /**
     * 判断是否应该显示广告
     * 需要满足：配置启用广告，且（不限制语言 或 语言为简体中文）
     */
    public static boolean shouldShowAds() {
        // 检查配置是否启用广告
        if (!Config.isAdsEnabled()) {
            return false;
        }

        // 如果不限制仅中文用户，直接返回 true
        if (!Config.isChineseOnly()) {
            return true;
        }

        // 优先使用缓存的语言代码
        if (cachedLanguage != null) {
            return "zh_cn".equalsIgnoreCase(cachedLanguage);
        }

        // 实时检测当前语言设置
        try {
            String mcLanguage = Minecraft.getMinecraft().gameSettings.language;
            return "zh_cn".equalsIgnoreCase(mcLanguage);
        } catch (Exception e) {
            // Minecraft 实例可能尚未初始化，使用系统语言作为备选
            String systemLanguage = System.getProperty("user.language", "");
            String systemCountry = System.getProperty("user.country", "");
            return "zh".equalsIgnoreCase(systemLanguage) && "CN".equalsIgnoreCase(systemCountry);
        }
    }
}

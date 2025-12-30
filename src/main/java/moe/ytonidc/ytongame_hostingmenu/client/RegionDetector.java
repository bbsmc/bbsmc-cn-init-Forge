package moe.ytonidc.ytongame_hostingmenu.client;

import moe.ytonidc.ytongame_hostingmenu.Config;
import net.minecraft.client.Minecraft;

/**
 * 地区检测工具类
 * 通过检测用户本地语言设置判断是否为中国大陆用户
 */
public class RegionDetector {

    /**
     * 判断是否应该显示广告
     * 需要同时满足：配置启用 + 语言为简体中文
     */
    public static boolean shouldShowAds() {
        // 检查配置是否启用广告
        if (!Config.isAdsEnabled()) {
            return false;
        }

        // 每次都实时检测当前语言设置
        try {
            String mcLanguage = Minecraft.getInstance().options.languageCode;
            return "zh_cn".equalsIgnoreCase(mcLanguage);
        } catch (Exception e) {
            // Minecraft 实例可能尚未初始化，使用系统语言作为备选
            String systemLanguage = System.getProperty("user.language", "");
            String systemCountry = System.getProperty("user.country", "");
            return "zh".equalsIgnoreCase(systemLanguage) && "CN".equalsIgnoreCase(systemCountry);
        }
    }
}

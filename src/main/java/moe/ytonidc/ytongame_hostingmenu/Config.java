package moe.ytonidc.ytongame_hostingmenu;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Ytongame_hostingmenu.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.ConfigValue<String> PURCHASE_URL = BUILDER
            .comment("购买页面链接")
            .define("purchaseUrl", "https://example.com/buy");

    private static final ForgeConfigSpec.BooleanValue ENABLE_ADS = BUILDER
            .comment("是否启用广告功能（Hosting 标签页、多人游戏广告等）")
            .define("enableAds", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    private static String purchaseUrl;
    private static boolean enableAds;

    public static String getPurchaseUrl() {
        return purchaseUrl;
    }

    public static boolean isAdsEnabled() {
        return enableAds;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        purchaseUrl = PURCHASE_URL.get();
        enableAds = ENABLE_ADS.get();
    }
}

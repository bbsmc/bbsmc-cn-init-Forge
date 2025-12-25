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

    static final ForgeConfigSpec SPEC = BUILDER.build();

    private static String purchaseUrl;

    public static String getPurchaseUrl() {
        return purchaseUrl;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        purchaseUrl = PURCHASE_URL.get();
    }
}

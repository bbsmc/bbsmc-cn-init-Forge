package net.bbsmc.cninit;

import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@net.minecraftforge.common.config.Config(modid = BbsmcCnInit.MODID)
public class Config {

    @Mod.EventBusSubscriber(modid = BbsmcCnInit.MODID)
    public static class ConfigEventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(BbsmcCnInit.MODID)) {
                ConfigManager.sync(BbsmcCnInit.MODID, net.minecraftforge.common.config.Config.Type.INSTANCE);
            }
        }
    }
}

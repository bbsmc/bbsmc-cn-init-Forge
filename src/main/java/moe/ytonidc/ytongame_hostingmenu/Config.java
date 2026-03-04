package moe.ytonidc.ytongame_hostingmenu;

import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@net.minecraftforge.common.config.Config(modid = Ytongame_hostingmenu.MODID)
public class Config {

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

package chatemotes

import chatemotes.commands.ToggleCommand
import chatemotes.config.Config
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.minecraft.client.Minecraft
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import java.io.File

@Mod(
    modid = ChatEmotes.MOD_ID,
    name = ChatEmotes.MOD_NAME,
    version = ChatEmotes.MOD_VERSION,
    clientSideOnly = true
)
class ChatEmotes {
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        // this seems to be redundant
        val directory = File(event.modConfigurationDirectory, "chatemotes")
        if (!directory.exists()) {
            directory.mkdirs()
        }

    }

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {

        listOf(
            ToggleCommand()
        ).forEach {
            ClientCommandHandler.instance.registerCommand((it))
        }
    }

    @Mod.EventHandler
    fun postInit(event: FMLLoadCompleteEvent) = runBlocking {

        launch {
            // TODO load config

        }
    }


    companion object {
        const val MOD_ID = "chatemotes"
        const val MOD_NAME = "Chat Emotes"
        const val MOD_VERSION = "0.0.1"

        @JvmField
        val mc: Minecraft = Minecraft.getMinecraft()

        val config = Config(File(mc.mcDataDir, "config/chatemotes"))

    }
}

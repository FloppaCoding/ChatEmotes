package chatemotes.commands

import chatemotes.ChatEmotes
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender


class ToggleCommand : CommandBase() {
    override fun getCommandName(): String {
        return "chatemotes"
    }

    override fun getCommandAliases(): List<String> {
        return listOf(
            "emotes"
        )
    }

    override fun getCommandUsage(sender: ICommandSender): String {
        return "/$commandName"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        ChatEmotes.config.enabled = !ChatEmotes.config.enabled
        ChatEmotes.config.save()
    }
}

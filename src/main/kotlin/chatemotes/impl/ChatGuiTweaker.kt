package chatemotes.impl

import chatemotes.ChatEmotes.Companion.mc
import chatemotes.impl.EmoteHandler.getEmoteCompletions
import chatemotes.impl.EmoteHandler.isEmoteInfrontOfCursor
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiTextField
import net.minecraft.util.MathHelper
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.max

object ChatGuiTweaker {

    fun tweakChatGui(inputField: GuiTextField, foundNames: List<String>, autoCompleteIndex: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val matches = getEmoteCompletions(inputField.text, inputField.cursorPosition).asList().ifEmpty {
            if (isEmoteInfrontOfCursor(inputField.text, inputField.cursorPosition))
                foundNames
            else
                emptyList()
        }
        if (matches.isEmpty()) return

        var emoteIndex = 0
        var i: Int = inputField.cursorPosition
        // Chech whether right behind emote
        if (isEmoteInfrontOfCursor(inputField.text, inputField.cursorPosition)){
            i--
            emoteIndex = autoCompleteIndex
        }

        while (i > 0 && inputField.text[i - 1] != ":"[0]) {
            --i
        }

         val emoteStartIndex = (i - 1).coerceAtLeast(0)

        val position = mc.fontRendererObj.getStringWidth(inputField.text.substring(0,emoteStartIndex))

        val start = MathHelper.clamp_int( emoteIndex - 2, 0, max(matches.size -5,0))
        val end = MathHelper.clamp_int( start + 5, 1, matches.size)
        val subList = matches.subList(start, end).map { "${it.removeSurrounding(":")} $it" }
        val newIndex = MathHelper.clamp_int(emoteIndex - start - 1, 0 , subList.size-1)

        var offs = - (subList.size )* mc.fontRendererObj.FONT_HEIGHT - 2
        var width = 0
        for (match in subList) {
            width = max(width, mc.fontRendererObj.getStringWidth(match))
        }

        GL11.glPushMatrix()
        // Background behind the suggestions
        Gui.drawRect(position + inputField.xPosition, inputField.yPosition + offs,
            position + inputField.xPosition + width, inputField.yPosition - 2,
            Color(10,10,10,130).rgb
        )
        // Highlight selected
        Gui.drawRect(position + inputField.xPosition,
            inputField.yPosition + offs + mc.fontRendererObj.FONT_HEIGHT * newIndex,
            position + inputField.xPosition + width,
            inputField.yPosition + offs + mc.fontRendererObj.FONT_HEIGHT * (newIndex + 1),
            Color(227,12,24,190).rgb
        )

        for (match in subList) {
            mc.fontRendererObj.drawString(
                match,
                position + inputField.xPosition,
                inputField.yPosition + offs,
                0xffffff
            )
            offs += mc.fontRendererObj.FONT_HEIGHT
        }
        GL11.glPopMatrix()

    }
}
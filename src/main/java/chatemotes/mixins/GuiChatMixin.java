package chatemotes.mixins;

import chatemotes.impl.EmoteHandler;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = {GuiChat.class})
public abstract class GuiChatMixin {

    @Shadow protected GuiTextField inputField;

    @Shadow private List<String> foundPlayerNames;

    @Shadow private boolean playerNamesFound;

    @Inject(method = {"autocompletePlayerNames()V"}, at = {@At("HEAD")})
    public void customAutoCompleteRequest(CallbackInfo ci) {
        // Check whether the input field includes :, if not return because there in no emote to autocomplete.
        if(!this.inputField.getText().contains(":")) return;

        findEmoteCompletions();
    }

    /**
     * Looks whether any completions for emotes apply and if so sets the corresponding variables so that they can eb used.
     */
    private void findEmoteCompletions() {

        String[] matches = EmoteHandler.INSTANCE.getEmoteCompletions(this.inputField.getText(), this.inputField.getCursorPosition());

        if (matches.length == 0) return;

        this.playerNamesFound = false;
        this.foundPlayerNames.clear();

        for (String s : matches)
        {
            if (s.length() > 0)
            {
                this.foundPlayerNames.add(s);
            }
        }

        String s1 = this.inputField.getText().substring(lastEmoteStartBeforeCursor());
        String s2 = StringUtils.getCommonPrefix(matches);
//        s2 = net.minecraft.util.EnumChatFormatting.getTextWithoutFormattingCodes(s2);

        if (s2.length() > 0 && !s1.equalsIgnoreCase(s2))
        {
            this.inputField.deleteFromCursor(lastEmoteStartBeforeCursor() - this.inputField.getCursorPosition());
            this.inputField.writeText(s2);
        }
        // maybe not else?
        else if (this.foundPlayerNames.size() > 0)
        {
            this.playerNamesFound = true;

        }
    }

    /**
     * Adaptation from GuiTextField.func_146197_a
     */
    private int lastEmoteStartBeforeCursor()
    {
        int i = this.inputField.getCursorPosition();

        while (i > 0 && this.inputField.getText().charAt(i-1) != ":".charAt(0))
        {
            --i;
        }

        return i - 1;
    }
}

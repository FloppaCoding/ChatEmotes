package chatemotes.mixins;

import chatemotes.ChatEmotes;
import chatemotes.impl.ChatGuiTweaker;
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

    @Shadow private int autocompleteIndex;

    @Inject(method = {"autocompletePlayerNames()V"}, at = {@At("HEAD")}, cancellable = true)
    public void customAutoCompleteRequest(CallbackInfo ci) {
        if (!ChatEmotes.Companion.getConfig().getEnabled()) return;
        // Check whether the input field includes :, if not return because there in no emote to autocomplete.
        if(!this.inputField.getText().contains(":")) return;

        findEmoteCompletions();

        if (!foundPlayerNames.isEmpty() && EmoteHandler.INSTANCE.isOnlyEmotes(this.foundPlayerNames)) {
          doCustomAutoComplete();
          ci.cancel();
        }
    }

    @Inject(method = {"drawScreen(IIF)V"}, at = @At("RETURN"))
    public void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!ChatEmotes.Companion.getConfig().getEnabled()) return;
        ChatGuiTweaker.INSTANCE.tweakChatGui(inputField, this.foundPlayerNames, this.autocompleteIndex, mouseX, mouseY, partialTicks);
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
            //  possibly a different return or even function call when there are multiple completions.
            // but its redundant since playerNamesFound and foundPlayerNames both alr contain that info and can be used
            // to cover that situation in a different place.
        }
    }

    /**
     * Should be used to do the autocomplete action in case emotes completions were found.
     * The vanilla completion should be cancelled so that the autocompletion is not done twice.
     * Derived from autocompletePlayerNames
     */
    private void doCustomAutoComplete() {
        if (this.playerNamesFound)
        {
            this.inputField.deleteFromCursor(lastEmoteStartBeforeCursor() - this.inputField.getCursorPosition());

            if (this.autocompleteIndex >= this.foundPlayerNames.size())
            {
                this.autocompleteIndex = 0;
            }
        }
        else
        {
            int i = this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false);
            this.foundPlayerNames.clear();
            this.autocompleteIndex = 0;
            String s = this.inputField.getText().substring(i).toLowerCase();
            String s1 = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
            // this will not be needed
            //this.sendAutocompleteRequest(s1, s);

            // why is this here, it should always be true?!
            if (this.foundPlayerNames.isEmpty())
            {
                return;
            }

            this.playerNamesFound = true;
            this.inputField.deleteFromCursor(i - this.inputField.getCursorPosition());
        }

        // prevent the vanilla rendering of the possible tab completions.
/*        if (this.foundPlayerNames.size() > 1)
        {
            StringBuilder stringbuilder = new StringBuilder();

            for (String s2 : this.foundPlayerNames)
            {
                if (stringbuilder.length() > 0)
                {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(s2);
            }

            mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(stringbuilder.toString()), 1);
        }*/

        this.inputField.writeText(net.minecraft.util.EnumChatFormatting.getTextWithoutFormattingCodes(this.foundPlayerNames.get(this.autocompleteIndex++)));
    }

    /**
     * Adaptation from GuiTextField.func_146197_a
     */
    private int lastEmoteStartBeforeCursor()
    {
        int i = this.inputField.getCursorPosition();
        // first check whether right behind emote and if so adjust i
        if (i > 0 && this.inputField.getText().charAt(i-1) == ":".charAt(0)) i--;

        while (i > 0 && this.inputField.getText().charAt(i-1) != ":".charAt(0))
        {
            --i;
        }

        return i - 1;
    }
}

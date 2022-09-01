package chatemotes.mixins;

import chatemotes.ChatEmotes;
import chatemotes.impl.EmoteHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = {FontRenderer.class}, priority = 900)
public abstract class MixinFontRenderer {

    @Shadow protected float posX;

    @Shadow protected abstract void enableAlpha();

    @Shadow protected abstract void resetStyles();

    @Shadow public int FONT_HEIGHT;

    @Shadow protected abstract void bindTexture(ResourceLocation location);

    @Shadow protected float posY;

    @Shadow protected abstract void doDraw(float f);

    @Shadow protected abstract int renderString(String text, float x, float y, int color, boolean dropShadow);

    /**
     * This mixin will override the vanilla FontRenderer.drawString method whenever an emote is detected in the string
     * that is about to be rendered.
     * If so the string will be split up around the emotes and the emotes collected by a regex.
     * From those two lists the string is then drawn.
     */
    @Inject(method = {"drawString(Ljava/lang/String;FFIZ)I"}, at = {@At("HEAD")}, cancellable = true)
    public void drawCustomString(String text, float x, float y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> cir) {
        if (!ChatEmotes.Companion.getConfig().getEnabled()) return;
        List<String> matches = EmoteHandler.INSTANCE.emoteMatches(text);
        if (matches.isEmpty()) return;
        // Emotes are enabled and at least one was found in this string
        // From now on the vanilla is overridden


        List<String> surroundings = EmoteHandler.INSTANCE.splitText(text);

        enableAlpha();
        this.resetStyles();
        int i = (int)x;
        int j;
        int k = 0;


        // First check whether the text starts with an emote and if so draw that first.
        // for some reason this is not needed but even breaks stuff

//        if (ChatEmotes.INSTANCE.startsWithEmote(text)) {
//            ResourceLocation emote = ChatEmotes.INSTANCE.getEmoteResource(matches.get(0));
//            if (emote != null) {
//                i = renderEmote(emote, x, y);
//                k = 1;
//            }
//        }

        for (j = 0; j < surroundings.size(); j++)  {
            // Render a piece of text
            if (dropShadow)
            {
                int newX = i;
                i = this.renderString(surroundings.get(j), newX + 1.0F, y + 1.0F, color, true);
                i = Math.max(i, this.renderString(surroundings.get(j), newX, y, color, false));
            }
            else
            {
                i = this.renderString(surroundings.get(j), i, y, color, false);
            }

            // Render the next emote
            if (j + k < matches.size()) {
                ResourceLocation emote = EmoteHandler.INSTANCE.getEmoteResource(matches.get(j + k));
                if (emote != null) {
                    i = renderEmote(emote, i, y);
                }
            }
        }
        cir.setReturnValue(i);
    }

    /**
     * Renders the emote from the given resource at the current position.
     * @param resource Resource location for the emote.
     * @param x position for the emote.
     * @param y position for the emote.
     * @return The current x position where the next character has to be drawn.
     */
    private int renderEmote(ResourceLocation resource, float x, float y) {

        this.posX = x;
        this.posY = y;
        int l = this.FONT_HEIGHT;

        GL11.glPushMatrix();
        GlStateManager.color(255f, 255f, 255f, 255f);
        bindTexture(resource);
        Gui.drawModalRectWithCustomSizedTexture((int)this.posX, (int)this.posY - 1, 0f, 0f, l, l, l, l);
        GL11.glPopMatrix();

        this.posX += (float)l;

        return (int)this.posX;
    }
}

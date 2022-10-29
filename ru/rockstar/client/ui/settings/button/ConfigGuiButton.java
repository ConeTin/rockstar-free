package ru.rockstar.client.ui.settings.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import ru.rockstar.api.utils.render.DrawHelper;

import org.lwjgl.input.Mouse;

import java.awt.*;

public class ConfigGuiButton extends GuiButton {

    private int fade = 20;

    public ConfigGuiButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 35, buttonText);
    }

    public ConfigGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    public static int getMouseX() {
        return Mouse.getX() * sr.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
    }

    public static int getMouseY() {
        return sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float mouseButton) {
        if (this.visible) {
            int height = this.height - 31;
            this.hovered = (mouseX >= this.xPosition + 113 && mouseY >= (this.yPosition - 41) && mouseX < this.xPosition + this.width - 63 && mouseY < height + yPosition - 30);
            Color text = new Color(155, 155, 155, 255);
            if (!this.enabled) {
            } else if (this.hovered) {
                if (this.fade < 100)
                    this.fade += 8;
                text = Color.white;
            } else {
                if (this.fade > 20)
                    this.fade -= 8;
            }
            mc.mntsb_17.drawCenteredString(this.displayString, this.xPosition + this.width / 2F + 25, (this.yPosition) + (this.height - 70), text.getRGB());

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);


            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
    }

    public void mouseReleased(int mouseX, int mouseY) {

    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        int height = this.height - 31;
        return (this.enabled && this.visible && (mouseX >= this.xPosition + 113 && mouseY >= (this.yPosition - 45) && mouseX < this.xPosition + this.width - 63 && mouseY < height + yPosition - 30));
    }

    public boolean isMouseOver() {
        return this.hovered;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public int getButtonWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    }
package net.minecraft.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import ru.rockstar.api.utils.font.CFont;
import ru.rockstar.api.utils.render.ClientHelper;
import ru.rockstar.api.utils.render.DrawHelper;

import org.lwjgl.opengl.GL11;


import java.awt.*;

public class MCFontRenderer extends CFont {
    private final int[] colorCode = new int[32];
    protected CharData[] boldChars = new CharData[256];
    protected CharData[] italicChars = new CharData[256];
    protected CharData[] boldItalicChars = new CharData[256];
    protected DynamicTexture texBold;
    protected DynamicTexture texItalic;
    protected DynamicTexture texItalicBold;
    String colorcodeIdentifiers = "0123456789abcdefklmnor";

    public MCFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        super(font, antiAlias, fractionalMetrics);
        setupBoldItalicIDs();

        for (int index = 0; index < 32; index++) {
            int noClue = (index >> 3 & 0x1) * 85;
            int red = (index >> 2 & 0x1) * 170 + noClue;
            int green = (index >> 1 & 0x1) * 170 + noClue;
            int blue = (index & 0x1) * 170 + noClue;

            if (index == 6) {
                red += 85;
            }

            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }

            this.colorCode[index] = ((red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF);
        }
    }

    public void drawSmoothString(String text, double x2, float y2, int color) {
        this.drawString(text, x2, y2, color, false, 8.3f, true);
    }

        public void drawBlurredStringWithShadow(String text, double x, double y, int blurRadius, Color blurColor, int color) {
            GlStateManager.resetColor();
            DrawHelper.renderBlurredShadow((int) x, (int) y, (int) getStringWidth(text), (int) getFontHeight(), blurRadius, blurColor);
            drawStringWithShadow(text, (float) x, (float) y, color);
        }

    public void drawBlurredString(String text, double x, double y, int blurRadius, Color blurColor, int color) {
        GlStateManager.resetColor();
        DrawHelper.renderBlurredShadow((int) x, (int) y, (int) getStringWidth(text), (int) getFontHeight(), blurRadius, blurColor);
        drawString(text, (float) x, (float) y, color);
    }

    public void drawCenteredBlurredString(String text, double x, double y, int blurRadius, Color blurColor, int color) {
        GlStateManager.resetColor();
        DrawHelper.renderBlurredShadow((int) ((int) x - (float) this.getStringWidth(text) / 2.0f), (int) y, (int) getStringWidth(text), (int) getFontHeight(), blurRadius, blurColor);
        drawString(text, (float) (x - this.getStringWidth(text) / 2F), (float) y, color);
    }

    public void drawCenteredBlurredStringWithShadow(String text, double x, double y, int blurRadius, Color blurColor, int color) {
        GlStateManager.resetColor();
        DrawHelper.renderBlurredShadow((int) ((int) x - (float) this.getStringWidth(text) / 2.0f), (int) y, (int) getStringWidth(text), (int) getFontHeight(), blurRadius, blurColor);
        drawStringWithShadow(text, (float) (x - this.getStringWidth(text) / 2F), (float) y, color);
    }

    public void drawStringWithFade(String s, double x, double y) {
        double updateX = x;
        for (int i = 0; i < s.length(); i += 1) {

            String str = s.charAt(i) + "";

            this.drawStringWithShadow(str, updateX, y, ClientHelper.getClientColor(i - i + 1, i, 0.7f, 30).getRGB());

            updateX += getStringWidth(s.charAt(i) + "") + 0.4F;
        }
    }

    public static void drawStringWithOutline(MCFontRenderer fontRenderer, String text, float x, float y, int color) {
        fontRenderer.drawString(text, x - 0.8F, y, Color.BLACK.getRGB());
        fontRenderer.drawString(text, x + 0.8F, y, Color.BLACK.getRGB());
        fontRenderer.drawString(text, x, y - 0.8F, Color.BLACK.getRGB());
        fontRenderer.drawString(text, x, y + 0.8F, Color.BLACK.getRGB());
        fontRenderer.drawString(text, x, y, color);
    }

    public static void drawStringWithOutline(net.minecraft.client.gui.FontRenderer fontRenderer, String text, float x, float y, int color) {
        fontRenderer.drawString(text, x - 1, y, Color.BLACK.getRGB());
        fontRenderer.drawString(text, x + 1, y, Color.BLACK.getRGB());
        fontRenderer.drawString(text, x, y - 1, Color.BLACK.getRGB());
        fontRenderer.drawString(text, x, y + 1, Color.BLACK.getRGB());
        fontRenderer.drawString(text, x, y, color);
    }

    public static void drawCenteredStringWithOutline(net.minecraft.client.gui.FontRenderer fontRenderer, String text, float x, float y, int color) {
        fontRenderer.drawCenteredString(text, x - 1, y, Color.BLACK.getRGB());
        fontRenderer.drawCenteredString(text, x + 1, y, Color.BLACK.getRGB());
        fontRenderer.drawCenteredString(text, x, y - 1, Color.BLACK.getRGB());
        fontRenderer.drawCenteredString(text, x, y + 1, Color.BLACK.getRGB());
        fontRenderer.drawCenteredString(text, x, y, color);
    }

    public static float drawCenteredStringWithShadow(net.minecraft.client.gui.FontRenderer fontRenderer, String text, float x, float y, int color) {
        return fontRenderer.drawString(text, x - fontRenderer.getStringWidth(text) / 2, y, color);
    }

    public void drawCenteredStringWithOutline(MCFontRenderer fontRenderer, String text, float x, float y, int color) {
        drawCenteredString(text, x - 1, y, Color.BLACK.getRGB());
        drawCenteredString(text, x + 1, y, Color.BLACK.getRGB());
        drawCenteredString(text, x, y - 1, Color.BLACK.getRGB());
        drawCenteredString(text, x, y + 1, Color.BLACK.getRGB());
        drawCenteredString(text, x, y, color);
    }

    public float drawStringWithShadow(String text, double x, double y, int color) {
        float shadowWidth = drawString(text, x + 0.9D, y + 0.7D, color, true);
        return Math.max(shadowWidth, drawString(text, x, y, color, false));
    }

    public float drawString(String text, float x, float y, int color) {
        return drawString(text, x, y, color, false);
    }


    public float drawCenteredString(String text, float x, float y, int color) {
        return drawString(text, x - getStringWidth(text) / 2F, y, color);
    }

    public float drawCenteredStringWithShadow(String text, float x, float y, int color) {
        return drawString(text, x - getStringWidth(text) / 2, y, color);
    }

    public float drawString(String text, double x, double y, int color, boolean shadow, float kerning, boolean smooth) {
        if (text == null) {
            return 0;
        }

        if (shadow) {
            color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
        }

        CharData[] currentData = this.charData;
        float alpha = (float) (color >> 24 & 255) / 255f;
        boolean bold = false,
                italic = false,
                strikethrough = false,
                underline = false;
        x = (x - 1) * 2;
        y = (y - 3) * 2;
        GL11.glPushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        DrawHelper.color(color);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(this.tex.getGlTextureId());
        DrawHelper.bindTexture(this.tex.getGlTextureId());
        if (smooth) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        } else {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        }

        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);

            // If you need to apply color codes
            if (character == '�') {
                int colorIndex = 21;

                try {
                    colorIndex = colorcodeIdentifiers.indexOf(text.charAt(index + 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                    underline = false;
                    strikethrough = false;
                    GlStateManager.bindTexture(this.tex.getGlTextureId());
                    currentData = this.charData;

                    if (colorIndex < 0) {
                        colorIndex = 15;
                    }

                    if (shadow) {
                        colorIndex += 16;
                    }

                    DrawHelper.color(this.colorCode[colorIndex], alpha);
                } else {
                    switch (colorIndex) {
                        case 17:
                            bold = true;

                            if (italic) {
                                GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
                                currentData = this.boldItalicChars;
                            } else {
                                GlStateManager.bindTexture(this.texBold.getGlTextureId());
                                currentData = this.boldChars;
                            }
                            break;
                        case 18:
                            strikethrough = true;
                            break;
                        case 19:
                            underline = true;
                            break;
                        case 20:
                            italic = true;

                            if (bold) {
                                GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
                                currentData = this.boldItalicChars;
                            } else {
                                GlStateManager.bindTexture(this.texItalic.getGlTextureId());
                                currentData = this.italicChars;
                            }
                            break;
                        default:
                            bold = false;
                            italic = false;
                            underline = false;
                            strikethrough = false;
                            DrawHelper.color(color);
                            GlStateManager.bindTexture(this.tex.getGlTextureId());
                            currentData = this.charData;
                            break;
                    }
                }

                ++index;
            } else if (character < currentData.length) {
                drawLetter(x, y, currentData, strikethrough, underline, character);
                x += currentData[character].width - kerning + this.charOffset;
            }
        }

        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glPopMatrix();
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.bindTexture(0);
        return (float) x / 2f;
    }

    private void drawLetter(double x, double y, CharData[] currentData, boolean strikethrough, boolean underline, char character) {
        GL11.glBegin(GL11.GL_TRIANGLES);
        this.drawChar(currentData, character, (float) x, (float) y);
        GL11.glEnd();

        if (strikethrough) {
            this.drawLine(x, y + (double) (currentData[character].height / 2), x + (double) currentData[character].width - 8,
                    y + (double) (currentData[character].height / 2));
        }
        if (underline) {
            this.drawLine(x, y + (double) currentData[character].height - 2, x + (double) currentData[character].width - 8,
                    y + (double) currentData[character].height - 2);
        }
    }

    public float drawString(String text, double x, double y, int color, boolean shadow) {
        x -= 1.0;
        if (color == 0x20FFFFFF) {
            color = 0xFFFFFF;
        }
        if ((color & 0xFC000000) == 0) {
            color |= 0xFF000000;
        }
        if (shadow) {
            color = (color & 0xFCFCFC) >> 2 | color & new Color(20, 20, 20, 200).getRGB();
        }
        CharData[] currentData = this.charData;
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        boolean bold = false;
        boolean italic = false;
        boolean strikethrough = false;
        boolean underline = false;
        x *= 2;
        y = (y - 3) * 2;
        GL11.glPushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color((color >> 16 & 0xFF) / 255.0f, (color >> 8 & 0xFF) / 255.0f, (color & 0xFF) / 255.0f, alpha);
        int size = text.length();
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(this.tex.getGlTextureId());
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.tex.getGlTextureId());
        int i = 0;
        while (i < size) {
            char character = text.charAt(i);
            if (String.valueOf(character).equals("�")) {
                int colorIndex = 21;
                try {
                    colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                    underline = false;
                    strikethrough = false;
                    GlStateManager.bindTexture(this.tex.getGlTextureId());
                    currentData = this.charData;
                    if (colorIndex < 0) {
                        colorIndex = 15;
                    }
                    if (shadow) {
                        colorIndex += 16;
                    }
                    int colorcode = this.colorCode[colorIndex];
                    GlStateManager.color((colorcode >> 16 & 0xFF) / 255.0f, (colorcode >> 8 & 0xFF) / 255.0f, (colorcode & 0xFF) / 255.0f, alpha);
                } else if (colorIndex == 17) {
                    bold = true;
                    if (italic) {
                        GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
                        currentData = this.boldItalicChars;
                    } else {
                        GlStateManager.bindTexture(this.texBold.getGlTextureId());
                        currentData = this.boldChars;
                    }
                } else if (colorIndex == 18) {
                    strikethrough = true;
                } else if (colorIndex == 19) {
                    underline = true;
                } else if (colorIndex == 20) {
                    italic = true;
                    if (bold) {
                        GlStateManager.bindTexture(texItalicBold.getGlTextureId());
                        currentData = boldItalicChars;
                    } else {
                        GlStateManager.bindTexture(this.texItalic.getGlTextureId());
                        currentData = italicChars;
                    }
                } else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    underline = false;
                    strikethrough = false;
                    GlStateManager.color((color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F, alpha);
                    GlStateManager.bindTexture(this.tex.getGlTextureId());
                    currentData = this.charData;
                }
                ++i;
            } else if (character < currentData.length) {
                GL11.glBegin(4);
                this.drawChar(currentData, character, (float) x, (float) y);
                GL11.glEnd();
                if (strikethrough) {
                    this.drawLine(x, y + currentData[character].height / 2F, x + currentData[character].width - 8f, y + currentData[character].height / 2f, 1f);
                }
                if (underline) {
                    this.drawLine(x, y + currentData[character].height - 2.0, x + currentData[character].width - 8f, y + currentData[character].height - 2f, 1);
                }
                x += currentData[character].width - 8 + this.charOffset;
            }
            ++i;
        }
        GL11.glPopMatrix();
        return (float) (x / 2);
    }

    @Override
    public int getStringWidth(String text) {
        int width = 0;
        CharData[] currentData = this.charData;
        boolean bold = false;
        boolean italic = false;
        int size = text.length();
        int i = 0;
        while (i < size) {
            char character = text.charAt(i);
            if (String.valueOf(character).equals("�")) {
                int colorIndex = "0123456789abcdefklmnor".indexOf(character);
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                } else if (colorIndex == 17) {
                    bold = true;
                    currentData = italic ? this.boldItalicChars : this.boldChars;
                } else if (colorIndex == 20) {
                    italic = true;
                    currentData = bold ? this.boldItalicChars : this.italicChars;
                } else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    currentData = this.charData;
                }
                ++i;
            } else if (character < currentData.length) {
                width += currentData[character].width - 8 + this.charOffset;
            }
            ++i;
        }
        return width / 2;
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        setupBoldItalicIDs();
    }

    @Override
    public void setAntiAlias(boolean antiAlias) {
        super.setAntiAlias(antiAlias);
        setupBoldItalicIDs();
    }

    @Override
    public void setFractionalMetrics(boolean fractionalMetrics) {
        super.setFractionalMetrics(fractionalMetrics);
        setupBoldItalicIDs();
    }

    private void setupBoldItalicIDs() {
        texBold = setupTexture(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics, this.boldChars);
        texItalic = setupTexture(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics, this.italicChars);
        texItalicBold = setupTexture(this.font.deriveFont(3), this.antiAlias, this.fractionalMetrics, this.boldItalicChars);
    }
    public void drawSmoothStringWithShadow(String text, double x2, float y2, int color) {
        this.drawString(text, x2 + 0.5f, y2 + 0.5f, color, true, 8.3f, true);

        this.drawString(text, x2, y2, color, false, 8.3f, true);
    }
    private void drawLine(double x, double y, double x1, double y1, float width) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(width);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x1, y1);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void drawLine(double x2, double y2, double x1, double y1) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth((float) 1);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x1, y1);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public void drawStringWithOutline(String text, double x, double y, int color) {
        drawString(text, x - 0.5, y, Color.BLACK.getRGB(), false);
        drawString(text, x + 0.5F, y, Color.BLACK.getRGB(), false);
        drawString(text, x, y - 0.5F, Color.BLACK.getRGB(), false);
        drawString(text, x, y + 0.5F, Color.BLACK.getRGB(), false);
        drawString(text, x, y, color, false);
    }

    public void drawCenteredStringWithOutline(String text, float x, float y, int color) {
        drawCenteredString(text, x - 0.5F, y, Color.BLACK.getRGB());
        drawCenteredString(text, x + 0.5F, y, Color.BLACK.getRGB());
        drawCenteredString(text, x, y - 0.5F, Color.BLACK.getRGB());
        drawCenteredString(text, x, y + 0.5F, Color.BLACK.getRGB());
        drawCenteredString(text, x, y, color);
    }

}
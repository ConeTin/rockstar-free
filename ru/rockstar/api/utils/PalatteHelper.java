package ru.rockstar.api.utils;

import java.awt.Color;
import java.util.regex.Pattern;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import ru.rockstar.api.utils.math.MathematicHelper;
import ru.rockstar.client.features.impl.display.HUD;

public class PalatteHelper implements Helper
{
    public static Pattern COLOR_PATTERN;
    
    public static Color getHealthColor(final float health, final float maxHealth) {
        GlStateManager.pushMatrix();
        final float[] fractions = { 0.0f, 0.5f, 1.0f };
        final Color[] colors = { new Color(108, 0, 0), new Color(255, 51, 0), Color.GREEN };
        final float progress = health / maxHealth;
        GlStateManager.popMatrix();
        return blendColors(fractions, colors, progress).brighter();
    }
    
    public static int fade(int startColor, int endColor, float progress) {
        float invert = 1.0f - progress;
        int r = (int) ((startColor >> 16 & 0xFF) * invert + (endColor >> 16 & 0xFF) * progress);
        int g = (int) ((startColor >> 8 & 0xFF) * invert + (endColor >> 8 & 0xFF) * progress);
        int b = (int) ((startColor & 0xFF) * invert + (endColor & 0xFF) * progress);
        int a = (int) ((startColor >> 24 & 0xFF) * invert + (endColor >> 24 & 0xFF) * progress);
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }
    
    public static int reAlpha(final int color, final float alpha) {
        final Color c = new Color(color);
        final float r = 0.003921569f * c.getRed();
        final float g = 0.003921569f * c.getGreen();
        final float b = 0.003921569f * c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }
    
    public static int fadeColor(int startColor, int endColor, float progress) {
        if (progress > 1) {
            progress = 1 - progress % 1;
        }
        return fade(startColor, endColor, progress);
    }
    
    public static String removeColorCode(final String text) {
        String finalText = text;
        if (text.contains("¡\u00ec")) {
            for (int i = 0; i < finalText.length(); ++i) {
                if (Character.toString(finalText.charAt(i)).equals("¡\u00ec")) {
                    try {
                        final String part1 = finalText.substring(0, i);
                        final String part2 = finalText.substring(Math.min(i + 2, finalText.length()));
                        finalText = part1 + part2;
                    }
                    catch (Exception ex) {}
                }
            }
        }
        return finalText;
    }
    
    public static int astolfoColors(final int yOffset, final int yTotal) {
        float speed;
        float hue;
        for (speed = 2900.0f, hue = System.currentTimeMillis() % (int)speed + (float)((yTotal - yOffset) * 9); hue > speed; hue -= speed) {}
        hue /= speed;
        if (hue > 0.5) {
            hue = 0.5f - (hue - 0.5f);
        }
        hue += 0.5f;
        return Color.HSBtoRGB(hue, 0.5f, 1.0f);
    }
    
    private int getHealthColor(final EntityLivingBase player) {
        final float f = player.getHealth();
        final float f2 = player.getMaxHealth();
        final float f3 = Math.max(0.0f, Math.min(f, f2) / f2);
        return Color.HSBtoRGB(f3 / 3.0f, 1.0f, 1.0f) | 0xFF000000;
    }
    
    public static int[] getFractionIndicies(final float[] fractions, final float progress) {
        final int[] range = new int[2];
        int startPoint;
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {}
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }
    
    public static Color blendColors(final float[] fractions, final Color[] colors, final float progress) {
        Color color = null;
        if (fractions != null && colors != null && fractions.length == colors.length) {
            final int[] indicies = getFractionIndicies(fractions, progress);
            if (indicies[0] < 0 || indicies[0] >= fractions.length || indicies[1] < 0 || indicies[1] >= fractions.length) {
                return colors[0];
            }
            final float[] range = { fractions[indicies[0]], fractions[indicies[1]] };
            final Color[] colorRange = { colors[indicies[0]], colors[indicies[1]] };
            final float max = range[1] - range[0];
            final float value = progress - range[0];
            final float weight = value / max;
            color = blend(colorRange[0], colorRange[1], 1.0f - weight);
        }
        return color;
    }
    
    public static Color blend(final Color color1, final Color color2, final double ratio) {
        final float r = (float)ratio;
        final float ir = 1.0f - r;
        final float[] rgb1 = new float[3];
        final float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0f) {
            red = 0.0f;
        }
        else if (red > 255.0f) {
            red = 255.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        }
        else if (green > 255.0f) {
            green = 255.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        }
        else if (blue > 255.0f) {
            blue = 255.0f;
        }
        final Color color3 = new Color(red, green, blue);
        return color3;
    }
    
    public static int getColor(final Color color) {
        return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
    
    public static int getColor(final int bright) {
        return getColor(bright, bright, bright, 255);
    }
    
    public static Color getColorWithOpacity(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
    
    public static int getColor(final int red, final int green, final int blue) {
        return getColor(red, green, blue, 255);
    }
    
    public static int getColor(final int red, final int green, final int blue, final int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        color |= blue;
        return color;
    }
    
    public static int getColor(final int brightness, final int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }
    
    public static Color rainbow(final int delay, final float saturation, final float brightness) {
        double rainbow = Math.ceil((double)((System.currentTimeMillis() + delay) / 16L));
        rainbow %= 360.0;
        return Color.getHSBColor((float)(rainbow / 360.0), saturation, brightness);
    }
    
    public static Color rainbow2(final int delay, final float saturation, final float brightness) {
        double rainbow = Math.ceil((double)(System.currentTimeMillis() / delay));
        rainbow %= 360.0;
        return Color.getHSBColor((float)(rainbow / 360.0), saturation, brightness);
    }
    
    public static Color TwoColorEffect(final Color color, final Color color2, final double speed) {
        final double thing = speed / 4.0 % 1.0;
        final float clamp = MathematicHelper.clamp((float)(Math.sin(18.84955592153876 * thing) / 2.0 + 0.5), 0.0f, 1.0f);
        return new Color(MathematicHelper.lerp(color.getRed() / 255.0f, color2.getRed() / 255.0f, clamp), MathematicHelper.lerp(color.getGreen() / 255.0f, color2.getGreen() / 255.0f, clamp), MathematicHelper.lerp(color.getBlue() / 255.0f, color2.getBlue() / 255.0f, clamp));
    }
    
    public static Color astolfo(final float speed, final int yOffset) {
        float hue;
        for (hue = (float)(System.currentTimeMillis() % (int)speed + yOffset); hue > speed; hue -= speed) {}
        hue /= speed;
        if (hue > 0.5) {
            hue = 0.5f - (hue - 0.5f);
        }
        hue += 0.5f;
        return Color.getHSBColor(hue, 0.4f, 1.0f);
    }
    
    public static Color astolfo(final boolean clickgui, final int yOffset) {
        float speed;
        float hue;
        for (speed = (2000.0f), hue = (float)(System.currentTimeMillis() % (int)speed + yOffset); hue > speed; hue -= speed) {}
        hue /= speed;
        if (hue > 0.5) {
            hue = 0.5f - (hue - 0.5f);
        }
        hue += 0.5f;
        return Color.getHSBColor(hue, 0.4f, 1.0f);
    }
    
    public static String stripColor(final String name) {
        return PalatteHelper.COLOR_PATTERN.matcher(name).replaceAll("");
    }
    
    static {
        PalatteHelper.COLOR_PATTERN = Pattern.compile("(?i)&[0-9A-FK-OR]");
    }
}

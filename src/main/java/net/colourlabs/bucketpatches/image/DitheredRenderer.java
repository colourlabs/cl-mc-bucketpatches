package net.colourlabs.bucketpatches.image;

import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;

public final class DitheredRenderer implements BiConsumer<MapCanvas, BufferedImage> {
    @SuppressWarnings("deprecation")
    @Override
    public void accept(MapCanvas canvas, BufferedImage image) {
        if (image == null)
            return;

        int width = Math.min(image.getWidth(), 128);
        int height = Math.min(image.getHeight(), 128);

        float[][] errR = new float[height + 1][width];
        float[][] errG = new float[height + 1][width];
        float[][] errB = new float[height + 1][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = image.getRGB(x, y);
                int alpha = (argb >>> 24) & 0xFF;

                if (alpha < 128) {
                    // transparent pixel: skip dithering, don't propagate error either
                    canvas.setPixel(x, y, (byte) 0);
                    continue;
                }

                float r = clamp(((argb >> 16) & 0xFF) + errR[y][x]);
                float g = clamp(((argb >> 8) & 0xFF) + errG[y][x]);
                float b = clamp((argb & 0xFF) + errB[y][x]);

                byte colorIdx = MapPalette.matchColor((int) r, (int) g, (int) b);
                Color mc = MapPalette.getColor(colorIdx);

                float deR = r - mc.getRed();
                float deG = g - mc.getGreen();
                float deB = b - mc.getBlue();

                if (x + 1 < width) {
                    errR[y][x + 1] += deR * 7 / 16;
                    errG[y][x + 1] += deG * 7 / 16;
                    errB[y][x + 1] += deB * 7 / 16;
                }
                
                if (y + 1 < height) {
                    if (x > 0) {
                        errR[y + 1][x - 1] += deR * 3 / 16;
                        errG[y + 1][x - 1] += deG * 3 / 16;
                        errB[y + 1][x - 1] += deB * 3 / 16;
                    }

                    errR[y + 1][x] += deR * 5 / 16;
                    errG[y + 1][x] += deG * 5 / 16;
                    errB[y + 1][x] += deB * 5 / 16;

                    if (x + 1 < width) {
                        errR[y + 1][x + 1] += deR * 1 / 16;
                        errG[y + 1][x + 1] += deG * 1 / 16;
                        errB[y + 1][x + 1] += deB * 1 / 16;
                    }
                }

                canvas.setPixel(x, y, colorIdx);
            }
        }
    }

    private static float clamp(float v) {
        if (v < 0)
            return 0;
        if (v > 255)
            return 255;
        return v;
    }
}

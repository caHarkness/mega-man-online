package com.caharkness.megaman.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.caharkness.engine.Image;

public class GFX
{
    public static BufferedImage getCharImage(char c)
    {
        int row = 0;
        int col = 0;

        int ord = (int) c;

        if (ord < 32 || ord > 126)
            return null;

        col = ord - 32;
        row = 0;

        while (col > 31)
        {
            col -= 32;
            row++;
        }

        return Image.get("/sprites/spr_font_metallic.png", 8, 8, row, col);
    }

    public static void drawText(
        Graphics2D g,
        int x,
        int y,
        String text,
        int w,
        boolean centered,
        boolean squished)
    {
        int sx = x;
        int sy = y;

        if (centered)
            for (char c : text.toCharArray())
                if (squished)
                {
                    if (c != ' ')
                            sx -= 4;
                    else    sx -= 2;
                }   else    sx -= 4;

        int cx = 0;
        int cy = 0;

        for (char c : text.toCharArray())
        {
            if (centered)
                g.drawImage(
                    getCharImage(c),
                    sx + cx,
                    sy + cy,
                    null);
            else
                g.drawImage(
                    getCharImage(c),
                    x + cx,
                    y + cy,
                    null);


            if (cx + 8 >= (w * 8))
            {
                cx = 0;
                cy += 8;
            }
            else
            {
                if (squished)
                {
                    if (c != ' ')
                            cx += 8;
                    else    cx += 4;
                }   else    cx += 8;
            }
        }
    }

    public static void drawText(Graphics2D g, int x, int y, String text, int w)
    {
        drawText(g, x, y, text, w, false, false);
    }

    public static BufferedImage flipHorizontally(BufferedImage original)
    {
        BufferedImage image =
            new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D
            g = image.createGraphics();
            g.translate(32, 0);
            g.scale(-1, 1);
            g.drawImage(original, 0, 0, null);
            g.dispose();

        return image;
    }
}

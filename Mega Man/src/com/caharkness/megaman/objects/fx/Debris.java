package com.caharkness.megaman.objects.fx;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.caharkness.engine.Entity;

public class Debris extends Entity
{
    public BufferedImage source;
    int cx = 0;
    int cy = 0;

    @Override
    public void onCreate()
    {
        this.cx = (int) (Math.random() * source.getWidth() - 4);
        this.cy = (int) (Math.random() * source.getHeight() - 4);
        this.vel_x = (float) ((Math.random() * -4) + (Math.random() * 4));
        this.vel_y = (float) ((Math.random() * -4)); // + (Math.random() * 4));
        this.vel_r = (float) ((Math.random() * -8) + (Math.random() * 8));
        this.timers.put("life", 30 * 3);
    }

    @Override
    public void onStep()
    {
        this.vel_y += 1.65;

        if (!this.timers.containsKey("life"))
            this.destroy();
    }

    @Override
    public void onDraw(Graphics2D g)
    {
        g.drawImage(
            source,
            (int) (this.x - 2),
            (int) (this.y - 2),
            (int) (this.x + 2),
            (int) (this.y + 2),

            this.cx,
            this.cy,
            this.cx + 4,
            this.cy + 4,

            null,
            null);
    }
}

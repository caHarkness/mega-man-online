package com.caharkness.megaman.objects.fx;

import java.awt.Graphics2D;

import com.caharkness.engine.Entity;
import com.caharkness.engine.Image;

public class DeathOrb extends Entity
{
    @Override
    public void onCreate()
    {
        this.min_frame = 0;
        this.max_frame = 4;
        this.frame_max_age = 2;
    }

    @Override
    public void onStep()
    {

    }

    @Override
    public void onDraw(Graphics2D g)
    {
        g.drawImage(
            Image.get("/sprites/boom.png", 24, 24, 0, this.frame),
            (int) this.x,
            (int) this.y,
            null);
    }
}

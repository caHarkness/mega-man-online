package com.caharkness.megaman.objects.fx;

import java.awt.Graphics2D;

import com.caharkness.engine.Entity;
import com.caharkness.engine.Image;
import com.caharkness.engine.Sound;

public class Explosion extends Entity
{
    float offset_x = 0;
    float offset_y = 0;
    int count = 15;

    @Override
    public void onCreate()
    {
        this.width = 24;
        this.height = 24;

        this.min_frame = 0;
        this.max_frame = 4;

        this.frame = 0;
        this.frame_max_age = 0;
    }

    @Override
    public void onStep()
    {
        if (this.frame == this.max_frame && this.frame_age == this.frame_max_age)
        {
            count--;
            if (this.count < 1)
            {
                Sound.play("/sounds/snd_explosion_quiet.wav");
                this.destroy();
            }
            else Sound.play("/sounds/snd_explosion.wav");
            this.offset_x = (float) ((Math.random() * -12f) + (Math.random() * 12f));
            this.offset_y = (float) ((Math.random() * -12f) + (Math.random() * 12f));
        }
    }

    @Override
    public void onDraw(Graphics2D g)
    {
        g.drawImage(
            Image.get(
                "/sprites/spr_effects_explosion.png",
                24,
                24,
                0,
                this.max_frame - this.frame),
            (int) (this.x - 12) + (int) this.offset_x,
            (int) (this.y - 12) + (int) this.offset_y,
            null,
            null);
    }
}

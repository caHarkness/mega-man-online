package com.caharkness.megaman.objects;

import java.awt.Graphics2D;

import com.caharkness.engine.Entity;
import com.caharkness.megaman.util.GFX;

public class GenericDialog extends Entity
{
    String body;
    String buffer;

    @Override
    public void onCreate()
    {
        this.placement = Placement.IN_FOREGROUND;


    }

    @Override
    public void onStep()
    {
        if (!this.timers.containsKey("blip"))
            this.timers.put("blip", 10);

        if (body != null && body.length() > 0)
        {
            if (buffer == null)
                buffer = "";

            if (this.timers.get("blip") == 0)
            {
                buffer = buffer + body.charAt(0);
                body = body.substring(1);
            }
        }
    }

    @Override
    public void onDraw(Graphics2D g)
    {
        g.fillRect(
            0,
            this.engine.screen_height - 32,
            this.engine.screen_width,
            32);

        if (buffer != null)
            GFX.drawText(
                g,
                8,
                this.engine.screen_height - 24,
                buffer,
                100);
    }
}

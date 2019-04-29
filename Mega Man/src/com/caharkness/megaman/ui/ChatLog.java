package com.caharkness.megaman.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import com.caharkness.engine.Entity;
import com.caharkness.megaman.util.GFX;

public class ChatLog extends Entity
{
    ArrayList<String> feed;

    @Override
    public void onCreate()
    {
        this.feed = new ArrayList<>();
        this.placement = Placement.IN_FOREGROUND;
    }

    public void show()
    {
        this.timers.put("showing", 30 * 7);
    }

    public void println(String line)
    {
        String buffer = line;

        while (buffer.length() > (this.engine.screen_width / 8))
        {
            String nline = "";

            for (int i = 0; i < 32; i++)
            {
                nline += buffer.charAt(0);
                buffer = buffer.substring(1);
            }

            this.feed.add(nline);
        }

        this.feed.add(buffer);

        if (this.feed.size() > 5)
            this.feed.remove(0);

        this.show();
    }

    @Override
    public void onStep()
    {
    }

    @Override
    public void onDraw(Graphics2D g)
    {
        if (!this.timers.containsKey("showing"))
            return;

        if (this.timers.containsKey("showing"))
            if (this.timers.get("showing") < 30 * 2)
                if (this.timers.get("showing") % 2 == 0)
                    return;

        g.setColor(new Color(0, 0, 0, 64));
        g.fillRect(
            0,
            this.engine.screen_height - 8 - (8 * this.feed.size()),
            this.engine.screen_width,
            8);

        int yo = 0;
        for (String line : this.feed)
        {

            GFX.drawText(
                g,
                0,
                this.engine.screen_height - 8 - (8 * this.feed.size()) + (yo * 8),
                line,
                this.engine.screen_width / 8);

            yo++;
        }
    }
}

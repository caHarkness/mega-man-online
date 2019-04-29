package com.caharkness.megaman.ui;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import com.caharkness.engine.Entity;
import com.caharkness.engine.Keyboard;
import com.caharkness.engine.Sound;
import com.caharkness.megaman.util.GFX;

public class TextRoll extends Entity
{
    public ArrayList<String> roll = new ArrayList<>();
    public Entity after;
    public String text;
    public String buffer;
    public String drawn_text;

    @Override
    public void onCreate()
    {
        this.placement = Placement.IN_FOREGROUND;
    }

    public static char randomSeriesForThreeCharacter()
    {
        Random r = new Random();
        char random_3_Char = (char) (48 + r.nextInt(47));
        return random_3_Char;
    }

    public void end()
    {
        if (after != null)
        {
            this.spawn(after);
            this.destroy();
            Sound.play("/sounds/snd_explosion_quiet.wav");
        }
    }

    @Override
    public void onStep()
    {
        if (Keyboard.await(KeyEvent.VK_ESCAPE))
        {
            this.roll.clear();
            this.end();
        }

        if (!this.timers.containsKey("roll"))
        {
            if (roll.size() > 0)
            {
                this.timers.put("roll", 30 * 6);
                this.text = roll.get(0);
                this.buffer = this.text;
                this.drawn_text = "";
                this.roll.remove(0);
                this.x = this.engine.screen_width / 2;
                this.y = this.engine.screen_height + 8;
                Sound.play("/sounds/snd_explosion.wav");
            }
            else
            this.end();
        }

        if (this.timers.containsKey("roll"))
        {
            if (this.y > this.engine.screen_height / 2)
            {
                this.y -= 2;
            }
            else
            {
                if (this.timers.get("roll") < 30)
                    if (!this.timers.containsKey("blink"))
                        this.timers.put("blink", 1);
            }

            if (this.buffer.length() > 0)
            {
                if (!this.timers.containsKey("char"))
                {
                    this.timers.put("char", 1 + (int) (Math.random() * 2));
                    this.drawn_text += this.buffer.charAt(0);
                    this.buffer = this.buffer.substring(1);
                    Sound.play("/sounds/snd_teleprompt_char.wav");
                }
            }
        }
    }

    @Override
    public void onDraw(Graphics2D g)
    {
        if (text != null)
        {
            if (this.timers.containsKey("blink"))
                if (this.timers.get("blink") < 1)
                    return;

            int i = 0;
            for (String s : (drawn_text + "\n").split("\n"))
            {
                GFX.drawText(
                    g,
                    (int) this.x,
                    (int) this.y + (8 * i),
                    s,
                    (this.engine.screen_width) / 8 - 4,
                    true,
                    false);

                i += 1;
            }
        }
    }
}

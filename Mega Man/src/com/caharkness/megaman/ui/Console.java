package com.caharkness.megaman.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import com.caharkness.engine.Entity;
import com.caharkness.engine.Keyboard;
import com.caharkness.megaman.util.GFX;

public class Console extends Entity
{
    public String text = "";
    public String write_buffer = "";
    public String input_buffer = "";

    public int cx = 0;
    public int cy = 0;

    public Console()
    {
    }

    @Override
    public void onCreate()
    {
        this.placement = Placement.IN_FOREGROUND;

        this.x = 0;
        this.y = 0;
        this.width = this.engine.screen_width;
        this.height = this.engine.screen_height;

        Keyboard.setFocus(this);
    }

    @Override
    public void onStep()
    {
        if (!this.timers.containsKey("blink"))
            this.timers.put("blink", 14);

        if (!this.timers.containsKey("buffer"))
        {
            if (this.write_buffer.length() > 0)
            {
                this.text += this.write_buffer.charAt(0);
                this.write_buffer = this.write_buffer.substring(1);
            }

            this.timers.put("buffer", 1 + (int) (Math.random() * 1));
        }
    }

    @Override
    public void onKeyPressed(KeyEvent e)
    {
        if (e.getKeyChar() > 31 && e.getKeyChar() < 127)
            input_buffer = input_buffer + e.getKeyChar();

        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
            if (input_buffer.length() > 0)
                input_buffer = input_buffer.substring(0, input_buffer.length() - 1);

        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            this.onEnterKeyPressed();
    }

    public void onEnterKeyPressed()
    {
        this.text += this.input_buffer + "\n";
        this.input_buffer = "";
    }

    public void println(String line)
    {
        this.write_buffer += line + "\n";
    }

    @Override
    public void onDraw(Graphics2D g)
    {
        g.setColor(new Color(0, 0, 255, 64));
        g.fillRect(
            (int) this.x,
            (int) this.y,
            (int) this.width,
            (int) this.height);

            cx = 0;
            cy = 0;

            for (char c : (this.text + this.input_buffer).toCharArray())
            {
                if (c == '\n')
                {
                    cx = 0;
                    cy += 8;
                    continue;
                }

                g.drawImage(
                    GFX.getCharImage(c),
                    (int) this.x + cx,
                    (int) this.y + cy,
                    null);

                if (cx + 8 >= this.width)
                {
                    cx = 0;
                    cy += 8;
                }
                else cx += 8;
            }

        if (this.timers.containsKey("blink"))
            if (this.timers.get("blink") < 7)
            {
                g.setColor(Color.WHITE);
                g.fillRect(
                    (int) this.x + cx,
                    (int) this.y + cy,
                    8,
                    8);
            }

        //if ()
    }
}

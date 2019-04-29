package com.caharkness.megaman.objects;

import java.awt.Graphics2D;
import java.util.ArrayList;

import com.caharkness.engine.Entity;
import com.caharkness.engine.Keyboard;
import com.caharkness.engine.Sound;
import com.caharkness.megaman.util.GFX;
import com.sun.glass.events.KeyEvent;

public class GenericMenu extends Entity
{
    class MenuItem
    {
        public String label;
        public Runnable runnable;
        public Class<? extends Entity> cls;

        public MenuItem(String label, Class<? extends Entity> cls)
        {
            this.label = label;
            this.cls = cls;
        }

        public MenuItem(String label, Runnable runnable)
        {
            this.label = label;
            this.runnable = runnable;
        }
    }

    ArrayList<MenuItem> items = new ArrayList<>();
    int selected = 0;

    @Override
    public void onCreate()
    {
        this.placement = Placement.IN_FOREGROUND;
    }

    @Override
    public void onStep()
    {
        if (Keyboard.await(KeyEvent.VK_W))
        {
            Sound.play("/sounds/snd_selection_change.wav");
            this.selected--;
        }

        if (Keyboard.await(KeyEvent.VK_S))
        {
            Sound.play("/sounds/snd_selection_change.wav");
            this.selected++;
        }

        if (Keyboard.await(KeyEvent.VK_ENTER))
        {
            try
            {
                MenuItem selection = items.get(selected);

                if (selection.cls != null)
                {
                    this.destroy();
                    this.engine.spawn(selection.cls.newInstance());
                }
                else
                if (selection.runnable != null)
                {
                    this.destroy();
                    selection
                        .runnable
                        .run();
                }

                Sound.play("/sounds/snd_selection_made.wav");
            }
            catch (Exception x) {}
        }

        if (this.selected < 0)
            this.selected = items.size() - 1;

        if (this.selected >= items.size())
            this.selected = 0;
    }

    public MenuItem addItem(String label, Class<? extends Entity> cls)
    {
        MenuItem item = new MenuItem(label, cls);

        items.add(item);

        return item;
    }

    public MenuItem addItem(String label, Runnable runnable)
    {
        MenuItem item = new MenuItem(label, runnable);

        items.add(item);

        return item;
    }

    @Override
    public void onDraw(Graphics2D g)
    {
        g.fillRect(
            0,
            0,
            this.engine.screen_width,
            this.engine.screen_height);

        int i = 0;
        for (MenuItem item : items)
        {
            String s = (this.selected == i? ">" : " ") + " " + item.label;

            GFX.drawText(g, (int) this.x, (int) this.y + (i * 8), s, 100);
            i++;
        }
    }
}

package com.caharkness.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.caharkness.engine.Entity.CollisionRect;

public class Engine extends JPanel implements Runnable
{
    private static final long serialVersionUID = 3032294362501311883L;

    public float scale_x                    = 0;
    public float scale_y                    = 0;
    public int offset_x                     = 0;
    public int offset_y                     = 0;
    public int screen_width                 = 640;
    public int screen_height                = 480;
    public int target_framerate             = 30;
    public int timer                        = 30;
    public float mouse_sx                   = 0;
    public float mouse_sy                   = 0;
    public boolean running                  = false;
    public boolean sort                     = false;
    public boolean debug                    = true;
    public Thread thread                    = null;
    public BufferedImage image              = null;
    public BufferedImage image_x            = null;
    public Graphics2D graphics              = null;
    private Color vertical_blank            = null;
    private AffineTransform at_original     = null;
    private AffineTransform at_translated   = null;
    public JFrame window                    = null;

    @Override
    public void run()
    {
        long start;
        long elapsed;
        long wait;

        this.running = true;

        while (this.running)
        {
            start = System.nanoTime();

            Keyboard.publish();
            Mouse.feed(this);
            Mouse.publish();

            this.onPrepareStep();
            this.onPrepareRender();
            this.onPrepareDraw();
            this.repaint();

            this.processDespawns();
            this.processSpawns();

            if (this.sort)
                this.stageSort();

            elapsed = System.nanoTime() - start;
            wait =
                (1000 / this.target_framerate) -
                (elapsed / 1000000);

            try
            {
                Thread.sleep(wait);
            }
            catch (Exception x) {}
        }
    }

    @Override
    public void addNotify()
    {
        super.addNotify();
        this.addKeyListener(Keyboard.getInstance());
        this.addMouseListener(Mouse.getInstance());
        this.addMouseWheelListener(Mouse.getInstance());

        if (thread == null)
        {
            thread = new Thread(this, "Main Loop");
            thread.start();
        }
    }

    private void onPrepareStep()
    {
        for (Entity e : this.entities)
            e.onPrepareStep();
    }

    private void onPrepareRender()
    {
        for (Entity e : this.entities)
            e.onPrepareRender();
    }

    private void onPrepareDraw()
    {
        this.graphics = this.image.createGraphics();

        if (this.vertical_blank == null)
            this.vertical_blank = new Color(0, 0, 0, 200);

        this.graphics.setColor(this.vertical_blank);
        this.graphics.fillRect(
            0,
            0,
            this.screen_width,
            this.screen_height);

        this.at_original = this.graphics.getTransform();
        this.at_translated = new AffineTransform();

        this.at_translated.translate(
            -this.camera_x,
            -this.camera_y);

        //
        //  Draw the static background entities first.
        //
        for (Entity e : this.entities)
            if (e.placement == Entity.Placement.IN_BACKGROUND)
                e.onPrepareDraw(this.graphics);

        //
        //  Draw the entities that can be tracked with a camera.
        //
        this.graphics.setTransform(this.at_translated);

        for (Entity e : this.entities)
            if (e.placement == Entity.Placement.ON_MAP)
                e.onPrepareDraw(this.graphics);

        //
        //  Draw the entities in the foreground such as the HUD.
        //
        this.graphics.setTransform(this.at_original);

        for (Entity e : this.entities)
            if (e.placement == Entity.Placement.IN_FOREGROUND)
                e.onPrepareDraw(this.graphics);

        /*
        for (int y = 0; y < this.screen_height; y+=2)
        {
            graphics.setColor(new Color(0, 0, 0, 25));
            graphics.drawLine(0, y, this.screen_width, y);
        }
        */

        this.graphics.dispose();
        this.onScale();
    }

    public void onScale()
    {
        try
        {
            int ew = Engine.this.getWidth();
            int eh = Engine.this.getHeight();

            image_x =
                Image.resizeImage(
                    image,
                    ew,
                    eh);

            scale_x = image_x.getWidth() * 1f / image.getWidth() * 1f;
            scale_y = image_x.getHeight() * 1f / image.getHeight() * 1f;

            offset_x = ew > eh? ew - (image_x.getWidth() / 2) - (ew / 2) : 0;
            offset_y = eh > ew? eh - (image_x.getHeight() / 2) - (eh / 2) : 0;

            mouse_sx = (this.getMousePosition().x - offset_x) / scale_x;
            mouse_sy = (this.getMousePosition().y - offset_y) / scale_y;
        }
        catch (Exception x) {}
    }

    public float camera_x = 0;
    public float camera_y = 0;

    public void moveCamera(float x, float y)
    {
        this.camera_x = x - ((this.screen_width) / 2);
        this.camera_y = y - ((this.screen_height) / 2);
    }

    public ArrayList<Entity> entities = new ArrayList<>();
    public ArrayList<Entity> stage = new ArrayList<>();

    public Entity spawn(Entity e)
    {
        e.engine = this;
        stage.add(e);

        this.sort = true;
        return e;
    }

    public List<Entity> findEntitiesWithTag(String tag)
    {
        ArrayList<Entity> list = new ArrayList<>();

        for (Entity e : entities)
            if (e.tags.contains(tag))
                list.add(e);

        return list;
    }

    public List<Entity> findEntitiesAt(float x, float y)
    {
        ArrayList<Entity> list = new ArrayList<>();

        for (Entity e : entities)
            if (e.getRect(CollisionRect.WHOLE).contains(x, y))
                list.add(e);

        return list;
    }

    private synchronized void processDespawns()
    {
        ArrayList<Entity> cache = new ArrayList<>();

        for (Entity e : this.entities)
        {
            if (e.destroyed == false)
                cache.add(e);
        }

        cache.sort(new Comparator<Entity>()
        {
            @Override
            public int compare(Entity o1, Entity o2)
            {
                return (int) o1.z - (int) o2.z;
            }
        });

        entities.clear();
        entities.addAll(cache);
    }

    private synchronized void processSpawns()
    {
        for (Entity e : this.stage)
            this.entities.add(e);

        this.stage.clear();
    }

    private void stageSort()
    {
        ArrayList<Entity> cache = new ArrayList<>();

        for (Entity e : this.entities)
        {
            if (e.destroyed == false)
                cache.add(e);
        }

        cache.sort(new Comparator<Entity>()
        {
            @Override
            public int compare(Entity o1, Entity o2)
            {
                return (int) o1.z - (int) o2.z;
            }
        });

        this.entities.clear();
        this.entities.addAll(cache);
        this.sort = false;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        if (image_x != null)
        {
            g.drawImage(
                image_x,
                offset_x,
                offset_y,
                Color.BLACK,
                null);
        }
    }

    public void resizeWindow(int width, int height)
    {
        this.setPreferredSize(new Dimension(width, height));
        this.window.pack();
    }

    public void resizeViewport(int width, int height)
    {
        //this.setPreferredSize(new Dimension(width, height));
        //this.window.pack();

        this.running = false;

        int t = 0;
        while (this.thread.isAlive())
            t++;

        this.screen_width = width;
        this.screen_height = height;

        image = new BufferedImage(
            this.screen_width,
            this.screen_height,
            BufferedImage.TYPE_INT_ARGB);

        this.thread = new Thread(this);
        thread.start();
    }

    public Engine(int width, int height)
    {
        this.screen_width   = width;
        this.screen_height  = height;

        Dimension resolution =
            new Dimension(
                this.screen_width,
                this.screen_height);

        this.setPreferredSize(resolution);
        this.setMinimumSize(resolution);
        this.setFocusable(true);
        this.requestFocus();
        this.setFocusTraversalKeysEnabled(false);

        image = new BufferedImage(
            this.screen_width,
            this.screen_height,
            BufferedImage.TYPE_INT_ARGB);

        //
        //
        //

        window = new JFrame();
        window.setBackground(Color.BLACK);
        window.setContentPane(this);
        //window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setFocusTraversalKeysEnabled(false);

        window.pack();
        window.setVisible(true);
        window.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                Engine.this.running = false;

                int t = 0;
                while (Engine.this.thread.isAlive())
                    t++;

                System.exit(0);
            }
        });
    }

    public void setTargetFramerate(int fr)
    {
        this.target_framerate = fr;
    }
}



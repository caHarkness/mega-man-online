package com.caharkness.engine;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

public class Entity
{
    public Engine engine     = null;
    public Entity parent     = null;

    public float x			 = 0;
    public float y			 = 0;
    public float z           = 0;
    public float r           = 0;

    public float vel_x		 = 0;
    public float vel_y		 = 0;
    public float vel_z       = 0;
    public float vel_r       = 0;

    public float piv_x       = 0;
    public float piv_y       = 0;

    public float width		 = 0;
    public float height	     = 0;

    public boolean created   = false;
    public long birth		 = 0;
    public long age			 = 0;
    public boolean destroyed = false;

    public int frame         = 0;
    public int frame_age     = 0;
    public int frame_max_age = 0;
    public int min_frame     = 0;
    public int max_frame     = 0;

    public BufferedImage image;
    public Placement placement = Placement.ON_MAP;

    public enum Placement
    {
        IN_BACKGROUND,
        ON_MAP,
        IN_FOREGROUND
    }

    public Entity()
    {
    }

    //
    //
    //
    //  Entity Categorization and Identification
    //
    //
    //

    public ArrayList<String> tags = new ArrayList<>();
    public HashMap<String, Integer> timers = new HashMap<>();

    public Entity spawn(Entity e)
    {
        this.engine.spawn(e);
        // e.x = this.x;
        // e.y = this.y;
        e.parent = this;
        return e;
    }

    //
    //
    //
    //  Collision detection
    //
    //
    //

    public enum CollisionRect
    {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        HORIZONTAL,
        VERTICAL,
        WHOLE
    }

    Rectangle.Float top_rectangle;
    Rectangle.Float bottom_rectangle;
    Rectangle.Float left_rectangle;
    Rectangle.Float right_rectangle;
    Rectangle.Float horizontal_rectangle;
    Rectangle.Float vertical_rectangle;
    Rectangle.Float whole_rectangle;

    public Rectangle.Float getRect(CollisionRect type)
    {
        switch (type)
        {
            case TOP:
                if (top_rectangle == null)
                    top_rectangle = new Rectangle.Float();

                top_rectangle.setRect(
                    this.x,
                    this.y,
                    this.width,
                    4);

                return top_rectangle;

                //
                //
                //

            case BOTTOM:
                if (bottom_rectangle == null)
                    bottom_rectangle = new Rectangle.Float();

                bottom_rectangle.setRect(
                    this.x,
                    this.y + this.height - 4,
                    this.width,
                    4);

                return bottom_rectangle;

                //
                //
                //

            case LEFT:
                if (left_rectangle == null)
                    left_rectangle = new Rectangle.Float();

                left_rectangle.setRect(
                    this.x,
                    this.y,
                    4,
                    this.height);

                return left_rectangle;

                //
                //
                //

            case RIGHT:
                if (right_rectangle == null)
                    right_rectangle = new Rectangle.Float();

                right_rectangle.setRect(
                    this.x + this.width - 4,
                    this.y,
                    4,
                    this.height);

                return right_rectangle;

                //
                //
                //

            case HORIZONTAL:
                if (horizontal_rectangle == null)
                    horizontal_rectangle = new Rectangle.Float();

                horizontal_rectangle.setRect(
                    this.x,
                    this.y + 4,
                    this.width,
                    this.height - 8);

                return horizontal_rectangle;

                //
                //
                //

            case VERTICAL:
                if (vertical_rectangle == null)
                    vertical_rectangle = new Rectangle.Float();

                vertical_rectangle.setRect(
                    this.x + 4,
                    this.y,
                    this.width - 8,
                    this.height);

                return vertical_rectangle;

                //
                //
                //

            case WHOLE:
                if (whole_rectangle == null)
                    whole_rectangle = new Rectangle.Float();

                whole_rectangle.setRect(
                    this.x,
                    this.y,
                    this.width,
                    this.height);

                return whole_rectangle;

            default:
                return null;
        }
    }

    public List<Entity> getCollisions(CollisionRect type)
    {
        ArrayList<Entity> collisions = new ArrayList<>();

        for (Entity e : this.engine.entities)
            if (e != this)
                if (this.getRect(type).intersects(e.getRect(CollisionRect.WHOLE)))
                    collisions.add(e);

        return collisions;
    }

    public Entity getCollisionWith(CollisionRect type, String tag)
    {
        for (Entity e : this.engine.entities)
            if (e.tags.contains(tag))
                if (e != this)
                    if (this.getRect(type).intersects(e.getRect(CollisionRect.WHOLE)))
                        return e;

        return null;
    }

    public List<Entity> getCollisionsWithTag(CollisionRect type, String tag)
    {
        ArrayList<Entity> collisions = new ArrayList<>();

        for (Entity e : this.engine.entities)
            if (e.tags.contains(tag))
                if (e != this)
                    if (this.getRect(type).intersects(e.getRect(CollisionRect.WHOLE)))
                        collisions.add(e);

        return collisions;
    }

    //
    //
    //  The onCreate() event is called once per instance of an object.
    //
    //

    public void onPrepareCreate()
    {
        this.birth = System.nanoTime();
        this.onCreate();
    }

    public void onCreate()
    {
    }

    //
    //
    //  The onStep() event is called repetitively throughout and during the lifecycle of an object.
    //
    //

    public void onPrepareStep()
    {
        if (this.created == false)
        {
            this.onPrepareCreate();
            this.created = true;
        }

        this.x += this.vel_x;
        this.y += this.vel_y;
        this.z += this.vel_z;
        this.r += this.vel_r;

        if (this.r < 0)     this.r += 360;
        if (this.r > 360)   this.r -= 360;

        this.age = System.nanoTime() - this.birth;


        this.frame_age++;
        if (this.frame_age > this.frame_max_age)
        {
            this.frame_age = 0;
            this.frame++;
        }

        if (this.frame > this.max_frame)
            this.frame = this.min_frame;

        try
        {
            for (String key : timers.keySet())
            {
                timers.put(key, timers.get(key) - 1);

                if (timers.get(key) < 0)
                    timers.remove(key);
            }
        }
        catch (ConcurrentModificationException x) {}

        this.onStep();
    }

    public void onStep()
    {
    }

    public void onPrepareRender()
    {
        this.onRender();
    }

    public void onRender()
    {
    }

    //
    //
    //  The onDraw() event for each object is called after every object's onStep() event has been called.
    //
    //

    public void onPrepareDraw(Graphics2D g)
    {
        AffineTransform transform = g.getTransform();

        g.rotate(
            Math.toRadians(this.r),
            this.x + piv_x,
            this.y + piv_y
            //this.x,
            //this.y
        );

        // Call the overridden onDraw() method
        this.onDraw(g);

        g.setTransform(transform);
    }

    public void onDraw(Graphics2D graphics)
    {
    }

    //
    //
    //  The onDestroy() event is called only after the object has been flagged for removal.
    //
    //

    public void onDestroy()
    {
    }

    public void onApplicationExit()
    {
    }

    public void destroy()
    {
        this.destroyed = true;
        this.onDestroy();
    }

    public void onKeyPressed(KeyEvent e)
    {
    }

    public void onKeyReleased(KeyEvent e)
    {
    }

    public void onMousePressed(MouseEvent e)
    {
    }

    public void onMouseReleased(MouseEvent e)
    {
    }

    public void onMouseWheelMoved(MouseWheelEvent e)
    {
    }

    //
    //
    //
    //  Handy Methods
    //
    //
    //

    public long getAgeInMilliseconds() { return (int) this.age / 100000; }
    public long getAgeInSeconds()      { return (int) this.getAgeInMilliseconds() / 1000; }
    public float getCenterX()          { return this.x + this.width / 2f; }
    public float getCenterY()          { return this.y + this.height / 2f; }
}

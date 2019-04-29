package com.caharkness.megaman.models;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import com.caharkness.engine.Entity;
import com.caharkness.engine.Image;
import com.caharkness.engine.Keyboard;
import com.caharkness.engine.Sound;
import com.caharkness.megaman.controllers.NetworkClient;
import com.caharkness.megaman.objects.NetworkPlayer;
import com.caharkness.megaman.objects.PlayerSpawn;
import com.caharkness.megaman.objects.WeaponProjectile;
import com.caharkness.megaman.objects.fx.DeathOrb;
import com.caharkness.megaman.packets.PlayerPacket;
import com.caharkness.megaman.util.GFX;

public class PlayerEntity extends Entity
{
    public static final int PLAYER_VISIBLE = 0x01;
    public static final int PLAYER_FORWARD = 0x02;
    public static final int PLAYER_RUNNING = 0x04;
    public static final int PLAYER_SHOOTING = 0x08;
    public static final int PLAYER_FALLING = 0x10;
    public static final int PLAYER_CLIMBING = 0x20;
    public static final int PLAYER_HURTING = 0x40;
    public static final int PLAYER_DEAD = 0x80;

    public BufferedImage sprite = null;

    public String name = "";
    public int health = 32;
    public int sprite_row = 0;
    public int sprite_col = 0;
    public byte state = 0;
    public int appearance_index = 0;
    public PlayerAppearance appearance = PlayerAppearance.CLASSIC_MEGA_MAN;

    public void set(int flag, boolean value)
    {
        if (value == true)
                this.state = (byte) (this.state | flag);
        else    this.state = (byte) (this.state & ~flag);
    }

    public boolean is(int flag)
    {
        return (this.state & flag) != 0;
    }

    public int getUpKey()
    {
        return KeyEvent.VK_W;
    }

    public int getLeftKey()
    {
        return KeyEvent.VK_A;
    }

    public int getDownKey()
    {
        return KeyEvent.VK_S;
    }

    public int getRightKey()
    {
        return KeyEvent.VK_D;
    }

    public int getFireKey()
    {
        return KeyEvent.VK_CONTROL;
    }

    public int getDetachKey()
    {
        return KeyEvent.VK_SPACE;
    }

    public String getFireSound()
    {
        return appearance.getFireSound();
    }

    public String getJumpSound()
    {
        return appearance.getJumpSound();
    }

    public String getLandingSound()
    {
        return appearance.getLandSound();
    }

    public String getSpriteSheet()
    {
        return appearance.getSpriteSheet();
    }

    public PlayerPacket getPacket()
    {
        return new PlayerPacket(this);
    }

    public void updateFromPacket(PlayerPacket packet)
    {
        this.x = packet.getX();
        this.y = packet.getY();
        this.vel_x = packet.getVelocityX();
        this.vel_y = packet.getVelocityY();
        this.state = packet.getState();
    }

    @Override
    public void onCreate()
    {
        this.tags.add("player");

        this.x = 0;
        this.y = 0;

        this.width = 16;
        this.height = 32;

        this.min_frame = 0;
        this.max_frame = 2;
        this.frame_max_age = 2;

        this.set(PLAYER_VISIBLE, true);
        this.set(PLAYER_DEAD, false);
    }













    public void handleState()
    {
        if (!this.timers.containsKey("shooting"))
            this.set(PLAYER_SHOOTING, false);

        if (Math.abs(this.vel_x) > 0.5)
                this.set(PLAYER_RUNNING, true);
        else    this.set(PLAYER_RUNNING, false);

        if (Math.abs(this.vel_y) > 2)
        {
            this.set(PLAYER_RUNNING, false);
            this.set(PLAYER_FALLING, true);
        }

        if (Math.abs(this.vel_x) > 0.2 || Math.abs(this.vel_y) > 0.2)
            if (!this.timers.containsKey("moving"))
                this.timers.put("moving", 5);

        if (!this.timers.containsKey("hurting"))
            this.set(PLAYER_HURTING, false);
    }

    public boolean checkInput()
    {
        return true;
    }

    public void moveLeft()
    {
        this.vel_x -= 3;
        this.set(PLAYER_FORWARD, false);
        this.set(PLAYER_RUNNING, true);
    }

    public void moveRight()
    {
        this.vel_x += 3;
        this.set(PLAYER_FORWARD, true);
        this.set(PLAYER_RUNNING, true);
    }

    public void jump()
    {
        // Sound.play("/sounds/mml_jump.wav");
        this.vel_y = -12;
        this.set(PLAYER_FALLING, true);
    }

    public void cancelJump()
    {
        this.vel_y = 0;
    }

    public void climbUp(Entity e)
    {
        this.timers.remove("shooting");

        this.vel_y -=  2;
        this.set(PLAYER_CLIMBING, true);

        if (!this.timers.containsKey("climbing"))
            this.timers.put("climbing", 14);

        this.x = e.x;
    }

    public void climbDown(Entity e)
    {
        this.timers.remove("shooting");

        this.vel_y += 2;
        this.set(PLAYER_CLIMBING, true);

        if (!this.timers.containsKey("climbing"))
            this.timers.put("climbing", 14);

        this.x = e.x;
    }

    public void onUpKeyHeld()
    {
        Entity ladder = this.getCollisionWith(CollisionRect.VERTICAL, "ladder");
        if (ladder != null)
        {
            if (Keyboard.check(this.getFireKey()))
                return;

            if (this.is(PLAYER_CLIMBING))
            {
                this.climbUp(ladder);
                return;
            }
            else
            if (Keyboard.await(this.getUpKey()))
                if (!Keyboard.check(this.getLeftKey()) && !Keyboard.check(this.getRightKey()))
                {
                    this.set(PLAYER_CLIMBING, true);
                    this.climbUp(ladder);
                    return;
                }
        }

        if (Keyboard.await(this.getUpKey()))
        {
            if (vel_y == 0 && !this.is(PLAYER_CLIMBING))
                this.jump();
        }
    }

    public void onUpKeyNotHeld()
    {
        if (this.vel_y < 0)
            this.cancelJump();
    }

    public void onDownKeyHeld()
    {
        Entity ladder = this.getCollisionWith(CollisionRect.VERTICAL, "ladder");

        if (ladder != null)
        {
            if (Keyboard.check(this.getFireKey()))
                return;

            if (this.is(PLAYER_CLIMBING))
                this.climbDown(ladder);
        }
    }

    public boolean canShoot()
    {
        return true;
    }

    public void onShoot()
    {
        Sound.play(getFireSound());

        float sx = 0;
        float sy = 0;
        float svx = 0;
        if (!this.is(PLAYER_FORWARD)) sx = this.x - 6;
        if (this.is(PLAYER_FORWARD)) sx = this.x + 14;
        if (!this.is(PLAYER_FALLING)) sy = this.y + 14 + appearance.getStandingShootHeight();
        if (this.is(PLAYER_FALLING)) sy = this.y + 4 + appearance.getStandingShootHeight();
        if (this.is(PLAYER_CLIMBING)) sy = this.y + 10;

        svx = this.is(PLAYER_FORWARD)?
            this.appearance.getWeaponProjectileType().getSpeed() :
            -this.appearance.getWeaponProjectileType().getSpeed();

        final float fsx = sx;
        final float fsy = sy;
        final float fsvx = svx;

        WeaponProjectile shot =
            (WeaponProjectile) this.spawn(new WeaponProjectile()
            {
                @Override
                public void onCreate()
                {
                    super.onCreate();
                    this.x = fsx;
                    this.y = fsy;
                    this.vel_x = fsvx;
                    this.row = PlayerEntity.this.appearance.getWeaponProjectileType().getRow();
                }
            });

        if (NetworkClient.getInstance() != null)
        {
            NetworkClient
                .getInstance()
                .send(
                    "?",
                    "shoot",
                    (int) Math.round(sx) + "",
                    (int) Math.round(sy) + "",
                    (int) Math.round(svx) + "");
        }
    }

    public void onFireKeyHeld()
    {
        if (!this.canShoot())
            return;

        this.set(PLAYER_SHOOTING, true);
        this.timers.put("shooting", 15);

        if (Keyboard.await(this.getFireKey()))
            this.onShoot();
        
        this.timers.put("updating", 3);
    }

    public void handleEarlyInput()
    {
        if (!this.checkInput())
            return;

        if (Keyboard.check(this.getLeftKey()))
            this.moveLeft();

        if (Keyboard.check(this.getRightKey()))
            this.moveRight();
    }

    public void handleLateInput()
    {
    	if (!this.checkInput())
            return;
    	
        if (Keyboard.check(this.getUpKey()))
                this.onUpKeyHeld();
        else    this.onUpKeyNotHeld();

        if (Keyboard.check(this.getDownKey()))
            this.onDownKeyHeld();

        if (Keyboard.check(this.getFireKey()))
            this.onFireKeyHeld();
        
        if (this.is(PLAYER_CLIMBING))
        	if (Keyboard.await(this.getDetachKey()))
        		this.set(PLAYER_CLIMBING, false);
    }

    public boolean onHorizontalWallCollision(Entity e)
    {
        if (e.x > this.x)
        {
            this.x = e.x - this.width;
            this.vel_x = 0;
            return true;
        }

        if (e.x < this.x)
        {
            this.x = e.x + e.width;
            this.vel_x = 0;
            return true;
        }

        return false;
    }

    public boolean onVerticalWallCollision(Entity e)
    {
        if (e.y > this.y)
        {
            if (this.is(PLAYER_FALLING) && this.vel_y > 2)
                Sound.play(this.getLandingSound());

            this.y = e.y - this.height + 1;
            this.vel_y = 0;

            this.set(PLAYER_FALLING, false);
            this.set(PLAYER_CLIMBING, false);
            return true;
        }

        if (e.y < this.y)
        {
            this.y = e.y + e.height;
            this.vel_y = 0;
            return true;
        }

        return false;
    }

    public void onHazardCollision(Entity e)
    {

    }

    public void handlePhysics()
    {
        if (this.vel_x < -4) this.vel_x = -4;
        if (this.vel_x > 4) this.vel_x = 4;

        this.vel_x *= 0.67f;

        if (!this.is(PLAYER_CLIMBING))
            this.vel_y += 1.65;

        if (this.vel_y > 14)
            this.vel_y = 14;

        for (Entity e : this.getCollisionsWithTag(CollisionRect.VERTICAL, "solid"))
        {
            if (this.onVerticalWallCollision(e))
                break;
        }

        for (Entity e : this.getCollisionsWithTag(CollisionRect.HORIZONTAL, "solid"))
        {
            if (this.onHorizontalWallCollision(e))
                break;
        }

        if (this.getCollisionWith(CollisionRect.VERTICAL, "ladder") != null)
        {
            if (this.is(PLAYER_CLIMBING))
            {
                this.vel_x = 0;
                this.vel_y *= 0.10;
            }
        }
        else
        this.set(PLAYER_CLIMBING, false);

        if (!this.is(PLAYER_HURTING))
        {
            Entity hazard = this.getCollisionWith(CollisionRect.VERTICAL, "hazard");
            if (hazard != null)
            {
                this.onHazardCollision(hazard);
                this.set(PLAYER_HURTING, true);
                this.timers.put("hurting", 30 * 2);
            }
        }
    }

    @Override
    public void onStep()
    {
        this.handleState();
        this.handleEarlyInput();
        this.handlePhysics();
        this.handleLateInput();
    }

    public void setAppearance(int index)
    {
        appearance_index = index;
        appearance = PlayerAppearance.values()[appearance_index];
    }

    public void onPlayerDeath()
    {

    }

    @Override
    public void onDraw(Graphics2D g)
    {
        if (!this.is(PLAYER_VISIBLE) || this.is(PLAYER_DEAD))
            return;

        //
        //  Draw the actual sprite
        //

        if (!this.is(PLAYER_FALLING))
        {
            if (this.is(PLAYER_RUNNING))
            {
                this.sprite_row = 2 + (this.is(PLAYER_SHOOTING)? 1 : 0);
                this.sprite_col = this.frame;
            }
            else
            {
                this.sprite_row = (this.is(PLAYER_SHOOTING)? 1 : 0);
                this.sprite_col = 0;
            }
        }
        else
        {
            this.sprite_row = (this.is(PLAYER_SHOOTING)? 1 : 0);
            this.sprite_col = 1;
        }

        if (this.is(PLAYER_CLIMBING))
        {
            this.sprite_row = 4;
            this.sprite_col = (((int)this.y) % 2 == 0)? 0 : 1;

            if (this.is(PLAYER_SHOOTING))
            {
                this.sprite_row = 1;
                this.sprite_col = 2;
            }
        }
        
        sprite = Image.get(
            this.getSpriteSheet(),
            32,
            32,
            this.sprite_row,
            this.sprite_col);

        //
        //  When we are damaged, we can skip drawing
        //  Every other frame for a blinking effect.
        //
        if (this.is(PLAYER_HURTING))
        {
            if (this.age % 2 == 0)
                return;
        }

        if (!this.is(PLAYER_FORWARD))
            this.sprite = GFX.flipHorizontally(sprite);

        //
        //  After all that logic, we are only drawing to the
        //  Screen once. This also ensures ONE Mega Man is drawn.
        //
        g.drawImage(
            this.sprite,
            (int) this.x - 8,
            (int) this.y - 1,
            null,
            null);

        if (this instanceof NetworkPlayer)
        {
            GFX.drawText(
                g,
                (int) this.x + 8,
                (int) this.y + 32,
                this.name,
                10,
                true,
                false);
        }
    }
}

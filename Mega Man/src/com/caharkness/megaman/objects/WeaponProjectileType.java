package com.caharkness.megaman.objects;

public enum WeaponProjectileType
{
    MEGA_BUSTER(0f, 6f, 0, 0),
    MEGA_BUSTER_CHARGED(0f, 6f, 0, 0),
    BUSTER_GUN(0f, 6f, 0, 0);

    private float spin;
    private float speed;
    private int row;
    private int animation_speed;

    private WeaponProjectileType(float spin, float speed, int row, int animation_speed)
    {
        this.spin = spin;
        this.speed = speed;
        this.row = row;
        this.animation_speed = animation_speed;
    }

    public float getSpin()
    {
        return this.spin;
    }

    public float getSpeed()
    {
        return this.speed;
    }

    public int getRow()
    {
        return this.row;
    }

    public int getAnimationSpeed()
    {
        return this.animation_speed;
    }
}

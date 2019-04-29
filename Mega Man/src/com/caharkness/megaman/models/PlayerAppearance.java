package com.caharkness.megaman.models;

import com.caharkness.megaman.objects.WeaponProjectileType;

public enum PlayerAppearance
{
    CLASSIC_MEGA_MAN(
        "/sprites/spr_player_mega_man.png",
        "/sounds/snd_player_mega_man_shoot.wav",
        null,
        "/sounds/snd_player_mega_man_land.wav",
        0,
        0,
        WeaponProjectileType.MEGA_BUSTER),

    MEGA_MAN_X(
        "/sprites/spr_player_mega_man_x.png",
        "/sounds/snd_player_mega_man_shoot.wav",
        null,
        "/sounds/snd_player_mega_man_land.wav",
        0,
        0,
        WeaponProjectileType.MEGA_BUSTER),

    MEGA_MAN_TRIGGER(
        "/sprites/spr_player_mega_man_trigger.png",
        "/sounds/snd_player_mega_man_shoot.wav",
        null,
        "/sounds/snd_player_mega_man_land.wav",
        0,
        0,
        WeaponProjectileType.MEGA_BUSTER),

    PROTO_MAN(
        "/sprites/spr_player_proto_man.png",
        "/sounds/snd_player_mega_man_shoot.wav",
        null,
        "/sounds/snd_player_mega_man_land.wav",
        3,
        3,
        WeaponProjectileType.MEGA_BUSTER),

    PROTO_MAN_MODIFIED(
        "/sprites/spr_player_proto_man_modified.png",
        "/sounds/snd_player_mega_man_shoot.wav",
        null,
        "/sounds/snd_player_mega_man_land.wav",
        3,
        3,
        WeaponProjectileType.MEGA_BUSTER),

    CLASSIC_ZERO(
        "/sprites/spr_player_zero_red.png",
        "/sounds/snd_player_mega_man_shoot.wav",
        null,
        "/sounds/snd_player_mega_man_land.wav",
        0,
        0,
        WeaponProjectileType.MEGA_BUSTER),

    ZERO(
        "/sprites/spr_player_zero.png",
        "/sounds/snd_player_mega_man_shoot.wav",
        null,
        "/sounds/snd_player_mega_man_land.wav",
        0,
        0,
        WeaponProjectileType.MEGA_BUSTER),

    ROLL(
        "/sprites/spr_player_roll.png",
        "/sounds/snd_player_mega_man_shoot.wav",
        null,
        "/sounds/snd_player_mega_man_land.wav",
        0,
        0,
        WeaponProjectileType.MEGA_BUSTER);

    //
    //
    //
    //
    //

    private String sprite_sheet;
    private String fire_sound;
    private String jump_sound;
    private String land_sound;
    private int standing_shoot_height;
    private int falling_shoot_height;
    private WeaponProjectileType wp_type;

    private PlayerAppearance(
        String sprite_sheet,
        String fire_sound,
        String jump_sound,
        String land_sound,
        int standing_shoot_height,
        int falling_shoot_height,
        WeaponProjectileType wp_type)
    {
        this.sprite_sheet = sprite_sheet;
        this.fire_sound = fire_sound;
        this.jump_sound = jump_sound;
        this.land_sound = land_sound;
        this.standing_shoot_height = standing_shoot_height;
        this.falling_shoot_height = falling_shoot_height;
        this.wp_type = wp_type;
    }

    public String getSpriteSheet()
    {
        return this.sprite_sheet;
    }

    public String getFireSound()
    {
        return this.fire_sound;
    }

    public String getJumpSound()
    {
        return this.jump_sound;
    }

    public String getLandSound()
    {
        return this.land_sound;
    }

    public int getStandingShootHeight()
    {
        return this.standing_shoot_height;
    }

    public int getFallingShootHeight()
    {
        return this.falling_shoot_height;
    }

    public WeaponProjectileType getWeaponProjectileType()
    {
        return this.wp_type;
    }
}

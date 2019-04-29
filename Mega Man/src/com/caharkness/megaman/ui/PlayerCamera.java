package com.caharkness.megaman.ui;

import com.caharkness.engine.Entity;
import com.caharkness.megaman.models.PlayerEntity;

public class PlayerCamera extends Entity
{
    @Override
    public void onStep()
    {
        if (this.parent instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) this.parent;
            if (player.is(PlayerEntity.PLAYER_VISIBLE) || !player.is(PlayerEntity.PLAYER_DEAD))
            {
                this.x = player.x;
                this.y = player.y;
            }
        }

        this.engine
            .moveCamera(
                this.getCenterX(),
                this.getCenterY());
    }
}

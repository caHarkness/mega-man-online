package com.caharkness.megaman.objects;


import com.caharkness.megaman.models.PlayerEntity;

public class NetworkPlayer extends PlayerEntity
{
    @Override
    public boolean checkInput()
    {
        return false;
    }
    
    @Override
    public void onStep()
    {
    	
    }
}

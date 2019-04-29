package com.caharkness.megaman;

import java.awt.Dimension;

import com.caharkness.engine.Engine;
import com.caharkness.megaman.controllers.MapEditor;
import com.caharkness.megaman.controllers.NetworkClient;
import com.caharkness.megaman.controllers.NetworkServer;
import com.caharkness.megaman.models.MapEntity;
import com.caharkness.megaman.models.PlayerEntity;
import com.caharkness.megaman.models.TileBehavior;
import com.caharkness.megaman.objects.GenericMenu;
import com.caharkness.megaman.ui.PlayerCamera;
import com.caharkness.megaman.ui.TextRoll;

public class Client
{
	@SuppressWarnings("serial")
    public static void main(String[] parameters)
	{
		Engine e = new Engine(256, 240)
		{{
		    final Engine engine = this;

		    this.setPreferredSize(new Dimension(256 * 2, 240 * 2));
		    this.window.pack();

		    GenericMenu menu = new GenericMenu()
            {{
                final GenericMenu menu = this;

                this.x = 8;
                this.y = 8;

                this.addItem("Host & Join localhost", new Runnable()
                {
                    @Override
                    public void run()
                    {
                        engine.spawn(new NetworkServer()
                        {
                            @Override
                            public void onCreate()
                            {
                                System.out.println("Starting server...");
                                super.onCreate();

                                for (int y = 0; y < 32; y++)
                                    for (int x = 0; x < 32; x++)
                                    {
                                        if (y == 31)
                                        {
                                            this.map.setTileInMemory(
                                                x,
                                                y,
                                                "/sprites/spr_tileset_fire_man.png",
                                                0,
                                                0,
                                                TileBehavior.SOLID);
                                        }
                                    }
                            }
                        });

                        engine.spawn(new NetworkClient()
                        {
                            @Override
                            public void onCreate()
                            {
                                System.out.println("Starting client...");
                                super.onCreate();

                            }
                        });
                    }
                });

                this.addItem("Join localhost", new Runnable()
                {
                    @Override
                    public void run()
                    {
                        engine.spawn(new NetworkClient()
                        {
                            @Override
                            public void onCreate()
                            {
                                System.out.println("Starting client...");
                                super.onCreate();
                            }
                        });
                    }
                });
            }};

            TextRoll intro = new TextRoll()
            {{
                this.roll.add("Mega Man and all related\ncontent Copyright\n(c) CAPCOM 2017");
                this.roll.add("A non-profit Java\nExperiment brought to you by");
                this.roll.add("caHarkness");
                this.roll.add("Developed watching\nMega Super Lionheart\nOn YouTube");
                this.roll.add("Presenting");
                this.roll.add("MEGA MAN ONLINE!\nEarly Alpha 0.0.1a");
                this.after = menu;
            }};

            this.spawn(intro);
		}};
	}
}
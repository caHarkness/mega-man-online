package com.caharkness.megaman.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

import com.caharkness.engine.Entity;
import com.caharkness.engine.Keyboard;
import com.caharkness.megaman.models.MapEntity;
import com.caharkness.megaman.models.PlayerEntity;
import com.caharkness.megaman.models.TileBehavior;
import com.caharkness.megaman.objects.NetworkPlayer;
import com.caharkness.megaman.packets.ChatPacket;
import com.caharkness.megaman.packets.MapPacket;
import com.caharkness.megaman.packets.MarcoPacket;
import com.caharkness.megaman.packets.Packet;
import com.caharkness.megaman.packets.PacketType;
import com.caharkness.megaman.packets.PoloPacket;
import com.caharkness.megaman.packets.ShootPacket;
import com.caharkness.megaman.packets.SyncPacket;
import com.caharkness.megaman.packets.TilePacket;
import com.caharkness.megaman.ui.ChatLog;
import com.caharkness.megaman.ui.ChatStrip;
import com.caharkness.megaman.ui.MessageDialog;
import com.caharkness.megaman.ui.PlayerCamera;
import com.caharkness.megaman.ui.QuestionDialog;

public class NetworkClient extends Entity
{
    private static NetworkClient self;

    public static NetworkClient getInstance()
    {
        return self;
    }

    //
    //
    //
    //
    //
    //
    //

    String name;
    PlayerEntity me;
    ChatLog log;
    ChatStrip input;
    MapEntity map;
    HashMap<String, NetworkPlayer> players;

    Socket socket;
    InetAddress address;
    Integer port;
    PrintWriter writer;
    BufferedReader reader;
    Thread thread;

    public String connect_to_host = "127.0.0.1";
    public int connect_to_port = 4004;

    /**
     *  Write a message to the output stream for
     *  The server to read.
     */
    public void send(String data)
    {
        try
        {
            this.writer.println(data);
            this.writer.flush();
        }
        catch (Exception x) {}
    }

    /**
     *  Convenience method for sending a packet object.
     */
    public void send(Packet packet)
    {
        this.send(packet.getSerialized());
    }

    /**
     *  Convenience method for sending more than one string.
     *  This will get converted into a packet before sending.
     */
    public void send(String... data)
    {
        this.send(new Packet(data));
    }

    /**
     *  Read the next line available from the input stream.
     *  If none is available, the execution will pause until
     *  More data is available.
     */
    public String receive()
    {
        try
        {
            return
            this.reader.readLine();
        }
        catch (IOException x)
        {
            x.printStackTrace();
            return null;
        }
    }

    public void onRecieveMessage(String data)
    {
        try
        {
            Packet packet   = new Packet(data);
            PacketType type = PacketType.fromPacket(packet);

            switch (type)
            {
                case PLAYER:
                    {
                        NetworkPlayer
                            p = this.getPlayerById(packet.getId());
                            p.updateFromPacket(packet.resolve());
                    }
                    break;

                case SHOOT:
                    {
                        ShootPacket s = packet.resolve();

                        this.spawn(s.getResultingEntity());

                        NetworkPlayer
                            p = this.getPlayerById(packet.getId());
                            p.timers.put("buster", 15);
                    }
                    break;

                case TILE:
                    {
                        TilePacket t = packet.resolve();

                        this.map.setTileRemote(
                            t.getTileX(),
                            t.getTileY(),
                            t.getTileset(),
                            t.getRow(),
                            t.getColumn(),
                            t.getBehavior());
                    }
                    break;

                case MAP:
                    {
                        MapPacket
                            p = packet.resolve();
                            this.map.setTiles(p.getTilePackets());

                        this.map.constructLevel();
                    }
                    break;

                case CHAT:
                    {
                        ChatPacket c = packet.resolve();

                        NetworkPlayer p =
                            this.getPlayerById(packet.getId());
                            this.log.println(
                                String.format(
                                    "%s: %s",
                                    p.name,
                                    c.getMessage()));
                    }
                    break;

                case DISCONNECT:
                    {
                        NetworkPlayer
                            p = this.getPlayerById(packet.getId());
                            p.destroy();
                    }
                    break;

                case MARCO:
                    {
                        MarcoPacket ma = new MarcoPacket(packet);

                        NetworkPlayer
                            p = this.getPlayerById(ma.getId());
                            p.name = ma.getName();
                            p.setAppearance(ma.getAppearanceIndex());

                        PoloPacket po = new PoloPacket(
                            new Packet(
                                "?",
                                "polo",
                                this.me.name,
                                this.me.appearance_index + ""));

                        this.send(po);
                    }
                    break;

                case POLO:
                    {
                        PoloPacket po = new PoloPacket(packet);

                        NetworkPlayer
                            p = this.getPlayerById(po.getId());
                            p.name = po.getName();
                            p.setAppearance(po.getAppearanceIndex());
                    }
                    break;

                default:
                    break;
            }

        }
        catch (Exception x) { x.printStackTrace(); }
    }

    public void connect(String host, int port)
    {
        try
        {
            this.socket = new Socket(host, port);
            this.writer = new PrintWriter(this.socket.getOutputStream(), true);
            this.reader = new BufferedReader(
                new InputStreamReader(
                    this.socket.getInputStream()));
        }
        catch (Exception x)
            { x.printStackTrace(); }
    }

    public void listen()
    {
        //
        //  Create the reading loop so our game isn't
        //  Throttled by network requests.
        //
        this.thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (!NetworkClient.this.destroyed)
                {
                    try
                    {
                        //
                        //  The thread spends most of its time here waiting
                        //  Until the server commits a message ending in a line feed.
                        //
                        String input =
                            NetworkClient.this.receive();

                        NetworkClient
                            .this
                            .onRecieveMessage(input);

                        Thread.sleep(10);
                    }
                    catch (Exception x)
                        { break; }
                }

                try
                {
                    writer.close();
                    reader.close();
                }
                catch (Exception x) {}

                NetworkClient
                    .this
                    .spawn(new MessageDialog("You have been disconnected from the game server"));
            }
        }, "Network Client Receiver");
        this.thread.start();
    }

    public void handshake()
    {
        this.send(
            new SyncPacket());
    }

    public void init()
    {
        this.connect(
            this.connect_to_host,
            this.connect_to_port);
        this.listen();
        this.handshake();
    }

    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //

    public NetworkPlayer getPlayerById(String id)
    {
        if (this.players == null)
            this.players = new HashMap<>();

        NetworkPlayer player;

        if (!this.players.containsKey(id))
        {
            player = (NetworkPlayer)
                this.spawn(new NetworkPlayer());
                this.players.put(id, player);
        }
        else player = players.get(id);
        return player;
    }

    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //

    @Override
    public void onCreate()
    {
        self = this;

        //
        //  Create and manage a custom MapEntity for
        //  Handling the client-side map.
        //
        this.map = (MapEntity) this.spawn(new MapEntity()
        {
            @Override
            public void setTileLocal(Integer tx, Integer ty, String ts, Integer ro, Integer co, TileBehavior be)
            {
                super.setTileLocal(tx, ty, ts, ro, co, be);

                TilePacket t =
                TilePacket.fromString(
                    "?",
                    "tile",
                    tx + "",
                    ty + "",
                    ts,
                    ro + "",
                    co + "",
                    be.toString());

                NetworkClient.this.send(t);
            }
        });

        //
        //  Ask for the player's name!
        //
        this.spawn(new QuestionDialog("What is your name?")
        {
            @Override
            public void onEnterKeyPressed()
            {
                NetworkClient.this.name =
                    this.buffer;
                    this.destroy();

                Keyboard.clearFocus();
                NetworkClient
                    .this
                    .onCreateContinued();
            }
        });

        this.placement = Placement.IN_FOREGROUND;
    }

    public void onCreateContinued()
    {
        this.me = (PlayerEntity) this.spawn(new PlayerEntity());
        this.me.spawn(new PlayerCamera());
        this.me.name = this.name;

        this.log = (ChatLog) this.spawn(new ChatLog());
        this.me.spawn(new MapEditor());
        this.me.spawn(new ChatStrip()
        {
            @Override
            public void onStep()
            {
                super.onStep();

                if (this.open)
                    NetworkClient.this.log.show();
            }

            @Override
            public void onEnterKeyPressed()
            {
                if (this.buffer.length() < 1)
                    return;

                NetworkClient.this.log.println(
                    NetworkClient.this.name +
                    ": " +
                    this.buffer);

                NetworkClient
                    .this
                    .send(
                        "?",
                        "chat",
                        this.buffer);

                this.buffer = "";
                this.open = false;
                Keyboard.clearFocus();
            }
        });

        //
        //  Keep all the core network stuff away from the
        //  Game engine — down here is the game content.
        //
        this.init();
        this.sendMarco();
    }

    public void sendMarco()
    {
        MarcoPacket ma = new MarcoPacket(
            new Packet(
                "?",
                "marco",
                this.me.name,
                this.me.appearance_index + ""));

        this.send(ma);
    }

    @Override
    public void onStep()
    {
        if (this.me != null)
        if (!this.timers.containsKey("net"))
        {
            if (this.me.timers.containsKey("moving") || this.me.timers.containsKey("updating"))
                NetworkClient
                    .getInstance()
                    .send(this.me.getPacket());

            this.timers.put("net", 1);
        }
    }
}

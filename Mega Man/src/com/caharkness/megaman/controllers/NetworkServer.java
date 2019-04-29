package com.caharkness.megaman.controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.caharkness.engine.Entity;
import com.caharkness.megaman.models.MapEntity;
import com.caharkness.megaman.packets.MapPacket;
import com.caharkness.megaman.packets.Packet;
import com.caharkness.megaman.packets.PacketType;
import com.caharkness.megaman.packets.TilePacket;
import com.caharkness.megaman.util.Network;

public class NetworkServer extends Entity implements Runnable
{
    private static NetworkServer self;

    public static NetworkServer getInstance()
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

    int tcp_rx = 0;
    int tcp_tx = 0;

    public ServerSocket server;
    public Boolean accepting;
    public ArrayList<ActiveClient> clients;
    public HashMap<String, ArrayList<String>> outbox;
    public Thread thread;
    public MapEntity map;

    public void log(String line)
    {
        String time =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date());

        String msg = String.format(
                "[%s] %s",
                time,
                line);

        System.out.println(msg);
    }

    @Override
    public void onCreate()
    {
        self = this;
        this.map = (MapEntity) this.spawn(new MapEntity());

        try
        {
            this.server     = new ServerSocket(4004);
            this.accepting  = true;
            this.clients    = new ArrayList<>();
            this.outbox     = new HashMap<>();
            this.thread     = new Thread(this, "Network Server Connection Negotiator");
            this.thread.start();
        }
        catch (Exception x) {}
    }

    @Override
    public void run()
    {
        log("Accepting TCP connections...");
        while (this.accepting)
        {
            Socket socket;
            try
            {
                //
                //  This particular thread spends most of its time waiting
                //  For a new connection. As soon as `server.accept()` returns,
                //  A new thread is run to keep the TCP connection alive.
                //
                socket = this.server.accept();
                this.clients.add(new ActiveClient(socket));
            }
            catch (Exception x)
                { x.printStackTrace(); }
        }
    }

    public ActiveClient getClientById(String id)
    {
        for (ActiveClient client : this.clients)
            if (client.id.equalsIgnoreCase(id))
                return client;

        return null;
    }

    public void broadcast(String... data)
    {
        Packet packet = new Packet(data);

        int count = 0;
        for (ActiveClient client : clients)
            if (!client.id.equalsIgnoreCase(packet.getId()))
            {
                client.send(packet.getSerialized());
                count++;
            }

        if (count < 1)
            return;

        NetworkServer
            .this
            .log(String.format(
                "[Server] >> %sx\tout\t%s", count, packet.getSerialized()));
    }

    public void onCommand(String... data)
    {
        try
        {
            Packet packet   = new Packet(data);
            PacketType type = PacketType.fromPacket(packet);

            NetworkServer
                .this
                .log(String.format(
                    "[Server] << 1x\tin\t%s", packet.getSerialized()));

            switch (type)
            {
                case TILE:
                    {
                        TilePacket tp = packet.resolve();

                        this.map.setTileInMemory(
                            tp.getTileX(),
                            tp.getTileY(),
                            tp.getTileset(),
                            tp.getRow(),
                            tp.getColumn(),
                            tp.getBehavior());

                        this.broadcast(data);
                    }
                    break;

                case SYNC:
                    {
                        MapPacket m = new MapPacket(map);

                        ActiveClient
                            client = this.getClientById(packet.getId());
                            client.send(m.getSerialized());
                    }
                    break;

                //
                //  By default, forward the packet to
                //  Every connected client.
                //
                default:
                    this.broadcast(data);
                    break;
            }
        }
        catch (Exception x)
            { x.printStackTrace(); }
    }

    public class ActiveClient implements Runnable
    {
        Socket socket;
        Thread thread;
        InetAddress address;
        Integer port;
        String id;

        BufferedReader reader;
        PrintWriter writer;
        boolean open;

        public ActiveClient(Socket s)
        {
            this.socket     = s;
            this.address    = socket.getInetAddress();
            this.port       = socket.getPort();
            this.id         = String.format(
                "%s:%s",
                address.getHostAddress(),
                port);

            this.thread =
            new Thread(
                this,
                String.format(
                    "Network Server TCP Keep-alive for %s",
                    this.id));

            this.thread.start();
        }

        /**
         *  Send a string of text to the connected client
         *  This method belongs to.
         */
        public void send(String data)
        {
            this.writer.println(data);
            this.writer.flush();

            int bytes = data.getBytes().length;
            NetworkServer.this.tcp_tx += bytes;

            /*
            NetworkServer
                .this
                .log("[Server] >>  wrote " + bytes + " bytes to " + this.id);
            */
        }

        @Override
        public void run()
        {
            if (!outbox.containsKey(this.id))
            {
                NetworkServer
                    .this
                    .log(String.format("New connection from %s!", this.id));
                outbox.put(this.id, new ArrayList<String>());
            }

            try
            {
                this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                this.writer = new PrintWriter(this.socket.getOutputStream(), true);
                this.open = true;

                while (this.open)
                {
                    try
                    {
                        String line     = reader.readLine();
                        String[] data   = Network.deserializePacket(line);
                        data[0]         = this.id;

                        NetworkServer
                            .this
                            .tcp_rx += line.getBytes().length;

                        NetworkServer
                            .this
                            .onCommand(data);
                    }
                    catch (Exception x)
                        { break; }
                }

                this.reader.close();
                this.writer.close();

                NetworkServer
                    .this
                    .clients
                    .remove(this);

                NetworkServer.this.broadcast(this.id, "disconnect");

                NetworkServer
                    .this
                    .log(String.format("%s disconnected!", this.id));
            }
            catch (Exception x) {}
        }
    }
}

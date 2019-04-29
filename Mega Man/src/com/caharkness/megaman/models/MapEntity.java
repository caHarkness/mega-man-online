package com.caharkness.megaman.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.caharkness.engine.Entity;
import com.caharkness.megaman.packets.TilePacket;
import com.caharkness.megaman.util.Network;

public class MapEntity extends Entity
{
    /*
    private static MapEntity self;
    public static MapEntity getInstance()
    {
        if (self == null)
            throw new RuntimeException("The map controller can only be used when an instance of it exists!");

        return self;
    }
    */

    //
    //
    //
    //
    //

    private HashMap<String, TilePacket> map = new HashMap<>();
    private HashMap<String, TileEntity> tiles = new HashMap<>();

    public void setTile(Integer tx, Integer ty, String ts, Integer ro, Integer co, TileBehavior be, boolean create)
    {
        String key =
            String.format(
                "%s%s%s",
                tx,
                Network.DELIMITER,
                ty);

        TilePacket packet =
            TilePacket.fromString(
                "?",
                "tile",
                tx.toString(),
                ty.toString(),
                ts,
                ro.toString(),
                co.toString(),
                be.toString());

        this.map.put(key, packet);

        if (create)
        {
            TileEntity existing = this.getTileAt(tx, ty);

            if (existing != null)
                existing.destroy();

            if (be != TileBehavior.VOID)
            {
                TileEntity tile = packet.getResultingEntity();

                this.tiles.put(key, tile);
                this.spawn(tile);
            }
            else
            while (map.containsKey(key))
                map.remove(key);
        }
    }

    public void setTileLocal(Integer tx, Integer ty, String ts, Integer ro, Integer co, TileBehavior be)
    {
        setTile(
            tx,
            ty,
            ts,
            ro,
            co,
            be,
            true);
    }

    public void setTileRemote(Integer tx, Integer ty, String ts, Integer ro, Integer co, TileBehavior be)
    {
        setTile(
            tx,
            ty,
            ts,
            ro,
            co,
            be,
            true);
    }

    public void setTileInMemory(Integer tx, Integer ty, String ts, Integer ro, Integer co, TileBehavior be)
    {
        setTile(
            tx,
            ty,
            ts,
            ro,
            co,
            be,
            false);
    }

    public void constructLevel()
    {
        if (this.tiles.size() > 0)
            for (TileEntity e : tiles.values())
                e.destroy();

        this.tiles.clear();
        for (TilePacket packet : map.values())
        {
            String key =
                String.format(
                    "%s%s%s",
                    packet.getTileX(),
                    Network.DELIMITER,
                    packet.getTileY());

            TileEntity
                tile = packet.getResultingEntity();
                tiles.put(key, tile);

            this.spawn(tile);
        }
    }

    @Override
    public void onCreate()
    {
        this.x = 0;
        this.y = 0;
        this.engine.moveCamera(0, 0);
    }

    @Override
    public void onStep()
    {
        int a = 0;
    }

    public TilePacket[] getTilePackets()
    {
        TilePacket[] packets = new TilePacket[map.size()];

        int index = 0;
        for (TilePacket packet : map.values())
        {
            packets[index] = packet;
            index++;
        }

        return packets;
    }

    public ArrayList<TileEntity> getTiles()
    {
        ArrayList<TileEntity>
            list = new ArrayList<>();
            list.addAll(tiles.values());

        return list;
    }

    public TileEntity getTileAt(int tx, int ty)
    {
        String key =
            String.format(
                "%s%s%s",
                tx,
                Network.DELIMITER,
                ty);

        if (this.tiles.containsKey(key))
                return this.tiles.get(key);
        else    return null;
    }

    public void setTiles(TilePacket... packets)
    {
        for (TilePacket packet : packets)
        {
            this.setTileInMemory(
                packet.getTileX(),
                packet.getTileY(),
                packet.getTileset(),
                packet.getRow(),
                packet.getColumn(),
                packet.getBehavior());
        }
    }

    public String serializeMap()
    {
        String output = "";
        for (Entry<String, TilePacket> entry : map.entrySet())
            output += String.format("%s%s", entry.getValue().getSerialized(), "\n");

        output = output.substring(0, output.length() - 1);
        return output;
    }

    //
    //
    //
    //
    //

    public static TilePacket[] deserializeMap(String data)
    {
        String[] instructions = data.split("\n");
        TilePacket[] packets = new TilePacket[instructions.length];

        for (int i = 0; i < instructions.length; i++)
            packets[i] = new TilePacket(instructions[i]);

        return packets;
    }
}

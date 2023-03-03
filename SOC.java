import java.awt.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.*;

public class SOC
{
    /// Don't change these
    public enum resource
    {
        EMPTY, DESERT, SHEEP, ORE, WOOD, WHEAT, BRICK
    }
    public enum buildType
    {
        EMPTY, SETTLEMENT, CITY, ROAD
    }
    public enum location
    {
        N, NE, SE, S, SW, NW
    }

    static class piece 
    {
        Color m_color;
        buildType m_type;
        Polygon m_poly;
        public piece(buildType t, Color c, Point p)
        {
            m_color = c;
            if (t == SOC.buildType.SETTLEMENT)
            {
                int x[] = {p.x, p.x + 16, p.x - 16};
                int y[] = {p.y - 10, p.y +  10, p.y + 10};
                m_poly = new Polygon(x, y, x.length);
            }
            else if (t == SOC.buildType.CITY)
            {
                int x[] = {p.x - 18, p.x + 18, p.x + 18, p.x - 18};
                int y[] = {p.y - 9, p.y - 9, p.y + 9, p.y + 9};
                m_poly = new Polygon(x, y, x.length);
            }
            else if (t == SOC.buildType.ROAD)
            {
                int x[] = {p.x - 5, p.x + 5, p.x + 5, p.x - 5};
                int y[] = {p.y - 5, p.y - 5, p.y + 5, p.y + 5};
                m_poly = new Polygon(x, y, x.length);
            }
            else
            {
                m_poly = new Polygon();
            }

        }

        Polygon shape()
        {
            return m_poly;
        }

        Color color()
        {
            return m_color;
        }
    }
    static class Road extends Junction
    {
        // Special case for roads
        Road(Tile[] tiles, int tile, location loc, location loc2)
        {
            super(tiles, tile, loc);
            address = new int[2];

            int n1[] = neighborhood(tile, loc);
            int n2[] = neighborhood(tile, loc2);
            int idx = 0;
            for(int i = 0;i < n1.length && idx < 2; i++)
                for(int j = 0;j < n2.length && idx < 2; j++)
                    if (n1[i] == n2[j]) address[idx++] = n1[i];
            locations = findLocations(address);
        }

        public boolean canBuild(Player p, boolean build)
        {
            if (!p.canBuild(buildType.ROAD))
                return false;

            // check on either tile
            if (!build && !(m_tiles[address[0]].canBuildRoad(p, locations[0], locations[1])
                ||  m_tiles[address[1]].canBuildRoad(p, locations[2], locations[3])))
                return false;
            //build on both tiles
            if (build)
            {
                m_tiles[address[0]].buildRoad(p, locations[0], locations[1]);
                m_tiles[address[1]].buildRoad(p, locations[2], locations[3]);
                p.build(buildType.ROAD);  // deduct resources
            }
            return true;
        }

        public void draw(Graphics2D g)
        {
            if (owner() != null)
            {
                Point p1 = m_tiles[address[0]].points[locations[0].ordinal()];
                Point p2 = m_tiles[address[0]].points[locations[1].ordinal()];
                g.setColor(owner().getColor());
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        private Player owner() { return m_tiles[address[0]].owner(locations[0], locations[1]);}

        buildType type() { return (owner() == null?buildType.EMPTY:buildType.ROAD);}

        public String toString()
        {
            return "Road: " + address[0] + "," + address[1];
        }
    }

    static class Junction
    {
        int[] address;
        location[] locations;
        protected Tile[] m_tiles;

        //  Three tiles (SETTLEMENT/CITY)
        Junction(Tile[] tiles, int tile, location l)
        {
            m_tiles = tiles;
            address = neighborhood(tile, l);
            locations = findLocations(address);
        }

        public boolean canBuild(Player p)
        {
            return canBuild(p,false);
        }

        public void build(Player p)
        {
            if (canBuild(p))
                canBuild(p,true);
        }

        public boolean canBuild(Player p, boolean build)
        {
            buildType b;

            if (type() == buildType.EMPTY)
                b = buildType.SETTLEMENT;
            else
                b = buildType.CITY;

            if (!p.canBuild(b))
                return false;

            // check/build on all the tiles
            for (int i = 0; i < address.length;i++)
            {
                if (!build && !m_tiles[address[i]].canBuild(p, b, locations[i])) return false;
                if (build)
                    m_tiles[address[i]].build(p, b, locations[i]);
            }
            if (build)
                p.build(b);  // deduct resources
            return true;
        }

        public void draw(Graphics2D g)
        {
            if (type() != buildType.EMPTY)
            {
                Point p = m_tiles[address[0]].points[locations[0].ordinal()];
                g.setColor(owner().getColor());
                g.fillPolygon(new SOC.piece(type(), owner().getColor(), p).shape());
            }
        }

        private Player owner() { return m_tiles[address[0]].owner(locations[0]);}

        buildType type() { return m_tiles[address[0]].junctionType(locations[0]);}
        
        ArrayList<Road> roads()
        {
            ArrayList<Road> ret = new ArrayList<Road>();
            int[] roadTiles = new int[2];
            location[] locations;

            roadTiles[0] = address[0];
            roadTiles[1] = address[1];
            locations = findLocations(roadTiles);
            ret.add(new Road(m_tiles, address[0], locations[0], locations[1]));

            roadTiles[0] = address[0];
            roadTiles[1] = address[2];
            locations = findLocations(roadTiles);
            ret.add(new Road(m_tiles, address[0], locations[0], locations[1]));

            roadTiles[0] = address[1];
            roadTiles[1] = address[2];
            locations = findLocations(roadTiles);
            ret.add(new Road(m_tiles, address[0], locations[0], locations[1]));
            
            return ret;
        }

        public String toString()
        {
            return "Junction: " + address[0] + "," + address[1] + "," + address[2];
        }
    }

    public static SOC.location[] findLocations(int[] tiles)
    {
        if (tiles.length == 3)  // Settlement or City
        {
            if(tiles[1] - tiles[0] == 1)
                return new SOC.location[] {SOC.location.SE, SOC.location.SW ,SOC.location.N};    
            return new SOC.location[] {SOC.location.S, SOC.location.NE ,SOC.location.NW};
        }
        if (tiles.length ==2)  // road
        {
            if(tiles[1] - tiles[0] == 1) //adjacent tiles
                return new SOC.location[] {SOC.location.NE, SOC.location.SE, SOC.location.NW, SOC.location.SW};

            SOC.location left[] = new SOC.location[] { SOC.location.S, SOC.location.SW,SOC.location.N, SOC.location.NE};//down right
            SOC.location right[] = new SOC.location[] { SOC.location.S, SOC.location.SE,SOC.location.N, SOC.location.NW}; // down left

            if (tiles[0] < 4) return (tiles[0] % 2 == tiles[1] %2?left:right);
            if (tiles[0] < 9) return (tiles[0] % 2 == tiles[1] %2?right:left);
            if (tiles[0] < 15) return (tiles[0] % 2 == tiles[1] %2?left:right);
            if (tiles[0] < 22) return (tiles[0] % 2 == tiles[1] %2?left:right);
            if (tiles[0] < 28) return (tiles[0] % 2 == tiles[1] %2?right:left);
            if (tiles[0] < 33) return (tiles[0] % 2 == tiles[1] %2?left:right);

            return left;
        }
        return null;
    }

    public static int findTile(int r, int c)
    {
        int address = 0;
        for (int row = 0; row < 7; row++) 
        {
            int cols = 7 - java.lang.Math.abs(row - 3);

            for (int col = 0; col < cols; col++) 
            {
                if(r == row && c == col)
                    return address;
                address++;
            }
        }
        return address;
    }

    public static int[] neighborhood(int tile, SOC.location loc)
    {
        int address = 0;
        for (int row = 0; row < 7; row++) {
            int cols = 7 - java.lang.Math.abs(row - 3);

            for (int col = 0; col < cols; col++) 
            {
                int n = 0;
                if(address == tile)
                {
                    if(tile < 15 ||(tile < 22 && (loc.ordinal() == 5 ||loc.ordinal() == 0 ||loc.ordinal() == 1)))
                        n = 0; //top half
                    else
                        n = 1;  //add one on bottom half

                    switch (loc)
                    {
                        case N:
                        return new int[] {findTile(row - 1, col - 1 + n),findTile(row - 1, col + n), tile};
                        case NW:
                        return new int[] {findTile(row - 1, col - 1 + n),findTile(row, col - 1), tile};
                        case NE:
                        return new int[] {findTile(row - 1, col + n), tile, findTile(row, col + 1)}; 
                        case S:
                        return new int[] {tile, findTile(row + 1, col - n), findTile(row + 1, col + 1 - n)}; 
                        case SW:
                        return new int[] {findTile(row, col - 1), tile, findTile(row + 1, col - n)}; 
                        case SE:
                        return new int[] {tile, findTile(row, col + 1),findTile(row + 1, col + 1 - n)}; 
                    }
                }
                address++;
            }
        }
        return null;
    }

}

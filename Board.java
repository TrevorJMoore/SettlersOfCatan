import java.util.*;
import java.awt.*;

public class Board implements BoardInterface
{
    private Player[] m_players = new Player[4];
    private PlayerStrategy[] m_strategies = new PlayerStrategy[4];
    private Tile[] m_tiles = new Tile[37];

    private ArrayList<SOC.Junction> m_junctions = new ArrayList<SOC.Junction>();
    private ArrayList<SOC.Road> m_roads = new ArrayList<SOC.Road>();
    private int m_currentPlayer;
    private int m_turn = 1;
    private SOC.Junction m_lastJunction;

    int m_wood = 4;
    int m_wheat = 4;
    int m_desert = 1;
    int m_sheep = 4;
    int m_ore = 3;
    int m_brick = 3;
    int num2 = 1;
    int num3 = 2;
    int num4 = 2;
    int num5 = 2;
    int num6 = 2;
    int num8 = 2;
    int num9 = 2;
    int num10 = 2;
    int num11 = 2;
    int num12 = 1;
    int WIDTH;
    int HEIGHT;
    int numPlayers;
    Board(int w, int h) 
    {
        WIDTH = w;
        HEIGHT = h;
        setupGrid();
    }

    // BoardInterface
    public ArrayList<SOC.Junction> allJunctions() { return m_junctions;};

    public ArrayList<SOC.Road> allRoads() { return m_roads;};

    public ArrayList<SOC.Junction> availableJunctions() 
    {
        ArrayList<SOC.Junction> ret = new ArrayList<SOC.Junction>();
        for(int i=0; i < m_junctions.size();i++)
        {
            if (m_junctions.get(i).canBuild(m_players[m_currentPlayer]))
                ret.add(m_junctions.get(i));
        }
        return ret;
    };

    public ArrayList<SOC.Road> availableRoads() 
    {
        ArrayList<SOC.Road> ret = new ArrayList<SOC.Road>();
        for(int i=0; i < m_roads.size();i++)
        {
            if (m_turn > 4 && m_turn < 9)// round 2
            {
                return m_lastJunction.roads();
            }

            if (m_roads.get(i).canBuild(m_players[m_currentPlayer]))
                ret.add(m_roads.get(i));
        }
        return ret;
    }

    public void build(SOC.Road r) { r.build(currentPlayer());};

    public void build(SOC.Junction j) { j.build(currentPlayer());};

    public boolean canBuild(SOC.Road r) { return r.canBuild(currentPlayer());};

    public boolean canBuild(SOC.Junction j) { return j.canBuild(currentPlayer());};

    public int tileNumber(int n) {return tiles()[n].number();};

    public SOC.resource tileResource(int n) { return tiles()[n].resource();};

    public String OwnerName(int n, SOC.location loc) { return tiles()[n].owner(loc).getName();};

    public String OwnerName(int n, SOC.location loc, SOC.location loc2) { return tiles()[n].owner(loc, loc2).getName();};

    public boolean trade(SOC.resource r1, SOC.resource r2)
    {
        return currentPlayer().trade(r1, r2);
    }

    public void addPlayer(Player p, PlayerStrategy s)
    {
        if (numPlayers < 4)
        {
            m_strategies[numPlayers] = s;
            addPlayer(p);
        }
    }

    public void addPlayer(Player p)
    {
        if (numPlayers < 4)
            m_players[numPlayers++] = p;
        if (numPlayers == 4)
            makeMove();
    }

    private void makeMove()
    {
        if (m_turn > 4 && m_turn < 9) // round 2
        {
            m_players[m_currentPlayer].collectResources(SOC.resource.WOOD, 2);
            m_players[m_currentPlayer].collectResources(SOC.resource.WHEAT, 1);
            m_players[m_currentPlayer].collectResources(SOC.resource.SHEEP, 1);
            m_players[m_currentPlayer].collectResources(SOC.resource.BRICK, 2);

        }

        PlayerStrategy ps = m_strategies[m_currentPlayer];
        if (ps != null)
        {
            if (m_turn < 5) // round 1
            {
                ps.placeFirstSettlement(this).build(currentPlayer());
                ps.placeFirstRoad(this).build(currentPlayer());
            }
            if (m_turn > 4 && m_turn < 9) // round 2
            {
                m_lastJunction = ps.placeSecondSettlement(this);
                m_lastJunction.build(currentPlayer());
                ps.placeSecondRoad(this).build(currentPlayer());
            }
            if (m_turn > 8)
            {
                ps.takeTurn(this);
            }
            nextPlayer();
        }
    }

    public Player currentPlayer() {return m_players[m_currentPlayer];};

    public void nextPlayer() 
    {
        int direction = (m_turn > 4 && m_turn < 9)?-1:1; // reverse for round 2

        if (numPlayers == 4)
        {
            m_currentPlayer = (m_currentPlayer + direction) % 4;
            m_turn++;
            if (m_turn == 5)
            {
                m_currentPlayer = 3;
                for (Tile t : m_tiles)
                    t.giveCards();
            }
            if (m_turn > 8)
            {
                int temp = (int) (Math.floor(Math.random() * 6) + 1) +
                    (int) (Math.floor(Math.random() * 6) + 1);
                System.out.println("Rolled: " + temp);
                for (Tile t : m_tiles)
                {
                    t.roll(temp);
                }
            }
        }

        makeMove();
    }

    private Tile[] tiles() {return m_tiles;};

    private SOC.Junction findJunction(int[] tiles)
    {
        for(SOC.Junction j : m_junctions)
        {
            if (tiles[0] == j.address[0] &&
            tiles[1] == j.address[1] &&
            tiles[2] == j.address[2])
            {
                return j;
            }
        }
        return null;
    }

    public SOC.Junction findRoad(int[] tiles)
    {
        for(SOC.Junction j : m_roads)
        {
            if (tiles[0] == j.address[0] &&
            tiles[1] == j.address[1])
            {
                return j;
            }
        }
        return null;
    }

    public int tileCount(Rectangle r)
    {
        int count = 0;
        for (int i = 0;i < tiles().length;i++)
        {
            if (tiles()[i].intersects(r))
                count++;
        }
        return count;
    }

    public void build(Rectangle r)
    {
        int[] tiles = new int[3];
        int count = 0;

        // Figure out tiles that were picked
        for (int i = 0;i < tiles().length;i++)
        {
            if (tiles()[i].intersects(r))
                tiles[count++] = i;
        }
        build(tiles);
    }

    public void build(int[] tiles) 
    {
        if (tiles[2] == 0)
            findRoad(tiles).build(currentPlayer());
        else
            findJunction(tiles).build(currentPlayer());
    }

    public void setupGrid()
    {
        int size = 7;
        int radius = 50;
        int padding = 2;
        Point origin = new Point(WIDTH / 2, HEIGHT / 2);
        double ang30 = Math.toRadians(30);
        double xOff = Math.cos(ang30) * (radius + padding);
        double yOff = Math.sin(ang30) * (radius + padding);
        int half = size / 2;
        int tile = 0;
        int number = 0;
        for (int row = 0; row < size; row++) {
            int cols = size - java.lang.Math.abs(row - half);

            for (int col = 0; col < cols; col++) 
            {
                int x = (int) (origin.x + xOff * (col * 2 + 1 - cols));
                int y = (int) (origin.y + yOff * (row - half) * 3);

                SOC.resource res = SOC.resource.EMPTY;
                if (!(row>0 && row < size-1 && col>0 && col < cols-1) )
                    res = SOC.resource.EMPTY;
                else
                {

                    res = randomResource();
                    if(res != SOC.resource.DESERT)
                        number = randomNumber();
                    else
                        number = 0;

                    //  Setup up junctions and roads using tile number addresses
                    if (row > 0 && col > 0 && col < cols-1)
                    {
                        if (col == 1) // get NW location for first tile in each row
                        {
                            m_junctions.add(new SOC.Junction(m_tiles, tile, SOC.location.NW));
                        }
                        m_junctions.add(new SOC.Junction(m_tiles, tile, SOC.location.N));
                        m_junctions.add(new SOC.Junction(m_tiles, tile, SOC.location.NE));
                        if (row ==  size - 2)
                        {
                            m_junctions.add(new SOC.Junction(m_tiles, tile, SOC.location.S));
                            m_junctions.add(new SOC.Junction(m_tiles, tile, SOC.location.SW));
                            if (col == cols - 2)
                            {
                                m_junctions.add(new SOC.Junction(m_tiles, tile, SOC.location.SE));
                            }
                            m_roads.add(new SOC.Road(m_tiles, tile, SOC.location.S, SOC.location.SW));
                            m_roads.add(new SOC.Road(m_tiles, tile, SOC.location.SE, SOC.location.S));
                        }
                        m_roads.add(new SOC.Road(m_tiles, tile, SOC.location.SW, SOC.location.NW));
                        m_roads.add(new SOC.Road(m_tiles, tile, SOC.location.NW, SOC.location.N));
                        m_roads.add(new SOC.Road(m_tiles, tile, SOC.location.N, SOC.location.NE));

                        if (col == cols - 2)
                        {
                            m_roads.add(new SOC.Road(m_tiles, tile, SOC.location.NE, SOC.location.SE));
                        }
                    }
                }

                m_tiles[tile] = new Tile(x, y, res, number);
                tile++;
            }
        }
    }

    public void paintComponent(Graphics g) 
    {
        Graphics2D g2d = (Graphics2D) g;
        Color tmpC = g.getColor();

        // draw tiles
        for (int i=0;i < m_tiles.length; i++)
        {
            m_tiles[i].draw(g2d, 0, true);
        }

        // draw players
        for (int i=0;i < m_players.length;i++)
        {
            m_players[i].draw(g2d); 
        }

        // draw settlements
        for (int i=0;i < m_junctions.size();i++)
        {
            m_junctions.get(i).draw(g2d);
        }

        // draw roads
        for (int i=0;i < m_roads.size();i++)
        {
            m_roads.get(i).draw(g2d);
        }

        g.setColor(tmpC);

    }

    public SOC.resource randomResource()
    {
        int temp = (int)(Math.floor(Math.random() * 6) + 1);
        if(temp == 1 && m_wood > 0)
        {
            m_wood--;
            return SOC.resource.WOOD;
        }
        if(temp == 2 && m_wheat > 0)
        {
            m_wheat--;
            return SOC.resource.WHEAT;
        }
        if(temp == 3 && m_brick > 0)
        {
            m_brick--;
            return SOC.resource.BRICK;
        }
        if(temp == 4 && m_ore > 0)
        {
            m_ore--;
            return SOC.resource.ORE;
        }
        if(temp == 5 && m_sheep > 0)
        {
            m_sheep--;
            return SOC.resource.SHEEP;
        }
        if(temp == 6 && m_desert > 0)
        {
            m_desert--;
            return SOC.resource.DESERT;
        }
        return randomResource();
    }

    public int randomNumber()
    {
        int temp = (int)(Math.floor(Math.random() * 12) + 1);
        if(temp == 2 && num2 >0)
        {
            num2--;
            return 2;
        }
        if(temp == 3 && num3 >0)
        {
            num3--;
            return 3;
        }
        if(temp == 4 && num4 >0)
        {
            num4--;
            return 4;
        }
        if(temp == 5 && num5 >0)
        {
            num5--;
            return 5;
        }
        if(temp == 6 && num6 >0)
        {
            num6--;
            return 6;
        }
        if(temp == 8 && num8 >0)
        {
            num8--;
            return 8;
        }
        if(temp == 9 && num9 >0)
        {
            num9--;
            return 9;
        }
        if(temp == 10 && num10 >0)
        {
            num10--;
            return 10;
        }
        if(temp == 11 && num11 >0)
        {
            num11--;
            return 11;
        }
        if(temp == 12 && num12 >0)
        {
            num12--;
            return 12;
        }
        return randomNumber();
    }
}

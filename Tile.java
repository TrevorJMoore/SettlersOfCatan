import java.util.*;
import java.awt.*;

public class Tile extends TileBase
{
    private int m_number;
    private SOC.resource m_resource;
    private ArrayList<Player> m_givecards;
    private Player[] m_players = new Player[6];
    private SOC.buildType[] m_buildings = new SOC.buildType[6];
    private Player[] m_roads = new Player[6];

    Player owner(SOC.location loc) { return m_players[loc.ordinal()];}; // junction
    Player owner(SOC.location loc1, SOC.location loc2) { return m_roads[roadLocation(loc1, loc2)];}; // road

    SOC.buildType junctionType(SOC.location loc) { return m_buildings[loc.ordinal()];};

    int number() {return m_number;};

    SOC.resource resource() { return m_resource;};

    public Tile(SOC.resource r, int n)
    {
        this(0,0,r,n);
    }

    public Tile(int x, int y, SOC.resource r, int n)
    {
        super(x, y, r, n);
        m_number = n;
        m_resource = r;
        m_givecards = new ArrayList<Player>();
        for(int i = 0;i < m_buildings.length; i++)
            m_buildings[i] = SOC.buildType.EMPTY;
    }

    public boolean canBuild(Player p, SOC.buildType b, SOC.location loc)
    {
        switch(b)
        {
            case CITY:
            if(m_players[loc.ordinal()] ==  p && m_buildings[loc.ordinal()] == SOC.buildType.SETTLEMENT)
                return true;
            break;
            case SETTLEMENT:
            if(m_buildings[loc.ordinal()] == SOC.buildType.EMPTY)
            {

                if(loc.ordinal() == 0 )
                {
                    if(m_buildings[5] == SOC.buildType.EMPTY && m_buildings[1] == SOC.buildType.EMPTY)
                    {
                        return true;
                    }
                }
                else
                {
                    if(loc.ordinal() ==5 && m_buildings[4] == SOC.buildType.EMPTY && m_buildings[0] == SOC.buildType.EMPTY)
                    {
                        return true;
                    }
                    else
                    if(m_buildings[loc.ordinal() -1] == SOC.buildType.EMPTY && m_buildings [loc.ordinal() +1] == SOC.buildType.EMPTY)
                    {
                        return true;
                    }

                }
            }
            break;
        }

        return false;  // can't build it 
    }

    public boolean build(Player p, SOC.buildType b, SOC.location loc)
    {
        if (!canBuild(p, b, loc))
            return false;

        m_buildings[loc.ordinal()] = SOC.buildType.SETTLEMENT;
        m_players[loc.ordinal()] = p; 
        m_givecards.add(p); 
        return true;
    }

    public boolean canBuildRoad(Player p, SOC.location loc, SOC.location loc2)
    {
        int location = roadLocation(loc, loc2);
        if(((m_players[loc.ordinal()] == p || m_players[loc2.ordinal()] == p)||adjacentRoad(p, location)) && m_roads[location] == null)
        {
            if(Math.abs(loc.ordinal() - loc2.ordinal()) ==1)
            {
                return true;
            }
            if((loc.ordinal() == 5 && loc2.ordinal() == 0) ||(loc2.ordinal() == 5 && loc.ordinal() == 0)&& m_roads[location] == null)
            {
                return true;
            }
        }
        return false;
    }

    public int roadLocation( SOC.location loc, SOC.location loc2)
    {
        int location;
        //if is n
        if ((loc.ordinal() == 0 && loc2.ordinal() == 5) ||(loc.ordinal() == 5 && loc2.ordinal() == 0))
            location = 5;
        else if(loc.ordinal() > loc2.ordinal())
        {
            location = loc2.ordinal();
        }
        else
            location = loc.ordinal();
        return location;
    }

    public boolean adjacentRoad(Player p, int location)
    {
        if(location == 0 && (m_roads[5] == p || m_roads[1] == p))
            return true;
        if(location == 5 && (m_roads[0] == p || m_roads[4] == p))
            return true;

        if(location != 0 && location != 5)
            if(m_roads[location - 1] == p || m_roads[location + 1] ==p)
                return true;

        return false;

    }

    public boolean buildRoad(Player p, SOC.location loc, SOC.location loc2)
    {
        m_roads[roadLocation(loc, loc2)] = p;
        return true;
    }

    public void giveCards()
    {
        for(int i = 0; i < m_givecards.size(); i ++)
        {
            m_givecards.get(i).collectResources(m_resource, 1);
        }
    }

    public void roll(int n)
    {
        if(m_number == n)
            giveCards();
    }
}

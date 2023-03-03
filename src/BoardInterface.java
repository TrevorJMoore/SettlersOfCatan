package src;

import java.util.*;

public interface BoardInterface
{
    public ArrayList<SOC.Junction> allJunctions();
    public ArrayList<SOC.Road> allRoads();
    public ArrayList<SOC.Junction> availableJunctions();
    public ArrayList<SOC.Road> availableRoads();
    public void build(SOC.Road r);
    public void build(SOC.Junction j);
    public boolean canBuild(SOC.Road r);
    public boolean canBuild(SOC.Junction j);
    public int tileNumber(int n);
    public SOC.resource tileResource(int n);
    public String OwnerName(int n, SOC.location loc);
    public String OwnerName(int n, SOC.location loc, SOC.location loc2);
    public boolean trade(SOC.resource r1, SOC.resource r2);
}

public class Strategg implements PlayerStrategy
{
    public SOC.Junction placeFirstSettlement(BoardInterface b)
    {
        double[] avg;
        for (SOC.Junction j :  b.availableJunctions())
        {
            System.out.println(j);
            for (int t : j.address)
            {
                
                System.out.println(b.tileNumber(t) + " - " + b.tileResource(t));
            }
                
          
        }
        return b.availableJunctions().get(0);
    }
    public SOC.Junction placeFirstRoad(BoardInterface b)
    {
        return b.availableRoads().get(0);
    }
    public SOC.Junction placeSecondSettlement(BoardInterface b)
    {
        return b.availableJunctions().get(0);
    }
    public SOC.Road placeSecondRoad(BoardInterface b)
    {
        return b.availableRoads().get(0);
    }
    public void takeTurn(BoardInterface b)
    {
        if (b.availableRoads().size() > 0)
            b.build(b.availableRoads().get(0));
        if (b.availableJunctions().size() > 0)
            b.build(b.availableJunctions().get(0));
    }
}
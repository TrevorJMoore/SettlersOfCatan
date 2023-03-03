
public interface PlayerStrategy
{
    abstract SOC.Junction placeFirstSettlement(BoardInterface b);
    abstract SOC.Junction placeFirstRoad(BoardInterface  b);
    abstract SOC.Junction placeSecondSettlement(BoardInterface  b);
    abstract SOC.Road placeSecondRoad(BoardInterface  b);
    abstract void takeTurn(BoardInterface  b);
}

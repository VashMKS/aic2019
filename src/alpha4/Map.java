package alpha4;

import aic2019.Location;
import aic2019.Team;
import aic2019.UnitType;

public class Map {

    public Location base;
    public int xOffset;
    public int yOffset;
    public Coord[][] map;

    public Map (Location _allyBase, int _currentRound) {
        this.base = _allyBase;
        this.xOffset = 49 - base.x;
        this.yOffset = 49 - base.y;

        // create an empty map our base at the center
        this.map = new Coord[100][100];
        map[49][49].content = "ALLY_BASE";

    }

    // returns the known information of a position given world coordinates (might be null)
    public Coord readMap (int x, int y) {
        return map[x + xOffset][y + yOffset];
    }

    // updates a coordinate on the map
    public void updateMap (int x, int y, String content, int currentRound) {
        int xlocal = x + xOffset;
        int ylocal = y + yOffset;
        map[xlocal][ylocal].content = content;
        map[xlocal][ylocal].lastExplored = currentRound;
    }

    // Encodes a Coord object into a single integer
    // format is 1XYZTVRRRR where
    // X    = content
    // Y    = unit type
    // Z    = team
    // T    = remaining health
    // V    = ???? (free space)
    // RRRR = round of the last update
    public int encodeCoord (Coord coord) {

        if (coord == null) return 0;

        int content      = coord.getIndex(coord.content);
        int unitType     = coord.unitType.ordinal();
        int team         = coord.team.ordinal();
        int healthLevel  = coord.healthLevel;
        int lastExplored = coord.lastExplored;

        return 1000000000 + 100000000*content + 10000000*unitType + 1000000*team + 100000*healthLevel + lastExplored;
    }

    // Decodes a Coord object from an integer formatted as described in encodeCoord above
    public Coord decodeCoord (int n) {
        Coord coord = new Coord();

        if (n == 0) return null;

        int contentIndex   = n%100000000 - n%10000000;
        int unitTypeIndex  = n%10000000 - n%1000000;
        int teamIndex      = n%1000000 - n%100000;
        coord.healthLevel  = n%100000 - n%10000;
        coord.lastExplored = n%10000;

        coord.content      = coord.getValue(contentIndex);
        coord.unitType     = UnitType.values()[unitTypeIndex];
        coord.team         = Team.values()[teamIndex];

        return coord;
    }

}
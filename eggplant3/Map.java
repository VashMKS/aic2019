package eggplant3;

import aic2019.*;

public class Map {

    UnitController uc;

    public Location base;
    public int xOffset;
    public int yOffset;
    public Coord[][] coords;

    public Map (Location _allyBase, UnitController _uc) {

        this.uc = _uc;

        this.base = _allyBase;
        this.xOffset = 50 - base.x;
        this.yOffset = 50 - base.y;

        // create an empty map our base at the center
        this.coords = new Coord[101][101];
        uc.println("1");
        // TODO: this does not run, keep going from here
        coords[50][50].content = "ALLY_BASE";
        uc.println("2");

    }

    // returns the known information of a position given world coordinates (might be null)
    public Coord readMap (int x, int y) {
        return coords[x + xOffset][y + yOffset];
    }

    // updates a coordinate on the map
    public void updateMap (int x, int y, String content, int currentRound, UnitInfo unit) {
        int xlocal = x + xOffset;
        int ylocal = y + yOffset;
        coords[xlocal][ylocal].content = content;
        coords[xlocal][ylocal].lastExplored = currentRound;

        if (!(unit == null)) {
            coords[xlocal][ylocal].unitType = unit.getType();
            coords[xlocal][ylocal].team = unit.getTeam();
            coords[xlocal][ylocal].healthLevel = Math.round(((float)unit.getHealth() / (float)unit.getType().maxHealth * 10));
        }
    }

    // Encodes a Coord object into an integer
    // format is 1CUTHXRRRR where
    // C    = content
    // U    = unit type
    // T    = team
    // H    = remaining health
    // X    = ???? (free space)
    // RRRR = round of the last update
    public int encodeCoord (Coord coord) {

        if (coord == null) return 0;

        int content      = coord.getIndex(coord.content);
        int unitType     = coord.unitType.ordinal();
        int team         = coord.team.ordinal();
        int healthLevel  = coord.healthLevel;
        int freeSlot     = 0; // unused bit
        int lastExplored = coord.lastExplored;

        return 1000000000 + 100000000*content + 10000000*unitType + 1000000*team + 100000*healthLevel + 10000*freeSlot + lastExplored;
    }

    // Decodes a Coord object from an integer formatted as described in encodeCoord above
    public Coord decodeCoord (int n) {
        Coord coord = new Coord();

        if (n == 0) return null;

        int contentIndex   = (n%1000000000 - n%100000000) / 100000000;
        int unitTypeIndex  = (n%100000000  - n%10000000)  / 10000000;
        int teamIndex      = (n%10000000   - n%1000000)   / 1000000;
        coord.healthLevel  = (n%1000000    - n%100000)    / 100000;
        coord.freeSlot     = (n%100000     - n%10000)     / 10000;
        coord.lastExplored =  n%10000;

        coord.content      = coord.getValue(contentIndex);
        coord.unitType     = UnitType.values()[unitTypeIndex];
        coord.team         = Team.values()[teamIndex];

        return coord;
    }

}
package eggplant3;

import aic2019.*;

public class Map {

    public Location base;
    public int xOffset;
    public int yOffset;
    public Coord[][] coords;
    public int[][] visitedCoords;

    public Map (Location _allyBase) {

        this.base = _allyBase;
        this.xOffset = 50 - base.x;
        this.yOffset = 50 - base.y;

        // create an empty map with our base at the center
        this.coords = new Coord[101][101];
        // this explodes the bytecode cost so we are forced to implement visitedCoords and check every time
        /*for (int i = 0; i < 101; i++) {
            for (int j = 0; j < 101; j++) {
                coords[i][j] = new Coord();
            }
        }*/
        this.visitedCoords = new int[101][101];

        coords[50][50] = new Coord();
        coords[50][50].content = "ALLY_BASE";
        visitedCoords[50][50] = 1;

    }

    // returns the known information of a position given world coordinates (might be null)
    public Coord readMap (int x, int y) {

        int xLocal = x + xOffset;
        int yLocal = y + xOffset;

        if (visitedCoords[xLocal][yLocal] == 0) return null;
        return coords[xLocal][yLocal];
    }

    // updates a coordinate on the map
    public void updateMap (int x, int y, String content, int currentRound, UnitInfo unit) {
        int xLocal = x + xOffset;
        int yLocal = y + yOffset;

        if (visitedCoords[xLocal][yLocal] == 0) {
            coords[xLocal][yLocal] = new Coord();
            visitedCoords[xLocal][yLocal] = 1;
        }

        coords[xLocal][yLocal].content = content;
        coords[xLocal][yLocal].lastExplored = currentRound;

        if (!(unit == null)) {
            coords[xLocal][yLocal].unitType = unit.getType();
            coords[xLocal][yLocal].team = unit.getTeam();
            coords[xLocal][yLocal].healthLevel = Math.round(((float)unit.getHealth() / (float)unit.getType().maxHealth * 10));
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

        int content = coord.getIndex(coord.content);
        if (content == -1) return 0;

        int unitType;
        if (coord.unitType == null) unitType = 0;
        else unitType = coord.unitType.ordinal();

        int team;
        if (coord.team == null) team = 0;
        else team = coord.team.ordinal();

        int healthLevel = coord.healthLevel;
        int freeSlot = coord.freeSlot; // unused bit
        int lastExplored = coord.lastExplored;

        return 1000000000 + 100000000*content + 10000000*unitType + 1000000*team + 100000*healthLevel + 10000*freeSlot + lastExplored;
    }

    // Decodes a Coord object from an integer formatted as described in encodeCoord above
    public Coord decodeCoord (int n) {
        Coord coord = new Coord();

        if (n == 0) return coord;

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
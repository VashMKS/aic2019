package eggplant3;

import aic2019.Team;
import aic2019.UnitType;

public class Coord {

    String content;    // default: null
    UnitType unitType; // default: null
    Team team;         // default: null
    int healthLevel;   // default: 0      int ranging 0-9 denoting %HP left, 1 is 10%, 9 is 90%, 0 is 100%
    int freeSlot;      // default: 0
    int lastExplored;  // default: 0

    String getValue(int i) {
        if (i == 0) return "PLAINS";
        if (i == 1) return "MOUNTAIN";
        if (i == 2) return "WATER";
        if (i == 3) return "WOOD";
        if (i == 4) return "IRON";
        if (i == 5) return "CRYSTAL";
        if (i == 6) return "TOWN";
        if (i == 7) return "ALLY_BASE";
        if (i == 8) return "ENEMY_BASE";
        if (i == 9) return "OUT_OF_BOUNDS";
        return "ERROR";
    }

    int getIndex(String s) {
        int i = -1;
        if (s.equals("PLAINS"))        i = 0;
        if (s.equals("MOUNTAIN"))      i = 1;
        if (s.equals("WATER"))         i = 2;
        if (s.equals("WOOD"))          i = 3;
        if (s.equals("IRON"))          i = 4;
        if (s.equals("CRYSTAL"))       i = 5;
        if (s.equals("TOWN"))          i = 6;
        if (s.equals("ALLY_BASE"))     i = 7;
        if (s.equals("ENEMY_BASE"))    i = 8;
        if (s.equals("OUT_OF_BOUNDS")) i = 9;
        return i;
    }

    // true if
    boolean canRead() {
        if (content.equals("OUT_OF_BOUNDS")) return false;
        if (lastExplored == 0) return false;
        return true;
    }

    // legend for the map logs
    String legend () {
        if (unitType == UnitType.WORKER)    return "w";
        if (unitType == UnitType.EXPLORER)  return "e";
        if (unitType == UnitType.SOLDIER)   return "s";
        if (unitType == UnitType.ARCHER)    return "a";
        if (unitType == UnitType.MAGE)      return "m";
        if (unitType == UnitType.CATAPULT)  return "c";
        if (unitType == UnitType.TOWER)     return "t";
        if (unitType == UnitType.BARRACKS)  return "b";
        if (content == null)                return "F";
        if (content.equals("PLAINS"))       return "_";
        if (content.equals("MOUNTAIN"))     return "^";
        if (content.equals("WATER"))        return "~";
        if (content.equals("WOOD"))         return "W";
        if (content.equals("IRON"))         return "I";
        if (content.equals("CRYSTAL"))      return "C";
        if (content.equals("TOWN"))         return "T";
        if (content.equals("ALLY_BASE"))    return "A";
        if (content.equals("ENEMY_BASE"))   return "B";
        return "X";
    }

    // legend for the map logs without units other than the bases
    String legendNoUnits () {
        if (content == null)                return "F";
        if (content.equals("PLAINS"))       return "_";
        if (content.equals("MOUNTAIN"))     return "^";
        if (content.equals("WATER"))        return "~";
        if (content.equals("WOOD"))         return "W";
        if (content.equals("IRON"))         return "I";
        if (content.equals("CRYSTAL"))      return "C";
        if (content.equals("TOWN"))         return "T";
        if (content.equals("ALLY_BASE"))    return "A";
        if (content.equals("ENEMY_BASE"))   return "B";
        return "X";
    }

}

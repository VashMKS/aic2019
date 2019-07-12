package eggplant_2;

import aic2019.Team;
import aic2019.UnitType;

public class Coord {

    String content;    // default: null
    UnitType unitType; // default: null
    Team team;         // default: null
    int healthLevel;   // default: 0
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
        return "ERROR";
    }

    int getIndex(String s) {
        int i = -1;
        if (s.equals("PLAINS"))     i = 0;
        if (s.equals("MOUNTAIN"))   i = 1;
        if (s.equals("WATER"))      i = 2;
        if (s.equals("WOOD"))       i = 3;
        if (s.equals("IRON"))       i = 4;
        if (s.equals("CRYSTAL"))    i = 5;
        if (s.equals("TOWN"))       i = 6;
        if (s.equals("ALLY_BASE"))  i = 7;
        if (s.equals("ENEMY_BASE")) i = 8;
        return i;
    }


}

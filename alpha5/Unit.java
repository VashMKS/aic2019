package alpha5;

import aic2019.Location;
import aic2019.ResourceInfo;
import aic2019.TownInfo;
import aic2019.UnitController;

public class Unit {

    UnitController uc;
    Data data;
    Tools tools;

    void report() {
        reportMyself();
        reportEnemies();
        reportEnvironment();
    }

    // overrided for each different unit
    void reportMyself() {
    }

    // report enemies on sight
    void reportEnemies () {
    }

    // report mines, towns, terrain, etc
    void reportEnvironment() {
        reportMines();
        reportTowns();
        reportMap();
    }

    // scans for mines and reports new findings to the comm channels
    void reportMines() {
        ResourceInfo[] minesOnSight = uc.senseResources();
        if(minesOnSight.length > 0) {
            //uc.println("mine scan successful, potential new mines: " + minesOnSight.length);
            boolean newMineFound = false;
            int mineLocChannel = data.nMineCh + 2*data.nMine - 1;
            for (ResourceInfo mineInfo : minesOnSight) {
                Location mineLoc = mineInfo.getLocation();
                //uc.println("  checking location (" + mineLoc.x + " " + mineLoc.y + ")");
                if (!tools.reportedMine(mineLoc)) {
                    //uc.println("   new mine found, storing location");
                    mineLocChannel += 2;
                    uc.write(mineLocChannel, tools.encrypt(mineLoc.x,mineLoc.y));
                    uc.write(data.nMineCh, data.nMine+1);
                    newMineFound = true;
                } else {
                    //uc.println("   mine already scanned");
                }
            }
            if (newMineFound) data.updateMines();
            //uc.println("so far " + uc.read(data.nMineCh) + " mines discovered");
        }
    }

    // scans for towns and reports new findings to the comm channels
    void reportTowns() {
        TownInfo[] townsOnSight = uc.senseTowns();
        if(townsOnSight.length > 0) {
            //uc.println("town scan successful, potential new towns: " + townsOnSight.length);
            boolean newTownFound = false;
            int townLocChannel = data.nTownCh + 2*data.nTown - 1;
            for (TownInfo townInfo : townsOnSight) {
                Location townLoc = townInfo.getLocation();
                //uc.println("  checking location (" + townLoc.x + " " + townLoc.y + ")");
                if (!tools.reportedTown( townLoc )) {
                    //uc.println("   new town found, storing location");
                    townLocChannel += 2;
                    uc.write(townLocChannel, tools.encrypt(townLoc.x,townLoc.y));
                    uc.write(data.nTownCh, data.nTown);
                    newTownFound = true;
                } else {
                    //uc.println("   town already scanned");
                }
            }
            if (newTownFound) data.updateTowns();
            //uc.println("so far " + uc.read(data.nTownCh) + " towns discovered");
        }
    }

    void reportMap() {

    }

}

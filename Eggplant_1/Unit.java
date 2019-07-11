package Eggplant_1;

import aic2019.*;

public class Unit {

    UnitController uc;
    Data data;
    Tools tools;

    void report() {
        reportMyself();
        reportEnemies();
        reportEnvironment();
    }

    // placeholder, this is overridden for each different unit
    void reportMyself() {
        // Report to the Comm Channel
        uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
        // Reset Next Slot
        uc.write(data.unitResetCh, 0);
    }

    // report hostile (enemy + neutral) units on sight and adjacent
    void reportEnemies () {
        UnitInfo[] nonAllyUnitsOnSight = uc.senseUnits(data.allyTeam, true);

        //
        if (nonAllyUnitsOnSight.length > 0) uc.write(data.hostileFoundCh, 1);

        for (UnitInfo unit : nonAllyUnitsOnSight) {
            // checks if the hostile unit is reachable
            if (!data.hostileContact && unit.getLocation().distanceSquared(uc.getLocation()) <= 2) {
                data.hostileContact = true;
                uc.write(data.hostileContactCh, 1);
            }

            if (!data.neutralFound && unit.getTeam() == Team.NEUTRAL) {
                uc.write(data.neutralFoundCh, 1);
                // checks if the neutral unit is reachable
                if (!data.neutralContact && unit.getLocation().distanceSquared(uc.getLocation()) <= 2) {
                    data.neutralContact = true;
                    uc.write(data.neutralContactCh, 1);
                }
            }

            if (!data.enemyFound && unit.getTeam() == data.enemyTeam) {
                uc.write(data.enemyFoundCh, 1);
                // checks if the enemy unit is reachable
                if (!data.enemyContact && unit.getLocation().distanceSquared(uc.getLocation()) <= 2) {
                    data.enemyContact = true;
                    uc.write(data.enemyContactCh, 1);
                }
            }
        }
    }

    // report mines, towns, terrain, etc
    void reportEnvironment() {
        reportResources();
        reportTowns();
        reportTerrain();
    }

    // scans for mines and reports new findings to the comm channels
    void reportResources() {
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

    // scans for terrain and updates the internal map
    void reportTerrain() {

    }

}

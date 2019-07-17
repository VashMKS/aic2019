package huevoplanta3;

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

        if (nonAllyUnitsOnSight.length > 0) uc.write(data.hostileOnSightCh, 1);

        for (UnitInfo unit : nonAllyUnitsOnSight) {
            // checks if the hostile unit is reachable
            boolean adjacent = tools.areAdjacent(uc.getLocation(), unit.getLocation());
            if (adjacent) {
                if (!data.hostileContact) {
                    data.hostileContact = true;
                    uc.write(data.hostileContactCh, 1);
                }

                if (!data.neutralOnSight && unit.getTeam() == Team.NEUTRAL) {
                    uc.write(data.neutralOnSightCh, 1);
                    // checks if the neutral unit is reachable
                    if (!data.neutralContact) {
                        data.neutralContact = true;
                        uc.write(data.neutralContactCh, 1);
                    }
                }

                if (!data.enemyOnSight && unit.getTeam() == data.enemyTeam) {
                    uc.write(data.enemyOnSightCh, 1);
                    // checks if the enemy unit is reachable
                    if (!data.enemyContact) {
                        data.enemyContact = true;
                        uc.write(data.enemyContactCh, 1);
                    }
                }
            }
        }
    }

    // report mines, towns, terrain, etc
    void reportEnvironment() {
        reportResources();
        reportTowns();
        reportTownControl();
    }

    // scans for mines and reports new findings to the comm channels
    void reportResources() {
        ResourceInfo[] minesOnSight = uc.senseResources();
        if(minesOnSight.length > 0) {
            //uc.println("mine scan successful, potential new mines: " + minesOnSight.length);
            int counter = 0;
            for (ResourceInfo mineInfo : minesOnSight) {

                if (counter > 16) return;
                else counter++;

                boolean newMineFound = false;
                Location mineLoc = mineInfo.getLocation();
                int distSqToBase = mineLoc.distanceSquared(data.allyBase);

                if(distSqToBase > 100 && data.nMine > 4) continue;
                if(mineLoc.distanceSquared(data.enemyBase) < 50) continue;

                // TODO: hard cap on number of iterations of this function
                if (data.nMine < data.nMineMax) {
                    // add this mine to our list of mines if never seen before
                    if (tools.reportedMine(mineLoc) == -1) {
                        int mineLocChannel = data.nMineCh + data.channelsPerMine*data.nMine + 1;
                        int mineDistSqToBaseChannel = mineLocChannel + 2;
                        uc.write(mineLocChannel, tools.encodeLocation(mineLoc.x,mineLoc.y));
                        uc.write(mineDistSqToBaseChannel, distSqToBase);
                        uc.write(data.nMineCh, data.nMine+1);
                        newMineFound = true;
                    }
                } else if (distSqToBase < data.mineMinDistSqToBase) {
                    // swap the mine with the longest distance to base for this one
                    data.mineMinDistSqToBase = distSqToBase;
                    uc.write(data.mineMinDistSqToBaseCh, distSqToBase);
                    int index = data.mineMaxDistSqToBaseIndexCh;
                    int mineLocChannel = data.nMineCh + data.channelsPerMine*index + 1;
                    int mineDistSqToBaseChannel = mineLocChannel + 2;
                    uc.write(mineLocChannel, tools.encodeLocation(mineLoc.x, mineLoc.y));
                    uc.write(mineDistSqToBaseChannel, distSqToBase);
                    newMineFound = true;
                }

                if (newMineFound) data.updateMines();
            }
            //uc.println("so far " + uc.read(data.nMineCh) + " mines discovered");
        }
    }

    // scans for towns and reports new findings to the comm channels
    void reportTowns() {
        TownInfo[] townsOnSight = uc.senseTowns();
        if(townsOnSight.length > 0 && data.nTown < 10) {
            uc.println("town scan successful, potential new towns: " + townsOnSight.length);
            for (TownInfo town : townsOnSight) {
                boolean newTownFound = false;
                Location townLoc = town.getLocation();
                uc.println("  checking location (" + townLoc.x + " " + townLoc.y + ")");
                int index = tools.reportedTown(townLoc);
                if (index == -1) {
                    uc.println(" new town found, storing location");
                    int townLocChannel = data.nTownCh + data.channelsPerTown*data.nTown + 1;
                    int townOwnerChannel = townLocChannel + 1;
                    int townsDistSqToBaseChannel = townLocChannel + 2;
                    uc.write(townLocChannel, tools.encodeLocation(townLoc.x,townLoc.y));
                    uc.write(townOwnerChannel, 0);
                    uc.write(townsDistSqToBaseChannel, townLoc.distanceSquared(data.allyBase));
                    uc.write(data.nTownCh, data.nTown+1);
                    newTownFound = true;
                } else {
                    if (town.getOwner() == data.allyTeam) {
                        data.townOwned[index] = true;
                        int townOwnerChannel = data.nTownCh + data.channelsPerTown*data.nTown + 2;
                        uc.write(townOwnerChannel, 1);
                    } else {
                        data.townOwned[index] = false;
                        int townOwnerChannel = data.nTownCh + data.channelsPerTown*data.nTown + 2;
                        uc.write(townOwnerChannel, 0);
                    }
                }

                if (newTownFound) data.updateTowns();
            }
            //uc.println("so far " + uc.read(data.nTownCh) + " towns discovered");
        }
    }

    void reportTownControl() {
        TownInfo[] townsOnSight = uc.senseTowns();

        if (townsOnSight.length > 0) {
            for (TownInfo townInfo : townsOnSight) {

                int townLocChannel = data.nTownCh + data.channelsPerTown*data.townToAttack + 1;
                Location loc = townInfo.getLocation();

                if(tools.decodeLocation(uc.read(townLocChannel)).isEqual(loc)){
                    if (townInfo.getOwner() == data.allyTeam) {
                        data.townToAttack += 1;
                        data.townToAttack = data.townToAttack % data.nTown;
                    }
                }
            }
        }
    }
}

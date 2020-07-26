package berenjena1;

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

        reportEnemyLocation();
        reportNeutralLocation();

        /*
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
        */
    }

    void reportEnemyLocation(){

        if(data.enemyFound) {
            if(uc.canSenseLocation(data.enemyLoc)) {

                //uc.println("checking position (" + neutralLoc.x + ", " + neutralLoc.y + ")");

                UnitInfo[] units = uc.senseUnits(data.enemyLoc, 2, data.enemyTeam);
                if(units.length == 0) {
                    uc.write(data.enemyLocCh, 0);
                    uc.write(data.enemyFoundCh, 0);
                    data.enemyFound = false;
                    //uc.println("No neutral hostile here, proceed");
                }
            }
        } else {
            UnitInfo[] enemiesOnSight = uc.senseUnits(data.enemyTeam, false);
            if (enemiesOnSight.length > 0) {
                Location neutralLoc = enemiesOnSight[0].getLocation();
                uc.write(data.enemyLocCh, tools.encodeLocation(neutralLoc.x, neutralLoc.y));
                uc.write(data.enemyFoundCh, 1);
                data.enemyFound = true;
                //uc.println("found a neutral unit on (" + neutralLoc.x + " " + neutralLoc.y + ")" );
            }
        }

    }



    void reportNeutralLocation(){

        if(data.neutralFound) {
            if(uc.canSenseLocation(data.neutralLoc)) {

                //uc.println("checking position (" + neutralLoc.x + ", " + neutralLoc.y + ")");

                UnitInfo[] units = uc.senseUnits(data.neutralLoc, 2, Team.NEUTRAL);
                if(units.length == 0) {
                    uc.write(data.neutralLocCh, 0);
                    uc.write(data.neutralFoundCh, 0);
                    data.neutralFound = false;
                    //uc.println("No neutral hostile here, proceed");
                }
            }
        } else {
            UnitInfo[] neutralsOnSight = uc.senseUnits(Team.NEUTRAL, false);
            if (neutralsOnSight.length > 0) {
                Location neutralLoc = neutralsOnSight[0].getLocation();
                uc.write(data.neutralLocCh, tools.encodeLocation(neutralLoc.x, neutralLoc.y));
                uc.write(data.neutralFoundCh, 1);
                data.neutralFound = true;
                //uc.println("found a neutral unit on (" + neutralLoc.x + " " + neutralLoc.y + ")" );
            }
        }

    }

    // report mines, towns, terrain, etc
    void reportEnvironment() {
        reportResources();
        reportTowns();
    }

    // scans for mines and reports new findings to the comm channels
    void reportResources() {

        //if(data.nMine > 8) return;

        ResourceInfo[] minesOnSight = uc.senseResources();
        if(minesOnSight.length > 0) {
            //uc.println("mine scan successful, potential new mines: " + minesOnSight.length);
            int counter = 0;
            for (ResourceInfo mineInfo : minesOnSight) {

                // hard cap on the number of iterations of this function
                if (counter > 10) return;
                else counter++;

                boolean newMineFound = false;
                Location mineLoc = mineInfo.getLocation();
                int distSqToBase = mineLoc.distanceSquared(data.allyBase);

                // don't assign too many mines adjacent to the base (workers will block the base)
                if (distSqToBase <= 2 && data.nMine > 4) continue;
                // don't gather on mines too far away if we got a few closer
                if (distSqToBase > 100 && data.nMine > 4) continue;
                // don't gather no mines too close to the enemy base
                if (mineLoc.distanceSquared(data.enemyBase) < 81) continue;

                if (data.nMine < data.nMineMax) {
                    // add this mine to our list of mines if never seen before
                    if (tools.reportedMine(mineLoc) == -1) {
                        int mineLocChannel = data.nMineCh + data.channelsPerMine*data.nMine + 1;
                        int mineDistSqToBaseChannel = mineLocChannel + 4;
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
                    int mineDistSqToBaseChannel = mineLocChannel + 4;
                    uc.write(mineLocChannel, tools.encodeLocation(mineLoc.x, mineLoc.y));
                    uc.write(mineDistSqToBaseChannel, distSqToBase);
                    newMineFound = true;
                }

                // update if a new mine was added to the comm channels
                if (newMineFound) data.updateMines();
            }
            //uc.println("so far " + uc.read(data.nMineCh) + " mines discovered");
        }
    }

    // scans for towns and reports new findings to the comm channels
    void reportTowns() {
        TownInfo[] townsOnSight = uc.senseTowns();
        if(townsOnSight.length > 0) {
            //uc.println("town scan successful, potential new towns: " + townsOnSight.length);
            for (TownInfo town : townsOnSight) {
                boolean newTownFound = false;
                Location townLoc = town.getLocation();

                int index = tools.reportedTown(townLoc);

                //uc.println("  checking location (" + townLoc.x + " " + townLoc.y + ")");

                if (index == -1) {
                    //uc.println(" new town found, storing location");
                    int townLocChannel = data.nTownCh + data.channelsPerTown*data.nTown + 1;
                    //int townOwnerChannel = townLocChannel + 1;
                    int townDistSqToBaseChannel = townLocChannel + 2;
                    uc.write(townLocChannel, tools.encodeLocation(townLoc.x,townLoc.y));
                    //uc.write(townOwnerChannel, 0);
                    uc.write(townDistSqToBaseChannel, townLoc.distanceSquared(data.allyBase));
                    uc.write(data.nTownCh, data.nTown+1);
                    newTownFound = true;
                } else {
                    if (town.getOwner() == data.allyTeam) {
                        data.townOwned[index] = true;
                        int townOwnerChannel = data.nTownCh + data.channelsPerTown*index + 2;
                        uc.write(townOwnerChannel, 1);
                    } else {
                        data.townOwned[index] = false;
                        int townOwnerChannel = data.nTownCh + data.channelsPerTown*index + 2;
                        uc.write(townOwnerChannel, 0);
                    }
                }

                // report in which round we are visiting this town
                int townLastVisitedChannel = data.nTownCh + data.channelsPerTown*index + 4;
                uc.write(townLastVisitedChannel, data.currentRound);

                if (newTownFound) data.updateTowns();
            }
            //uc.println("so far " + uc.read(data.nTownCh) + " towns discovered");
        }
    }


}

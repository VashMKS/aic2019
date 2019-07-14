package eggplant3;

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
        //reportMap();
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
    void reportMap() {
        for (int i = -data.sightRange; i < data.sightRange; i++) {
            for (int j = -data.sightRange; j < data.sightRange; j++) {
                if (i*i + j*j < data.sightRangeSquared) {
                    int x = data.myLoc.x + i;
                    int y = data.myLoc.y + j;
                    uc.println("1");
                    Location loc = new Location(x,y);
                    uc.println("2");
                    if (uc.canSenseLocation(loc)) {
                        // get the Coord object in that position
                        int mapCh = data.localMapCoordCh(loc);
                        int encodedCoord = uc.read(mapCh);
                        uc.println("3");
                        Coord coord;
                        if (encodedCoord == 0) {
                            coord = new Coord();
                        } else {
                            coord = data.map.decodeCoord(encodedCoord);
                        }

                        uc.println("4");
                        // update the Coord object
                        if (coord.content == null) {
                            if (loc.equals(data.enemyBase)) {
                                coord.content = "ENEMY_BASE";
                            } else if (uc.senseTown(loc) != null) {
                                coord.content = "TOWN";
                            } else if (uc.senseResource(loc) != Resource.NONE){
                                coord.content = uc.senseResource(loc).toString();
                            } else {
                                coord.content = uc.senseTerrain(loc).toString();
                            }
                        }
                        uc.println("5");
                        UnitInfo unit = uc.senseUnit(loc);
                        if (unit == null) {
                            coord.unitType    = null;
                            coord.team        = null;
                            coord.healthLevel = 0;
                        } else {
                            coord.unitType    = unit.getType();
                            coord.team        = unit.getTeam();
                            coord.healthLevel = Math.round(((float)unit.getHealth() / (float)unit.getType().maxHealth * 10));
                        }
                        coord.lastExplored = data.currentRound;

                        uc.println("6");
                        // write the updated object to the local map and the comm channel
                        data.map.updateMap(x,y,coord.content,data.currentRound,unit);
                        uc.println(encodedCoord);
                        encodedCoord = data.map.encodeCoord(coord);
                        uc.println(encodedCoord);
                        //uc.write(mapCh, encodedCoord);
                    }
                    uc.println("7");
                }
            }
        }
    }

}

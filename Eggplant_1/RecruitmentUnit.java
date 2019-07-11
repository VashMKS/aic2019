package Eggplant_1;

import aic2019.Direction;
import aic2019.UnitInfo;
import aic2019.UnitType;

class RecruitmentUnit extends Structure {

    void spawnUnits() {

        // Base
        if (uc.getType() == UnitType.BASE) {
            // TODO: do it with allied mines/towns instead
            if (data.nWorker < data.nMinerThreshold + data.nTownsfolkThreshold) {
                if (data.nMiner < data.nMinerPerMine*data.nMine && data.nMiner < data.nMinerThreshold) {
                    trySpawnWorker();
                }
                if (data.nTownsfolk < data.nTownsfolkPerTown*data.nTown && data.nTownsfolk < data.nTownsfolkThreshold) {
                    trySpawnWorker();
                }

            }
            //TODO: poner una condicion mejor
            if(uc.getRound() > 50) trySpawnArmy();
        }

        // Barracks
        if (uc.getType() == UnitType.BARRACKS) {
            if (data.nCombatUnit < 2 || data.hostileContact) {
                trySpawnArmy();
            }
        }

        /*
        // Both
        if (data.nExplorer < 2) {
            trySpawnExplorer();
        }
        */

    }

    void request(UnitType type, int quantity){

        if (quantity < 1) return;

        // Report to the Comm Channel
        uc.write(data.requestWoodCh   , uc.read(data.requestWoodCh)     + quantity*type.woodCost);
        uc.write(data.requestIronCh   , uc.read(data.requestIronCh)     + quantity*type.ironCost);
        uc.write(data.requestCrystalCh, uc.read(data.requestCrystalCh)  + quantity*type.crystalCost);
        // Reset Next Slot
        uc.write(data.requestWoodResetCh   , 0);
        uc.write(data.requestIronResetCh   , 0);
        uc.write(data.requestCrystalResetCh, 0);
    }

    // spawn a worker
    void trySpawnWorker() {
        boolean done = false;
        for (Direction dir : data.dirs) {
            if (!done && uc.canSpawn(dir, UnitType.WORKER)) {
                uc.spawn(dir, UnitType.WORKER);
                // Report to the Comm Channel
                uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
                uc.write(data.workerReportCh, uc.read(data.workerReportCh + 1));
                // Reset Next Slot
                uc.write(data.unitResetCh, 0);
                uc.write(data.workerResetCh, 0);
                // Update current data
                data.nUnit = data.nUnit + 1;
                data.nWorker = data.nWorker + 1;
                done = true;
            }
        }

        if (!done){
            request(UnitType.WORKER, Math.min(data.nTownsfolkPerTown*data.nTown, data.nTownsfolk) - data.nTownsfolk);
            request(UnitType.WORKER, Math.min(data.nMinerPerMine*data.nMine, data.nMinerThreshold) - data.nWorker);

        }

    }

    // spawn an explorer
    void trySpawnExplorer() {
        boolean done = false;
        for (Direction dir : data.dirs) {
            if (!done && uc.canSpawn(dir, UnitType.EXPLORER)) {
                uc.spawn(dir, UnitType.EXPLORER);
                // Report to the Comm Channel
                uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
                uc.write(data.explorerReportCh, uc.read(data.explorerReportCh + 1));
                // Reset Next Slot
                uc.write(data.unitResetCh, 0);
                uc.write(data.explorerResetCh, 0);
                // Update current data
                data.nUnit = data.nUnit + 1;
                data.nExplorer = data.nExplorer + 1;
                done = true;
            }
        }
    }

    // spawn army
    // TODO: better composition
    void trySpawnArmy() {

        boolean SpawnSoldier = (data.nSoldier <= data.nRequestedSoldier) && (data.nCombatUnit%2 == 0);
        boolean SpawnArcher  = (data.nArcher  <= data.nRequestedArcher) && (data.nCombatUnit%2 == 1);


        boolean done = false;
        for (Direction dir : data.dirs) {
            if(!done) {
                if (SpawnSoldier && uc.canSpawn(dir, UnitType.SOLDIER)) {
                    uc.spawn(dir, UnitType.SOLDIER);
                    // Report to the Comm Channel
                    uc.write(data.unitReportCh, uc.read(data.unitReportCh) + 1);
                    uc.write(data.soldierReportCh, uc.read(data.soldierReportCh + 1));
                    uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh + 1));
                    // Reset Next Slot
                    uc.write(data.unitResetCh, 0);
                    uc.write(data.soldierResetCh, 0);
                    uc.write(data.combatUnitResetCh, 0);
                    // Update current data
                    data.nUnit = data.nUnit + 1;
                    data.nSoldier = data.nSoldier + 1;
                    data.nCombatUnit = data.nCombatUnit + 1;
                    done = true;
                }

                if (SpawnArcher && uc.canSpawn(dir, UnitType.ARCHER)) {
                    uc.spawn(dir, UnitType.ARCHER);
                    // Report to the Comm Channel
                    uc.write(data.unitReportCh, uc.read(data.unitReportCh) + 1);
                    uc.write(data.archerReportCh, uc.read(data.archerReportCh + 1));
                    uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh + 1));
                    // Reset Next Slot
                    uc.write(data.unitResetCh, 0);
                    uc.write(data.archerResetCh, 0);
                    uc.write(data.combatUnitResetCh, 0);
                    // Update current data
                    data.nUnit = data.nUnit + 1;
                    data.nArcher = data.nArcher + 1;
                    data.nCombatUnit = data.nCombatUnit + 1;
                    done = true;
                }

            }
        }
        if(!done){
            //If we can't spawn a unit that turn we request the resources to Spawn our army
            request(UnitType.SOLDIER, data.nRequestedSoldier - data.nSoldier);
            request(UnitType.ARCHER , data.nRequestedArcher - data.nArcher );
        }
    }

    @Override
    void report() {
        reportMyself();
        reportUnitsUnderConstruction();
        reportEnemies();
        reportEnvironment();
    }

    // accounts for units that are under construction
    void reportUnitsUnderConstruction() {
        UnitInfo[] unitsAround = uc.senseUnits(2, data.allyTeam, false);

        for (UnitInfo unit : unitsAround) {
            UnitType type = unit.getType();
            if (type != UnitType.BARRACKS && type != UnitType.BASE && unit.getConstructionTurns() > 0) {
                if (type == UnitType.WORKER) {
                    // Report to the Comm Channel
                    uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
                    uc.write(data.workerReportCh, uc.read(data.workerReportCh)+1);
                    // Reset Next Slot
                    uc.write(data.unitResetCh, 0);
                    uc.write(data.workerResetCh, 0);
                } else if (type == UnitType.EXPLORER) {
                    // Report to the Comm Channel
                    uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
                    uc.write(data.explorerReportCh, uc.read(data.explorerReportCh)+1);
                    // Reset Next Slot
                    uc.write(data.unitResetCh, 0);
                    uc.write(data.explorerResetCh, 0);
                } else if (type == UnitType.SOLDIER) {
                    // Report to the Comm Channel
                    uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
                    uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh)+1);
                    uc.write(data.soldierReportCh, uc.read(data.soldierReportCh)+1);
                    // Reset Next Slot
                    uc.write(data.unitResetCh, 0);
                    uc.write(data.combatUnitResetCh, 0);
                    uc.write(data.soldierResetCh, 0);
                } else if (type == UnitType.ARCHER) {
                    // Report to the Comm Channel
                    uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
                    uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh)+1);
                    uc.write(data.archerReportCh, uc.read(data.archerReportCh)+1);
                    // Reset Next Slot
                    uc.write(data.unitResetCh, 0);
                    uc.write(data.combatUnitResetCh, 0);
                    uc.write(data.archerResetCh, 0);
                } else if (type == UnitType.KNIGHT) {
                    // Report to the Comm Channel
                    uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
                    uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh)+1);
                    uc.write(data.knightReportCh, uc.read(data.knightReportCh)+1);
                    // Reset Next Slot
                    uc.write(data.unitResetCh, 0);
                    uc.write(data.combatUnitResetCh, 0);
                    uc.write(data.knightResetCh, 0);
                } else if (type == UnitType.MAGE) {
                    // Report to the Comm Channel
                    uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
                    uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh)+1);
                    uc.write(data.mageReportCh, uc.read(data.mageReportCh)+1);
                    // Reset Next Slot
                    uc.write(data.unitResetCh, 0);
                    uc.write(data.combatUnitResetCh, 0);
                    uc.write(data.mageResetCh, 0);
                } else if (type == UnitType.CATAPULT) {
                    // Report to the Comm Channel
                    uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
                    uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh)+1);
                    uc.write(data.catapultReportCh, uc.read(data.catapultReportCh)+1);
                    // Reset Next Slot
                    uc.write(data.unitResetCh, 0);
                    uc.write(data.combatUnitResetCh, 0);
                    uc.write(data.catapultResetCh, 0);
                } else if (type == UnitType.TOWER) {
                    // Report to the Comm Channel
                    uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
                    uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh)+1);
                    uc.write(data.towerReportCh, uc.read(data.towerReportCh)+1);
                    // Reset Next Slot
                    uc.write(data.unitResetCh, 0);
                    uc.write(data.combatUnitResetCh, 0);
                    uc.write(data.towerResetCh, 0);
                }
            }
        }

    }
}

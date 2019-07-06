package alpha2;

import aic2019.Direction;
import aic2019.UnitInfo;
import aic2019.UnitType;

public class RecruitmentUnit extends Structure {

    void spawnUnits() {
        if (data.nExplorer < 2) {
            trySpawnExplorer();
        }
        if (data.nWorker < 2*data.nMine) {
            trySpawnWorker();
        }
        if (data.enemyFound) {
            trySpawnArmy();
        }
    }

    // spawn a worker
    void trySpawnWorker() {
        boolean done = false;
        for (Direction dir : data.dirs) {
            if (!done && uc.canSpawn(dir, UnitType.WORKER)) {
                uc.spawn(dir, UnitType.WORKER);
                uc.write(data.workerReportCh, uc.read(data.workerReportCh+1));
                done = true;
            }
        }
    }

    // spawn an explorer
    void trySpawnExplorer() {
        boolean done = false;
        for (Direction dir : data.dirs) {
            if (!done && uc.canSpawn(dir, UnitType.EXPLORER)) {
                uc.spawn(dir, UnitType.EXPLORER);
                done = true;
            }
        }
    }

    // spawn army
    // TODO: better composition
    void trySpawnArmy() {
        boolean done = false;
        for (Direction dir : data.dirs) {
            if (!done && uc.canSpawn(dir, UnitType.SOLDIER)) {
                uc.spawn(dir, UnitType.SOLDIER);
                done = true;
            }
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

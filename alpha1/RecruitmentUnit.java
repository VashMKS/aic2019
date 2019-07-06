package alpha1;

import aic2019.*;

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

    void trySpawnWorker() {
        for (Direction dir : data.dirs) {
            if (uc.canSpawn(dir, UnitType.WORKER)) {
                uc.spawn(dir, UnitType.WORKER);
                uc.write(data.workerReportCh, uc.read(data.workerReportCh+1));
            }
        }
    }

    void trySpawnExplorer() {
        for (Direction dir : data.dirs) {
            if (uc.canSpawn(dir, UnitType.EXPLORER)) {
                uc.spawn(dir, UnitType.EXPLORER);
            }
        }
    }

    void trySpawnArmy() {
        for (Direction dir : data.dirs) {
            if (uc.canSpawn(dir, UnitType.SOLDIER)) {
                uc.spawn(dir, UnitType.SOLDIER);
            }
        }
    }

    void reportUnitsUnderConstruction() {
        UnitInfo[] unitsAround = uc.senseUnits(2, data.allyTeam, false);

        for (UnitInfo unit : unitsAround) {
            UnitType type = unit.getType();
            if (type != UnitType.BARRACKS && type != UnitType.BASE && unit.getConstructionTurns() > 0) {

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
}

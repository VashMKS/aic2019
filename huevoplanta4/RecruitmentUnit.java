package huevoplanta4;

import aic2019.Direction;
import aic2019.UnitInfo;
import aic2019.UnitType;

class RecruitmentUnit extends Structure {

    void spawnUnits() {

        // Base
        if (uc.getType() == UnitType.BASE) {
            if (data.nMiner < data.nMinerPerMine*data.nMine && data.nMiner < data.nMinerMax && data.nWorker < data.nWorkerMax) {
                trySpawnWorker();
            }
            //TODO: poner una condicion mejor
            if(uc.getRound() > 25){

                if(data.towerFound){
                    trySpawnCatapult();
                    if(uc.getWood() > 180 && uc.getIron() > 60) trySpawnArmy();
                }
                else trySpawnArmy();
            }

        }

        // Barracks
        if (uc.getType() == UnitType.BARRACKS) {
            if(data.towerFound){
                trySpawnCatapult();
                if(uc.getWood() > 180 && uc.getIron() > 60) trySpawnArmy();
            }
            else trySpawnArmy();
        }

        // Both
        if (data.nExplorer < 2) {
            trySpawnExplorer();
        }


    }

    void request(UnitType type, int quantity){

        if (quantity < 1) return;

        // Report to the Comm Channel
        uc.write(data.requestWoodReportCh   , uc.read(data.requestWoodReportCh)     + quantity*type.woodCost);
        uc.write(data.requestIronReportCh   , uc.read(data.requestIronReportCh)     + quantity*type.ironCost);
        uc.write(data.requestCrystalReportCh, uc.read(data.requestCrystalReportCh)  + quantity*type.crystalCost);
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
                data.nUnit   = data.nUnit + 1;
                data.nWorker = data.nWorker + 1;
                done = true;
            }
        }

        if (!done){
            request(UnitType.WORKER, Math.min(data.nMinerPerMine*data.nMine, data.nMinerMax) - data.nWorker);

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
                uc.write(data.explorerReportCh, uc.read(data.explorerReportCh)+1);
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

    void trySpawnCatapult() {

        boolean done = false;
        boolean spawnCatapult  =(data.nCatapult  <= data.nRequestedCatapult) && data.towerFound;

        for (Direction dir : data.dirs) {
            if (!done && spawnCatapult && uc.canSpawn(dir, UnitType.CATAPULT)) {
                uc.spawn(dir, UnitType.CATAPULT);
                // Report to the Comm Channel
                uc.write(data.unitReportCh, uc.read(data.unitReportCh) + 1);
                uc.write(data.catapultReportCh, uc.read(data.catapultReportCh) + 1);
                // Reset Next Slot
                uc.write(data.unitResetCh, 0);
                uc.write(data.catapultResetCh, 0);
                // Update current data
                data.nUnit = data.nUnit + 1;
                data.nCatapult = data.nCatapult + 1;
                done = true;
            }
        }
    }

    // spawn army
    void trySpawnArmy() {

        boolean spawnSoldier =(data.nSoldier <= data.nRequestedSoldier);
        boolean spawnArcher  =(data.nArcher  <= data.nRequestedArcher) && (data.nSoldier > 2);
        boolean spawnKnight  =(data.nKnight  <= data.nRequestedKnight) && (data.nArcher  > 4);
        boolean spawnMage    =(data.nMage    <= data.nRequestedMage)   && (data.nArcher  > 6);

        boolean done = false;

        if (data.nCombatUnit%4 == 0) {
            if(!done && spawnSoldier) done = trySpawnSoldier();
            if(!done && spawnArcher ) done = trySpawnArcher();
            if(!done && spawnMage   ) done = trySpawnMage();
            if(!done && spawnKnight ) done = trySpawnKnight();
        }
        if (data.nCombatUnit%4 == 1) {
            if(!done && spawnArcher ) done = trySpawnArcher();
            if(!done && spawnMage   ) done = trySpawnMage();
            if(!done && spawnKnight ) done = trySpawnKnight();
            if(!done && spawnSoldier) done = trySpawnSoldier();
        }
        if (data.nCombatUnit%4 == 2) {
            if(!done && spawnMage   ) done = trySpawnMage();
            if(!done && spawnKnight ) done = trySpawnKnight();
            if(!done && spawnSoldier) done = trySpawnSoldier();
            if(!done && spawnArcher ) done = trySpawnArcher();

        }
        if (data.nCombatUnit%4 == 3) {
            if(!done && spawnKnight ) done = trySpawnKnight();
            if(!done && spawnSoldier) done = trySpawnSoldier();
            if(!done && spawnArcher ) done = trySpawnArcher();
            if(!done && spawnMage   ) done = trySpawnMage();
        }

        if(!done && tools.valueOf(uc.getWood(),uc.getIron(),uc.getCrystal()) > 1000) {
            data.nRequestedSoldier = 2*data.nRequestedSoldier;
            data.nRequestedArcher  = 2*data.nRequestedArcher;
            data.nRequestedKnight  = 2*data.nRequestedKnight;
            data.nRequestedMage    = 2*data.nRequestedMage;
        }

        if(spawnSoldier) request(UnitType.SOLDIER, data.nRequestedSoldier - data.nSoldier);
        if(spawnArcher)  request(UnitType.ARCHER , data.nRequestedArcher - data.nArcher );
        if(spawnKnight)  request(UnitType.KNIGHT , data.nRequestedKnight - data.nKnight );
        if(spawnMage)    request(UnitType.MAGE   , data.nRequestedMage - data.nMage );

    }

    boolean trySpawnSoldier(){

        boolean done = false;
        for (Direction dir : data.dirs) {
            if (!done && uc.canSpawn(dir, UnitType.SOLDIER)) {
                uc.spawn(dir, UnitType.SOLDIER);
                // Report to the Comm Channel
                uc.write(data.unitReportCh, uc.read(data.unitReportCh) + 1);
                uc.write(data.soldierReportCh, uc.read(data.soldierReportCh) + 1);
                uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh) + 1);
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
        }

        return done;

    }

    boolean trySpawnArcher(){

        boolean done = false;
        for (Direction dir : data.dirs) {
            if (!done && uc.canSpawn(dir, UnitType.ARCHER)) {
                uc.spawn(dir, UnitType.ARCHER);
                // Report to the Comm Channel
                uc.write(data.unitReportCh, uc.read(data.unitReportCh) + 1);
                uc.write(data.archerReportCh, uc.read(data.archerReportCh) + 1);
                uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh) + 1);
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
        return done;

    }

    boolean trySpawnMage(){

        boolean done = false;
        for (Direction dir : data.dirs) {
            if (!done && uc.canSpawn(dir, UnitType.MAGE)) {
                uc.spawn(dir, UnitType.MAGE);
                // Report to the Comm Channel
                uc.write(data.unitReportCh, uc.read(data.unitReportCh) + 1);
                uc.write(data.mageCh, uc.read(data.mageCh) + 1);
                uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh) + 1);
                // Reset Next Slot
                uc.write(data.unitResetCh, 0);
                uc.write(data.mageResetCh, 0);
                uc.write(data.combatUnitResetCh, 0);
                // Update current data
                data.nUnit = data.nUnit + 1;
                data.nMage = data.nMage + 1;
                data.nCombatUnit = data.nCombatUnit + 1;
                done = true;
            }

        }

        return done;
    }

    boolean trySpawnKnight(){

        boolean done = false;
        for (Direction dir : data.dirs) {
            if (!done && uc.canSpawn(dir, UnitType.KNIGHT)) {
                uc.spawn(dir, UnitType.KNIGHT);
                // Report to the Comm Channel
                uc.write(data.unitReportCh, uc.read(data.unitReportCh) + 1);
                uc.write(data.knightCh, uc.read(data.knightCh) + 1);
                uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh) + 1);
                // Reset Next Slot
                uc.write(data.unitResetCh, 0);
                uc.write(data.knightCh, 0);
                uc.write(data.combatUnitResetCh, 0);
                // Update current data
                data.nUnit = data.nUnit + 1;
                data.nKnight = data.nKnight + 1;
                data.nCombatUnit = data.nCombatUnit + 1;
                done = true;
            }

        }

        return done;
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

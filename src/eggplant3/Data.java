package eggplant3;

import aic2019.*;

public class Data {

    UnitController uc;
    Tools tools;
    Map map;

    /* ----------------------------------------------- COMM CHANNELS ----------------------------------------------- */

    // Comm Channels (dynamic)
    int unitCh,             unitReportCh,           unitResetCh;            // Ch 0, 1, 2
    int workerCh,           workerReportCh,         workerResetCh;          // Ch 3, 4, 5
    int explorerCh,         explorerReportCh,       explorerResetCh;        // Ch 6, 7, 8
    int soldierCh,          soldierReportCh,        soldierResetCh;         // Ch 9, 10, 11
    int archerCh,           archerReportCh,         archerResetCh;          // Ch 12, 13, 14
    int knightCh,           knightReportCh,         knightResetCh;          // Ch 15, 16, 17
    int mageCh,             mageReportCh,           mageResetCh;            // Ch 18, 19, 20
    int catapultCh,         catapultReportCh,       catapultResetCh;        // Ch 21, 22, 23
    int barracksCh,         barracksReportCh,       barracksResetCh;        // Ch 24, 25, 26
    int towerCh,            towerReportCh,          towerResetCh;           // Ch 27, 28, 29
    int combatUnitCh,       combatUnitReportCh,     combatUnitResetCh;      // Ch 30, 31, 32
    int minerCh,            minerReportCh,          minerResetCh;           // Ch 33, 34, 35
    int townsfolkCh,        townsfolkReportCh,      townsfolkResetCh;       // Ch 36, 37, 38
    int requestWoodCh,      requestWoodReportCh,    requestWoodResetCh;     // Ch 39, 40, 41
    int requestIronCh,      requestIronReportCh,    requestIronResetCh;     // Ch 42, 43, 44
    int requestCrystalCh,   requestCrystalReportCh, requestCrystalResetCh;  // Ch 45, 46, 47
    int joblessCh,          joblessReportCh,        joblessResetCh;         // Ch 48, 49, 50
    int trainingCh,         trainingReportCh,       trainingResetCh;        // Ch 51, 52, 53

    // Comm Channels (static)
    int macroCh                 = 99;     // Ch 99
    int workerHealthThresholdCh = 100;    // Ch 100
    int hostileOnSightCh        = 101;    // Ch 101
    int hostileContactCh        = 102;    // Ch 102
    int enemyOnSightCh          = 103;    // Ch 103
    int enemyContactCh          = 104;    // Ch 104
    int neutralOnSightCh        = 105;    // Ch 105
    int neutralContactCh        = 106;    // Ch 106
    int enemyLocCh              = 107;    // Ch 107
    int requestedSoldiersCh     = 108;    // Ch 108
    int requestedArchersCh      = 109;    // Ch 109
    int requestedKnightCh       = 110;    // Ch 110

    int nMineCh                 = 1000;   // Ch 1000
    int nTownCh                 = 10000;  // Ch 10000
    int mapCh                   = 100000; // Ch 290000

    /* ------------------------------------------------- VARIABLES ------------------------------------------------- */

    // General Info
    final int INF = Integer.MAX_VALUE;
    final float ironMultiplier    = GameConstants.INITIAL_IRON_VALUE;
    final float crystalMultiplier = GameConstants.INITIAL_CRYSTAL_VALUE;
    int ID;                     Location allyBase;              Direction[] dirs = Direction.values();
    UnitType type;              Location enemyBase;             UnitType[] types = UnitType.values();
    Team allyTeam;              int VP;                         int spawnRound;
    Team enemyTeam;             int enemyVP;                    int turnsAlive;
    int currentRound;           int sightRangeSquared;          int sightRange;
    Location myLoc;             int macroID;                    String macroState;

    // Unit Count Info
    int nUnit;                  int nCombatUnit;                int nWorker;
    int nMiner;                 int nTownsfolk;                 int nExplorer;
    int nSoldier;               int nArcher;                    int nKnight;
    int nMage;                  int nCatapult;                  int nBarracks;
    int nTower;                 int nJobless;                   int nTraining;

    // Mine Info
    int nMine;                  Location[] mineLocations;       int[] miners;
    int nOwnedMine;             int[] mineDanger;               Team[] mineOwners;

    // Town Info
    int nTown;                  Location[] townLocations;       int[] townsfolk;
    int nOwnedTown;             int[] townDanger;               Team[] townOwners;

    // Map Info
    int xOffset;
    int yOffset;

    // Enemy Intel
    // true when in field of vision             // true when adjacent
    boolean hostileOnSight;                     boolean hostileContact;
    boolean enemyOnSight;                       boolean enemyContact;
    boolean neutralOnSight;                     boolean neutralContact;
    Location enemyLoc; // currently unused

    // Worker variables
    int nMinerThreshold = 16;   int nTownsfolkThreshold = 4;    int workerHealthThreshold;
    int nMinerPerMine = 2;      int nTownsfolkPerTown = 1;
    boolean isMiner;            boolean isTownsfolk;            boolean isJobless;
    int myMineCh;               int myTownCh;
    int myMineMinerCh;          int myTownTownsfolkCh;
    Location myMine;            Location myTown;
    boolean onDelivery;

    // Base variables
    float woodSurplus;          float requestedWood;            int tradingWood;
    float ironSurplus;          float requestedIron;            int tradingIron;
    float crystalSurplus;       float requestedCrystal;         int tradingCrystal;
    int economyThreshold = 300;

    // Army variables TODO: demanar unitats de forma dinamica
    int nRequestedSoldier = 15; int nRequestedArcher = 10;      int nRequestedMage = 5;

    /* ------------------------------------------------ CONSTRUCTOR ------------------------------------------------ */

    public Data (UnitController _uc) {
        uc           = _uc;
        tools        = new Tools(uc, this);
        allyTeam     = uc.getTeam();
        enemyTeam    = uc.getOpponent();
        currentRound = uc.getRound();
        spawnRound   = currentRound;
        enemyBase    = enemyTeam.getInitialLocation();
        allyBase     = allyTeam.getInitialLocation();
        xOffset      = 50 - allyBase.x;
        yOffset      = 50 - allyBase.y;

        // Unit Specific Constants
        ID                = uc.getInfo().getID();
        type              = uc.getType();
        sightRangeSquared = type.getSightRangeSquared();
        sightRange        = (int)Math.round(Math.sqrt(sightRangeSquared));
        turnsAlive        = 0;

        // Local Map Initializer
        map = new Map(allyBase);

        // Base Initializer
        if (uc.getType() == UnitType.BASE) {

            // macro
            macroState = "DEFAULT";
            macroID    = getMacroID(macroState);
            uc.write(macroCh, macroID);

            // map
            Coord coord = new Coord();
            coord.content      = "ALLY_BASE";
            coord.unitType     = UnitType.BASE;
            coord.team         = allyTeam;
            coord.lastExplored = currentRound;
            int encodedCoord = map.encodeCoord(coord);
            uc.println("Encoded ally base: " + encodedCoord);
            uc.write(localMapCoordCh(allyBase), encodedCoord);

            // market
            tradingWood = 0;    tradingIron = 0;    tradingCrystal = 0;

            // workers
            workerHealthThreshold = 11;
            uc.write(workerHealthThresholdCh, workerHealthThreshold);

        }

        // Worker Initializer
        if (uc.getType() == UnitType.WORKER) {
            isMiner     = false;
            onDelivery  = false;
            isTownsfolk = false;
            isJobless   = true;
        }
    }

    /* -------------------------------------------------- UPDATE --------------------------------------------------- */

    // This function is called once per turn
    public void update() {

        // General updates
        updateGeneral();
        updateChannels();
        updateUnitInfo();
        updateMines();
        updateTowns();
        updateEnemyIntel();

        // Class specific updates
        updateBase();
        updateWorker();
    }

    void updateMines() {
        nMine           = uc.read(nMineCh);
        mineLocations   = new Location[nMine];
        miners          = new int[nMine];
        for (int i = 0; i < nMine ; i++) {
            int mineLocChannel = nMineCh + 2*i + 1;
            Location mineLoc = tools.decrypt(uc.read(mineLocChannel));
            mineLocations[i] = mineLoc;
            int minersChannel = nMineCh + 2*i + 2;
            miners[i] = uc.read(minersChannel);
        }
    }

    void updateTowns() {
        nTown           = uc.read(nTownCh);
        townLocations   = new Location[nTown];
        townsfolk       = new int[nTown];
        for (int i = 0; i < nTown; i++) {
            int townLocChannel = nTownCh + 2*i + 1;
            Location townLoc = tools.decrypt(uc.read(townLocChannel));
            townLocations[i] = townLoc;
            int townsfolkChannel = nTownCh + 2*i + 2;
            townsfolk[i] = uc.read(townsfolkChannel);
        }
    }

    // Base specific updates
    void updateBase() {
        if (type == UnitType.BASE) {
            // reset resource request channels
            uc.write(requestWoodResetCh, 0);
            uc.write(requestIronResetCh, 0);
            uc.write(requestCrystalResetCh, 0);

            requestedWood = uc.read(requestWoodCh);
            requestedIron = uc.read(requestIronCh);
            requestedCrystal = uc.read(requestCrystalCh);

            woodSurplus = uc.getWood() - requestedWood;
            ironSurplus = uc.getWood() - requestedIron;
            crystalSurplus = uc.getWood() - requestedCrystal;
        }
    }

    // Worker specific update
    void updateWorker() {
        if (type.equals(UnitType.WORKER)) {
            // adaptive threshold for workers to leave jobs due to low health
            if (nJobless > nWorker/10) {
                workerHealthThreshold = Math.max(4, workerHealthThreshold - 1);
                uc.write(workerHealthThresholdCh, workerHealthThreshold);
            } else {
                workerHealthThreshold = Math.min(11, workerHealthThreshold + 1);
                uc.write(workerHealthThresholdCh, workerHealthThreshold);
            }
            // job assignments
            int remainingHealth = uc.getInfo().getHealth();
            if (remainingHealth <= workerHealthThreshold) {
                if (isMiner) {
                    isMiner = false;
                    myMine  = null;
                    uc.write(myMineMinerCh, uc.read(myMineMinerCh) - 1);
                }
                if (isTownsfolk) {
                    isTownsfolk = false;
                    myTown      = null;
                    uc.write(myTownTownsfolkCh, uc.read(myTownTownsfolkCh) - 1);
                }
                uc.write(joblessReportCh, uc.read(joblessReportCh) + 1);
                uc.write(joblessResetCh, 0);
                nJobless = nJobless + 1;
                isJobless = true;
            } else {
                assignJob();
            }
        }
    }


    void updateEnemyIntel() {

        hostileOnSight = (uc.read(hostileOnSightCh) == 1);
        hostileContact = (uc.read(hostileContactCh) == 1);
        neutralOnSight = (uc.read(neutralOnSightCh) == 1);
        neutralContact = (uc.read(neutralContactCh) == 1);
        enemyOnSight   = (uc.read(enemyOnSightCh) == 1);
        enemyContact   = (uc.read(enemyContactCh) == 1);

        enemyLoc = tools.decrypt(uc.read(enemyLocCh));

        // Reset Contact every 20 rounds
        if (currentRound%20 == 0) {
            uc.write(hostileContactCh, 0);
            uc.write(neutralContactCh, 0);
            uc.write(enemyContactCh, 0);
        }

        hostileContact = (uc.read(hostileContactCh) == 1);
        neutralContact = (uc.read(neutralContactCh) == 1);
        enemyContact   = (uc.read(enemyContactCh) == 1);
    }

    void updateGeneral() {
        currentRound    = uc.getRound();
        myLoc           = uc.getLocation();
        turnsAlive      = currentRound - spawnRound;
        VP              = allyTeam.getVictoryPoints();
        enemyVP         = enemyTeam.getVictoryPoints();
        macroID         = uc.read(macroCh);
        macroState      = getMacroState(macroID);
    }

    void updateChannels() {
        int x = currentRound%3;
        int y = (currentRound+1)%3;
        int z = (currentRound+2)%3;

        unitReportCh            = x;         workerReportCh         = 3 + x;     explorerReportCh       = 6 + x;
        unitResetCh             = y;         workerResetCh          = 3 + y;     explorerResetCh        = 6 + y;
        unitCh                  = z;         workerCh               = 3 + z;     explorerCh             = 6 + z;

        soldierReportCh         = 9 + x;     archerReportCh         = 12 + x;    knightReportCh         = 15 + x;
        soldierResetCh          = 9 + y;     archerResetCh          = 12 + y;    knightResetCh          = 15 + y;
        soldierCh               = 9 + x;     archerCh               = 12 + z;    knightCh               = 15 + z;

        mageReportCh            = 18 + x;    catapultReportCh       = 21 + x;    barracksReportCh       = 24 + x;
        mageResetCh             = 18 + y;    catapultResetCh        = 21 + y;    barracksResetCh        = 24 + y;
        mageCh                  = 18 + z;    catapultCh             = 21 + z;    barracksCh             = 24 + z;

        towerReportCh           = 27 + x;    combatUnitReportCh     = 30 + x;    minerReportCh          = 33 + x;
        towerResetCh            = 27 + y;    combatUnitResetCh      = 30 + y;    minerResetCh           = 33 + y;
        towerCh                 = 27 + z;    combatUnitCh           = 30 + z;    minerCh                = 33 + z;

        townsfolkReportCh       = 36 + x;    requestWoodReportCh    = 39 + x;    requestIronReportCh    = 42 + x;
        townsfolkResetCh        = 36 + y;    requestWoodResetCh     = 39 + y;    requestIronResetCh     = 42 + y;
        townsfolkCh             = 36 + z;    requestWoodCh          = 39 + z;    requestIronCh          = 42 + z;

        requestCrystalReportCh  = 45 + x;    joblessReportCh        = 48 + x;    trainingReportCh       = 51 + x;
        requestCrystalResetCh   = 45 + y;    joblessResetCh         = 48 + y;    trainingResetCh        = 51 + y;
        requestCrystalCh        = 45 + z;    joblessCh              = 48 + z;    trainingCh             = 51 + z;
    }

    void updateUnitInfo() {
        nUnit                 = uc.read(unitCh);
        nCombatUnit           = uc.read(combatUnitCh);
        nWorker               = uc.read(workerCh);
        nExplorer             = uc.read(explorerCh);
        nSoldier              = uc.read(soldierCh);
        nArcher               = uc.read(archerCh);
        nKnight               = uc.read(knightCh);
        nMage                 = uc.read(mageCh);
        nCatapult             = uc.read(catapultCh);
        nBarracks             = uc.read(barracksCh);
        nTower                = uc.read(towerCh);
        nMiner                = uc.read(minerCh);
        nTownsfolk            = uc.read(townsfolkCh);
        nJobless              = uc.read(joblessCh);
        nTraining             = uc.read(trainingCh);
        workerHealthThreshold = uc.read(workerHealthThresholdCh);
    }

    /* -------------------------------------------------- METHODS -------------------------------------------------- */

    // try to assign a job to a worker
    void assignJob() {
        assignMine();
        assignTown();
        assignJobless();
    }

    // try to assign a worker to a free mine
    void assignMine() {
        if (!isMiner && !isTownsfolk) {
            isJobless = false;
            for(int i = 0; i < nMine; ++i) {
                if (miners[i] < 2) {
                    myMineCh      = nMineCh + 2*i + 1;
                    myMineMinerCh = myMineCh + 1;
                    uc.write(myMineMinerCh, uc.read(myMineMinerCh) + 1);
                    myMine     = mineLocations[i];
                    miners[i]  = miners[i] + 1;
                    isJobless = false;
                    isMiner    = true;
                    //uc.println("Worker ID" + ID + " assigned as miner at (" + myMine.x + ", " + myMine.y + ")");
                    return;
                }
            }
        }
    }

    // try to assign a worker to a free town
    void assignTown() {
        if (!isMiner && !isTownsfolk) {
            for(int i = 0; i < nTown; ++i) {
                if (townsfolk[i] < 1) {
                    myTownCh          = nTownCh + 2*i + 1;
                    myTownTownsfolkCh = myTownCh + 1;
                    uc.write(myTownTownsfolkCh, uc.read(myTownTownsfolkCh) + 1);
                    myTown       = townLocations[i];
                    townsfolk[i] = townsfolk[i] + 1;
                    isJobless = false;
                    isTownsfolk  = true;
                    //uc.println("Worker ID" + ID + " assigned as townsfolk at (" + myTown.x + ", " + myTown.y + ")");
                    return;
                }
            }
        }
    }

    void assignJobless() {
        if (!isMiner && !isTownsfolk) {
            isJobless = true;
        }
    }

    // returns the comm channel allocated to a position in the world
    int localMapCoordCh (Location loc) {
        int x = loc.x + xOffset;
        int y = loc.y + yOffset;
        return mapCh + 1000*x + y;
    }

    String getMacroState (int _macroID) {
        if (_macroID == 0) return "DEFENSE";
        if (_macroID == 1) return "EXPLORATION";
        if (_macroID == 2) return "CONQUEST";
        if (_macroID == 3) return "DESTRUCTION";
        return "DEFAULT";
    }

    int getMacroID (String _macroState) {
        if (_macroState.equals("DEFENSE"))     return 0;
        if (_macroState.equals("EXPLORATION")) return 1;
        if (_macroState.equals("CONQUEST"))    return 2;
        if (_macroState.equals("DESTRUCTION")) return 3;
        return -1;
    }

}
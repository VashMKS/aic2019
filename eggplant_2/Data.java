package eggplant_2;

import aic2019.*;

public class Data {

    UnitController uc;
    Tools tools;

    /* ----------------------------------------------- COMM CHANNELS ----------------------------------------------- */

    // Comm Channels (dynamic)
    int unitCh,            unitReportCh,           unitResetCh;             // Ch 0, 1, 2
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
    int wandererCh,         wandererReportCh,       wandererResetCh;        // Ch 48, 49, 50

    // Comm Channels (static)
    int workerHealthThresholdCh = 100;    // Ch 100
    int hostileFoundCh          = 101;    // Ch 101
    int hostileContactCh        = 102;    // Ch 102
    int enemyFoundCh            = 103;    // Ch 103
    int enemyContactCh          = 104;    // Ch 104
    int neutralFoundCh          = 105;    // Ch 105
    int neutralContactCh        = 106;    // Ch 106
    int enemyLocCh              = 107;    // Ch 107
    int requestedSoldiersCh     = 108;    // Ch 108
    int requestedArchersCh      = 109;    // Ch 109
    int requestedKnightCh       = 110;    // Ch 110

    int nMineCh                 = 1000;   // Ch 1000
    int nTownCh                 = 10000;  // Ch 10000
    int mapCh                   = 290000; // Ch 290000

    /* ------------------------------------------------- VARIABLES ------------------------------------------------- */

    // General Info
    final int INF = Integer.MAX_VALUE;
    final float ironMultiplier    = GameConstants.INITIAL_IRON_VALUE;
    final float crystalMultiplier = GameConstants.INITIAL_CRYSTAL_VALUE;
    int ID;                     Location allyBase;              Direction[] dirs;
    UnitType type;              Location enemyBase;             UnitType[] types;
    Team allyTeam;              int VP;                         int spawnRound;
    Team enemyTeam;             int enemyVP;                    int turnsAlive;
    int currentRound;

    // Unit Count Info
    int nUnit;                  int nCombatUnit;                int nWorker;
    int nMiner;                 int nTownsfolk;                 int nExplorer;
    int nSoldier;               int nArcher;                    int nKnight;
    int nMage;                  int nCatapult;                  int nBarracks;
    int nTower;                 int nWanderer;

    // Mine Info
    int nMine;                  Location[] mineLocations;       int[] miners;
    int nAllyMine;              Location[] allyMineLocations;

    // Town Info
    int nTown;                  Location[] townLocations;       int[] townsfolk;
    int nAllyTown;              Location[] allyTownLocations;

    // Map Info
    int xOffset;
    int yOffset;

    // Enemy Intel
    // true when unit enters field of vision    // true when unit is adjacent
    boolean hostileFound;                       boolean hostileContact;
    boolean enemyFound;                         boolean enemyContact;
    boolean neutralFound;                       boolean neutralContact;
    Location enemyLoc; // currently unused

    // Worker variables
    int nMinerThreshold = 16;   int nTownsfolkThreshold = 4;    int workerHealthThreshold;
    int nMinerPerMine = 2;      int nTownsfolkPerTown = 1;
    boolean isMiner;            boolean isTownsfolk;            boolean isWanderer;
    int myMineCh;               int myTownCh;
    int myMineMinerCh;          int myTownTownsfolkCh;
    Location myMine;            Location myTown;
    boolean onDelivery;

    // Army variables TODO: demanar unitats de forma dinamica
    int nRequestedSoldier = 15; int nRequestedArcher = 10;      int nRequestedMage = 5;


    // Economy variables
    int tradingWood;            int tradingIron;                int tradingCrystal;
    int economyThreshold = 300;

    /* ------------------------------------------------ CONSTRUCTOR ------------------------------------------------ */

    public Data (UnitController _uc) {
        uc           = _uc;
        tools        = new Tools(uc, this);
        ID           = uc.getInfo().getID();
        type         = uc.getType();
        allyTeam     = uc.getTeam();
        enemyTeam    = uc.getOpponent();
        dirs         = Direction.values();
        types        = UnitType.values();
        currentRound = uc.getRound();
        spawnRound   = currentRound;
        enemyBase    = enemyTeam.getInitialLocation();
        allyBase     = allyTeam.getInitialLocation();
        xOffset      = 49 - allyBase.x;
        yOffset      = 49 - allyBase.y;
        turnsAlive   = 0;

        // Base Initializer
        if (uc.getType() == UnitType.BASE) {
            workerHealthThreshold = 11;
            uc.write(workerHealthThresholdCh, workerHealthThreshold);

            tradingWood = 0;    tradingIron = 0;    tradingCrystal = 0;

        }

        // Worker Initializer
        if (uc.getType() == UnitType.WORKER) {
            isMiner     = false;
            onDelivery  = false;
            isTownsfolk = false;
            isWanderer  = false;
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
        }
    }

    // Worker specific update
    void updateWorker() {
        if (type.equals(UnitType.WORKER)) {
            // adaptive threshold for workers leaving jobs due to low health
            if (nWanderer > nWorker/10) {
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
                uc.write(wandererReportCh, uc.read(wandererReportCh) + 1);
                uc.write(wandererResetCh, 0);
                nWanderer = nWanderer + 1;
                isWanderer = true;
            } else {
                assignJob();
            }
        }
    }


    void updateEnemyIntel() {

        hostileFound   = (uc.read(hostileFoundCh) == 1);
        hostileContact = (uc.read(hostileContactCh) == 1);
        neutralFound   = (uc.read(neutralFoundCh) == 1);
        neutralContact = (uc.read(neutralContactCh) == 1);
        enemyFound     = (uc.read(enemyFoundCh) == 1);
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
        currentRound = uc.getRound();
        turnsAlive = currentRound - spawnRound;
        VP = allyTeam.getVictoryPoints();
        enemyVP = enemyTeam.getVictoryPoints();
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

        requestCrystalReportCh  = 45 + x;    wandererReportCh       = 48 + x;
        requestCrystalResetCh   = 45 + y;    wandererResetCh        = 48 + y;
        requestCrystalCh        = 45 + z;    wandererCh             = 48 + z;
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
        nWanderer             = uc.read(wandererCh);
        workerHealthThreshold = uc.read(workerHealthThresholdCh);
    }

    /* -------------------------------------------------- METHODS -------------------------------------------------- */

    // try to assign a job to a worker
    void assignJob() {
        assignMine();
        assignTown();
    }

    // try to assign a worker to a free mine
    void assignMine() {
        if (!isMiner && !isTownsfolk) {
            isWanderer = false;
            for(int i = 0; i < nMine; ++i) {
                if (miners[i] < 2) {
                    myMineCh      = nMineCh + 2*i + 1;
                    myMineMinerCh = myMineCh + 1;
                    uc.write(myMineMinerCh, uc.read(myMineMinerCh) + 1);
                    myMine     = mineLocations[i];
                    miners[i]  = miners[i] + 1;
                    isWanderer = false;
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
                    isWanderer   = false;
                    isTownsfolk  = true;
                    //uc.println("Worker ID" + ID + " assigned as townsfolk at (" + myTown.x + ", " + myTown.y + ")");
                    return;
                }
            }
        }
    }

    // returns the comm channel allocated to a position in the world
    int localMapCoordCh (Location loc) {
        int x = loc.x + xOffset;
        int y = loc.y + yOffset;
        return mapCh + 100*x + y;
    }

}
package huevoplanta3;

import aic2019.*;

public class Data {

    UnitController uc;
    Tools tools;

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
    int joblessCh,          joblessReportCh,        joblessResetCh;         // Ch 39, 40, 41
    int requestWoodCh,      requestWoodReportCh,    requestWoodResetCh;     // Ch 42, 43, 44
    int requestIronCh,      requestIronReportCh,    requestIronResetCh;     // Ch 45, 46, 47
    int requestCrystalCh,   requestCrystalReportCh, requestCrystalResetCh;  // Ch 48, 49, 50

    // Comm Channels (static)
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

    int mineMaxDistSqToBaseIndexCh = 9996;   // Ch 9996
    int mineMaxDistSqToBaseCh      = 9997;   // Ch 9997
    int mineMinDistSqToBaseIndexCh = 9998;   // Ch 9998
    int mineMinDistSqToBaseCh      = 9999;   // Ch 9999
    int nMineCh                    = 10000;  // Ch 10000
    int nTownCh                    = 20000;  // Ch 20000

    /* ------------------------------------------------- VARIABLES ------------------------------------------------- */

    // General Info
    final int INF = Integer.MAX_VALUE;
    final float ironMultiplier    = GameConstants.INITIAL_IRON_VALUE;
    final float crystalMultiplier = GameConstants.INITIAL_CRYSTAL_VALUE;
    int ID;                     Location allyBase;              Direction[] dirs = Direction.values();
    UnitType type;              Location enemyBase;             //UnitType[] types = UnitType.values();
    int currentRound;           int spawnRound;                 int turnsAlive;
    Team allyTeam;              int VP;
    Team enemyTeam;             int enemyVP;


    // Unit Count Info
    int nUnit;                  int nCombatUnit;                int nWorker;
    int nMiner;                 int nJobless;                   int nExplorer;
    int nSoldier;               int nArcher;                    int nKnight;
    int nMage;                  int nCatapult;                  int nBarracks;
    int nTower;

    // Mine Info
    int nMine;                  Location[] mineLocations;       int[] miners;
    int[] mineDistSqToBase;     int nMineMax = 8;               int channelsPerMine = 4;
    int mineMinDistSqToBase;    int mineMinDistSqToBaseIndex;
    int mineMaxDistSqToBase;    int mineMaxDistSqToBaseIndex;

    // Town Info
    int nTown;                  Location[] townLocations;       boolean[] townOwned;
    int[] townDistSqToBase;     int channelsPerTown = 4;

    // Enemy Intel
    // true when in field of vision             // true when adjacent
    boolean hostileOnSight;                     boolean hostileContact;
    boolean enemyOnSight;                       boolean enemyContact;
    boolean neutralOnSight;                     boolean neutralContact;
    Location enemyLoc; // currently unused

    // Worker variables
    int nMinerMax = 16;         int nMinerPerMine = 2;          int workerHealthThreshold;
    boolean isMiner;            boolean isJobless;              boolean onDelivery;
    Location myMine;            int myMineIndex;                int myMineLocCh;
    int myMineMinerCh;          int myMineDistSqToBaseCh;       boolean hasTown;
    Location myTown;            int myTownIndex;                int myTownLocCh;


    // Base variables
    float woodSurplus;          float ironSurplus;              float crystalSurplus;
    float requestedWood;        float requestedIron;            float requestedCrystal;
    int tradingWood;            int tradingIron;                int tradingCrystal;
    int economyThreshold = 200;

    // Army variables TODO: demanar unitats de forma dinamica
    int nRequestedSoldier = 25; int nRequestedArcher = 15;      int nRequestedMage = 5;
    int townToAttack;

    // Explorer variables
    Direction prefDir;

    /* ------------------------------------------------ CONSTRUCTOR ------------------------------------------------ */

    public Data (UnitController _uc) {
        uc           = _uc;
        tools        = new Tools(uc, this);
        ID           = uc.getInfo().getID();
        type         = uc.getType();
        allyTeam     = uc.getTeam();
        enemyTeam    = uc.getOpponent();
        currentRound = uc.getRound();
        spawnRound   = currentRound;
        enemyBase    = enemyTeam.getInitialLocation();
        allyBase     = allyTeam.getInitialLocation();
        turnsAlive   = 0;
        townToAttack = 0;

        // Base Initializer
        if (uc.getType() == UnitType.BASE) {
            workerHealthThreshold = 11;
            uc.write(workerHealthThresholdCh, workerHealthThreshold);

            uc.write(mineMinDistSqToBaseCh, INF);

            tradingWood = 0; tradingIron = 0; tradingCrystal = 0;
        }

        // Worker Initializer
        if (uc.getType() == UnitType.WORKER) {
            isMiner     = false;
            onDelivery  = false;
            hasTown     = false;
            isJobless   = true;
        }

        // Explorer Initializer
        if (uc.getType() == UnitType.EXPLORER) prefDir = allyBase.directionTo(enemyBase);

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
        nMine            = uc.read(nMineCh);
        mineLocations    = new Location[nMine];
        miners           = new int[nMine];
        mineDistSqToBase = new int[nMine];
        for (int i = 0; i < nMine ; i++) {
            int mineLocChannel      = nMineCh + channelsPerMine*i + 1;
            int minersChannel       = mineLocChannel + 1;
            int distSqToBaseChannel = mineLocChannel + 2;

            mineLocations[i]    = tools.decodeLocation(uc.read(mineLocChannel));
            miners[i]           = uc.read(minersChannel);
            int distSqToBase = uc.read(distSqToBaseChannel);
            mineDistSqToBase[i] = distSqToBase;

            // update furthest away mine
            if (distSqToBase > mineMaxDistSqToBase) {
                mineMaxDistSqToBase = distSqToBase;
                uc.write(mineMaxDistSqToBaseCh, distSqToBase);
                mineMaxDistSqToBaseIndex = i;
                uc.write(mineMaxDistSqToBaseIndexCh, i);
            }

            // update closest mine
            if (distSqToBase < mineMinDistSqToBase) {
                mineMinDistSqToBase = distSqToBase;
                uc.write(mineMinDistSqToBaseCh, distSqToBase);
                mineMinDistSqToBaseIndex = i;
                uc.write(mineMinDistSqToBaseIndexCh, i);
            }
        }
    }

    void updateTowns() {
        nTown            = uc.read(nTownCh);
        townLocations    = new Location[nTown];
        townOwned        = new boolean[nTown];
        townDistSqToBase = new int[nTown];
        for (int i = 0; i < nTown; i++) {
            int townLocChannel = nTownCh + channelsPerTown*i + 1;
            int townOwnedChannel = townLocChannel + 1;
            int townsDistSqToBaseChannel = townLocChannel + 2;

            townLocations[i]    = tools.decodeLocation(uc.read(townLocChannel));
            townOwned[i]        = (uc.read(townOwnedChannel) == 1);
            townDistSqToBase[i] = uc.read(townsDistSqToBaseChannel);
        }
    }

    // Base specific updates
    void updateBase() {
        if (type == UnitType.BASE) {

            requestedWood    = uc.read(requestWoodCh);
            requestedIron    = uc.read(requestIronCh);
            requestedCrystal = uc.read(requestCrystalCh);

            woodSurplus = uc.getWood() - requestedWood;
            ironSurplus = uc.getWood() - requestedIron;
            crystalSurplus = uc.getWood() - requestedCrystal;
        }
    }

    // Worker specific update
    void updateWorker() {
        if (type.equals(UnitType.WORKER)) {
            // adaptive threshold for workers leaving jobs due to low health
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
                    myTown  = null;
                    uc.write(myMineMinerCh, uc.read(myMineMinerCh) - 1);
                }
                uc.write(joblessReportCh, uc.read(joblessReportCh) + 1);
                uc.write(joblessResetCh, 0);
                nJobless = nJobless + 1;
                isJobless = true;
            } else {
                if (isMiner) myMine = mineLocations[myMineIndex];
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

        enemyLoc = tools.decodeLocation(uc.read(enemyLocCh));

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

        unitReportCh           = x;         workerReportCh         = 3 + x;     explorerReportCh       = 6 + x;
        unitResetCh            = y;         workerResetCh          = 3 + y;     explorerResetCh        = 6 + y;
        unitCh                 = z;         workerCh               = 3 + z;     explorerCh             = 6 + z;

        soldierReportCh        = 9 + x;     archerReportCh         = 12 + x;    knightReportCh         = 15 + x;
        soldierResetCh         = 9 + y;     archerResetCh          = 12 + y;    knightResetCh          = 15 + y;
        soldierCh              = 9 + z;     archerCh               = 12 + z;    knightCh               = 15 + z;

        mageReportCh           = 18 + x;    catapultReportCh       = 21 + x;    barracksReportCh       = 24 + x;
        mageResetCh            = 18 + y;    catapultResetCh        = 21 + y;    barracksResetCh        = 24 + y;
        mageCh                 = 18 + z;    catapultCh             = 21 + z;    barracksCh             = 24 + z;

        towerReportCh          = 27 + x;    combatUnitReportCh     = 30 + x;    minerReportCh          = 33 + x;
        towerResetCh           = 27 + y;    combatUnitResetCh      = 30 + y;    minerResetCh           = 33 + y;
        towerCh                = 27 + z;    combatUnitCh           = 30 + z;    minerCh                = 33 + z;

        joblessReportCh        = 39 + x;     requestWoodReportCh   = 42 + x;    requestIronReportCh    = 45 + x;
        joblessResetCh         = 39 + y;     requestWoodResetCh    = 42 + y;    requestIronResetCh     = 45 + y;
        joblessCh              = 39 + z;     requestWoodCh         = 42 + z;    requestIronCh          = 45 + z;

        requestCrystalReportCh = 48 + x;
        requestCrystalResetCh  = 48 + y;
        requestCrystalCh       = 48 + z;
    }

    void updateUnitInfo() {
        nUnit                    = uc.read(unitCh);
        nCombatUnit              = uc.read(combatUnitCh);
        nWorker                  = uc.read(workerCh);
        nExplorer                = uc.read(explorerCh);
        nSoldier                 = uc.read(soldierCh);
        nArcher                  = uc.read(archerCh);
        nKnight                  = uc.read(knightCh);
        nMage                    = uc.read(mageCh);
        nCatapult                = uc.read(catapultCh);
        nBarracks                = uc.read(barracksCh);
        nTower                   = uc.read(towerCh);
        nMiner                   = uc.read(minerCh);
        nJobless                 = uc.read(joblessCh);
        workerHealthThreshold    = uc.read(workerHealthThresholdCh);
        mineMinDistSqToBase      = uc.read(mineMinDistSqToBaseCh);
        mineMinDistSqToBaseIndex = uc.read(mineMinDistSqToBaseIndexCh);
        mineMaxDistSqToBase      = uc.read(mineMaxDistSqToBaseCh);
        mineMaxDistSqToBaseIndex = uc.read(mineMaxDistSqToBaseIndexCh);
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
        if (!isMiner) {
            for(int i = 0; i < nMine; ++i) {
                if (miners[i] < nMinerPerMine) {
                    myMineIndex          = i;
                    myMineLocCh          = nMineCh + channelsPerMine*myMineIndex + 1;
                    myMineMinerCh        = myMineLocCh + 1;
                    myMineDistSqToBaseCh = myMineLocCh + 2;
                    uc.write(myMineMinerCh, uc.read(myMineMinerCh) + 1);
                    myMine     = mineLocations[i];
                    miners[i]  = miners[i] + 1;
                    isJobless  = false;
                    isMiner    = true;
                    //uc.println("Worker ID" + ID + " assigned as miner at (" + myMine.x + ", " + myMine.y + ")");
                    return;
                }
            }
        }
    }

    // try to assign a worker to a free town
    void assignTown() {
        if (isMiner) {
            // TODO: assign conquered town that is closest to myMine
            int minDistSquared = mineDistSqToBase[myMineIndex];
            boolean townIsCloser = false;
            for(int i = 0; i < nTown; ++i) {
                if (townOwned[i]) {
                    int townLocCh = nTownCh + channelsPerTown*i + 1;
                    Location townLoc = tools.decodeLocation(uc.read(townLocCh));
                    int townDistSqToMyMine = townLoc.distanceSquared(myMine);
                    if (townDistSqToMyMine < minDistSquared) {
                        minDistSquared = townDistSqToMyMine;
                        myTownIndex = i;
                        townIsCloser = true;
                    }
                }
            }
            if (townIsCloser) {
                hasTown = true;
                myTown = townLocations[myTownIndex];
                myTownLocCh = nTownCh + channelsPerTown*myTownIndex + 1;
            } else {
                hasTown = false;
            }
        }
    }

    void assignJobless() {
        if (!isMiner) {
            isJobless = true;
        }
    }

}
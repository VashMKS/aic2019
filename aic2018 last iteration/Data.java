package betav4;

import aic2018.*;

public class Data {

    UnitController uc;

    // Comm Channels
    int UnitsCh;                // Ch 0, 1, 2
    int unitReportCh;           // Ch 0, 1, 2
    int unitResetCh;            // Ch 0, 1, 2
    int workerCh;               // Ch 3, 4, 5
    int workerReportCh;         // Ch 3, 4, 5
    int workerResetCh;          // Ch 3, 4, 5
    int barracksCh;             // Ch 6, 7, 8
    int barracksReportCh;       // Ch 6, 7, 8
    int barracksResetCh;        // Ch 6, 7, 8
    int warriorCh;              // Ch 9, 10, 11
    int warriorReportCh;        // Ch 9, 10, 11
    int warriorResetCh;         // Ch 9, 10, 11
    int archerCh;               // Ch 12, 13, 14
    int archerReportCh;         // Ch 12, 13, 14
    int archerResetCh;          // Ch 12, 13, 14
    int knightCh;               // Ch 15, 16, 17
    int knightReportCh;         // Ch 15, 16, 17
    int knightResetCh;          // Ch 15, 16, 17
    int ballistaCh;             // Ch 18, 19, 20
    int ballistaReportCh;       // Ch 18, 19, 20
    int ballistaResetCh;        // Ch 18, 19, 20
    int treeReportCh;           // Ch 21, 22, 23
    int treeResetCh;            // Ch 21, 22, 23
    int treeCh;                 // Ch 21, 22, 23
    int activeWorkerReportCh;   // Ch 24, 25, 26
    int activeWorkerResetCh;    // Ch 24, 25, 26
    int activeWorkerCh;         // Ch 24, 25, 26
    int workerXReportCh;        // Ch 27, 28, 29
    int workerXResetCh;         // Ch 27, 28, 29
    int workerXCh;              // Ch 27, 28, 29
    int workerYReportCh;        // Ch 30, 31, 32
    int workerYResetCh;         // Ch 30, 31, 32
    int workerYCh;              // Ch 30, 31, 32
    int scoutReportCh;          // Ch 33, 34, 35
    int scoutResetCh;           // Ch 33, 34, 35
    int scoutCh;                // Ch 33, 34, 35
    int plantedTreesCh = 50;    // Ch 50
    int enemyFoundCh = 51;      // Ch 51
    int enemyLocCh = 52;        // Ch 52
    int enemyContactCh = 53;    // Ch 53
    int workerBarycenterCh = 54;// Ch 54
    int firstEnemyBaseCh = 99;  // Ch 99
    int enemyBase0Ch = 100;     // Ch 100+

    // Comm Info
    int nUnits;
    int nWorker;
    int nBarracks;
    int nWarrior;
    int nArcher;
    int nKnight;
    int nBallista;
    int nTrees;
    int nActiveWorker;
    int nPlantedTrees;
    int nAttackUnit;
    int nScout;
    int workerX;
    int workerY;
    int workerBarycenter;
    boolean enemyFound;
    int enemyLoc;
    int firstEnemyBase;

    // Random Info
    Team allyTeam;
    Team enemyTeam;
    Direction[] dirs;
    UnitType[] types;
    Location[] enemyBases;
    int nEnemyBases;
    int currentRound;
    int VP;
    int enemyVP;
    int turnsAlive;

    // Parameters
    final int INF = Integer.MAX_VALUE;
    final int minimumTreeHealth = GameConstants.SMALL_TREE_CHOPPING_DMG;
    boolean poorEconomy;
    boolean growthEconomy;
    boolean stableEconomy;
    boolean richEconomy;
    boolean overflowingEconomy;
    boolean loneWorker;
    boolean activeWorker;
    boolean isScout;
    boolean enemyContact;

    public Data(){}

    public Data(UnitController _uc) {
        uc = _uc;
        allyTeam = uc.getTeam();
        enemyTeam = uc.getOpponent();
        dirs = Direction.values();
        types = UnitType.values();
        currentRound = uc.getRound();
        enemyBases = enemyTeam.getInitialLocations();
        nEnemyBases = enemyBases.length;
        turnsAlive = 0;

        // Worker variables
        loneWorker = false;
        activeWorker = false;
    }

    // This function is called once per turn
    public void Update() {

        // General Updates
        turnsAlive += 1;
        VP = allyTeam.getVictoryPoints();
        enemyVP = enemyTeam.getVictoryPoints();

        // Update economy stats
        poorEconomy = (uc.getResources() < 300);
        growthEconomy = (uc.getResources() > 200)&&(poorEconomy);
        stableEconomy = (uc.getResources() > 500);
        richEconomy = (uc.getResources() > 1000);
        overflowingEconomy = (uc.getResources() > 3000);

        // Update Comm Channels
        currentRound = uc.getRound();
        int x = currentRound%3;
        int y = (currentRound+1)%3;
        int z = (currentRound+2)%3;
        unitReportCh = x;
        unitResetCh = y;
        UnitsCh = z;
        workerReportCh = 3 + x;
        workerResetCh = 3 + y;
        workerCh = 3 + z;
        barracksReportCh = 6 + x;
        barracksResetCh = 6 + y;
        barracksCh = 6 + z;
        warriorReportCh = 9 + x;
        warriorResetCh = 9 + y;
        warriorCh = 9 + x;
        archerReportCh = 12 + x;
        archerResetCh = 12 + y;
        archerCh = 12 + z;
        knightReportCh = 15 + x;
        knightResetCh = 15 + y;
        knightCh = 15 + z;
        ballistaReportCh = 18 + x;
        ballistaResetCh = 18 + y;
        ballistaCh = 18 + z;
        treeReportCh = 21 + x;
        treeResetCh = 21 + y;
        treeCh = 21 + z;
        activeWorkerReportCh = 24 + x;
        activeWorkerResetCh = 24 + y;
        activeWorkerCh = 24 + z;
        workerXReportCh = 27 + x;
        workerXResetCh = 27 + y;
        workerXCh = 27 + z;
        workerYReportCh = 30 + x;
        workerYResetCh = 30 + y;
        workerYCh = 30 + z;
        scoutReportCh = 33 + x;
        scoutResetCh = 33 + y;
        scoutCh = 33 + z;

        // Fetch Comm Info
        nUnits = uc.read(UnitsCh);
        nWorker = uc.read(workerCh);
        nBarracks = uc.read(barracksCh);
        nWarrior = uc.read(warriorCh);
        nArcher = uc.read(archerCh);
        nKnight = uc.read(knightCh);
        nBallista = uc.read(ballistaCh);
        nTrees = uc.read(treeCh);
        nActiveWorker = uc.read(activeWorkerCh);
        nPlantedTrees = uc.read(plantedTreesCh);
        nAttackUnit = nUnits - nWorker;
        workerX = uc.read(workerXCh);
        workerY = uc.read(workerYCh);
        workerBarycenter = uc.read(workerBarycenterCh);
        enemyFound = (uc.read(enemyFoundCh) == 1);
        enemyLoc = uc.read(enemyLocCh);
        nScout = uc.read(scoutCh);

        // Assign scouts
        if (uc.getType() == UnitType.KNIGHT && !isScout) {
            if (nScout < 2) {
                isScout = true;
                uc.write(scoutCh, nScout + 1);
            }
        }

        // Encode the enemy base on turn 1
        if (currentRound == 1) {
            enemyLoc = enemyTeam.getInitialLocations()[0].x*1000 + enemyTeam.getInitialLocations()[0].y;
            uc.write(enemyLocCh, enemyLoc);
            firstEnemyBase = 0;
        }

        // Decide which is the first nonempty enemy base
        for (int i = firstEnemyBase; i < nEnemyBases; i++) {
            if (uc.read(enemyBase0Ch + i) == 0) {
                uc.write(firstEnemyBaseCh, i);
                firstEnemyBase = i;
                break;
            }
        }

        // Reset enemyContact every 100 rounds
        if (currentRound%100 == 0) {
            uc.write(enemyContactCh, 0);
        }

        enemyContact = (uc.read(enemyContactCh) == 1);

    }

}

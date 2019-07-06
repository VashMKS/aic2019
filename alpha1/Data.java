package alpha1;

import aic2019.*;

public class Data {

    UnitController uc;

    // Comm Channels
    int UnitsCh, unitReportCh, unitResetCh;                     // Ch 0, 1, 2
    int workerCh, workerReportCh, workerResetCh;                // Ch 3, 4, 5
    int explorerCh, explorerReportCh, explorerResetCh;          // Ch 6, 7, 8
    int soldierCh, soldierReportCh, soldierResetCh;             // Ch 9, 10, 11
    int archerCh, archerReportCh, archerResetCh;                // Ch 12, 13, 14
    int knightCh, knightReportCh, knightResetCh;                // Ch 15, 16, 17
    int mageCh, mageReportCh, mageResetCh;                      // Ch 18, 19, 20
    int catapultCh, catapultReportCh, catapultResetCh;          // Ch 21, 22, 23
    int barracksCh, barracksReportCh, barracksResetCh;          // Ch 24, 25, 26
    int towerCh, towerReportCh, towerResetCh;                   // Ch 27, 28, 29
    int combatUnitCh, combatUnitReportCh, combatUnitResetCh;    // Ch 30, 31, 32

    int nMineCh = 1000;                                         // Ch 1000
    int nTownCh = 6000;                                      // Ch 6000

    int enemyFoundCh = 51;      // Ch 51
    int enemyLocCh = 52;        // Ch 52
    int enemyContactCh = 53;    // Ch 53

    // Comm Info
    int nUnits;
    int nCombatUnit;
    int nWorker;
    int nExplorer;
    int nSoldier;
    int nArcher;
    int nKnight;
    int nMage;
    int nCatapult;
    int nBarracks;
    int nTower;
    int nMine;
    int nTown;

    boolean enemyFound;
    int enemyLoc;

    // Random Info
    Team allyTeam;
    Team enemyTeam;
    Direction[] dirs;
    UnitType[] types;
    Location enemyBase;
    Location allyBase;
    int currentRound;
    int VP;
    int enemyVP;
    int turnsAlive;

    // Parameters
    final int INF = Integer.MAX_VALUE;
    boolean enemyContact;

    //Worker Parameters
    int myMine;
    boolean miner;
    boolean townFolk;
    boolean delivering;

    public Data (UnitController _uc) {
        uc = _uc;
        allyTeam = uc.getTeam();
        enemyTeam = uc.getOpponent();
        dirs = Direction.values();
        types = UnitType.values();
        currentRound = uc.getRound();
        enemyBase = enemyTeam.getInitialLocation();
        allyBase = allyTeam.getInitialLocation();
        turnsAlive = 0;

        //Worker stuff
        if (uc.getType() == UnitType.WORKER) {
            miner = false; townFolk = false; delivering = false;
            assignMine();
            if (!miner) assignTown();
        }
    }

    // This function is called once per turn
    public void Update() {

        // General Updates
        turnsAlive += 1;
        VP = allyTeam.getVictoryPoints();
        enemyVP = enemyTeam.getVictoryPoints();

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

        explorerReportCh = 6 + x;
        explorerResetCh = 6 + y;
        explorerCh = 6 + z;

        soldierReportCh = 9 + x;
        soldierResetCh = 9 + y;
        soldierCh = 9 + x;

        archerReportCh = 12 + x;
        archerResetCh = 12 + y;
        archerCh = 12 + z;

        knightReportCh = 15 + x;
        knightResetCh = 15 + y;
        knightCh = 15 + z;

        mageReportCh = 18 + x;
        mageResetCh = 18 + y;
        mageCh = 18 + z;

        catapultReportCh = 21 + x;
        catapultResetCh = 21 + y;
        catapultCh = 21 + z;

        barracksReportCh = 24 + x;
        barracksResetCh = 24 + y;
        barracksCh = 24 + z;

        towerReportCh = 27 + x;
        towerResetCh = 27 + y;
        towerCh = 27 + z;

        combatUnitReportCh = 30 + x;
        combatUnitResetCh = 30 + y;
        combatUnitCh = 30 + z;

        // Fetch Comm Info
        nUnits = uc.read(UnitsCh);
        nWorker = uc.read(workerCh);
        nExplorer = uc.read(explorerCh);
        nSoldier = uc.read(soldierCh);
        nArcher = uc.read(archerCh);
        nKnight = uc.read(knightCh);
        nMage = uc.read(mageCh);
        nCatapult = uc.read(catapultCh);
        nBarracks = uc.read(barracksCh);
        nTower = uc.read(towerCh);
        nCombatUnit = uc.read(combatUnitCh);
        nMine = uc.read(nMineCh);
        nTown = uc.read(nTownCh);

        enemyFound = (uc.read(enemyFoundCh) == 1);
        enemyLoc = uc.read(enemyLocCh);

        // Reset enemyContact every 100 rounds
        if (currentRound%100 == 0) {
            uc.write(enemyContactCh, 0);
        }

        enemyContact = (uc.read(enemyContactCh) == 1);

    }

    void assignMine(){
        for(int i = 0; i < nMine; ++i){
            if ( (uc.read(nMineCh + 2 +2*i)) < 2){
                uc.write(nMineCh + 2 +2*i, uc.read(nMineCh + 2 + 2*i) + 1);
                myMine =  uc.read(nMineCh + 1 + 2*i) ;
                miner = true;
                return;
            }

        }
    }

    void assignTown(){

        townFolk = true;

    }

}

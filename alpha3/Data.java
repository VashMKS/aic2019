package alpha3;

import aic2019.*;

public class Data {

    UnitController uc;
    Tools tools;

    // Comm Channels (dynamic)
    int UnitsCh,        unitReportCh,       unitResetCh;        // Ch 0, 1, 2
    int workerCh,       workerReportCh,     workerResetCh;      // Ch 3, 4, 5
    int explorerCh,     explorerReportCh,   explorerResetCh;    // Ch 6, 7, 8
    int soldierCh,      soldierReportCh,    soldierResetCh;     // Ch 9, 10, 11
    int archerCh,       archerReportCh,     archerResetCh;      // Ch 12, 13, 14
    int knightCh,       knightReportCh,     knightResetCh;      // Ch 15, 16, 17
    int mageCh,         mageReportCh,       mageResetCh;        // Ch 18, 19, 20
    int catapultCh,     catapultReportCh,   catapultResetCh;    // Ch 21, 22, 23
    int barracksCh,     barracksReportCh,   barracksResetCh;    // Ch 24, 25, 26
    int towerCh,        towerReportCh,      towerResetCh;       // Ch 27, 28, 29
    int combatUnitCh,   combatUnitReportCh, combatUnitResetCh;  // Ch 30, 31, 32

    // Comm Channels (static)
    int enemyFoundCh    = 100;                                  // Ch 100
    int enemyLocCh      = 101;                                  // Ch 101
    int enemyContactCh  = 102;                                  // Ch 102
    int nMineCh         = 1000;                                 // Ch 1000
    int nTownCh         = 6000;                                 // Ch 6000

    // General Info
    final int INF = Integer.MAX_VALUE;
    final float ironMultiplier = GameConstants.INITIAL_IRON_VALUE;
    final float crystalMultiplier = GameConstants.INITIAL_CRYSTAL_VALUE;
    int ID;
    Team allyTeam;
    Team enemyTeam;
    Direction[] dirs;
    UnitType[] types;
    Location enemyBase;
    Location allyBase;
    int currentRound;
    int VP;
    int enemyVP;
    int spawnRound;
    int turnsAlive;

    // Unit Info
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

    // Mine Info
    int nMine;
    int nMinesLastKnown;
    Location[] mineLocations;
    int[] miners;

    // Town Info
    int nTown;
    int nTownLastTurn;
    Location[] townLocations;
    int[] townsfolk;

    // Enemy Intel
    boolean enemyFound;
    Location enemyLoc;
    boolean enemyContact;

    //Worker variables
    Location myMine;
    boolean isMiner;
    boolean isTownsfolk;
    boolean onDelivery;

    // Constructor
    public Data (UnitController _uc) {
        uc = _uc;
        tools = new Tools(uc, this);
        ID = uc.getInfo().getID();
        allyTeam = uc.getTeam();
        enemyTeam = uc.getOpponent();
        dirs = Direction.values();
        types = UnitType.values();
        currentRound = uc.getRound();
        spawnRound = currentRound;
        enemyBase = enemyTeam.getInitialLocation();
        allyBase = allyTeam.getInitialLocation();
        turnsAlive = 0;

        // Worker constructor
        if (uc.getType() == UnitType.WORKER) {
            isMiner = false;
            isTownsfolk = false;
            onDelivery = false;
        }
    }

    // This function is called once per turn
    public void update() {
        updateRound();
        updateChannels();
        updateUnitInfo();
        updateMines();
        updateTowns();
        updateEnemyIntel();
    }

    void updateMines() {
        //nMinesLastKnown = nMine; // no use for it unless we use ArrayList instead of Array
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
        //nTownLastKnown  = nTown; // no use for it unless we use ArrayList instead of Array
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

    void updateWorker() {
        if (!isMiner) assignMine();
        if (!isMiner && !isTownsfolk) assignTown();
    }

    void assignMine() {
        for(int i = 0; i < nMine; ++i) {
            if (miners[i] < 2) {
                int minersChannel = nMineCh + 2 + 2*i;
                uc.write(minersChannel, uc.read(minersChannel) + 1);
                miners[i] = miners[i] + 1;
                myMine =  mineLocations[i];
                uc.println("Worker ID" + ID + " assigned as isMiner at (" + myMine.x + ", " + myMine.y + ")");
                isMiner = true;
                return;
            }
        }
    }

    void assignTown() {

        isTownsfolk = true;

    }

    void updateEnemyIntel() {
        enemyFound = (uc.read(enemyFoundCh) == 1);
        enemyLoc = tools.decrypt(uc.read(enemyLocCh));

        // Reset enemyContact every 100 rounds
        if (currentRound%100 == 0) {
            uc.write(enemyContactCh, 0);
        }

        enemyContact = (uc.read(enemyContactCh) == 1);
    }

    void updateRound() {
        currentRound = uc.getRound();
        turnsAlive = currentRound - spawnRound;
        VP = allyTeam.getVictoryPoints();
        enemyVP = enemyTeam.getVictoryPoints();
    }

    void updateChannels() {
        int x = currentRound%3;
        int y = (currentRound+1)%3;
        int z = (currentRound+2)%3;

        unitReportCh        = x;
        unitResetCh         = y;
        UnitsCh             = z;
        workerReportCh      = 3 + x;
        workerResetCh       = 3 + y;
        workerCh            = 3 + z;
        explorerReportCh    = 6 + x;
        explorerResetCh     = 6 + y;
        explorerCh          = 6 + z;
        soldierReportCh     = 9 + x;
        soldierResetCh      = 9 + y;
        soldierCh           = 9 + x;
        archerReportCh      = 12 + x;
        archerResetCh       = 12 + y;
        archerCh            = 12 + z;
        knightReportCh      = 15 + x;
        knightResetCh       = 15 + y;
        knightCh            = 15 + z;
        mageReportCh        = 18 + x;
        mageResetCh         = 18 + y;
        mageCh              = 18 + z;
        catapultReportCh    = 21 + x;
        catapultResetCh     = 21 + y;
        catapultCh          = 21 + z;
        barracksReportCh    = 24 + x;
        barracksResetCh     = 24 + y;
        barracksCh          = 24 + z;
        towerReportCh       = 27 + x;
        towerResetCh        = 27 + y;
        towerCh             = 27 + z;
        combatUnitReportCh  = 30 + x;
        combatUnitResetCh   = 30 + y;
        combatUnitCh        = 30 + z;
    }

    void updateUnitInfo() {
        nUnits      = uc.read(UnitsCh);
        nCombatUnit = uc.read(combatUnitCh);
        nWorker     = uc.read(workerCh);
        nExplorer   = uc.read(explorerCh);
        nSoldier    = uc.read(soldierCh);
        nArcher     = uc.read(archerCh);
        nKnight     = uc.read(knightCh);
        nMage       = uc.read(mageCh);
        nCatapult   = uc.read(catapultCh);
        nBarracks   = uc.read(barracksCh);
        nTower      = uc.read(towerCh);
    }

}

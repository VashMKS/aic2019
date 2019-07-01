package betav4;

import aic2018.Location;
import aic2018.UnitController;

public class Knight extends AttackUnit {

    public Knight(UnitController _uc) {
        super(_uc);
    }

    @Override
    void run() {

        //Update our info according to the Comm Channel
        data.Update();

        //Try to attack before movement
        attack();
        gatherVP();

        //Movement
        move();
        dash();

        //Try to attack after movement
        attack();
        attackOak();
        gatherVP();

        //Report intel to the Comm Channel
        report();
    }

    @Override
    void report() {
        reportMyself();
        reportEnemies();
        reportEnemyBases();
    }

    @Override
    void reportMyself() {
        // Report to the Comm Channel
        uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
        uc.write(data.knightReportCh, uc.read(data.knightReportCh)+1);
        // Reset Next Slot
        uc.write(data.unitResetCh, 0);
        uc.write(data.knightResetCh, 0);
        // Report if scout
        if (data.isScout) {
            uc.write(data.scoutReportCh, uc.read(data.scoutReportCh)+1);
            uc.write(data.scoutResetCh, 0);
        }
    }

    @Override
    void move() {
        Location target;
        if (data.enemyFound) {
                target = tools.Decrypt(data.enemyLoc);
        } else if (data.isScout) {
            target = data.enemyBases[data.firstEnemyBase];
        } else if (data.currentRound < 600) {
            Location enemyBase = tools.Barycenter(data.enemyBases);
            Location allyBase = new Location(data.workerX/data.nWorker,data.workerY/data.nWorker);
            target = tools.BarCoord(new Location[] {enemyBase, allyBase}, new int[] {1,3});
        } else {
            target = data.enemyBases[data.firstEnemyBase];
        }
        tools.MoveTo(target);
    }

    void dash() {

    }

}
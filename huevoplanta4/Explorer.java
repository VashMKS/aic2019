package huevoplanta4;

import aic2019.UnitController;

public class Explorer extends CombatUnit {

    public Explorer (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
        this.movement = new Movement(uc, data);
    }

    void run() {

        while (true) {

            data.update();

            report();

            move();

            uc.yield();
        }
    }

    @Override
    void reportMyself() {
        // Report to the Comm Channel
        uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
        uc.write(data.explorerReportCh, uc.read(data.explorerReportCh)+1);
        //uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh)+1);
        // Reset Next Slot
        uc.write(data.unitResetCh, 0);
        uc.write(data.explorerResetCh, 0);
        //uc.write(data.combatUnitResetCh, 0);
        if (data.isTownScout) {
            // Report to the Comm Channel
            uc.write(data.townScoutReportCh, uc.read(data.townScoutReportCh) + 1);
            // Reset Next Slot
            uc.write(data.townScoutResetCh, 0);
        }
    }

    @Override
    void move() {
        if(!movement.doMicro(data.prefDir)) {

            if (data.isTownScout) {
                movement.moveTo(data.townLocations[data.townScoutCurrentTownIndex]);
            } else {
                if (uc.canMove(data.prefDir)) uc.move(data.prefDir);
                else {
                    double r = Math.random();
                    boolean done = false;
                    if (r < 0.5) {
                        for (int i = 0; i < 8; ++i) {
                            if (done) continue;
                            data.prefDir = data.prefDir.rotateLeft();
                            if (uc.canMove(data.prefDir)) {
                                uc.move(data.prefDir);
                                done = true;
                            }
                        }
                    } else {
                        for (int i = 0; i < 8; ++i) {
                            if (done) continue;
                            data.prefDir = data.prefDir.rotateRight();
                            if (uc.canMove(data.prefDir)) {
                                uc.move(data.prefDir);
                                done = true;
                            }
                        }
                    }
                }
            }
        } else {
            double r = Math.random();
            if (r <0.5) data.prefDir = data.prefDir.rotateLeft();
            else data.prefDir = data.prefDir.rotateRight();
        }

    }


}
package huevoplanta3;

import aic2019.Direction;
import aic2019.Location;
import aic2019.UnitController;
import aic2019.UnitType;

public class Worker extends MovingUnit {

    public Worker (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
        this.movement = new Movement(uc, data);
    }

    void run() {

        while (true){

            data.update();

            report();

            //logs();

            gather();

            deliver();

            repair();

            buildTower();

            move();

            deliver();

            repair();

            uc.yield();
        }

    }

    @Override
    void reportMyself() {
        // Report to the Comm Channel
        uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
        uc.write(data.workerReportCh, uc.read(data.workerReportCh)+1);
        // Reset Next Slot
        uc.write(data.unitResetCh, 0);
        uc.write(data.workerResetCh, 0);
        if (data.isMiner) {
            // Report to the Comm Channel
            uc.write(data.minerReportCh, uc.read(data.minerReportCh)+1);
            uc.write(data.myMineMinerReportCh, uc.read(data.myMineMinerReportCh)+1);
            // Reset Next Slot
            uc.write(data.minerResetCh, 0);
            uc.write(data.myMineMinerResetCh, 0);
        }
    }

    void logs() {
        if (data.currentRound % 100 == 1) {
            uc.println("worker ID" + data.ID + "assigned to mine " + data.myMineIndex +
                    " at (" + data.myMine.x + ", " + data.myMine.y);
            uc.println("myMineMinerReportCh = " + data.myMineMinerReportCh + " reads " + uc.read(data.myMineMinerReportCh));
            uc.println("myMineMinerResetCh = " + data.myMineMinerResetCh + " reads " + uc.read(data.myMineMinerResetCh));
            uc.println("myMineMinerCh = " + data.myMineMinerCh + " reads " + uc.read(data.myMineMinerCh));
        }
    }

    void deliver(){
        if(tools.alliesAround(2, UnitType.BASE) > 0){
            Direction d = uc.getLocation().directionTo(data.allyBase);
            if(uc.canDeposit(d)) {
                //uc.println("resources delivered successfully");
                uc.deposit(d);
                data.onDelivery = false;
            }
        }
    }

    void repair() {
        // TODO: if adjacent to an owned town repair it
    }

    void gather(){
        if(uc.canGather()) uc.gather();
    }

   @Override
    void move() {

        Direction dir = tools.randomDir();
        Location target = uc.getLocation().add(dir);

        if (data.isMiner) {
            // TODO: make that 50 less arbitrary (maybe depend on distance to the base)
            if (data.onDelivery || tools.currency(uc.getInfo().getWood(), uc.getInfo().getIron(), uc.getInfo().getCrystal()) > 50) {

                if (data.hasTown) target = data.myTown;
                else target = data.allyBase;

                data.onDelivery = true;
                //uc.println("Worker ID" + data.ID + " on delivery towards (" + target.x + ", " + target.y + ")");

            } else {
                target = data.myMine;
                //uc.println("Worker ID" + data.ID + " headed to mine at (" + data.myMine.x + ", " + data.myMine.y + ")");
            }
        }

        //uc.println("target: (" + target.x + ", " + target.y + ")");
       if(!movement.doMicro(uc.getLocation().directionTo(target))){
           //uc.drawLine(uc.getLocation(), target, "#0000ff" );
           movement.moveTo(target);
       }

    }

    void buildTower(){

        Location myLoc = uc.getLocation();

        if(data.requestedWood > 0 && data.requestedIron > 0) return;
        if(uc.getWood() < 1000 && uc.getIron() < 500) return;

        if (myLoc.isEqual(data.myMine) ) {

            // TODO: mark mine as "fortified" when all 3 towers have been built (or tried)
            Direction target = myLoc.directionTo(data.allyBase).opposite();
            boolean done = false;

            for (int i = 0; i < 3; ++i) {

                if (i == 1) target = target.rotateLeft();
                if (i == 2) target = target.rotateRight();

                if (!done && uc.canSpawn(target, UnitType.TOWER)) {
                    uc.spawn(target, UnitType.TOWER);
                    done = true;
                }

            }
        }
    }

    void panic() {
        if(uc.senseUnits(data.allyTeam, true).length > 0 ){
            for(Direction d : data.dirs){
                if (!d.isEqual(uc.getLocation().directionTo(data.allyBase)) && uc.canSpawn(d, UnitType.TOWER)) {
                    uc.spawn(d, UnitType.TOWER);
                }

            }

        }
    }

}

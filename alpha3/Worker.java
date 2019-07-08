package alpha3;

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
            data.updateWorker();

            report();

            gather();

            deliver();

            buildTower();

            move();

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
    }

    void deliver(){
        if(tools.alliesAround(2, UnitType.BASE) > 0){
            Direction d = uc.getLocation().directionTo(data.allyBase);
            if(uc.canDeposit(d)) {
                uc.println("resources delivered successfully");
                uc.deposit(d);
                data.onDelivery = false;
            }
        }
    }

    void gather(){
        if(uc.canGather()) uc.gather();
    }

   @Override
    void move() {

        Direction dir = tools.randomDir();
        Location target = uc.getLocation().add(dir);

        if (data.isMiner) {

            if (data.onDelivery || tools.currency(uc.getInfo().getWood(), uc.getInfo().getIron(), uc.getInfo().getCrystal()) > 10) {
                target = data.allyBase;
                data.onDelivery = true;
                //uc.println("Worker ID" + data.ID + " on delivery");

            } else {
                target = data.myMine;
                //uc.println("Worker ID" + data.ID + " headed to mine at (" + data.myMine.x + ", " + data.myMine.y + ")");
            }

        }

        //uc.println("target: (" + target.x + ", " + target.y + ")");
        movement.moveTo(target);

    }

    void buildTower(){

        Location myLoc = uc.getLocation();

        if (myLoc.isEqual(data.myMine) ) {

            for (int i = 0; i < 3; ++i) {

                Direction target = myLoc.directionTo(data.allyBase).opposite();
                if (i == 1) target = target.rotateLeft();
                if (i == 2) target = target.rotateRight();

                if (uc.canSpawn(target, UnitType.TOWER)) {
                    uc.spawn(target, UnitType.TOWER);
                }

            }
        }

        if(uc.senseUnits(data.allyTeam, true).length > 0 ){
            for(Direction d : data.dirs){
                if (uc.canSpawn(d, UnitType.TOWER)) {
                    uc.spawn(d, UnitType.TOWER);
                }

            }

        }
    }


}

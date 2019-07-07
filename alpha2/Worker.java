package alpha2;

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

            data.Update();
            data.UpdateWorker();

            report();

            gather();

            deliver();

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
                uc.deposit(d);
                data.delivering = false;
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

        if (data.miner) {

            if (uc.getWood() + 3*uc.getIron() + 10*uc.getCrystal() > 10) {
                target = data.allyBase;
                data.delivering = true;
            } else {
                target = tools.decrypt(data.myMine);
            }

            if (data.currentRound%100 == 6) {
                uc.println("My assigned mine is " + data.myMine);
                uc.println("currently heading towards (" + target.x + ", " + target.y + ")");
            }

            /*
            Location myMine = tools.decrypt(data.myMine);
            target = myMine;

            // if done gathering go back to base
            // TODO: deliver to the base or the nearest safe town
            if (data.delivering) target = data.allyBase;

            uc.println(uc.getLocation().x + ' ' + uc.getLocation().y + " - " + myMine.x + ' ' + myMine.y);

            if (uc.getLocation().isEqual(myMine) ) {
                uc.println("I'm mining here");
                uc.println("I have " + tools.alliesAround(2, UnitType.WORKER) + " mates around");
            }


            if(uc.getLocation().isEqual(myMine) && tools.alliesAround(2, UnitType.WORKER) > 0) {
                //TODO: mirar que el worker adjacent esta assignat a la teva mina
                target = data.allyBase;
                data.delivering = true;
            }*/
        }

        movement.moveTo(target);

    }



}

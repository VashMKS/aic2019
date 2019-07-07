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

        if(tools.matesAround(2, UnitType.BASE) > 0){
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

            uc.println("My assigned mine is " + data.myMine);

            if (uc.getIron() + uc.getWood() + uc.getCrystal() > 0) {
                target = data.allyBase;
            } else {
                target = tools.decrypt(data.myMine);
            }

            uc.println("I must go to: " + target.x + " " + target.y);

            /*
            Location myMine = tools.decrypt(data.myMine);
            target = myMine;

            // if done gathering go back to base
            // TODO: deliver to the base or the nearest safe town
            if (data.delivering) target = data.allyBase;

            uc.println(uc.getLocation().x + ' ' + uc.getLocation().y + " - " + myMine.x + ' ' + myMine.y);

            if (uc.getLocation().isEqual(myMine) ) {
                uc.println("I'm mining here");
                uc.println("I have " + tools.matesAround(2, UnitType.WORKER) + " mates around");
            }


            if(uc.getLocation().isEqual(myMine) && tools.matesAround(2, UnitType.WORKER) > 0) {
                //TODO: mirar que el worker adjacent esta assignat a la teva mina
                target = data.allyBase;
                data.delivering = true;
            }*/
        }

        movement.moveTo(target);

    }



}

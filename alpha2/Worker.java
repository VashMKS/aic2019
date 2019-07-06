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


    void move(){

        Location myMine = tools.decrypt(data.myMine);
        Location target = myMine;

        if(uc.getLocation() == myMine && tools.matesAround(2, UnitType.WORKER) > 0){
            //TODO: mirar que el worker adjacent esta asignat a la teva mina
            target = data.allyBase;
            data.delivering = true;
        }

        if (! data.delivering) target = data.allyBase;

        movement.moveTo(target);

    }



}

package alpha2;

import aic2019.Direction;
import aic2019.Location;
import aic2019.UnitController;
import aic2019.UnitType;

import java.awt.*;

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

            gather();

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


    void move(){

        if(data.delivering)uc.println("I'm delivering!");
        else uc.println("I'm Gathering!");

        Location myMine = tools.decrypt(data.myMine);
        Location target = myMine;

        if (data.delivering) {
            target = data.allyBase;
        }

        if(uc.getLocation().isEqual(myMine) && tools.matesAround(2, UnitType.WORKER) > 0) {
            //TODO: mirar que el worker adjacent esta asignat a la teva mina
            target = data.allyBase;
            data.delivering = true;
        }



        movement.moveTo(target);

    }



}

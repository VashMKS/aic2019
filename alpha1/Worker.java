package alpha1;

import aic2019.*;

public class Worker extends MovingUnit {

    public Worker (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.movement = new Movement(uc, data);
    }

    void run() {

        while (true){

            data.Update();

            report();

            /*Generate a random number from 0 to 7, both included*/
            int randomNumber = (int)(Math.random()*8);

            /*Get corresponding direction*/
            Direction dir = Direction.values()[randomNumber];

            /*move in direction dir if possible*/
            if (uc.canMove(dir)) uc.move(dir);

            /*If this unit is a base, try spawning a soldier at direction dir*/
            if (uc.getType() == UnitType.BASE) {
                if (uc.canSpawn(dir, UnitType.SOLDIER)) uc.spawn(dir, UnitType.SOLDIER);
            }

            /*Else, go through all visible units and attack the first one you see*/
            else {
                /*Sense all units not from my team, which includes opponent and neutral units*/
                UnitInfo[] visibleEnemies = uc.senseUnits(uc.getTeam(), true);
                for (int i = 0; i < visibleEnemies.length; ++i) {
                    if (uc.canAttack(visibleEnemies[i].getLocation())) uc.attack(visibleEnemies[i].getLocation());
                }
            }

            uc.yield(); //End of turn
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

}

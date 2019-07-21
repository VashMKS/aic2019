package huevoplanta4;

import aic2019.Location;
import aic2019.UnitController;

public class Catapult extends CombatUnit {

    public Catapult (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
        this.movement = new Movement(uc, data);
    }

    void run() {

        while (true) {

            data.update();

            report();

            attack();

            move();

            attack();

            uc.yield();

        }
    }


    @Override
    void move(){

        Location target = new Location();

        if(data.nCombatUnit > 14 && data.nCatapult >  2) data.armyReadyToSiege = true;
        if(data.nCombatUnit < 6  || data.nCatapult == 0) data.armyReadyToSiege = false;

        if(!data.armyReadyToSiege ){
            target.x = (3*data.allyBase.x + data.enemyBase.x)/4;
            target.y = (3*data.allyBase.y + data.enemyBase.y)/4;
        } else {
            if (data.towerFound) {
                target = data.towerLoc;
            }else if (data.neutralFound) {
                target = data.neutralLoc;
            }else{
                target.x = (data.allyBase.x + data.enemyBase.x)/2;
                target.y = (data.allyBase.y + data.enemyBase.y)/2;
            }
        }

        //uc.println("My target is at (" + target.x + ", " + target.y + "). towerFound = " + data.towerFound);


        if(!movement.doMicroCatapult(target)){
            //uc.drawLine(uc.getLocation(), target, "#0000ff" );
            movement.moveTo(target);
        }

    }

    void attack(){

        Location target = new Location();
        boolean readyToAttack = false;

        if (data.towerFound) {
            target = data.towerLoc;
            readyToAttack = true;
        }

        if(readyToAttack && uc.canAttack(target) ) uc.attack(target);


    }


    @Override
    void reportMyself() {
        // Report to the Comm Channel
        uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
        uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh)+1 );
        uc.write(data.catapultReportCh, uc.read(data.catapultReportCh)+1);
        // Reset Next Slot
        uc.write(data.unitResetCh, 0);
        uc.write(data.combatUnitResetCh, 0);
        uc.write(data.catapultResetCh, 0);
    }

}
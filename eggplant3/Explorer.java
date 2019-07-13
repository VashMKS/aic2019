package eggplant3;

import aic2019.Direction;
import aic2019.Location;
import aic2019.UnitController;
import aic2019.UnitInfo;

public class Explorer extends CombatUnit {

    public Explorer (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
        this.movement = new Movement(uc, data);
    }

    /* IDEA: [distributed mapping] always assign 1 explorer as Cartographer and some as Map Keepers
     * add to the Data class a Map object to be read every turn by each unit, the cartographer and keepers
     * are in charge of updating it (hopefully in a safe and cheap way)
     * The Cartographer keeps a list of positions that have never been visited ans tries to reach them
     * The MapKeepers keep a queue with previously explored positions ordered by last updated and refresh the map */

    void run() {

        while (true) {

            data.update();

            report();

            attack();

            move();

            attack();

            attackTowns();

            uc.yield();
        }
    }

    @Override
    void reportMyself() {
        // Report to the Comm Channel
        uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
        //uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh)+1);
        uc.write(data.explorerReportCh, uc.read(data.explorerReportCh)+1);
        // Reset Next Slot
        uc.write(data.unitResetCh, 0);
        //uc.write(data.combatUnitResetCh, 0);
        uc.write(data.explorerResetCh, 0);
    }

    @Override
    void move() {

        if(! movement.doMicro() ){
            Direction dir = tools.randomDir();
            if (uc.canMove(dir)) uc.move(dir);
        }

    }

    public void attack(){

        UnitInfo[] enemiesAround = uc.senseUnits(data.allyTeam, true);
        Location target = uc.getLocation();
        float priority = 0;

        for (UnitInfo unit : enemiesAround){

            if(!uc.canAttack(unit.getLocation()) ) continue;

            float unitPriority = targetPriority(unit);
            //prioriza atacar a matar
            if(unit.getHealth() <= uc.getType().attack) unitPriority += 50;
            else unitPriority = unitPriority * (float)(unit.getType().maxHealth/unit.getHealth());

            if (unitPriority > priority){
                priority = unitPriority;
                target = unit.getLocation();
            }
        }

        if (!target.isEqual(uc.getLocation()) ) uc.attack(target);

    }

}
package alpha5;

import aic2019.Location;
import aic2019.UnitController;
import aic2019.UnitInfo;
import aic2019.UnitType;

public class Soldier extends CombatUnit {

    public Soldier (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
        this.movement = new Movement(uc, data);
        this.combat = new Combat(uc, data, tools, movement);
    }

    void run() {

        while (true) {

            data.update();

            report();

            attack();

            move();

            attack();

            combat.attackTowns();

            uc.yield();
        }

    }

    @Override
    void reportMyself() {
        // Report to the Comm Channel
        uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
        uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh)+1);
        uc.write(data.soldierReportCh, uc.read(data.soldierReportCh)+1);
        // Reset Next Slot
        uc.write(data.unitResetCh, 0);
        uc.write(data.combatUnitResetCh, 0);
        uc.write(data.soldierResetCh, 0);
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

    public int targetPriority(UnitInfo unit) {

        if(unit.getType() == UnitType.MAGE)     return 10;
        if(unit.getType() == UnitType.SOLDIER)  return 9;
        if(unit.getType() == UnitType.ARCHER)   return 8;
        if(unit.getType() == UnitType.KNIGHT)   return 7;
        if(unit.getType() == UnitType.TOWER)    return 6;
        if(unit.getType() == UnitType.BASE)     return 5;
        if(unit.getType() == UnitType.CATAPULT) return 4;
        if(unit.getType() == UnitType.BARRACKS) return 3;
        if(unit.getType() == UnitType.EXPLORER) return 2;
        if(unit.getType() == UnitType.WORKER)   return 1;
        return 0;
    }


}

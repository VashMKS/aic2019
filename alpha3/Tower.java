package alpha3;

import aic2019.*;

public class Tower extends Structure implements StructureCombat {

    public Tower (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
    }

    void run() {

        while (true) {

            data.update();

            report();

            attack();

            uc.yield();
        }
    }

    public void attack(){

        UnitInfo[] enemiesAround = uc.senseUnits(data.allyTeam, true);
        Location target = uc.getLocation();
        int priority = 0;

        for (UnitInfo unit : enemiesAround){
            int unitPriority = targetPriority(unit);
            unitPriority = unitPriority * (unit.getType().maxHealth/unit.getHealth());

            if (unitPriority > priority){
                priority = unitPriority;
                target = unit.getLocation();
            }
        }

        if (!target.isEqual(uc.getLocation()) && uc.canAttack(target) ) uc.attack(target);

    }

    public int targetPriority(UnitInfo unit) {
        if(unit.getType() == UnitType.BASE)     return 10;
        if(unit.getType() == UnitType.BARRACKS) return 9;
        if(unit.getType() == UnitType.TOWER)    return 8;
        if(unit.getType() == UnitType.CATAPULT) return 7;
        if(unit.getType() == UnitType.MAGE)     return 6;
        if(unit.getType() == UnitType.SOLDIER)  return 5;
        if(unit.getType() == UnitType.ARCHER)   return 4;
        if(unit.getType() == UnitType.KNIGHT)   return 3;
        if(unit.getType() == UnitType.EXPLORER) return 2;
        if(unit.getType() == UnitType.WORKER)   return 1;
        return 0;
    }

}


package alpha2;

import aic2019.*;

public class Tower extends Structure implements StructureCombat {

    public Tower (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
    }

    void run() {

        while (true) {

            data.Update();

            report();

            attack();

            uc.yield();
        }
    }

    public void attack(){

        UnitInfo[] enemiesAround = uc.senseUnits(data.allyTeam, true);
        Location target = null;
        int priority = 0;

        for (UnitInfo unit : enemiesAround){
            int unitPriority = tools.attackPriorityStructure(unit);
            unitPriority = unitPriority / (unit.getHealth()/unit.getType().maxHealth);

            if (unitPriority > priority){
                priority = unitPriority;
                target = unit.getLocation();
            }
        }

        if (!target.isEqual(null) && uc.canAttack(target) ) uc.attack(target);

    }

    public int targetPriority(UnitInfo unit) {
        return 0;
    }

}


package alpha2;

import aic2019.*;

public class Base extends RecruitmentUnit implements StructureCombat {

    public Base (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
    }

    void run() {

        uc.println("Base is at (" + uc.getLocation().x + ", " + uc.getLocation().y + ")");

        while (true) {

            data.update();

            report();

            // logs every 100 rounds
            if (data.currentRound % 100 == 15) {
                uc.println("Round " + data.currentRound + " report:");
                for (int i = 0; i < data.nMine; i++) {
                    uc.println(data.nMine);
                    Location mineLoc = data.mineLocations[i];
                    uc.println(mineLoc);
                    int nMiners = data.miners[i];
                    uc.println("  - mine " + i + " is at (" + mineLoc.x + ", " + mineLoc.y + ") with " + nMiners + " miners");
                }
                for (int i = 0; i < data.nTown; i++) {
                    Location mineLoc = data.townLocations[i];
                    int nMiners = data.townsfolk[i];
                    uc.println("  - town " + i + " is at (" + mineLoc.x + ", " + mineLoc.y + ") with " + nMiners + " isTownsfolk");
                }
            }

            spawnUnits();

            attack();

            uc.yield();
        }

    }

    public void attack() {

        UnitInfo[] unitsAround = uc.senseUnits();
        Location target = uc.getLocation();
        int priority = 0;

        for (UnitInfo unit : unitsAround) {

            if (!unit.getTeam().equals(data.allyTeam)) {

                //TODO: falta mirar les caselles al voltant de les unitats enemigues
                int unitPriority = areaAttackPriority( unit.getLocation() );
                //uc.println("My target is at " + unit.getLocation().x + " " + unit.getLocation().y + " with priority " + unitPriority );

                if (unitPriority > priority) {
                    priority = unitPriority;
                    target = unit.getLocation();
                }
            }
        }

        if (!target.isEqual(uc.getLocation()) && uc.canAttack(target) ){
            //uc.println("I'm about to attack " + target.x + " " + target.y );
            uc.attack(target);
        }

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

    int areaAttackPriority(Location loc){
        UnitInfo[] unitsNearLoc = uc.senseUnits(loc, 2);
        int priority = 0;
        for (UnitInfo unit : unitsNearLoc) {
            if (unit.getTeam().equals(data.allyTeam)) priority -= 100;
            else {
                int p = targetPriority(unit);
                p = p * (unit.getType().maxHealth / unit.getHealth());
                priority += p;
            }
        }
        return priority;
    }

}

package alpha5;

import aic2019.*;

public class CombatUnit extends MovingUnit {

    Direction[] randomDirs;

    // placeholder, overriden by each unit type
    void attack() {
        return;
    }

    // ranks hostile unit types by target priority
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

    // attack a nearby non-ally town if possible
    public void attackTown(){

        TownInfo[] nearbyTowns = uc.senseTowns(data.allyTeam, true);
        Location target = uc.getLocation();
        float priority = 0;

        for (TownInfo town : nearbyTowns){
            float townPriority = (float) 1/town.getLoyalty();
            if (townPriority > priority) {
                priority = townPriority;
                target = town.getLocation();
            }
        }

        if (!target.isEqual(uc.getLocation()) && uc.canAttack(target) ) uc.attack(target);

    }

}

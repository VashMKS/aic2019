package alpha4;

import aic2019.Location;
import aic2019.UnitController;
import aic2019.UnitInfo;
import aic2019.UnitType;

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
                uc.println("  - currently there are " + data.nWanderer + " workers wandering around. " +
                           "Threshold is at " + data.workerHealthThreshold + " HP");
                uc.println("  - currently " + data.nMiner + " miners out of " + data.nWorker + " workers are active in " + data.nMine + " mines. " +
                           "Cap is at " + data.nMinerThreshold + " miners");
                for (int i = 0; i < data.nMine; i++) {
                    Location mineLoc = data.mineLocations[i];
                    int nMiners = data.miners[i];
                    uc.println("  - mine " + i + " is at (" + mineLoc.x + ", " + mineLoc.y + ") with " + nMiners + " miners");
                }
                uc.println("  - currently " + data.nTownsfolk + " townsfolk out of " + data.nWorker + " workers are active in " + data.nTown + " towns. " +
                           "Cap is at " + data.nTownsfolkThreshold + " townsfolk");
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
        float priority = 0;

        for (UnitInfo unit : unitsAround) {

            if (!unit.getTeam().equals(data.allyTeam)) {

                //TODO: falta mirar les caselles al voltant de les unitats enemigues
                float unitPriority = areaAttackPriority( unit.getLocation() );
                //uc.println("My target is at " + unit.getLocation().x + " " + unit.getLocation().y + " with priority " + unitPriority );

                if (unitPriority > priority) {
                    priority = unitPriority;
                    target = unit.getLocation();
                }
            }
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

    float areaAttackPriority(Location loc){

        if( loc.distanceSquared(uc.getLocation() ) <= 2 ) return -data.INF;

        UnitInfo[] unitsNearLoc = uc.senseUnits(loc, 2);
        float priority = 0;
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

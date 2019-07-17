package huevoplanta3;

import aic2019.*;

public class Base extends RecruitmentUnit {

    Market market;

    public Base (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
        this.market = new Market(uc, data);
    }

    void run() {

        //uc.println("Base is at (" + uc.getLocation().x + ", " + uc.getLocation().y + ")");

        while (true) {

            data.update();

            report();

            //logs();

            spawnUnits();

            attack();

            market.economy();

            uc.yield();
        }

    }

    void logs() {

        // recruitment logs
        /*if (data.currentRound < 25) {
            uc.println("current round: " + data.currentRound + ", x: " + data.x + ", y: " + data.y + ", z: " + data.z);
            uc.println("explorer report channel " + data.explorerReportCh + " reads " + uc.read(data.explorerReportCh));
            uc.println("explorer reset channel " + data.explorerResetCh + " reads " + uc.read(data.explorerResetCh));
            uc.println("explorer channel " + data.explorerCh + " reads " + uc.read(data.explorerCh));
        }*/

        // miner logs
        if (data.currentRound % 100 == 15) {
            uc.println("Round " + data.currentRound + " Worker Report:");
            uc.println("  - " + data.nMiner + " miners are active in " + data.nMine + " mines. " +
                       "Cap is at " + data.nMinerMax + " miners and " + data.nMineMax + " mines");
            for (int i = 0; i < data.nMine; i++) {
                Location mineLoc = data.mineLocations[i];
                uc.println("  - mine " + i + " (" + mineLoc.x + ", " + mineLoc.y + "), dist sq to base " +
                           data.mineDistSqToBase[i] + " and " + data.miners[i] + " miners");
            }
        }

        // town logs
        if (data.currentRound % 100 == 35) {
            uc.println("Round " + data.currentRound + " Town Report:");
            for (int i = 0; i < data.nTown; i++) {
                Location townLoc = data.townLocations[i];
                if (data.townOwned[i]) uc.println("  - town " + i + " at (" + townLoc.x + ", " + townLoc.y + ") is owned by us");
                else uc.println("  - town " + i + " at (" + townLoc.x + ", " + townLoc.y + ") is not owned by us");
            }
        }

        // army logs
        if (data.currentRound % 100 == 25) {
            uc.println("Round " + data.currentRound + " Army Report:\n  - Combat Units: " + data.nCombatUnit +
                    "\n  - Soldiers: " + data.nSoldier + " out of " + data.nRequestedSoldier +
                    "\n  - Archers: " + data.nArcher + " out of " + data.nRequestedArcher +
                    "\n  - Mages: " + data.nMage + " out of " + data.nRequestedMage);
        }
    }
    

    public void attack() {

        UnitInfo[] unitsAround = uc.senseUnits(data.allyTeam, true);
        Location target = uc.getLocation();
        float priority = 0;

        for (UnitInfo unit : unitsAround) {

            if(!uc.canAttack(unit.getLocation())) continue;

            //TODO: falta mirar les caselles al voltant de les unitats enemigues
            float unitPriority = areaAttackPriority( unit.getLocation() );
            //uc.println("My target is at " + unit.getLocation().x + " " + unit.getLocation().y + " with priority " + unitPriority );

            if (unitPriority > priority) {
                priority = unitPriority;
                target = unit.getLocation();
            }

        }
        if(!target.isEqual(uc.getLocation()) )uc.attack(target);
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

        if(loc.distanceSquared(uc.getLocation() ) <= 2) return -1000;

        UnitInfo[] unitsNearLoc = uc.senseUnits(loc, 2);
        float priority = 0;
        for (UnitInfo unit : unitsNearLoc) {
            if (unit.getTeam().equals(data.allyTeam)) priority -= 100;
            else {
                float p = targetPriority(unit);
                p = p * (float)(unit.getType().maxHealth / unit.getHealth());
                priority += p;
            }
        }
        return priority;
    }

}

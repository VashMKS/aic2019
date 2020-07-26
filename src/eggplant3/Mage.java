package eggplant3;

import aic2019.*;

public class Mage extends CombatUnit {

    public Mage (UnitController _uc) {
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

            attackTowns();

            uc.yield();
        }

    }

    public void attack() {

        UnitInfo[] unitsAround = uc.senseUnits(data.allyTeam, true);
        Location target = uc.getLocation();
        float priority = 0;

        for (UnitInfo unit : unitsAround) {

            for (Direction d : data.dirs) {

                Location loc = unit.getLocation().add(d);

                if (!uc.canAttack(loc)) continue;

                float unitPriority = areaAttackPriority(loc);
                //uc.println("My target is at " + unit.getLocation().x + " " + unit.getLocation().y + " with priority " + unitPriority );

                if (unitPriority > priority) {
                    priority = unitPriority;
                    target = loc;
                }
            }

        }
        if(!target.isEqual(uc.getLocation()) )uc.attack(target);
    }


    float areaAttackPriority(Location loc){

        if(loc.distanceSquared(uc.getLocation() ) <= 2) return -1000;

        UnitInfo[] unitsNearLoc = uc.senseUnits(loc, 2);
        float priority = 0;
        for (UnitInfo unit : unitsNearLoc) {
            if (unit.getTeam().equals(data.allyTeam)) priority -= 100;
            else {
                float p = targetPriority(unit);
                if (unit.getHealth() <= uc.getType().attack) p += 50;
                else p *=  (float)(unit.getType().maxHealth / unit.getHealth());
                priority += p;
            }
        }
        return priority;
    }

    @Override
    void reportMyself() {
        // Report to the Comm Channel
        uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
        uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh)+1);
        uc.write(data.mageReportCh, uc.read(data.mageReportCh)+1);
        // Reset Next Slot
        uc.write(data.unitResetCh, 0);
        uc.write(data.combatUnitResetCh, 0);
        uc.write(data.mageResetCh, 0);
    }

    @Override
    public void attackTowns(){

        TownInfo[] nearbyTowns = uc.senseTowns(data.allyTeam, true);
        Location target = uc.getLocation();
        float priority = 0;

        for (TownInfo town : nearbyTowns){

            if(!uc.canAttack(town.getLocation() ) ) continue;

            UnitInfo[] alliesAround = uc.senseUnits(town.getLocation(), 2, data.allyTeam, false);
            float townPriority = (float) 1/town.getLoyalty();
            townPriority -= 100*alliesAround.length;

            if(townPriority > priority){
                priority = townPriority;
                target = town.getLocation();
            }
        }

        if (!target.isEqual(uc.getLocation()) ) uc.attack(target);

    }



}
package berenjena1;

import aic2019.Location;
import aic2019.TownInfo;
import aic2019.UnitInfo;
import aic2019.UnitType;

public class CombatUnit extends MovingUnit {

    @Override
    void move(){

        chooseTownToAttack();

        Location target = new Location();

        if(data.nCombatUnit > 10) data.armyReadyToAttack = true;
        if(data.nCombatUnit < 6 ) data.armyReadyToAttack = false;

        if(!data.armyReadyToAttack ){
            target.x = (3*data.allyBase.x + data.enemyBase.x)/4;
            target.y = (3*data.allyBase.y + data.enemyBase.y)/4;
        } else {
            if (data.townToAttack != -1) {
                int townLocChannel = data.nTownCh + data.channelsPerTown * data.townToAttack + 1;
                target = tools.decodeLocation(uc.read(townLocChannel));
            }else if (data.neutralFound) {
                target = data.neutralLoc;
            } else if (data.enemyFound) {
                target = data.enemyLoc;
            } else{
                target.x = (data.allyBase.x + data.enemyBase.x)/2;
                target.y = (data.allyBase.y + data.enemyBase.y)/2;
            }
        }

        //uc.println("We are currently on Town " + data.townToAttack + " of " + data.nTown + ". My target is at " + target.x + " " + target.y);

        if(!movement.doMicro(uc.getLocation().directionTo(target))){
            uc.drawLine(uc.getLocation(), target, "#0000ff" );
            movement.moveTo(target);
        }

    }

    public void attackTowns(){

        TownInfo[] nearbyTowns = uc.senseTowns(data.allyTeam, true);
        Location target = uc.getLocation();
        float priority = 0;

        for (TownInfo town : nearbyTowns){
            float townPriority = (float) 1/town.getLoyalty();
            if(townPriority > priority){
                priority = townPriority;
                target = town.getLocation();
            }
        }

        if (!target.isEqual(uc.getLocation()) && uc.canAttack(target) ) uc.attack(target);

    }

    public void chooseTownToAttack(){

        boolean found = false;
        int minDist  = data.INF;

        for(int i = 0; i < data.nTown; ++i){
            if(!data.townOwned[i] && data.townDistSqToBase[i] < minDist){
                data.townToAttack = i;
                minDist = data.townDistSqToBase[i];
                found = true;
            }
        }

        if (!found) data.townToAttack = -1;

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

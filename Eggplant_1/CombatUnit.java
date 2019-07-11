package Eggplant_1;

import aic2019.Location;
import aic2019.TownInfo;

public class CombatUnit extends MovingUnit {

    Combat combat;

    @Override
    void move(){
        Location target = new Location();

        if(uc.getRound() <= 250 || data.nCombatUnit < 10 ){
            target.x = (3*data.allyBase.x + data.enemyBase.x)/4;
            target.y = (3*data.allyBase.y + data.enemyBase.y)/4;
        }else if(uc.getRound() <= 250 || data.nCombatUnit < 30){
            target.x = (data.allyBase.x + data.enemyBase.x)/2;
            target.y = (data.allyBase.y + data.enemyBase.y)/2;
        }else target = data.enemyBase;

        if(! movement.doMicro() ){
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

}

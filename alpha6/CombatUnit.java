package alpha5;

import aic2019.Location;

public class CombatUnit extends MovingUnit {

    Combat combat;

    @Override
    void move(){
        Location target = new Location();

        if(uc.getRound() <= 250 || data.nCombatUnit < 9){
            target.x = (3*data.allyBase.x + data.enemyBase.x)/4;
            target.y = (3*data.allyBase.y + data.enemyBase.y)/4;
        }else{
            target.x = (data.allyBase.x + data.enemyBase.x)/2;
            target.y = (data.allyBase.y + data.enemyBase.y)/2;
        }

        if(! movement.doMicro() ){
            uc.drawLine(uc.getLocation(), target, "#0000ff" );
            movement.moveTo(target);
        }

    }

}

package alpha5;

import aic2019.*;

public class Combat {

    UnitController uc;
    Data data;
    Tools tools;
    Movement movement;

    public Combat(UnitController _uc, Data _data, Tools _tools, Movement _movement) {
        this.uc = _uc;
        this.data = _data;
        this.tools = _tools;
        this.movement = _movement;
    }

    int targetPriority(UnitInfo unit) {
        return 0;
    }

    public void attackTowns(){
        TownInfo[] nearbyTowns = uc.senseTowns(data.allyTeam, true);

        Location target = uc.getLocation();
        float priority = 0;

        for (TownInfo town : nearbyTowns){
            int townPriority = 1/town.getLoyalty();
            if(townPriority > priority){
                priority = townPriority;
                target = town.getLocation();
            }
        }

        if (!target.isEqual(uc.getLocation()) && uc.canAttack(target) ) uc.attack(target);

    }



    void attack() {
        return;
    }

}

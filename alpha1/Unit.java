package alpha1;

import aic2019.*;

public class Unit {

    UnitController uc;
    Data data;
    Tools tools;

    void report() {
        reportMyself();
        reportEnemies();
        reportEnvironment();
    }

    void reportMyself() {
    }

    void reportEnemies () {
    }

    void reportEnvironment() {

        ResourceInfo[] minesAround = uc.senseResources();
        if(minesAround.length > 0) {
            for (ResourceInfo mineInfo : minesAround){
                Location mineLoc = mineInfo.getLocation();

                if (! tools.reportedMine( mineLoc )){
                    uc.write(data.nMineCh + 1 + 2*data.nMine, tools.encrypt(mineLoc.x,mineLoc.y) );
                    uc.write(data.nMineCh, data.nMine+1);
                }
            }
        }

        TownInfo[] townsAround = uc.senseTowns();
        if(townsAround.length > 0) {
            for (TownInfo townInfo : townsAround){
                Location townLoc = townInfo.getLocation();
                if (! tools.reportedTown( townLoc )){
                    uc.write(data.nTownCh + 1 + 2*data.nTown, tools.encrypt(townLoc.x,townLoc.y) );
                    uc.write(data.nTownCh, data.nTown+1);
                }
            }
        }



    }

}

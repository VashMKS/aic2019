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
                    uc.write(1001 + 2*data.nMine, tools.encrypt(mineLoc.x,mineLoc.y) );
                    uc.write((1001 + 2*data.nMine)+1, 0 ); //TODO: write the type
                    uc.write(1000, data.nMine+1);

                }

            }


        }

    }

}

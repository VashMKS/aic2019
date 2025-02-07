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
        //uc.println("sense mines successful, mines found: " + minesAround.length);
        if(minesAround.length > 0) {
            for (ResourceInfo mineInfo : minesAround) {
                Location mineLoc = mineInfo.getLocation();
                //uc.println("*notes mine location*: " + mineLoc.x + " " + mineLoc.y);
                //uc.println("encrypted: " + tools.encrypt(mineLoc.x, mineLoc.y));
                if (!tools.reportedMine(mineLoc)) {
                    //uc.println("it hasn't. I'll take note of it");
                    uc.write(data.nMineCh + 1 + 2*data.nMine, tools.encrypt(mineLoc.x,mineLoc.y));
                    data.nMine = data.nMine + 1;
                    uc.write(data.nMineCh, data.nMine);
                    //uc.println("now we've seen " + uc.read(data.nMineCh) + " mines so far");
                }
            }
        }

        TownInfo[] townsAround = uc.senseTowns();
        //uc.println("sense towns completed, towns found: " + townsAround.length);
        if(townsAround.length > 0) {
            for (TownInfo townInfo : townsAround){
                Location townLoc = townInfo.getLocation();
                if (! tools.reportedTown( townLoc )){
                    uc.write(data.nTownCh + 1 + 2*data.nTown, tools.encrypt(townLoc.x,townLoc.y));
                    data.nTown = data.nTown + 1;
                    uc.write(data.nTownCh, data.nTown);
                }
            }
        }

    }

}

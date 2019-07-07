package alpha2;

import aic2019.Location;
import aic2019.UnitController;
import aic2019.UnitInfo;

public class Base extends RecruitmentUnit {

    public Base (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
    }

    void run() {

        uc.println("base is at: " + uc.getLocation().x + " " + uc.getLocation().y);

        while (true) {

            data.Update();

            // uc.println("current number of mines: " + data.nMine);

            report();

            if (data.currentRound % 100 == 5) {
                uc.println("Round " + data.currentRound + " report");
                for (int i = 0; i < data.nMine; i++) {
                    int mineLocChannel = data.nMineCh + 1 + 2 * i;
                    Location mineLoc = tools.decrypt(uc.read(mineLocChannel));
                    int minersChannel = mineLocChannel + 1;
                    int nMiners = uc.read(minersChannel);
                    uc.println("mine number " + i + " is at (" + mineLoc.x + ", " + mineLoc.y + ") and has " + nMiners + " miners assigned");
                }
            }

            spawnUnits();

            attack();

            uc.yield();
        }

    }

    void attack(){

        UnitInfo[] unitsAround = uc.senseUnits();
        Location target = uc.getLocation();
        int priority = 0;

        for (UnitInfo unit : unitsAround) {

            if (!unit.getTeam().equals(data.allyTeam)) {

                //TODO: falta mirar les caselles al voltant de les unitats enemigues
                int unitPriority = tools.areaAttackPriority( unit.getLocation() );
                //uc.println("My target is at " + unit.getLocation().x + " " + unit.getLocation().y + " with priority " + unitPriority );

                if (unitPriority > priority) {
                    priority = unitPriority;
                    target = unit.getLocation();
                }
            }
        }

        if (! target.isEqual(uc.getLocation()) && uc.canAttack(target) ){
            //uc.println("I'm about to attack " + target.x + " " + target.y );
            uc.attack(target);
        }

    }

}

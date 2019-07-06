package alpha2;

import aic2019.UnitController;

public class Base extends RecruitmentUnit {

    public Base (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
    }

    void run() {

        while (true) {

            data.Update();

            // uc.println("current number of mines: " + data.nMine);

            report();

            spawnUnits();

            attack();

            uc.yield();
        }

    }

    void attack() {

    }

}

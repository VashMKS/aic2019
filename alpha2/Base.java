package alpha2;

import aic2019.UnitController;

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

            for (int i = 0; i < data.nMine; i++) {
                int mineLocChannel = data.nMineCh + 1 + 2*i;
                int minepos = uc.read(mineLocChannel);
                uc.println("mine number " + i + " is at: " + minepos);
            }

            spawnUnits();

            attack();

            uc.yield();
        }

    }

    void attack() {

    }

}

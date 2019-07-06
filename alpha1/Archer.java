package alpha1;

import aic2019.*;

public class Archer extends CombatUnit {

    public Archer (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
        this.movement = new Movement(uc, data);
    }

    void run() {

        while (true) {
            uc.yield();
        }

    }

}

package alpha1;

import aic2019.*;

public class Explorer extends CombatUnit {

    public Explorer (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.movement = new Movement(uc, data);
    }

    void run() {

        while (true) {
            uc.yield();
        }
    }

}
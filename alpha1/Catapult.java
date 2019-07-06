package alpha1;

import aic2019.*;

public class Catapult extends CombatUnit {

    public Catapult (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
        this.movement = new Movement(uc, data);
    }

    void run() {
        uc.yield();
    }

}
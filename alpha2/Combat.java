package alpha2;

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

    void attack() {
        return;
    }

}

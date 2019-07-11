package alpha4;

import aic2019.UnitController;

public class MapKeeper {

    UnitController uc;
    Data data;
    Map map;

    public MapKeeper(UnitController _uc, Data _data, Map _map) {
        this.uc = _uc;
        this.data = _data;
        this.map = _map;
    }

    // called every turn, performs all actions a cartographer must take
    void keeperLoop() {

    }

}
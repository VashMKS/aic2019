package alpha1;

import aic2019.*;

public class Barracks extends Structure {

    public Barracks (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
    }

    void run() {
        uc.yield();
    }

}

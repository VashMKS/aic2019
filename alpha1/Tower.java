package alpha1;

import aic2019.*;

public class Tower extends Structure {

    public Tower (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
    }

    void run() {

        while (true) {
            uc.yield();
        }
    }

}
package alpha1;

import aic2019.*;

public class Tower extends Structure {

    public Tower (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
    }

    void run() {

        while (true) {

            data.Update();

            report();

            uc.yield();
        }
    }

}
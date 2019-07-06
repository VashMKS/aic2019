package alpha1;

import aic2019.*;

public class Barracks extends Structure {

    public Barracks (UnitController _uc) {
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

    @Override
    void reportMyself() {
        // Report to the Comm Channel
        uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
        uc.write(data.barracksReportCh, uc.read(data.barracksReportCh)+1);
        // Reset Next Slot
        uc.write(data.unitResetCh, 0);
        uc.write(data.barracksResetCh, 0);
    }

}

package berenjena0;

import aic2019.UnitController;

public class Barracks extends RecruitmentUnit {

    public Barracks (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
    }

    void run() {

        while (true) {

            data.update();

            report();

            spawnUnits();

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

package alpha1;

import aic2019.*;

public class Soldier extends CombatUnit {

    public Soldier (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.movement = new Movement(uc, data);
    }

    void run() {

        while (true) {

            report();

            uc.yield();
        }

    }

    @Override
    void reportMyself() {
        // Report to the Comm Channel
        uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
        uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh)+1);
        uc.write(data.soldierReportCh, uc.read(data.soldierReportCh)+1);
        // Reset Next Slot
        uc.write(data.unitResetCh, 0);
        uc.write(data.combatUnitResetCh, 0);
        uc.write(data.soldierResetCh, 0);
    }

}

package alpha4;

import aic2019.UnitController;

public class Catapult extends CombatUnit {

    public Catapult (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
        this.movement = new Movement(uc, data);
        this.combat = new Combat(uc, data, tools, movement);
    }

    void run() {

        while (true) {

            data.update();

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
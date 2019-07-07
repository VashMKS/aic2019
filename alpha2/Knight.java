package alpha2;

import aic2019.UnitController;

public class Knight extends CombatUnit {

    public Knight (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
        this.movement = new Movement(uc, data);
        this.combat = new Combat(uc, data, tools, movement);
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
        uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh)+1);
        uc.write(data.knightReportCh, uc.read(data.knightReportCh)+1);
        // Reset Next Slot
        uc.write(data.unitResetCh, 0);
        uc.write(data.combatUnitResetCh, 0);
        uc.write(data.knightResetCh, 0);
    }

}
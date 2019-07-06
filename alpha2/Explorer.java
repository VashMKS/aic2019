package alpha2;

import aic2019.UnitController;

import java.awt.*;

public class Explorer extends CombatUnit {

    public Explorer (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
        this.movement = new Movement(uc, data);
    }

    void run() {

        while (true) {

            data.Update();

            report();

            move();

            uc.yield();
        }
    }

    @Override
    void reportMyself() {
        // Report to the Comm Channel
        uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
        //uc.write(data.combatUnitReportCh, uc.read(data.combatUnitReportCh)+1);
        uc.write(data.explorerReportCh, uc.read(data.explorerReportCh)+1);
        // Reset Next Slot
        uc.write(data.unitResetCh, 0);
        //uc.write(data.combatUnitResetCh, 0);
        uc.write(data.explorerResetCh, 0);
    }

    @Override
    void move() {
        super.move();
    }
}
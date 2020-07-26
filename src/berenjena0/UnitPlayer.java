package berenjena0;

import aic2019.UnitController;
import aic2019.UnitType;

public class UnitPlayer {

    public void run(UnitController uc) {
	/*Insert here the code that should be executed only at the beginning of the unit's lifespan*/

        if (uc.getType() == UnitType.BASE) {
            Base base = new Base(uc);
            base.run();
        } else if (uc.getType() == UnitType.WORKER) {
            Worker worker = new Worker(uc);
            worker.run();
        } else if (uc.getType() == UnitType.EXPLORER) {
            Explorer explorer = new Explorer(uc);
            explorer.run();
        } else if (uc.getType() == UnitType.SOLDIER) {
            Soldier soldier = new Soldier(uc);
            soldier.run();
        } else if (uc.getType() == UnitType.ARCHER) {
            Archer archer = new Archer(uc);
            archer.run();
        } else if (uc.getType() == UnitType.KNIGHT) {
            Knight knight = new Knight(uc);
            knight.run();
        } else if (uc.getType() == UnitType.MAGE) {
            Mage mage = new Mage(uc);
            mage.run();
        } else if (uc.getType() == UnitType.CATAPULT) {
            Catapult catapult = new Catapult(uc);
            catapult.run();
        } else if (uc.getType() == UnitType.BARRACKS) {
            Barracks barracks = new Barracks(uc);
            barracks.run();
        } else if (uc.getType() == UnitType.TOWER) {
            Tower tower = new Tower(uc);
            tower.run();
        }

    }
}

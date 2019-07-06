package alpha1;

import aic2019.*;

public class Base extends Structure {

    public Base (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
    }

    void run() {

        while (true) {

            data.Update();

            report();

            trySpawnWorker();

            uc.yield();
        }

    }

    void trySpawnWorker() {

        uc.println("hello world");
        for (Direction dir : data.dirs) {
            if (uc.canSpawn(dir, UnitType.WORKER)) {
                uc.spawn(dir, UnitType.WORKER);
            }
        }
    }

}

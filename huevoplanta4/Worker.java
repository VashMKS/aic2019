package huevoplanta4;

import aic2019.*;

public class Worker extends MovingUnit {

    public Worker (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
        this.movement = new Movement(uc, data);
    }

    void run() {

        while (true){

            data.update();

            report();

            //logs();

            gather();

            deliver();

            repair();

            buildTower();

            move();

            deliver();

            repair();

            uc.yield();
        }

    }

    @Override
    void reportMyself() {
        // Report to the Comm Channel
        uc.write(data.unitReportCh, uc.read(data.unitReportCh)+1);
        uc.write(data.workerReportCh, uc.read(data.workerReportCh)+1);
        // Reset Next Slot
        uc.write(data.unitResetCh, 0);
        uc.write(data.workerResetCh, 0);
        if (data.isMiner) {
            // Report to the Comm Channel
            uc.write(data.minerReportCh, uc.read(data.minerReportCh)+1);
            uc.write(data.myMineMinerReportCh, uc.read(data.myMineMinerReportCh)+1);
            // Reset Next Slot
            uc.write(data.minerResetCh, 0);
            uc.write(data.myMineMinerResetCh, 0);
        }
    }

    void logs() {
        if (data.currentRound % 100 == 1) {
            uc.println("worker ID" + data.ID + "assigned to mine " + data.myMineIndex +
                    " at (" + data.myMine.x + ", " + data.myMine.y);
            uc.println("myMineMinerReportCh = " + data.myMineMinerReportCh + " reads " + uc.read(data.myMineMinerReportCh));
            uc.println("myMineMinerResetCh = " + data.myMineMinerResetCh + " reads " + uc.read(data.myMineMinerResetCh));
            uc.println("myMineMinerCh = " + data.myMineMinerCh + " reads " + uc.read(data.myMineMinerCh));
        }
    }

    // if adjacent to the base or an owned town, deliver carried resources
    void deliver(){
        if(tools.alliesAround(2, UnitType.BASE) > 0) {
            Direction d = uc.getLocation().directionTo(data.allyBase);
            if(uc.canDeposit(d)) {
                //uc.println("resources delivered successfully");
                uc.deposit(d);
                data.onDelivery = false;
                return;
            }
        }

        TownInfo[] nearbyOwnedTowns = uc.senseTowns(data.allyTeam, false, 2);

        if(nearbyOwnedTowns.length > 0) {
            Location myLoc = uc.getLocation();
            for (TownInfo town : nearbyOwnedTowns) {
                Direction dir = myLoc.directionTo(town.getLocation());
                if(uc.canDeposit(dir)) {
                    //uc.println("resources delivered successfully");
                    uc.deposit(dir);
                    data.onDelivery = false;
                    return;
                }
            }
        }
    }

    // if adjacent to an owned town, repair it
    void repair() {
        Location myLoc = uc.getLocation();
        TownInfo[] nearbyOwnedTowns = uc.senseTowns(data.allyTeam, false, 2);

        for (TownInfo town : nearbyOwnedTowns) {
            if (town.getLoyalty() < town.getMaxLoyalty()) {
                Location loc = town.getLocation();
                Direction dir = myLoc.directionTo(loc);
                if (uc.canRepair(dir)) uc.repair(dir);
            }
        }
    }

    // if on top of a mine, gather resources
    void gather(){
        if(uc.canGather()) uc.gather();
    }

   @Override
    void move() {

        Direction dir = tools.randomDir();
        Location target = uc.getLocation().add(dir);

        if (data.isMiner) {
            // calculate how much should miners gather resources before delivering
            int maxGather = 50;
            // TODO: make maxGather less arbitrary (maybe depend on distance to the delivery point)
            /*if (data.hasTown) {
                int myMineDistSqToMyTown = data.myMine.distanceSquared(data.myTown);
                // update maxGather based on distance to nearest town
            } else {
                // update maxGather based on distance to base
            }*/

            if (data.onDelivery || tools.valueOf(uc.getInfo().getWood(), uc.getInfo().getIron(), uc.getInfo().getCrystal()) > maxGather) {

                if (data.hasTown) target = data.myTown;
                else target = data.allyBase;

                data.onDelivery = true;
                //uc.println("Worker ID" + data.ID + " on delivery towards (" + target.x + ", " + target.y + ")");

            } else {
                target = data.myMine;
                //uc.println("Worker ID" + data.ID + " headed to mine at (" + data.myMine.x + ", " + data.myMine.y + ")");
            }
        }

        //uc.println("target: (" + target.x + ", " + target.y + ")");
       if(!movement.doMicro(uc.getLocation().directionTo(target))){
           //uc.drawLine(uc.getLocation(), target, "#0000ff" );
           movement.moveTo(target);
       }

    }

    // if we have many resources, fortify your mine
    void buildTower(){

        Location myLoc = uc.getLocation();

        if(data.requestedWood > 0 && data.requestedIron > 0) return;
        if(tools.valueOf(uc.getWood(), uc.getIron(), uc.getCrystal()) < 2000) return;

        if (myLoc.isEqual(data.myMine) ) {

            // TODO: mark mine as "fortified" when all 3 towers have been built (or tried)
            Direction target = myLoc.directionTo(data.allyBase).opposite();
            boolean done = false;

            for (int i = 0; i < 3; ++i) {

                if (i == 1) target = target.rotateLeft();
                if (i == 2) target = target.rotateRight();

                if (!done && uc.canSpawn(target, UnitType.TOWER)) {
                    uc.spawn(target, UnitType.TOWER);
                    done = true;
                }

            }
        }
    }

    void panic() {
        if(uc.senseUnits(data.allyTeam, true).length > 0 ){
            for(Direction d : data.dirs){
                if (!d.isEqual(uc.getLocation().directionTo(data.allyBase)) && uc.canSpawn(d, UnitType.TOWER)) {
                    uc.spawn(d, UnitType.TOWER);
                }

            }

        }
    }

}

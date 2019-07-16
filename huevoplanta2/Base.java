package huevoplanta2;

import aic2019.*;

public class Base extends RecruitmentUnit implements StructureCombat {

    public Base (UnitController _uc) {
        this.uc = _uc;
        this.data = new Data(uc);
        this.tools = new Tools(uc, data);
    }

    void run() {

        uc.println("Base is at (" + uc.getLocation().x + ", " + uc.getLocation().y + ")");

        while (true) {

            data.update();

            report();

            // logs every 100 rounds
            if (data.currentRound % 100 == 15) {
                uc.println("Round " + data.currentRound + " report:");
                uc.println("  - currently there are " + data.nWanderer + " workers wandering around. " +
                           "Threshold is at " + data.workerHealthThreshold + " HP");
                uc.println("  - currently " + data.nMiner + " miners out of " + data.nWorker + " workers are active in " + data.nMine + " mines. " +
                           "Cap is at " + data.nMinerThreshold + " miners");
                for (int i = 0; i < data.nMine; i++) {
                    Location mineLoc = data.mineLocations[i];
                    int nMiners = data.miners[i];
                    uc.println("  - mine " + i + " is at (" + mineLoc.x + ", " + mineLoc.y + ") with " + nMiners + " miners");
                }
                uc.println("  - currently " + data.nTownsfolk + " townsfolk out of " + data.nWorker + " workers are active in " + data.nTown + " towns. " +
                           "Cap is at " + data.nTownsfolkThreshold + " townsfolk");
                for (int i = 0; i < data.nTown; i++) {
                    Location mineLoc = data.townLocations[i];
                    int nMiners = data.townsfolk[i];
                    uc.println("  - town " + i + " is at (" + mineLoc.x + ", " + mineLoc.y + ") with " + nMiners + " isTownsfolk");
                }
            }

            spawnUnits();

            attack();

            economy();

            uc.yield();
        }

    }

    public void trade(){

        float rWood    = uc.read(data.requestWoodCh);       float woodStock    = uc.getWood()    - rWood;
        float rIron    = uc.read(data.requestIronCh);       float ironStock    = uc.getIron()    - rIron;
        float rCrystal = uc.read(data.requestCrystalCh);    float crystalStock = uc.getCrystal() - rCrystal;


        if(data.tradingWood > 0){
            if(data.tradingWood%2 ==0 && woodStock > 0){
                if(ironStock    < 0 && ironStock <= crystalStock) uc.trade(Resource.WOOD, Resource.IRON   , 2*woodStock/data.tradingWood );
                if(crystalStock < 0 && crystalStock <  ironStock) uc.trade(Resource.WOOD, Resource.CRYSTAL, 2*woodStock/data.tradingWood );
            }
            --data.tradingWood;
        }

        if(data.tradingIron > 0){
            if(data.tradingIron%2 == 0 && rIron > 0){
                if(woodStock    < 0 && woodStock <= crystalStock) uc.trade(Resource.IRON, Resource.WOOD   , 2*ironStock/data.tradingIron );
                if(crystalStock < 0 && crystalStock <  woodStock) uc.trade(Resource.IRON, Resource.CRYSTAL, 2*ironStock/data.tradingIron );
            }
            --data.tradingIron;
        }

        if(data.tradingCrystal > 0){
            //uc.println("Trading crystals for " + data.tradingCrystal + " turns more! Total Crystals for trade: " + crystalStock);
            if(data.tradingCrystal%2 == 0 && crystalStock > 0){

                //uc.println("This turn trading " + 2*crystalStock/data.tradingCrystal);

                if(woodStock < 0 && woodStock <= ironStock) uc.trade(Resource.CRYSTAL, Resource.WOOD, 2*crystalStock/data.tradingCrystal );
                if(ironStock < 0 && ironStock <  woodStock) uc.trade(Resource.CRYSTAL, Resource.IRON, 2*crystalStock/data.tradingCrystal );
            }
            --data.tradingCrystal;
        }

    }

    public void economy() {

        float rWood    = uc.read(data.requestWoodCh);
        float rIron    = uc.read(data.requestIronCh);
        float rCrystal = uc.read(data.requestCrystalCh);

        trade();

        //uc.println("Requested: WOOD " + rWood + ", IRON " + rIron + ", CRYSTAL " + rCrystal);
        //uc.println("Stock: WOOD " + uc.getWood() + ", IRON " + uc.getIron() + ", CRYSTAL " + uc.getCrystal());

        if(rWood == 0 && rIron == 0 && rCrystal == 0) return;

        float woodStock    = uc.getWood()    - rWood;
        float ironStock    = uc.getIron()    - rIron;
        float crystalStock = uc.getCrystal() - rCrystal;

        if(woodStock > 0 && ironStock > 0 && crystalStock > 0) return;

        if(woodStock                           > data.economyThreshold && data.tradingWood    == 0){
            data.tradingWood    = 20;
            uc.println("Trading Wood now!");
        }
        if(ironStock   *data.ironMultiplier    > data.economyThreshold && data.tradingIron    == 0){
            data.tradingIron    = 20;
            uc.println("Trading Iron now!");
        }
        if(crystalStock*data.crystalMultiplier > data.economyThreshold && data.tradingCrystal == 0) {
            data.tradingCrystal = 20;
            uc.println("Trading crystals now!");
        }

    }

    public void attack() {

        UnitInfo[] unitsAround = uc.senseUnits(data.allyTeam, true);
        Location target = uc.getLocation();
        float priority = 0;

        for (UnitInfo unit : unitsAround) {

            if(!uc.canAttack(unit.getLocation())) continue;

            //TODO: falta mirar les caselles al voltant de les unitats enemigues
            float unitPriority = areaAttackPriority( unit.getLocation() );
            //uc.println("My target is at " + unit.getLocation().x + " " + unit.getLocation().y + " with priority " + unitPriority );

            if (unitPriority > priority) {
                priority = unitPriority;
                target = unit.getLocation();
            }

        }
        if(!target.isEqual(uc.getLocation()) )uc.attack(target);
    }

    public int targetPriority(UnitInfo unit) {
        if(unit.getType() == UnitType.BASE)     return 10;
        if(unit.getType() == UnitType.BARRACKS) return 9;
        if(unit.getType() == UnitType.TOWER)    return 8;
        if(unit.getType() == UnitType.CATAPULT) return 7;
        if(unit.getType() == UnitType.MAGE)     return 6;
        if(unit.getType() == UnitType.SOLDIER)  return 5;
        if(unit.getType() == UnitType.ARCHER)   return 4;
        if(unit.getType() == UnitType.KNIGHT)   return 3;
        if(unit.getType() == UnitType.EXPLORER) return 2;
        if(unit.getType() == UnitType.WORKER)   return 1;
        return 0;
    }

    float areaAttackPriority(Location loc){

        if(loc.distanceSquared(uc.getLocation() ) <= 2) return -1000;

        UnitInfo[] unitsNearLoc = uc.senseUnits(loc, 2);
        float priority = 0;
        for (UnitInfo unit : unitsNearLoc) {
            if (unit.getTeam().equals(data.allyTeam)) priority -= 100;
            else {
                float p = targetPriority(unit);
                p = p * (float)(unit.getType().maxHealth / unit.getHealth());
                priority += p;
            }
        }
        return priority;
    }

}

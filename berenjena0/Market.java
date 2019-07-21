package berenjena0;

import aic2019.Resource;
import aic2019.UnitController;

public class Market {

    UnitController uc;
    Data data;
    Tools tools;

    public Market(UnitController _uc, Data _data) {
        this.uc    = _uc;
        this.data  = _data;
        this.tools = new Tools(uc, data);
    }

    /*
    public void economy() {

        // market logs
        if (data.currentRound%100 == 55 && data.tradingWood + data.tradingIron + data.tradingCrystal > 0) {
            uc.println("Market Report:" +
                    "\n  - Resource surpluses: " + data.woodSurplus + "W, " + data.ironSurplus + "I, " + data.crystalSurplus + "C" +
                    "\n  - Trading turns left: " + data.tradingWood + " (W), " + data.tradingIron + " (I), " + data.tradingCrystal + " (C)");
        }

        trade();

        uc.println("Requested: WOOD " + data.requestedWood + ", IRON " + data.requestedIron + ", CRYSTAL " + data.requestedCrystal);
        uc.println("Stock: WOOD " + uc.getWood() + ", IRON " + uc.getIron() + ", CRYSTAL " + uc.getCrystal());

        if(data.requestedWood == 0 && data.requestedIron == 0 && data.requestedCrystal == 0) return;

        if(data.woodSurplus > 0 && data.ironSurplus > 0 && data.crystalSurplus > 0) return;

        if(data.woodSurplus > data.economyThreshold && data.tradingWood == 0){
            data.tradingWood = 20;
            //uc.println("Trading Wood now!");
        }
        if(data.ironSurplus*data.ironMultiplier > data.economyThreshold && data.tradingIron == 0){
            data.tradingIron = 20;
            //uc.println("Trading Iron now!");
        }
        if(data.crystalSurplus*data.crystalMultiplier > data.economyThreshold && data.tradingCrystal == 0) {
            data.tradingCrystal = 20;
            //uc.println("Trading crystals now!");
        }

    }

    public void trade(){

        if(data.tradingWood > 0){
            if(data.tradingWood%2 ==0 && data.woodSurplus > 0){
                if(data.ironSurplus    < 0 && data.ironSurplus <= data.crystalSurplus) uc.trade(Resource.WOOD, Resource.IRON   , 2*data.woodSurplus/data.tradingWood );
                if(data.crystalSurplus < 0 && data.crystalSurplus <  data.ironSurplus) uc.trade(Resource.WOOD, Resource.CRYSTAL, 2*data.woodSurplus/data.tradingWood );
            }
            --data.tradingWood;
        }

        if(data.tradingIron > 0){
            if(data.tradingIron%2 == 0 && data.requestedIron > 0){
                if(data.woodSurplus    < 0 && data.woodSurplus <= data.crystalSurplus) uc.trade(Resource.IRON, Resource.WOOD   , 2*data.ironSurplus/data.tradingIron );
                if(data.crystalSurplus < 0 && data.crystalSurplus <  data.woodSurplus) uc.trade(Resource.IRON, Resource.CRYSTAL, 2*data.ironSurplus/data.tradingIron );
            }
            --data.tradingIron;
        }

        if(data.tradingCrystal > 0){
            uc.println("Trading crystals for " + data.tradingCrystal + " turns more! Total Crystals for trade: " + data.crystalSurplus);
            if(data.tradingCrystal%2 == 0 && data.crystalSurplus > 0){

                uc.println("This turn trading " + 2*data.crystalSurplus/data.tradingCrystal);

                if(data.woodSurplus < 0 && data.woodSurplus <= data.ironSurplus) uc.trade(Resource.CRYSTAL, Resource.WOOD, 2*data.crystalSurplus/data.tradingCrystal );
                if(data.ironSurplus < 0 && data.ironSurplus <  data.woodSurplus) uc.trade(Resource.CRYSTAL, Resource.IRON, 2*data.crystalSurplus/data.tradingCrystal );
            }
            --data.tradingCrystal;
        }

    }
    */

    public void trade(){

        float rWood    = uc.read(data.requestWoodCh);
        float rIron    = uc.read(data.requestIronCh);
        float rCrystal = uc.read(data.requestCrystalCh);
        float woodSurplus    = uc.getWood()    - rWood;        float woodSurplusValue    = woodSurplus;
        float ironSurplus    = uc.getIron()    - rIron;        float ironSurplusValue    = ironSurplus    *data.ironMultiplier;
        float crystalSurplus = uc.getCrystal() - rCrystal;     float crystalSurplusValue = crystalSurplus *data.crystalMultiplier;

        if(data.tradingCrystal > 0 && uc.canTrade()){
            //uc.println("Trading crystals for " + data.tradingCrystal + " turns more! Total Crystals for trade: " + crystalSurplus);
            if(data.tradingCrystal%2 == 0 && crystalSurplus > 0){

                //uc.println("This turn trading " + 2*crystalSurplus/data.tradingCrystal);

                if(woodSurplus < 0 && woodSurplusValue <= ironSurplusValue) uc.trade(Resource.CRYSTAL, Resource.WOOD, 2* crystalSurplus /data.tradingCrystal );
                if(ironSurplus < 0 && ironSurplusValue < woodSurplusValue) uc.trade(Resource.CRYSTAL, Resource.IRON, 2* crystalSurplus /data.tradingCrystal );
            }
            --data.tradingCrystal;
        }

        if(data.tradingIron > 0 && uc.canTrade()){
            if(data.tradingIron%2 == 0 && ironSurplus > 0){
                if(woodSurplus < 0 && woodSurplusValue <= crystalSurplusValue) uc.trade(Resource.IRON, Resource.WOOD   , 2* ironSurplus /data.tradingIron );
                if(crystalSurplus < 0 && crystalSurplusValue < woodSurplusValue) uc.trade(Resource.IRON, Resource.CRYSTAL, 2* ironSurplus /data.tradingIron );
            }
            --data.tradingIron;
        }

        if(data.tradingWood > 0 && uc.canTrade()){
            if(data.tradingWood%2 == 0 && woodSurplus > 0){
                if(ironSurplus < 0 && ironSurplusValue <= crystalSurplusValue) uc.trade(Resource.WOOD, Resource.IRON   , 2* woodSurplus /data.tradingWood );
                if(crystalSurplus < 0 && crystalSurplusValue < ironSurplusValue) uc.trade(Resource.WOOD, Resource.CRYSTAL, 2* woodSurplus /data.tradingWood );
            }
            --data.tradingWood;
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

        float woodStock    = uc.getWood()    - rWood;     float woodStockValue    = woodStock;
        float ironStock    = uc.getIron()    - rIron;     float ironStockValue    = ironStock*data.ironMultiplier;
        float crystalStock = uc.getCrystal() - rCrystal;  float crystalStockValue = crystalStock*data.crystalMultiplier;

        if(woodStock > 0 && ironStock > 0 && crystalStock > 0) return;

        if(woodStockValue    > data.economyThreshold && data.tradingWood    == 0){
            data.tradingWood    = 20;
            //uc.println("Trading Wood now!");
        }
        if(ironStockValue    > data.economyThreshold && data.tradingIron    == 0){
            data.tradingIron    = 20;
            //uc.println("Trading Iron now!");
        }
        if(crystalStockValue > data.economyThreshold && data.tradingCrystal == 0) {
            data.tradingCrystal = 20;
            //uc.println("Trading crystals now!");
        }

    }

}

package huevoplanta3;

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

    // exchange resources to fit our needs
    /*void trade(){

        if(data.tradingWood > 0) {
            if(data.tradingWood%2 ==0 && data.woodSurplus > 0) {
                float tradeAmount = data.woodSurplus/data.tradingWood;
                if(data.ironSurplus < 0 && data.crystalSurplus >= 0) {
                    float tradeOutput = uc.tradeOutput(Resource.WOOD, Resource.IRON, 2*tradeAmount);
                    uc.println("  - trade -" + 2*tradeAmount + "W --> +" + tradeOutput + "I");

                    uc.trade(Resource.WOOD, Resource.IRON, 2*tradeAmount);
                }
                if(data.ironSurplus >= 0 && data.crystalSurplus < 0) {
                    float tradeOutput = uc.tradeOutput(Resource.WOOD, Resource.CRYSTAL, 2*tradeAmount);
                    uc.println("  - trade -" + 2*tradeAmount + "w --> +" + tradeOutput + "C");

                    uc.trade(Resource.WOOD, Resource.CRYSTAL, 2*tradeAmount);
                }
                if(data.ironSurplus < 0  && data.crystalSurplus < 0) {
                    float tradeOutput1 = uc.tradeOutput(Resource.WOOD, Resource.IRON, tradeAmount);
                    float tradeOutput2 = uc.tradeOutput(Resource.WOOD, Resource.CRYSTAL, tradeAmount);
                    uc.println("  - trade -" + tradeAmount + "W --> +" + tradeOutput1 + "I");
                    uc.println("  - trade -" + tradeAmount + "W --> +" + tradeOutput2 + "C");

                    uc.trade(Resource.WOOD, Resource.IRON   , tradeAmount);
                    uc.trade(Resource.WOOD, Resource.CRYSTAL, tradeAmount);
                }
            }
            --data.tradingWood;
        }

        if(data.tradingIron > 0) {
            if(data.tradingIron%2 == 0 && data.requestedIron > 0) {
                float tradeAmount = data.ironSurplus/data.tradingIron;
                if(data.woodSurplus < 0 && data.crystalSurplus >= 0) {
                    float tradeOutput = uc.tradeOutput(Resource.IRON, Resource.WOOD, 2*tradeAmount);
                    uc.println("  - trade -" + 2*tradeAmount + "I --> +" + tradeOutput + "W");

                    uc.trade(Resource.IRON, Resource.WOOD   , 2*tradeAmount);
                }
                if(data.woodSurplus >= 0 && data.crystalSurplus < 0) {
                    float tradeOutput = uc.tradeOutput(Resource.IRON, Resource.CRYSTAL, 2*tradeAmount);
                    uc.println("  - trade -" + 2*tradeAmount + "I --> +" + tradeOutput + "C");

                    uc.trade(Resource.IRON, Resource.CRYSTAL, 2*tradeAmount);
                }
                if(data.woodSurplus < 0  && data.crystalSurplus < 0) {
                    float tradeOutput1 = uc.tradeOutput(Resource.IRON, Resource.WOOD, tradeAmount);
                    float tradeOutput2 = uc.tradeOutput(Resource.IRON, Resource.CRYSTAL, tradeAmount);
                    uc.println("  - trade -" + tradeAmount + "I --> +" + tradeOutput1 + "W");
                    uc.println("  - trade -" + tradeAmount + "I --> +" + tradeOutput2 + "C");

                    uc.trade(Resource.IRON, Resource.WOOD   , tradeAmount);
                    uc.trade(Resource.IRON, Resource.CRYSTAL, tradeAmount);
                }
            }
            --data.tradingIron;
        }

        if(data.tradingCrystal > 0) {
            if(data.tradingCrystal%2 == 0 && data.crystalSurplus > 0) {
                float tradeAmount = data.crystalSurplus/data.tradingCrystal;
                if(data.woodSurplus < 0 && data.ironSurplus >= 0) {
                    float tradeOutput = uc.tradeOutput(Resource.CRYSTAL, Resource.WOOD, 2*tradeAmount);
                    uc.println("  - trade -" + 2*tradeAmount + "C --> +" + tradeOutput + "W");

                    uc.trade(Resource.CRYSTAL, Resource.WOOD, 2*tradeAmount);
                }
                if(data.woodSurplus >= 0 && data.ironSurplus < 0) {
                    float tradeOutput = uc.tradeOutput(Resource.CRYSTAL, Resource.WOOD, 2*tradeAmount);
                    uc.println("  - trade -" + 2*tradeAmount + "C --> +" + tradeOutput + "I");

                    uc.trade(Resource.CRYSTAL, Resource.IRON, 2*tradeAmount);
                }
                if(data.woodSurplus < 0  && data.ironSurplus < 0) {
                    float tradeOutput1 = uc.tradeOutput(Resource.CRYSTAL, Resource.WOOD, tradeAmount);
                    float tradeOutput2 = uc.tradeOutput(Resource.CRYSTAL, Resource.IRON, tradeAmount);
                    uc.println("  - trade -" + tradeAmount + "C --> +" + tradeOutput1 + "W");
                    uc.println("  - trade -" + tradeAmount + "C --> +" + tradeOutput2 + "I");

                    uc.trade(Resource.CRYSTAL, Resource.WOOD, tradeAmount);
                    uc.trade(Resource.CRYSTAL, Resource.IRON, tradeAmount);
                }
            }
            --data.tradingCrystal;
        }

    }

    void economy() {

        trade();

        //uc.println("Requested: WOOD " + data.requestedWood + ", IRON " + data.requestedIron + ", CRYSTAL " + data.requestedCrystal);

        if(data.requestedWood == 0 && data.requestedIron == 0 && data.requestedCrystal == 0) return;

        if(data.woodSurplus                           > data.economyThreshold && data.tradingWood    == 0){
            data.tradingWood    = 20;
            //uc.println("Trading Wood now!");
        }
        if(data.ironSurplus   *data.ironMultiplier    > data.economyThreshold && data.tradingIron    == 0){
            data.tradingIron    = 20;
            //uc.println("Trading Iron now!");
        }
        if(data.crystalSurplus*data.crystalMultiplier > data.economyThreshold && data.tradingCrystal == 0) {
            data.tradingCrystal = 20;
            //uc.println("Trading crystals now!");
        }

    }*/

}

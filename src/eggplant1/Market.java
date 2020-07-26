package eggplant1;

import aic2019.Resource;
import aic2019.UnitController;

public class Market {

    UnitController uc;
    Data data;
    Tools tools;

    public Market (UnitController _uc, Data _data) {
        this.uc    = _uc;
        this.data  = _data;
        this.tools = new Tools(uc, data);
    }

    // exchange resources to fit our needs
    void trade(){

        if(data.tradingWood > 0){
            if(data.tradingWood%2 ==0 && data.woodSurplus > 0){
                if(data.ironSurplus < 0 && data.crystalSurplus >= 0) uc.trade(Resource.WOOD, Resource.IRON   , 2*data.woodSurplus/data.tradingWood );
                if(data.ironSurplus >= 0 && data.crystalSurplus < 0) uc.trade(Resource.WOOD, Resource.CRYSTAL, 2*data.woodSurplus/data.tradingWood );
                if(data.ironSurplus < 0  && data.crystalSurplus < 0){
                    uc.trade(Resource.WOOD, Resource.IRON   , data.woodSurplus/data.tradingWood );
                    uc.trade(Resource.WOOD, Resource.CRYSTAL, data.woodSurplus/data.tradingWood );
                }
            }
            --data.tradingWood;
        }

        if(data.tradingIron > 0){
            if(data.tradingIron%2 == 0 && data.requestedIron > 0){
                if(data.woodSurplus < 0 && data.crystalSurplus >= 0) uc.trade(Resource.IRON, Resource.WOOD   , 2*data.ironSurplus/data.tradingIron );
                if(data.woodSurplus >= 0 && data.crystalSurplus < 0) uc.trade(Resource.IRON, Resource.CRYSTAL, 2*data.ironSurplus/data.tradingIron );
                if(data.woodSurplus < 0  && data.crystalSurplus < 0){
                    uc.trade(Resource.IRON, Resource.WOOD   , data.ironSurplus/data.tradingIron );
                    uc.trade(Resource.IRON, Resource.CRYSTAL, data.ironSurplus/data.tradingIron );
                }
            }
            --data.tradingIron;
        }

        if(data.tradingCrystal > 0){
            uc.println("Trading crystals for " + data.tradingCrystal + " turns more! Total Crystals for trade: " + data.crystalSurplus);
            uc.println("This turn trading " + 2*data.crystalSurplus/data.tradingCrystal);
            if(data.tradingCrystal%2 == 0 && data.crystalSurplus > 0){
                if(data.woodSurplus < 0 && data.ironSurplus >= 0) uc.trade(Resource.CRYSTAL, Resource.WOOD, 2*data.crystalSurplus/data.tradingCrystal );
                if(data.woodSurplus >= 0 && data.ironSurplus < 0) uc.trade(Resource.CRYSTAL, Resource.IRON, 2*data.crystalSurplus/data.tradingCrystal );
                if(data.woodSurplus < 0  && data.ironSurplus < 0){
                    uc.trade(Resource.CRYSTAL, Resource.WOOD, data.crystalSurplus/data.tradingCrystal );
                    uc.trade(Resource.CRYSTAL, Resource.IRON, data.crystalSurplus/data.tradingCrystal );
                }
            }
            --data.tradingCrystal;
        }

    }

    void economy() {

        trade();

        uc.println("Requested: WOOD " + data.requestedWood + ", IRON " + data.requestedIron + ", CRYSTAL " + data.requestedCrystal);

        if(data.requestedWood == 0 && data.requestedIron == 0 && data.requestedCrystal == 0) return;

        if(data.woodSurplus                           > data.economyThreshold && data.tradingWood    == 0){
            data.tradingWood    = 20;
            uc.println("Trading Wood now!");
        }
        if(data.ironSurplus   *data.ironMultiplier    > data.economyThreshold && data.tradingIron    == 0){
            data.tradingIron    = 20;
            uc.println("Trading Iron now!");
        }
        if(data.crystalSurplus*data.crystalMultiplier > data.economyThreshold && data.tradingCrystal == 0) {
            data.tradingCrystal = 20;
            uc.println("Trading crystals now!");
        }

    }

}

package adAuctions.test;

import adAuctions.algorithms.*;
import adAuctions.structures.*;

public class Main {

    public static void main(String[] args) {
	    Market newGame = new Market(7,7,1,10,1,1,7,7,1,1,.5);
        returnSet returnSet = Waterfall.WaterfallAlgorithm(newGame);
        Exhaustive exhaustive = new Exhaustive();
        //ArrayList<Allocation[][]> exhaustiveSet = exhaustive.search(newGame);
        System.out.println("Waterfall");
        printAllocation2D(returnSet.allocations);
        System.out.println("----------------------");
        /*
        for (int i = 0; i < exhaustiveSet.size(); i++) {
            System.out.printf("Exhaustive Allocation %d\n", i);
            printAllocation2D(exhaustiveSet.get(i));
        }
        */
        System.out.println("Waterfall Value");
        double WaterfallValue = getAllocationValue(newGame,returnSet.allocations);
        System.out.println(WaterfallValue);
        System.out.println("Exhaustive Value");
        System.out.println(exhaustive.maxValue);
        System.out.println("Waterfall Efficiency Ratio");
        System.out.println(WaterfallValue/exhaustive.maxValue);
        System.out.println("hello");
    }

    public static void printAllocation2D(Allocation[][] allocations){
        int x = allocations.length;
        int y = allocations[0].length;

        for (int i = 0; i < x; i++) {
            System.out.printf("[");
            for (int j = 0; j < y; j++) {
                allocations[i][j].print();
                System.out.printf(",");
            }
            System.out.printf("\n");
        }
        System.out.printf("\n");
    }

    public static double getAllocationValue(Market game,Allocation[][] allocations){
        double value = 0;
        for (int i = 0; i < allocations.length; i++) {
            int userCount = 0;
            for (int j = 0; j < allocations[0].length; j++) {
                userCount+=allocations[i][j].num_customers;
            }
            if(userCount==game.campaigns[i].numImpressions){
                value+=game.campaigns[i].totalValue;
            }
        }
        return value;
    }
}

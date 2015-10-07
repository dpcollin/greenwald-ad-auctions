package com.greenwald;

public class Main {

    public static void main(String[] args) {
	    Game newGame = new Game(8,8,1,10,1,1,6,6,1,1,.5);
        returnSet returnSet = Waterfall.Waterfall(newGame);
        Allocation[][] exhaustive = Exhaustive.Exhaustive(newGame);
        printAllocation2D(returnSet.allocations);
        printAllocation2D(exhaustive);
        System.out.println(getAllocationValue(newGame,returnSet.allocations));
        System.out.println(getAllocationValue(newGame,exhaustive));
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

    public static double getAllocationValue(Game game,Allocation[][] allocations){
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

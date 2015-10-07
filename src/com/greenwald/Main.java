package com.greenwald;

public class Main {

    public static void main(String[] args) {
	    Game newGame = new Game(7,7,1,10,1,1,7,7,1,1,.8);
        returnSet returnSet = Waterfall.Waterfall(newGame);
        Allocation[][] exhaustive = Exhaustive.Exhaustive(newGame);
        printAllocation2D(returnSet.allocations);
        printAllocation2D(exhaustive);
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

}

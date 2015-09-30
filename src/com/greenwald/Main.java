package com.greenwald;

public class Main {

    public static void main(String[] args) {
	    Game newGame = new Game(2,4,1,10,1,3,2,4,1,3);
        returnSet returnSet = Waterfall.Waterfall(newGame);
        System.out.println("hello");
    }

}

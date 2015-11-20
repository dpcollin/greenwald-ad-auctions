package adAuctions.algorithms.linearp.test;

import adAuctions.algorithms.linearp.efficient_linear;
import adAuctions.structures.Market;


public class efficient_test{
	public static void main(String[] args){
		Market newGame = new Market(800,800,1,10,90,100,800,800,90,100,0.15);
		newGame.setConnectionsMatrix();
		efficient_linear eff = new efficient_linear(newGame);
		eff.Solve();
	}
}





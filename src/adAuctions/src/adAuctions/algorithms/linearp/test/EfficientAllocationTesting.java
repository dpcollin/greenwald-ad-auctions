package adAuctions.algorithms.linearp.test;

import adAuctions.algorithms.linearp.EfficientAllocationLinearP;
import adAuctions.structures.Market;


public class EfficientAllocationTesting{
	public static void main(String[] args){
		Market M = new Market(3,3,1,100,1,1,4,4,1,1,0.5);
		M.setConnectionsMatrix();
		EfficientAllocationLinearP eff = new EfficientAllocationLinearP(M);
		eff.Solve();
		System.out.println("\n" + M);
	}
}





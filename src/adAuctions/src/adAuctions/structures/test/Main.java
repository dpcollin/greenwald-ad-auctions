package adAuctions.structures.test;

import java.util.ArrayList;
import java.util.Random;

import adAuctions.algorithms.linearp.EfficientAllocationLinearP;
import adAuctions.algorithms.linearp.EnvyFreePrices;
//import adAuctions.algorithms.linearp.singleusermarket.EnvyFreePricesLinearPAlpha;
import adAuctions.structures.*;

public class Main {
	public static void main(String args[]){ 
		/*
		 * The following test is for a market with some number of users,
		 * some number of campaigns where all of them are connected.
		 */
		
		
		/*
		 * Interesting test: numCam = 2, numUse = 4, user provides 1 campaign needs 3.
		 * Prob of connection 0.65
		 */
		int numCam = 4;
		int numUse = 3;
		UserSet[] U = new UserSet[numUse];
		Random R = new Random();
		ArrayList<Connection> c = new ArrayList<Connection>();
		for(int i=0;i<numUse;i++){
			U[i] = new UserSet(3);
			for(int j=0;j<numCam;j++){
				if(R.nextDouble()<0.5){
					c.add(new Connection(j,i));
				}
			}
		}
		Campaign[] C = new Campaign[numCam];
		for(int j=0;j<numCam;j++){
			C[j] = new Campaign(4,(j+1)^2);
		}
		Connection[] Connections = new Connection[c.size()];
		Connections = c.toArray(Connections);
		Market M = new Market(C, U, Connections);
		M.setConnectionsMatrix();
		EfficientAllocationLinearP eff = new EfficientAllocationLinearP(M);
		ArrayList<int[][]> efficientAllocations = eff.Solve();
		for(int i=0;i<efficientAllocations.size();i++){
			M.setAllocationMatrix(efficientAllocations.get(i));
			System.out.println("**********************Efficient allocation #"+i+"**********************\n"+M);
			/*
			 * Test that the envy-free prices work for all efficient allocations
			 */
			//double[] envyFreePrices = new EnvyFreePricesLinearPAlpha(M).Solve();
			double[] envyFreePrices = new EnvyFreePrices(M).Solve();
			if(envyFreePrices == null){
				System.out.println("Could not find envy free prices");
			}else{
				System.out.println("Envy-free prices");
				Market.printPrices(envyFreePrices);
			
				if(M.areAllCampaignsEnvyFree(envyFreePrices)==-1){
					System.out.println("Everyone is happy");
				}else{
					System.out.println("Someone is not happy");
					System.exit(0);
				}
			}
			
			/*double[] prices = new double[numUse];
			prices[0] = 2.0;
			prices[1] = 1.0;
			prices[2] = 1.0;
			System.out.println("Some test prices");
			Market.printPrices(prices);*/
		}
	}
}

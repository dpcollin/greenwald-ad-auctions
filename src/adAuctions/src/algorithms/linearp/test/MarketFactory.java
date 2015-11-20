package adAuctions.algorithms.linearp.test;

import java.util.ArrayList;
import java.util.List;

import adAuctions.algorithms.Exhaustive;
import adAuctions.algorithms.linearp.LinearP;
import adAuctions.structures.Allocation;
import adAuctions.structures.Market;


public class MarketFactory {
	
	public static boolean verbose = false;
	
	/*
	 * This function creates a MonoMarket (single User, single impression Campaign),
	 * loops through all possible efficient allocations and solve a linear programming
	 * to find envy-free prices for each efficient allocation. It returns true
	 * if all the efficient allocations had envy-free prices and false otherwise
	 */
	public static boolean CreateMonoMarket(double ratioOfConnections,int minNumCamp, int maxNumCamp,int minNumUser,int maxNumUser){
		
		Market market = new Market(	minNumCamp, /*minNumCampaigns*/
							maxNumCamp, /*maxNumCampaigns*/
							1,			/*minPricePerCampaign*/
							100,		/*maxPricePerCampaign*/
							1, 			/*minImpressionsPerCampaign*/ 
							1, 			/*maxImpressionsPerCampaign*/
							minNumUser, /*minNumUserSets*/
							maxNumUser, /* maxNumUserSets*/
							1, 			/*minUsersPerSet*/
							1,			/*maxUsersPerSet*/
							ratioOfConnections);			/*ratioOfConnections*/

    	if(MarketFactory.verbose){
    		System.out.println("Connections: " + market.connections.length);
    		for(int i=0;i<market.connections.length;i++){
    			if(market.connections[i] != null){
    				System.out.println("("+market.connections[i].campaignIndex+","+market.connections[i].userIndex+")");
    			}
    		}
    	}		
    	market.setConnectionsMatrix();
		
		int[][] allocmatrix = new int[market.userSets.length][market.campaigns.length];
		
		if(MarketFactory.verbose) System.out.println("\nEfficient Allocation");
		if(market.connections[0] != null){//There might not really be any connections, in which case it makes no sense to allocate anything
			Exhaustive E = new Exhaustive();
			ArrayList<Allocation[][]> efficAllocations =  E.search(market);
			if(MarketFactory.verbose) System.out.println("Size:"+efficAllocations.size());
			if(efficAllocations.size()>0){
				if(MarketFactory.verbose)	System.out.println("There is at least one efficient allocation");
				//for(int k=0;k<Math.min(5,efficAllocations.size());k++){
				for(int k=0;k<efficAllocations.size();k++){					
					//Get the k-th efficient allocation
					Allocation[][] efficAlloc = efficAllocations.get(k);
					for(int i=0;i<efficAlloc.length;i++){
						if(MarketFactory.verbose) System.out.println("k = "+ k);
						for(int j=0;j<efficAlloc[0].length;j++){
							if(MarketFactory.verbose) System.out.print("\t" + efficAlloc[i][j].num_customers);
							//create the allocation
							allocmatrix[j][i]= efficAlloc[i][j].num_customers;
						}
					}
					//Set the allocation for this market
					market.setAllocationMatrix(allocmatrix);
					if(MarketFactory.verbose) System.out.println("\n ----- Market generated automatically ---- \n" + market);
					double[] envyFreePrices = new LinearP(market).Solve();
					if(envyFreePrices == null){
						System.out.println("There are NO envy-free prices for this market:" + market);
						return false;
					}
				}
			}
		}else{
			if(MarketFactory.verbose) System.out.println("No Connections");
			return true;
		}
		return true;
	}
	
	
	/*
	 * This class creates a random market with the given parameters and find just the first 
	 * efficient allocation. It returns a list with the market in the 0th position and the
	 * envy-free prices in the second.
	 */
	public static List<Object> createMonoMarketWithOneEfficientAllocation(double ratioOfConnections,int minNumCamp, int maxNumCamp,int minNumUser,int maxNumUser){
			
			Market market = new Market(	minNumCamp, /*minNumCampaigns*/
								maxNumCamp, /*maxNumCampaigns*/
								1,			/*minPricePerCampaign*/
								100,		/*maxPricePerCampaign*/
								1, 			/*minImpressionsPerCampaign*/ 
								1, 			/*maxImpressionsPerCampaign*/
								minNumUser, /*minNumUserSets*/
								maxNumUser, /* maxNumUserSets*/
								1, 			/*minUsersPerSet*/
								1,			/*maxUsersPerSet*/
								ratioOfConnections);			/*ratioOfConnections*/

	    	if(MarketFactory.verbose){
	    		System.out.println("Connections: " + market.connections.length);
	    		for(int i=0;i<market.connections.length;i++){
	    			if(market.connections[i] != null){
	    				System.out.println("("+market.connections[i].campaignIndex+","+market.connections[i].userIndex+")");
	    			}
	    		}
	    	}		
	    	if(market.connections[0] != null){    	
	    		market.setConnectionsMatrix();
	    		Exhaustive E = new Exhaustive();
	    		ArrayList<Allocation[][]> efficAllocations =  E.search(market);
	    		if(efficAllocations.size()>0){
	    			Allocation[][] efficAlloc = efficAllocations.get(0);
	    			int[][] allocmatrix = new int[market.userSets.length][market.campaigns.length];
					for(int i=0;i<efficAlloc.length;i++){
						for(int j=0;j<efficAlloc[0].length;j++){
							//create the allocation
							allocmatrix[j][i]= efficAlloc[i][j].num_customers;
						}
					}
					market.setAllocationMatrix(allocmatrix);
					double[] envyFreePrices = new LinearP(market).Solve();
					List<Object> list = new ArrayList<Object>();
					list.add(0, market);
					list.add(1,envyFreePrices);
					return list;
	    		}
	    	}
	    	return null;
	}
	
	
	/*
	 * This function creates a bunch of mono markets and checks if all the efficient allocations
	 * for said mono market lead to envy-free prices.
	 */
	public static void createMultipleMonoMarkets(){
		int repeatExperiments = 10;
		int numExperiments = 10;
		int numCampaigns = 9;
		int numUsers = 9;
		int countEnvyFreeExists = 0;
		boolean envyFreeExists;
		/*
		 * Vary the number of Users
		 */
		for(int i=3;i<numUsers;i++){
			/*
			 * Vary the number of campaigns
			 */
			for(int j=3;j<numCampaigns;j++){
				/*
				 * How many times to repeat each experiment
				 */
				double connectivity = 0.1;
				for(int k=0;k<repeatExperiments;k++){
					/*
					 * For each connectivity level perform numExperiments number of experiments
					 */
					for(int l=0;l<numExperiments;l++){
						envyFreeExists = MarketFactory.CreateMonoMarket(connectivity, i,i,j,j);
						if(envyFreeExists){
								countEnvyFreeExists++;
						}else{
								System.out.println("Did not find envy-free prices!");
								System.exit(0);
						}
					}
					System.out.println(i + "\t" + j + "\t" + ((double)countEnvyFreeExists / (double)numExperiments) + "\t" + connectivity);
					connectivity += 0.1;
					countEnvyFreeExists = 0;
				}
			}
		}
	}	
}

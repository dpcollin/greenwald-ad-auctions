package adAuctions.test;

import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.List;

import adAuctions.algorithms.*;
import adAuctions.structures.*;
import adAuctions.algorithms.linearp.EfficientAllocationLinearP;
import adAuctions.algorithms.linearp.EnvyFreePrices;
import adAuctions.algorithms.linearp.EnvyFreePricesCompactCondition;
import adAuctions.algorithms.linearp.LPSolution;
import adAuctions.algorithms.linearp.singleusermarket.HungarianAlgorithm;
import adAuctions.algorithms.linearp.singleusermarket.LinearP;

public class Main {

	public static void main(String[] args) {
		
        for(int users=3;users<30;users++){
        	for(int camp=3;camp<30;camp++){
        		double con = 0.0;
        		for(int connectivity=0;connectivity<10;connectivity++){
        			con += 0.1;
        			System.out.println("users = "+ users + ",camp = " + camp + ", con = " + con);
					Market M = new Market(	3, /*minNumCampaigns*/
							camp, 	/*maxNumCampaigns*/
							1,		/*minPricePerCampaign*/
							100,	/*maxPricePerCampaign*/
							1, 		/*minImpressionsPerCampaign*/ 
							10, 		/*maxImpressionsPerCampaign*/
							2, 		/*minNumUserSets*/
							users, 		/* maxNumUserSets*/
							1, 		/*minUsersPerSet*/
							10,		/*maxUsersPerSet*/
							con);	/*ratioOfConnections*/    	
			    	if(M.connections[0] != null){
			    		M.setConnectionsMatrix();
			    		EfficientAllocationLinearP E = new EfficientAllocationLinearP(M);
			    		ArrayList<int[][]> efficAllocations =  E.Solve();
			    		if(efficAllocations.size()>0){
							M.setAllocationMatrix(efficAllocations.get(0));
							try{
								LPSolution Solution = new EnvyFreePricesCompactCondition(M).Solve();
								if(Solution.getStatus() == "Empty" || Solution.getStatus() == "Unknown" || Solution.getStatus() == "Infeasible"){
									System.out.println("Something went wrong. Solution Status = " + Solution.getStatus());
									System.out.println(M);
									System.exit(-1);
								}else if(Solution.getStatus() == "Unbounded"){
									System.out.println("Solution Unbounded");
									//System.out.println(M);
									
								}else{
									System.out.println("Solution Status = " + Solution.getStatus());
									double[] envyFreePrices = Solution.getPrices();
									if(envyFreePrices.length > 0){
										boolean pricenotzero = false;
										for(int i=0;i<envyFreePrices.length;i++){
											System.out.println("P("+i+") = " + envyFreePrices[i]);
											
											if(envyFreePrices[i]>0){
												pricenotzero = true;
											}
										}
										/*if(pricenotzero){
											System.out.println(M);
											System.exit(-1);											
										}*/
									}
									if(M.areAllCampaignsEnvyFree(envyFreePrices)>=0){
							    		System.out.println("******************Campaign "+M.areAllCampaignsEnvyFree(envyFreePrices)+" is envy**************");
							    		System.out.println(M);
							    		for(int i=0;i<envyFreePrices.length;i++){
							        		System.out.println("P("+i+") = " + envyFreePrices[i]);
							    		}
							    		System.out.println(adAuctions.graphs.Latex.createLatexGraph(M, envyFreePrices));
							    		System.exit(0);
							    	}									
								}
							}catch(Exception e){
								System.out.println("Exception - " + e);
							}
			    		}
			    	}
        		}
        	}
        }
	}
	public static void main2(String[] args) {
    	
    	
    	int actualMarketTested = 0, totalMarkets = 0;
        for(int users=3;users<30;users++){
        	for(int camp=3;camp<30;camp++){
        		double con = 0.0;
        		for(int connectivity=0;connectivity<10;connectivity++){
        			con += 0.1;
        			totalMarkets++;
        			System.out.println("users = "+ users + ",camp = " + camp + ", con = " + con);
    	
		Market M = new Market(	3, /*minNumCampaigns*/
				camp, 	/*maxNumCampaigns*/
				1,		/*minPricePerCampaign*/
				100,	/*maxPricePerCampaign*/
				1, 		/*minImpressionsPerCampaign*/ 
				5, 		/*maxImpressionsPerCampaign*/
				2, 		/*minNumUserSets*/
				users, 		/* maxNumUserSets*/
				1, 		/*minUsersPerSet*/
				5,		/*maxUsersPerSet*/
				con);	/*ratioOfConnections*/    	
    	    			
    	if(M.connections[0] != null){    	
    		M.setConnectionsMatrix();
    		EfficientAllocationLinearP E = new EfficientAllocationLinearP(M);
    		ArrayList<int[][]> efficAllocations =  E.Solve();
    		if(efficAllocations.size()>0){
				M.setAllocationMatrix(efficAllocations.get(0));
				try{
					double[] envyFreePrices = new EnvyFreePrices(M).Solve();
					actualMarketTested++;
			    	if(M.areAllCampaignsEnvyFree(envyFreePrices)>=0){
			    		System.out.println("******************Some campaign is envy**************");
			    		System.out.println(M);
			    		for(int i=0;i<envyFreePrices.length;i++){
			        		System.out.println("P("+i+") = " + envyFreePrices[i]);
			    		}
			    		System.exit(0);
			    	}
				}catch(Exception e){
					//System.out.println("IloCplex.UnknownObjectException");
				}
    		}
    	}		
    	
		//System.out.println("\n" + M);
        		}
        	}
        }

    	System.out.println("There were "+actualMarketTested+" actual Markets Tested out of" + totalMarkets + "total Markets");
    	
    	
	    /*Market newGame = new Market(7,7,1,10,1,1,7,7,1,1,.5);
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
        
        System.out.println("Waterfall Value");
        double WaterfallValue = getAllocationValue(newGame,returnSet.allocations);
        System.out.println(WaterfallValue);
        System.out.println("Exhaustive Value");
        System.out.println(exhaustive.maxValue);
        System.out.println("Waterfall Efficiency Ratio");
        System.out.println(WaterfallValue/exhaustive.maxValue);
        System.out.println("hello");*/
    	
    	
    	
    	/*
    for(int users=5;users<10;users++){
    	for(int camp=5;camp<10;camp++){
    		double con = 0.0;
    		for(int connectivity=0;connectivity<10;connectivity++){
    			con += 0.1;
    			System.out.println("users = "+ users + ",camp = " + camp + ", con = " + con);
    	Market M = new Market(users,users,1,100,1,1,camp,camp,1,1,con);
		M.setConnectionsMatrix();
		EfficientAllocationLinearP eff = new EfficientAllocationLinearP(M);
		ArrayList<int[][]> efficientAllocations = eff.Solve();
		//System.out.println("\n" + M);
		System.out.println("We found "+efficientAllocations.size()+" many efficient allocations");
		
		double reward = 0.0, previous_reward = 0.0;
		for(int l=0;l<efficientAllocations.size();l++){
			System.out.print("Solution #"+l + ": ");
			for (int i=0; i<M.getNumberUsers(); i++){
				for (int j=0; j<M.getNumberCampaigns(); j++){
					//System.out.print(efficientAllocations.get(l)[i][j]);
					//System.out.print(" ");
					reward += M.campaigns[j].getReward() * efficientAllocations.get(l)[i][j];
				}
				//System.out.print("\n");
			}
			System.out.println("Total Reward = " + reward);
			if(l > 0){
				if(Math.abs(reward - previous_reward)> 0.1){
					System.out.println("OOpsss -> reward = " + reward + ", previous_reward = "+previous_reward);
					System.exit(0);
				}
			}
			previous_reward = reward;
			reward = 0.0;
		}		    	
        
        double[][] costMatrix = new double[M.getNumberUsers()][M.getNumberCampaigns()];        
        for(int i=0;i<M.getNumberUsers();i++){
        	for(int j=0;j<M.getNumberCampaigns();j++){
        		if(M.isConnected(i, j)){
        			costMatrix[i][j] =-1.0 * M.getCampaign(j).getReward();
        		}else{
        			costMatrix[i][j] = Double.MAX_VALUE;
        		}
        	}
        }
        //Main.printMatrix(costMatrix);
         HungarianAlgorithm H = new HungarianAlgorithm(costMatrix);
         int[] result = H.execute();
         double totalRewardHungarian = 0.0;
         for(int i=0;i<result.length;i++){
        	 //System.out.println(result[i]);
        	 //If the assignment is possible and the user is actually connected to the campaigns
        	 if(result[i] > -1 && M.isConnected(i, result[i])){
        		 totalRewardHungarian += M.getCampaign(result[i]).getReward();
        	 }
         }
        System.out.println("Total Hungarian reward: " + totalRewardHungarian);
        if(Math.abs(previous_reward - totalRewardHungarian) > 0.1){
			System.out.println("OOpsss -> previous_reward = " + previous_reward + ", totalRewardHungarian = "+totalRewardHungarian);
			System.exit(0);
        	
        }
    		}
    	}
    }*/
        
    }

    public static void printMatrix(double[][] matrix){
    	for(int i=0;i<matrix.length;i++){
    		for(int j=0;j<matrix[0].length;j++){
    			System.out.print(matrix[i][j] + "\t");
    		}
    		System.out.print("\n");
    	}
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
            if(userCount==game.campaigns[i].getNumImpressions()){
                value+=game.campaigns[i].getReward();
            }
        }
        return value;
    }
}

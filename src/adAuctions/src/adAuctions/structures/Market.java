package adAuctions.structures;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

import adAuctions.structures.Campaign;
import adAuctions.structures.UserSet;
import adAuctions.structures.util.UserSetPriceComparator;

/**
 * Created by Daniel on 9/25/15.
 */

/**
 * Represents an entire market with sets of Campaigns and UserSets.
 *
 * Contains an initializer to generate a random market based upon some given restrictions
 */
public class Market {
	/*
	 * Array of Campaigns
	 */	
    public Campaign[] campaigns;
    /*
     * Array of Users
     */    
    public UserSet[] userSets;
    /*
     * Array of Connections
     */    
    public Connection[] connections;
    /*
     * Boolean matrix indicating which campaign is connected to which user.
     */
    protected boolean[][] connectionsMatrix;
    /*
     * An allocation matrix indicated how many users are allocated per campaign.
     * This is the product of some algorithm, i.e., waterfall
     */
    protected int[][] allocationMatrix;
    /*
     * Highest reward among all campaigns
     */
    protected double highestReward = -1.0;
    
    /*
     * Constructor. Receives arrays of campaigns, userSets and connections. 
     */
    public Market(Campaign[] campaigns, UserSet[] userSets, Connection[] connections){
        this.campaigns = campaigns;
        this.userSets = userSets;
        this.connections = connections;
    }
    /*
     * Constructor. Receives a market and copies it.
     */
    public Market(Market market){
        this.campaigns = Support.campaignArrayCopy(market.campaigns);
        this.userSets = Support.userSetArrayCopy(market.userSets);
        this.connections = Support.connectionArrayCopy(market.connections);
    }
    /*
     * Constructor for a new game given certain constraints
     */
    public Market(int minNumCampaigns, int maxNumCampaigns, double minPricePerCampaign, double maxPricePerCampaign,int minImpressionsPerCampaign, int maxImpressionsPerCampaign, int minNumUserSets, int maxNumUserSets, int minUsersPerSet, int maxUsersPerSet, double ratioOfConnections){
        //RNG setup
        int campaignSpread = maxNumCampaigns - minNumCampaigns;
        double campaignPriceSpread = maxPricePerCampaign - minPricePerCampaign;
        int impressionsSpread = maxImpressionsPerCampaign - minImpressionsPerCampaign;
        int userSetSpread = maxNumUserSets - minNumUserSets;
        int usersPerSetSpread = maxUsersPerSet - minUsersPerSet;
        Random rand = new Random();

        int i = 0;
        int numCampaigns = Support.adjustRand(rand.nextInt(),campaignSpread,minNumCampaigns);
        this.campaigns = new Campaign[numCampaigns];

        while(i < numCampaigns){
            double price = Support.adjustRand(rand.nextDouble(),campaignPriceSpread,minPricePerCampaign);
            int impressions = Support.adjustRand(rand.nextInt(),impressionsSpread,minImpressionsPerCampaign);
            this.campaigns[i] = new Campaign(impressions,price);
            i++;
        }

        i = 0;
        int numUserSets = Support.adjustRand(rand.nextInt(),userSetSpread,minNumUserSets);
        this.userSets = new UserSet[numUserSets];

        while(i<numUserSets){
            int users = Support.adjustRand(rand.nextInt(),usersPerSetSpread,minUsersPerSet);
            this.userSets[i] = new UserSet(users);
            i++;
        }

        ArrayList<Connection> connectionsList = new ArrayList<Connection>();

        i = 0;
        ArrayList<Integer> userListBasis = new ArrayList<Integer>(numUserSets);

        while(i<numUserSets) {
            userListBasis.add(i);
            i++;
        }

        i = 0;
        int minConnections = (int) Math.floor(numUserSets*ratioOfConnections);
        while(i<numCampaigns){
            java.util.Collections.shuffle(userListBasis);
            int numConnections = Support.adjustRand(rand.nextInt(),numUserSets-minConnections,minConnections);

            int j = 0;
            while(j<numConnections){
                connectionsList.add(new Connection(i,userListBasis.get(j)));
                this.campaigns[i].connections.add(userListBasis.get(j));
                this.userSets[j].connections.add(i);
                j++;
            }
            i++;
        }

        this.connections = connectionsList.toArray(new Connection[1]);
    }
	/*
     * Constructor for a new game given default constraints
     */
    public static Market autoGenerate(){
        int minNumCampaigns = 3;
        int maxNumCampaigns = 10;
        double minPricePerCampaign = 100.0;
        double maxPricePerCampaign = 1000.0;
        int minImpressionsPerCampaign = 100;
        int maxImpressionsPerCampaign = 1000;
        int minNumUserSets = 3;
        int maxNumUserSets = 10;
        int minUsersPerSet = 100;
        int maxUsersPerSet = 1000;
        double ratioOfConnections = .5;

        return new Market(minNumCampaigns,maxNumCampaigns,minPricePerCampaign,maxPricePerCampaign,minImpressionsPerCampaign,maxImpressionsPerCampaign,minNumUserSets,maxNumUserSets,minUsersPerSet,maxUsersPerSet,ratioOfConnections);
    }
    
    
    /*
     * Getters
     */
    public Campaign[] getCampaigns(){
    	return this.campaigns;
    }
    
    public Campaign getCampaign(int i){
    	return this.campaigns[i];
    }
    
    public UserSet[] getUsers(){
    	return this.userSets;
    }
    
    public UserSet getUser(int i){
    	return this.userSets[i];
    }
    
    public int getNumberUsers(){
    	return this.userSets.length;
    }
    
    public int getNumberCampaigns(){
    	return this.campaigns.length;
    }
    /*
     * Setters
     */
    public void setConnectionsMatrix(){
    	/*
    	 * Take an array of connections and turn it into a matrix of booleans
    	 */
		this.connectionsMatrix = new boolean[this.userSets.length][this.campaigns.length];
		for(int i=0;i<this.userSets.length;i++){
			for(int j=0;j<this.campaigns.length;j++){
				this.connectionsMatrix[i][j] = false; //Initialize all connections to false
			}
		}
		for(int k=0;k<this.connections.length;k++){
			//Set only those connections that are actually in the connection objects
			this.connectionsMatrix[this.connections[k].userIndex][this.connections[k].campaignIndex] = true;
    	}
    }
    public void setAllocationMatrix(int[][] allocationMatrix){
    	this.allocationMatrix = new int[allocationMatrix.length][allocationMatrix[0].length];
    	this.allocationMatrix = allocationMatrix;
    }
    /*
     * Get highest reward among all campaigns. Implements a singleton
     */
    public double getHighestReward(){
    	if(this.highestReward == -1.0){
    		double temp = -1.0;
    		for(int j=0;j<this.getNumberCampaigns();j++){
    			if(this.getCampaign(j).getReward() > temp){
    				temp = this.getCampaign(j).getReward();
    			}
    		}
    		this.highestReward = temp;
    	}
    	return this.highestReward;
    }
    /*
     * How many of user i is allocated to campaign j
     */
    public int allocationMatrixEntry(int i,int j){
    	return this.allocationMatrix[i][j];
    	
    }
    /*
     * Is user i connected to campaign j?
     */
    public boolean isConnected(int i, int j){
    	return this.connectionsMatrix[i][j];
    }
    
    /*
     * Checks if a campaign is assign something at all
     */
    public boolean isCampaignBundleZero(int j){
    	for(int i=0;i<this.getNumberUsers();i++){
    		if(this.allocationMatrix[i][j]>0){
    			return false;
    		}
    	}
    	return true;
    }
    
    /*
     * Checks if a user allocates something at all
     */
    public boolean isUserAllocationZero(int i){
    	for(int j=0;j<this.getNumberCampaigns();j++){
    		if(this.allocationMatrix[i][j]>0){
    			return false;
    		}
    	}
    	return true;
    }
    /*
     * Get current bundle cost for a campaign
     */
    public double getBundleCost(int j){
    	double cost = 0.0;
    	for(int i=0;i<this.getNumberUsers();i++){
    		if(this.allocationMatrix[i][j]>0){
    			cost += this.allocationMatrix[i][j]*this.userSets[i].getPrice();
    		}
    	}
    	return cost;
    }
    /*
     * Get current bundle number for a campaign
     */
    public double getBundleNumber(int j){
    	double cost = 0.0;
    	for(int i=0;i<this.getNumberUsers();i++){
    		if(this.allocationMatrix[i][j]>0){
    			cost += this.allocationMatrix[i][j];
    		}
    	}
    	return cost;
    }    
    /*
     * Heuristic to check envy-free-ness for a campaign. 
     */
    public boolean isCampaignEnvyFree(PriorityQueue<UserSet> queue, int campaignIndex){
    	System.out.println("Heuristic for campaign:" + campaignIndex + ", check this many users:" + queue.size());
    	double cost = 0.0;
    	int impressionsNeeded = this.campaigns[campaignIndex].numImpressions;
    	while (impressionsNeeded > 0 && queue.size() != 0){
			UserSet u = queue.remove();
			if(this.isConnected(u.getIndex(), campaignIndex)){
				System.out.println(u);
				if(u.getNumUsers()>= impressionsNeeded){
					cost += impressionsNeeded*u.getPrice();
					impressionsNeeded = 0;
				}else{
					cost += u.getPrice() * u.getNumUsers();
					impressionsNeeded -= u.getNumUsers();
				}
			}
		}
    	System.out.println("impressionsNeeded = "+impressionsNeeded);
    	System.out.println("cost of cheapest bundle = "+cost);
    	System.out.println("cost of current  bundle = "+getBundleCost(campaignIndex));
    	System.out.println("nbr  of current  bundle = "+getBundleNumber(campaignIndex));
    	System.out.println("reward of this campaign = "+this.campaigns[campaignIndex].getReward());
    	//if(impressionsNeeded > 0 || getBundleCost(campaignIndex) <= cost){
    	if(impressionsNeeded > 0){ //If you cannot be satisfied, you are inmediately envy-free
    		System.out.println("true");
    		return true;
    	}else{
    		if(getBundleNumber(campaignIndex) >= this.campaigns[campaignIndex].getNumImpressions()){//This campaign was satisfied
    			if(cost < getBundleCost(campaignIndex)){
    				return false;
    			}
    			return true;
    		}else{//the campaign was not satisfied
    			if(cost < this.campaigns[campaignIndex].getReward()){
    				return false;
    			}
    			return true;
    		}
    	}
    }
    /*
     * Check if this whole market is envy-free
     */
    public boolean areAllCampaignsEnvyFree(double[] prices){
		System.out.println("Check Heuristic For Envy-free-ness");
    	/*
    	 * Construct a priority queue with users where the priority is price in ascending order.
    	 */
    	int numUsers = this.getNumberUsers();
    	PriorityQueue<UserSet> queue = new PriorityQueue<UserSet>(numUsers, new UserSetPriceComparator());
		for(int i=0;i<numUsers;i++){
			 this.userSets[i].setPrice(prices[i]);
			 this.userSets[i].setIndex(i);
			 queue.add(this.userSets[i]);
		}
    	/*
    	 * Check that each campaign is envy-free for the previously constructed queue.
    	 */
    	for(int j=0;j<this.campaigns.length;j++){
    		if(!this.isCampaignEnvyFree(new PriorityQueue<UserSet>(queue), j)){//Pass a copy of the queue each time...
    			return false;
    		}
    	}
    	return true;
    }
    /*
     * Just a helper function to print the allocation
     */
    
   protected String printConnectionsMatrix(){
	   if(this.connectionsMatrix != null){	   
		   String ret = "";
		   for(int i=0;i<this.getNumberUsers();i++){
   				ret += "\n";
   				for(int j=0;j<this.getNumberCampaigns();j++){
   					if(this.isConnected(i, j)){
   						ret += "\t yes";
   					}else{
   						ret += "\t no";
   					}
   				}
   			}
   			return ret+"\n";
	   }else{
		   return "The connection matrix is not initialized";
	   }
   }
    /*
     * Just a helper function to print the allocation matrix
     */
    protected String printAllocationMatrix(){
    	if(this.allocationMatrix != null){    	
    		String ret = "";
    		for(int i=0;i<this.getNumberUsers();i++){
    			ret += "\n";
    			for(int j=0;j<this.getNumberCampaigns();j++){
    				ret += "\t"+this.allocationMatrixEntry(i, j) ;
    			}	
    		}
    		return ret+"\n";
    	}else{
    		return "There is no allocation for this market";
    	}
    }
    
    public static void printPrices(double[] prices){
    	for(int i=0;i<prices.length;i++){
    		System.out.println("P("+i+") = " + prices[i]);
    	}
    }
    
    protected String printCampaignsRewards(){
    	String ret = "";
    	for(int j=0;j<this.getNumberCampaigns();j++){
    		ret += "\nR("+j+") = "+ this.campaigns[j].totalValue;
    	}
    	return ret;
    }
    
    @Override
    public String toString(){
    	
    	return  "NbrCampaigns:\t"+this.getNumberCampaigns() + "\n" +
    			"NbrUsers:\t"+this.getNumberUsers() + "\n" +
    			"Campaigns Rewards" + this.printCampaignsRewards() + "\n" +
    			"Connections Matrix:\t"+this.printConnectionsMatrix() + "\n" +
    			"Allocation Matrix:\t" + this.printAllocationMatrix();
    }
    
}

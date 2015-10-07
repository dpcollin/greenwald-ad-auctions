package com.greenwald;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Daniel on 9/25/15.
 */

/**
 * Represents an entire game with sets of Campaigns and UserSets.
 *
 * Contains an initializer to generate a random game based upon some given restrictions
 */
public class Game {
    Campaign[] campaigns;
    UserSet[] userSets;
    Connection[] connections;

    public Game(Campaign[] campaigns, UserSet[] userSets, Connection[] connections){
        this.campaigns = campaigns;
        this.userSets = userSets;
        this.connections = connections;
    }

    public Game(Game game){
        this.campaigns = Support.campaignArrayCopy(game.campaigns);
        this.userSets = Support.userSetArrayCopy(game.userSets);
        this.connections = Support.connectionArrayCopy(game.connections);
    }

    //constructor for a new game given certain constraints
    public Game(int minNumCampaigns, int maxNumCampaigns, double minPricePerCampaign, double maxPricePerCampaign,int minImpressionsPerCampaign, int maxImpressionsPerCampaign, int minNumUserSets, int maxNumUserSets, int minUsersPerSet, int maxUsersPerSet, double ratioOfConnections){
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

    //constructor for a new game given default constraints
    public static Game autoGenerate(){
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

        return new Game(minNumCampaigns,maxNumCampaigns,minPricePerCampaign,maxPricePerCampaign,minImpressionsPerCampaign,maxImpressionsPerCampaign,minNumUserSets,maxNumUserSets,minUsersPerSet,maxUsersPerSet,ratioOfConnections);
    }
}

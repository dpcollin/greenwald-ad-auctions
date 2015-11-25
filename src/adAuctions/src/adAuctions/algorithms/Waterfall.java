package adAuctions.algorithms;

import adAuctions.structures.*;

/**
 * Created by Daniel on 9/28/15.
 */
public class Waterfall {
    public static returnSet WaterfallAlgorithm(Market start){
        Allocation[][] allocations = new Allocation[start.campaigns.length][start.userSets.length];
        Market modifyableGame = new Market(start);
        boolean[] isCampaignFeasible = new boolean[start.campaigns.length];
        boolean[] isUserSetEmpty = new boolean[start.userSets.length];
        double[] minObservedPrices = new double[start.campaigns.length];
        BidSet[] bids = new BidSet[start.userSets.length];

        int iter = 0;

        while (iter < start.campaigns.length){
            minObservedPrices[iter] = Double.MAX_VALUE;
            iter++;
        }

        int x = 0;
        int y;

        while(x < start.campaigns.length){
            y=0;
            while(y < start.userSets.length){
                allocations[x][y] = new Allocation(0.0,0);
                y++;
            }
            x++;
        }

        while(true) {
            boolean unfilledFeasibleCampaignExists = false;
            boolean unusedSupplyExists = false;

            int i = 0;

            while (i < start.campaigns.length){
                if(modifyableGame.campaigns[i].getNumImpressions() == 0){
                    isCampaignFeasible[i] = false;
                    i++;
                    continue;
                }
                int supply = calculateSupply(modifyableGame,i);
                if(supply < modifyableGame.campaigns[i].getNumImpressions()){
                    isCampaignFeasible[i] = false;
                }else{
                    isCampaignFeasible[i] = true;
                    unfilledFeasibleCampaignExists = true;
                }

                i++;
            }

            i = 0;

            while (i < start.userSets.length){
                if(modifyableGame.userSets[i].getNumUsers() == 0){
                    isUserSetEmpty[i] = true;
                }else{
                    isUserSetEmpty[i] = false;
                    unusedSupplyExists = true;
                }
                i++;
            }

            if(!(unfilledFeasibleCampaignExists && unusedSupplyExists)){break;}

            i = 0;

            while (i < start.userSets.length){
                bids[i] = new BidSet(-1,modifyableGame.userSets[i].getReserve());
                i++;
            }

            i = 0;
            int j = 0;
            while(i < start.campaigns.length){
                if(isCampaignFeasible[i]){
                    j = 0;
                    double bidPrice = modifyableGame.campaigns[i].getValuePerImpression();
                    while (j < modifyableGame.campaigns[i].connections.size()){
                        int connection = modifyableGame.campaigns[i].connections.get(j);
                        bids[connection].update(i,bidPrice);
                        j++;
                    }
                }
                i++;
            }
            //garbage starting bidset
            BidSet lowestSecondBid = new BidSet(999,999);
            lowestSecondBid.secondValue = Double.MAX_VALUE;
            int lowestBidIndex = -1;
            i = 0;

            while(i < bids.length) {
                if(!(isUserSetEmpty[i]) && (bids[i].secondValue != -1.) && (bids[i].secondValue < lowestSecondBid.secondValue)){
                    lowestSecondBid = bids[i];
                    lowestBidIndex = i;
                }
                i++;
            }

            int campaignRequiredImpressions = modifyableGame.campaigns[lowestSecondBid.highIndex].getNumImpressions();
            int supplyAllowedImpressions = modifyableGame.userSets[lowestBidIndex].getNumUsers();

            int allocated = Math.min(campaignRequiredImpressions,supplyAllowedImpressions);
            double bidprice = lowestSecondBid.secondValue;

            if(lowestSecondBid.secondIndex == -1){
                bidprice = Math.min(lowestSecondBid.highValue,minObservedPrices[lowestSecondBid.highIndex]);
            }

            //modifyableGame.userSets[lowestBidIndex].numUsers -= allocated;
            modifyableGame.userSets[lowestBidIndex].subtractFromNumUser(allocated);
            
            modifyableGame.campaigns[lowestSecondBid.highIndex].modifyValuesBy(allocated,bidprice);

            i = 0;

            while(i < modifyableGame.userSets[lowestBidIndex].getConnections().size()){
                int campaignIndex = modifyableGame.userSets[lowestBidIndex].getConnections().get(i);
                minObservedPrices[campaignIndex] = Math.min(minObservedPrices[campaignIndex],bidprice);
                i++;
            }

            allocations[lowestSecondBid.highIndex][lowestBidIndex] = new Allocation(bidprice,allocated);
        }

        return new returnSet(allocations,modifyableGame);
    }

    private static int calculateSupply(Market game, int campaignIndex){
        Campaign target = game.campaigns[campaignIndex];
        int count = 0;

        int i = 0;
        while(i < target.connections.size()){
            int thisUserSet = target.connections.get(i);
            count += game.userSets[thisUserSet].getNumUsers();
            i++;
        }

        return count;
    }
}

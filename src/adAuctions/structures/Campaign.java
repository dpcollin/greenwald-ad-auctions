package adAuctions.structures;

/**
 * Created by Daniel on 9/25/15.
 */

import java.util.ArrayList;

/**
 * Represents a campaign with a number of impressions desired and a value per impression offered
 */

public class Campaign {
    public int numImpressions;
    public double valuePerImpression;
    public double totalValue;
    public ArrayList<Integer> connections;

    public Campaign(int numImpressions, double valuePerImpression){
        this.numImpressions = numImpressions;
        this.valuePerImpression = valuePerImpression;
        this.totalValue = numImpressions * valuePerImpression;
        this.connections = new ArrayList<>();
    }

    public Campaign(Campaign campaign){
        this.numImpressions = campaign.numImpressions;
        this.valuePerImpression = campaign.valuePerImpression;
        this.totalValue = campaign.totalValue;
        this.connections = campaign.connections;
    }

    public void updateValues(int numImpressions, double totalValue){
        this.numImpressions = numImpressions;
        this.totalValue = totalValue;
        this.valuePerImpression = totalValue/numImpressions;
    }

    public void modifyValuesBy(int impressionsLost, double valuePerImpression){
        this.numImpressions -= impressionsLost;
        this.totalValue -= (impressionsLost * valuePerImpression);
        this.valuePerImpression = this.totalValue/this.numImpressions;
    }
}

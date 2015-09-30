package com.greenwald;

/**
 * Created by Daniel on 9/30/15.
 */
public class BidSet {
    int highIndex;
    double highValue;
    int secondIndex;
    double secondValue;

    public BidSet(int highIndex,double highValue){
        this.highIndex = highIndex;
        this.highValue = highValue;
        this.secondValue = -1.;
    }

    public void update(int newIndex, double newValue){
        if(newValue >= this.highValue){
            this.secondIndex = this.highIndex;
            this.highIndex = newIndex;
            this.secondValue = this.highValue;
            this.highValue = newValue;
        }
    }

}

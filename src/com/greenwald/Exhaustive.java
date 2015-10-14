package com.greenwald;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Daniel on 10/7/15.
 */

//only applies to unit impressions available/wanted per campaign
public class Exhaustive {
    double maxValue;
    ArrayList<Allocation[][]> sols;

    public Exhaustive(){
        this.maxValue = 0;
        this.sols = new ArrayList<>();
    }

    public ArrayList<Allocation[][]> search(Game start){
        Allocation[][] sol = new Allocation[start.campaigns.length][start.userSets.length];
        boolean[] isUserTaken = new boolean[start.userSets.length];

        for (int i = 0; i < start.campaigns.length; i++) {
            for (int j = 0; j < start.userSets.length; j++) {
                sol[i][j] = new Allocation(0.0,0);
            }
        }

        for (int i = 0; i < start.userSets.length; i++) {
            isUserTaken[i] = false;
        }


        iterator(start,sol,isUserTaken,0.0,0);
        /*
        int maxindex = -1;
        double maxvalue = 0.0;

        for (int i = 0; i < values.size(); i++) {
            if(values.get(i) > maxvalue){
                maxindex = i;
                maxvalue = values.get(i);
            }
        }*/

        return this.sols;//.get(maxindex);
    }

    public void iterator(Game game,Allocation[][] sol, boolean[] isUserTaken, Double value, int campaign){
        if(campaign == game.campaigns.length){
            if(value < maxValue){
                return;
            }else if(value > maxValue){
                this.sols = new ArrayList<>();
                maxValue = value;
            }
            this.sols.add(sol);
            return;
        }

        double maxValHere = value;
        for (int i = campaign; i < game.campaigns.length; i++) {
            maxValHere += game.campaigns[i].totalValue;
        }

        if(maxValHere < this.maxValue){
            return;
        }


        for (int i = 0; i < game.campaigns[campaign].connections.size(); i++) {
            int userSet = game.campaigns[campaign].connections.get(i);
            Allocation[][] clonedSol = Allocation.copy2D(sol);
            boolean[] clonedIsUserTaken = Arrays.copyOf(isUserTaken,isUserTaken.length);
            double newValue = value;
            if(clonedIsUserTaken[userSet] == false){
                clonedSol[campaign][userSet] = new Allocation(game.campaigns[campaign].valuePerImpression,1);
                clonedIsUserTaken[userSet] = true;
                newValue += game.campaigns[campaign].valuePerImpression;
            }else{
                continue;
            }
            iterator(game,clonedSol,clonedIsUserTaken,newValue,campaign+1);
        }
        Allocation[][] clonedSol = Allocation.copy2D(sol);
        boolean[] clonedIsUserTaken = Arrays.copyOf(isUserTaken,isUserTaken.length);
        double newValue = value;
        iterator(game,clonedSol,clonedIsUserTaken,newValue,campaign+1);
    }
}

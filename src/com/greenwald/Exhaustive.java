package com.greenwald;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by Daniel on 10/7/15.
 */

//only applies to unit impressions available/wanted per campaign
public class Exhaustive {
    public static Allocation[][] Exhaustive(Game start){
        ArrayList<Allocation[][]> sols = new ArrayList<>();
        Allocation[][] sol = new Allocation[start.campaigns.length][start.userSets.length];
        ArrayList<Double> values = new ArrayList<>();
        boolean[] isUserTaken = new boolean[start.userSets.length];

        for (int i = 0; i < start.campaigns.length; i++) {
            for (int j = 0; j < start.userSets.length; j++) {
                sol[i][j] = new Allocation(0.0,0);
            }
        }

        for (int i = 0; i < start.userSets.length; i++) {
            isUserTaken[i] = false;
        }


        iterator(start,sol,sols,isUserTaken,0.0,values,0);
        int maxindex = -1;
        double maxvalue = 0.0;

        for (int i = 0; i < values.size(); i++) {
            if(values.get(i) > maxvalue){
                maxindex = i;
                maxvalue = values.get(i);
            }
        }

        return sols.get(maxindex);
    }

    public static void iterator(Game game,Allocation[][] sol, ArrayList<Allocation[][]> sols, boolean[] isUserTaken, Double value, ArrayList<Double> values, int campaign){
        if(campaign == game.campaigns.length){
            sols.add(sol);
            values.add(value);
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
                return;
            }
            iterator(game,clonedSol,sols,clonedIsUserTaken,newValue,values,campaign+1);
        }
        Allocation[][] clonedSol = Allocation.copy2D(sol);
        boolean[] clonedIsUserTaken = Arrays.copyOf(isUserTaken,isUserTaken.length);
        double newValue = value;
        iterator(game,clonedSol,sols,clonedIsUserTaken,newValue,values,campaign+1);
    }
}

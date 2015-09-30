package com.greenwald;

/**
 * Created by Daniel on 9/28/15.
 */
public class Support {
    public static int adjustRand(int random, int spread, int min){
        return mod(random,spread) + min;
    }

    public static double adjustRand(double random, double spread, double min){
        return min + (spread * random);
    }

    public static int mod(int a, int b){
        return (a % b + b) % b;
    }

    public static double mod(double a, double b){
        return (a % b + b) % b;
    }

    public static Campaign[] campaignArrayCopy(Campaign[] array){
        int i = 0;
        Campaign[] toreturn = new Campaign[array.length];
        while(i<array.length){
            toreturn[i] = new Campaign(array[i]);
            i++;
        }
        return toreturn;
    }

    public static Connection[] connectionArrayCopy(Connection[] array){
        int i = 0;
        Connection[] toreturn = new Connection[array.length];
        while(i<array.length){
            toreturn[i] = new Connection(array[i]);
            i++;
        }
        return toreturn;
    }

    public static UserSet[] userSetArrayCopy(UserSet[] array){
        int i = 0;
        UserSet[] toreturn = new UserSet[array.length];
        while(i<array.length){
            toreturn[i] = new UserSet(array[i]);
            i++;
        }
        return toreturn;
    }
}

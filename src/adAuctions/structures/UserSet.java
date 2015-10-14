package adAuctions.structures;

/**
 * Created by Daniel on 9/25/15.
 */

import java.util.ArrayList;

/**
 * Represents a set of users with a single price
 */
public class UserSet {
    public int numUsers;
    public ArrayList<Integer> connections;
    //price is uninitialized, exists for later use if necessary
    public double price;
    public double reserve;

    public UserSet(int numUsers){
        this.numUsers = numUsers;
        this.connections = new ArrayList<>();
        //reserve set to 0 by default
        this.reserve = 0;
    }

    public UserSet(int numUsers, double price){
        this.numUsers = numUsers;
        this.connections = new ArrayList<>();
        this.price = price;
        this.reserve = 0;
    }

    public UserSet(UserSet userSet){
        this.numUsers = userSet.numUsers;
        this.connections = userSet.connections;
        this.price = userSet.price;
        this.reserve = userSet.reserve;
    }
}

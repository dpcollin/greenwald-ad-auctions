package adAuctions.structures;

/**
 * Created by Daniel on 9/25/15.
 */

import java.util.ArrayList;

/**
 * Represents a set of users with a single price
 */
public class UserSet {
	protected int i; //Index of this user set in a market
    protected int numUsers;
    protected ArrayList<Integer> connections;
    //price is uninitialized, exists for later use if necessary
    protected double price;
    protected double reserve;

    /*
     * Constructor. Receives only the number of users.
     */
    public UserSet(int numUsers){
        this.numUsers = numUsers;
        this.connections = new ArrayList<>();
        //reserve set to 0 by default
        this.reserve = 0;
    }
    /*
     * Constructor. Receives both the number of users and price. 
     */
    public UserSet(int numUsers, double price){
        this.numUsers = numUsers;
        this.connections = new ArrayList<>();
        this.price = price;
        this.reserve = 0;
    }

    /*
     * Constructor that copies a user set into this one
     */
    public UserSet(UserSet userSet){
        this.numUsers = userSet.numUsers;
        this.connections = userSet.connections;
        this.price = userSet.price;
        this.reserve = userSet.reserve;
    }
    /*
     * Some useful functions
     */
    public void subtractFromNumUser(int amountToSubtract){
    	this.numUsers -= amountToSubtract;
    }
    /*
     * Getters
     */
    public int getNumUsers(){
    	return this.numUsers;
    }
    public double getPrice(){
    	return this.price;
    }
    public int getIndex(){
    	return this.i;
    }
    public double getReserve(){
    	return this.reserve;
    }
    public ArrayList<Integer> getConnections(){
    	return this.connections;
    }
    /*
     * Setters
     */
    public void setPrice(double price){
    	this.price = price;
    }
    public void setIndex(int i){
    	this.i = i;
    }
    public void setNumUsers(int numUsers){
    	this.numUsers = numUsers;
    }
    @Override
    public String toString(){
		return  "(i = " + this.i + ", numUsers = "+ this.numUsers + ", pricePerUser = " + this.price +")";
    }
}

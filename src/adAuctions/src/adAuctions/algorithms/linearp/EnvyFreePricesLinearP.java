package adAuctions.algorithms.linearp;

import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import java.util.ArrayList;

import adAuctions.structures.Market;
/*
 * This class takes a Market Object M, which we assume has an allocation matrix,
 * and solves a Linear Programming program to find envy-free prices according to conditions: 
 * compact, A and B.
 * 
 * As of now (Oct-5-2015), this class only works for mono users and mono campaigns.
 *  
 * @author Enrique Areyan
 */
public class EnvyFreePricesLinearP {
	/*
	 * Boolean to control whether or not to output.
	 */
	protected boolean verbose = true;
	/*
	 * The market for which we are going to build the linear
	 * programming solution
	 */
	protected Market market;
	/*
	 * Contains all the linear constrains
	 */
	protected ArrayList<IloRange> linearConstrains;
	/*
	 * Objects needed to interface with CPlex Library.
	 */
	protected IloNumVar[] prices;
	protected IloCplex cplex;
 	protected IloNumVar[][] var;
	
	/*
	 * Constructor receives a game G.
	 */
	public EnvyFreePricesLinearP(Market G){
		this.market = G;
	}
	public EnvyFreePricesLinearP(){
		
	}
	/*
	 * This method generate the compact conditions.
	 * Note that this method also sets up the alphas to be minimized.
	 */
	protected void generateCompactConditions() throws IloException{
		//System.out.println("NumberCampaigns = "+ this.G.getNumberCampaigns());
		//System.out.println("NumberUsers = "+ this.G.getNumberUsers());
		for(int i=0;i<this.market.getNumberUsers();i++){
			for(int j=0;j<this.market.getNumberCampaigns();j++){
				if(this.market.allocationMatrixEntry(i, j) > 0){
					//System.out.print("\t x["+i+","+j+"] = " + this.G.allocationMatrixEntry(i, j));
					/*
					 * In this case we have to add a condition for the compact condition
					 */
					for(int k=0;k<this.market.getNumberUsers();k++){
						//If you are connected to this campaign and you don't get all of this campaign
						if(this.market.isConnected(k, j) && this.market.allocationMatrixEntry(k, j) < this.market.getUser(k).getNumUsers()){
							if(this.verbose){
								System.out.println("Add compact condition for user "+k+" on campaign " + j + ", where x_{"+k+j+"} = " + this.market.allocationMatrixEntry(k, j));
								System.out.println("\t-- Price("+i+") less than Price("+k+")");
							}
							this.linearConstrains.add(this.cplex.addLe(
				    	    							this.cplex.sum(	this.cplex.prod(-1.0, this.prices[k]),
				    	    											this.cplex.prod( 1.0, this.prices[i])), 0.0));
						}
					}
				}
			}
		}	
	}
	/*
	 * This method generates conditions A.
	 */
	protected void generateConditionA() throws IloException{
		
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			if(!this.market.isCampaignBundleZero(j)){
				if(this.verbose) System.out.println("\nCondition A applies to campaign "+j);
				/*
				 * WARNING: the following loop will work only because we know that
				 * any campaign gets allocated at most one user
				 */
				for(int k=0;k<this.market.getNumberUsers();k++){
					if(this.market.allocationMatrixEntry(k, j) > 0){
						if(this.verbose) System.out.println("\t-- For condition A Price("+k +")<=: "+this.market.getCampaign(j).getReward());
						this.linearConstrains.add(cplex.addLe(cplex.prod(1.0, this.prices[k]), this.market.getCampaign(j).getReward()));
					}
				}
			}
		}
		
	}
	/*
	 * This method generates conditions B.
	 */
	protected void generateConditionB() throws IloException{
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			if(this.market.isCampaignBundleZero(j)){
				if(this.verbose) System.out.println("\nCondition B applies to campaign "+j);
				/*
				 * WARNING: the following loop will work only because we know that
				 * any campaign gets allocated at most one user
				 */
				for(int k=0;k<this.market.getNumberUsers();k++){
					if(this.market.isConnected(k, j) && this.market.allocationMatrixEntry(k, j) == 0){
						if(this.verbose) System.out.println("\t\t-- For condition B user "+k + " should be greater than or equal reward: "+this.market.getCampaign(j).getReward());
						this.linearConstrains.add(cplex.addGe(cplex.prod(1.0, this.prices[k]), this.market.getCampaign(j).getReward()));
					}
				}
			}
		}		
	}
	/*
	 * This method generates conditions C.
	 * This condition is a way of not letting price of users that
	 * have no allocation go unbounded. As of now we set the price of an
	 * unallocated user to be equal to the highest priced campaign
	 */
	protected void generateConditionC() throws IloException{
		for(int i=0;i<this.market.getNumberUsers();i++){
			if(this.market.isUserAllocationZero(i)){
				if(this.verbose) System.out.println("\n Condition C applies to user "+i);
				this.linearConstrains.add(cplex.addEq(cplex.prod(1.0,this.prices[i]), this.market.getHighestReward()));
			}
		}
		
	}
	/*
	 * This method generates the objective function max P1+P2+...+Pn
	 * First set upper and lower bound according to 
	 * how many prices we want to solve for, i.e.,
	 * how many users. Also set the objective function
	 * to P1+P2+...+Pn
	 */
	protected void generatePriceObjective() throws IloException{
	 	double[] lb = new double[this.market.getNumberUsers()];
	 	double[] ub = new double[this.market.getNumberUsers()];
	    double[] objvals = new double[this.market.getNumberUsers()];
	 	
	 	for(int i=0;i<this.market.getNumberUsers();i++){
	 		lb[i] = 0.0;
	 		ub[i] = Double.MAX_VALUE;
	 		objvals[i] = 1.0;
	 	}
	 	this.prices  = this.cplex.numVarArray(this.market.getNumberUsers(), lb, ub);
	    this.var[0] = this.prices;
	    this.cplex.addMaximize(this.cplex.scalProd(this.prices, objvals));
		
	}	
	/*
	 * This is the most important method of this class. 
	 * It build the linear programming and solves it.
	 */
	public double[] Solve(){
		try {
			if(this.verbose) System.out.println("\n==================Optimizing prices for the following market:=======================\n" + this.market);
			this.linearConstrains = new ArrayList<IloRange>();
			this.cplex = new IloCplex();
			if(!this.verbose) cplex.setOut(null);
		 	IloRange[][]  rng = new IloRange[1][];
		 	this.var = new IloNumVar[1][];
		 	
		 	this.generatePriceObjective();
		 	this.generateCompactConditions();
			this.generateConditionA();
			this.generateConditionB();
			this.generateConditionC();
			
    	    rng[0] = new IloRange[this.linearConstrains.size()];
    	    if(this.verbose) System.out.println("\n***Total Conditions:" + this.linearConstrains.size()+"*******");
    	    for(int i=0;i<this.linearConstrains.size();i++){
    	    	rng[0][i] = this.linearConstrains.get(i);
    	    }
    	    if ( cplex.solve() ) {
        	    double[] envyFreePrices = new double[this.market.getNumberUsers()];
    		 	for(int i=0;i<this.market.getNumberUsers();i++){
            	    envyFreePrices[i]= -1.0;
    		 	}        	    
    	    	double[] y     = cplex.getValues(this.var[0]);
    	        /*double[] dj    = cplex.getReducedCosts(var[0]);
    	        double[] pi    = cplex.getDuals(rng[0]);
    	        double[] slack = cplex.getSlacks(rng[0]);*/

    	    	if(this.verbose){
    	    		System.out.println("Solution status = " + cplex.getStatus());
    	    		System.out.println("Solution value  = " + cplex.getObjValue());
    	    	}
    	        int ncols = cplex.getNcols();
    	        if(this.verbose) System.out.println("Optimal Prices");
    	        for (int j = 0; j < ncols; ++j) {
    	        	//System.out.println("Column: " + j +" Value = "+ y[j] +" Reduced cost = "+dj[j]);
    	        	if(this.verbose) System.out.println("P("+j+") = " + y[j]);
    	        	envyFreePrices[j] = y[j];
    	        }

    	        /*int nrows = cplex.getNrows();
    	        for (int i = 0; i < nrows; ++i) {
    	        System.out.println("Row   : " + i + " Slack = " + slack[i] + " Pi = " + pi[i]);
    	        }*/
    	        return envyFreePrices;
    	    }
		} catch (IloException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception: ==>");
			e.printStackTrace();
		}
		return null;//if we reach this point, then there were no envy-free prices
	}
}

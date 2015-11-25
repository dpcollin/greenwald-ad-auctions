package adAuctions.algorithms.linearp;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import java.util.ArrayList;

import adAuctions.structures.Market;

public class EnvyFreePrices {
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
	protected IloNumVar[] alphas;
	protected IloCplex cplex;
 	protected IloNumVar[][] var;
 	
	/*
	 * Constructor receives a market M.
	 */
	public EnvyFreePrices(Market M){
		this.market = M;
	}
	
	/*
	 * This method generates conditions A.
	 */
	protected void generateConditionA() throws IloException{
		
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			if(!this.market.isCampaignBundleZero(j)){
				if(this.verbose) System.out.println("\nCondition A applies to campaign "+j);
				IloLinearNumExpr lhs = cplex.linearNumExpr();
				for(int k=0;k<this.market.getNumberUsers();k++){
						this.linearConstrains.add(cplex.addLe(cplex.prod(1.0, this.prices[k]), this.market.getCampaign(j).getReward()));
						lhs.addTerm(this.market.allocationMatrixEntry(k, j), this.prices[k]);
				}
				this.linearConstrains.add(cplex.addLe(lhs, this.market.getCampaign(j).getReward()));
				if(this.verbose) System.out.println(cplex.addLe(lhs, this.market.getCampaign(j).getReward()));
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
	 * This method generate the compact conditions.
	 */
	protected void generateCompactConditions() throws IloException{
		for(int i=0;i<this.market.getNumberUsers();i++){
			for(int j=0;j<this.market.getNumberCampaigns();j++){
				if(this.market.allocationMatrixEntry(i, j) > 0){
					System.out.println(i+","+j);
					/*
					 * In this case we have to add a condition for the compact condition
					 */
					for(int k=0;k<this.market.getNumberUsers();k++){
						//If you are connected to this campaign and you don't get all of this campaign
						System.out.println("\t"+k+","+j);
						if(this.market.isConnected(k, j) && this.market.allocationMatrixEntry(k, j) < this.market.getUser(k).getNumUsers()){
							if(this.verbose){
								System.out.println("Add compact condition for user "+k+" on campaign " + j + ", where x_{"+k+j+"} = " + this.market.allocationMatrixEntry(k, j));
								System.out.println("\t Price("+i+") <= Price("+k+") + Alpha("+k+")");
							}
							this.linearConstrains.add(this.cplex.addLe(
				    	    							this.cplex.sum(	
				    	    											this.cplex.sum(	this.cplex.prod(-1.0,	this.alphas[k]),
				    	    															this.cplex.prod(-1.0, 	this.prices[k])),
				    	    											this.cplex.prod( 1.0, this.prices[i])), 0.0));
						}
					}
				}
			}
		}	
	}
	
	/*
	 * This function generates the objective function, i.e., minimize alphas.
	 * It also sets up the variables we need
	 */
	protected void generateVariablesAndObjective() throws IloException{
	 	double[] lb = new double[this.market.getNumberUsers()];
	 	double[] ub = new double[this.market.getNumberUsers()];
	    double[] objvals = new double[this.market.getNumberUsers()];
	 	
	 	for(int i=0;i<this.market.getNumberUsers();i++){
	 		lb[i] = 0.0;
	 		ub[i] = Double.MAX_VALUE;
	 		objvals[i] = 1.0;
	 	}
	 	System.out.println("generateAlphaObjective " + this.market.getNumberUsers());
	 	this.alphas  = this.cplex.numVarArray(this.market.getNumberUsers(), lb, ub);
	    this.var[0] = this.alphas;

	    this.prices  = this.cplex.numVarArray(this.market.getNumberUsers(), lb, ub);
	    this.var[1] = this.prices;
	    
	    //this.cplex.addMaximize(this.cplex.scalProd(this.alphas, objvals));
	    this.cplex.addMinimize(this.cplex.scalProd(this.alphas, objvals));
	}	
	
	/*
	 * This is the most important method of this class. 
	 * It build the linear programming and solves it.
	 */
	public double[] Solve(){
		try{
			if(this.verbose) System.out.println("\n==================Optimizing prices for the following market:=======================\n" + this.market);
			this.linearConstrains = new ArrayList<IloRange>();
			this.cplex = new IloCplex();
			if(!this.verbose) cplex.setOut(null);
		 	IloRange[][]  rng = new IloRange[1][];
		 	this.var = new IloNumVar[2][];
		 	
		 	this.generateVariablesAndObjective();
			this.generateConditionA();
			//this.generateConditionB();
			//this.generateConditionC();
		 	this.generateCompactConditions();
		 	
		    rng[0] = new IloRange[this.linearConstrains.size()];
		    if(this.verbose) System.out.println("\n***Total Conditions:" + this.linearConstrains.size()+"*******");
	
		    if ( cplex.solve() ) {
		    	double[] LP_Prices     = cplex.getValues(this.prices);
		    	double[] Alphas     = cplex.getValues(this.alphas);	
		    	
		    	if(this.verbose){
		    		System.out.println("Solution status = " + cplex.getStatus());
		    		System.out.println("Solution value  = " + cplex.getObjValue());
		    	}
		        int ncols = cplex.getNcols();
		        System.out.println("ncols = "+ncols);
		        if(this.verbose) System.out.println("Optimal Prices");
		        for (int j = 0; j < this.market.getNumberUsers(); j++) {
		        	if(this.verbose){
		        		System.out.println("P("+j+") = " + LP_Prices[j]);
		        	}
		        }
		        for(int j = 0; j<this.market.getNumberUsers(); j++){
		        	if(this.verbose){
		        		System.out.println("Alpha("+j+") = " + Alphas[j]);    	        		
		        	}
		        }
		        return LP_Prices;
		    }
		} catch (IloException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception: ==>");
			e.printStackTrace();
		}	    
		return null;
	}
}

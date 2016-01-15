package adAuctions.algorithms.linearp;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import java.util.ArrayList;

import adAuctions.structures.Market;

public class EnvyFreePricesCompactCondition {

	/*
	 * Boolean to control whether or not to output.
	 */
	protected boolean verbose = false;
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
 	protected IloNumVar[][] I1;

	/*
	 * Constructor receives a market M.
	 */
	public EnvyFreePricesCompactCondition(Market M){
		this.market = M;
	} 	
	
	/*
	 * This method generate the compact conditions.
	 */
	protected void generateCompactConditions() throws IloException{
		if(this.verbose) System.out.println("--- Start generate Compact Conditions ---");
		for(int i=0;i<this.market.getNumberUsers();i++){
			for(int j=0;j<this.market.getNumberCampaigns();j++){
				if(this.market.allocationMatrixEntry(i, j) > 0){
					if(this.verbose) System.out.println("Entry: "+i+","+j);
					/*
					 * In this case we have to add a condition for the compact condition
					 */
					for(int k=0;k<this.market.getNumberUsers();k++){						
						//If you are connected to this campaign and you don't get all of this campaign
						if(i!=k && this.market.isConnected(k, j) && this.market.allocationMatrixEntry(k, j) < this.market.getUser(k).getNumUsers()){
							if(this.verbose){
								System.out.println("Add compact condition for user "+k+" on campaign " + j + ", where x_{"+k+j+"} = " + this.market.allocationMatrixEntry(k, j));
								System.out.println("\t Price("+i+") <= Price("+k+")");
							}
							this.linearConstrains.add(this.cplex.addLe(this.cplex.sum(	this.cplex.prod(-1.0, 	this.prices[k]), this.cplex.prod( 1.0, this.prices[i])), 0.0));
						}
					}
				}
			}
		}
		if(this.verbose) System.out.println("--- End generate Compact Conditions ---");		
	}

	public LPSolution Solve(){
		double[] LP_Prices  = {};
		LPSolution Solution = new LPSolution();
		try{
			this.cplex = new IloCplex();			
			if(!this.verbose) this.cplex.setOut(null);
			this.linearConstrains = new ArrayList<IloRange>();
		 	IloRange[][]  rng = new IloRange[1][];
		 	this.var = new IloNumVar[2][];
		 	cplex.setParam(IloCplex.BooleanParam.PreInd, false);

		 	double[] lb = new double[this.market.getNumberUsers()];
		 	double[] ub = new double[this.market.getNumberUsers()];
		    double[] objvals = new double[this.market.getNumberUsers()];
		    this.I1 = new IloNumVar[this.market.getNumberUsers()][];
		    for(int i=0;i<this.market.getNumberUsers();i++){
		 		lb[i] = 0.0;
		 		ub[i] = Double.MAX_VALUE;
		 		objvals[i] = 1.0;
				this.I1[i] = cplex.intVarArray(this.market.getNumberCampaigns(),0,Integer.MAX_VALUE);
		 	}
		    
		    this.prices  = this.cplex.numVarArray(this.market.getNumberUsers(), lb, ub);
		    this.var[1] = this.prices;

		    this.cplex.addMinimize(this.cplex.scalProd(this.prices, objvals));
		    //this.cplex.addMaximize(this.cplex.scalProd(this.prices, objvals));
		    
		    this.generateCompactConditions();
		    this.generateIndividualRationalityConditions();
		    //this.generateIndividualRationalityConditions();
		    
		    rng[0] = new IloRange[this.linearConstrains.size()];
		    
		    if ( cplex.solve() ) {
		    	LP_Prices     = cplex.getValues(this.prices);
		    	Solution = new LPSolution(LP_Prices,cplex.getStatus().toString());
		    }else{
		    	Solution = new LPSolution(cplex.getStatus().toString());
		    }

	    	if(this.verbose){
	    		System.out.println("Solution status = " + cplex.getStatus());
	    		System.out.println("Solution value  = " + cplex.getObjValue());
	    	}		    	
	    	
		 	
		} catch (IloException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception: ==>");
			e.printStackTrace();
		}
		return Solution;
	}

	
	
	
	/*
	 * This method generate the Individual Rationality condition.
	 */
	protected void generateIndividualRationalityConditions() throws IloException{
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			if(!this.market.isCampaignBundleZero(j)){
				IloLinearNumExpr lhs = cplex.linearNumExpr();
				int counter = 0;
				for(int i=0;i<this.market.getNumberUsers();i++){
					if(this.market.allocationMatrixEntry(i, j)>0){
						lhs.addTerm(this.market.allocationMatrixEntry(i, j), this.prices[i]);
						counter += this.market.allocationMatrixEntry(i, j);
					}
				}
				if(counter >= this.market.getCampaign(j).getNumImpressions()){
					this.linearConstrains.add(cplex.addLe(lhs, this.market.getCampaign(j).getReward()));
				}else{
					this.linearConstrains.add(cplex.addLe(lhs, 0));
				}
			}
		}
	}
	
}

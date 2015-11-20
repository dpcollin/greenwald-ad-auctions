package adAuctions.algorithms.linearp;

import java.util.ArrayList;

import adAuctions.structures.Market;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;



public class EfficientAllocationLinearP {
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
	 * Objects needed to interface with CPlex Library.
	 */
	protected IloCplex cplex;
	/*
	 * Constructor receives a game G.
	 */
	public EfficientAllocationLinearP(Market G){
		this.market = G;
	}

	public ArrayList<double[][]> Solve(){
		try {
			this.cplex = new IloCplex();
			if(!this.verbose) cplex.setOut(null);
			/*
			 * These two next parameters controls how many solutions we want to get.
			 * The first parameter controls how far from the optimal we allow solutions to be,
			 * The second parameter controls how many solutions we will get in total.
			 */
			this.cplex.setParam(IloCplex.DoubleParam.SolnPoolGap, 0.0);
			this.cplex.setParam(IloCplex.IntParam.PopulateLim, 1000);
			/*
			 * variables
			 */
			IloNumVar[] y = cplex.boolVarArray(this.market.getNumberCampaigns());
			IloNumVar[][] u = new IloNumVar[this.market.getNumberCampaigns()][];
			for (int i=0; i<this.market.getNumberCampaigns(); i++){
				u[i] = cplex.intVarArray(this.market.getNumberUsers(),0,Integer.MAX_VALUE);
			}
			/*
			 * Objective.
			 */
			IloLinearNumExpr obj = this.cplex.linearNumExpr();
			for (int i=0; i<this.market.getNumberCampaigns(); i++){
				obj.addTerm(this.market.campaigns[i].totalValue, y[i]);
			}
			this.cplex.addMaximize(obj);
			
			/*
			 * Constraints.
			 */
			for (int i=0; i<this.market.getNumberCampaigns(); i++){
				double coeff = 1.0/(double)this.market.campaigns[i].numImpressions;
				IloLinearNumExpr expr = cplex.linearNumExpr();
				for (int j=0; j<this.market.getNumberUsers(); j++){
					if(this.market.isConnected(j, i)){
						expr.addTerm(coeff,u[i][j]);
					}else{
						IloLinearNumExpr restrict = cplex.linearNumExpr();
						restrict.addTerm(1,u[i][j]);
						this.cplex.addEq(0,restrict);
					}
					
				}
				this.cplex.addGe(expr,y[i]);
				this.cplex.addLe(expr,1);
			}
			for (int j=0; j<this.market.getNumberUsers(); j++){
				IloLinearNumExpr expr = cplex.linearNumExpr();
				for (int i=0; i<this.market.getNumberCampaigns(); i++){
					expr.addTerm(1.0,u[i][j]);
				}
				this.cplex.addLe(expr,this.market.userSets[j].numUsers);
			}
			/*
			 * Solve the problem and get many solutions:
			 */
			this.cplex.solve();
			if ( cplex.populate() ) {
				int numsol = cplex.getSolnPoolNsolns();
	            int numsolreplaced = cplex.getSolnPoolNreplaced();
				/*
				 * Print some information
				 */
				if(this.verbose){
					System.out.println("***********************************Populate");
		            System.out.println("The solution pool contains " + numsol + " solutions.");
		            System.out.println(numsolreplaced + " solutions were removed due to the " + "solution pool relative gap parameter.");
		            System.out.println("In total, " + (numsol + numsolreplaced) + " solutions were generated.");
				}
	            /*
	             * Store all the solutions in an ArrayList.
	             */
	            ArrayList<double[][]> Solutions = new ArrayList<>();
	            for (int l = 0; l < numsol; l++) {
	    			double[][] sol = new double[this.market.getNumberCampaigns()][this.market.getNumberUsers()];
	    			for (int i=0; i<this.market.getNumberCampaigns(); i++){
	    				sol[i] = this.cplex.getValues(u[i],l);
	    			}
	    			Solutions.add(sol);
	            }
	            this.cplex.end();
	            return Solutions;
			}
			return null;
		} catch (IloException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception: ==>");
			e.printStackTrace();
		}
		return null;//if we reach this point, then there were no envy-free prices
	}
}

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

	public ArrayList<int[][]> Solve(){
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
				obj.addTerm(this.market.campaigns[i].getReward(), y[i]);
			}
			this.cplex.addMaximize(obj);
			
			/*
			 * Constraints.
			 */
			for (int i=0; i<this.market.getNumberCampaigns(); i++){
				double coeff = 1.0/(double)this.market.campaigns[i].getNumImpressions();
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
				this.cplex.addLe(expr,this.market.userSets[j].getNumUsers());
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
	            ArrayList<int[][]> Solutions = new ArrayList<>();
	            for (int l = 0; l < numsol; l++) {
	            	/*
	            	 * The solution should be a matrix of integers. However, CPLEX returns a matrix of doubles.
	            	 * So we are going to have to cast this into integers.
	            	 */
	    			int[][] sol = new int[this.market.getNumberCampaigns()][this.market.getNumberUsers()];
	    			double[][] solDouble = new double[this.market.getNumberCampaigns()][this.market.getNumberUsers()];
	    			for (int i=0; i<this.market.getNumberCampaigns(); i++){
	    				solDouble[i] = this.cplex.getValues(u[i],l);
	    				/*
	    				 * Unfortunately in Java the only way to cast your array is to iterate through each element and cast them one by one
	    				 */
	    				for(int j=0;j<this.market.getNumberUsers(); j++){
	    					sol[i][j] = (int) solDouble[i][j];
	    				}
	    			}
	    			Solutions.add(cleanMatrix(transposeMatrix(sol)));
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
	/*
	 * Helper method to return the transpose of a matrix.
	 */
	public static int[][] transposeMatrix(int [][] m){
		int[][] temp = new int[m[0].length][m.length];
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[0].length; j++)
				temp[j][i] = m[i][j];
        return temp;
    }
	/*
	 * This method sets the columns of the matrix to zero if a campaign is not 
	 * completely satisfied. This is is line with the way in which all the conditions
	 * and the analysis has been done so far. This method avoids a matrix where a 
	 * campaign might get some allocation when in fact it is not satisfied.
	 */
	protected int[][] cleanMatrix(int[][] efficientAllocation){
		int totalAllocation = 0;
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			for(int i=0;i<this.market.getNumberUsers();i++){
				totalAllocation += efficientAllocation[i][j];
			}
			if(totalAllocation < this.market.getCampaign(j).getNumImpressions()){//This campaign is not satisfied
				for(int i=0;i<this.market.getNumberUsers();i++){
					efficientAllocation[i][j] = 0;
				}
			}
			totalAllocation = 0;
		}
		return efficientAllocation;
	}
}

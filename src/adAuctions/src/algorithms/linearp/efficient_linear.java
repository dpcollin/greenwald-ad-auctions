package adAuctions.algorithms.linearp;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;


import adAuctions.structures.Market;

public class efficient_linear {

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
	public efficient_linear(Market G){
		this.market = G;
	}

	public double[][] Solve(){
		try {
			this.cplex = new IloCplex();
			//variables
			IloNumVar[] y = cplex.boolVarArray(this.market.getNumberCampaigns());

			IloNumVar[][] u = new IloNumVar[this.market.getNumberCampaigns()][];
			for (int i=0; i<this.market.getNumberCampaigns(); i++){
				u[i] = cplex.intVarArray(this.market.getNumberUsers(),0,Integer.MAX_VALUE);
			}
			
			// Objective
			IloLinearNumExpr obj = this.cplex.linearNumExpr();
			for (int i=0; i<this.market.getNumberCampaigns(); i++){
				obj.addTerm(this.market.campaigns[i].totalValue, y[i]);
			}
			this.cplex.addMaximize(obj);
			
			//constraints
			
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
			
			this.cplex.solve();
			double[][] out = new double[this.market.getNumberCampaigns()][this.market.getNumberUsers()];
			for (int i=0; i<this.market.getNumberCampaigns(); i++){
				out[i] = this.cplex.getValues(u[i]);
			}
			
			for (int i=0; i<this.market.getNumberCampaigns(); i++){
				for (int j=0; j<this.market.getNumberUsers(); j++){
					//System.out.print(out[i][j]);
					//System.out.print(" ");
				}
				//System.out.print("\n");
			}
			System.out.print(this.cplex.getObjValue());
			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception: ==>");
			e.printStackTrace();
		}
		return null;//if we reach this point, then there were no envy-free prices
	}
}

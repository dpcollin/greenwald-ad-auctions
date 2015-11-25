package adAuctions.algorithms.linearp.test;

import java.io.IOException;

import adAuctions.graphs.Latex;

public class Main {
	
	public static void main(String args[]) throws IOException{
		MarketFactory.createMultipleMonoMarkets();
		System.exit(0);
		if(args.length <4){
			throw new RuntimeException("need at least 4 parameters to run: int minNumCamp, int maxNumCamp,int minNumUser,int maxNumUser");
		}else{
			if(Integer.parseInt(args[0])>Integer.parseInt(args[1])){
				throw new RuntimeException("The first parameter -minNumCamp- must be less than of equal to the second -maxNumCamp-");
			}
			if(Integer.parseInt(args[2])>Integer.parseInt(args[3])){
				throw new RuntimeException("The third parameter -minNumUser- must be less than of equal to the fourth -maxNumUser-");
			}
			if(args.length == 4){
				MarketFactory.CreateMonoMarket(1.0,Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]));
			}else if(args.length == 6){
				Latex.latexManyMarkets(Double.parseDouble(args[5]),Integer.parseInt(args[4]),Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]));
			}else{
				throw new RuntimeException("numbers of parameters not valid");
			}
		}
	}
}

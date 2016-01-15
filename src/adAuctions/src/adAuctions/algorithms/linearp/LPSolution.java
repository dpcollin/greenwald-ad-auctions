package adAuctions.algorithms.linearp;

public class LPSolution {
	protected double[] Prices;
	String Status;
	
	
	public LPSolution(){
		this.Status = "Empty";
	}
	public LPSolution(String Status){
		this.Status = Status;
	}
	
	public LPSolution(double[] Prices, String Status){
		this.Status = Status;
		this.Prices = Prices;
	}
	
	public String getStatus(){
		return this.Status;
	}
	
	public double[] getPrices(){
		return this.Prices;
	}

}

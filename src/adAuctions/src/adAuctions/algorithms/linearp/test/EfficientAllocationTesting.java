package adAuctions.algorithms.linearp.test;

import java.util.ArrayList;

import adAuctions.algorithms.linearp.EfficientAllocationLinearP;
import adAuctions.structures.Market;


public class EfficientAllocationTesting{
	public static void main(String[] args){
		Market M = new Market(3,3,1,100,1,1,3,3,1,1,1.0);
		M.setConnectionsMatrix();
		EfficientAllocationLinearP eff = new EfficientAllocationLinearP(M);
		ArrayList<int[][]> efficientAllocations = eff.Solve();
		System.out.println("\n" + M);
		System.out.println("We found "+efficientAllocations.size()+" many efficient allocations");
		
		for(int l=0;l<efficientAllocations.size();l++){
			System.out.println("Solution #"+l);
			for (int i=0; i<M.getNumberUsers(); i++){
				for (int j=0; j<M.getNumberCampaigns(); j++){
					System.out.print(efficientAllocations.get(l)[j][i]);
					System.out.print(" ");
				}
				System.out.print("\n");
			}
		}		
		
		/*
		 * The following test is for a market with some number of users,
		 * some number of campaigns where all of them are connected.
		 */
		/*
		int numCam = 10;
		int numUse = 10;
		Connection[] Connections = new Connection[numCam*numUse];
		UserSet[] U = new UserSet[numUse];
		int k=0;
		for(int i=0;i<numUse;i++){
			U[i] = new UserSet(1);
			for(int j=0;j<numCam;j++){
				Connections[k] = new Connection(i,j);
				k++;
			}
		}
		Campaign[] C = new Campaign[numCam];
		for(int j=0;j<numCam;j++){
			C[j] = new Campaign(1,(j+1)*10);
		}
		Market F = new Market(C, U, Connections);
		F.setConnectionsMatrix();
		EfficientAllocationLinearP eff = new EfficientAllocationLinearP(F);
		ArrayList<double[][]> efficientAllocations = eff.Solve();
		System.out.println(F);
		System.out.println("We found "+efficientAllocations.size()+" many efficient allocations");
		
		for(int l=0;l<efficientAllocations.size();l++){
			System.out.println("Solution #"+l);
			for (int i=0; i<numUse; i++){
				for (int j=0; j<numCam; j++){
					System.out.print(efficientAllocations.get(l)[j][i]);
					System.out.print(" ");
				}
				System.out.print("\n");
			}
		}*/		
		
		
	}
}





package adAuctions.algorithms.linearp;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import adAuctions.algorithms.linearp.test.MarketFactory;
import adAuctions.structures.Market;

public class Latex {
	/*
	 * The following parameters indicate the location of various latex files
	 */
	protected static String headerFileLocation = "/home/eareyanv/Dropbox/CSCI2980 - Reading and Research/docs/visualization/common/header.tex";
	protected static String footerFileLocation = "/home/eareyanv/Dropbox/CSCI2980 - Reading and Research/docs/visualization/common/footer.tex";
	protected static String resultFileLocation = "/home/eareyanv/Dropbox/CSCI2980 - Reading and Research/docs/visualization/lib/market.tex";
	/*
	 * Given a market and an array of envy-free prices, produces the 
	 * latex graph using tikzpicture of this market. Note that the array
	 * of envy-free prices might be null, in which case the Linear Program
	 * was unable to find envy-free prices.
	 */
	public static String createLatexGraph(Market M,double[] envyFreePrices){
		DecimalFormat df = new DecimalFormat("#.00"); 
		String latex = "";
				latex += "$$ "+
						 "\\begin{tikzpicture}[thick, every node/.style={draw,circle}, fsnode/.style={fill=myblue}, ssnode/.style={fill=mygreen}, every fit/.style={ellipse,draw,inner sep=10pt,text width=2cm},-,shorten >= 3pt,shorten <= 3pt]";

				latex += "\n% the vertices of U\n" +
						"\\begin{scope}[start chain=going below,node distance=7mm]\n";
				String users  = "";
				for(int i=0;i<M.getNumberUsers();i++){
					if(envyFreePrices == null){
						latex += "\\node[fsnode,on chain] (f"+i+") [label=left: .] {};\n";
					}else{
						latex += "\\node[fsnode,on chain] (f"+i+") [label=left: "+df.format(envyFreePrices[i])+"] {};\n";						
					}
					users += "(f"+i+") ";
				}
				latex += "\\end{scope}";

				latex += "\n% the vertices of C\n" +
						"\\begin{scope}[xshift=5cm,yshift=-0.1cm,start chain=going below,node distance=7mm]\n";
				String camp = "";
				for(int i=0;i<M.getNumberCampaigns();i++){
					latex += "\\node[ssnode,on chain] (s"+(M.getNumberUsers()+i)+") [label=right: \\$"+df.format(M.getCampaign(i).getReward())+"] {};\n";
					camp += "(s"+(M.getNumberUsers()+i)+") ";
				}
				latex += "\\end{scope}";
				latex += "\n% the set U\n" +
						 "\\node [mywhite,fit="+users+"] {};"; 
				latex += "\n% the set C\n" +
						 "\\node [mywhite,fit="+camp+"] {};"; 
				
				latex += "% the edges";
				for(int i=0;i<M.getNumberUsers();i++){
					for(int j=0;j<M.getNumberCampaigns();j++){
						if(M.isConnected(i, j)){
							if(M.allocationMatrixEntry(i, j)>0){
								//latex += "\n\\draw (f"+i+") -- (s"+(M.getNumberUsers()+j)+") node [near start,sloped,fill=mywhite,text width=0.09cm]{1};";
								latex += "\n\\draw (f"+i+") -- (s"+(M.getNumberUsers()+j)+");";
							}else{
								latex += "\n\\draw[dotted] (f"+i+") -- (s"+(M.getNumberUsers()+j)+");";								
							}
						}
					}
				}
				latex += "\n\\end{tikzpicture} $$";
				
				return latex;
	}
	/*
	 * Calls the function createLatexGraph a number 'int number'
	 * of times and produces a latex file with the results of
	 * all these markets.
	 */
	public static void latexManyMarkets(double ratioOfConnections, int number,int minNumCamp, int maxNumCamp,int minNumUser,int maxNumUser) throws IOException{
		System.out.println("ratioOfConnections = "+ ratioOfConnections + ",number="+number + ",minNumCamp = " + minNumCamp +  ",maxNumCamp = " + maxNumCamp + ",minNumUser = " + minNumUser + ",maxNumUser = " + maxNumUser);
		List<Object> list = new ArrayList<Object>();
		String latex = "";
		for(int i=0;i<number;i++){
			list = MarketFactory.createMonoMarketWithOneEfficientAllocation(ratioOfConnections,minNumCamp, maxNumCamp, minNumUser, maxNumUser);
			if(list != null){
				latex += Latex.createLatexGraph((Market) list.get(0),(double []) list.get(1));
			}
		}
		
		String latexHeader = Latex.readFile(Latex.headerFileLocation, StandardCharsets.UTF_8);
		String latexFooter = Latex.readFile(Latex.footerFileLocation, StandardCharsets.UTF_8);
		PrintWriter out = new PrintWriter(Latex.resultFileLocation);
		out.println(latexHeader);
		out.println("\\section*{Envy-Free Prices (as given by LP)}");
		out.println("\\subsection*{Parameters}");
		out.println("\\begin{enumerate}");
		out.println("\\item[] Number of Markets: "+ number);
		out.println("\\item[] Min. number of campaigns: "+ minNumCamp);
		out.println("\\item[] Max. number of campaigns: "+ maxNumCamp);
		out.println("\\item[] Min. number of users: "+ minNumUser);
		out.println("\\item[] Max. number of users: "+ maxNumUser);
		out.println("\\item[] Ratio of Connections: "+ ratioOfConnections);
		out.println("\\end{enumerate}");
		out.println(latex);
		out.println(latexFooter);
		out.close();
		
	}
	/*
	 * A helper function to read a file to a string
	 */
	public static String readFile(String path, Charset encoding) throws IOException{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}	

}

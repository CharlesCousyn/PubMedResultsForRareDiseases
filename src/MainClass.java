

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainClass
{
	public static void main(String args[])
	{
		try
		{
			//Extraction of rare deseases
			Document pageDiseases=Jsoup.connect("https://globalgenes.org/rarelist/").get();
			List<String> listeRareDiseases=new ArrayList<String>();
			
			for(int i= 11; i <=115; i+=4)
			{
				Elements lis=pageDiseases.select("#content > div > div > ul:nth-child("+i+")");
				Elements vraiLis=lis.get(0).getElementsByTag("li");
				for(Element li: vraiLis)
				{
					listeRareDiseases.add(li.text());
				}
			}
			
			PrintWriter out = new PrintWriter("results.csv");
			out.println("NumberOfResults");
			//Traitement des requête de rare disease
			for(String disease: listeRareDiseases)
			{
				//Word Tokenization
				String[] words=disease.split("\\s+");
				//Creation of argument of search request
				String argRequest="";
				for (int i = 0; i <words.length; i++)
				{
					argRequest+=words[i];
					if(i<words.length-1)
					{
						argRequest+="+";
					}
				}
				
				//Extraction numberOfResults
				boolean error=true;
				Document doc = null;
				//We loop to be sure to try again if website goes down for few instants
				while(error)
				{
					try
					{
						doc=Jsoup.connect("https://www.ncbi.nlm.nih.gov/pubmed/?term="+argRequest).get();
						error=false;
					}
					catch(Exception e)
					{
						error=true;
						System.err.println("Error occured");
						System.err.println("We try again...");
					}
				}
				Elements element=doc.select("#maincontent > div > div:nth-child(3) > div:nth-child(1) > h3");
				String text=element.text();
				String numberOfResults="";
				//No results?
				if(text.equals(""))
				{
					numberOfResults="0";
				}
				//There are results !
				else
				{
					boolean isAnInteger=false;
					do
					{
						String number=text.substring(text.length()-1, text.length());
						text=text.substring(0, text.length()-1);
						//Is it an integer?
						isAnInteger=isInteger(number);
						if(isAnInteger)
						{
							numberOfResults=number+numberOfResults;
						}
					}
					while(isAnInteger);
				}
				//We put the number of results in file "results.csv"
				out.println(numberOfResults);
				System.out.println(numberOfResults+"\tresults for rare disease named \""+disease+"\"");
			}
			out.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	//Test if a string could be parsed in Integer
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    // only got here if we didn't return false
	    return true;
	}
}

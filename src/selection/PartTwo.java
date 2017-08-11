package selection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartTwo {
	
	static String basehttp = "http://adam.goucher.ca/parkcalc/index.php?Lot=";
	static String web2http = "http://cesar.org.br/buscar?search=";
	static String[] borderHours = new String[3];
	
	public static void main(String[] args) throws IOException {

		/* Border case 1: 00:00 AM to 12:00 AM Na mesma data
		 * Border case 2: 11:59 PM 00:00 AM Próximo dia
		*/
		
		borderHours[0] = "11%3A59";
		borderHours[1] = "12%3A00";
		borderHours[2] = "00%3A00";
		
		//System.out.println("-----------------------------------------------------------------");
		System.out.println("Web 1:");
		testParkingLot();
		
		System.out.println("Web 2:");
		// Para mudar o termo que queremos procurar, basta mudar o parametro passado para a afuncao.
		getThirdResult("work");
		
		
	}

	/** Metodo utilizado paga fazer a pesquisa na pagina do cesar pelo argumento passado para a funcao. Imprime no console o Titulo da terceira vaga encontrada
	 *  na busca assim como o link para acessar a mesma. **/
	public static void getThirdResult ( String searchTag ) throws IOException {
		
		// Trabalho returns with 0 results, can't find a reason why. Searching for work instead. The html parser is able to handle 0 returns.
		String charset = "UTF-8";
		String searchRequest = "http://www.cesar.org.br/buscar?search=" + searchTag;
		
		URL url = new URL(searchRequest);
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("Accept-Charset", charset);
		
 		BufferedReader br = new BufferedReader(
               new InputStreamReader(connection.getInputStream()));
	    
 		String postTopic = "";
 		String postLink = "";
 		
 		String inputLine;
 		
 		// Nesse loop interpretamos a resposta em HTML para retirar o dado que queremos. Verificacoes feitas para lidar com o caso da
 		// consulta retornar menos que 3 valores, incluindo a possibilidade da mesma retornar nenhum resultado.
	    while ((inputLine = br.readLine()) != null) {
	    	
	    	if ( inputLine.contains("<div class=\"post-list\">")) {
	    		
	    		// Reads through 3 posts, as we want the third post.
	    		for ( int j = 0; j < 3; j++ ) {
	    			
	    			// Reads one post.
	    			for ( int i = 0; i < 10; i++ ) {
		    			
		    			try {
	    				inputLine = br.readLine();
		    			} catch ( Exception e ) {
		    				// get out of this loop.
		    				i = 11;
		    				j = 3;
		    			}
		    			
		    			// i = 4 onde o link se encontra. j = 2 o terceiro item.
		    			if ( j == 2 && i == 4 ) {
		    				postLink = getLink(inputLine);
		    			}
		    			
		    			if ( inputLine.contains("<h3>")) {
		    				// post Title
		    				
		    				if ( j == 2 ) {
		    					postTopic = getPostTitle(inputLine);
		    					i = 11;
		    					j = 4;
				    		}
		    			}
		    		}
	    		}
	    	}
	    }
	    
	    
	    // Prints, imprimem a informacao obtida na interpretacao do html. 
	    if ( postTopic.equals("") || postLink.equals("") ) {
	    	System.out.println("A busca pelo termo " + searchTag + " retornou menos que 3 ou nenhum resultado.");
	    } else {
	    	System.out.println("Titulo da Vaga: " + postTopic);
		    System.out.println("Página Acessada da vaga: " + postLink);
		}
	       
	}
	
	// Dado uma linha em um formato especifico em html, retira o link da mesma.
	public static String getLink( String line ) {
		
		String link = "";
		
		for ( int i = 0; i < line.length(); i ++ ) {
			
			if ( line.charAt(i) == '<' && i+1 < line.length() && line.charAt(i+1) == 'a') {
				i+=9;
				link = line.substring(i, line.length()-2);
			}
			
		}
		
		return link;
	}
	
	// Dado uma linha em formato especifico em html, teria o titulo do post da mesma.
	public static String getPostTitle( String line ) {
		
		String title = "";
		
		for ( int i = 0; i < line.length(); i++ ) {
			
			if ( line.charAt(i) == '>') {
				title = line.substring(i+1, line.length()-5);
				i = line.length();
			}
			
		}
		
		return title;
	}
	
	// Metodo a ser chamado para executar os dois casos de testes de fronteira descritos de forma automatica.
	public static void testParkingLot() throws IOException {
		
		for ( int i = 0; i < 2; i++ ) {
			
			System.out.print("case = " + (i+1) + " result: ");
			getFormResult(i);
			
		}
		
	}
	
	// Metodo que lida com a requisicao web e interpretacao da resposta para obtermos o resultado dos testes.
	public static void getFormResult ( int i ) throws IOException {
		
		URL url;
		URLConnection connection = null;
		
		String stpRequest = basehttp;
		String parkType = "STP&";
		stpRequest += parkType;
		
		/*
		borderHours[0] = "11%3A59";
		borderHours[1] = "12%3A00";
		borderHours[2] = "00%3A00";
		*/
		
		// i = 0, veremos transicao de AM -> PM do mesmo dia. Para i = 1 veremos transicao de PM -> AM do dia seguinte.
		if ( i == 0 ) {
			stpRequest += "EntryTime=" + borderHours[0] + "&EntryTimeAMPM=AM&EntryDate=8%2F9%2F2017&ExitTime=" + borderHours[1]
					+ "&ExitTimeAMPM=PM&ExitDate=8%2F9%2F2017&action=calculate&Submit=Calculate";
		} else {
			stpRequest += "EntryTime=" + borderHours[0] + "&EntryTimeAMPM=PM&EntryDate=8%2F9%2F2017&ExitTime=" + borderHours[2]
					+ "&ExitTimeAMPM=AM&ExitDate=8%2F10%2F2017&action=calculate&Submit=Calculate";
		}
		
		url = new URL(stpRequest);
		connection = url.openConnection();
		
 		BufferedReader br = new BufferedReader(
               new InputStreamReader(connection.getInputStream()));
	    String inputLine;
	    while ((inputLine = br.readLine()) != null) {
	    	
		    // inputline contains html return line. we need to find the results.
		    if ( inputLine.contains("COST") ) {	
		    	
		    	inputLine = br.readLine(); // Result is here.
		    	inputLine = inputLine.replaceAll("&nbsp;", "");
		    	System.out.println(htmlParser(inputLine));
		    	
		    }
		    	
		    
	    }
	    
	    br.close();
		
	}
	
	// Classe usada para interpretar o html e retornar apenas o valor desejado, que se encontra dentro de uma tag <b></b>
	public static String htmlParser ( String htmlinput ) {
		
		Pattern pattern = Pattern.compile("<b>(.*?)</b>");
		Matcher matcher = pattern.matcher(htmlinput);
		String parsedString = "Cost: ";
		
		while (matcher.find()) {
		    parsedString += matcher.group(1);
		}
		
		return parsedString;
	}
	
	
}

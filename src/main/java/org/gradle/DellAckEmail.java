package org.gradle;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DellAckEmail {
	
	private static final String HOST = "pop3.live.com";
	private static final String USERNAME = "waldenlaker@hotmail.com";
	private static final String PASSWORD = "538552Cyx";
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        System.out.println("Hello World!");
        
        SimpleDateFormat myFormatter = new SimpleDateFormat( "yyyy-MM-dd", Locale.US );
        
        Properties props = System.getProperties();
        props.setProperty( "mail.pop3s.port", "995" );
        
        BufferedWriter myWriter = null;
        try {
             myWriter = new BufferedWriter( new FileWriter( "/home/ethan/Documents/DellAck.csv" ) );
             myWriter.write( "\"PurchaseID\",\"Description\",\"Unit Price\",\"Quantity\",\"Total Price\","
             		+ "\"Estimated Delivery Data\",\"Billing\",\"Shipping\"\n" );
        }
        catch ( IOException myIOE ) {
             myIOE.printStackTrace();
        }
        
        try{
        	Session mySession = Session.getInstance(props, null);
        	Store myStore = mySession.getStore("pop3s");
        	myStore.connect( HOST, 995, USERNAME, PASSWORD );
        	
        	System.out.println( myStore );
        	
        	//List all the folders
        	
        	Folder[] f = myStore.getDefaultFolder().list();
        	for(Folder fd:f)
        	    System.out.println(">> " + fd.getName());
        	
        	
        	Folder inbox = myStore.getFolder("INBOX");
        	inbox.open(Folder.READ_ONLY);
        	//FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
        	Message messages[] = inbox.getMessages();
        	
        	System.out.println( messages.length );
        	
        	for(int i = 0; i < 100; i++){
        		Address addys[] = messages[messages.length - i -1].getFrom();
        		for (Address addy: addys){
        			if( addy.toString().trim().equals("\"Dell Inc.\" <dell_automated_email@dell.com>")){
        				if ( messages[messages.length - i - 1].getSubject().startsWith( "Dell Order Has Been Acknowledged" ) ){
        					String messageSubject =  messages[messages.length - i -1].getSubject();
        					System.out.println(messageSubject);
        					String sentDate = myFormatter.format( messages[messages.length - i -1].getSentDate() );
        					
        					try{
        						//System.out.println(messages[messages.length - i -1].getContent().toString());
        						String messageContent = messages[messages.length - i -1].getContent().toString();
        						Document htmldom = Jsoup.parse(messageContent);
        						List<Element> tables = htmldom.body().select("table[width=600]");
        						List<Element> address = tables.get(4).select("td[align=left]");
        						String purchaseID = messages[messages.length - i -1].getSubject().replaceAll("[^0-9]+", "") ;
        						String shipping = address.get(0).text().replaceAll(",", " ").replaceAll("Shipping Info", "");
        						String billing = address.get(1).text().replaceAll(",", " ").replaceAll("Billing Info", "");
        						
        						List<Element> items = tables.get(5).select("table table table");
        						//System.out.println(items.get(0).text());
        						List<Element> rows = items.get(0).select("tr:has([valign])");
        						for (int m=0; m < rows.size(); m++){
        							if ((m%2) == 1 || m == rows.size() - 1){
        								continue;
        							}
        							List<Element> cells = rows.get(m).select("td");
        							myWriter.append(purchaseID);
    								myWriter.append(COMMA_DELIMITER);
        							for(int x=0; x < cells.size(); x++){
        								myWriter.append(cells.get(x).text());
        								if (x == cells.size() - 1){
        									myWriter.append(COMMA_DELIMITER);
        									myWriter.append(billing);
        									myWriter.append(COMMA_DELIMITER);
        									myWriter.append(shipping);
        									myWriter.append(NEW_LINE_SEPARATOR);
        								} else{
        									myWriter.append(COMMA_DELIMITER);
        								}
        							}
        							
        							
        						}
        						for (Element row: rows){
        							System.out.println(row.text());
        						}
        						
        					}
        					catch ( IOException myIOE ) {
        						myIOE.printStackTrace();
        					}
        				}
        			}
        		}
        	}
        	
        	/*
        	for (Message message:messages){
        		Address addys[] = message.getFrom();
        		for (Address addy: addys){
        			System.out.println(addy.toString());
        			if( addy.toString().trim().equals("\"Dell Inc.\" <dell_automated_email@dell.com>")){
        				if ( message.getSubject().startsWith( "Dell Order" ) ){
        					String messageSubject =  message.getSubject();
        					System.out.println(messageSubject);
        					String sentDate = myFormatter.format( message.getSentDate() );
        					
        					MimeMessage myMimeMessage = (MimeMessage) message;
        					String messageContent = null;
        					//System.out.print(myMimeMessage);
        					
        					try{
        						//Object obj = message.getContent();
        						Multipart mp = (Multipart) myMimeMessage.getContent();
        						for(int i=0;i<mp.getCount();i++) {
        						    BodyPart bodyPart = mp.getBodyPart(i);
        						    if (getText(bodyPart) != null){
        						    	messageContent = getText(bodyPart);
        						    	break;
        						    }    
        						}
        						
        						//System.out.print(messageContent);
        						Document htmldom = Jsoup.parse(messageContent);
        						List<Element> rows = htmldom.select("tr");
        						for(int n=0; n< rows.size(); n++){
        							if (n == 0){
        								continue;
        							}
        							List<Element> cells = rows.get(n).select("td");
        							for(int i=0; i < cells.size(); i++){
        								myWriter.append(cells.get(i).text());
        								if (i == cells.size() - 1){
        									myWriter.append(NEW_LINE_SEPARATOR);
        								} else{
        									myWriter.append(COMMA_DELIMITER);
        								}
        								
        							}
        							
        				        }
        						
        						String messageContentLines[] = messageContent.split("\n");
        						System.out.println(messageContentLines.length);
        						
        						//for (String line: messageContentLines){
        						//	System.out.println(line);
        						//	System.out.println("");
        						//}
        					}
        					
        					catch ( IOException myIOE ) {
        						myIOE.printStackTrace();
        					}
        				}
        			}
        		}
        		
        		
        	} 
        	*/
        }
        catch ( NoSuchProviderException e ) {
        	e.printStackTrace();
        }
        catch ( MessagingException e ) {
            e.printStackTrace();
        }
        
        try {
           myWriter.close();
        }
        catch ( IOException myIOE ) {
           myIOE.printStackTrace();
        }
        
	}
	
	private static String getText(Part p) throws MessagingException, IOException {
		if (p.isMimeType("text/*")) {
			String s = (String)p.getContent();
			//textIsHtml = p.isMimeType("text/html");
			return s;
		}

		if (p.isMimeType("multipart/alternative")) {
			// prefer html text over plain text
			Multipart mp = (Multipart) p.getContent();
			String text = null;
			for (int i = 0; i < mp.getCount(); i++) {
				Part bp = mp.getBodyPart(i);
				if (bp.isMimeType("text/plain")) {
					if (text == null)
						text = getText(bp);
					continue;
				} else if (bp.isMimeType("text/html")) {
					String s = getText(bp);
					if (s != null)
						return s;
				} else {
					return getText(bp);
				}
			}
			return text;
		} else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart)p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				String s = getText(mp.getBodyPart(i));
				if (s != null)
					return s;
			}
		}

		return null;
	}

}

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
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DellAckEmail {
	
	private static final String HOST = "imap.gmail.com";
	private static final String USERNAME = "waldenlaker";
	private static final String PASSWORD = "538552Cyx";
	private static final String COMMA_DELIMITER = ";";
	private static final String NEW_LINE_SEPARATOR = "\n";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        System.out.println("Hello World!");
        
        SimpleDateFormat myFormatter = new SimpleDateFormat( "yyyy-MM-dd", Locale.US );
        
        Properties props = System.getProperties();
        props.setProperty( "mail.store.protocol", "imaps" );
        
        BufferedWriter myWriter = null;
        try {
             myWriter = new BufferedWriter( new FileWriter( "/home/ethan/Documents/DellAck.csv" ) );
             myWriter.write( "\"Product Name\";\"Quantity\";\"Price\";\"Account\"\n" );
        }
        catch ( IOException myIOE ) {
             myIOE.printStackTrace();
        }
        
        try{
        	Session mySession = Session.getDefaultInstance( props, null );
        	Store myStore = mySession.getStore("imaps");
        	myStore.connect( HOST, USERNAME, PASSWORD );
        	
        	System.out.println( myStore );
        	
        	//List all the folders
        	/*
        	Folder[] f = myStore.getDefaultFolder().list();
        	for(Folder fd:f)
        	    System.out.println(">> " + fd.getName());
        	*/
        	
        	Folder inbox = myStore.getFolder("Test");
        	inbox.open(Folder.READ_ONLY);
        	Message messages[] = inbox.getMessages();
        	System.out.println( messages.length );
        	
        	for (Message message:messages){
        		Address addys[] = message.getFrom();
        		for (Address addy: addys){
        			System.out.println(addy.toString());
        			if( addy.toString().trim().equals("\"r3000ebiz.com\" <admin@r3000ebiz.com>")){
        				if ( message.getSubject().startsWith( "Pay Mail" ) ){
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

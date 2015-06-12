package org.gradle;

import java.util.List;
import java.util.Properties;
import java.util.Date;
import java.util.Locale;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.NoSuchProviderException;
import javax.mail.Address;
import javax.mail.internet.MimeMessage;

import java.text.SimpleDateFormat;

public class DellAckEmail {
	
	private static final String HOST = "imap.gmail.com";
	private static final String USERNAME = "waldenlaker";
	private static final String PASSWORD = "538552Cyx";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        System.out.println("Hello World!");
        
        SimpleDateFormat myFormatter = new SimpleDateFormat( "yyyy-MM-dd", Locale.US );
        
        Properties props = System.getProperties();
        props.setProperty( "mail.store.protocol", "imaps" );
        
        BufferedWriter myWriter = null;
        try {
             myWriter = new BufferedWriter( new FileWriter( "/home/ethan/Documents/DellAck.csv" ) );
             myWriter.write( "\"Sent Date\",\"Subject\",\"From\",\"E-Mail\",\"Phone Number\",\"Skills & Interests\",\"Comments\"\n" );
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
        					//System.out.print(myMimeMessage);
        					
        					try{
        						//Object obj = message.getContent();
        						Multipart mp = (Multipart) message.getContent();
        						for(int i=0;i<mp.getCount();i++) {
        							System.out.println("test1");
        						    BodyPart bodyPart = mp.getBodyPart(i);
        						    if ("text/html".equals(bodyPart.getContentType())) {
        						    	System.out.println("test2");
        						        String s = (String) bodyPart.getContent();
        						        System.out.println(s);
        						    }
        						}
        						
        						
        						//String messageContent = (String) myMimeMessage.getContent();
        						//String messageContentLines[] = messageContent.split("\n");
        						
        						//for (String line: messageContentLines){
        						//	System.out.println(line);
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
        
	}

}

package org.gradle;

import java.util.List;
import java.util.Properties;
import java.util.Date;
import java.util.Locale;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
        
	}

}

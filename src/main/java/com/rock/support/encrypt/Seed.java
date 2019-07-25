package com.rock.support.encrypt;

import java.io.UnsupportedEncodingException;

import kr.cipher.seed.Seed128Cipher; 

public class Seed {  

    // salt for lotte
	private static final String SEED_SALT = "-change-salt-password-for-xxxxx-com-";
    
	public static String enc_word(String word)
	{ 
		String enc_word = null;
		
		// attach salt 
		word = SEED_SALT + word ;
		
		byte[] key = new byte[32];
		for( int i = 0; i < key.length; i++ ) {
			key[i] = (byte)i;
		}
		
		try {
			enc_word = Seed128Cipher.encrypt(word, key, null);
		} catch (UnsupportedEncodingException e) { 
			e.printStackTrace();
		}

		return enc_word;
	}
	
	public static String dec_word(String word) throws Exception
	{ 
		String dec_word = null;
		
		if(word.length() < 32){
			throw new Exception("Wrong Decryption Password");
	    }
		
		byte[] key = new byte[32];
		for( int i = 0; i < key.length; i++ ) {
			key[i] = (byte)i;
		}
		
		try {
			dec_word = Seed128Cipher.decrypt(word, key, null);
			
			if(dec_word.length() > SEED_SALT.length()) {
				// remove salt 
				dec_word = dec_word.substring(SEED_SALT.length());
			} else {
				dec_word = "";
			}
			
		} catch (UnsupportedEncodingException e) { 
			e.printStackTrace();
		}

		return dec_word;
	}
	
	public static void main(String args[]){

		try{ 
			String enc_word = Seed.enc_word(args[0]);

			System.out.println("source word :" + args[0]);
			System.out.println("encrypt :" + enc_word);
			
			//String dec_word = Seed.dec_word(enc_word);
			//System.out.println("decript :" + dec_word);
			
			//System.out.println("==================" + dec_word); 
		}catch (Exception e) { 
			//System.out.println("Usage: java -cp kr.cipher-1.0.jar:tomcat85-dbcp-encrypt-0.0.1-SNAPSHOT.jar com.rock.support.tomcat.dbcp.security.Seed <password>");
			System.out.println("Usage: ./seed_encryption.sh <password>");
			//e.printStackTrace();
		}

	}
	
}

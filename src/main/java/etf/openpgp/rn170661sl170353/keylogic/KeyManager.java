package etf.openpgp.rn170661sl170353.keylogic;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

public class KeyManager {
	
    private PGPPublicKeyRingCollection publicKeyRingCollection;
    private PGPSecretKeyRingCollection secretKeyRingCollection;
    
    
    public PGPPublicKeyRingCollection getPublicKeyRingCollection() {
		return publicKeyRingCollection;
	}

	public PGPSecretKeyRingCollection getSecretKeyRingCollection() {
		return secretKeyRingCollection;
	}


	private static KeyManager keyManager;
    
    private KeyManager()
    {
    	
    }
    
    public static KeyManager getInstance()
    {
    	if(keyManager != null)
    		return keyManager;
    	else
    	{
    		keyManager = new KeyManager();
    		return keyManager;
    	}
    }
    
    
    /**
    *
    * @param name
    * @param email
    * @param passPhrase
    * @param dsaKeySize
    * @param elgamalKeySize
    */
   public void generateDSAElgamalKeyPair(
           String name,
           String email,
           char[] passPhrase,
           int dsaKeySize,
           int elgamalKeySize
   )
   {
       try
       {
           //DSA KeyPairGenerator and KeyPair
           KeyPairGenerator dsaKpg = KeyPairGenerator.getInstance("DSA", "BC");
           dsaKpg.initialize(dsaKeySize);
           KeyPair dsaKp = dsaKpg.generateKeyPair();

           //ElGamal KeyPairGenerator and KeyPair error if ElGamal keysize 4096
           KeyPairGenerator elgKpg = KeyPairGenerator.getInstance("ELGAMAL", "BC");
           elgKpg.initialize(elgamalKeySize);
           KeyPair elgKp = elgKpg.generateKeyPair();
           //PGPKeyPair's and KeyRing
           PGPKeyPair dsaKeyPair = new JcaPGPKeyPair(PGPPublicKey.DSA, dsaKp, new Date());
           PGPKeyPair elgKeyPair = new JcaPGPKeyPair(PGPPublicKey.ELGAMAL_ENCRYPT, elgKp, new Date());
           PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
           PGPKeyRingGenerator keyRingGen = new PGPKeyRingGenerator(
                   PGPSignature.POSITIVE_CERTIFICATION,
                   dsaKeyPair,
                   name + " <" + email + ">",
                   sha1Calc,
                   null,
                   null,
                   new JcaPGPContentSignerBuilder(
                           dsaKeyPair.getPublicKey().getAlgorithm(),
                           HashAlgorithmTags.SHA1
                   ),
                   new JcePBESecretKeyEncryptorBuilder(
                           PGPEncryptedData.AES_256,
                           sha1Calc
                   ).setProvider("BC").build(passPhrase)
           );
           keyRingGen.addSubKey(elgKeyPair);
//           PGPSecretKey pgpSecretKey = keyRingGen.generateSecretKeyRing().getSecretKey();
//           PGPPublicKey pgpPublicKey = keyRingGen.generatePublicKeyRing().getPublicKey();
           
           PGPSecretKeyRing pgpSecretKeyRing = keyRingGen.generateSecretKeyRing();
           PGPPublicKeyRing pgpPublicKeyRing = keyRingGen.generatePublicKeyRing();
           //Izvezemo u .asc fajlove
           OutputStream secretOut = new ArmoredOutputStream(
                   new FileOutputStream("secret-keys/" + Long.toHexString(pgpSecretKeyRing.getSecretKey().getKeyID()).toUpperCase() + ".asc")
           );
           OutputStream publicOut = new ArmoredOutputStream(
                   new FileOutputStream("public-keys/" + Long.toHexString(pgpSecretKeyRing.getSecretKey().getKeyID()).toUpperCase() + ".asc")
           );

           pgpSecretKeyRing.encode(secretOut);
           secretOut.close();
           pgpPublicKeyRing.encode(publicOut);
           publicOut.close();
           
           this.secretKeyRingCollection = PGPSecretKeyRingCollection.addSecretKeyRing(secretKeyRingCollection, pgpSecretKeyRing);
           this.publicKeyRingCollection = PGPPublicKeyRingCollection.addPublicKeyRing(publicKeyRingCollection, pgpPublicKeyRing);
           
       }
       catch(Exception e)
       {
           e.printStackTrace();
       }
   }
    
    public void initPublicKeyRingCollection()
    {
    	List<PGPPublicKeyRing> pgpPublicKeyRingList = new ArrayList<>();
    	try 
    	{
    			InputStream inputStream;
    			File publicKeysFolder = new File("./public-keys");
    			for (final File fileEntry : publicKeysFolder.listFiles()) 
    			{
			        if (!fileEntry.isDirectory()) 
			        {
			        	//TODO: read only files with .asc extension
			        	inputStream = new BufferedInputStream(
			        			new FileInputStream(fileEntry)
	        			);
			        	
			        	pgpPublicKeyRingList.add(
			        			new PGPPublicKeyRing(
			        					PGPUtil.getDecoderStream(inputStream),
			        					new JcaKeyFingerprintCalculator()
			        			)
	        			);
			        	
			        	inputStream.close();
			        	 		         
			        }
    			}
    			
    			this.publicKeyRingCollection = new PGPPublicKeyRingCollection(pgpPublicKeyRingList);
    			System.out.println(this.publicKeyRingCollection);
    		
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    
    public void initSecretKeyRingCollection()
    {
    	List<PGPSecretKeyRing> pgpSecretKeyRingList = new ArrayList<>();
    	try 
    	{
    			InputStream inputStream;
    			File secretKeysFolder = new File("./secret-keys");
    			for (final File fileEntry : secretKeysFolder.listFiles()) 
    			{
			        if (!fileEntry.isDirectory()) 
			        {
			        	//TODO: read only files with .asc extension
			        	inputStream = new BufferedInputStream(
			        			new FileInputStream(fileEntry)
	        			);
			        	
			        	pgpSecretKeyRingList.add(
			        			new PGPSecretKeyRing(
			        					PGPUtil.getDecoderStream(inputStream),
			        					new JcaKeyFingerprintCalculator()
			        			)
	        			);
			        	
			        	inputStream.close();
			        	 		         
			        }
    			}
    			
    			this.secretKeyRingCollection = new PGPSecretKeyRingCollection(pgpSecretKeyRingList);
    			System.out.println(this.secretKeyRingCollection);
    		
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}    	
    	
    }

	public PGPSecretKey readSecretKeyFromFile(String filename) {
		
		try (InputStream inputStream = new BufferedInputStream(new FileInputStream("secret-keys/" + filename + ".asc"))) {
		    PGPSecretKeyRingCollection pgpSecretKeyRingCollection = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(inputStream), new JcaKeyFingerprintCalculator());
		    Iterator<PGPSecretKeyRing> keyRingIterator = pgpSecretKeyRingCollection.getKeyRings();
		    while (keyRingIterator.hasNext()) {
		        PGPSecretKeyRing keyRing =  keyRingIterator.next();
		        Iterator<PGPSecretKey> keyIterator = keyRing.getSecretKeys();
		        while (keyIterator.hasNext()) {
		            PGPSecretKey key =  keyIterator.next();
		            if (key.isSigningKey()) {
		                return key;
		            }
		        }
		    }
		}  catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public PGPPublicKey readPublicKeyFromFile(String filename) {
		
		try (InputStream inputStream = new BufferedInputStream(new FileInputStream("public-keys/" + filename + ".asc"))) {
		    PGPPublicKeyRingCollection pgpPublicKeyRingCollection = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(inputStream), new JcaKeyFingerprintCalculator());
		    Iterator<PGPPublicKeyRing> keyRingIterator = pgpPublicKeyRingCollection.getKeyRings();
		    while (keyRingIterator.hasNext()) {
		    	PGPPublicKeyRing keyRing =  keyRingIterator.next();
		        Iterator<PGPPublicKey> keyIterator = keyRing.getPublicKeys();
		        while (keyIterator.hasNext()) {
		            PGPPublicKey key =  keyIterator.next();
		            if (key.isEncryptionKey()) {
		                return key;
		            }
		        }
		    }
		}  catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public void importPublicKeyFromFile(File publicKeyFile) throws Exception
	{
		InputStream inputStream = new BufferedInputStream(new FileInputStream(publicKeyFile.getAbsolutePath()));
		PGPPublicKeyRing pgpPublicKeyRing = new PGPPublicKeyRing(PGPUtil.getDecoderStream(inputStream), new JcaKeyFingerprintCalculator());
        this.publicKeyRingCollection = PGPPublicKeyRingCollection.addPublicKeyRing(publicKeyRingCollection, pgpPublicKeyRing);
        
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream("./public-keys/" + publicKeyFile.getName()));
        
        inputStream.reset();
        
        byte[] buffer = new byte[1024];
        int lengthRead;
        while ((lengthRead = inputStream.read(buffer)) > 0) {
        	outputStream.write(buffer, 0, lengthRead);
        	outputStream.flush();
        }
        
        inputStream.close();
        outputStream.close(); 
	}

	
	public void importSecretKeyFromFile(File secretKeyFile) throws Exception
	{
		InputStream inputStream = new BufferedInputStream(new FileInputStream(secretKeyFile.getAbsolutePath()));
		PGPSecretKeyRing pgpSecretKeyRing = new PGPSecretKeyRing(PGPUtil.getDecoderStream(inputStream), new JcaKeyFingerprintCalculator());
        this.secretKeyRingCollection = PGPSecretKeyRingCollection.addSecretKeyRing(secretKeyRingCollection, pgpSecretKeyRing);
        

        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream("./secret-keys/" + secretKeyFile.getName()));
        
        inputStream.reset();
        
        byte[] buffer = new byte[1024];
        int lengthRead;
        while ((lengthRead = inputStream.read(buffer)) > 0) {
        	outputStream.write(buffer, 0, lengthRead);
        	outputStream.flush();
        }
        
        inputStream.close();
        outputStream.close(); 
	}

	
	
	public void removePublicKey(String publicKeyId) throws Exception
	{
		
		this.publicKeyRingCollection = PGPPublicKeyRingCollection.removePublicKeyRing(
				this.publicKeyRingCollection, 
				this.publicKeyRingCollection.getPublicKeyRing(Long.parseUnsignedLong(publicKeyId, 16))
		);
		
	}

	public void removeSecretKey(String secretKeyId) throws Exception{
		this.secretKeyRingCollection = PGPSecretKeyRingCollection.removeSecretKeyRing(
				this.secretKeyRingCollection, 
				this.secretKeyRingCollection.getSecretKeyRing(Long.parseUnsignedLong(secretKeyId, 16))
		);	
	}
    
    
    
	

}

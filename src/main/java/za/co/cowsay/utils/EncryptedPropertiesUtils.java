package za.co.cowsay.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.EncryptedProperties;
import org.owasp.esapi.Logger;
import org.owasp.esapi.crypto.CipherText;
import org.owasp.esapi.crypto.CryptoHelper;
import org.owasp.esapi.crypto.PlainText;
import org.owasp.esapi.errors.EncryptionException;

public final class EncryptedPropertiesUtils implements EncryptedProperties{
	
	private static final Logger LOGGER = ESAPI.getLogger(EncryptedPropertiesUtils.class);
	private final Properties properties = new Properties();

	public synchronized String getProperty(String key ) throws EncryptionException {
            String serializedBytes = properties.getProperty(key);
            
            if(serializedBytes == null) {
                    return null;
            }

            CipherText cipherText = CipherText.fromPortableSerializedBytes(serializedBytes.getBytes());
            PlainText plainText = ESAPI.encryptor().decrypt(cipherText);
            return plainText.toString();
	}

	public Set keySet() {
		return properties.keySet();

	}

	public void load(InputStream in) throws IOException {
		properties.load(in);
        LOGGER.trace(Logger.SECURITY_SUCCESS, "Encrypted properties loaded successfully");
	}

	public String setProperty(String key, String value) {

        if ( key == null ) {
            throw new NullPointerException("Property name may not be null.");
        }
        if ( value == null ) {
            throw new NullPointerException("Property value may not be null.");
        }
        
        try {
        	PlainText plain = new PlainText(value);
        	CipherText cipherText = ESAPI.encryptor().encrypt(plain);
			return (String)properties.setProperty(key, new String(cipherText.asPortableSerializedByteArray()));
		} catch (EncryptionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;

	}

	public void store(OutputStream out, String comments) throws IOException {
		properties.store(out, comments);
	}

	public void encryptExistingProperties(String existingPropertiesFileName) {
		
	}
	
	/**
	 * Takes the path to an existing file and creates new properties file at 'encryptedPropertiesFileName' path.
	 * The 'comments' argument adds a comment at the top of the new properties file
	 * @param existingPropertiesFileName
	 * @param encryptedPropertiesFileName
	 * @param comments
	 */
	public void createNewEncryptedPropertiesFromExisting(String existingPropertiesFileName, String encryptedPropertiesFileName, String comments) {
		try {
		Properties props = new Properties();
		props.load(new FileInputStream(new File(existingPropertiesFileName)));
				
		for (Entry<Object, Object> property: props.entrySet()) {
			this.setProperty((String) property.getKey(), (String)property.getValue());
		}
		
		FileOutputStream outputStream = new FileOutputStream(new File(encryptedPropertiesFileName));
		this.store(outputStream, comments);
		
		outputStream.flush();
		outputStream.close();
		outputStream = null;
		
		} catch (IOException fne) {
			LOGGER.error(Logger.EVENT_FAILURE, "Could not create new Encryption file from Existing properties: " + fne);
		} 
	}
}

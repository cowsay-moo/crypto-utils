package za.co.cowsay.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EncryptedPropertiesUtilsTest {

	private EncryptedPropertiesUtils propertiesUtils = null;

	@Before
	public void setup() throws FileNotFoundException, IOException {
	}

	@Test
	public void encryptExistingUnencryptedProperties() throws FileNotFoundException, IOException {

		String existingPropertiesFilename = "./src/test/resources/application.properties";
		String encryptedPropertiesFileName = "./src/test/resources/application_encrypted.properties";
		String comments = "Encrypted Application Properties File";

		// Do the call to create a new properties file where the values were
		// encrypted
		EncryptedPropertiesUtils encryptedProps = new EncryptedPropertiesUtils();
		encryptedProps.createNewEncryptedPropertiesFromExisting(existingPropertiesFilename, encryptedPropertiesFileName, comments);

		// Now re-read the properties file to check if the properties were
		// correctly encrypted and serialized to file
		Properties encryptedProperties = new Properties();
		encryptedProps.load(new FileInputStream(new File(encryptedPropertiesFileName)));

		Properties props = new Properties();
		props.load(new FileInputStream(new File(existingPropertiesFilename)));

		for (Entry<Object, Object> property : props.entrySet()) {
			for (Entry<Object, Object> encryptedProperty : encryptedProperties.entrySet()) {
				Assert.assertEquals(property.getKey(), encryptedProperty.getKey());
				Assert.assertNotEquals(property.getValue(), encryptedProperty.getValue());
			}
		}
	}

	@After
	public void deleteTheEncryptedPropertiesFile() {
		File file = new File("./src/test/resources/application_encrypted.properties");

		if (file.exists()) {
			if (file.delete()) {
				System.out.println("Deleted temperorary resources in: \"src/test/resources\"");
			}
		}
	}
}

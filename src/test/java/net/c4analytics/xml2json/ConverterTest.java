package net.c4analytics.xml2json;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.c4analytics.xml2json.utils.Util;

/**
 * @author Steve Carton (steve.carton@smartlogic.com)
 *
 * April 8, 2019
 */
public class ConverterTest {
	final static Logger logger = LoggerFactory.getLogger(ConverterTest.class);

	private File getClassPathResource(String n) throws URISyntaxException {
		URL url = ClassLoader.getSystemResource(n);
		File file = new File(url.toURI());
		return file;
	}
	/**
	 * Test
	 * @throws IOException
	 * @throws URISyntaxException 
	 * @throws ClassificationException 
	 */
	@Test
	public void testConvertBasicContent() throws IOException, URISyntaxException {
	    TestRunner runner = TestRunners.newTestRunner(new XmlToJson());
	    InputStream content = new ByteArrayInputStream(Files.readAllBytes(getClassPathResource("sample.xml").toPath()));
	    runner.enqueue(content);
	    try {
	    	runner.run();
	    } catch (Exception e) {
	    	logger.debug(Util.stackTrace(e));
	    }
	    runner.assertQueueEmpty();
	    List<MockFlowFile> results = runner.getFlowFilesForRelationship(XmlToJson.SUCCESS);
	    assertTrue("1 match", results.size() == 1);
	    MockFlowFile result = results.get(0);
	    byte[] xmlb = runner.getContentAsByteArray(result);
	    assert xmlb != null;
	    JSONObject jo = new JSONObject(new String(xmlb));
	    assert jo != null;
	}
}

package com.livefyre.streamhub_android_sdk.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;
import com.livefyre.streamhub_android_sdk.*;

public class ClientTest extends InstrumentationTestCase {
	private String articleId = "mockArticleId";
	private String siteId = "mockSiteId"; 
	private String collectionId = "mockCollId"; 
	private String networkDomain = "mockDomain";
	private String userToken = "mocklftoken";
	private String environment = null;
	private static MockURLStreamHandler mockHandler = null;
	
	public ClientTest() {
		super();
	}
	
	@Override
	public void setUp() {
		if (mockHandler == null) {
		    MockURLStreamHandler handler = new MockURLStreamHandler();
		    System.out.println("Setting URL mock factory");
		    URL.setURLStreamHandlerFactory(handler);
		    mockHandler = handler;
		}
	}
	
	@Override
	public void tearDown() {
		//
	}
	
	public void testBootstrapGetInit() {
		URL initEndpoint = null;
		try {
			initEndpoint = BootstrapClient.generateInitEndpoint(articleId, siteId, networkDomain, environment);
		} catch (UnsupportedEncodingException e) {
			fail("UnsupportedEncodingException");
		} catch (MalformedURLException e) {
			fail("MalformedURLException");
		}
		
		JSONObject data = null;
		try {
			data = GETJSON.fetchData(initEndpoint);
		} catch (IOException e) {
			fail("IOException");
		} catch (JSONException e) {
			fail("JSONException");
		}
		assertNotNull(data);
	}
	
	public void testAdminClientWithCollId() {
		URL authEndpoint = null;
		try {
			authEndpoint = AdminClient.generateAuthEndpoint(userToken, collectionId, articleId, siteId, networkDomain);
		} catch (UnsupportedEncodingException e) {
			fail("UnsupportedEncodingException");
		} catch (MalformedURLException e) {
			fail("MalformedURLException");
		}
		
		JSONObject data = null;
		try {
			data = GETJSON.fetchData(authEndpoint);
		} catch (IOException e) {
			fail("IOException");
		} catch (JSONException e) {
			fail("JSONException");
		}
		assertNotNull(data);
	}
	
	public void testAdminClientWithoutCollId() {
		URL authEndpoint = null;
		try {
			authEndpoint = AdminClient.generateAuthEndpoint(userToken, null, articleId, siteId, networkDomain);
		} catch (UnsupportedEncodingException e) {
			fail("UnsupportedEncodingException");
		} catch (MalformedURLException e) {
			fail("MalformedURLException");
		}
		
		JSONObject data = null;
		try {
			data = GETJSON.fetchData(authEndpoint);
		} catch (IOException e) {
			fail("IOException");
		} catch (JSONException e) {
			fail("JSONException");
		}
		assertNotNull(data);
	}
	
	public void testPublicAPIClientUserContent() {
		URL userContentEndpoint = null;
		try {
			userContentEndpoint = PublicAPIClient.generateUserContentEndpoint("mockUserId", userToken, networkDomain, null, null);
		} catch (MalformedURLException e) {
			fail("MalformedURLException");
		}
		
		JSONObject data = null;
		try {
			data = GETJSON.fetchData(userContentEndpoint);
		} catch (IOException e) {
			fail("IOException");
		} catch (JSONException e) {
			fail("JSONException");
		}
		assertNotNull(data);
	}
	
	public void testPublicAPIClientHotness() {
		URL hotnessEndpoint = null;
		try {
			hotnessEndpoint = PublicAPIClient.generateTrendingCollectionsEndpoint("mockTag", null, networkDomain, 22);
		} catch (MalformedURLException e) {
			fail("MalformedURLException");
		}
		
		JSONObject data = null;
		try {
			data = GETJSON.fetchData(hotnessEndpoint);
		} catch (IOException e) {
			fail("IOException");
		} catch (JSONException e) {
			fail("JSONException");
		}
		assertNotNull(data);
	}
	
	// TODO WriteClient not tested. StreamClient not tested.
	
	// This is silly to nest the classes like this, but easiest way to load the resources we want.
	public class MockURLStreamHandler extends URLStreamHandler implements URLStreamHandlerFactory {
		public Resources res;

		@Override
		protected URLConnection openConnection(URL inurl) throws IOException {
			System.out.println("Mocking opening a connection.");
			MockHttpURLConnection mockConnection = new MockHttpURLConnection(inurl);
	        return mockConnection;
		}

		@Override
		public URLStreamHandler createURLStreamHandler(String protocol) {
			// ???
			return this;
		}
	}
	
	public class MockHttpURLConnection extends HttpURLConnection {
		private Integer mockFile;
			
		protected MockHttpURLConnection(URL inurl) {
			super(inurl);
			System.out.println("Mocking the Url...");
			String inString = inurl.toString();
			System.out.println(inString);
			
			// AdminClient
			if (inString.equals("http://admin.mockDomain/api/v3.0/auth/?collectionId=mockCollId&lftoken=mocklftoken")) {
				System.out.println("Setting mock to auth sample");
	    		setMockFile(R.raw.auth_sample);
			}
			if (inString.equals("http://admin.mockDomain/api/v3.0/auth/?siteId=mockSiteId&articleId=bW9ja0FydGljbGVJZA%3D%3D&lftoken=mocklftoken")) {
				System.out.println("Setting mock to auth sample");
	    		setMockFile(R.raw.auth_sample);
			}
			// BootstrapClient
			if (inString.equals("http://bootstrap.mockDomain/bs3/mockDomain/mockSiteId/bW9ja0FydGljbGVJZA%3D%3D/init")) {
				System.out.println("Setting mock to bootstrap sample");
	    		setMockFile(R.raw.init_sample);
			}
	    	// Public API Client
			if (inString.equals("http://bootstrap.mockDomain/api/v3.0/hottest/?tag=mockTag&number=22")) {
				System.out.println("Setting mock to hotness sample");
	    		setMockFile(R.raw.hottest_sample);
			}
			if (inString.equals("http://bootstrap.mockDomain/api/v3.0/author/mockUserId/comments/?lftoken=mocklftoken")) {
				System.out.println("Setting mock to user content sample");
	    		setMockFile(R.raw.usercontent_sample);
			}
			// Write Client
	    		
			return;
		}
	  
		@Override
		public InputStream getInputStream() throws IOException {
	 		System.out.println("Loading the mock file...");
			System.out.println(mockFile.toString());
		    System.out.println("Setting mock resources");
	        Resources res = getInstrumentation().getContext().getResources();
		    System.out.println("Opening mock resources");
			return res.openRawResource(mockFile);
		}
	 
		@Override
		public void connect() throws IOException {
		}
	 
		@Override
		public void disconnect() {
		}
	 
		@Override
		public boolean usingProxy() {
			return false;
		}

		public Integer getMockFile() {
			return mockFile;
		}

		public void setMockFile(Integer mockFile) {
			this.mockFile = mockFile;
		}
	}
}

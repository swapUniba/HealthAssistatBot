package com.triage.logic;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.triage.rest.models.tagmeApi.TagMeResult;

public class TagMeApi {
	private String text;
	private String language;
	private TagMeResult result;
	
	public TagMeApi(String text, String lang) {
		this.text = text;
		if(lang != null)
			this.language = lang;
		else
			this.language = "it";
		
		try {
			this.result = call();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public TagMeApi(String text) {
		this.text = text;
		this.language="it";
		
		try {
			this.result = call();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	private final String URL = "https://tagme.d4science.org/tagme/tag";
	private final String APIKEY = "d4e9c3dd-7ab5-4704-be9b-416df4c533d0-843339462";
	public TagMeResult call() throws NoSuchAlgorithmException{
		ClientConfig config = new DefaultClientConfig();
		config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(new HostnameVerifier() {
			
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		}, getSSLContext()));
		
		Client client = Client.create(config);
		WebResource service = client
				.resource(UriBuilder.fromUri(URL).build())
				.queryParam("lang", this.language)
				.queryParam("gcube-token", APIKEY)
				.queryParam("text", this.text);
		String json = service.accept(MediaType.APPLICATION_JSON).get(String.class);
		
		TagMeResult result = new Gson().fromJson(json, TagMeResult.class);
		return result;
	}
	
	public String getText() {
		return text;
	}

	public TagMeResult getResult() {
		return result;
	}
	
	private SSLContext getSSLContext() {
        javax.net.ssl.TrustManager x509 = new javax.net.ssl.X509TrustManager() {

            //@Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
                return;
            }

            //@Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
                return;
            }

            //@Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("SSL");
            ctx.init(null, new javax.net.ssl.TrustManager[]{x509}, null);
        } catch (java.security.GeneralSecurityException ex) {
        }
        return ctx;
    }
}

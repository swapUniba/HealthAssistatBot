package com.triage.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.*;
import com.sun.jersey.multipart.file.FileDataBodyPart;

import static com.sun.javafx.PlatformUtil.isWindows;

public class OCRApi {

	private String url = "http://0.0.0.0:10147";
	//private static String URL = "http://90.147.102.235:16002";
	private String path_image = "C:/Users/frank/TriageBotRestServer-data/TrackingUpload/";
	//public static String PATH_IMAGE ="/home/baccaro/TriageBotRestServer-data/TrackingUpload/";
	public OCRApi(){
        if(!System.getProperty("os.name").equals("Windows 8.1")) {
            path_image = "/home/baccaro/TriageBotRestServer-data/TrackingUpload/";
            url = "http://90.147.102.235:16000";
        }
		/*this.inputStream = inputStream;
		this.fileName = fileName;*/
		/*try {
			ClientResponse clientResponse=call();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}*/
	}

	public String getUrl() {
		return url;
	}

	public String getPath_image() {
		return path_image;
	}

	//Get results from Server OCR of a specified image
	public String get_result(String photo_id){
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("imageid", photo_id);
		Client client=Client.create(new DefaultClientConfig());
		WebResource	service=client.resource(this.url+"/getResult");
		ClientResponse clientresponse=  service.queryParams(queryParams).get(ClientResponse.class);
		if (clientresponse.getStatus()==200){
			return clientresponse.getEntity(String.class);
		}
		return null;
	}

	//Sends a file to Server OCR
	public ClientResponse send_file(String fileName,int photo_id,int userid,int trackingid) {
		//String PATH_IMAGE = "C:/Users/frank/TriageBotRestServer-data/TrackingUpload/";
		//PATH_IMAGE="/home/baccaro/TriageBotRestServer-data/TrackingUpload/";
		//writeToFile(inputStream, PATH_IMAGE + fileName);
		//prepara il file da inviare al server OCR
		File imageFile = new File(this.path_image + fileName);
		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		FileDataBodyPart filePart = new FileDataBodyPart("file", imageFile);
		formDataMultiPart.bodyPart(filePart);
		formDataMultiPart.field("photo_id",String.valueOf(photo_id));
		formDataMultiPart.field("user_id",String.valueOf(userid));
		formDataMultiPart.field("tracking_id",String.valueOf(trackingid));

		Client client=null;
		client=Client.create(new DefaultClientConfig());
		WebResource	service=client.resource(this.url+"/ocr");
		WebResource.Builder builder = service.accept(MediaType.MULTIPART_FORM_DATA);
		builder.type(MediaType.MULTIPART_FORM_DATA);
		ClientResponse clientresponse=builder.post(ClientResponse.class,formDataMultiPart);
		client.destroy();
		imageFile.deleteOnExit();
		imageFile.delete();
		return clientresponse;
	}

	
	// Salva su file l'immagine ricevuta dal Client
	public void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
		try {
			int read = 0;
			byte[] bytes = new byte[1024];

			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}


}

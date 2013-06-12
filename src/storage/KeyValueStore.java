package storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import db.Config;

public class KeyValueStore {
	private AmazonS3 s3;
	private final String bucketName;
	private boolean debugEnabled = true;

	public KeyValueStore() {
		s3 = new AmazonS3Client(
				new ClasspathPropertiesFileCredentialsProvider());
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		s3.setRegion(usWest2);

    this.bucketName = Config.bucketName;
	}

	public KeyValueStore(String bucketName) {
		s3 = new AmazonS3Client(
				new ClasspathPropertiesFileCredentialsProvider());
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		s3.setRegion(usWest2);

    this.bucketName = bucketName;
	}

  public void createBucket() {
		debugPrint("Creating bucket " + bucketName + "\n");
		try {
			s3.createBucket(bucketName);
		} catch (AmazonServiceException ase) {
			printASErrMsg(ase);
		} catch (AmazonClientException ace) {
			printACEerrMsg(ace);
		}
  }
	
	public void deleteBucket(){
		debugPrint("Deleting an object:" + bucketName + "\n");
		try {
			s3.deleteBucket(bucketName);
		} catch (AmazonServiceException ase) {
			printASErrMsg(ase);
		} catch (AmazonClientException ace) {
			printACEerrMsg(ace);
		}
	}

	public void put(String key, File file) {
		debugPrint("Uploading a new object to S3 from a file\n");
		try {
			s3.putObject(new PutObjectRequest(bucketName, key, file));
		} catch (AmazonServiceException ase) {
			printASErrMsg(ase);
		} catch (AmazonClientException ace) {
			printACEerrMsg(ace);
		}
	}
	
	public void put(String key, String redirectLocation) {
		debugPrint("Uploading a new object to S3 from a file\n");
		try {
			s3.putObject(new PutObjectRequest(bucketName, key, redirectLocation));
		} catch (AmazonServiceException ase) {
			printASErrMsg(ase);
		} catch (AmazonClientException ace) {
			printACEerrMsg(ace);
		}
	}
	
	public void put(String key, InputStream input, ObjectMetadata metadata){
		debugPrint("Uploading a new object to S3 from a file\n");
		try {
			s3.putObject(new PutObjectRequest(bucketName, key, input, metadata));
		} catch (AmazonServiceException ase) {
			printASErrMsg(ase);
		} catch (AmazonClientException ace) {
			printACEerrMsg(ace);
		}
	}
	
	public void deleteKey(String key){
		try {
			s3.deleteObject(bucketName, key);
		} catch (AmazonServiceException ase) {
			printASErrMsg(ase);
		} catch (AmazonClientException ace) {
			printACEerrMsg(ace);
		}
	}
	
	public InputStreamReader get(String key){
	      debugPrint("Downloading an object");
          S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
          debugPrint("Content-Type: "  + object.getObjectMetadata().getContentType());
          return new InputStreamReader(object.getObjectContent());
	}
	
	public String getDocument(String key) {
		InputStreamReader inputReader = this.get(key);
		Scanner s = new Scanner(inputReader).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
	}
	
	public void putDocument(String key, String content) throws IOException {
		
		File file = File.createTempFile("aws-java-sdk-", ".txt");
		file.deleteOnExit();

		Writer writer = new OutputStreamWriter(new FileOutputStream(file));
		writer.write(content);
		writer.close();
		
		put(key, file);
	}

  public void createBlankDocument(String key) throws IOException {
    putDocument(key, "");
  }
	
	private void debugPrint(String msg){
		if(true == debugEnabled)
			System.err.println(msg);
	}
	
	private void printASErrMsg(AmazonServiceException ase){
		System.out.println("Caught an AmazonServiceException, which means your request made it "
						+ "to Amazon S3, but was rejected with an error response for some reason.");
		System.out.println("Error Message:    " + ase.getMessage());
		System.out.println("HTTP Status Code: " + ase.getStatusCode());
		System.out.println("AWS Error Code:   " + ase.getErrorCode());
		System.out.println("Error Type:       " + ase.getErrorType());
		System.out.println("Request ID:       " + ase.getRequestId());
	}
	
	private void printACEerrMsg(AmazonClientException ace){
		System.out.println("Caught an AmazonClientException, which means the client encountered "
				+ "a serious internal problem while trying to communicate with S3, "
				+ "such as not being able to access the network.");
		System.out.println("Error Message: " + ace.getMessage());
	}
	
	private static File createSampleFile() throws IOException {
		File file = File.createTempFile("aws-java-sdk-", ".txt");
		file.deleteOnExit();

		Writer writer = new OutputStreamWriter(new FileOutputStream(file));
		writer.write("abcdefghijklmnopqrstuvwxyz\n");
		writer.write("01234567890112345678901234\n");
		writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
		writer.write("01234567890112345678901234\n");
		writer.write("abcdefghijklmnopqrstuvwxyz\n");
		writer.close();
		return file;
	}
	
	private static File createTestFile() throws IOException {
		File file = File.createTempFile("aws-java-sdk-", ".txt");
		file.deleteOnExit();

		Writer writer = new OutputStreamWriter(new FileOutputStream(file));
		writer.write("hello xixi!");
		writer.close();
		return file;
	}
	
	
	public static void main(String[] args) {
		KeyValueStore kv = new KeyValueStore();

    kv.createBucket();
		// List the buckets in your account
		System.out.println("Listing buckets");
		for (Bucket bucket : kv.s3.listBuckets()) {
			System.out.println(" - " + bucket.getName());
		}
		try {
			kv.put("223", createTestFile());

			System.out.println(kv.getDocument("223"));
			
			System.out.println();
			
			//kv.deleteKey("mytest");
			//kv.deleteBucket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

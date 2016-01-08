package irproject;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import util.UserFields;

public class IndexCreator {
		
	private static IndexWriter createIndexWriter() {
		
		//Generate timestamp
		Date date = new Date();
		long time = date.getTime();
		
		Path path = FileSystems.getDefault().getPath("logs", String.valueOf(time));
	    Directory index = null;
		try {
			index = new SimpleFSDirectory(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	        
		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter w = null;
		try {
			w = new IndexWriter(index, config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return w;
	}
	
	private static void populateIndex(IndexWriter w, ArrayList<UserFields> users ) {
		
		java.lang.reflect.Field[] fields = UserFields.class.getDeclaredFields();
		
		if (w != null) {
			for (UserFields user : users) {
				Document doc = new Document();
				Field docField = null;
				for (java.lang.reflect.Field field : fields ){
					if (field.getModifiers() == 1) {
						String type = field.getGenericType().toString();
						switch (type){
						case "class java.lang.String":
							try {
								System.out.println("Attribute: " + field.getName() + " Value: " + (String)field.get(user) );
								if (field.get(user) != null)
									docField = new TextField(field.getName(), (String)field.get(user), Field.Store.YES);
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
							break;
						case "int":
							try {
								System.out.println("Attribute: " + field.getName() + " Value: " + (int)field.get(user) );
								docField = new IntField(field.getName(), (int)field.get(user), Field.Store.YES);
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
							break;
						default:
							throw new IllegalArgumentException("Invalid type " + type);
						}
						System.out.println("Field:" + docField);
						if (docField != null)
							doc.add(docField);
					}
				}
				try {
					System.out.println("Doc:" + doc);
					w.addDocument(doc);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void create(ArrayList<UserFields> users) {
		// Create index
		IndexWriter w = createIndexWriter();
		
		// Populate index
		populateIndex(w, users);
	}
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		
		UserFields user = new UserFields();
		user.gender = "maschio";
		user.hashtags = "#pirla";
		user.ageMax = 15;
		user.ageMin = 11;
		user.location = "benebene";
		user.interest = "niente";
		
		UserFields user2 = new UserFields();
		user2.gender = "femmina";
		user2.hashtags = "#pirla";
		
		
		ArrayList<UserFields> uf = new ArrayList<UserFields>();
		uf.add(user);
		//uf.add(user2);
		create(uf);
		
	}

}
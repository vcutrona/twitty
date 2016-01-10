package irproject;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import util.UserFields;

public class IndexCreator {
	
	private static final String[] boostVars = {"follower"};
	private static final String[] untokenizedVars = {"screenName", "gender"};
		
	private static IndexWriter createIndexWriter() {
		
		Path path = FileSystems.getDefault().getPath("logs", "index");

		//delete old index
		for (File file: Arrays.asList(path.toFile().listFiles())) {
			file.delete();
		}
		
		Directory index = null;
		try {
			index = new SimpleFSDirectory(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	        
		// map field-name to analyzer
		Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
		analyzerPerField.put("interest", new EnglishAnalyzer());
		 
		// create a per-field analyzer wrapper using the StandardAnalyzer as .. standard analyzer ;)
		PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);
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
				for (java.lang.reflect.Field field : fields ){
					Field docField = null;
					if (field.getModifiers() == 1) {
						String type = field.getGenericType().toString();
						
						//create field type
						FieldType fieldType = new FieldType();
						fieldType.setStored(true);
						fieldType.setIndexOptions(IndexOptions.DOCS);
						fieldType.setTokenized(!(Arrays.asList(untokenizedVars).contains(field.getName())));

						switch (type){
						case "class java.lang.String":
							try {
								System.out.println("Attribute: " + field.getName() + " Value: " + (String)field.get(user) );
								if (field.get(user) != null) {
									docField = new Field(field.getName(), (String)field.get(user), fieldType);
								}
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
							break;
						case "int":
							try {
								System.out.println("Attribute: " + field.getName() + " Value: " + (int)field.get(user) );
								if (Arrays.asList(boostVars).contains(field.getName())) {
									System.out.println("Faccio boost");
									docField = new NumericDocValuesField(field.getName(), (int)field.get(user));
								} else {
									docField = new IntField(field.getName(), (int)field.get(user), Field.Store.YES);
								}
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
					//System.out.println("Doc:" + doc);
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
	
	public void create(ArrayList<UserFields> users) {
		// Create index
		IndexWriter w = createIndexWriter();
		
		// Populate index
		populateIndex(w, users);
	}
	
	public static void get(String[] args) throws IllegalArgumentException, IllegalAccessException {
		
		UserFields user = new UserFields();
		user.gender = "maschio";
		user.hashtags = "#pirla";
		user.ageMax = 15;
		user.ageMin = 11;
		user.location = "benebene";
		user.interest = "to be or not to be aliens";
		user.follower = 5;
		user.screenName = "Angelino";
		
		UserFields user2 = new UserFields();
		user2.gender = "femmina";
		user2.hashtags = "#pirla";
		
		
		ArrayList<UserFields> uf = new ArrayList<UserFields>();
		uf.add(user);
		uf.add(user2);
		//create(uf);
		
	}

}
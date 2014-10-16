package apex.com.main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Parser {
	private static String[] rgstrScope = {"public","global","private"};
	public static ClassModel parseFileContents(String filePath){
		try{
			FileInputStream fstream = new FileInputStream(filePath);
		    // Get the object of DataInputStream
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    //Read File Line By Line
		    boolean classparsed = false;
		    boolean commentsStarted = false;
		    ArrayList<String> lstComments = new ArrayList<String>();
		    ClassModel cModel = new ClassModel();
		    ArrayList<MethodModel> methods = new ArrayList<MethodModel>();
		    ArrayList<PropertyModel> properties = new ArrayList<PropertyModel>();
		    
		    // DH: Consider using java.io.StreamTokenizer to read the file a token at a time?
		    //
		    // new strategy notes:
		    // 		any line with " class " is a class definition
		    // 		any line with scope (global, public, private) is a class, method, or property definition.
		    // 		you can detect a method vs. a property by the presence of ( )'s
		    // 		you can also detect properties by get; or set;, though they may not be on the first line.
		    // 		in apex, methods that start with get and take no params, or set with 1 param, are actually properties.
		    //

	    
		    while ((strLine = br.readLine()) != null)   {
		    
		    	strLine = strLine.trim();
		    	if (strLine.length() == 0) 
		    		continue;
		    	
		    	// ignore anything after // style comments.  this allows hiding of tokens from ApexDoc.
		    	int ich = strLine.indexOf("//");
		    	if (ich > -1) {
	    			strLine = strLine.substring(0, ich);
		    	}
		    	
		    	// gather up our comments
		    	if (strLine.startsWith("/**")) {
		    		commentsStarted = true;
		    		lstComments.clear();
		    		continue;
		    	}
		    	
		    	if (commentsStarted && strLine.endsWith("*/")) {
		    		commentsStarted = false;
		    		continue;
		    	}
		    	
		    	if (commentsStarted) {
		    		lstComments.add(strLine);
		    		continue;
		    	}
		    	
		    	// ignore anything after an =.  this avoids confusing properties with methods.
		    	ich = strLine.indexOf("=");
		    	if (ich > -1) {
	    			strLine = strLine.substring(0, ich);
		    	}
		    	
		    	// ignore anything after an {.  this avoids confusing properties with methods.
		    	ich = strLine.indexOf("{");
		    	if (ich > -1) {
	    			strLine = strLine.substring(0, ich);
		    	}

		    	// ignore lines not dealing with scope
		    	if (!strContainsScope(strLine))
		    		continue;

		    	// look for a class
		    	if (!classparsed && strLine.contains(" class ")) {
	    			classparsed = true;
	    			fillClassModel(cModel, strLine, lstComments);
	    			lstComments.clear();
	    			continue;
		    	}
		    	
		    	// look for a method
		    	if (strLine.contains("(")) {
		    		MethodModel mModel = new MethodModel();
    				fillMethodModel(mModel, strLine, lstComments);
    				methods.add(mModel);
	    			lstComments.clear();
	    			continue;		    		
		    	}
		    	
		    	// TODO: need to handle nested class.  ignore it for now!
		    	if (strLine.contains(" class "))
		    		continue;
		    	
		    	// must be a property
		    	PropertyModel propertyModel = new PropertyModel();
		    	fillPropertyModel(propertyModel, strLine, lstComments);
		    	properties.add(propertyModel);
		    	lstComments.clear();
		    	continue;
		    }
		    cModel.setMethods(methods);
		    cModel.setProperties(properties);
		    
		    debug(cModel);
		    //Close the input stream
		    in.close();
		    return cModel;
	    }catch (Exception e){//Catch exception if any
	      System.err.println("Error: " + e.getMessage());
	    }
	    
	    return null;
	}

	private static boolean strContainsScope(String str) {
		for (int i = 0; i < rgstrScope.length; i++) {
			if (str.contains(rgstrScope[i])) {
				return true;
			}
		}
		return false;
	}
	
	private static void fillPropertyModel(PropertyModel propertyModel, String name, ArrayList<String> lstComments) {
		propertyModel.setNameLine(name);
		boolean inDescription = false;
		for (String comment : lstComments) {
			comment = comment.trim();
			int idxStart = comment.toLowerCase().indexOf("* @description");
			if (idxStart != -1) {
				propertyModel.setDescription(comment.substring(idxStart + 15).trim());
				inDescription = true;
				continue;
			}
			
			// handle multiple lines for description.
			if (inDescription) {
				int i;
				for (i = 0; i < comment.length(); i++) {
					char ch = comment.charAt(i);
					if (ch != '*' && ch != ' ')
						break;				
				}
				if (i < comment.length()) {
					propertyModel.setDescription(propertyModel.getDescription() + ' ' + comment.substring(i));
				}
				continue;
			}
		}
	}
	
	private static void fillMethodModel(MethodModel mModel, String name, ArrayList<String> lstComments){
		mModel.setNameLine(name);
		boolean inDescription = false;
		for (String comment : lstComments) {
			comment = comment.trim();
			int idxStart = comment.toLowerCase().indexOf("* @author");
			if(idxStart != -1){
				mModel.setAuthor(comment.substring(idxStart + 10).trim());
				inDescription = false;
				continue;
			}
			
			idxStart = comment.toLowerCase().indexOf("* @date");
			if(idxStart != -1){
				mModel.setDate(comment.substring(idxStart + 7).trim());
				inDescription = false;
				continue;
			}
			
			idxStart = comment.toLowerCase().indexOf("* @return");
			if(idxStart != -1){
				mModel.setReturns(comment.substring(idxStart + 10).trim());
				inDescription = false;
				continue;
			}
			
			idxStart = comment.toLowerCase().indexOf("* @param");
			if(idxStart != -1){
				mModel.getParams().add(comment.substring(idxStart + 8).trim());
				inDescription = false;
				continue;
			}
			
			idxStart = comment.toLowerCase().indexOf("* @description");
			if(idxStart != -1){
				mModel.setDescription(comment.substring(idxStart + 15).trim());
				inDescription = true;
				continue;
			}
			if(inDescription == false){
				inDescription = (comment.toLowerCase().indexOf("* ") >= 0 && comment.toLowerCase().indexOf("* @") < 0);
			}
			// handle multiple lines for description.
			if (inDescription) {
				int i;
				for (i = 0; i < comment.length(); i++) {
					char ch = comment.charAt(i);
					if (ch != '*' && ch != ' ')
						break;				
				}
				if (i < comment.length()) {
					mModel.setDescription(mModel.getDescription() + ' ' + comment.substring(i));
				}
				continue;
			}
			//System.out.println("#### ::" + comment);
		}
	}
	private static void fillClassModel(ClassModel cModel, String name, ArrayList<String> lstComments){
		//System.out.println("@@@@ " + name);
		cModel.setNameLine(name);
		boolean inDescription = false;
		for (String comment : lstComments) {
			comment = comment.trim();
			int idxStart = comment.toLowerCase().indexOf("* @description");
			if(idxStart != -1){
				cModel.setDescription(comment.substring(idxStart + 15).trim());
				inDescription = true;
				continue;
			}
			
			idxStart = comment.toLowerCase().indexOf("* @author");
			if(idxStart != -1){
				cModel.setAuthor(comment.substring(idxStart + 10).trim());
				inDescription = false;
				continue;
			}
			
			idxStart = comment.toLowerCase().indexOf("* @date");
			if(idxStart != -1){
				cModel.setDate(comment.substring(idxStart + 7).trim());
				inDescription = false;
				continue;
			}
			
			// handle multiple lines for description.
			if (inDescription) {
				int i;
				for (i = 0; i < comment.length(); i++) {
					char ch = comment.charAt(i);
					if (ch != '*' && ch != ' ')
						break;				
				}
				if (i < comment.length()) {
					cModel.setDescription(cModel.getDescription() + ' ' + comment.substring(i));
				}
				continue;
			}
			//System.out.println("#### ::" + comment);
		}
	}
	
	private static JSONObject generateJSON(ClassModel cModel){
		JSONArray jsonClasses = new JSONArray();
		
		return null;
	}
	
	private static void debug(ClassModel cModel){
		JSONObject classDescription = new JSONObject();
		try{
			System.out.println("Class::::::::::::::::::::::::");
			if(cModel.getClassName() != null) System.out.println(cModel.getClassName());			
			if(cModel.getNameLine() != null) System.out.println(cModel.getNameLine());
			System.out.println(cModel.getAuthor());
			System.out.println(cModel.getDescription());
			System.out.println(cModel.getDate());
			if(cModel.getClassName() != null && cModel.getClassName() != ""){
				classDescription.put("name", cModel.getClassName());
				classDescription.put("className", cModel.getClassName());
				classDescription.put("author", cModel.getAuthor());
				classDescription.put("description", cModel.getDescription());
				classDescription.put("date", cModel.getDate());
				
				System.out.println("Properties::::::::::::::::::::::::");
				JSONArray properties = new JSONArray();
				for (PropertyModel property : cModel.getProperties()) {
					JSONObject prop = new JSONObject();
					prop.put("name", property.getNameLine());
					prop.put("description", property.getDescription());
					System.out.println(property.getNameLine());
					System.out.println(property.getDescription());
					properties.add(prop);
				}
				
				classDescription.put("properties", properties);
				System.out.println("Methods::::::::::::::::::::::::");
				JSONArray methods = new JSONArray();
				JSONArray constructors = new JSONArray();
				for (MethodModel method : cModel.getMethods()) {

					JSONObject methodDescription = new JSONObject();
					System.out.println(method.getMethodName());
					System.out.println(method.getAuthor());
					System.out.println(method.getDescription());
					System.out.println(method.getDate());
					methodDescription.put("name", method.getNameLine());
					methodDescription.put("author", method.getAuthor());
					methodDescription.put("description", method.getDescription());
					methodDescription.put("date", method.getDate());
					
					JSONArray params = new JSONArray();
					for (String param : method.getParams()) {
						String[] pValues = param.split("\\s+", 2);
						JSONObject pObj = new JSONObject();
						pObj.put("name", pValues[0]);
						pObj.put("description", "");
						if(pValues.length > 1){
							pObj.put("description", pValues[1]);
						}
						params.add(pObj);
					}
					methodDescription.put("params", params);
					if(method.getMethodName().compareToIgnoreCase(cModel.getClassName()) == 0){
						constructors.add(methodDescription);
					}else{
						methods.add(methodDescription);
					}
				}
				classDescription.put("methods", methods);
				classDescription.put("constructors", constructors);
				System.out.println(classDescription.toJSONString());
				//jsonClasses.add(classDescription);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}

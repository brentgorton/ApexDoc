package apex.com.main;
import java.util.ArrayList;
import java.util.Collections;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class ClassModel extends ApexModel {
	
	public ClassModel(){
		super();
		methods = new ArrayList<MethodModel>();
		properties = new ArrayList<PropertyModel>();
	}
	
	public void populate(String name, ArrayList<String> comments){
		this.setNameLine(name);
		this.parseComments(comments);
	}

	private ArrayList<MethodModel> methods;
	private ArrayList<PropertyModel> properties;
	
	public ArrayList<PropertyModel> getProperties() {
		return properties;
	}

	public void setProperties(ArrayList<PropertyModel> properties) {
		this.properties = properties;
	}

	public ArrayList<MethodModel> getMethods() {
		if(methods != null){
			Collections.sort(methods);
		}
		return methods;
	}

	public void setMethods(ArrayList<MethodModel> methods) {
		this.methods = methods;
	}
	
	public String getClassName(){
		String nameLine = getNameLine();
		if (nameLine != null) nameLine = nameLine.trim();
		//System.out.println("@@ File Name = " + nameLine);
		if(nameLine != null && nameLine.trim().length() > 0 ){
			//System.out.println("## File Name = " + nameLine.trim().lastIndexOf(" "));
			int fFound = nameLine.indexOf("class ");
			int lFound = nameLine.indexOf(" ", fFound + 6);
			if(lFound == -1)
				return nameLine.substring(fFound + 6);
			try{
				String name = nameLine.substring(fFound + 6, lFound);
				return name;
			}catch(Exception ex){
				return nameLine.substring(nameLine.lastIndexOf(" ") + 1);
			}
		}else{
			return "";
		}
		
	}
	
	public JSONObject toJSON(){
		JSONObject obj = super.toJSON();
		obj.put("name", this.getClassName());
		JSONArray mArray = new JSONArray();
		JSONArray constructors = new JSONArray();
		for(MethodModel m : this.getMethods()){
			if(m.getIsConstructor()){
				constructors.add(m.toJSON());
			}else{
				mArray.add(m.toJSON());
			}
		}
		obj.put("methods", mArray);
		obj.put("constructors", constructors);
		return obj;
		//return null;
	}
	
}

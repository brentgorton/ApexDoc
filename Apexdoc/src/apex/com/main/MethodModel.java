package apex.com.main;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class MethodModel extends ApexModel implements Comparable<MethodModel> {
	private boolean isConstructor;
	public MethodModel(){
		super();
		//params = new ArrayList<String>();
	}
	
	public MethodModel(String name, ArrayList<String> comments){
		this();
		this.isConstructor = false;
		this.setNameLine(name);
		this.parseComments(comments);
	}
	
	public void setNameLine(String nameLine) {
		// remove anything after the parameter list
		if (nameLine != null) {
			int i = nameLine.lastIndexOf(")");
			if (i >= 0) 
				nameLine = nameLine.substring(0, i+1);
		}
		super.setNameLine(nameLine);
		String constructorNameLine = getNameLine().toLowerCase().replace("public", "").trim();
		this.isConstructor = false;
		if(constructorNameLine != null && constructorNameLine.length() > 0 ){
			int lastindex = constructorNameLine.indexOf("(");
			if(lastindex > 0){
				String methodName = constructorNameLine.substring(0,lastindex);
				if(!methodName.contains(" ")){
					this.isConstructor = true;
				}
			}
		}
	}
	/*
	public ArrayList<String> getParams() {
		return params;
	}
	public void setParams(ArrayList<String> params) {
		this.params = params;
	}
	*/
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	
	public String getMethodName(){
		String nameLine = getNameLine().trim();
		if(nameLine != null && nameLine.length() > 0 ){
			int lastindex = nameLine.indexOf("(");
			if (lastindex >= 0) { 
				String methodName = ApexDoc.strPrevWord(nameLine, lastindex);
				return methodName;
			}
		}
		return "";
	}
	
	public boolean getIsConstructor(){
		return this.isConstructor;
	}

	@Override
	public int compareTo(MethodModel method){
		try{
			return this.getMethodName().compareToIgnoreCase(method.getMethodName());
		}catch(NullPointerException ex){
			return 0;
		}
	}
	/*
	public JSONObject toJSON(){
		JSONObject obj = super.toJSON();
		obj.put("params", new JSONArray());
		return obj;
	}
	*/
	//private ArrayList<String> params;
	private String returnType;
}

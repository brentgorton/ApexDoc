package apex.com.main;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ApexModel {
	public ApexModel(){
		params = new ArrayList<String>();
	}
	public void parseComments(ArrayList<String> lstComments){
		boolean inDescription = false;
		for (String comment : lstComments) {
			comment = comment.trim();
			int idxStart = comment.toLowerCase().indexOf("* @author");
			if(idxStart != -1){
				this.setAuthor(comment.substring(idxStart + 10).trim());
				inDescription = false;
				continue;
			}
			
			idxStart = comment.toLowerCase().indexOf("* @date");
			if(idxStart != -1){
				this.setDate(comment.substring(idxStart + 7).trim());
				inDescription = false;
				continue;
			}
			
			idxStart = comment.toLowerCase().indexOf("* @return");
			if(idxStart != -1){
				this.setReturns(comment.substring(idxStart + 10).trim());
				inDescription = false;
				continue;
			}
			
			idxStart = comment.toLowerCase().indexOf("* @param");
			if(idxStart != -1){
				this.getParams().add(comment.substring(idxStart + 8).trim());
				inDescription = false;
				continue;
			}
			
			idxStart = comment.toLowerCase().indexOf("* @description");
			if(idxStart != -1){
				this.setDescription(comment.substring(idxStart + 15).trim());
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
					this.setDescription(this.getDescription() + ' ' + comment.substring(i));
				}
				continue;
			}
			//System.out.println("#### ::" + comment);
		}
	}
	public String getNameLine() {
		return nameLine;
	}
	public void setNameLine(String nameLine) {
		this.nameLine = nameLine;
	}
	public String getDescription() {
		return description == null ? "" : description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAuthor() {
		return author == null ? "" : author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDate() {
		return date == null ? "" : date;
	}
	public void setDate(String date) {
		this.date = date;
	}	
	public String getReturns() {
		return returns == null ? "" : returns;
	}
	public void setReturns(String returns) {
		this.returns = returns;
	}

	public ArrayList<String> getParams() {
		return params;
	}
	public void setParams(ArrayList<String> params) {
		this.params = params;
	}
	
	private String nameLine;
	private String description;
	private String author;
	private String date;
	private String returns;
	private ArrayList<String> params;
	private String returnType;
	
	public JSONObject toJSON(){
		JSONObject apexDescription = new JSONObject();
		apexDescription.put("name", this.getNameLine());
		apexDescription.put("className", this.getNameLine());
		apexDescription.put("author", this.getAuthor());
		apexDescription.put("description", this.getDescription());
		apexDescription.put("date", this.getDate());
		if(this instanceof MethodModel){
			JSONArray pArray = new JSONArray();
			for(String param : params){
				String[] pValues = param.split("\\s+", 2);
				JSONObject pObj = new JSONObject();
				pObj.put("name", pValues[0]);
				pObj.put("description", "");
				if(pValues.length > 1){
					pObj.put("description", pValues[1]);
				}
				pArray.add(pObj);
			}
			apexDescription.put("params", pArray);
		}
		return apexDescription;
	}
}

package org.aksw.tsoru.acids3.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class Oracle  {
	
	private String path;
	private HashMap<String, String> srcMap, tgtMap;
	
	public Oracle(String path) {
		super();
		this.path = path;
		srcMap = new HashMap<String, String>();
		tgtMap = new HashMap<String, String>();
	}

	public void build() throws IOException {
		CSVReader reader = new CSVReader(new FileReader(path), ',', 
				CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER);
		String[] nextLine = reader.readNext(); // skip header
		while ((nextLine = reader.readNext()) != null) {
			srcMap.put(nextLine[0], nextLine[1]);
			tgtMap.put(nextLine[1], nextLine[0]);
		}
		reader.close();
	}
	
	public String get(String uri) {
		String x = srcMap.get(uri);
		return (x != null) ? x : tgtMap.get(uri);
	}
	
	public int getSourceSize() {
		return srcMap.size();
	}
	
	public Set<String> sourceKeySet() {
		return srcMap.keySet();
	}

	public boolean ask(String s, String t) {
		if(srcMap.containsKey(s))
			return srcMap.get(s).equals(t);
		return false;
	}

}

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
	private HashMap<String, String> map;
	
	public Oracle(String path) {
		super();
		this.path = path;
		map = new HashMap<String, String>();
	}

	public void build() throws IOException {
		CSVReader reader = new CSVReader(new FileReader(path), ',', 
				CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER);
		String[] nextLine = reader.readNext(); // skip header
		while ((nextLine = reader.readNext()) != null) {
			map.put(nextLine[0], nextLine[1]);
			map.put(nextLine[1], nextLine[0]);
		}
		reader.close();
	}
	
	public String get(String uri) {
		return map.get(uri);
	}
	
	public int getSize() {
		return map.size();
	}
	
	public Set<String> keySet() {
		return map.keySet();
	}

	public boolean ask(String s, String t) {
		if(map.containsKey(s))
			return map.get(s).equals(t);
		return false;
	}

}

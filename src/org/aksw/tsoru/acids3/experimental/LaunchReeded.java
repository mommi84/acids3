package org.aksw.tsoru.acids3.experimental;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.aksw.tsoru.acids3.db.SQLiteManager;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class LaunchReeded {

	public static void main(String[] args) throws SQLException {
		ReededFilter rf = new ReededFilter();
		SQLiteManager sql1 = new SQLiteManager("data/DBLP-semantified.nt_5561821106850792.db");
		SQLiteManager sql2 = new SQLiteManager("data/ACM-semantified.nt_0015028756133490528.db");
		
		Statement stmt1 = sql1.getStatement();
		Statement stmt2 = sql2.getStatement();
		
		String query = "select o from triples where otype='STRING';";
		ArrayList<String> src = new ArrayList<String>();
		ArrayList<String> tgt = new ArrayList<String>();
		ResultSet rs1 = stmt1.executeQuery(query);
		do {
			src.add(rs1.getString("o"));
		} while(rs1.next());
		ResultSet rs2 = stmt2.executeQuery(query);
		do {
			tgt.add(rs2.getString("o"));
		} while(rs2.next());
		
		System.out.println(src);
		System.out.println(tgt);
		
		ArrayList<Pair> pairs = rf.filter(src, tgt, 4.0);
		for(Pair p : pairs)
			System.out.println(p.getS()+"\t"+p.getT());
	}

}

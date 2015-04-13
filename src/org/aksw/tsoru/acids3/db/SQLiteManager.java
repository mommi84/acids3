package org.aksw.tsoru.acids3.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.io.Arg;
import org.aksw.tsoru.acids3.util.Randomly;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class SQLiteManager {

	private static final Logger LOGGER = Logger.getLogger(SQLiteManager.class);
	
	private Statement statement;
	private Connection connection;
	private String dbPrefix;

	private String filename;

	public SQLiteManager(String dbPrefix) {

		filename = dbPrefix + "_" + Randomly.getRandom() + ".db";
		LOGGER.info(filename + " created.");

		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + filename);
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			
			this.dbPrefix = dbPrefix;
			
			statement.executeUpdate("drop table if exists triples");
			statement.executeUpdate("create table triples(s, p, o, otype)");
			connection.commit();
		} catch (ClassNotFoundException | SQLException e) {
			LOGGER.error(e.getMessage());
		}

	}

	/**
	 * @param triple
	 * @throws SQLException
	 */
	public void insert(Triple triple) {
		
		String o = "", otype = "";
		
		Node obj = triple.getObject();
		
		if(obj.isURI()) {
			o = obj.getURI();
			otype = "URI";
		}
		if(obj.isLiteral()) {
			o = "" + obj.getLiteral().getValue();
			otype = obj.getLiteralDatatypeURI();
			if(otype == null)
				otype = "STRING";
		}
	
		String q = "insert into triples values('"
				+ triple.getSubject().getURI() + "', '" 
				+ triple.getPredicate().getURI() + "', '" 
				+ o.replaceAll("'", "''") + "', '" 
				+ otype + "')";
		
		try {
			statement.executeUpdate(q);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
			LOGGER.error("Query: "+q);
		}

	}

	public ArrayList<Tuple> getTuples(String uri) {
		
		ArrayList<Tuple> res = new ArrayList<Tuple>();
		
		ResultSet rs;
		try {
			rs = statement
					.executeQuery("select * from triples where s = '"+uri+"' or (o = '"+uri+"' and otype = 'URI');");
//					.executeQuery("select * from triples where (s = '"+uri+"' or (o = '"+uri+"' and otype = 'URI')) and p <> '"+URLs.RDF_TYPE+"';");
			while (rs.next()) {
				// read the result set
				Tuple t = new Tuple(rs.getString("s"), rs.getString("p"), rs.getString("o"), rs.getString("otype"));
				res.add(t);
			}
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
		}

		return res;
	}

	

	public void commit() {
		try {
			connection.commit();
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
		}
	}

	public void statementClose() {
		try {
			statement.close();
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
		}
	}

	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
		}
		try {
			new File(filename).delete();
			LOGGER.info(filename + " deleted.");
		} catch (Exception x) {
			LOGGER.error("Cannot delete db: " + filename);
		}
	}

	public String getDbPrefix() {
		return dbPrefix;
	}

	public static void main(String[] args) {
		Parameters param = new Parameters();
		param.setSourcePath("data/ceur-ws.ttl");
		param.setTargetPath("data/colinda.nt");
		param.setOraclePath("data/oracle-person1.csv");
		new SQLiteManager(param.getPath(Arg.SOURCE));
	}

}

package org.aksw.tsoru.acids3.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.TreeSet;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.io.Arg;
import org.aksw.tsoru.acids3.io.Processing;
import org.aksw.tsoru.acids3.util.NodeUtils;
import org.aksw.tsoru.acids3.util.Randomly;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class SQLiteManager {

	private static final Logger LOGGER = Logger.getLogger(SQLiteManager.class);
	
	private Processing processing;

	private Statement statement;
	private Connection connection;
	private String dbPrefix;

	private String filename;

	/**
	 * Create the manager. Add two tables in the DB: 'triples' for [subject,
	 * predicate, object, object_type] and 'instances' for [instance_uri,
	 * int_incoming, int_outcoming, flag_hub, flag_authority].
	 * 
	 * @param dbPrefix
	 */
	public SQLiteManager(Processing processing, String dbPrefix) {
		
		this.processing = processing;
		this.dbPrefix = dbPrefix;

		filename = dbPrefix + "_" + Randomly.getRandom() + ".db";
		LOGGER.info(filename + " created.");

		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + filename);
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.


			statement.executeUpdate("drop table if exists triples");
			statement.executeUpdate("create table triples(s, p, o, otype)");

			statement.executeUpdate("drop table if exists instances");
			statement
					.executeUpdate("create table instances(uri, incoming, outcoming, hub, authority)");

			connection.commit();
						
		} catch (ClassNotFoundException | SQLException e) {
			LOGGER.error(e.getMessage());
		}

	}

	/**
	 * @param triple
	 */
	public void insert(Triple triple) {

		NodeUtils utils = new NodeUtils(triple.getObject());
		String object = utils.getObject();
		String otype = utils.getOtype();

		String q = "insert into triples values('"
				+ triple.getSubject().getURI() + "', '"
				+ triple.getPredicate().getURI() + "', '" + object + "', '"
				+ otype + "')";

		try {
			statement.executeUpdate(q);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
			LOGGER.error("Query: " + q);
		}

	}

	public void add(String instanceURI, Field field) {
		// if instance exists
		int amount = this.amount(instanceURI, field);
		if (amount == 0)
			this.addInstance(instanceURI, field);
		else
			this.increase(instanceURI, field, amount);

	}

	private void increase(String instanceURI, Field field, int amount) {
		String q = "update instances set " + field.getName() + " = '"
				+ (amount + 1) + "' where uri = '" + instanceURI + "'";

		try {
			statement.executeUpdate(q);
			LOGGER.trace("Set " + field + " for '" + instanceURI + "' to "
					+ (amount + 1));
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
			LOGGER.error("Query: " + q);
		}
	}

	private int amount(String instanceURI, Field field) {

		String q = "select " + field.getName()
				+ " from instances where uri = '" + instanceURI + "'";

		try {
			ResultSet rs = statement.executeQuery(q);
			if (!rs.next())
				return 0;
			return rs.getInt(field.getName());
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
			LOGGER.error("Query: " + q);
		}

		return 0;

	}

	private void addInstance(String instanceURI, Field field) {

		String str = (field == Field.OUTCOMING) ? "'1', '0'" : "'0', '1'";

		String q = "insert into instances values('" + instanceURI + "', " + str
				+ ", '0', '0')";

		try {
			statement.executeUpdate(q);
			LOGGER.trace("Set " + field + " for '" + instanceURI + "' to 1");
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
			LOGGER.error("Query: " + q);
		}

	}

	public ArrayList<Tuple> getTuples(String uri) {

		ArrayList<Tuple> res = new ArrayList<Tuple>();

		ResultSet rs;
		try {
			rs = statement.executeQuery("select * from triples where s = '"
					+ uri + "' or (o = '" + uri + "' and otype = 'URI');");
			// .executeQuery("select * from triples where (s = '"+uri+"' or (o = '"+uri+"' and otype = 'URI')) and p <> '"+URLs.RDF_TYPE+"';");
			while (rs.next()) {
				// read the result set
				Tuple t = new Tuple(rs.getString("s"), rs.getString("p"),
						rs.getString("o"), rs.getString("otype"), processing);
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
		SQLiteManager sql = new SQLiteManager(new Processing(Arg.SOURCE, param), param.getPath(Arg.SOURCE));

		String uri = "http://subject.com";
		for (int i = 0; i < 1000; i++) {
			Triple t = new Triple(ResourceFactory.createResource(uri).asNode(),
					ResourceFactory.createProperty("http://property.com")
							.asNode(), ResourceFactory.createResource(
							"http://object.com/" + i).asNode());
			sql.insert(t);
			// sql.commit();
			LOGGER.info("#triples = " + sql.getTuples(uri).size());
		}
		for (Tuple tu : sql.getTuples(uri))
			LOGGER.info(tu);

	}

	public void deleteAll(TreeSet<String> uris) {
		try {
			for (String uri : uris)
				statement.executeUpdate("delete from triples where s = '" + uri
						+ "' or p = '" + uri + "' or o = '" + uri + "';");
			this.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int count() {
		
		String q = "select count(*) as c from triples;";

		try {
			ResultSet rs = statement.executeQuery(q);
			if (!rs.next())
				return 0;
			return rs.getInt("c");
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
			LOGGER.error("Query: " + q);
		}

		return 0;
	}

	public Processing getProcessing() {
		return processing;
	}

}

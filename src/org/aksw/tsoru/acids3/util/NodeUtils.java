package org.aksw.tsoru.acids3.util;

import com.hp.hpl.jena.graph.Node;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class NodeUtils {
	
	private Node node;
	private String object;
	private String otype;
	
	public Node getNode() {
		return node;
	}

	public String getObject() {
		return object;
	}

	public String getOtype() {
		return otype;
	}

	public NodeUtils(Node node) {
		super();
		this.node = node;
		
		if(node.isURI()) {
			object = node.getURI();
			otype = "URI";
		}
		if(node.isLiteral()) {
			object = "" + node.getLiteral().getValue();
			otype = node.getLiteralDatatypeURI();
			if(otype == null)
				otype = "STRING";
		}
		
		object = object.replaceAll("'", "''");
		
	}
	
}

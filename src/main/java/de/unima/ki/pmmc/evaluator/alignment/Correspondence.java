package de.unima.ki.pmmc.evaluator.alignment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class Correspondence implements Comparable<Correspondence> {

	private String uri1;
	private String uri2;
	private double confidence;
	private SemanticRelation relation;
	private Optional<CorrespondenceType> type;
	private Optional<List<String>> annotators;
	
	private static Map<SemanticRelation, String> symbolTable = new HashMap<SemanticRelation, String>();
	
	static {
		symbolTable.put(SemanticRelation.EQUIV, "=");
		symbolTable.put(SemanticRelation.SUB, "<");
		symbolTable.put(SemanticRelation.SUPER, ">");
		symbolTable.put(SemanticRelation.DISJOINT, "!=");
		symbolTable.put(SemanticRelation.NA, "?");
	}
	
	public Correspondence(String uri1, String uri2, SemanticRelation relation, double confidence, CorrespondenceType type) {
		this.uri1 = uri1;
		this.uri2 = uri2;
		this.relation = relation;
		this.confidence = confidence;
		this.type = Optional.ofNullable(type);
		this.annotators = Optional.empty();
	}
	
	public Correspondence(String uri1, String uri2, SemanticRelation relation, double confidence) {
		this.uri1 = uri1;
		this.uri2 = uri2;
		this.relation = relation;
		this.confidence = confidence;
		this.type = Optional.empty();
		this.annotators = Optional.empty();
	}
	
	public Correspondence(String uri1, String uri2, SemanticRelation relation) {
		this(uri1, uri2, relation, 1.0d);
	}

	public Correspondence(String uri1, String uri2, double confidence) {
		this(uri1, uri2, SemanticRelation.EQUIV, confidence);
	}
	
	public Correspondence(String uri1, String uri2, double confidence, CorrespondenceType type) {
		this(uri1, uri2, SemanticRelation.EQUIV, confidence, type);
	}
	
	public Correspondence(String uri1, String uri2) {
		this(uri1, uri2, SemanticRelation.EQUIV, 1.0d);
	}
	
	public Correspondence(String uri1, String uri2, CorrespondenceType type) {
		this(uri1, uri2, SemanticRelation.EQUIV, 1.0d, type);
	}
	
	public String getUri1() {
		return this.uri1;
	}
	
	public String getUri2() {
		return this.uri2;
	}
	
	public void setUri1(String uri1) {
		this.uri1 = uri1;
	}

	public void setUri2(String uri2) {
		this.uri2 = uri2;
	}

	public double getConfidence() {
		return this.confidence;
	}
	
	public SemanticRelation getRelation() {
		return this.relation;
	}
	
	public String getRelationSymbol() {
		return Correspondence.symbolTable.get(this.relation);
	}
	
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	public String toString() {
		String relationSymbol = Correspondence.symbolTable.get(this.relation);
		String type = "";
		if(this.type.isPresent()) {
			type = this.type.get().getName();
		}
		return uri1 + " " + relationSymbol + " " + uri2 + ", " + this.confidence + ", " + type;
	}


	public int compareTo(Correspondence correspondence) {
		Correspondence that = (Correspondence)correspondence;
		if (this.getConfidence() > that.getConfidence()) { return 1; }	
		else if (this.getConfidence() < that.getConfidence()) { return -1; }
		else {
			if (this.getUri1().compareTo(that.getUri1()) > 0) { return 1; }	
			else if	(this.getUri1().compareTo(that.getUri1()) < 0) { return -1; }	
			else {
				if (this.getUri2().compareTo(that.getUri2()) > 0) { return 1; }	
				else if	(this.getUri2().compareTo(that.getUri2()) < 0) { return -1; }	
				else { return 0; }
			}
		}
	}	
	
	public boolean equals(Object correspondence) {
		if (correspondence instanceof Correspondence) {
			Correspondence that = (Correspondence)correspondence;
			return this.getUri1().equals(that.getUri1()) && this.getUri2().equals(that.getUri2());
		}
		else {
			return false;
		}
		
	}
	

	@Override
	public int hashCode() {
		return Objects.hash(uri1, uri2);
	}

	public Optional<CorrespondenceType> getCType() {
		return type;
	}

	public void setType(CorrespondenceType type) {
		this.type = Optional.ofNullable(type);
	}

	public Optional<List<String>> getAnnotators() {
		return annotators;
	}

	public void setAnnotators(Optional<List<String>> annotators) {
		this.annotators = annotators;
	}

}

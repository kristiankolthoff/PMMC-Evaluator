package de.unima.ki.pmmc.evaluator.data;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.unima.ki.pmmc.evaluator.Evaluator;

public class Evaluation {

	private String name;
	private Date creationDate;
	private Map<Double, List<Report>> reports;
	
	public Evaluation(String name, Date creationDate, Map<Double, List<Report>> reports) {
		this.name = name;
		this.creationDate = creationDate;
		this.reports = reports;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public List<Report> getReports(double threshold) {
		return reports.get(threshold);
	}
	
	public List<Report> getReports() {
		return reports.get(Evaluator.THRESHOLD_ZERO);
	}

	public void setReports(Map<Double, List<Report>> reports) {
		this.reports = reports;
	}
	
	
}

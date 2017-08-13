package de.unima.ki.pmmc.evaluator.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class Evaluation {

	private String name;
	private Date creationDate;
	private List<Report> reports;
	
	public Evaluation(String name, Date creationDate, List<Report> reports) {
		this.name = name;
		this.creationDate = creationDate;
		this.reports = reports;
	}
	
	public Evaluation() {
		this.reports = new ArrayList<>();
	}
	
	public boolean addReport(final Report report) {
		return this.reports.add(report);
	}
	
	public boolean addReports(final Collection<Report> reports) {
		return this.reports.addAll(reports);
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
		return this.reports.stream()
				.filter(report -> {return report.getTreshold() == threshold;})
				.collect(Collectors.toList());
	}
	
	public List<Report> getReports(String goldstandardName) {
		return this.reports.stream()
				.filter(report ->  {return report.getGroup().getName().equals(goldstandardName);})
				.collect(Collectors.toList());
	}
	
	public List<Report> getReports(String goldstandardName, double threshold) {
		return this.reports.stream()
				.filter(report -> {return (report.getGroup().getName().equals(goldstandardName)) 
						&& (report.getTreshold() == threshold);})
				.collect(Collectors.toList());
	}
	
	public List<Double> getThresholds() {
		return this.reports.stream()
				.mapToDouble(report -> {return report.getTreshold();})
				.distinct()
				.boxed()
				.collect(Collectors.toList());
	}
	
	public List<String> getGroupNames() {
		return this.reports.stream()
				.map(report -> {return report.getGroup().getName();})
				.distinct()
				.collect(Collectors.toList());
	}
	
	public List<Report> getReports() {
		return this.reports;
	}

}

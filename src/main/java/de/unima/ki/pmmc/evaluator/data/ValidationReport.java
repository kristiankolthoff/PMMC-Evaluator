package de.unima.ki.pmmc.evaluator.data;

import java.util.ArrayList;
import java.util.List;

public class ValidationReport {

	private List<Throwable> throwable;
	private boolean errorsOccured;
	private List<String> info;
	
	public ValidationReport(List<Throwable> throwable, List<String> info) {
		this.throwable = throwable;
		this.info = info;
	}
	
	public ValidationReport() {
		this.throwable = new ArrayList<>();
		this.info = new ArrayList<>();
	}
	
	public boolean isOk() {
		return errorsOccured || throwable.isEmpty();
	}
	
	public void setErrorsOccured(boolean errorsOccured) {
		this.errorsOccured = errorsOccured;
	}
	
	public List<Throwable> getErrors() {
		return throwable;
	}
	
	public void addInfo(String message) {
		this.info.add(message);
	}
	
	public void addError(Throwable throwable) {
		this.throwable.add(throwable);
	}
	
	public String getInfoMessage() {
		StringBuilder builder = new StringBuilder();
		for(String message : info) {
			builder.append(message);
			builder.append("\n");
		}
		return builder.toString();
	}
}

package de.unima.ki.pmmc.evaluator.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MetricResult<T> {

	private T value;
	private String info;
	private List<Throwable> errors;
	
	
	public MetricResult(T value, String info) {
		this.value = value;
		this.info = info;
		this.errors = new ArrayList<>();
	}

	public T getValue() {
		return value;
	}


	public void setValue(T value) {
		this.value = value;
	}


	public String getInfo() {
		return info;
	}


	public void setInfo(String info) {
		this.info = info;
	}


	public List<Throwable> getErrors() {
		return errors;
	}


	public void setErrors(List<Throwable> errors) {
		this.errors = errors;
	}
	
	public static <T> MetricResult<T> from(T value) {
		Objects.requireNonNull(value);
		return new MetricResult<T>(value, "");
		
	}
	
}

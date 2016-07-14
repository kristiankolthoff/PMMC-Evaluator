package de.unima.ki.pmmc.evaluator;

import java.util.Iterator;
import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.TypeCharacteristic;

public class Result implements Iterable<Alignment>{

	private String name;
	private String path;
	private List<Alignment> alignments;
	private List<Characteristic> characteristics;
	private List<TypeCharacteristic> tCharacteristics;
	
	public Result(String name, String path, List<Alignment> alignments,
			List<Characteristic> characteristics,
			List<TypeCharacteristic> tCharacteristics) {
		super();
		this.name = name;
		this.path = path;
		this.alignments = alignments;
		this.characteristics = characteristics;
		this.tCharacteristics = tCharacteristics;
	}

	public Result(String name, String path, List<Alignment> alignments) {
		super();
		this.name = name;
		this.path = path;
		this.alignments = alignments;
	}

	public int size() {
		return alignments.size();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Alignment> getAlignments() {
		return alignments;
	}

	public void setAlignments(List<Alignment> alignments) {
		this.alignments = alignments;
	}

	public List<Characteristic> getCharacteristics() {
		return characteristics;
	}

	public void setCharacteristics(List<Characteristic> characteristics) {
		this.characteristics = characteristics;
	}

	public List<TypeCharacteristic> gettCharacteristics() {
		return tCharacteristics;
	}

	public void settCharacteristics(List<TypeCharacteristic> tCharacteristics) {
		this.tCharacteristics = tCharacteristics;
	}

	@Override
	public Iterator<Alignment> iterator() {
		return alignments.iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Result other = (Result) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}

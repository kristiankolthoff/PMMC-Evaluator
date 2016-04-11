package de.unima.ki.pmmc.evaluator.renderer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;

public abstract class Renderer {
	
	protected BufferedWriter bw;
	protected File file;
	
	public Renderer(String file) throws IOException {
		this.file = new File(file);
		this.bw = new BufferedWriter(new FileWriter(this.file));
	}

	public abstract void render(List<? extends Characteristic> characteristics, String mappingInfo) throws IOException, CorrespondenceException;
	
	public void flush() throws IOException {
		this.bw.flush();
		this.bw.close();
	}
	
}

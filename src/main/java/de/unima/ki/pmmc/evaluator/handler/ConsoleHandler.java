package de.unima.ki.pmmc.evaluator.handler;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;


import de.unima.ki.pmmc.evaluator.matcher.Result;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;

public class ConsoleHandler implements ResultHandler{

	private final String FORMATTER = "| %-25s | %-4s | %-4s | %-4s "
			+ "| %-4s | %-4s | %-4s | %-4s | %-4s | %-4s | %-4s | %-4s |%n";
	private DecimalFormat df;
	private String mappingInfo;
	
	public ConsoleHandler() {
		this.df = new DecimalFormat("#.##");
	}
	
	@Override
	public void open() throws IOException {
	}

	@Override
	public void receive(List<Result> results) {
		this.printHeader();
		for(Result result : results) {
			this.printResult(result.getCharacteristics(), result.getName());
		}
		this.printFooter();
	}
	
	private void printFooter() {
		System.out.format("+---------------------------+------+------+------+------+------+------+------+------+------+------+--------+%n");
		System.out.println();
	}
	
	private void printResult(List<Characteristic> characteristics, String name) {
		String microPrecision = this.df.format(Characteristic.getNBPrecisionMicro(characteristics));
		String macroPrecision = this.df.format(Characteristic.getNBPrecisionMacro(characteristics));
		String stdDevPrecision = this.df.format(Characteristic.getNBPrecisionStdDev(characteristics));
		String microRecall = this.df.format(Characteristic.getNBRecallMicro(characteristics));
		String macroRecall = this.df.format(Characteristic.getNBRecallMacro(characteristics));
		String stdDevRecall = this.df.format(Characteristic.getNBRecallStdDev(characteristics));
		String microFMeasure = this.df.format(Characteristic.getNBFMeasureMicro(characteristics));
		String macroFMeasure = this.df.format(Characteristic.getNBFMeasureMacro(characteristics));
		String stdDevFMeasure = this.df.format(Characteristic.getNBFMeasureStdDev(characteristics));
		String correlation = this.df.format(Characteristic.getCorrelationMicro(characteristics, false));
		String relativeDistance = this.df.format(Characteristic.getRelativeDistance(characteristics, true));
		System.out.format(FORMATTER, name, microPrecision, macroPrecision, stdDevPrecision,
		    	microRecall, macroRecall, stdDevRecall, microFMeasure, macroFMeasure, stdDevFMeasure,
		    	correlation, relativeDistance);
	}
	
	private void printHeader() {
		System.out.format("MappingInformation : " + this.mappingInfo + "%n");
		System.out.format("+---------------------------+--------------------+--------------------+--------------------+------+--------+%n");
		System.out.format("| Approach                  | Precision          | Recall             | F-Measure          | Corr.| R.Dist.|%n");
		System.out.format("+---------------------------+--------------------+--------------------+--------------------+------+--------+%n");
		System.out.format("|                           | Mic. | Mac. | SD   | Mic. | Mac. | SD   | Mic. | Mac. | SD   |      |        |%n");
		System.out.format("+---------------------------+--------------------+--------------------+--------------------+------+--------+%n");
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void setFlowListener(Consumer<String> listener) {
		
	}

	@Override
	public void setOutputPath(Path path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMappingInfo(String info) {
		this.mappingInfo = info;
	}

}

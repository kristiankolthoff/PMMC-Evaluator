package de.unima.ki.pmmc.evaluator.handler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.matcher.Result;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.TypeCharacteristic;

public class JSONHandler implements ResultHandler{

	private DecimalFormat df;
	private String mappingInfo;
	private Path outputPath;
	private BufferedWriter bw;
	private JsonWriterFactory writerFactory;
	private JsonWriter jsonWriter;
	private JsonArrayBuilder jsonArray;
	private Map<String, Object> properties;
	public static final String FILE_TYPE = ".json";
	
	private static final String MICRO = "micro";
	private static final String MACRO = "macro";
	private static final String STD_DEV = "stdDev";
	private static final String PRECISION = "precision";
	private static final String RECALL = "recall";
	private static final String FMEASURE = "f1-measure";
	
	
    public JSONHandler() {
    	this.df = new DecimalFormat("#.###");
    	this.properties = new HashMap<>();
    	this.properties.put(JsonGenerator.PRETTY_PRINTING, true);
	}
    
	@Override
	public void open() throws IOException {
		jsonArray = Json.createArrayBuilder();
		try {
			this.bw = Files.newBufferedWriter(Paths.get(this.outputPath + "/" + this.mappingInfo + FILE_TYPE));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void receive(List<Result> results) {
		for(Result result : results) {
			List<? extends Characteristic> characteristics = null;
			if(result.isEmptyCharacteristics()) {
				characteristics = result.getTypeCharacteristics();
			} else {
				characteristics = result.getCharacteristics();
			}
			//Binary metrics for plain characteristics
			String microPrecision = this.df.format(Characteristic.getPrecisionMicro(characteristics));
			String macroPrecision = this.df.format(Characteristic.getPrecisionMacro(characteristics));
			String stdDevPrecision = this.df.format(Characteristic.getPrecisionStdDev(characteristics));
			String microRecall = this.df.format(Characteristic.getRecallMicro(characteristics));
			String macroRecall = this.df.format(Characteristic.getRecallMacro(characteristics));
			String stdDevRecall = this.df.format(Characteristic.getRecallStdDev(characteristics));
			String microFMeasure = this.df.format(Characteristic.getFMeasureMicro(characteristics));
			String macroFMeasure = this.df.format(Characteristic.getFMeasureMacro(characteristics));
			String stdDevFMeasure = this.df.format(Characteristic.getFMeasureStdDev(characteristics));
			String correlation = this.df.format(Characteristic.getCorrelationMicro(characteristics, false));
			String relativeDistance = this.df.format(Characteristic.getRelativeDistance(characteristics, true));
			//Non-binary metrics for plain characteristics
			String microPrecisionNB = this.df.format(Characteristic.getNBPrecisionMicro(characteristics));
			String macroPrecisionNB = this.df.format(Characteristic.getNBPrecisionMacro(characteristics));
			String stdDevPrecisionNB = this.df.format(Characteristic.getNBPrecisionStdDev(characteristics));
			String microRecallNB = this.df.format(Characteristic.getNBRecallMicro(characteristics));
			String macroRecallNB = this.df.format(Characteristic.getNBRecallMacro(characteristics));
			String stdDevRecallNB = this.df.format(Characteristic.getNBRecallStdDev(characteristics));
			String microFMeasureNB = this.df.format(Characteristic.getNBFMeasureMicro(characteristics));
			String macroFMeasureNB = this.df.format(Characteristic.getNBFMeasureMacro(characteristics));
			String stdDevFMeasureNB = this.df.format(Characteristic.getNBFMeasureStdDev(characteristics));
			JsonObjectBuilder json = Json.createObjectBuilder(); 
			JsonObjectBuilder resultBuilder =  Json.createObjectBuilder();
			json.add("result", resultBuilder.
					add("name", result.getName()).
					add("path", result.getPath()).
					add("threshold", result.getAppliedThreshold()).
					add("binary-metrics", Json.createObjectBuilder().
							add(PRECISION, Json.createObjectBuilder().
									add(MICRO, microPrecision).
									add(MACRO, macroPrecision).
									add(STD_DEV, stdDevPrecision)).
							add(RECALL, Json.createObjectBuilder().
									add(MICRO, microRecall).
									add(MACRO, macroRecall).
									add(STD_DEV, stdDevRecall)).
							add(FMEASURE, Json.createObjectBuilder().
									add(MICRO, microFMeasure).
									add(MACRO, macroFMeasure).
									add(STD_DEV, stdDevFMeasure)).
							add("correlation", correlation).
							add("relativeDistance", relativeDistance)).
					add("non-binary-metrics", Json.createObjectBuilder().
							add(PRECISION, Json.createObjectBuilder().
									add(MICRO, microPrecisionNB).
									add(MACRO, macroPrecisionNB).
									add(STD_DEV, stdDevPrecisionNB)).
							add(RECALL, Json.createObjectBuilder().
									add(MICRO, microRecallNB).
									add(MACRO, macroRecallNB).
									add(STD_DEV, stdDevRecallNB)).
							add(FMEASURE, Json.createObjectBuilder().
									add(MICRO, microFMeasureNB).
									add(MACRO, macroFMeasureNB).
									add(STD_DEV, stdDevFMeasureNB))));
			if(result.isEmptyCharacteristics()) {
				List<TypeCharacteristic> typeCharacteristics = result.getTypeCharacteristics();
				for(CorrespondenceType type : CorrespondenceType.values()) {
					String microPrecisionType = this.df.format(TypeCharacteristic.getPrecisionMicro(typeCharacteristics, type));
					String macroPrecisionType = this.df.format(TypeCharacteristic.getPrecisionMacro(typeCharacteristics, type));
					String stdDevPrecisionType = this.df.format(TypeCharacteristic.getPrecisionStdDev(typeCharacteristics, type));
					String microRecallType = this.df.format(TypeCharacteristic.getRecallMicro(typeCharacteristics, type));
					String macroRecallType = this.df.format(TypeCharacteristic.getRecallMacro(typeCharacteristics, type));
					String stdDevRecallType = this.df.format(TypeCharacteristic.getRecallStdDev(typeCharacteristics, type));
					String macroFMeasureType = this.df.format(TypeCharacteristic.getPrecisionMacro(typeCharacteristics, type));
					String stdDevFMeasureType = this.df.format(TypeCharacteristic.getPrecisionStdDev(typeCharacteristics, type));
					resultBuilder.add("type_metrics", Json.createObjectBuilder().
							add("type", Json.createObjectBuilder().
									add(PRECISION, Json.createObjectBuilder().
											add(MICRO, microPrecisionType).
											add(MACRO, macroPrecisionType).
											add(STD_DEV, stdDevPrecisionType)).
									add(RECALL, Json.createObjectBuilder().
											add(MICRO, microRecallType).
											add(MACRO, macroRecallType).
											add(STD_DEV, stdDevRecallType)).
									add(FMEASURE, Json.createObjectBuilder().
											add(MICRO, ""))));
				}
			}
			jsonArray.add(json);
		}
	}
	

	@Override
	public void close() throws IOException {
		writerFactory = Json.createWriterFactory(properties);
		jsonWriter = writerFactory.createWriter(this.bw);
		jsonWriter.writeArray(jsonArray.build());
		jsonWriter.close();
	}

	@Override
	public void setFlowListener(Consumer<String> listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOutputPath(Path path) {
		this.outputPath = path;
	}

	@Override
	public void setMappingInfo(String info) {
		this.mappingInfo = info;
	}

}

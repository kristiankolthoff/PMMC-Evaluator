package de.unima.ki.pmmc.evaluator.handler;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.ecs.html.B;
import org.apache.ecs.html.Caption;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TH;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;
import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters.CharArrayConverter;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.data.Result;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.TypeCharacteristic;



public class HTMLHandler implements ResultHandler{

	public static final boolean PRETTY_PRINT = true;
	public static final String FILE_TYPE = ".html";
	
	private Table table;
	private DecimalFormat df;
	private boolean showInBrowser;
	private String mappingInfo;
	private Path outputPath;
	private Consumer<String> listener;
	private BufferedWriter bw;
	
	public HTMLHandler(boolean showInBrowser) throws IOException {
		this.df = new DecimalFormat("#.###");
		this.showInBrowser = showInBrowser;
	}
	
	private void init(List<Result> results) throws CorrespondenceException {
		this.table = new Table();
		this.startTable();
		this.createTableHeadRow1(results);
		this.createTableHeadRow2();
	}

	private void startTable() {
		this.table.setBorder(2);
		this.table.addAttribute("cellpadding", "3");
		this.table.addAttribute("cellspacing", "5");
		Caption caption = new Caption().addElement("Matcher Evaluation Summary");
		caption.setStyle("font-size:50;font-weight:bold");
		this.table.addElement(caption);
		Caption caption1 = new Caption().addElement(this.mappingInfo);
		caption1.setStyle("font-size:20;font-weight:bold");
//		this.table.addElement(caption1);
		this.table.setPrettyPrint(HTMLHandler.PRETTY_PRINT);
		TR trStart = new TR();
		trStart.addAttribute("colspan", "13");
		trStart.setPrettyPrint(HTMLHandler.PRETTY_PRINT);
		this.table.addElement(trStart);
	}
	
	private void createTableHeadRow1(List<Result> results) {
		TR trHead1 = new TR();
		trHead1.setPrettyPrint(HTMLHandler.PRETTY_PRINT);
		trHead1.addElement(new TH().setStyle("border:none;"));
		TH th1 = new TH();
		th1.setStyle("border:none;");
		trHead1.addElement(th1.addAttribute("width", "15"));
		trHead1.addElement(new TH("Precision").addAttribute("colspan", "3"));
		TH th2 = new TH();
		th2.setStyle("border:none;");
		trHead1.addElement(th2.addAttribute("width", "20"));
		trHead1.addElement(new TH("Recall").addAttribute("colspan", "3"));
		TH th3 = new TH();
		th3.setStyle("border:none;");
		trHead1.addElement(th3.addAttribute("width", "20"));
		trHead1.addElement(new TH("F-Measure").addAttribute("colspan", "3"));
		//Compute size of types
		Map<CorrespondenceType, Integer> vals = new HashMap<>();
		for(CorrespondenceType type : CorrespondenceType.values()) {
			vals.put(type, 0);
		}
		int sizeTrivial = 0, sizeOneWord = 0;
		for(TypeCharacteristic tc : results.get(1).getTypeCharacteristics()) {
			for(Correspondence c : tc.getAlignmentReference()) {
				int curr = vals.get(c.getCType().get());
				vals.put(c.getCType().get(), curr+1);
			}
			sizeTrivial+=tc.getAlignmentReference(CorrespondenceType.TRIVIAL).size();
			sizeOneWord+=tc.getAlignmentReference(CorrespondenceType.ONE_WORD_IDENT).size();
		}
		int sum = 0;
		for(Map.Entry<CorrespondenceType, Integer> e : vals.entrySet()) {
			sum += e.getValue();
		}
		
		for(CorrespondenceType type : CorrespondenceType.values()) {
			if(type != CorrespondenceType.DEFAULT) {
				TH th4 = new TH();
				th4.setStyle("border:none;");
				trHead1.addElement(th4.addAttribute("width", "20"));
				trHead1.addElement(new TH(type.getName() + " [" + this.df.format((vals.get(type)/(double)sum)*100) + "%] [#" +
						vals.get(type) + "]").addAttribute("colspan", "4"));
			}
		}
		TH th5 = new TH();
		th5.setStyle("border:none;");
		trHead1.addElement(th5.addAttribute("width", "20"));
		trHead1.addElement(new TH("Correspondence Sum.").addAttribute("colspan", "4"));
//		TH th4 = new TH();
//		th4.setStyle("border:none;");
//		trHead1.addElement(th4.addAttribute("width", "20"));
//		trHead1.addElement(new TH("Corr.").addAttribute("colspan", "1"));
//		TH th5 = new TH();
//		th5.setStyle("border:none;");
//		trHead1.addElement(th5.addAttribute("width", "20"));
//		trHead1.addElement(new TH("Rel. Dist.").addAttribute("colspan", "1"));
//		TH th6 = new TH();
//		th6.setStyle("border:none;");
//		trHead1.addElement(th6.addAttribute("width", "20"));
//		trHead1.addElement(new TH("SpearRang").addAttribute("colspan", "1"));
		this.table.addElement(trHead1);
	}
	
	
	private void createTableHeadRow2() {
		final String AVG_ICON = "&#x2205";
		TR trHead2 = new TR();
		trHead2.setPrettyPrint(HTMLHandler.PRETTY_PRINT);
		trHead2.addElement(new TD(new B("Approach")));
		trHead2.addElement(new TD().setStyle("border:none;"));
		for (int i = 0; i < 3; i++) {
			trHead2.addElement(new TD(AVG_ICON + ";-mic"));
			trHead2.addElement(new TD(AVG_ICON + "-mac"));
			trHead2.addElement(new TD("SD"));
			trHead2.addElement(new TD().setStyle("border:none;"));
		}
		for(CorrespondenceType type : CorrespondenceType.values()) {
			if(type != CorrespondenceType.DEFAULT) {
				trHead2.addElement(new TD("#"));
				trHead2.addElement(new TD(AVG_ICON + "Prec"));
				trHead2.addElement(new TD(AVG_ICON + "Rec"));
				trHead2.addElement(new TD("FMeas"));
//				trHead2.addElement(new TD(AVG_ICON + " Acc"));
				trHead2.addElement(new TD().setStyle("border:none;"));
			}
		}
//		trHead2.addElement(new TD());
//		trHead2.addElement(new TD().setStyle("border:none;"));
//		trHead2.addElement(new TD());
//		trHead2.addElement(new TD().setStyle("border:none;"));
//		trHead2.addElement(new TD().setStyle("border:none;"));
//		trHead2.addElement(new TD());
//		trHead2.addElement(new TD().setStyle("border:none;"));
		trHead2.addElement(new TD("min"));
		trHead2.addElement(new TD("max"));
		trHead2.addElement(new TD("mean"));
		trHead2.addElement(new TD("stdDev"));
//		trHead2.addElement(new TD(AVG_ICON + " Acc"));
		trHead2.addElement(new TD().setStyle("border:none;"));
		this.table.addElement(trHead2);
	}
	
	

	private void appendMetricData(List<TypeCharacteristic> characteristics, String mappingInfo, Result result) throws CorrespondenceException {
		this.table.addElement(new TR().addAttribute("colspan", "13"));
		/**
		 * Standard metrics
		 */
		String microPrecision = this.df.format(Characteristic.getNBPrecisionMicro(characteristics));
		String macroPrecision = this.df.format(Characteristic.getNBPrecisionMacro(characteristics));
		String stdDevPrecision = this.df.format(Characteristic.getNBPrecisionStdDev(characteristics));
		String microRecall = this.df.format(Characteristic.getNBRecallMicro(characteristics));
		String macroRecall = this.df.format(Characteristic.getNBRecallMacro(characteristics));
		String stdDevRecall = this.df.format(Characteristic.getNBRecallStdDev(characteristics));
		String microFMeasure = this.df.format(Characteristic.getNBFMeasureMicro(characteristics));
		String macroFMeasure = this.df.format(Characteristic.getNBFMeasureMacro(characteristics));
		String stdDevFMeasure = this.df.format(Characteristic.getNBFMeasureStdDev(characteristics));
		//TODO create corresponding constants
//		String correlation = this.df.format(Characteristic.getCorrelationMicro(characteristics, false));
//		String relativeDistance = this.df.format(Characteristic.getRelativeDistance(characteristics, true));
//		String spearmanRangCorr = this.df.format(Characteristic.getSpearRangCorrMicro(characteristics));
		System.out.println(Characteristic.isFirstLineMatcher(characteristics) ? "FLM" : "SLM");
		if(Characteristic.isFirstLineMatcher(characteristics)) {
			this.table.addElement(new TD(mappingInfo + " (FLM)").setStyle("background-color:gray;"));
		} else {
			this.table.addElement(new TD(mappingInfo + " (SLM)").setStyle("background-color:white;"));
		}
		this.table.addElement(new TD().setStyle("border:none;"));
		this.table.addElement(new TD(microPrecision));
		this.table.addElement(new TD(macroPrecision));
		this.table.addElement(new TD(stdDevPrecision));
		this.table.addElement(new TD().setStyle("border:none;"));
		this.table.addElement(new TD(microRecall));
		this.table.addElement(new TD(macroRecall));
		this.table.addElement(new TD(stdDevRecall));
		this.table.addElement(new TD().setStyle("border:none;"));
		this.table.addElement(new TD(microFMeasure));
		this.table.addElement(new TD(macroFMeasure));
		this.table.addElement(new TD(stdDevFMeasure));
		Map<CorrespondenceType, Integer> mapTypeCount = TypeCharacteristic.getMatcherTypeCount(characteristics);
		for(CorrespondenceType type : CorrespondenceType.values()) {
			if(type != CorrespondenceType.DEFAULT) {
				String typeCount = String.valueOf(mapTypeCount.get(type));
				String typePrecsion = this.df.format(TypeCharacteristic.getNBPrecisionMacro(characteristics, type));
				String typeRecall = this.df.format(TypeCharacteristic.getNBRecallMacro(characteristics, type));
				String typeFMeasure = this.df.format(TypeCharacteristic.getNBFMeasureMacro(characteristics, type));
				String typeAccuracy = this.df.format(0);
				
//				String typeAccuracy = this.df.format(TypeCharacteristic.getAccuracyMicro(characteristics, type));
				this.table.addElement(new TD().setStyle("border:none;"));
				this.table.addElement(new TD(typeCount));
				this.table.addElement(new TD(typePrecsion));
				this.table.addElement(new TD(typeRecall));
				this.table.addElement(new TD(typeFMeasure));
//				if(type == CorrespondenceType.DEFAULT) {
//					this.table.addElement(new TD("0"));
//				} else {
//					this.table.addElement(new TD(typeAccuracy));
//				}
			}
		}
		this.table.addElement(new TD().setStyle("border:none;"));
		this.table.addElement(new TD(this.df.format(result.minConf())));
		this.table.addElement(new TD(this.df.format(result.maxConf())));
		this.table.addElement(new TD(this.df.format(result.meanConf())));
		this.table.addElement(new TD(this.df.format(result.stdDevConf())));
//		this.table.addElement(new TD().setStyle("border:none;"));
//		this.table.addElement(new TD(correlation));
//		this.table.addElement(new TD().setStyle("border:none;"));
//		this.table.addElement(new TD().setStyle("border:none;"));
//		this.table.addElement(new TD().setStyle("border:none;"));
//		this.table.addElement(new TD(relativeDistance));
//		this.table.addElement(new TD().setStyle("border:none;"));
//		this.table.addElement(new TD().setStyle("border:none;"));
//		this.table.addElement(new TD(spearmanRangCorr));
	}

	public boolean showInBrowser() {
		return showInBrowser;
	}

	public void setShowInBrowser(boolean showInBrowser) {
		this.showInBrowser = showInBrowser;
	}

	@Override
	public void open() throws IOException {
	}

	@Override
	public void receive(List<Result> results) {
		try {
			this.bw = Files.newBufferedWriter(Paths.get(this.outputPath + "/" + this.mappingInfo + FILE_TYPE));
			this.init(results);
			for(Result result : results) {
				System.out.println(result.getName());
//				if(result.getName().toLowerCase().equals("match-sss")) {
//					for(TypeCharacteristic c :result.getTypeCharacteristics()) {
//						for(Correspondence corr : c.getAlignmentMapping(CorrespondenceType.DEFAULT)) {
//							System.out.println(corr);
//						}
//					}
//				}
				//TODO change call to result only
				this.appendMetricData(result.getTypeCharacteristics(), result.getName(), result);
			}
			this.bw.append(this.table.toString());
			this.bw.flush();
			this.bw.close();
			if(this.showInBrowser) {
				File htmlFile = new File(this.outputPath + "/" + this.mappingInfo + FILE_TYPE);
				Desktop.getDesktop().browse(htmlFile.toURI());
			}
		} catch (IOException | CorrespondenceException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void setFlowListener(Consumer<String> listener) {
		this.listener = listener;
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
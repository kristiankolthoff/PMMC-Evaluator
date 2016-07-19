package de.unima.ki.pmmc.evaluator.handler;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;

import org.apache.ecs.html.B;
import org.apache.ecs.html.Caption;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TH;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;

import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.matcher.Result;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;



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
	
	private void init() throws CorrespondenceException {
		this.table = new Table();
		this.startTable();
		this.createTableHeadRow1();
		this.createTableHeadRow2();
	}

	private void startTable() {
		this.table.setBorder(2);
		this.table.addAttribute("cellpadding", "3");
		this.table.addAttribute("cellspacing", "5");
		Caption caption = new Caption().addElement("Matcher Evaluation Summary");
		caption.setStyle("font-size:50;font-weight:bold");
		this.table.addElement(caption);
		this.table.setPrettyPrint(HTMLHandler.PRETTY_PRINT);
		TR trStart = new TR();
		trStart.addAttribute("colspan", "13");
		trStart.setPrettyPrint(HTMLHandler.PRETTY_PRINT);
		this.table.addElement(trStart);
	}
	
	private void createTableHeadRow1() {
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
		TH th4 = new TH();
		th4.setStyle("border:none;");
		trHead1.addElement(th4.addAttribute("width", "20"));
		trHead1.addElement(new TH("Corr.").addAttribute("colspan", "3"));
		TH th5 = new TH();
		th5.setStyle("border:none;");
		trHead1.addElement(th5.addAttribute("width", "20"));
		trHead1.addElement(new TH("Rel. Dist.").addAttribute("colspan", "3"));
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
		trHead2.addElement(new TD());
		trHead2.addElement(new TD().setStyle("border:none;"));
		trHead2.addElement(new TD().setStyle("border:none;"));
		trHead2.addElement(new TD().setStyle("border:none;"));
		trHead2.addElement(new TD());
		this.table.addElement(trHead2);
	}
	

	private void appendMetricData(List<Characteristic> characteristics, String mappingInfo) throws CorrespondenceException {
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
		String correlation = this.df.format(Characteristic.getCorrelationMicro(characteristics, false));
		String relativeDistance = this.df.format(Characteristic.getRelativeDistance(characteristics, true));
		
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
		this.table.addElement(new TD().setStyle("border:none;"));
		this.table.addElement(new TD(correlation));
		this.table.addElement(new TD().setStyle("border:none;"));
		this.table.addElement(new TD().setStyle("border:none;"));
		this.table.addElement(new TD().setStyle("border:none;"));
		this.table.addElement(new TD(relativeDistance));
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
			this.bw = Files.newBufferedWriter(Paths.get(this.outputPath + this.mappingInfo + FILE_TYPE));
			this.init();
			for(Result result : results) {
				this.appendMetricData(result.getCharacteristics(), result.getName());
			}
			this.bw.append(this.table.toString());
			this.bw.flush();
			this.bw.close();
			if(this.showInBrowser) {
				File htmlFile = new File(this.outputPath + this.mappingInfo + FILE_TYPE);
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
package de.unima.ki.pmmc.evaluator.renderer;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.ecs.html.B;
import org.apache.ecs.html.Caption;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TH;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;

import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;



public class HTMLTableNBRenderer extends Renderer{

	public static final boolean PRETTY_PRINT = true;
	
	private Table table;
	private DecimalFormat df;
	private boolean initialized;
	
	public HTMLTableNBRenderer(String file) throws IOException {
		super(file);
		this.table = new Table();
		this.df = new DecimalFormat("#.###");
		this.initialized = false;
	}
	
	private void init(List<Characteristic> characteristics) throws CorrespondenceException {
		this.startTable();
		this.createTableHeadRow1();
		this.createTableHeadRow2();
		this.initialized = true;
	}
	
	@Override
	public void render(List<? extends Characteristic> characteristics, String mappingInfo)
			throws IOException, CorrespondenceException {
		List<Characteristic> actualCharacteristics = new ArrayList<>();
		for(Characteristic c : characteristics) {
			actualCharacteristics.add(c);
		}
		if(!this.initialized) {
			this.init(actualCharacteristics);			
		}
		this.appendMetricData(actualCharacteristics, mappingInfo);
	}
	
	@Override
	public void flush() throws IOException {
		this.bw.append(this.table.toString());
		super.flush();
	}

	private void startTable() {
		this.table.setBorder(2);
		this.table.addAttribute("cellpadding", "3");
		this.table.addAttribute("cellspacing", "5");
		Caption caption = new Caption().addElement("Matcher Evaluation Summary");
		caption.setStyle("font-size:50;font-weight:bold");
		this.table.addElement(caption);
		this.table.setPrettyPrint(HTMLTableNBRenderer.PRETTY_PRINT);
		TR trStart = new TR();
		trStart.addAttribute("colspan", "13");
		trStart.setPrettyPrint(HTMLTableNBRenderer.PRETTY_PRINT);
		this.table.addElement(trStart);
	}
	
	private void createTableHeadRow1() {
		TR trHead1 = new TR();
		trHead1.setPrettyPrint(HTMLTableNBRenderer.PRETTY_PRINT);
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
		trHead2.setPrettyPrint(HTMLTableNBRenderer.PRETTY_PRINT);
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
		String correlation = this.df.format(Characteristic.getCorrelationMicro(characteristics, false));
		String relativeDistance = this.df.format(Characteristic.getRelativeDistance(characteristics));
		
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

}

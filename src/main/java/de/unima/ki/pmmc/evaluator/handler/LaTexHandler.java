package de.unima.ki.pmmc.evaluator.handler;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.data.Evaluation;
import de.unima.ki.pmmc.evaluator.data.Report;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.generator.MetricBinding;
import de.unima.ki.pmmc.evaluator.generator.MetricGroupBinding;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.MetricGroupFactory;

public class LaTexHandler implements ReportHandler {

	public static final String FILE_TYPE = ".tex";
	
	private BufferedWriter bw;
	private Path outputPath;
	private String mappingInfo;
	private DecimalFormat dfSmall;
	private DecimalFormat dfLarge;
	
	public LaTexHandler() {
		this.dfSmall = new DecimalFormat(".###");
		this.dfLarge = new DecimalFormat("###.###");
	}
	
	@Override
	public void open() throws IOException {
		
	}
	
	private void init(List<Report> results) {
		try {
			this.bw.append("\\begin{table}[htb]");
			this.bw.newLine();
			this.bw.append("\\setlength{\\tabcolsep}{0.5em}");
			this.bw.newLine();
			this.bw.append("\\centering");
			this.bw.newLine();
			this.bw.append("\\scriptsize");
			this.bw.newLine();
			this.bw.append("\\begin{tabular}[tb]{lllp{2.3cm}");
			Report report = results.get(0);
			for(MetricGroupBinding binding : report) {
				for(@SuppressWarnings("unused") MetricBinding metricBinding : binding) {
					this.bw.append("l");
				}
				this.bw.append("l");
			}
			this.bw.append("l}");
			this.bw.newLine();
			this.bw.append("\\noalign{\\smallskip}\\hline\\noalign{\\smallskip}");
			this.bw.newLine();
			this.bw.append("\\multicolumn{3}{c}{\\textbf{Rank}}& \\textbf{Approach}  &");
			for(int i = 0; i < report.getBindings().size(); i++) {
				MetricGroupBinding groupBinding = report.getBindings().get(i);
				this.bw.append(" \\multicolumn{" + groupBinding.getBindings().size() 
						+ "}{c}{\\textbf{" + groupBinding.getGroup().getName() + "}}  & \\hspace*{1mm} ");
				if(i == report.getBindings().size()-1) {
					this.bw.append("\\\\");
				} else {
					this.bw.append(" &");
				}
			}
//			this.bw.append("\\multicolumn{3}{c}{\\textbf{Rank}}& \\textbf{Approach}  &"
//					+ " \\multicolumn{2}{c}{\\textbf{ProFM}}  & \\hspace*{1mm} & "
//					+ "\\multicolumn{2}{c}{\\textbf{ProP}} & \\hspace*{1mm} &"
//					+ "\\multicolumn{2}{c}{\\textbf{ProR}} & \\hspace*{1mm} &"
//					+ "\\multicolumn{1}{c}{\\textbf{Dist.}} & \\hspace*{1mm}\\\\ ");
			this.bw.newLine();
			this.bw.append("New & Old & $\\Delta$ & &");
			for(int i = 0; i < report.getBindings().size(); i++) {
				MetricGroupBinding groupBinding = report.getBindings().get(i);
				for(int j = 0; j < groupBinding.getBindings().size(); j++) {
					MetricBinding metricBinding = groupBinding.getBindings().get(j);
					this.bw.append(metricBinding.getMetric().getName());
					if(j != groupBinding.getBindings().size()-1) {
						this.bw.append(" & ");
					}
				}
				if(i == report.getBindings().size()-1) {
					this.bw.append("\\\\");
				} else {
					this.bw.append(" && ");
				}
			}
//			this.bw.append("New & Old & $\\Delta$ & & mic & mac  &&  mic  & mac && mic  & mac \\\\");
			this.bw.newLine();
			this.bw.append("\\noalign{\\smallskip}\\hline\\noalign{\\smallskip}");
			this.bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void appendReport(Report report, int rank) {
		try {
			this.bw.append(rank + " & 1 		& $\\pm$0 &" + report.getMatcher().getName() + "    	&	");
			for(int i = 0; i < report.getBindings().size(); i++) {
				MetricGroupBinding groupBinding = report.getBindings().get(i);
				for(int j = 0; j < groupBinding.getBindings().size(); j++) {
					MetricBinding metricBinding = groupBinding.getBindings().get(j);
					this.bw.append(this.dfSmall.format(metricBinding.getValue()));
					if(j != groupBinding.getBindings().size()-1) {
						this.bw.append(" & ");
					}
				}
				if(i == report.getBindings().size()-1) {
					this.bw.append("\\\\");
				} else {
					this.bw.append(" & & ");
				}
			}
			this.bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
//	private void table(List<TypeCharacteristic> characteristics, String name, int rank) {
//		String microPrecision = this.dfSmall.format(Characteristic.getNBPrecisionMicro(characteristics)).replaceAll(",", ".");
//		String macroPrecision = this.dfSmall.format(Characteristic.getNBPrecisionMacro(characteristics)).replaceAll(",", ".");
//		String microRecall = this.dfSmall.format(Characteristic.getNBRecallMicro(characteristics)).replaceAll(",", ".");
//		String macroRecall = this.dfSmall.format(Characteristic.getNBRecallMacro(characteristics)).replaceAll(",", ".");
//		String microFMeasure = this.dfSmall.format(Characteristic.getNBFMeasureMicro(characteristics)).replaceAll(",", ".");
//		String macroFMeasure = this.dfSmall.format(Characteristic.getNBFMeasureMacro(characteristics)).replaceAll(",", ".");
//		System.out.println(Characteristic.isFirstLineMatcher(characteristics) ? "FLM" : "SLM");
//		//TODO create corresponding constants
//		String relativeDistance = this.dfLarge.format(Characteristic.getRelativeDistance(characteristics, true)).replaceAll(",", ".");
////		String spearmanRangCorr = this.dfSmall.format(Characteristic.getSpearRangCorrGSMacro(characteristics)).replaceAll(",", ".");
//		if(name.contains("dataset")) {
//			String[] words = name.split("-");
//			name = "";
//			for (int i = 0; i < words.length-1; i++) {
//				name += words[i];
//				if(i < words.length-2) {
//					name += "-";
//				}
//			}
//		}
//		try {
//			this.bw.append(rank + " & 1 		& $\\pm$0 &" + name + "    	&	" + microFMeasure + " & " + macroFMeasure
//					+ "	&&	 	" + microPrecision + " & " + macroPrecision + " 		& &       "
//					+ "   " + microRecall + "  & " + macroRecall + " & & " + relativeDistance + "		  \\\\");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private void table2(List<Characteristic> characteristics, String name, int rank) {
//		String microPrecision = this.dfSmall.format(Characteristic.getNBPrecisionGSMicro(characteristics)).replaceAll(",", ".");
//		String macroPrecision = this.dfSmall.format(Characteristic.getNBPrecisionGSMacro(characteristics)).replaceAll(",", ".");
//		String microRecall = this.dfSmall.format(Characteristic.getNBRecallGSMicro(characteristics)).replaceAll(",", ".");
//		String macroRecall = this.dfSmall.format(Characteristic.getNBRecallGSMacro(characteristics)).replaceAll(",", ".");
//		String microFMeasure = this.dfSmall.format(Characteristic.getNBFMeasureGSMicro(characteristics)).replaceAll(",", ".");
//		String macroFMeasure = this.dfSmall.format(Characteristic.getNBFMeasureGSMacro(characteristics)).replaceAll(",", ".");
//		System.out.println(Characteristic.isFirstLineMatcher(characteristics) ? "FLM" : "SLM");
//		//TODO create corresponding constants
//		String relativeDistance = this.dfLarge.format(Characteristic.getRelativeDistanceGSMacro(characteristics, true)).replaceAll(",", ".");
////		String spearmanRangCorr = this.dfSmall.format(Characteristic.getSpearRangCorrGSMacro(characteristics)).replaceAll(",", ".");
//		if(name.contains("dataset")) {
//			String[] words = name.split("-");
//			name = "";
//			for (int i = 0; i < words.length-1; i++) {
//				name += words[i];
//				if(i < words.length-2) {
//					name += "-";
//				}
//			}
//		}
//		try {
//			this.bw.append(rank + " & 1 		& $\\pm$0 &" + name + "    	&	" + microFMeasure + " & " + macroFMeasure
//					+ "	&&	 	" + microPrecision + " & " + macroPrecision + " 		& &       "
//					+ "   " + microRecall + "  & " + macroRecall + " & & " + relativeDistance + "		  \\\\");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	private void bottom() {
		try {
			this.bw.append("\\end{tabular}");
			this.bw.newLine();
			this.bw.append("\\caption{Results " + mappingInfo + "}");
			this.bw.newLine();
			this.bw.append("\\label{tbl:results}");
			this.bw.newLine();
			this.bw.append("\\end{table}");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() throws IOException {
		
	}

	@Override
	public void setFlowListener(Consumer<String> listener) {
		
	}

	@Override
	public void setOutputPath(Path path) {
		this.outputPath = path;
	}

	@Override
	public void setMappingInfo(String info) {
		this.mappingInfo = info;
	}

	@Override
	public void receive(Evaluation evaluation) {
		try {
			System.out.println(evaluation.getGroupNames());
			System.out.println(evaluation.getThresholds());
			System.out.println(evaluation.getReports());
			for(String groupName : evaluation.getGroupNames()) {
				File dir = new File(this.outputPath + "/" + groupName);
				dir.mkdir();
				for(double threshold : evaluation.getThresholds()) {
					List<Report> results = evaluation.getReports(groupName, threshold);
					this.bw = Files.newBufferedWriter(Paths.get(this.outputPath + "/" 
									+ groupName + "/" + this.mappingInfo + "t-" + threshold+ FILE_TYPE));
					this.init(results);
					for(int i = 0; i < results.size(); i++) {
						Report report = results.get(i);
						System.out.println(report.getMatcher().getName());
						this.appendReport(report, i);
					}
					this.bottom();
					this.bw.flush();
					this.bw.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

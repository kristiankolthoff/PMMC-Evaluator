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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.ecs.html.TD;

import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.data.Evaluation;
import de.unima.ki.pmmc.evaluator.data.Report;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.generator.MetricBinding;
import de.unima.ki.pmmc.evaluator.generator.MetricGroupBinding;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;

public class LaTexHandlerType implements ReportHandler {

	public static final String FILE_TYPE = ".tex";
	
	private BufferedWriter bw;
	private Path outputPath;
	private String mappingInfo;
	private DecimalFormat dfSmall;
	private DecimalFormat dfLarge;
	
	public LaTexHandlerType() {
		this.dfSmall = new DecimalFormat(".###");
		this.dfLarge = new DecimalFormat("###.###");
	}
	
	public LaTexHandlerType(DecimalFormat format) {
		
	}
	
	@Override
	public void open() throws IOException {
		
	}
	
	private void init(List<Report> results) {
		try {
			this.bw.append("\\begin{table}[htb]");
			this.bw.newLine();
			this.bw.newLine();
			this.bw.append("\\setlength{\\tabcolsep}{0.4em}");
			this.bw.newLine();
			this.bw.newLine();
			this.bw.append("\\centering");
			this.bw.newLine();
			this.bw.newLine();
//			this.bw.append("\\scriptsize");
//			this.bw.newLine();
			this.bw.append("\\resizebox{\\linewidth}{!}{");
			this.bw.newLine();
			this.bw.newLine();
			String labels = "l";
//			for(int i = 0; i < CorrespondenceType.values().length-1; i++) {
//				labels += "llllll";
//			}
			Report report = results.get(0);
			for(@SuppressWarnings("unused") MetricGroupBinding binding : report) {
				labels += "llllll";
			}
			labels += "lll";
			this.bw.append("\\begin{tabular}[tb]{" + labels + "}");
			this.bw.newLine();
			this.bw.newLine();
			this.bw.append("\\noalign{\\smallskip}\\hline\\noalign{\\smallskip}");
			this.bw.newLine();
			this.bw.newLine();
			this.bw.append("\\textbf{Approach}  ");
			for(MetricGroupBinding binding : report) {
				this.bw.append(" & \\multicolumn{" + binding.getBindings().size() 
						+ "}{c}{\\textbf{" + binding.getGroup().getName() + "}} & ");
			}
//			for(int i = 0; i < CorrespondenceType.values().length; i++) {
//				CorrespondenceType type = CorrespondenceType.values()[i];
//				if(type != CorrespondenceType.DEFAULT) {
//					String cat = "";
//					for (int j = 0; j < i; j++) {
//						cat += "I";
//					}
//					String name = (type.getName().equals("trivial") || 
//							type.getName().equals("misc") || 
//							type.getName().equals("trivial-norm")) ? type.getName() : "Cat " + cat;
//					this.bw.append(" & \\multicolumn{5}{c}{\\textbf{" + name + "}} & ");
//				}
//			}
			this.bw.append("\\\\");
			this.bw.newLine();
			this.bw.newLine();
//			for(int i = 0; i < CorrespondenceType.values().length; i++) {
//				CorrespondenceType type = CorrespondenceType.values()[i];
//				if(type != CorrespondenceType.DEFAULT) {
//					String percentage = this.dfLarge.format((vals.get(type)/(double)sum)*100);
//					String and = (i == CorrespondenceType.values().length-1) ? "" : "&";
//					this.bw.append(" & \\multicolumn{5}{c}{" + "[" 
//					+ percentage + "\\%][" + vals.get(type) + "]" + "} " + and + " ");
//				}
//			}
			this.bw.append("\\\\");
			this.bw.newLine();
			this.bw.newLine();
			for(int i = 0; i < report.getBindings().size(); i++) {
				MetricGroupBinding binding = report.getBindings().get(i);
				if(i != 0) this.bw.append(" && ");
				else this.bw.append(" & ");
				for(int j = 0; j < binding.getBindings().size(); j++) {
					this.bw.append(binding.getBindings().get(j).getMetric().getName());
					if(j >= 0 && j < binding.getBindings().size()-1) this.bw.append(" & ");
//					this.bw.append("Prec  & Rec & FM & FP & FN");
				}
			}
//			for(int j = 0; j < CorrespondenceType.values().length; j++) {
//				CorrespondenceType type = CorrespondenceType.values()[j];
//				if(type != CorrespondenceType.DEFAULT) {
//					if(j != 0) this.bw.append(" && ");
//					else this.bw.append(" & ");
//					this.bw.append("Prec  & Rec & FM & FP & FN");
//				}
//			}
			this.bw.append(" \\\\");
			this.bw.newLine();
			this.bw.newLine();
			this.bw.append("\\noalign{\\smallskip}\\hline\\noalign{\\smallskip}");
			this.bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	private void initFNFP(List<Report> results) {
//		try {
//			this.bw.append("\\begin{table}[htb]");
//			this.bw.newLine();
//			this.bw.newLine();
//			this.bw.append("\\setlength{\\tabcolsep}{0.4em}");
//			this.bw.newLine();
//			this.bw.newLine();
//			this.bw.append("\\centering");
//			this.bw.newLine();
//			this.bw.newLine();
////			this.bw.append("\\scriptsize");
////			this.bw.newLine();
//			this.bw.append("\\resizebox{\\linewidth}{!}{");
//			this.bw.newLine();
//			this.bw.newLine();
//			String labels = "l";
//			for(int i = 0; i < CorrespondenceType.values().length-1; i++) {
//				labels += "lll";
//			}
//			labels += "lll";
//			this.bw.append("\\begin{tabular}[tb]{" + labels + "}");
//			this.bw.newLine();
//			this.bw.newLine();
//			this.bw.append("\\noalign{\\smallskip}\\hline\\noalign{\\smallskip}");
//			this.bw.newLine();
//			this.bw.newLine();
////			this.bw.append("\\textbf{Approach}  &"
////					+ " \\multicolumn{2}{c}{\\textbf{ProFM}}  & \\hspace*{1mm} & "
////					+ "\\multicolumn{2}{c}{\\textbf{ProP}} & \\hspace*{1mm} &"
////					+ "\\multicolumn{2}{c}{\\textbf{ProR}} & \\hspace*{1mm} &"
////					+ "\\multicolumn{1}{c}{\\textbf{Dist.}} & \\hspace*{1mm}\\\\ ");
//////					+ "&\\multicolumn{1}{c}{\\textbf{Rang}}\\\\ ");
//			//Compute size of types
//			Map<CorrespondenceType, Integer> vals = new HashMap<>();
//			for(CorrespondenceType type : CorrespondenceType.values()) {
//				vals.put(type, 0);
//			}
////			for(TypeCharacteristic tc : results.get(1).getTypeCharacteristics()) {
////				for(Correspondence c : tc.getAlignmentReference()) {
////					int curr = vals.get(c.getCType().get());
////					vals.put(c.getCType().get(), curr+1);
////				}
////			}
////			int sum = 0;
////			for(Map.Entry<CorrespondenceType, Integer> e : vals.entrySet()) {
////				sum += e.getValue();
////			}
//			this.bw.append("\\textbf{Approach}  ");
//			for(int i = 0; i < CorrespondenceType.values().length; i++) {
//				CorrespondenceType type = CorrespondenceType.values()[i];
//				if(type != CorrespondenceType.DEFAULT) {
//					String cat = "";
//					for (int j = 0; j < i; j++) {
//						cat += "I";
//					}
//					String name = (type.getName().equals("trivial") || 
//							type.getName().equals("misc") || 
//							type.getName().equals("trivial-norm")) ? type.getName() : "Cat " + cat;
//					this.bw.append(" & \\multicolumn{2}{c}{\\textbf{" + name + "}} & ");
//				}
//			}
//			this.bw.append("\\\\");
//			this.bw.newLine();
//			this.bw.newLine();
////			for(int i = 0; i < CorrespondenceType.values().length; i++) {
////				CorrespondenceType type = CorrespondenceType.values()[i];
////				if(type != CorrespondenceType.DEFAULT) {
////					String percentage = this.dfLarge.format((vals.get(type)/(double)sum)*100);
////					String and = (i == CorrespondenceType.values().length-1) ? "" : "&";
////					this.bw.append(" & \\multicolumn{2}{c}{" + "[" 
////					+ percentage + "\\%][" + vals.get(type) + "]" + "} " + and + " ");
////				}
////			}
//			this.bw.append("\\\\");
//			this.bw.newLine();
//			this.bw.newLine();
//			for(int j = 0; j < CorrespondenceType.values().length; j++) {
//				CorrespondenceType type = CorrespondenceType.values()[j];
//				if(type != CorrespondenceType.DEFAULT) {
//					if(j != 0) this.bw.append(" && ");
//					else this.bw.append(" & ");
//					this.bw.append("FP & FN");
//				}
//			}
//			this.bw.append(" \\\\");
//			this.bw.newLine();
//			this.bw.newLine();
//			this.bw.append("\\noalign{\\smallskip}\\hline\\noalign{\\smallskip}");
//			this.bw.newLine();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
//	private void tableFNFP(List<Characteristic> characteristics, String name, int rank) {
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
//			this.bw.append(name);
//			for(int i = 0; i < CorrespondenceType.values().length; i++) {
//				CorrespondenceType type = CorrespondenceType.values()[i];
//				if(type != CorrespondenceType.DEFAULT) {
////					String typeCount = String.valueOf(mapTypeCount.get(type));
////					String typePrecsion = this.dfSmall.format(TypeCharacteristic.getPrecisionMacro(characteristics, type)).replace(",", ".");
////					String typeRecall = this.dfSmall.format(TypeCharacteristic.getRecallMacro(characteristics, type)).replace(",", ".");
////					String typeFMeasure = this.dfSmall.format(TypeCharacteristic.getFMeasureMacro(characteristics, type)).replace(",", ".");
//					int fp = TypeCharacteristic.getFPSum(characteristics, type);
//					int fn = TypeCharacteristic.getFNSum(characteristics, type);
//					if(i != 0) this.bw.append(" && ");
//					else this.bw.append(" & ");
//					this.bw.append(fp + " & " + fn);
//				}
//			}
//			this.bw.append("\\\\");
//			this.bw.newLine();
//			this.bw.newLine();
////			this.bw.append(rank + " & 1 		& $\\pm$0 &" + name + "    	&	" + microFMeasure + " & " + macroFMeasure
////					+ "	&&	 	" + microPrecision + " & " + macroPrecision + " 		& &       "
////					+ "   " + microRecall + "  & " + macroRecall + " & & " + relativeDistance + "		  \\\\");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
//	private void table(List<TypeCharacteristic> characteristics, String name, int rank) {
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
//			this.bw.append(name);
//			for(int i = 0; i < CorrespondenceType.values().length; i++) {
//				CorrespondenceType type = CorrespondenceType.values()[i];
//				if(type != CorrespondenceType.DEFAULT) {
////					String typeCount = String.valueOf(mapTypeCount.get(type));
//					String typePrecsion = this.dfSmall.format(TypeCharacteristic.getPrecisionMacro(characteristics, type)).replace(",", ".");
//					String typeRecall = this.dfSmall.format(TypeCharacteristic.getRecallMacro(characteristics, type)).replace(",", ".");
//					String typeFMeasure = this.dfSmall.format(TypeCharacteristic.getFMeasureMacro(characteristics, type)).replace(",", ".");
//					int fp = TypeCharacteristic.getFPSum(characteristics, type);
//					int fn = TypeCharacteristic.getFNSum(characteristics, type);
//					if(i != 0) this.bw.append(" && ");
//					else this.bw.append(" & ");
//					this.bw.append(typePrecsion + " & " + typeRecall + " & " + typeFMeasure + " & " + fp + " & " + fn);
//				}
//			}
//			this.bw.append("\\\\");
//			this.bw.newLine();
//			this.bw.newLine();
////			this.bw.append(rank + " & 1 		& $\\pm$0 &" + name + "    	&	" + microFMeasure + " & " + macroFMeasure
////					+ "	&&	 	" + microPrecision + " & " + macroPrecision + " 		& &       "
////					+ "   " + microRecall + "  & " + macroRecall + " & & " + relativeDistance + "		  \\\\");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
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
			this.bw.append("\\noalign{\\smallskip}\\hline\\noalign{\\smallskip}");
			this.bw.newLine();
			this.bw.newLine();
			this.bw.append("\\end{tabular}");
			this.bw.newLine();
			this.bw.newLine();
			this.bw.append("}");
			this.bw.newLine();
			this.bw.newLine();
			this.bw.append("\\caption{Results " + mappingInfo + "}");
			this.bw.newLine();
			this.bw.newLine();
			this.bw.append("\\label{tbl:results}");
			this.bw.newLine();
			this.bw.newLine();
			this.bw.append("\\end{table}");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void receive(Evaluation evaluation) {
//		try {
//			this.bw = Files.newBufferedWriter(Paths.get(this.outputPath + "/" + this.mappingInfo + "-type" + FILE_TYPE));
//			Collections.sort(results);
//			this.initFNFP(results);
//			for(int i = 0; i < results.size(); i++) {
//				System.out.println(results.get(i).getName());
//				this.tableFNFP(results.get(i).getTypeCharacteristics(), results.get(i).getName(), (i+1));
//			}
//			this.bottom();
//			this.bw.flush();
//			this.bw.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
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
						this.appendReport(report, i+1);
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

	private void appendReport(Report report, int rank) {
		try {
			this.bw.append(report.getMatcher().getName() + "    	&	");
			for(int i = 0; i < report.getBindings().size(); i++) {
				MetricGroupBinding groupBinding = report.getBindings().get(i);
				for(int j = 0; j < groupBinding.getBindings().size(); j++) {
					MetricBinding metricBinding = groupBinding.getBindings().get(j);
					this.bw.append(this.dfSmall.format(metricBinding.getValue()));
//					this.bw.append(String.valueOf(metricBinding.getValue()).split("\\.")[0]);
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
			this.bw.newLine();
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

}

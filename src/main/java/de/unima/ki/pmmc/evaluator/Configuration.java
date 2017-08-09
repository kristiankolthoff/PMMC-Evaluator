package de.unima.ki.pmmc.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReader;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.data.Result;
import de.unima.ki.pmmc.evaluator.handler.ResultHandler;
import de.unima.ki.pmmc.evaluator.metrics.Metric;
import de.unima.ki.pmmc.evaluator.metrics.MetricGroup;
import de.unima.ki.pmmc.evaluator.metrics.MetricGroupFactory;
import de.unima.ki.pmmc.evaluator.metrics.standard.PrecisionMacro;
import de.unima.ki.pmmc.evaluator.metrics.standard.PrecisionMicro;
import de.unima.ki.pmmc.evaluator.metrics.standard.PrecisionStdDev;
import de.unima.ki.pmmc.evaluator.metrics.statistics.MinimumConfidence;
import de.unima.ki.pmmc.evaluator.metrics.types.PrecisionMacroType;
import de.unima.ki.pmmc.evaluator.metrics.types.PrecisionMicroType;
import de.unima.ki.pmmc.evaluator.metrics.types.PrecisionStdDevType;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;
import de.unima.ki.pmmc.evaluator.model.parser.ParserFactory;


public class Configuration implements Iterable<MetricGroup>{

	private List<MetricGroup> metricGroups;
	private boolean persistToFile;
	private boolean debugOn;
	private String goldstandardPath;
	private Optional<String> matchersRootPath;
	private String modelsRootPath;
	private String outputPath;
	private String outputName;
	private List<String> matcherPaths;
	private List<String> modelPaths;
	private List<Double> thresholds;
	private AlignmentReader alignmentReader;
	private List<ResultHandler> handler;
	private Consumer<String> flowListener;
	private List<Function<Result, Result>> transformationsResult;
	private List<Function<Correspondence, Correspondence>> transformationsCorrespondence;
	private List<Function<Alignment, Alignment>> transformationsAlignment;
	private List<Predicate<Result>> filterResult;
	private List<Predicate<Correspondence>> filterCorrespondence;
	private List<Predicate<Alignment>> filterAlignment;
	private Parser parser;
	
	
	public Configuration(List<MetricGroup> metricGroups, 
			boolean persistToFile, String goldstandardPath,
			Optional<String> matchersRootPath, 
			String modelsRootPath, String outputPath, 
			String outputName,
			List<String> matcherPaths, 
			List<String> modelPaths, List<Double> thresholds,
			boolean debugOn,
			AlignmentReader alignmentReader,
			List<ResultHandler> handler, Consumer<String> flowListener,
			List<Function<Result, Result>> transformationsResult,
			List<Function<Correspondence, Correspondence>> transformationsCorrespondence,
			List<Function<Alignment, Alignment>> transformationsAlignment, 
			List<Predicate<Result>> filterResult,
			List<Predicate<Correspondence>> filterCorrespondence, 
			List<Predicate<Alignment>> filterAlignment,
			Parser parser) {
		this.metricGroups = metricGroups;
		this.persistToFile = persistToFile;
		this.goldstandardPath = goldstandardPath;
		this.matchersRootPath = matchersRootPath;
		this.modelsRootPath = modelsRootPath;
		this.outputPath = outputPath;
		this.outputName = outputName;
		this.matcherPaths = matcherPaths;
		this.modelPaths = modelPaths;
		this.thresholds = thresholds;
		this.debugOn = debugOn;
		this.alignmentReader = alignmentReader;
		this.handler = handler;
		this.flowListener = flowListener;
		this.transformationsResult = transformationsResult;
		this.transformationsCorrespondence = transformationsCorrespondence;
		this.transformationsAlignment = transformationsAlignment;
		this.filterResult = filterResult;
		this.filterCorrespondence = filterCorrespondence;
		this.filterAlignment = filterAlignment;
		this.parser = parser;
	}

	public boolean isPersistToFile() {
		return persistToFile;
	}

	public void setPersistToFile(boolean persistToFile) {
		this.persistToFile = persistToFile;
	}
	
	@Override
	public Iterator<MetricGroup> iterator() {
		return metricGroups.iterator();
	}
	
	
	public List<MetricGroup> getMetricGroups() {
		return metricGroups;
	}

	public void setMetricGroups(List<MetricGroup> metricGroups) {
		this.metricGroups = metricGroups;
	}

	public String getGoldstandardPath() {
		return goldstandardPath;
	}

	public void setGoldstandardPath(String goldstandardPath) {
		this.goldstandardPath = goldstandardPath;
	}

	public Optional<String> getMatchersRootPath() {
		return matchersRootPath;
	}

	public void setMatchersRootPath(Optional<String> matchersRootPath) {
		this.matchersRootPath = matchersRootPath;
	}

	public String getModelsRootPath() {
		return modelsRootPath;
	}

	public void setModelsRootPath(String modelsRootPath) {
		this.modelsRootPath = modelsRootPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}

	public List<String> getMatcherPaths() {
		return matcherPaths;
	}

	public void setMatcherPaths(List<String> matcherPaths) {
		this.matcherPaths = matcherPaths;
	}

	public List<String> getModelPaths() {
		return modelPaths;
	}

	public void setModelPaths(List<String> modelPaths) {
		this.modelPaths = modelPaths;
	}

	public List<Double> getThresholds() {
		return thresholds;
	}

	public void setThresholds(List<Double> thresholds) {
		this.thresholds = thresholds;
	}

	public boolean isDebugOn() {
		return debugOn;
	}

	public void setDebugOn(boolean debugOn) {
		this.debugOn = debugOn;
	}

	public AlignmentReader getAlignmentReader() {
		return alignmentReader;
	}

	public void setAlignmentReader(AlignmentReader alignmentReader) {
		this.alignmentReader = alignmentReader;
	}

	public List<ResultHandler> getHandler() {
		return handler;
	}

	public void setHandler(List<ResultHandler> handler) {
		this.handler = handler;
	}

	public Consumer<String> getFlowListener() {
		return flowListener;
	}

	public void setFlowListener(Consumer<String> flowListener) {
		this.flowListener = flowListener;
	}

	public List<Function<Result, Result>> getTransformationsResult() {
		return transformationsResult;
	}

	public void setTransformationsResult(List<Function<Result, Result>> transformationsResult) {
		this.transformationsResult = transformationsResult;
	}

	public List<Function<Correspondence, Correspondence>> getTransformationsCorrespondence() {
		return transformationsCorrespondence;
	}

	public void setTransformationsCorrespondence(
			List<Function<Correspondence, Correspondence>> transformationsCorrespondence) {
		this.transformationsCorrespondence = transformationsCorrespondence;
	}

	public List<Function<Alignment, Alignment>> getTransformationsAlignment() {
		return transformationsAlignment;
	}

	public void setTransformationsAlignment(List<Function<Alignment, Alignment>> transformationsAlignment) {
		this.transformationsAlignment = transformationsAlignment;
	}

	public List<Predicate<Result>> getFilterResult() {
		return filterResult;
	}

	public void setFilterResult(List<Predicate<Result>> filterResult) {
		this.filterResult = filterResult;
	}

	public List<Predicate<Correspondence>> getFilterCorrespondence() {
		return filterCorrespondence;
	}

	public void setFilterCorrespondence(List<Predicate<Correspondence>> filterCorrespondence) {
		this.filterCorrespondence = filterCorrespondence;
	}

	public List<Predicate<Alignment>> getFilterAlignment() {
		return filterAlignment;
	}

	public void setFilterAlignment(List<Predicate<Alignment>> filterAlignment) {
		this.filterAlignment = filterAlignment;
	}

	public Parser getParser() {
		return parser;
	}

	public void setParser(Parser parser) {
		this.parser = parser;
	}


	public static class Builder {
		
		private List<MetricGroup> metricGroups;
		private boolean persistToFile;
		private boolean debugOn;
		private boolean sort;
		private String goldstandardPath;
		private Optional<String> matchersRootPath;
		private String modelsRootPath;
		private String outputPath;
		private String outputName;
		private List<String> matcherPaths;
		private List<String> modelPaths;
		private List<Double> thresholds;
		private AlignmentReader alignmentReader;
		private List<ResultHandler> handler;
		private Consumer<String> flowListener;
		private List<Function<Result, Result>> transformationsResult;
		private List<Function<Correspondence, Correspondence>> transformationsCorrespondence;
		private List<Function<Alignment, Alignment>> transformationsAlignment;
		private List<Predicate<Result>> filterResult;
		private List<Predicate<Correspondence>> filterCorrespondence;
		private List<Predicate<Alignment>> filterAlignment;
		private Parser parser;
		
		public Builder() {
			this.metricGroups = new ArrayList<>();
		}
		
		public Builder addMetric(Metric metric) {
			metricGroups.add(new MetricGroup("unnamed", metric));
			return this;
		}
		
		public Builder addMetrics(String name, Metric... metrics) {
			metricGroups.add(new MetricGroup(name, metrics));
			return this;
		}
		
		
		public Builder addMetricGroup(MetricGroup metricGroup) {
			metricGroups.add(metricGroup);
			return this;
		}
		
		public Builder removeMetrics() {
			metricGroups.clear();
			return this;
		}
		
		public Builder persistToFile(boolean persist) {
			this.persistToFile = persist;
			return this;
		}
		
		/**
		 * Adds a <code>ResultHandler</code> which is used
		 * as a sink for the computed <code>Result</code>s.
		 * @param handler the handler which should be added
		 * @return this
		 */
		public Builder addHandler(ResultHandler handler) {
			this.handler.add(handler);
			return this;
		}
		
		/**
		 * Removes a <code>ResultHandler</code> from the current
		 * collection of handlers.
		 * @param handler the handler which should be removed
		 * @return this
		 */
		public Builder removeHandler(ResultHandler handler) {
			for (int i = 0; i < this.handler.size(); i++) {
				if(this.handler.get(i).getClass().getName().equals(handler.getClass().getName())) {
					this.handler.remove(i);
					return this;
				}
			}
			return this;
		}
		
		
		/**
		 * Adds a root path to alignments of a matcher.
		 * The files within this folder should be actual
		 * RDF alignment files.
		 * @param path the root path to the alignments of a single matcher
		 * @return this
		 */
		public Builder addMatcherPath(String path) {
			this.matcherPaths.add(path);
			return this;
		}
		
		/**
		 * Removes a root path to alignments of a matcher from
		 * the collection.
		 * @param path the root path which should be removed
		 * @return this
		 */
		public Builder removeMatcherPath(String path) {
			for (int i = 0; i < matcherPaths.size(); i++) {
				if(matcherPaths.get(i).equals(path)) {
					matcherPaths.remove(i);
					return this;
				}
			}
			return this;
		}
		
		/**
		 * Adds the root path of the source folder
		 * containing the actual <code>Model</code>s
		 * which were used to generate the alignments.
		 * @param path the root path of the actual models
		 * @return this
		 */
		public Builder addModelPath(String path) {
			this.modelPaths.add(path);
			return this;
		}
		
		/**
		 * Removes the root path to the <code>Model</code>s.
		 * @param path the root path of the actual models
		 * @return this
		 */
		public Builder removeModelPath(String path) {
			for (int i = 0; i < modelPaths.size(); i++) {
				if(modelPaths.get(i).equals(path)) {
					modelPaths.remove(i);
					return this;
				}
			}
			return this;
		}
		
		/**
		 * If debug mode is on, prints log information to the console
		 * about the evaluation progress.
		 * @param debugOn if true logs progress, else silent
		 * @return this
		 */
		public Builder setDebugOn(boolean debugOn) {
			this.debugOn = debugOn;
			return this;
		}

		/**
		 * Adds custom <code>Result</code> transformation. This transformation
		 * is applied to each generated <code>Result</code> instance of a matcher.
		 * @param transformation which should be applied to the generated results
		 * @return this
		 */
		public Builder addTransformationToResult(Function<Result, Result> transformation) {
			this.transformationsResult.add(transformation);
			return this;
		}
		
		/**
		 * Adds custom <code>Correspondence</code> transformation. This transformation
		 * is applied to each generated <code>Correspondence</code> instances of a matcher.
		 * @param transformation which should be applied to the generated correspondences
		 * @return this
		 */
		public Builder addTransformationToCorrespondence(Function<Correspondence, Correspondence> transformation) {
			this.transformationsCorrespondence.add(transformation);
			return this;
		}
		
		/**
		 * Adds custom <code>Alignment</code> transformation. This transformation
		 * is applied to each generated <code>Alignment</code> instance of a matcher.
		 * @param transformation which should be applied to the generated alignments
		 * @return this
		 */
		public Builder addTransformationToAlignment(Function<Alignment, Alignment> transformation) {
			this.transformationsAlignment.add(transformation);
			return this;
		}
		
		/**
		 * Adds custom <code>Result</code> filter. This filter
		 * is applied to each generated <code>Result</code> instance of a matcher,
		 * and filters the <code>Result</code> based on the filter predicate.
		 * @param filter which should be applied to the generated results
		 * @return this
		 */
		public Builder addFilterToResult(Predicate<Result> filter) {
			this.filterResult.add(filter);
			return this;
		}
		
		/**
		 * Adds custom <code>Correspondence</code> filter. This filter
		 * is applied to each <code>Correspondence</code> instance of a matcher,
		 * and filters the <code>Correspondence</code> based on the filter predicate.
		 * @param filter which should be applied to the generated correspondences
		 * @return this
		 */
		public Builder addFilterToCorrespondence(Predicate<Correspondence> filter) {
			this.filterCorrespondence.add(filter);
			return this;
		}
		
		/**
		 * Adds custom <code>Alignment</code> filter. This filter
		 * is applied to each generated <code>Alignment</code> instance of a matcher,
		 * and filters the <code>Alignment</code> based on the filter predicate.
		 * @param filter which should be applied to the generated alignments
		 * @return this
		 */
		public Builder addFilterToAlignment(Predicate<Alignment> filter) {
			this.filterAlignment.add(filter);
			return this;
		}
		
		/**
		 * Set root path of goldstandard alignment
		 * @param goldstandardPath the path to the goldstandard alignments
		 * @return this
		 */
		public Builder setGoldstandardPath(String goldstandardPath) {
			this.goldstandardPath = goldstandardPath;
			return this;
		}
		
		/**
		 * Set root path for alignments of multiple matchers. Used
		 * to automatically extract the name of a matcher and its
		 * <code>Alignment</code>s corresponding to the <code>Alignment</code>s
		 * from the goldstandard.
		 * @param matchersPath root path to matcher alignments
		 * @return this
		 */
		public Builder setMatchersRootPath(String matchersPath) {
			this.matchersRootPath = Optional.of(matchersPath);
			return this;
		}
		
		/**
		 * Set root path to the acutal RDF models used for generating
		 * the alignments.
		 * @param modelsPath the path to the RDF files of the acutal <code>Model</code>s
		 * @return this
		 */
		public Builder setModelsRootPath(String modelsPath) {
			this.modelsRootPath = modelsPath;
			return this;
		}

		/**
		 * Sets the prefix of the generated <code>Result</code>s
		 * @param outputName the name
		 * @return this
		 */
		public Builder setOutputName(String outputName) {
			this.outputName = outputName;
			return this;
		}
		
		/**
		 * Used to process current evalation events.
		 * @param listener the listener which should be used
		 * @return this
		 */
		public Builder setFlowListener(Consumer<String> listener) {
			this.flowListener = listener;
			return this;
		}

		/**
		 * Sets the path for the output which should be
		 * used by corresponding <code>ResultHandler</code>s.
		 * @param outputPath the output path
		 * @return this
		 */
		public Builder setOutputPath(String outputPath) {
			this.outputPath = outputPath;
			return this;
		}

		public Builder setThresholds(List<Double> thresholds) {
			this.thresholds = thresholds;
			return this;
		}
		
		public Builder setThresholds(Double[] thresholds) {
			this.thresholds = Arrays.asList(thresholds);
			return this;
		}
		
		public Builder addThreshold(double threshold) {
			this.thresholds.add(threshold);
			return this;
		}
		
		public Builder removeThreshold(double threshold) {
			for (int i = 0; i < thresholds.size(); i++) {
				if(thresholds.get(i) == threshold) {
					thresholds.remove(i);
					return this;
				}
			}
			return this;
		}
		

		/**
		 * Set the type of the <code>Parser</code> which should be used
		 * to read the model files.
		 * @param parsertype the parser type
		 * @return this
		 */
		public Builder setParser(String parsertype) {
			this.parser = ParserFactory.getParser(parsertype);
			return this;
		}

		/**
		 * Set the <code>AlignmentReader</code> implementation that should
		 * be used to read the RDF alignments.
		 * @param alignmentReader the alignment reader for parsing RDF files
		 * @return this
		 */
		public Builder setAlignmentReader(AlignmentReader alignmentReader) {
			this.alignmentReader = alignmentReader;
			return this;
		}
		
		public Configuration build() {
			return new Configuration(metricGroups, 
					persistToFile, 
					goldstandardPath, 
					matchersRootPath, 
					modelsRootPath, 
					outputPath, 
					outputName, 
					matcherPaths, 
					modelPaths, 
					thresholds, 
					debugOn, 
					alignmentReader,
					handler, 
					flowListener, 
					transformationsResult, 
					transformationsCorrespondence, 
					transformationsAlignment, 
					filterResult, 
					filterCorrespondence, 
					filterAlignment, 
					parser);
		}
	}
	
	public static void main(String[] args) {
		MetricGroupFactory factory = MetricGroupFactory.getInstance();
		Configuration.Builder builder = new Configuration.Builder().
				addMetricGroup(new MetricGroup("recall", "recall info")
						.addMetric(new PrecisionMicro())
						.addMetric(new PrecisionMacro())
						.addMetric(new PrecisionStdDev()))
				.addMetricGroup(factory.create(MetricGroup.PRECISION_GROUP))
				.addMetric(new PrecisionMacro())
				.addMetrics("min-conf", new MinimumConfidence())
				.persistToFile(true);
		for(CorrespondenceType type : CorrespondenceType.values()) {
			builder.addMetricGroup(new MetricGroup(type.getName())
					.addMetric(new PrecisionMacroType(type))
					.addMetric(new PrecisionMicroType(type))
					.addMetric(new PrecisionStdDevType(type)));
		}
		@SuppressWarnings("unused")
		Evaluator evaluator = new Evaluator(builder.build());
	}

}

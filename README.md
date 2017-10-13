# PMMC-Evaluator
This project provides an API for evaluating process model matchers with a variety of different metrics
and is used to generate the results for the official Process Model Matching Contest (PMMC). An evaluation experiment
is easily setup by using the builder pattern to increasingly build up a configuration for an evaluation. This Evaluation
instance forms the basis for any experiment.

# Example

For exploring the features and capabilities of the library, we use a concise example in the following. Assume that we have three matcher results for a given process matching problem. In addition, we have three goldstandards which form two goldstandard groups: GS1 and GS2 should form one group and the metric results should be averaged over both goldstandards, and finally GS3 forms an individual group. Given a single matcher, we want to compute the metric results for each goldstandard group individually. Note that we also want to apply different thresholds to the goldstandard groups and generate metric results for each thresholded goldstandard group. For the given evaluation, we use two thresholds t1 and t2. The following figure illustrates all components we described previously. 

![alt tag](https://raw.githubusercontent.com/kristiankolthoff/PMMC-Evaluator/master/src/main/resources/images/overview.png)

To start with, first we create a `Configuration` which can be used to specify e.g. the metrics that should be used and allows you to group the individual metrics.

```java
Configuration.Builder builder = new Configuration.Builder().
			addMetricGroup(new MetricGroup("Precision", "prec-info")
					.addMetric(new NBPrecisionMicro())
					.addMetric(new NBPrecisionMacro())
					.addMetric(new NBPrecisionStdDev()))
```

To start with, create a Evaluator Builder and add the
path to the RDF files of the goldstandard. Then add the 
path to the corresponding RDF alignment of the matcher and
set the parameters for the generated output like the name and
the output path. This API ships with direct support for alignments
in RDF-XML.

```java
Evaluator evaluator = new Evaluator.Builder().
				setGoldstandardPath(GOLDSTANDARD_PATH).
				addMatcherPath("src/main/resources/data/results/OAEI16/AML-PM/").
				setOutputPath(OUTPUT_PATH).
				setOutputName("output-name").
				setAlignmentReader(new AlignmentReaderXml()).
				build();
```
If you want to evaluate multiple matcher outputs at once,
Evaluator comes with matcher output search. That is, only
specify the root path of the outputs and Evaluator will
automatically detect the multiple outputs and load them.



```java
Evaluator evaluator = new Evaluator.Builder().
				setGoldstandardPath(GOLDSTANDARD_PATH).
				setMatchersRootPath(RESULTS_PATH).
				setOutputPath(OUTPUT_PATH).
				setOutputName("output-name").
				setAlignmentReader(new AlignmentReaderXml()).
				build();
```

The Evaluator also supports thresholding of experiments, which can
be simply added to the Evaluator. If now threshold is set explicitly,
a threshold of 0.0 is used implicitly.

```java
Evaluator evaluator = new Evaluator.Builder().
				setGoldstandardPath(GOLDSTANDARD_PATH).
				addMatcherPath("src/main/resources/data/results/OAEI16/AML-PM/").
				setOutputPath(OUTPUT_PATH).
				setOutputName("output-name").
				addThreshold(0.0).
				addThreshold(0.5).
				addThreshold(0.75).
				setAlignmentReader(new AlignmentReaderXml()).
				build();
```

To receive concise output of the experiments,
the Evaluator ships with different result handlers.
For a convienient overview of the results in the Browser,
you can use the HTMLHandler. Also the Evaluator ships with
some standard data format result handlers, like XML and JSON.

```java
Evaluator evaluator = new Evaluator.Builder().
				setGoldstandardPath(GOLDSTANDARD_PATH).
				addMatcherPath("src/main/resources/data/results/OAEI16/AML-PM/").
				setOutputPath(OUTPUT_PATH).
				setOutputName("output-name").
				addHandler(new HTMLHandler(SHOW_IN_BROSWER));
				addHandler(new JSONHandler());
				setAlignmentReader(new AlignmentReaderXml()).
				build();
```

Of course, you can also programmatically receive the results 
from the Evaluator and do additional processing of the data.
After successfully building the Evaluator instance, you start
the process with the following call.

```java
evaluator.run();
```


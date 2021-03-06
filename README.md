# PMMC-Evaluator

This project provides an API for evaluating process model matchers with a variety of different metrics
and is used to generate the results for the official Process Model Matching Contest (PMMC) conducted in 2016 [[1](https://github.com/kristiankolthoff/PMMC-Evaluator#publications)] and 2017 [[2](https://github.com/kristiankolthoff/PMMC-Evaluator#publications)]. This contest is about finding pairwise matching tasks or activities (denoted as correspondences) in comparison of two given process models. In particular, for this contest three different datasets are used all having different notation form. The first set of process models describes the admission process at various universities and is represented in [BPMN](http://www.bpmn.org/). The second set of process models describe the process of creating birth certificates and uses [Petri-Nets](https://en.wikipedia.org/wiki/Petri_net). The third and last set of process models describe the basic process of managing assets and is represented in [EPC](https://en.wikipedia.org/wiki/Event-driven_process_chain). For evaluating the submitted matchers, we construct various evaluation settings and compute the corresponding metrics. An evaluation process is easily setup by using the builder pattern to increasingly build up a configuration for an evaluation. This `Evaluation` instance forms the basis for any experiment.

## Example

For exploring the features and capabilities of the library, we use a concise example in the following. Assume that we have three matcher results for a given process matching problem. In addition, we have three goldstandards which form two goldstandard groups: GS1 and GS2 should form one group and the metric results should be averaged over both goldstandards, and finally GS3 forms an individual group. Given a single matcher, we want to compute the metric results for each goldstandard group individually. Note that we also want to apply different thresholds to the goldstandard groups and generate metric results for each thresholded goldstandard group. For the given evaluation, we use two thresholds `t1` and `t2`. The following figure illustrates all components we described previously. 

![alt tag](https://raw.githubusercontent.com/kristiankolthoff/PMMC-Evaluator/master/src/main/resources/images/overview.png)

### a.) Setting Up The Evaluation Process

To start with, first we create a `Configuration.Builder` which can be used to specify all the different settings for the evaluation to conduct e.g. define the `GoldstandardGroup`s, the matcher results and the `Metric`s that should be computed grouped into several `MetricGroup`s. In the following, we will walk through some of the possible settings. After creating the `Configuration.Builder` we add two `GoldstandardGroup`s and provide the corresponding directory paths of the included goldstandards. Instead of providing the group name and the goldstandard paths, you can also directly add a `GoldstandardGroup` instance. Note that we also add the two thresholds `t1` and `t2` two the our `Configuration`.

```java
Configuration.Builder builder = new Configuration.Builder().
			.addGoldstandardGroup("GSGroup1", gs1_path, gs2_path)
			.addGoldstandardGroup("GSGroup2", gs3_path)
			.addThreshold(t1).addThreshold(t2)
			.addMatcherPath(m1_path)
			.addMatcherPath(m2_path)
			.addMatcherPath(m3_path);
```
In addition, we also specify the paths to the generated matcher results we want to evaluate against our `GoldstandardGroup`s. This notation can be tedious if you have many matchers that you want to evaluate in parallel, hence you can also simply use `builder.setMatchersRootPath(matchersRootPath)` and provide the main directory path where all the matcher results are stored in. This will recursively search through all subdirectories, detect matcher results and load them named properly into the framework for further usage.

Depending on the `Alignment` format you use in you special matching evaluation, you also need to provide a corresponding `AlignmentReader` implementation. The framework ships with preimplemented `AligmentReaderXML` which is most commonly used but also provides an `AlignmentReaderTxt` which lets you read alignments that are provided in a simple TextFile form.

```java
//For commonly used alignments in XML
builder.setAlignmentReader(new AlignmentReaderXml());
//For simple textfile-based alignments
builder.setAlignmentReader(new AlignmentReaderTxt());
```

### b.) Adding Evaluation Metrics

So for we provided the basic `Configuration` parameters to implement the evaluation process as depicted in the figure above. Since we want to evaluate specific characteristics of the process model matchers, we need to add the metrics we are interested in to our `Configuration`. Note that you can add individual `Metric` instances directly, however, the preferred way is to use a `MetricGroup` instance to add several `Metrics` to and then add the created `MetricGroup` to the `Configuration.Builder`. For example, assume that we want to have a `MetricGroup` for precision that nicely groups the basic metric components like the micro-precision, macro-precision and its precision standard deviation. 

```java
builder.addMetricGroup(new MetricGroup("Precision", "prec-info")
				.addMetric(new PrecisionMicro())
				.addMetric(new PrecisionMacro())
				.addMetric(new PrecisionStdDev()));
```

Assume we want to add a similar group of metrics for recall consisting of the micro-recall, macro-recall and its recall standard deviation, we simply add the following calls on the `Configuration.Builder`.


```java
builder.addMetricGroup(new MetricGroup("Recall", "rec-info")
				.addMetric(new RecallMicro())
				.addMetric(new RecallMacro())
				.addMetric(new RecallStdDev()));
```

The framework ships with many important preimplemented `Metric`s which are also used in the process model matching contest. Among the metrics, we implemented [standard](https://github.com/kristiankolthoff/PMMC-Evaluator/tree/master/src/main/java/de/unima/ki/pmmc/evaluator/metrics/standard) metrics including Precision, Recall, F-Measure and many more. All of these metrics are also availabe as [type](https://github.com/kristiankolthoff/PMMC-Evaluator/tree/master/src/main/java/de/unima/ki/pmmc/evaluator/metrics/types) metrics in order to compute the metrics for specific subsets of correspondence types. The idea behind correspondence typing is explained later in this tutorial. Finally, we also implemented many simple [statistic](https://github.com/kristiankolthoff/PMMC-Evaluator/tree/master/src/main/java/de/unima/ki/pmmc/evaluator/metrics/statistics) metrics e.g. the mean size of the generated alignments and the number of FPs. In addition to the presented standard metrics, it also ships with the non-binary versions of precision, recall and f-measure as described in [[3](https://github.com/kristiankolthoff/PMMC-Evaluator#publications)]. However, if you want to compute custom metric functions that are not provided by the framework directly, you can use an instance of `FunctionMetric(Function<List<Characteristic>, Double>> function)` and provide a function that takes as input a list of characteristics and computes a single double value as the metrics final result value. In the following example we simply compute the TP (true positives) as a `FunctionMetric` and add it to the `Configuration.Builder`.


```java
builder.addMetricGroup(new MetricGroup("custom", "custom-info")
				.addMetric(new FunctionMetric(list -> 
				{return (double) list.stream()
					.mapToInt(c -> {return c.getAlignmentCorrect().size();})
				        .max().getAsInt();})));			
```

### c.) Report Generation

After specifying the overall evaluation process involving all goldstandards and matcher results we want to evaluate on, and also providing the metrics to compute, we finally want to access the evaluation results. The easiest and most convienient way of doing so, is to add a `ReportHandler` to the `Configuration.Builder`. Each of the assigned `ReportHandler`s will receive the evaluation results and transform it into a particular representation. The framework ships with many different handlers which are described in the following.

```java
//Print results in a table on the console
builder.addHandler(new ConsoleHandler());
//Generates a HTML file containing an HTML table for viewing the results in the browser
builder.addHandler(new HTMLHandler(SHOW_IN_BROWSER));
//Constructs a JSON file from the generated results
builder.addHandler(new JSONHandler());
//Constructs an XML file from the generated results
builder.addHandler(new XMLHandler());
//Builds a LaTex table respresentation for use in a paper
builder.addHandler(new LaTexHandler());
```
For each possible combination of goldstandard groups (in our example `GSGroup1` and `GSGroup2`), matchers (in our example `m1`, `m2` and `m3`) and thresholds (in our example `t1` and `t2`) we generate a report. Hence, in our example an overall of 12 reports are generated containing for example the report for matcher `m1` evaluated against `GSGroup1` with threshold `t1`. Of course you can also access the results programatically by inspecting the returned `Report` instances. Each of the previously specified `MetricGroup`s have a corresponding `MetricGroupBinding` and each of the specified `Metric` instances have a corresponding `MetricBinding`. To get the metric value simply call `MetricBinding.getValue()` on the desired `MetricBinding`. Finally you need to provide the output path and output name for the generated reports to be stored to.

```java
builder.setOutputName("oaei17-new-gs")
       .setOutputPath(OUTPUT_PATH);
```

### d.) Running The Evaluation

Once you specified all the parameters and settings described in the previous sections, you can build the actual `Configuration` instance and provide it to an `Evaluator`. This `Evaluator` is the main access to the actual running process and also informs you on errors and the final results.

```java
Configuration configuration = builder.build();
Evaluator evaluator = new Evaluator(configuration);
Evaluation evaluation = evaluator.run();
```

With the `Evaluation` instance at hand, you can access the individual generated `Report`s in different ways showed in the following.

```java
List<Report> reports1 = evaluation.getReports();
List<Report> reports2 = evaluation.getReports(t1);
List<Report> reports3 = evaluation.getReports("GSGroup1");
List<Report> reports4 = evaluation.getReports("GSGroup2", ts2);
```

### e.) Final Notes

Note that the example process described previously depicts the main capabilities of this process model matching evaluation framework. However, there is much more functionality implemented at the moment. One important additional functionality to mention is that you can also classify the correspondences in the goldstandards and matchers into classes of different difficulty automatically. Afterwards you can compute the metrics as shown on any combination of the individual subsets for the different classes only. The general idea on the automatic classification to matching patterns for process model matching evaluation is described in the corresponding paper [[4](https://github.com/kristiankolthoff/PMMC-Evaluator#publications)].


# Synthesizer

In addition to the Evaluator used to evaluate Process Model Matchin Systems described previously, this library also ships with a Synthesizer API which is capable of semi-autoamtically generating synthesized process models. It not only generates a synthesized process model based on an original process model, but also creates an entire process model matching systems test case consiting of the original untransformed model, the synthesized model and a corresponding goldstandard to evaluate against.

## Example

To get started with the Synthesizer, we first of all create a `Synthesizer` instance by specifying which `Transformer` should
be applied (here it is the `BPMNTransformer` because we want to work with BPMN process models), and add the model path.

```java
Synthesizer synthesizer = new Synthesizer(new BPMNTransformer());
synthesizer.readModel(ONE_2_MANY_PATH, "one2many");
```

After reading in the process model, we can start executing the first simple transformations shown in the following code snippet. Here we
simply transform two activities which are identified by the activity ID of the original process model and set new activity labels. We can additionally specify if the correspondence remains valid after the label transformation.

```java
synthesizer.transformActivity("Task_0et9ryz", "Apply via online")
	   .transformActivity("Task_0w1pt40", "Graduate from school")
	   .transformActivity("Task_0r2px70", "Test language skills", false);
```

Another useful transformation which keeps the original correspondences valid is to replace words by a specified synonym group. For example, in the following we replace the words *graduate* and *exmatriculate* with one of the remaining synonyms in the synonym group.
If we only want to replace a certain fraction of words contained in the process model, we can also specify a probability in the method
`replaceSynonymsWithProability(double probability, String... replacements)`.

```java
 synthesizer.replaceAllSynonyms("graduate", "exmatriculate")
  	    .replaceAllSynonyms("do", "make", "try")
	    .replaceSynonymsWithProbability(0.3, "rate", "evaluate");
```

In order to test process model matching systems specifically on their ability to identify one-to-many correspondences based on model specifications created in different kinds of levels of granularity, the Synthesizer ships with many methods to create test instances for the described characteristic. For example, we can replace a single activity by a set of activities executed in parallel (describing the single activity more fine-grained). We can do the same but instead of replacing it by parallel activties, we replace it by a set of sequentially executed activities. The corresponding original process model may look similar to the following figure.

![alt tag](https://raw.githubusercontent.com/kristiankolthoff/PMMC-Evaluator/master/src/main/resources/images/original.PNG)

```java
synthesizer.one2ManyParallel("Task_0et9ryz", "Upload necessary documents", 
					     "Upload cv", 
				             "Fill out questionaire")
	   .one2ManyParallel("Task_0w1pt40", "Write final exams",
	                                     "Search for programs")
	   .one2ManySequential("Task_0r2px70", "Invite for test", 
	   				       "Conduct test", 
					       "Evaluate test");
	   .many2OneParallel("Apply online", "Task_xfsad4",
	  				     "Task_trgw5",
					     "Task_62gji");
```

After applying the one-to-many parallel transformations on the previously shown simple example process model, the transformed process model is illustrated in the following figure.

![alt tag](https://raw.githubusercontent.com/kristiankolthoff/PMMC-Evaluator/master/src/main/resources/images/one2many.PNG)

So far we only examinated transformations that require manual input. In the subsequent section we show the capabilities of automatically generating synthesized models with only little manual input. For example, we can add totally irrelevant activities from another process model. However, we also can add an `Activity` manually by specifying a corresponding `AddStrategy` (or more specifically here we need to specify a `BPMNAddStrategy`).

```java
synthesizer.addIrrelevant(new BPMNAddStrategyUnconnected(), 
				new Activity("test_id_1", "Unconnected activity"))
	   .addIrrelevant(new BPMNAddStrategyTaskSequential(), 
	   			new Activity("test_id_2", "Sequential activity"))
	   .addIrrelevant(new BPMNAddStrategyParallelGateway(), 
	   			new Activity("test_id_3", "Parallel Gateway activity"))
	   .addIrrelevantFromDataset(new BPMNAddStrategyMeta(), 
	   			new File(ADDIRRELEVANT_TEST_PATH + "/birthcertificate.bpmn"), 1.0)
```

Another automatic transformation is to flip the process model. By flipping the process model vertically, we reverse the direction of the
information flow (e.g. for BPMN process models we inverse the *SequenceFlows* and swap the *StartEvent* and *EndEvent*). This can be achieved by the following call.

```java
synthesizer.flip(Direction.VERTICAL)
```

The described transformation functions are the most important, but there are also minor ones in the libary not described in this example. To finally let the `Synthesizer` create the transformed model and the goldstandard we call `synthesizer.finished(MODEL_PATH + "/case1");`. The `Synthesizer` creates a directory called *case1* and saves all three components in there: the original model, the transformed model and the goldstandard.

# Publications

[1] Manel Achichi, Michelle Cheatham, Zlatan Dragisic, Jérôme Euzenat, Daniel Faria, Alfio Ferrara, Giorgos Flouris, Irini Fundulaki, Ian Harrow, Valentina Ivanova, Ernesto Jiménez-Ruiz, Elena Kuss, Patrick Lambrix, Henrik Leopold, Huanyu Li, Christian Meilicke, Stefano Montanelli, Catia Pesquita, Tzanina Saveta, Pavel Shvaiko, Andrea Splendiani, Heiner Stuckenschmidt, Konstantin Todorov, Cássia Trojahn and Ondrej Zamazal **Results of the Ontology Alignment Evaluation Initiative 2016**. In: CEUR workshop proceedingsOM 2016 : proceedings of the 11th International Workshop on Ontology Matching co-located with the 15th International Semantic Web Conference (ISWC 2016) Kobe, Japan, October 18, 2016; 73-129. RWTH, Aachen, 2016.

[2] Manel Achichi, Michelle Cheatham, Zlatan Dragisic, Jérôme Euzenat, Daniel Faria, Alfio Ferrara, Giorgos Flouris, Irini Fundulaki, Ian Harrow, Valentina Ivanova, Ernesto Jiménez-Ruiz, Kristian Kolthoff, Elena Kuss, Patrick Lambrix, Henrik Leopold, Huanyu Li, Christian Meilicke, Majid Mohammadi, Stefano Montanelli, Catia Pesquita, Tzanina Saveta, Pavel Shvaiko, Andrea Splendiani, Heiner Stuckenschmidt, Élodie Thiéblin, Konstantin Todorov, Cássia Trojahn, Ondrej Zamazal:
**Results of the Ontology Alignment Evaluation Initiative 2017**. OM@ISWC 2017: 61-113

[3] Elena Kuss, Henrik Leopold, Han Van der Aa, Heiner Stuckenschmidt and Hajo A. Reijers **Probabilistic evaluation of process model matching techniques**. In: Lecture notes in computer scienceConceptual modeling : 35th international conference, ER 2016, Gifu, Japan, November 14-17, 2016 : proceedings; 279-292. Springer, Cham, 2016.

[4] Elena Kuss and Heiner Stuckenschmidt **Automatic classification to matching patterns for process model matching evaluation**. In: CEUR workshop proceedingsER-Forum-Demos 2017 : proceedings of the ER Forum 2017 and the ER 2017 Demo Track co-located with the 36th International Conference on Conceptual Modelling (ER 2017) Valencia, Spain, November 6-9, 2017; 306-319. RWTH, Aachen, 2017.

[5] Elena Kuss, Henrik Leopold, Christian Meilicke and Heiner Stuckenschmidt **Ranking-based evaluation of process model matching**. In: Lecture notes in computer scienceOn the Move to Meaningful Internet Systems. OTM 2017 Conferences : Confederated International Conferences: CoopIS, C&TC, and ODBASE 2017, Rhodes, Greece, October 23-27, 2017, Proceedings, Part I; 298-305. Springer, Cham, 2017.


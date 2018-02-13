# PMMC-Evaluator

This project provides an API for evaluating process model matchers with a variety of different metrics
and is used to generate the results for the official Process Model Matching Contest (PMMC) conducted in 2016 [[1](https://github.com/kristiankolthoff/PMMC-Evaluator#publications)] and 2017 [[2](https://github.com/kristiankolthoff/PMMC-Evaluator#publications)]. This contest is about finding pairwise matching tasks or activities (denoted as correspondences) in comparison of two given process models. In particular, for this contest three different datasets are used all having different notation form. The first set of process models describes the admission process at various universities and is represented in [BPMN](http://www.bpmn.org/). The second set of process models describe the process of creating birth certificates and uses [Petri-Nets](https://en.wikipedia.org/wiki/Petri_net). The third and last set of process models describe the basic process of managing assets and is represented in [EPC](https://en.wikipedia.org/wiki/Event-driven_process_chain). For evaluating the submitted matchers, we construct various evaluation settings and compute the corresponding metrics. An evaluation process is easily setup by using the builder pattern to increasingly build up a configuration for an evaluation. This `Evaluation` instance forms the basis for any experiment.

# Example

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











```java
evaluator.run();
```

# Publications

[1] Manel Achichi, Michelle Cheatham, Zlatan Dragisic, Jérôme Euzenat, Daniel Faria, Alfio Ferrara, Giorgos Flouris, Irini Fundulaki, Ian Harrow, Valentina Ivanova, Ernesto Jiménez-Ruiz, Elena Kuss, Patrick Lambrix, Henrik Leopold, Huanyu Li, Christian Meilicke, Stefano Montanelli, Catia Pesquita, Tzanina Saveta, Pavel Shvaiko, Andrea Splendiani, Heiner Stuckenschmidt, Konstantin Todorov, Cássia Trojahn and Ondrej Zamazal **Results of the Ontology Alignment Evaluation Initiative 2016**. In: CEUR workshop proceedingsOM 2016 : proceedings of the 11th International Workshop on Ontology Matching co-located with the 15th International Semantic Web Conference (ISWC 2016) Kobe, Japan, October 18, 2016; 73-129. RWTH, Aachen, 2016.

[2] Manel Achichi, Michelle Cheatham, Zlatan Dragisic, Jérôme Euzenat, Daniel Faria, Alfio Ferrara, Giorgos Flouris, Irini Fundulaki, Ian Harrow, Valentina Ivanova, Ernesto Jiménez-Ruiz, Kristian Kolthoff, Elena Kuss, Patrick Lambrix, Henrik Leopold, Huanyu Li, Christian Meilicke, Majid Mohammadi, Stefano Montanelli, Catia Pesquita, Tzanina Saveta, Pavel Shvaiko, Andrea Splendiani, Heiner Stuckenschmidt, Élodie Thiéblin, Konstantin Todorov, Cássia Trojahn, Ondrej Zamazal:
**Results of the Ontology Alignment Evaluation Initiative 2017**. OM@ISWC 2017: 61-113


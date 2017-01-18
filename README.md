# PMMC-Evaluator
This project provides an API for evaluating process model matchers with a variety of different metrics
and is used to generate the results for the Process Model Matching Contest (PMMC). An evaluation experiment
is easily setup by using the Builder pattern to increasingly build up an Evaluation instance. This Evaluation
instance forms the basis for an experiment.

#Example

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
				addHandler(new HTMLHandler(SHOW_IN_BROSWER));++
				addHandler(new JSONHandler());
				setAlignmentReader(new AlignmentReaderXml()).
				build();
```

Of course, you can also programmatically receive the results 
from the Evaluator and do additional processing of the data.

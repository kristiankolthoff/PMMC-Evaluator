Dear Matcher Developer
======================

The testcases of this dataset can easily accessed by using the following list of identifiers:

"Cologne", "Frankfurt", "FU_Berlin", "Hohenheim", "IIS_Erlangen", "Muenster", "Potsdam", "TU_Munich", "Wuerzburg"

Two nested loops can be used to access the 36 testcases in the following order:

Cologne-Frankfurt, 
Cologne-FU_Berlin
... 6 more
Frankfurt-FU_Berlin
... and so on

Note that, for example, the "reversed" testcase FU_Berlin-Frankfurt, does not exist in the collection.

The matching results should be stored in files named:

Cologne-Frankfurt.rdf
... and so on

The URIS used in the output should look like this:

http://Cologne#sid-50620B60-4F01-4DFD-8EB8-BBCAAECE6026

This way of referring to the matched activities is also used in the gold standard.
Using a different naming schema will result in 0% recall.

Have fun with the dataset!

UPDATE: Some minor incorrectness with the gold standard have been fixed at 26 of June 2015. This is the updates dataset.



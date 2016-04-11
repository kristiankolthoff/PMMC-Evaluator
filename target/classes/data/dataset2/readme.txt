Dear Matcher Developer
======================

The testcases of this dataset can easily be accessed by using the following list of identifiers:

"birthCertificate_p31", "birthCertificate_p32", "birthCertificate_p33", "birthCertificate_p34",
"birthCertificate_p246", "birthCertificate_p247", "birthCertificate_p248", "birthCertificate_p249",
"birthCertificate_p250"

Two nested loops can be used to access the 36 testcases in the following order:

birthCertificate_p31-birthCertificate_p32, 
birthCertificate_p31-birthCertificate_p33, 
... 6 more
birthCertificate_p32-birthCertificate_p33, 
... and so on

Note that, for example, the "reversed" testcase birthCertificate_p32-birthCertificate_p31, does not exist in the collection.

The matching results should be stored in files named:

birthCertificate_p31-birthCertificate_p32.rdf
... and so on

The URIS used in the output should look like this:

http://birthCertificate_p31#t33

This way of referring to the matched activities (transitions) is also used in the gold standard.
Using a different naming schema will result in 0% recall.

Have fun with the dataset!
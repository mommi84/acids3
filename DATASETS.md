As @mohamedsherif and I were discussing about the datasets we used in our publications and are still using to evaluate the approaches implemented in LIMES, I share here our findings.

### ER Benchmark
The [Benchmark for Entity Resolution](http://goo.gl/9ALII4) from Database Group Leipzig is being used. Since it was released in CSV format, it does not provide more than one object per property. For instance, the _:author_ property of  a DBLP publication is a string containing the author names, which definitely is not the purpose of RDF. Moreover, it does not contain information on URI resources that appear among the objects.

In general, **it is not recommended to evaluate link discovery / instance matching approaches on non-semantic datasets**! Among the datasets included in the benchmark, only [DBLP](http://datahub.io/dataset/rkb-explorer-dblp) and [ACM](http://datahub.io/dataset/rkb-explorer-acm) are RDF-based and belong to Linked Data, therefore it was possible to create a .NT version of the original benchmark dataset ([code here](https://github.com/mommi84/SemSRL/blob/master/src/org/aksw/simba/semsrl/BenchmarkSemantifier.java)).

* DBLP-ACM →  [**Download .NT version here**](https://bitbucket.org/mommi84/rocker-servlet/downloads/DBLP-ACM-semantified.tar.gz)
* DBLP-GoogleScholar → **Non-RDF!**
* Amazon-GoogleProducts → **Non-RDF!**
* ABT-BUY → **Non-RDF!**

### Heterogeneity of data
Since the DBLP-ACM dataset describes publications, _owl:sameAs_ links are only provided among publications. However, a link discovery algorithm should be able to find also _owl:sameAs_ links among authors, volumes, or venues. Neither the above dataset, nor the OAEI datasets provide these additional links and fulfil this requirement.

The main concept behind handling data heterogeneity is that **in a zero-configuration link discovery the classes which the resources belong to are not known**, thus the approach must be completely agnostic. Heterogeneous mappings of the following datasets will be uploaded here:

* DBLP-ACM
* OAEI 2010 Restaurant
* ...

### OAEI 2014
One last word about the OAEI 2014 datasets: simply avoid to use them, as they contain several bugs in the perfect mapping.

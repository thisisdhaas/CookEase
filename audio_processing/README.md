Audio Processing Library for CookEase
-------------------------------------

The goal of this code is to featurize kitchen sound data and use it to train
classifiers to detect various kitchen events (e.g. boiling water).

Our approach uses [jAudio](http://jaudio.sourceforge.net/) to featurize the data
in `data/raw/`, producing featurized data in `data/arff/`. We then load the data
into [Weka](http://www.cs.waikato.ac.nz/ml/weka/) to train our classifiers.

In `scripts/`, we provide helpful utilities for handling the featurized data.
Specifically:

* `add_labels.py` allows the addition of binary labels to data files, so that
  we can label sound samples for classification easily.

* `combine_arff.py` merges multiple arff files with the same set of attributes.

All scripts can be run with `-h` or `--help` to show script options.

The current workflow is as follows:

* Load raw files in `data/raw/` into jAudio.
* Generate `.arff` files from the raw data in jAudio and save them to
  `data/arff`.
* Add labels to the `.arff` files (e.g., add a label of `1` to all files
  generated from raw sounds of boiling water, and a label of `-1` to all
  files generated from raw sounds not of boiling water) using the
  `add_labels.py` script.
* Merge all `.arff` files into a single `.arff` file using the
  `combine_arff.py` script, even files with different labels.
* Load the comibned `.arff` file into Weka, and use Weka to train a
  classifier on the label attribute
* Save the model for use in our application
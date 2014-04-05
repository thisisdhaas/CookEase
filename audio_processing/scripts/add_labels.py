from argparse import ArgumentParser

BINARY_LABEL_CHOICES = "{-1,1}"
ARFF_DATA_SEPARATOR = "@DATA"

def arff_attribute_definition(label_name, label_choices):
    return "@ATTRIBUTE \"" + label_name + "\" " + label_choices

def arff_add_label(unlabeled_sample, label):
    return unlabeled_sample.strip() + "," + label

def label_samples(in_path, attr_name, label_val):
    reached_data = False
    out_data = []
    with open(in_path, 'r') as arff_file:
        for line in arff_file:
            if not reached_data:
                if line.strip().upper() == ARFF_DATA_SEPARATOR:
                    reached_data = True
                    out_data[-1] = arff_attribute_definition(
                        attr_name, BINARY_LABEL_CHOICES)
                    out_data.append("")
                out_data.append(line.strip())
            else:
                out_data.append(arff_add_label(line, label_val))
    return out_data

def output_labeled_samples(out_path, out_data):
    with open(out_path, 'w') as out_file:
        out_file.write("\n".join(out_data))

def get_args():
    parser = ArgumentParser(description=("Append a binary label attribute in "
                                         "{-1, 1} to all samples in a .arff "
                                         "file for use with Weka classifiers"))
    parser.add_argument("-l", "--label", default="-1", choices=["-1", "1"],
                        help="label to add to samples (Default -1)")
    parser.add_argument("-n", "--name", default="Is Boiling Sound",
                        help=("Name of the label attribute "
                              "(Default 'Is Boiling Sound')"))
    parser.add_argument("-i", "--input", default="feature_values_1.arff",
                        help=("Path to the .arff file to add labels to "
                              "(Default 'feature_values_1.arff')"))
    parser.add_argument("-o", "--output",
                        default="feature_values_1_labeled.arff",
                        help=("Path to the .arff file to output labeled "
                              "samples to (Default "
                              "'feature_values_1_labeled.arff')"))
    return parser.parse_args()

if __name__ == '__main__':
    args = get_args()
    output_labeled_samples(args.output,
                           label_samples(args.input, args.name, args.label))

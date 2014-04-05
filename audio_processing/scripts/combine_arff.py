from argparse import ArgumentParser

ARFF_DATA_SEPARATOR = "@DATA"

def parse_arff(arff_path):
    reached_data = False
    out_data = []
    out_definitions = []
    with open(arff_path, 'r') as arff_file:
        for line in arff_file:
            stripped_line = line.strip()
            out_list = out_data
            if not reached_data:
                out_list = out_definitions
                if stripped_line.upper() == ARFF_DATA_SEPARATOR:
                    reached_data = True
            out_list.append(line.strip())
    return (out_definitions, out_data)

def require_matching_definitions(defs):
    def1 = defs[0]
    for definition in defs:
        if definition != def1:
            raise ValueError("Cannot merge arff files: definitions don't match")

def merge_arffs(arffs):
    defs = [arff[0] for arff in arffs]
    data = [arff[1] for arff in arffs]
    require_matching_definitions(defs)
    return (defs[0], [sample for samples in data for sample in samples])

def output_arff(out_path, out_definitions, out_data):
    with open(out_path, 'w') as out_file:
        out_file.write("\n".join(out_definitions + out_data))

def get_args():
    parser = ArgumentParser(description=("Merge data in two .arff files with "
                                         "the same attributes"))
    parser.add_argument("-i", "--input", required=True, nargs="+",
                        help="Paths to the .arff files to merge")
    parser.add_argument("-o", "--output",
                        default="feature_values_merged.arff",
                        help=("Path to the .arff file to output the merged "
                              "data (Default "
                              "'feature_values_merged.arff')"))
    return parser.parse_args()

if __name__ == '__main__':
    args = get_args()
    parsed_arffs = [parse_arff(path) for path in args.input]
    output_arff(args.output, *merge_arffs(parsed_arffs))

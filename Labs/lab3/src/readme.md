CSCI 331: Intro to Artificial Intelligence
Lab 3
Derek Garcia

## Usage
### Train
Training creates the decision tree and writes it to a file

Usage: `java lab3 train <examples> <hypothesisOut> <learning-type>`
- `examples` - file containing labeled examples
- `hypothesisOut` - file name to write model to
- `learning-type` - type of learning algorithm to use
> Must be `dt` or `ada` for decision tree and ada boost respectively

### Predict
Use a given model to predict a file
Usage: `java lab3 predict <hypothesis> <file>`
- `hypothesis` - model file to use
- `file` - test data to use
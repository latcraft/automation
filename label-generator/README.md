
## Address label generator

To generate address labels from CSV data, go to the `label-generator` folder and run the following command:

    gradlew clean generateLabels concatenateLabels -PdataFile=all.csv 

The `all.csv` should be copied to that folder beforehand and should never be committed to Git. 
If `-PdataFile` is not specified, then test data from the `data.csv` file will be used.

If needed, adjust the `label_template.svg` to match the event design. 

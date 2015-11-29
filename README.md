# automation

LatCraft's community automation tasks.

## Badge generator

To generate event badges from CSV data, go to the `badge-generator` folder and run the following command:

    gradlew clean generateBadges concatenateBadges -PdataFile=all.csv 

The `all.csv` should be copied to that folder beforehand and should never be committed to Git. 
If `-PdataFile` is not specified, then test data from the `data.csv` file will be used.

If needed, adjust the `badge_template.svg` to match the event design. 

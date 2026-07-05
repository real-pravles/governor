# governor

## How to build

1. `mvn clean package`
2. Run `cat <Singularity CSV file> | java governor-1.1.jar -d` where `<Singularity CSV file>` is the CSV file which has been exported from the Singularity application.
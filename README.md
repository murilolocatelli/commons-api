# example-commons-api

> Example Commons API is a lib for general API configurations

## Config lombok for Eclipse

https://projectlombok.org/setup/eclipse

## Config lombok for IntelliJ

https://projectlombok.org/setup/intellij

## Building with tests

```sh
mvn clean package
```

## Coverage

After running the tests, see the reports in target/site/jacoco/index.html

## Run local Sonarqube

Run docker Sonarqube:

```sh
docker run -d --name sonarqube -p 9000:9000 sonarqube:<sonarqube-version>
```

Generate token in sonarqube to use in below command

```sh
mvn clean package sonar:sonar -Dsonar.login=<sonarqube-token>
```

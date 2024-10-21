# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## [Server Design](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdOUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uEGxkM7RS9Rs4zlBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKwn1+40S01a1mnS0wa22wOpB1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPYC+1L1CAAa3Qlam+0dlEL8GQ5nKACYnE5umWhmKxjAq48prW-Q3m2hW6sDugOKYvL5-AFoOxyTAADIQaJJAJpDJZHt5IvFEvVOpNVoGdQJNDDgblsfjNtfF5vD5fg7FiChT5iWI63B+k7fvorxLJ+S6YCBQFFGqKDlAgh48rCB5Hqi6KxNiCaGC6oZumSFIGrS76jCaRJhhaHIwNyvJGtoQoilRYiutKSZXih5QsfIjrOgSJEsuUnrenWsICcANFMm69HlHAuqZFGdaCgAZtAMA3CGtEKTxyFOvKalBtoQmJsm5zAuU2E8tmuYIdZnaGYBVxvqOoxwVOfQzvWTYtpBAHXiCpzZL2MADkOvQeeBXmQTWdZzoF-5mJwa7eH4gReCg6D7oevjMCe6SZJg4WXkZN7SAAonu1X1NVzQtI+qjPt0fnJWgHZIYh5QdQFXWIWyhFoQVvpYWNYC4RiBFykRIn6WJMDkmAsawv185yWa4aFJGTExuprHCjAG3oHp8ncbKfGmXG5mERqomkkYKDcJka2xsGXHmhGDGRMMEA0DdDpfeGV3GahMCKsqFlEVZQIlthhUOQgebOaFvElFcwXUC5YUXmA-aDsOy7pZ4mWbpCtp7tCMAAOJjqyxVnmV+PDSF5QVLT9VNfYY7tUlA3dWyvUnQL85OfDSEjTAyCxPToyqFh0Ly2o034TDD2LU9K1radaBbXRP1cjyB1mfIbGi7Ogsg120sfXdc2axdT2y2AKuKwbBm7Qx+1RsqdMM+d2223N5Tu9TsQa4ZIsR27DPI6jkshzjaY9FMvMK5+FRp30GcoAAktIn4AIx9gAzAALE8p6ZAaFYJVMOgIKAjZ1xBX5PHnAByY6QTAezNNjxyGeVBORUTvTpwzWc53nhcl+XVdTDX+ocd5XxNy3bfxR3U+jD3ox9wP-dpauZMboE2A+FA2DcPAKmGCrKQleeOTMGDN61A0PN88EYvoMObuY4h4uWAmjPq-8Xy9CAaMAEaM2bXQkpkFWsJlJemQWONWWIo4LWdu6ZaFJdaQM9txb2xteT23NsdPWQdDZg3BEDB2KEnbbXEg-FBMCUAkO+mQ++6DH5jhgMkDIqRIYoGSMtMchd2q8k4bQgy9CTIq3jI7aO4C+F6ndgnCWqZk6Y1LHvAuRdyil0rifQCXZR6E2irPKRxiYCmIrifFcGUL4BEsC9dCEiABSEAeQB1GIETeIBGwszfgg-R1RKT3haHnfmVt5zDlvsADxUA4AQHQlAWYc9pAgJ6uovWSTm6pPSZk7Jdi4FJ0URDAAVn4tAKDfE8hVlg2azDiJa3wTrKSetuE7XZOQ02qQtBULCDQm2rlQ6MMEvdDpeCyJx1GLCHJfTFKMRNgEwwx0cnyMuhjBhyimHgxYWGcofgRkoBQckkpGToCzFFKMcpoxC6rKNkUZgDzDAQA0jAa5lBSnaS0rAGwKAiAaUIJI550hdnfWqQqJUKpZlw1TOUJpDT45qEckNSZKcsZCxHvjaxxNT6uKygELwKTuxelgMAbAt9CDxESM-Zmo8IlVVqvVRqrRjD4rAZLUoOiCxwpgCAbgeBUFiqzCgNEM0cEyEevg0V1KjQKBqLCV5vDpAvQpIYYA-sZJypBuJSVyjVXqphf0yMWrXq6v9ocmZqjcGsJFZKj6ZqNUDJkNq1SeqEDTOABavRDCoaItUcimyVK8DaOxR-VOeTLGEvHjYkmq4gA)

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
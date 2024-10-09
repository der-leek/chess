# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## [Server Design](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIcDcuj8atr4vN4fT8HYsgoU+YlsOtzvhOX76K8SwfoumDAYBRRqig5QIAePKwvuh6ouisTYgmhgumGbpkhSBq0m+owmkS4YWhyMDcryBqCsKMCiuKrrSkml7IeUzHaI6zoEsRLLlCUSAAGaWFUUE-rC-HyLMWE8jhGLUUybp0eUcC6pkMCxsG2gwBJ0BsR0oY0Zp3FIU68r6UG8ZyoRybnMC5TKWg2a5vBrkdtZAFXK+I6jLBk59NOs7NhB-5XiCpzZD2MD9oOvRBWBIUQTWQaRfO0VmJwq7eH4gReCg6B7gevjMMe6SZJgCUXjZ17SAAoruLX1C1zQtA+qhPt0EWNug7aIQh5SDXOPlAn5PG2ShMBofYVWYZVvqqXhgmJsJlmiTA5JgAZAbZUNaDqWaEaFJaDE8jGDlGaxE3DZx5r+U55QGY5yEaiJpJGCg3CZIdH3aGdtGRvRkTDBAND2XGAnPRGsq8TAirKptzlAb5aZYVVXkIHmWOdgFpYxdQfnxeeYB9gOQ5LgVnhFRukK2ru0IwAA4qOrI1ae9WU2yxMVOzHXdfYo4Dcdk0AWyY0wI9aBTamAtvQt0Kc6MqiYWrXPrVi6PfTtv37Yd8ug1Zl30Yxt1w-IQphPLFkaVxSNze9d3yPrRGG+6quxOraiwmbXEW+UVv6cqHNc4751Eyr-uqKzsSey503Y9rGt4wTqex2TaY9FMYsax+FT530hcoAAktIH4AIy9gAzAALE8J6ZAaFaZVMOgIKADbt+Bn5POXAByo4QTAezNKTxzWQ1VNJTTvQF1zxel+XVe1w3zdTK3+qUfcg9dz3IB9-voVfCPY+fhPU-5SuDProE2A+FA2DcPAumGP7KS1WeOTMC7a8tQGii3FsESW6AhyX1GNPGaZxU7jQgc+Xo0CUAAkJi7cEMBPR6n9rCHSXpMj+11vhJyBsnZGwpCbJBQcXoh2uryYGtsHpIOjmDTBdkmHAE9ttChPscFENHLCVBtCLrsm0p-SOowYDJAyKkFGKBkh7VHFXAavJUFsKshw+a-tPpzU7LLAhuCdZqG8ghHOJQrjL1GBvcodcm4TxGp2Oe1MUprxUdXOxW9HHLkKo-AIlh-poSUQAKQgDyKRhgAjd17nzf+ytc7lGqJSO8LRy4SzrCdIcb9gCBKgHACAaEoCzHXtIWBo0saIMyXObJPc8kFKKSUjx6Ds7aPKAAK3CWgPBYSeTEJQGiNSPCZA-R9sbIMR1qnoFEVpBh1tUhaGYfbVhCMLFYK4cMhGpEwB4NKTM8GocbqRLtsomx0hNHO1mlg3RAkCLkPOuUPwiyUB4JyfUwplAmlnP2fQyIOT1Tf1YqUi5L02koyVCqO51lZa9O6SYnM+NFYFleokkmTjZ6U1cbTO+fjioBC8LkrsXpYDAGwG-Qg8REg-15nPBJljKitXap1bqxh0WYwQTAJFcCCLlBANwPA+D+VZgGbhPWULeEPOwUKwOIKxFXWkP9CkhhgARy4bMG5ik5ZMXtPDUZay7Ko0hWQ6FlSiV4EzlyuKs0QLlOcZihebi6YriAA)

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
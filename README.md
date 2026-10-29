# Pomodoro Timer

A desktop Pomodoro timer built with Java Swing.

It follows the **Model–View–Controller** pattern together with the **Observer**
pattern (a custom `PomodoroFigyelo` interface the model notifies).

## Features

- Timer states: `WORK`, `SHORT_BREAK`, `LONG_BREAK`, `PAUSED`
- Big `MM:SS` countdown display
- Start / Pause / Reset buttons
- Color change of the countdown based on the current state
- Sound (`Toolkit.getDefaultToolkit().beep()`) on state change
- Work / short break / long break durations configurable in a modal settings dialog
- Countdown driven by `javax.swing.Timer` (Event Dispatch Thread safe, no `Thread.sleep`)

## Requirements

- Java JDK (8 or newer)

## Project structure

```
Pomodoro_ora_ex/
├── src/
│   ├── Main.java
│   ├── model/         -> PomodoroModel, PomodoroFigyelo, State (no Swing!)
│   ├── view/          -> PomodoroView (JFrame, implements observer)
│   └── controller/    -> PomodoroController (buttons -> model)
├── test/
│   └── model/
│       └── PomodoroModelTest.java
├── lib/            -> junit-platform-console-standalone-6.1.3.jar
├── .vscode/        -> settings.json (source paths + JUnit on the classpath)
├── docs/
│   └── pomodoro.puml   -> PlantUML class diagram (Observer)
└── README.md
```

## How to run

```bash
javac -d out src/Main.java src/model/*.java src/view/*.java src/controller/*.java
java -cp out Main
```

## How to run the tests

```bash
javac -d out src/Main.java src/model/*.java src/view/*.java src/controller/*.java
javac -d out_test -cp out:lib/junit-platform-console-standalone-6.1.3.jar test/model/*.java
java -jar lib/junit-platform-console-standalone-6.1.3.jar \
  execute --class-path out:out_test --scan-class-path
```

## UML diagram

The class diagram lives in `docs/pomodoro.puml` (PlantUML) and shows the
Observer relationship: `PomodoroModel` notifies `PomodoroView` through the
`PomodoroFigyelo` interface.

## Screenshots

- Work state: `screenshots/pomodoro-work.png`
- Break state: `screenshots/pomodoro-break.png`
- Settings dialog: `screenshots/pomodoro-settings.png`

## Technique rules

- The model never imports `javax.swing`.
- The view implements the observer and only updates UI components.
- The controller holds the button listeners and only calls model methods.
- The Swing `Timer` drives the ticking (EDT safe).

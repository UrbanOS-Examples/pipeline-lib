# pipeline-lib
Jenkins shared library

## Install dependencies on OSX

```bash
brew tap homebrew/cask-versions
brew cask install java8
brew install gradle
```

Note that upgrading to java 11 may cause some groovy functionality to break. To test this locally make sure to match versions if tests start failing.

## Build

```bash
gradle build
```

## Test

```bash
gradle test
```
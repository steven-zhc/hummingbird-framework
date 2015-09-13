# Hummingbird

Smart, Light and Fast.

An implementation of DDD, CQRS, EventStore and etc.

This is core framework for user management team.

TODO add Image

## Quickstart

### Checkout Code
     git clone git@dev-git.fuhu.org:um-dev/hummingbird-framework.git
     cd hummingbird-framework
     gradlew clean build

The build requires a Java 7 JDK as JAVA_HOME, but will ensure Java 7 compatibility.

### Event Store (Cassandra) Setup
The schema were located at

     scrpt/cassandra

Execute by order, please update keyspace name in create_keyspace.cql

     create_keyspace.cql
     create_tables.cql

Note: We also provide another type of event store, mysql RDBMS.

     script/mysql


## Modules
##### Core
| Module | Description |
| ------ | ----------- |
| spi    | Service Provider Interface. This is the core module of Hummingbird |
| runtime| This is basic runtime library, including most basic infrastructures and implements|
| spring | A module could integrate spring framework |
| test   | Some test utiles could be used in other modules |

#### Extension
There are many extensions locations at another project: `hummingbird-ext`

## Resources
Hummingbird uses Gradle as its build tool. See the Gradle Primer section below if you are new to Gradle.


## Gradle primer
This section describes some of the basics developers and contributors new to Gradle might
need to know to get productive quickly.  The Gradle documentation is very well done; 2 in
particular that are indispensable:

* [Gradle User Guide](http://gradle.org/docs/current/userguide/userguide_single.html) is a typical user guide in that
it follows a topical approach to describing all of the capabilities of Gradle.
* [Gradle DSL Guide](http://gradle.org/docs/current/dsl/index.html) is quite unique and excellent in quickly
getting up to speed on certain aspects of Gradle.

## Using the Gradle Wrapper

For contributors who do not otherwise use Gradle and do not want to install it, Gradle offers a very cool
features called the wrapper.  It lets you run Gradle builds without a previously installed Gradle distro in
a zero-conf manner.  Hummingbird configures the Gradle wrapper for you.  If you would rather use the wrapper and
not install Gradle (or to make sure you use the version of Gradle intended for older builds) you would just use
the command `gradlew` (or `gradlew.bat`) rather than `gradle` (or `gradle.bat`) in the following discussions.
Note that `gradlew` is only available in the project's root dir, so depending on your `pwd` you may need to adjust
the path to `gradlew` as well.

## Executing Tasks

Gradle uses the concept of build tasks (equivalent to Ant targets or Maven phases/goals). You can get a list of
available tasks via

    gradle tasks

To execute a task across all modules, simply perform that task from the root directory.  Gradle will visit each
sub-project and execute that task if the sub-project defines it.  To execute a task in a specific module you can
either:

1. `cd` into that module directory and execute the task
2. name the "task path".  For example, in order to run the tests for the _sub_ module from the
root directory you could say `gradle sub-module:test`

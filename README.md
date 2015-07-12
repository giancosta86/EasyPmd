# EasyPmd

*Seamlessly integrates PMD into your NetBeans IDE*


## Introduction

*Elegance always matters, especially when 
creating software.*

However, a universal definition of such an ambitious goal seems to be fairly difficult, if not impossible, as different programmers adopt different styles.

PMD is a Java library/tool allowing you to scan your code and detecting *violations* of the *rules* that you requested to enforce: this introduces a great deal of flexibility, in particular if you consider that you can both use a wide range of predefined rules *and* write your own rules: for example, the predefined ruleset *rulesets/unusedcode.xml* will make PMD scan your source files for unused private fields, unused private methods, unused local variables and so on. Of course, you can instruct PMD to use *multiple rulesets simultaneously*.

PMD can be run both as a library and as a standalone program, and several excellent IDE plugins are now available, some of which target NetBeans: EasyPmd is designed as an open source plugin for NetBeans which seamlessly integrates the PMD scanning engine into NetBeans, by making PMD violation reports automatically appear both in the *Action items* window and in the editor: you just have to specify the scanning scope (current file, main project, all projects) in the *Action items* window.

The current major version provides a simplified, more elegant architecture, based on Helios, and *profiles*, to let you easily switch between multiple configurations.

The overall build process is based on Maven 3, for elegance and robustness.


## Features

* Includes PMD 5, compatible with Java 8.

* Fully-refactored, much better and faster architecture, relying on Helios and Maven 3.

* Automatically runs PMD on the files of your current task scope (selected in the *Action items* window).

* Reports every PMD violation both in the *Action Items* window and in the editor's side bar.

* Option field for setting PMD's *auxiliary classpath*

* **Supports profiles:** you can easily change the plugin's options simply by changing the active profile.

* **Glyphs of different colors for different priorities:** ranging from full green to full red - according to the priority of the rule that is is bound to each violation.

* **Priority filtering:** in the configuration dialog, one can choose the minimum priority level that PMD will consider when applying rules.

* **Optional priority label in task descriptions:** each violation in the *Action items* window shows its priority by default - one can therefore sort violations just by clicking the *Description* header.

* Includes a copy of PMD, for a safer and much faster execution.

* **Robustness:** most execution errors, should they occur, are caught and reported in the tasks list, without crashing the plugin.

* **Integrated cache:** to ensure maximum speed and avoid repeated scans, EasyPmd features a cache which is also persisted to disk, so it is available even after you restart NetBeans.

* You can extensively customize EasyPmd and the underlying PMD engine via a user-friendly options dialog.

* **Custom path filtering** (including and excluding paths), based on *regular expressions*.

* Predefined, customizable regular expressions, to simplify path filtering.

* Online help, integrated into the NetBeans help system.



## Requirements

EasyPmd 7 requires NetBeans 8+ and Java 8+.



## Installation

EasyPmd can be easily installed from within NetBeans, as explained in the tutorials below.

Alternatively, you can download the **.nbm** files from GitHub or from the NetBeans Plugin Portal.




## Tutorials on YouTube

* [Getting started - English version](https://www.youtube.com/watch?v=BsMx6PNn0aI)

* [Getting started - Version française](https://www.youtube.com/watch?v=3k4Fk43u0QE)



## Special thanks


Special thanks to (ordered by surname):

* *Christian Funder*
* *Ross Goldberg*
* *Matthew Harrison*
* *Thomas Kellerer*
* *Wes McKean*
* *Jeremy Pyman*
* *Toru Takahashi*
* *Thomas Wolf*



## Further references

* [PMD](http://pmd.sourceforge.net/)
* [Facebook page](https://www.facebook.com/easypmd)

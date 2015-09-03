![http://folk.ntnu.no/~edvardkk/isPhilosopherExample.png](http://folk.ntnu.no/~edvardkk/isPhilosopherExample.png)

## Introduction ##
Meaningful method names are crucial for readable and maintainable Java code. A good method name precisely conveys intent, whereas an imprecise, sloppy or misleading name can cause anything from confusion and mild frustration to full havoc.

**_Lancelot_** is an Eclipse plugin that identifies _naming bugs_: methods that significantly differ from the vast majority of methods with similar names. When possible, Lancelot also suggests better-fitting names for such ill-named methods.

## Installation ##
Lancelot is free software distributed under the terms of the Eclipse Public License. Developers using Eclipse can **download and install** the plugin from the update site at http://lancelot.nr.no/. The only requirement is a working Eclipse Indigo system with the Java Developer Tools installed. Although Lancelot should function out of the box, we strongly suggest glancing over the FrequentlyAskedQuestions for advice on some common tips and peculiarities.

## Scientific background ##
The design and implementation of Lancelot was presented as a tool paper ([PDF](http://www.nr.no/~bjarte/papers/lancelot-pepm.pdf)) at [PEPM 2012](http://www.program-transformation.org/PEPM12).  This paper is a short introduction to the design and implementation of Lancelot, and partly, an introduction to the theory behind it.

The theory behind Lancelot, and much of the non-Eclipse specific code, comes from the [PhD thesis](http://www.duo.uio.no/sok/work.html?WORKID=112975) of Einar W. Høst.  The most relevant paper from the thesis is _Debugging Method Names_ ([PDF](http://publications.nr.no/hoest_oestvold_ecoop2009.pdf)) from ECOOP 2009.

## Feedback ##
Use the [Google Group](https://groups.google.com/group/lancelot-discussion).
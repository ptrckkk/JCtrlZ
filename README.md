## Introduction
JCtrlZ is a lightweight Java library that makes use of aspect-oriented programming (AOP) to capture particular events that happen
in your Java (or more general: Java-byte code) programs. These events can be undone.

Let's have a look at a small code snippet (which I guess says more than 1,000 words :)

```java
@Undoable
class Person {
    public Person(int age) { this.age = age; }
    private int age;
    public int getAge() { return age; }  
    public void setAge(int age) { this.age = age;}
}

public class App {
    public static void main(String[] args) {
        Person p = new Person(25);
        p.setAge(p.getAge() + 1); // age has now value 26
        History.undo();
        System.out.println(p.getAge()); // Will print 25
    }
}
```

The idea of this project was inspired by a lecture that was given by António Menezes Leitão [1] at Instituto Superior
Técnico in Lisboa, Portugal.

## Detailed Range of Functions
Any class, which is annotated with the _Undoable_ annotation is automatically tracked. "Tracked" means that, whenever a
member variable of such a class is set, a history entry will be created containing the old member variable value. This
works with primitive as well as complex types, i. e., objects. Calling the _History#undo_ method will then undo the
last set operation, i. e., set the corresponding object's member variable value to the value before the last set
operation.

The _Undoable_ annotation provides a boolean parameter that indicates whether to track set operations which set the
a new member variable value to the old one, i. e., no change. If you wish to not track setters, which do not set a new
value, set this boolean value to _false_. The default is _true_, i. e., also track setting the same value. For 
primitive values, this is obvious; for objects it isn't. The implementation uses the _hashCode_ function to objects
determine if two objects are equal or not. Thus, if two objects with distinct memory location (this means "physically"
two objects) but the same hashCode are set after each other only the first set would be tracked (in case the
annotation's boolean value is set to _false_).

If you wish to not track all member variables of a class, you can annotate those with the _FieldNotUndoable_ annotation
to exclude them from being tracked.

Finally, note that there is _one_ and only one history, i. e., all objects share the same history! The history itself
is a first-in-last-out queue as usual with undo operations.

## Getting Started
Follow these steps to integrate JCtrlZ into a new or existing Maven project:
1. Download the provided release or package it yourself
2. Make sure your pom.xml
   - is properly configures to support AOP and
   - has the JCtrlZ.jar as a (local) dependency and
   - the library is configured to be an aspect library.
   
   You can find a working pom.xml in [6].

That's it! You can set your annotations, run your targets, and convince yourself that it works :)

Hint: I did not get it to work without Maven. I could include the JAR file and it would not show any error but
unfortunately not run the aspects. If someone knows how to fix this, please let me know [2] so I can update this
description. Thanks!

## Limitations and Future Work
It seems that retrieving annotation of local variables is not possible with default Java libraries as of June, 2017. If
I'm wrong or in the future there will be a way that I have not heard of: We can enhance this project further ([4]).

## Final Notes
If you wish to make use of this project but cannot due to issues related to licensing, please feel free to contact me
[2].

<hr>
[1] http://web.ist.utl.pt/antonio.menezes.leitao/HomePage/index.html<br>
[2] patrickk[dot]m[at]web[dot]de<br>
[3] https://gist.github.com/ptrckkk/a8a0448b784237ab4c330f4c3ad93f4d
[4] https://github.com/ptrckkk/JCtrlZ/issues/1

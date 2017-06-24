## Introduction
JCtrlZ is a lightweight Java library that makes use of aspect-oriented programming (AOP) to capture particular events that happen
in your Java (or more general: Java-byte code) programs. These events can be undone.

Let's have a look at a small code example (which I guess says more than 1,000 words):

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

## Range of Functions
Any class, which is annotated with the _Undoable_ annotation is automatically tracked. "Tracked" means that, whenever a
member variable of such a class is set, a history entry will be created containing the old member variable's value. This
works with primitive as well as complex types, i. e., objects. Calling the _History#undo_ method will then undo the
last set operation, i. e., set the corresponding object's member variable value to the value before the last set
operation. For an example, see the introduction section.

Note that there is _one_ and only one history, i. e., all objects share the same history! The history itself
is a first-in-last-out queue as usual with undo operations.

### Tracking of Duplicate Values

The _Undoable_ annotation provides a boolean parameter that indicates whether to track set operations which set a 
member variable value to the same value, i. e., no change. If you wish to not track setters, which do not set a new
value, set this boolean value to _false_. The default is _true_, i. e., also track setting the same value. For example:

```java
@Undoable(false) // value is set to false => do not track duplicate sets
class Person {
    // Exactly the same as in the introduction
}

public class App {
    public static void main(String[] args) {
        Person p = new Person(25);
        p.setAge(p.getAge() + 1); // age has now value 26
        p.setAge(p.getAge()); // age is still 26, i. e., no change!
        History.undo();
        System.out.println(p.getAge()); // Will print 25 even though two setters were called!
    }
}
```

The comparison of two primitive values is obvious; for objects it isn't. The implementation uses the _hashCode_ function
of objects to determine if two objects are identical or not. Thus, if two objects with distinct memory locations (this
means "physically" two objects) but with the same hashCode are set after each other only the first set would be tracked
(in case the annotation's boolean value is set to _false_).

### Ignore Particular Member Variables

If you wish to not track all member variables of a class, you can annotate those with the _FieldNotUndoable_ annotation
to exclude them from being tracked. Again, let's have a look at an example to make it clearer:

```java
@Undoable // The annoation's value does not matter
class Person {
    public Person(int age) { this.age = age; }
    @FieldNotUndoable
    private int age;
    public int getAge() { return age; }  
    public void setAge(int age) { this.age = age;}
}

public class App {
    public static void main(String[] args) {
        Person p = new Person(25);
        p.setAge(p.getAge() + 1); // age has now value 26
        History.undo();
        System.out.println(p.getAge()); // Will print 26!
    }
}
```

## Getting Started
Follow these steps to integrate JCtrlZ into a new or existing Maven project:
1. Download the provided release [5] or create the package yourself
2. Make sure your pom.xml
   - is properly configured to support AOP,
   - has the JCtrlZ.jar as a (local) dependency, and
   - the library is configured to be an aspect library.
   
   You can find such a pom.xml in [3].

That's it! You can set your annotations, run your code, and convince yourself that it works :)

Hint: I did not get it to work without Maven. I included the JAR file and it did not show any errors but
unfortunately not run the aspects. If someone knows how to fix this, please let me know [2] so I can update this
description. Thanks!

## Limitations and Future Work
It seems that retrieving annotation of local variables is not possible with default Java libraries as of June, 2017. If
I'm wrong or in the future there will be a way that I have not heard of: Let me know [2] and we can enhance this
project ([4]).

## Final Notes
If you wish to make use of this project but cannot due to issues related to licensing, please feel free to contact me
[2].

<hr>
[1] http://web.ist.utl.pt/antonio.menezes.leitao/HomePage/index.html<br>
[2] patrickk[dot]m[at]web[dot]de<br>
[3] https://gist.github.com/ptrckkk/a8a0448b784237ab4c330f4c3ad93f4d<br>
[4] https://github.com/ptrckkk/JCtrlZ/issues/1<br>
[5] https://github.com/ptrckkk/JCtrlZ/releases/tag/v1.0
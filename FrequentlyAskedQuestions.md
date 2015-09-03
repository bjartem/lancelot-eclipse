## How should I configure Eclipse to best work with Lancelot? ##
To use Lancelot effectively, users should change/ensure two things in Eclipse's preferences:
  * Set the severity level of `Unhandled token in '@SuppressWarnings'`, at **Compile** → **Errors/Warnings** → **Annotations**, to `Ignore`.
  * Enable `Add line number attributes to generated class file`, at **Preferences** → **Java** → **Compiler**.

## What are "regular", "string", "custom" and "own type" objects? ##
  * A **regular object** is an object that does not inherit from the type `java.lang.Throwable`.
  * A **string object** is an instance of the type `java.lang.String`.
  * A **custom object** is one that does not belong to either of the namespaces `java.*` and `javax.*`.
  * The attribute **creates own type objects** indicates that the method creates an instance of the class on which the method is deﬁned.
For more information on Lancelot's attribute set, please see the article discussing Lancelot's theory ([PDF](http://publications.nr.no/hoest_oestvold_ecoop2009.pdf)).

## Why doesn't Lancelot complain about my tiny, but horribly named method? ##
Lancelot ignores certain kinds of very small and/or very simple methods.
Among those are empty methods, methods that always return the same integer constants, and methods that always throw exceptions. Because of that, methods like
```
void isFruitful() throws Exception {
    throw new Exception():
}
```
won't be reported as buggy, even though we would expect Lancelot to complain
both about the non-`boolean` return type and the exception.

Such degenerate methods are considered too special for Lancelot to say anything meaningful about them, so they are simply ignored.

## Why does Lancelot complain about run-time type manipulation, when my code is cast-free? ##
Usually, type erasure of generic types causes this (rather annoying) phenomenon. Lancelot only looks at JVM bytecode, and currently does not differentiate between explicit source-level casts and implicit casts generated during type erasure. For instance, since the call to `List.get` in
```
int foo(java.util.List<Integer> bar) {
    return bar.get(42);
}
```
results in the bytecode
```
3: invokeinterface (2 args) #2=<InterfaceMethod java.util.List.get (int)java.lang.Object>
8: checkcast #3=<Class java.lang.Integer>
```
Lancelot interprets `foo' as a runtime type manipulator. Whether an attribute that triggers only on explicit, user-introduced, would be more interesting for the analysis, is a question open for discussion, but this behavior is the current state of affairs.
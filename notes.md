# Notes

## 9/5
[Course Github](https://github.com/softwareconstruction240)
- Focus on **software design and functionality** before writing any Java

## 9/13
- Implement Javadoc (like python docstrings)
  - Javadoc is available on programming exam
- Important string methods:
  - int length()
  - char charAt() (like indexing into a string)
  - String trim() (removes leading/trailing whitespace)
  - boolean startsWith(String)
  - int indexOf(int/char) (returns index of character in string)
  - int indexOf(String) (returns index of string in string)
  - String substring(int, int) (returns a string from start/end indices)
- Special Characters
  - \b (backspace)
  - \uXXX (unicode characters)
- StringBuilder
  - solves inefficiencies of string concat
  - most useful for building strings in loops
- Packages
  - a way to organize classes
  - support sub-packages

## 9/17
- Objects are instances of classes
  - objects occupy memory at runtime, classes do not
- Strings and arrays can both be created without the "new" keyword
- Creation of a reference does not create an object
- Reference equality vs. Object equality
  - ==
    - object -- memory address -- equality
  - equals()
    - performs identity comparison by default but can be overloaded
  - Java has instance and static variables/methods
    - static is like cls in python
    - static methods are invoked by class name
  - Use static vars to count pieces on each team?
    - during the constructor?
- All instance variables should be private
  - getters/setters should be used for access
- Inheritance
  - constructors, static methods/fields, and private methods/fields are NOT inherited

## 9/26
- LOOK FOR WAYS TO USE ANONYMOUS INNER CLASSES TO KEEP DECLARATIONS CLOSE TO USE

### Design Principles
1. Design is inherently iterative
     - Design, implement, test, repeat...
2. Abstraction
     - Abstractions are represented by higher-level classes
3. Good naming
4. Single Responsibility/Cohesion
5. Decomposition
     - Break large problems into a collection of very simple ones
6. Algorithm & Data Structure Selection
     - No amount of decomposition or abstraction will hide a fundamentally flawed selection of algorithm or data structure
7. Low Coupling
8. Encapsulation / Information Hiding
9. Avoid code duplication
# COMPILERS PROJECT
### GROUP: 4D
- Diogo André Barbosa Nunes, NR1: 201808546, GRADE1: 16, CONTRIBUTION1: 25%
- João Miguel Gomes Gonçalves, NR2: 201806796, GRADE2: 16, CONTRIBUTION2: 25%
- Marina Tostões Fernandes Leitão Dias, NR3: 201806787, GRADE3: 16, CONTRIBUTION3: 25%
- Nuno Filipe Ferreira de Sousa Resende, NR4: 201806825, GRADE4: 16, CONTRIBUTION4: 25%

**GLOBAL Grade of the project: 16**
We believe that this project easily reflects the work that was put into by our team. Although we are missing many optimizations, we made sure to implement all the necessary features, going through each checkpoint throughly.

**SUMMARY:**

The objective of this project was to implement a tool that would essentially compile a simplified java class definition into bytecodes so that it would be possible to run it and produce the expected results, as the original javac would.
The compiler follows a very logical structure, being divided in lexical analysis with comment ignoring, followed by the syntactic analysis with error recovery mechanisms, OLLIR and jasmin representations.

**DEALING WITH SYNTACTIC ERRORS:**

Our compiler has an built-in syntatic error recovery and is able to skip 10 errors. This is done with the function error_skipto (Parser.jjt), which reports up to 10 errors before giving an exception.

**SEMANTIC ANALYSIS:**

When it comes to the semantic analysis, our compiler verifies that which was present in the project specification, which includes:

- Variables:
    - Verifies the previous declaration of all variables, including arrays.
    - Verifies that there is a single definition for each variable.
    - Verifies that assignments of variables to other variables have compatible types.
    - Verifies that a function call associated to a variable is a class type variable.
    - Verifies if the variable's scope is valid within that scope.

- Functions:
    - Verifies if called functions have the corerct number of arguments, as well as the correct types.
    - Verifies if the return values are valid and can be assigned to a variable.
    - Verifies the initialization of a function's return value.
    - Verifies if a function's return type can be used in an expression.
    - Verifies that a void function does not contain a return with a value.

- Arrays:
    - Verifies the initialization of an array's access' expression and if it is an integer.
    - Verifies if an array accces is actually done in a varible of the array type.
    - Verifies if the length property is used in arrays only.

- While/If Statements:
    - Verifies that the condition of a while/if expression evaluates to a boolean type.

- Classes:
    - Verifies that upon a function call, that the variable is initialized and contains the function definition.
    - Verifies that there is not instanciation in an expression without the call to one of its functions.

- Operations:
    - Verifies that the conditional operations && and ! are used solely with boolean expressions.
    - Verifies that the conditional operation < is solely used on expressions that evaluate to an integer type.
    - Verifies if arrays aren't used in arithmetic and/or conditional operations <.

**CODE GENERATION:**

The OLLIR code is being successfully being generated, with all the required implementations.

We accomplished this generation by having done our own visitor, which works simillarly to a PreOrder visitor. With this tool, we traverse the previously generated tree and let each function handle the node associated with it (like the semantic analysis). This way, a large string is consecutively created and being written to by those functions, which results in the OLLIR code.

Jasmin is being succesfully generated from the OLLIR code, with only the increment optimization done (iinc). We tried to make use of all necessary functions already implemented in the libraries given, to clean our code and to make it readable. We utilized a loop through all the methods, generating the code depending on the instruction type.

**TASK DISTRIBUTION:**

We tried to distribute the tasks evenly during the project's development.

CHECKPOINT 1:
    - In this checkpoint we worked simultaneously, recurring to the Visual Studio's Live Share extension, implementing the needed features all together.

CHECKPOINT 2:
    - In this checkpoint, we divided the tasks in 3 parts:
        - Symbol Table and Semantic Analysis - João Gonçalves and Nuno Resende
        - Generation of OLLIR code from the AST - João Gonçalves, Nuno Resende and Diogo Nunes
        - Generation of Jasmin code: Marina Dias and Diogo Nunes

CHECKPOINT 3:
    - This checkpoint required us to treat the whiles, ifs and arrays, which we divided 2 ways:
        - Corresponding OLLIR changes - João Gonçalves and Nuno Resende
        - Corresponding Jasmin changes - Diogo Nunes and Marina Dias

**PROS:**

The project's suggested stages as per the project's specification were followed and completed succesfully. We believe that all the required features were correctly and successfully implemented, are now working, being also accompanied by the tests created in the test directory.  

**CONS:**

During the initial development stages, we weren't aware of the code quantitity and complexity the compiler would require, and therefore we could have had a tidier code organization and structure.

As such, we needed to rework and refactor some features, having to change the mindset midway.

Also, we did not manage to implement most of the optimizations and function overloading is not supported.

**CONCLUSION:**

We believe that this project largely contibuted for our learning process of a compiler's behaviour, how it works and processes information.

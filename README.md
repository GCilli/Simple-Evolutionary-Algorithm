# Simple-Evolutionary-Algorithm

This repository holds the code that implements the solution for the problem proposed by Goldberg [1]. I used a simple evolutionary algorithm (EA) consisting of:

 - Random initialization of the population
 - Fitness evaluation
 - Parent selection
 - One-point cross-over recombination
 - Mutation
 - Parent replacement
 
 The EA terminates when it finds the global optimum. By default, the fitness function is f(x) = cos(1.3 * (x-1.5)) * cos(10.0 * (x - 1.5)). It has a maximum in x = 1.5 where  f(1.5) = 1.

 Compilation:

    > javac *.java

Run:

    > java Run


## References

[1] David E. Goldberg. 1989. Genetic Algorithms in Search, Optimization and Machine Learning (1st ed.). Addison-Wesley Longman Publishing Co., Inc., Boston, MA, USA.

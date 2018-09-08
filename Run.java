public class Run {
    public static void main(String[] s) {
        int populationSize = 200;
        int bitFieldSize = 10;

        Individual[] population = new Individual[populationSize];
        
        for (int i=0; i<populationSize; i++) {
            population[i] = new Individual(bitFieldSize);
        }

        Solver solver = new Solver(bitFieldSize, populationSize, population);

        solver.run();
    }
}
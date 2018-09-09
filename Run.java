public class Run {
    public static void main(String[] s) {
        int populationSize = 4;
        int bitFieldSize = 5;

        Individual[] population = new Individual[populationSize];
        
        for (int i=0; i<populationSize; i++) {
            population[i] = new Individual(bitFieldSize);
        }

        Solver solver = new Solver(bitFieldSize, populationSize, population);

        // Fitness fn = new Fitness();
        // System.out.println(fn.getFitness(19));

        solver.run();
    }
}
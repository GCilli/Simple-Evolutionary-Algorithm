import java.util.Random;

public class Solver {
    private int populationSize;
    private int bitFieldSize;
    private Individual[] population;
    private Individual[] parents;
    private Individual[] offspring;

    public Solver(int bitFieldSize, int populationSize, Individual[] population) {
        this.populationSize = populationSize;
        this.population = population;
        this.bitFieldSize = bitFieldSize;
    }

    private Individual[] initialize() {

        Random rn = new Random();
        for (Individual candidate: this.population) {
            candidate.setPhenotype(rn.nextInt((int) Math.pow(2, this.bitFieldSize)));
        }

        return this.population;
    }

    private Individual[] evaluateFitness() {
        Fitness fitness_fn = new Fitness();
        for (Individual candidate: this.population) {
            int phenotype = candidate.getPhenotype();
            double fitness = fitness_fn.getFitness(phenotype);
            candidate.setFitness(fitness);
        }
        return this.population;
    }

    private Individual[] selectParent() {

        Individual[] parents = new Individual[this.populationSize];

        // compute average fitness.
        double total_fitness = 0.0;
        for (Individual candidate: population) {
            total_fitness += candidate.getFitness();
        }

        // compute chance to survive
        for (Individual candidate: population) {
            double fitness = candidate.getFitness();
            candidate.setChanceToSurvive(fitness / total_fitness);
        }

        // get all chances to survive
        double[] chancesToSurvive = new double[this.populationSize];
        for (int i=0; i<this.populationSize; i++) {
            chancesToSurvive[i] = population[i].getChanceToSurvive();
        }

        /*
            System.out.println("Chances");
            for (double chance: chancesToSurvive) {
                System.out.println(chance);
            }
        */

        // select parents based on their chance to survive.
        // I'll randomly sample the population four times.
        Random rn = new Random();
        for (int i=0; i<this.populationSize; i++) {
            double selection = rn.nextDouble(); // return double from 0.0 to 1.0
            int id = findIndividualId(chancesToSurvive, selection);
            parents[i] = population[id];
        }

        return parents;
    }

    private Individual[] applyOnePointCrossOver(Individual[] parents) {

        Individual[] offspring = new Individual[this.populationSize];
        Random rn = new Random();

        // I'll couple the first two and the last two.
        int recombination_performed = 0;
        while (recombination_performed < this.populationSize) {
            int split = rn.nextInt(this.bitFieldSize);

            int[] genotype_1 = parents[recombination_performed].getGenotype();
            int[] genotype_2 = parents[recombination_performed+1].getGenotype();

            int[] recombined_genotype_1 = new int[this.bitFieldSize];
            int[] recombined_genotype_2 = new int[this.bitFieldSize];

            for (int i=0; i<this.bitFieldSize; i++) {
                if (i<split) {
                    recombined_genotype_1[i] = genotype_1[i];
                    recombined_genotype_2[i] = genotype_2[i];
                }
                else {
                    recombined_genotype_1[i] = genotype_2[i];
                    recombined_genotype_2[i] = genotype_1[i];
                }
            }

            // instance new children
            offspring[recombination_performed] = new Individual(this.bitFieldSize);
            offspring[recombination_performed+1] = new Individual(this.bitFieldSize);
            
            // set their genotype (phenotype) to the recombined one.
            offspring[recombination_performed].setGenotype(recombined_genotype_1);
            offspring[recombination_performed+1].setGenotype(recombined_genotype_2);
            
            /*
                System.out.println("Recombination step: " + recombination_performed/2 + ", split = " + split);
                System.out.print("Genotype 1: "); for (int i: genotype_1) System.out.print(i); System.out.println();
                System.out.print("Genotype 2: "); for (int i: genotype_2) System.out.print(i); System.out.println();
                System.out.print("Recombined Genotype 1: "); for (int i: recombined_genotype_1) System.out.print(i); System.out.println();
                System.out.print("Recombined Genotype 2: "); for (int i: recombined_genotype_2) System.out.print(i); System.out.println();
                System.out.println();
            */

            recombination_performed += 2;
        }
    
        return offspring;
    }

    private Individual[] applyMutation(Individual[] offspring) {

        Random rn = new Random();
        double mutationRate = 0.8;
        for (Individual candidate: offspring) {
            int[] genotype = candidate.getGenotype();
            for (int i=0; i<this.bitFieldSize; i++) {
                if (rn.nextDouble() > mutationRate) genotype[i] ^= 1;
            }
            candidate.setGenotype(genotype);
        }

        return offspring;
    }



    public void run() {
        
        double globalOptimum = 1.0; // I know the best achievable fitness
        int iteration = 1;
        Individual bestCandidate;

        this.population = this.initialize();
        this.population = this.evaluateFitness();
        
        /*
            System.out.println("Phenotypes after initialization");
            for (Individual candidate: population) {
                System.out.println(candidate.getPhenotype());
            }

            System.out.println("Genotype after initialization");
            for (Individual candidate: population) {
                int[] genotype = candidate.getGenotype();
                for (int i: genotype) System.out.print(i);
                System.out.println(); 
            }
        */

        while (true) {
            this.parents = this.selectParent();

            /*
                System.out.println("Selected parents");
                for (Individual candidate: parents) {
                    System.out.println(candidate.getPhenotype());
                }
            */

            this.offspring = this.applyOnePointCrossOver(this.parents);
            
            /*
                System.out.println("Phenotype after cross-over");
                for (Individual candidate: offspring) {
                    System.out.println(candidate.getPhenotype());
                }
            */

            this.offspring = this.applyMutation(this.offspring);
            this.population = this.offspring;
            this.population = this.evaluateFitness();

            bestCandidate = findOptimum();
            System.out.printf("Iteration: %4d - Best candidate found: %2d - Fitness: %2.2f\n", iteration, bestCandidate.getPhenotype(), bestCandidate.getFitness());
            

            if (bestCandidate.getFitness() == globalOptimum)
                break;

            iteration++;
        }

        System.out.println("Number of iterations to terminate: " + iteration);
    }

    private int findIndividualId(double[] chancesToSurvive, double selection) {
        
        // incremental probabilities
        for (int i=1; i<this.populationSize; i++)
            chancesToSurvive[i] += chancesToSurvive[i-1];

        int id = 0;
        while (id < this.populationSize-1 && selection > chancesToSurvive[id])
            id++;

        return id;
    }

    private Individual findOptimum() {
        Individual bestCandidate = new Individual(this.bitFieldSize);
        double bestFitness = -100.0 ;
        for (Individual candidate: this.population) {
            if (candidate.getFitness() > bestFitness) {
                bestFitness = candidate.getFitness();
                bestCandidate = candidate;
            }
        }

        return bestCandidate; 
    }
}
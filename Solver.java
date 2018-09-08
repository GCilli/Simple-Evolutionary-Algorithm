import java.util.Random;

public class Solver {
    private int populationSize;
    private int bitFieldSize;
    private Individual[] population;

    public Solver(int bitFieldSize, int populationSize, Individual[] population) {
        this.populationSize = populationSize;
        this.population = population;
        this.bitFieldSize = bitFieldSize;
    }

    private void initialize() {

        Random rn = new Random();

        for (Individual candidate: population) {
            candidate.setPhenotype(rn.nextInt(1024));
        }
    }

    private Individual[] selectParent() {

        Fitness fitness_fn = new Fitness();
        Individual[] parents = new Individual[this.populationSize];

        // get fitnesses.
        for (Individual candidate: population) {
            int phenotype = candidate.getPhenotype();
            int fitness = fitness_fn.getFitness(phenotype);
            candidate.setFitness(fitness);
        }

        // compute average fitness.
        double total_fitness = 0.0;
        for (Individual candidate: population) {
            total_fitness += candidate.getFitness();
        }

        // compute chance to survive
        for (Individual candidate: population) {
            int fitness = candidate.getFitness();
            candidate.setChanceToSurvive(fitness / total_fitness);
        }

        // get all chances to survive
        double[] chancesToSurvive = new double[this.populationSize];
        for (int i=0; i<this.populationSize; i++) {
            chancesToSurvive[i] = population[i].getChanceToSurvive();
        }

        // System.out.println("Chances");
        // for (double chance: chancesToSurvive) {
        //     System.out.println(chance);
        // }

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

            // System.out.println("Recombination step: " + recombination_performed/2 + ", split = " + split);
            // System.out.print("Genotype 1: "); for (int i: genotype_1) System.out.print(i); System.out.println();
            // System.out.print("Genotype 2: "); for (int i: genotype_2) System.out.print(i); System.out.println();
            // System.out.print("Recombined Genotype 1: "); for (int i: recombined_genotype_1) System.out.print(i); System.out.println();
            // System.out.print("Recombined Genotype 2: "); for (int i: recombined_genotype_2) System.out.print(i); System.out.println();
            // System.out.println();

            recombination_performed += 2;
        }
    
        return offspring;
    }

    private Individual[] applyMutation(Individual[] offspring) {

        Random rn = new Random();
        double mutationRate = 0.98;
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
        this.initialize();

        // System.out.println("Phenotypes after initialization");
        // for (Individual candidate: population) {
        //     System.out.println(candidate.getPhenotype());
        // }

        // System.out.println("Genotype after initialization");
        // for (Individual candidate: population) {
        //     int[] genotype = candidate.getGenotype();
        //     for (int i: genotype) System.out.print(i);
        //     System.out.println(); 
        // }

        int iteration = 0;
        while (iteration < 500) {

            Individual[] parents = this.selectParent();

            // System.out.println("Selected parents");
            // for (Individual candidate: parents) {
            //     System.out.println(candidate.getPhenotype());
            // }


            Individual[] offspring = this.applyOnePointCrossOver(parents);

            // System.out.println("Phenotype after cross-over");
            // for (Individual candidate: offspring) {
            //     System.out.println(candidate.getPhenotype());
            // }

            offspring = this.applyMutation(offspring);
            this.population = offspring;

            iteration++;
        }
    }

    private int findIndividualId(double[] chancesToSurvive, double selection) {
        
        // incremental probabilities
        for (int i=1; i<this.populationSize; i++) {
            chancesToSurvive[i] += chancesToSurvive[i-1];
        }

        int id = 0;
        while (id < this.populationSize && selection > chancesToSurvive[id]) {
            id++;
        }

        return id;
    }
}
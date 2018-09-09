public class Fitness {
    public double getFitness(int phenotype) {
        return this.custom(phenotype);
    }

    private int square(int phenotype) {
        return (int) Math.pow(phenotype, 2);
    }
    private double custom(int phenotype) {
        double d_phenotype = phenotype / 10.0 - 1.5; 
        double d_fitness = Math.cos(1.3 * d_phenotype) * Math.cos(40.0 * d_phenotype);
        return d_fitness;
    }   
}
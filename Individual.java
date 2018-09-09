public class Individual {
    private int phenotype;
    private int bitFieldSize;
    private int[] genotype;
    private double fitness;
    private double chanceToSurvive;

    public Individual(int bitFieldSize) {
        this.bitFieldSize = bitFieldSize;
        this.genotype = new int[bitFieldSize];
    }

    public void setPhenotype(int phenotype) {
        this.phenotype = phenotype;
        this.updateGenotype();
    }

    public int getPhenotype() {
        return this.phenotype;
    }

    public void setGenotype(int[] genotype) {
        this.genotype = genotype;
        this.updatePhenotype();
    }

    public int[] getGenotype() {
        return this.genotype;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getFitness() {
        return this.fitness;
    }

    public void setChanceToSurvive(double chanceToSurvive) {
        this.chanceToSurvive = chanceToSurvive;
    }

    public double getChanceToSurvive() {
        return this.chanceToSurvive;
    }

    private void updatePhenotype() {
        this.phenotype = 0;
        for (int i=0; i<this.bitFieldSize; i++) {
            this.phenotype += Math.pow(2, this.bitFieldSize-1-i) * this.genotype[i];
        }
    }

    private void updateGenotype() {
        int pos = this.bitFieldSize-1;
        int tmp = this.phenotype;
        while (pos >= 0) {
            this.genotype[pos] = tmp % 2;
            tmp /= 2; 
            pos--;
        }
    }



}
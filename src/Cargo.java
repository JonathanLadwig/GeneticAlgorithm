import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Cargo {
    String[] population;
    Parcel[] parcelOptions;
    String bestString;

    public String[] getPopulation() {
        return population;
    }

    public Parcel[] getParcelOptions() {
        return parcelOptions;
    }

    public void setParcelOptions(int numParcel){
        parcelOptions = new Parcel[numParcel];
    }

    //sets the package at the index to the same values as given of the txt
    public void createParcelOptions(Parcel p, int index){
            parcelOptions[index] = p;
    }

    public void createPopulation(int numParcels){
        int populationNum = (int) Math.pow(2, Math.ceil(Math.log(numParcels*2)/Math.log(2)));
        System.out.println("Population Number: " + populationNum);
        population = new String[populationNum]; //setting the size of the population we want
        for(int x = 0; x < populationNum; x++){
            String cargo = "";
            for (int y = 0; y < numParcels; y++){
                if(Math.random() > 0.5){
                    cargo += 1;
                }else{
                    cargo += 0;
                }
                //makes it either a zero or one
            }
            //System.out.println(cargo);
            population[x] = cargo;
        }
    }

    public void sortPopulation(int capacity){
        //bubbleSort
        for(int x = 0; x < population.length - 1; x++){
            Boolean swapped = false;
            for (int j = 0; j < population.length - 1; j++) {
                if (getFitness(population[j], capacity) < getFitness(population[j+1], capacity))
                {
                    String temp = population[j];
                    population[j]= population[j+1];
                    population[j+1] = temp;
                    //System.out.println("Swap occurred!");
                    swapped = true;
                }
            }
            if(!swapped)
                break;
        }
    }

    public void tournament(){
        //code to do tournament select if I have time
    }

    public int getFitness(String cargo, int capacity){
        //System.out.println("Cargo String:" + cargo);
        int fitness;
        int totalWeight = 0;
        int totalValue = 0;
        for (int x = 0; x < cargo.length(); x++){
            if(cargo.charAt(x) == '1'){
                //System.out.println("Value is a 1");
                totalValue += parcelOptions[x].getValue();
                totalWeight += parcelOptions[x].getWeight();
            }
            if(totalWeight > capacity){
                //System.out.println("Over capacity");
                return 0;
            }
        }
        fitness = totalValue;
        //System.out.println("Fitness: " + fitness);
        return fitness;
    }

    public int getCargoWeight(String cargo){
        int totalWeight = 0;
        for (int x = 0; x < cargo.length(); x++){
            if(cargo.charAt(x) == '1') {
                totalWeight += parcelOptions[x].getWeight();
            }
        }
        return totalWeight;
    }

    public void prunePopulation(){
        for (int x = population.length/2; x < population.length; x++){
            population[x] = null;
        }
    }

    public void breedPopulation(int capacity){
        //takes the next member of the population and breeds it with another random member
        boolean[] taken = new boolean[population.length/2];
        for (int y = 0; y < taken.length; y++){
            taken[y] = false;
        }
        boolean match;
        int random;
        for(int x = 0; x < population.length/2; x++){
            //System.out.println("Index:" + x + ", Taken = " + taken[x]);
            if(!taken[x]){
                taken[x] = true;
                match = false;
                while (!match){
                    random = ThreadLocalRandom.current().nextInt(x+1, population.length/2);
                    //System.out.println("Random:" + random);
                    if(!taken[random]){
                        taken[random] = true;
                        match = true;
                        createChildren(population[x], population[random], x, capacity);
                    }
                }
            }
        }
    }

    public void createChildren(String parentA, String parentB, int index, int capacity){
        String childA = "";
        String childB = "";
        for(int x = 0; x < Math.ceil(parentA.length()/2); x++){
            childA += parentA.charAt(x);
            childB += parentB.charAt(x);
        }
        for (int y = (int)Math.ceil(parentA.length()/2); y < parentA.length(); y++){
            childA += parentB.charAt(y);
            childB += parentA.charAt(y);
        }
        //System.out.println("");
        //System.out.println("Before Mutation");
        //System.out.println("ChildA:" + childA + ", ChildB:"+ childB);

        //mutation
        childA = Mutate(childA, capacity);
        childB = Mutate(childB, capacity);

        //System.out.println("");
        //System.out.println("After Mutation");
        //System.out.println("ChildA:" + childA + ", ChildB:"+ childB);
        //System.out.println("");

        //adding the children into the population
        for(int x = 0; x < population.length; x++){
            if (population[x] == null){
                population[x] = childA;
                break;
            }
        }
        for(int x = 0; x < population.length; x++){
            if (population[x] == null){
                population[x] = childB;
                break;
            }
        }
    }

    public String Mutate(String child, int capacity){
        int range = 0;
        int totalValue = 0;
        int adjTotal = 1;
        ArrayList<Double> adjValues = new ArrayList<>();
        ArrayList<Integer> index = new ArrayList<>();
        ArrayList<Integer> value = new ArrayList<>();
        //LinkedHashMap<Integer, Integer> items = new LinkedHashMap<>();
        //NEEDS TO BE DONE store items as percentage in same area
        if(getCargoWeight(child) > capacity){
            //removes an item to make the weight less than capacity
            //items with a LOWER value are more likely to be taken out
            adjTotal = 0;
            range = getCargoWeight(child) - capacity;
            //finds the total value and sets the items in the list
            for(int x = 0; x < child.length(); x++){
                if(child.charAt(x) == '1' && parcelOptions[x].getWeight() >= range){
                    index.add(x);
                    value.add(parcelOptions[x].getValue());
                    totalValue += parcelOptions[x].getValue();
                }
            }
            //actually setting the adjusted values based off total value
            for(int y = 0; y < index.size(); y++){
                adjValues.add((double) (totalValue/value.get(y)));
                adjTotal += totalValue/value.get(y);
            }
        }else{
            //adds an item to make the weight closer to capacity
            //items with a HIGHER value are more likely to be put in
            range = capacity - getCargoWeight(child);
            for(int x = 0; x < child.length(); x++){
                if(child.charAt(x) == '0' && parcelOptions[x].getWeight() <= range){
                    index.add(x);
                    value.add(parcelOptions[x].getValue());
                    totalValue += parcelOptions[x].getValue();
                }
            }
            //actually setting the percentage values based off total value
            for(int y = 0; y < index.size(); y++){
                adjValues.add(((double)value.get(y)/totalValue));
            }
        }
        //changing a biased random bit
        double random = Math.random()*adjTotal;
        //System.out.println("Random:" + random);
        boolean withinRange = false;
        double total = 0;
        int counter = 0;
        char[] c;

        //choosing the bit
        do {
            try{
                total += adjValues.get(counter);
            //if it falls within the range
            if (random <= total){
                if(child.charAt(index.get(counter)) == '0'){
                    //change the value at this index to 1
                    c = child.toCharArray();
                    c[index.get(counter)] = '1';
                    child = String.valueOf(c);
                }else{
                    //change the value at this index to 0
                    c = child.toCharArray();
                    c[index.get(counter)] = '0';
                    child = String.valueOf(c);
                }
                withinRange = true;
                return child;
            }
            }catch(IndexOutOfBoundsException e){
                return child;
            }
            counter++;
        }while(!withinRange);

        return child;
    }

    public int getBest(int capacity){
        //gets top item in the population array
        int bestValue = 0;
        for(int y = 0; y < population.length; y++) {
            int totalValue = getFitness(population[y], capacity);
            if(totalValue > bestValue){
                bestValue = totalValue;
                bestString = population[y];
            }
        }
        return bestValue;
    }

    public String getBestString(){
        return bestString;
    }

    public void printPopulation(int capacity){
        System.out.println("Print Population Called");
        for(int x = 0; x < population.length; x++){
            System.out.println("Cargo"+ x + ":" + population[x]);
            if(population[x] != null){
                System.out.println(getFitness(population[x], capacity));
            }else{
                System.out.println("No value");
            }
        }
    }
}

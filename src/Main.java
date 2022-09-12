import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	// initialising the variables
        int capacity = 0;
        int quota = 0;
        int numParcels = 0;
        boolean reachQuota = false;
        String answer;
        Cargo c = new Cargo();

        //user input for file pathname
        int terminationCount = 0;
        String pathName = "";
        Scanner userScan = new Scanner(System.in);
        System.out.println("Please enter the file path");
        pathName = userScan.nextLine();
        //System.out.println("Filename:" + pathName);

        System.out.println("Please enter the termination counter");
        terminationCount = userScan.nextInt();
        //System.out.println("Termination Count:" + terminationCount);

        System.out.println("Would you like to break as soon as the quota is filled? (Y/N)");
        answer = userScan.next();
        if(answer.charAt(0) == 'Y' || answer.charAt(0) == 'y'){
            reachQuota = true;
        }
        //System.out.println("Fill Quota:" + reachQuota);
        System.out.println();

        //reading from file
        try {
            File file = new File(pathName);
            Scanner myReader = new Scanner(file);
            myReader.nextLine();
            capacity = myReader.nextInt();
            System.out.println("Capacity: " + capacity);
            quota = myReader.nextInt();
            System.out.println("Quota: " +quota);
            numParcels = myReader.nextInt();
            System.out.println("Num Parcels: " +numParcels);
            c.setParcelOptions(numParcels);

            for(int x = 0; x < numParcels; x++){
                Parcel p = new Parcel();
                p.setName(myReader.next());
                p.setWeight(myReader.nextInt());
                p.setValue(myReader.nextInt());
                c.createParcelOptions(p, x);
                //System.out.println("Parcel" + (x+1) + ": " + p.getName() + p.getWeight() + p.getValue());
            }
/*            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }*/
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        //file reading done

        //ACTUALLY RUNNING THE ALGORITHM STARTS HERE
        int counter = 0;
        int best = 0;
        int iterationCounter = 0;
        c.createPopulation(numParcels);
        while(counter < terminationCount){
            iterationCounter++;
            c.sortPopulation(capacity);
            c.prunePopulation();
            c.breedPopulation(capacity);
            if(best < c.getBest(capacity)){
                best = c.getBest(capacity);
                counter = 0;
            }
            if(best == c.getBest(capacity)){
                counter++;
            }
            if(best >= quota && reachQuota){
                break;
            }
        }
        System.out.println();
        System.out.println("Best Value is " + best);
        System.out.println("Best String is " + c.getBestString());
        System.out.println("Number of iterations: " + iterationCounter);
    }
}

package adAuctions.structures;

/**
 * Created by Daniel on 9/30/15.
 */
public class Allocation {
    public double value_per_customer;
    public int num_customers;

    public Allocation(double value_per_customer, int num_customers) {
        this.value_per_customer = value_per_customer;
        this.num_customers = num_customers;
    }

    public static Allocation[][] copy2D(Allocation[][] input){
        int x = input.length;
        int y = input[0].length;
        Allocation[][] toreturn = new Allocation[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                toreturn[i][j] = new Allocation(input[i][j].value_per_customer,input[i][j].num_customers);
            }
        }
        return toreturn;
    }

    public void print(){
        System.out.printf("(%d,%f)",this.num_customers,this.value_per_customer);
    }
}



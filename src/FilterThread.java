
import java.util.concurrent.RecursiveTask;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author shakeel
 */
public class FilterThread extends RecursiveTask<Double> {

    int lo; // arguments
    int hi;
    double[] arr;
    static final int SEQUENTIAL_CUTOFF = 500;

    //int ans = 0; // result 

    FilterThread(double[] a, int l, int h) {
        lo = l;
        hi = h;
        arr = a;
    }

    protected Double compute() {// return answer - instead of run
        if ((hi - lo) < SEQUENTIAL_CUTOFF) {
            //System.out.println("lo is " + lo + " hi is " + hi);
            Main.ParallelFilter(arr, lo, hi);
            return 1.0;
            
        } else {
            FilterThread left = new FilterThread(arr, lo, (hi + lo) / 2);
            FilterThread right = new FilterThread(arr, (hi + lo) / 2, hi);
            // order of next 4 lines
            // essential Ð why?
            left.fork();
            double rightAns = right.compute();
            double leftAns = left.join();
            return leftAns + rightAns;
        }
    }

}

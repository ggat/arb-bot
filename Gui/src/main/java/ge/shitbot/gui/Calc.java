package ge.shitbot.gui;

import java.math.BigDecimal;

/**
 * Created by giga on 10/1/17.
 */
public class Calc {

    public static class Pair<T> {
        private T a;
        private T b;

        public Pair(T a, T b) {
            this.a = a;
            this.b = b;
        }
    }

    //TODO: Use big decimals for precision.
    public static Double profit(Double a, Double b) {
        return 100 - ((1 / a * 100) + (1 / b * 100));
    }

    public static Pair<Double> proportions(Double a, Double b) {
        Double sum = a + b;

        return new Pair<>(a / sum, b / sum);
    }

    public static Pair<Double> stakes(Double totalStake, Double a, Double b) {

        Pair<Double> props = proportions(a, b);

        Double stakeA = totalStake * props.b;
        Double stakeB = totalStake * props.a;

        if(stakeA > stakeB) {
            stakeB = Math.ceil(stakeB);
            stakeA = totalStake - stakeB;
        } else {
            stakeA = Math.ceil(stakeA);
            stakeB = totalStake - stakeA;
        }

        return new Pair<>(stakeA, stakeB);
    }

    public static Pair<Double> wins(Double totalStake, Double a, Double b) {
        Pair<Double> stakes = stakes(totalStake, a, b);

        return new Pair<>((stakes.a * a) - totalStake, (stakes.b * b) - totalStake);
    }

    public static void main(String[] args) {

        Double a, b;
        a = 1.45;
        b = 5.5;

        System.out.printf("profit: %f\n", profit(a, b));
        System.out.printf("stakes: %f - %f\n", stakes(100.0, a, b).a, stakes(100.0, a, b).b);
        System.out.printf("wins: %f - %f\n", wins(100.0, a, b).a, wins(100.0, a, b).b);
    }
}

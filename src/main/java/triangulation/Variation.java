package triangulation;

import java.math.BigDecimal;
import java.util.LinkedList;

public class Variation{
    private BigDecimal variationAmount;
    private LinkedList<Trade> path;

    public Variation(BigDecimal variationAmount, LinkedList<Trade> path) {
        this.variationAmount = variationAmount;
        this.path = path;
    }

    public BigDecimal getVariationAmount() {
        return variationAmount;
    }

    public LinkedList<Trade> getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "Variation{" +
                "variationAmount=" + variationAmount +
                "%, path=" + path +
                '}';
    }
}

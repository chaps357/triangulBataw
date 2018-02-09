package triangulation;

import java.math.BigDecimal;
import java.util.LinkedList;

public class PriceVariation {
    private BigDecimal variationAmount;
    private LinkedList<Trade> path;

    public PriceVariation(BigDecimal variationAmount, LinkedList<Trade> path) {
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
                "%, Nb of Trade="+path.size()+", path=" + path +
                '}';
    }
}

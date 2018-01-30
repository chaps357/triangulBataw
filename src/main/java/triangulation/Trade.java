package triangulation;

import java.math.BigDecimal;

public class Trade {
    private final String origin;
    private final String target;
    private final BigDecimal price;

    public Trade(String origin, String target, BigDecimal price) {
        this.origin = origin;
        this.target = target;
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trade trade = (Trade) o;

        if (!origin.equals(trade.origin)) return false;
        return target.equals(trade.target);
    }

    @Override
    public int hashCode() {
        int result = origin.hashCode();
        result = 31 * result + target.hashCode();
        return result;
    }

    public String getOrigin() {
        return origin;
    }

    public String getTarget() {
        return target;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "origin='" + origin + '\'' +
                ", target='" + target + '\'' +
                ", price=" + price +
                '}';
    }
}

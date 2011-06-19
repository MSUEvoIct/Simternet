package simternet.nsp;

public interface InterconnectPricingStrategy {
	public Double getInterconnectPrice(NetworkProvider other);
}

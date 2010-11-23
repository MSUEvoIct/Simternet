package xcs;

public class AgentData {

	private double m_deltaRevenue;

	public AgentData() {
		this(0.0);
	}

	public AgentData(Double rev) {
		this.m_deltaRevenue = rev;
	}

	public double getM_deltaRevenue() {
		return this.m_deltaRevenue;
	}

	public void setM_deltaRevenue(double m_deltaRevenue) {
		this.m_deltaRevenue = m_deltaRevenue;
	}

}

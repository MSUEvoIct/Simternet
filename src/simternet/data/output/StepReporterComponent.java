package simternet.data.output;

import simternet.engine.Simternet;

public class StepReporterComponent extends ReporterComponent {
	private static final long	serialVersionUID	= 1L;
	private Simternet			s;
	private static final int	numFields			= 1;

	public StepReporterComponent(Simternet s) {
		this.s = s;
	}

	@Override
	public String[] getHeaders() {
		String headers[] = new String[StepReporterComponent.numFields];
		headers[0] = "Step";
		return headers;
	}

	@Override
	public Object[] getValues() {
		Object[] values = new Object[StepReporterComponent.numFields];
		values[0] = s.schedule.getSteps();
		return values;
	}

}

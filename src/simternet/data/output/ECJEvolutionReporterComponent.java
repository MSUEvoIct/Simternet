package simternet.data.output;

import simternet.engine.Simternet;

public class ECJEvolutionReporterComponent extends ReporterComponent {
	private static final long	serialVersionUID	= 1L;
	private Simternet			s;
	public static final int		numFields			= 2;

	public ECJEvolutionReporterComponent(Simternet s) {
		this.s = s;
	}

	@Override
	public String[] getHeaders() {
		String headers[] = new String[ECJEvolutionReporterComponent.numFields];
		headers[0] = "Generation";
		headers[1] = "Chunk";
		return headers;
	}

	@Override
	public Object[] getValues() {
		Object values[] = new Object[ECJEvolutionReporterComponent.numFields];
		values[0] = s.generation;
		values[1] = s.chunk;
		return values;
	}

}

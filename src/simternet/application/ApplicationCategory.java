package simternet.application;

import java.util.Set;

import simternet.temporal.TemporalHashSet;

/**
 * Represents a specific set of <i>like</i> applications, such that members
 * within the set are economic substitutes, and non-members are not.
 * 
 * @author kkoning
 * 
 */
public class ApplicationCategory {
	private TemporalHashSet<ApplicationServiceProvider> applications;

	public void addApplication(ApplicationServiceProvider asp) {
		this.applications.add(asp);
	}

	public Set<ApplicationServiceProvider> getApplications() {
		return this.applications;
	}

	public boolean isMember(ApplicationServiceProvider asp) {
		return this.applications.contains(asp);
	}

}

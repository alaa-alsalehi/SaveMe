package com.serveme.savemyphone.model;

public class Launcher {
	private String packageName;
	private String activity;

	public Launcher(String packageName, String activity) {
		this.packageName = packageName;
		this.activity = activity;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getActivity() {
		return activity;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Launcher) {
			Launcher launcher = (Launcher) o;
			StringBuilder myActivityPath = new StringBuilder();
			myActivityPath.append(getPackageName()).append(getActivity());
			StringBuilder otherActivityPath = new StringBuilder();
			otherActivityPath.append(launcher.getPackageName()).append(
					launcher.getActivity());
			return myActivityPath.toString().equals(otherActivityPath.toString());
		} else
			return false;
	}

	@Override
	public int hashCode() {
		StringBuilder builder = new StringBuilder();
		builder.append(getPackageName()).append(getActivity());
		return builder.toString().hashCode();
	}

	@Override
	public String toString() {
		return "Package Name " + getPackageName() + " Activity "
				+ getActivity();
	}
}

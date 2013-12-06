package com.serveme.savemyphone.model;

public class Launcher {
	private String packageName;
	private String activity;

	public Launcher(String packageName, String activity) {
		super();
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
			String myActivityPath = getPackageName() + " " + getActivity();
			String otherActivityPath = launcher.getPackageName() + " "
					+ launcher.getActivity();
			return myActivityPath.equals(otherActivityPath);
		} else
			return false;
	}

	@Override
	public int hashCode() {
		String myActivityPath = getPackageName() + " " + getActivity();
		return myActivityPath.hashCode();
	}

	@Override
	public String toString() {
		return "Package Name " + getPackageName() + " Activity "
				+ getActivity();
	}
}

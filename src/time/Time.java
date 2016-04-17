package time;

public enum Time {
    MILLISEC(1), SECOND(1000), MINUTE(60000), HOUR(3600000);
	
	int value;
	
	Time(int value) {
		this.value = value;
	}
	
	Time(Time rhs) {
		this.value = rhs.value;
	}
	
	public int value() { return this.value; }
}

package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import signal.Signal;
import time.Time;

public class Main {
	public static void main(String[] argv) {
		long startupTime = System.currentTimeMillis();
		Signal<String> signal = Signal.every(1, Time.SECOND, "");
		
		Signal<String> lastLine = new Signal<String>("");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		Thread t = new Thread(() -> {
			for(;;) {
				String ln;
				try {
					ln = reader.readLine();
				} catch (IOException e) {}
			}
		});
		t.start();
		
		Signal<String> reactive = lastLine.join(signal, (x,y) -> x + y);
		reactive.setAction(() -> {
			long uptime = System.currentTimeMillis() - startupTime;
			System.out.print(reactive.getValue() + ", uptime: " + uptime);
		});
	}
}

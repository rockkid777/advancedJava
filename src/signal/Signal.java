package signal;

import java.util.HashSet;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import time.Time;

public class Signal<T> {
	private T value;
	private Optional<Runnable> action = Optional.empty();
	public Runnable update;
	private HashSet<Signal> subscribers = new HashSet<>();
	
	public Signal(T value) {
		this.value = value;
	}
	
	public Signal(T value, Optional<Runnable> action) {
		this.value = value;
		this.action = action;
	}
	
	public T getValue() {
		return value;
	}
	
	public void setValue(T value) {
		this.value = value;
		if (action.isPresent()) {
			action.get().run();
		}
		subscribers.stream().forEach(s -> s.update.run());
	}

	public Optional<Runnable> getAction() {
		return action;
	}

	public void setAction(Runnable action) {
		this.action = Optional.of(action);
	}
	
	public <R> Signal<R> map(Function<T,R> fn) {
		Signal<R> result = new Signal<>(fn.apply(this.value));
		result.update = () -> result.setValue(fn.apply(this.getValue()));
		subscribers.add(result);
		return result;
	}
	
	public <S, R> Signal<R> join(Signal<S> rhs, BiFunction<T, S, R> fn) {
		Signal<R> result = new Signal<R>(fn.apply(this.value, rhs.getValue()));
		result.update = () -> result.setValue(fn.apply(this.value, rhs.getValue()));
		rhs.subscribers.add(result);
		this.subscribers.add(result);
		return result;
	}
	
	public <R> Signal<R> accumulate(BiFunction<R, T, R> fn, R fstValue) {
		Signal<R> result = new Signal<R>(fstValue);
		result.update = () -> result.setValue(fn.apply(result.getValue(), this.value));
		this.subscribers.add(result);
		return result;
	}
	
	public static <R> Signal<R> every(int intervall, Time timeUnit, R value) {
		Signal<R> result = new Signal<R>(value);
		result.update = () -> {};
		Thread thread = new Thread(() -> {
			for(;;) {
				try {
					Thread.sleep(intervall * timeUnit.value());
				} catch (InterruptedException e) {}
				result.setValue(result.getValue());
			}
		});
		try {
			thread.join();
		} catch (InterruptedException e) {}
		thread.start();
		
		return result;
	}
}

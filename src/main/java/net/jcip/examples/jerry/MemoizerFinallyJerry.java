package net.jcip.examples.jerry;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class MemoizerFinallyJerry<A, V> implements Computable<A, V> {

	private final Map<A, FutureTask<V>> cache = new ConcurrentHashMap<A, FutureTask<V>>();
	private final Computable<A, V> c;

	public MemoizerFinallyJerry(Computable<A, V> c) {
		this.c = c;
	}

	@Override
	public V compute(A arg) throws InterruptedException {
		while (true) {
			Future<V> f = cache.get(arg);
			if (f == null) {
				Callable<V> eval = new Callable<V>() {
					public V call() throws InterruptedException {
						return c.compute(arg);
					}
				};
				FutureTask<V> ft = new FutureTask<V>(eval);
				// 若没有则新增，在新增处设置同步，可以提高并发性能，减少锁块
				f = cache.putIfAbsent(arg, ft);
				if (f == null) {
					f = ft;
					ft.run();//这里将调用c.compute(arg)
				}
			}
			try {
				f.get();
			} catch (ExecutionException e) {
				cache.remove(arg, f);
				e.printStackTrace();
			}
		}
	}

}

interface Computable<A, V> {
	V compute(A arg) throws InterruptedException;
}

class ExpensiveFunction implements Computable<String, BigInteger> {
	public BigInteger compute(String arg) {
		// after deep thought...
		return new BigInteger(arg);
	}
}
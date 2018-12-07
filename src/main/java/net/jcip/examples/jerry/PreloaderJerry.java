package net.jcip.examples.jerry;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;


public class PreloaderJerry {

	private final FutureTask<ProductInfo> future = new FutureTask<ProductInfo>(new Callable<ProductInfo>() {
		@Override
		public ProductInfo call() throws Exception {
			return loadProductInfo();
		}

	});
	
	private final Thread thread = new Thread(future);
	
	public void start(){
		thread.start();
	}

	interface ProductInfo {
	}

	ProductInfo loadProductInfo() throws DataLoadException {
		return null;
	}
}

class DataLoadException extends Exception{
	
}

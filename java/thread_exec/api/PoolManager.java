package thread_exec.api;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;

/**
 * 
 * Java bindings for the api.
 * 
 * Use:
 * 
 * <code>
 * 
 * import static thread_exec.api.PoolManager;
 * 
 * Object p = defaultPoolManager(100, 4, {0 100}
 * 
 * </code>
 */
public class PoolManager {
	
	static{
		RT.var("clojure.core", "use").invoke(Symbol.create("thread-exec.core"));

			
	}
	
	public static final Object defaultPoolManager(int threshold, int maxGroups, int[] startGroup, int poolSize){
		return RT.var("thread-exec.core", "default-pool-manager").invoke(threshold, maxGroups, 
				RT.var("clojure.core", "vector").invoke(startGroup[0], startGroup[1]), poolSize);
	}
	
	public static final ExecutorService submit(Object manager, String topic, Runnable r){
		return (ExecutorService) RT.var("thread-exec.core", "submit").invoke(manager, topic, toFunction(r));
	}
	
	public static final ExecutorService submit(Object manager, String topic, Callable<?> r){
		return (ExecutorService) RT.var("thread-exec.core", "submit").invoke(manager, topic, toFunction(r));
	}
	
	public static final ExecutorService submit(Object manager, String topic, IFn r){
		return (ExecutorService) RT.var("thread-exec.core", "submit").invoke(manager, topic, r);
	}
	
	public static final Object shutdown(Object manager, long timeout){
		return RT.var("thread-exec.core", "shutdown").invoke(manager, timeout);
	}
	
	private static final IFn toFunction(final Runnable r){
		return new AFn() {

			@Override
			public Object invoke() {
				r.run();
				return null;
			}
			
		};
	}
	
	private static final IFn toFunction(final Callable<?> r){
		return new AFn() {

			@Override
			public Object invoke() {
				try {
					return r.call();
				} catch (Exception e) {
					throw new RuntimeException(e.toString(), e);
				}
			}
			
		};
	}
	
}

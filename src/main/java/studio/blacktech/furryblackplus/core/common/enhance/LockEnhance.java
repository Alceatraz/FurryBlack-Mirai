package studio.blacktech.furryblackplus.core.common.enhance;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockEnhance {

  public static class Latch {

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public void await() {
      lock.lock();
      try {
        condition.await();
      } catch (InterruptedException exception) {
        exception.printStackTrace();
      } finally {
        lock.unlock();
      }
    }

    public void signal() {
      lock.lock();
      try {
        condition.signal();
      } finally {
        lock.unlock();
      }
    }

  }

}

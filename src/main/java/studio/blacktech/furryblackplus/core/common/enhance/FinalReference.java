package studio.blacktech.furryblackplus.core.common.enhance;

import java.util.concurrent.atomic.AtomicReference;

public class FinalReference<T> {

  private final int index;
  private final String check;
  private final AtomicReference<T> reference = new AtomicReference<>();

  public FinalReference() {
    index = 0;
    check = null;
  }

  public FinalReference(int index, String check) {
    this.index = index;
    this.check = check;
  }

  private String extraceStackTrace(StackTraceElement[] stackTrace) {
    int i = 0;
    StringBuilder builder = new StringBuilder();
    for (StackTraceElement stackTraceElement : stackTrace) {
      builder
        .append(i++)
        .append(" ")
        .append(stackTraceElement.getClass().getName())
        .append(":")
        .append(stackTraceElement.getMethodName())
        .append("(")
        .append(stackTraceElement.getLineNumber())
        .append(")");
    }
    return builder.toString();
  }

  public String test() {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    return extraceStackTrace(stackTrace);
  }

  public void set(T instance) {
    if (index > 0 && check != null) {
      StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
      StackTraceElement stackTraceElement = stackTraceElements[index];
      if (stackTraceElement.getClassName().equals(check)) {
        setInternal(instance);
      } else if (!(stackTraceElement.getClassName() + ":" + stackTraceElement.getMethodName()).equals(check)) {
        setInternal(instance);
      } else {
        throw new IllegalStateException("Caller invalid - class mismatch");
      }
    }
  }

  private void setInternal(T instance) {
    synchronized (reference) {
      if (reference.get() != null) {
        throw new IllegalStateException("Value already set, Override not allow.");
      }
      reference.set(instance);
    }
  }

}

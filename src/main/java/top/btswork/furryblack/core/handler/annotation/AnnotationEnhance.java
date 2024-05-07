package top.btswork.furryblack.core.handler.annotation;

public class AnnotationEnhance {

  //= ================================================================================================================
  //=  顺序机制
  //= ================================================================================================================

  public static int compare(Runner o1, Runner o2) {
    return o1.priority() - o2.priority();
  }

  public static int compare(Filter o1, Filter o2) {
    return o1.priority() - o2.priority();
  }

  public static int compare(Monitor o1, Monitor o2) {
    return o1.priority() - o2.priority();
  }

  public static int compare(Checker o1, Checker o2) {
    return o1.priority() - o2.priority();
  }

  public static int compare(Executor o1, Executor o2) {
    return CharSequence.compare(o1.command(), o2.command());
  }

  //= ================================================================================================================
  //= 友好打印
  //= ================================================================================================================

  public static String printAnnotation(Runner annotation) {
    return annotation.value() + '[' + annotation.priority() + ']';
  }

  public static String printAnnotation(Filter annotation) {
    return annotation.value() + '[' + annotation.priority() + "]{" + (annotation.users() ? "U" : "") + (annotation.group() ? "G" : "") + "}";
  }

  public static String printAnnotation(Monitor annotation) {
    return annotation.value() + '[' + annotation.priority() + "]{" + (annotation.users() ? "U" : "") + (annotation.group() ? "G" : "") + "}";
  }

  public static String printAnnotation(Checker annotation) {
    return annotation.value() + '[' + annotation.priority() + ']' + '(' + annotation.command() + "){" + (annotation.users() ? "U" : "") + (annotation.group() ? "G" : "") + "}";
  }

  public static String printAnnotation(Executor annotation) {
    return annotation.value() + '(' + annotation.command() + "){" + (annotation.users() ? "U" : "") + (annotation.group() ? "G" : "") + "}";
  }

}

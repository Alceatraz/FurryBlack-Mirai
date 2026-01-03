package top.btswork.furryblack.core.config;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public  final class Argument {

  public static Argument parse(String[] arguments) {
    Argument instance = new Argument();
    int length = arguments.length;
    for (int i = 0; i < length; i++) {
      String argument = arguments[i].trim();
      if (argument.startsWith("--")) {
        if (i + 1 == length) {
          instance.options.add(argument.substring(2));
          break;
        }
        String next = arguments[i + 1];
        if (next.startsWith("--")) {
          instance.options.add(argument.substring(2));
        } else {
          instance.parameters.put(argument.substring(2), next);
          i++;
        }
      } else {
        instance.args.add(argument);
      }
    }
    return instance;
  }

  private final List<String> args;
  private final List<String> options;
  private final Map<String, String> parameters;

  private Argument() {
    args = new LinkedList<>();
    options = new LinkedList<>();
    parameters = new LinkedHashMap<>();
  }

  public int argSize() {
    return args.size();
  }

  public int optionSize() {
    return options.size();
  }

  public int parameterSize() {
    return parameters.size();
  }

  public LinkedList<String> cloneOptions() {
    return new LinkedList<>(options);
  }

  public LinkedHashMap<String, String> cloneParameters() {
    return new LinkedHashMap<>(parameters);
  }

  public boolean hasOption(String name) {
    return options.contains(name);
  }

  public boolean hasParameter(String name) {
    return parameters.containsKey(name);
  }

  public String getParameter(String name) {
    return parameters.get(name);
  }

  public String getParameter(String name, String defaultValue) {
    if (hasParameter(name)) {
      return getParameter(name);
    } else {
      return defaultValue;
    }
  }

  public String getParameter(String name, Supplier<String> defaultValue) {
    if (hasParameter(name)) {
      return getParameter(name);
    } else {
      return defaultValue.get();
    }
  }

}
package top.btswork.furryblack.core.config;

import java.util.Map;
import java.util.TreeMap;

public class Configuration {

  public static Configuration getInstance() {
    return new Configuration();
  }

  private final Node node;

  private Configuration() {
    this.node = new Node();
  }

  public Configuration append(String[] keys) {
    node.set(keys, null);
    return this;
  }

  public Configuration append(String[] keys, String value) {
    node.set(keys, value);
    return this;
  }

  //= ==========================================================================

  public boolean has(String[] keys) {
    return node.has(keys);
  }

  public String get(String[] keys) {
    return node.get(keys);
  }

  //= ==========================================================================

  private static class Node {

    private final Map<String, Node> nodes;

    private String value;

    private Node() {
      nodes = new TreeMap<>();
    }

    public void set(String[] keys, String value) {
      Node current = this;
      for (String key : keys) {
        current = current.nodes.computeIfAbsent(key, i -> new Node());
      }
      current.value = value;
    }

    public String get(String[] keys) {
      Node current = this;
      for (String key : keys) {
        Node next = current.nodes.get(key);
        if (next == null) return null;
        current = next;
      }
      return current.value;
    }

    public boolean has(String[] keys) {
      Node current = this;
      for (String key : keys) {
        Node next = current.nodes.get(key);
        if (next == null) return false;
        current = next;
      }
      return true;
    }

  }

}

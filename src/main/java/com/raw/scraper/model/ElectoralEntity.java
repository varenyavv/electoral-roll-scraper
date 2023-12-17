package com.raw.scraper.model;

public class ElectoralEntity {
  private int key;
  private String name;

  public ElectoralEntity(int key, String name) {
    this.key = key;
    this.name = name;
  }

  public int getKey() {
    return key;
  }

  public void setKey(int key) {
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name + "(" + key + ")";
  }
}

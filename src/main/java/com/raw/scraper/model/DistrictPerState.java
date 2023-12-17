package com.raw.scraper.model;

import java.util.List;

public class DistrictPerState {
  private ElectoralEntity state;
  private List<ElectoralEntity> districts;

  public DistrictPerState(ElectoralEntity state, List<ElectoralEntity> districts) {
    this.state = state;
    this.districts = districts;
  }

  public ElectoralEntity getState() {
    return state;
  }

  public void setState(ElectoralEntity state) {
    this.state = state;
  }

  public List<ElectoralEntity> getDistricts() {
    return districts;
  }

  public void setDistricts(List<ElectoralEntity> districts) {
    this.districts = districts;
  }

  @Override
  public String toString() {
    return state + " " + districts;
  }
}

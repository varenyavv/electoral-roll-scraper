package com.raw.scraper.model;

import java.util.List;

public class RegCenterPerWard {
  private ElectoralEntity ward;
  private List<ElectoralEntity> regCenters;

  public RegCenterPerWard(ElectoralEntity ward, List<ElectoralEntity> regCenters) {
    this.ward = ward;
    this.regCenters = regCenters;
  }

  public ElectoralEntity getWard() {
    return ward;
  }

  public void setWard(ElectoralEntity ward) {
    this.ward = ward;
  }

  public List<ElectoralEntity> getRegCenters() {
    return regCenters;
  }

  public void setRegCenters(List<ElectoralEntity> regCenters) {
    this.regCenters = regCenters;
  }
}

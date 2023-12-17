package com.raw.scraper.model;

import java.util.List;

public class WardPerVdc {
  private ElectoralEntity vdc;
  private List<ElectoralEntity> wards;

  public WardPerVdc(ElectoralEntity vdc, List<ElectoralEntity> wards) {
    this.vdc = vdc;
    this.wards = wards;
  }

  public ElectoralEntity getVdc() {
    return vdc;
  }

  public void setVdc(ElectoralEntity vdc) {
    this.vdc = vdc;
  }

  public List<ElectoralEntity> getWards() {
    return wards;
  }

  public void setWards(List<ElectoralEntity> wards) {
    this.wards = wards;
  }
}

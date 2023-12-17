package com.raw.scraper.model;

import java.util.List;

public class VdcPerDistrict {
  private ElectoralEntity district;
  private List<ElectoralEntity> vdcList;

  public VdcPerDistrict(ElectoralEntity district, List<ElectoralEntity> vdcList) {
    this.district = district;
    this.vdcList = vdcList;
  }

  public ElectoralEntity getDistrict() {
    return district;
  }

  public void setDistrict(ElectoralEntity district) {
    this.district = district;
  }

  public List<ElectoralEntity> getVdcList() {
    return vdcList;
  }

  public void setVdcList(List<ElectoralEntity> vdcList) {
    this.vdcList = vdcList;
  }
}

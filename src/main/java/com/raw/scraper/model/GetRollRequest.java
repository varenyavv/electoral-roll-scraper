package com.raw.scraper.model;

import com.raw.scraper.constant.NepalState;

public class GetRollRequest {
  NepalState state;
  ElectoralEntity district;
  ElectoralEntity vdc;
  ElectoralEntity ward;
  ElectoralEntity regCenter;

  public GetRollRequest(
      NepalState state,
      ElectoralEntity district,
      ElectoralEntity vdc,
      ElectoralEntity ward,
      ElectoralEntity regCenter) {
    this.state = state;
    this.district = district;
    this.vdc = vdc;
    this.ward = ward;
    this.regCenter = regCenter;
  }

  public NepalState getState() {
    return state;
  }

  public ElectoralEntity getDistrict() {
    return district;
  }

  public ElectoralEntity getVdc() {
    return vdc;
  }

  public ElectoralEntity getWard() {
    return ward;
  }

  public ElectoralEntity getRegCenter() {
    return regCenter;
  }
}

package com.raw.scraper.model;

import com.raw.scraper.constant.NepalState;

public class VoterEntity {
  private String voterId;

  private String name;
  private String age;
  private String gender;
  private String spouse;
  private String parent;
  private NepalState state;
  private ElectoralEntity district;
  private ElectoralEntity vdc;
  private ElectoralEntity ward;
  private ElectoralEntity registrationCenter;

  public VoterEntity(
      NepalState state,
      ElectoralEntity district,
      ElectoralEntity vdc,
      ElectoralEntity ward,
      ElectoralEntity registrationCenter) {
    this.state = state;
    this.district = district;
    this.vdc = vdc;
    this.ward = ward;
    this.registrationCenter = registrationCenter;
  }

  public String getVoterId() {
    return voterId;
  }

  public VoterEntity setVoterId(String voterId) {
    this.voterId = voterId;
    return this;
  }

  public String getName() {
    return name;
  }

  public VoterEntity setName(String name) {
    this.name = name;
    return this;
  }

  public String getAge() {
    return age;
  }

  public VoterEntity setAge(String age) {
    this.age = age;
    return this;
  }

  public String getGender() {
    return gender;
  }

  public VoterEntity setGender(String gender) {
    this.gender = gender;
    return this;
  }

  public String getSpouse() {
    return spouse;
  }

  public VoterEntity setSpouse(String spouse) {
    this.spouse = spouse;
    return this;
  }

  public String getParent() {
    return parent;
  }

  public VoterEntity setParent(String parent) {
    this.parent = parent;
    return this;
  }

  public NepalState getState() {
    return state;
  }

  public VoterEntity setState(NepalState state) {
    this.state = state;
    return this;
  }

  public ElectoralEntity getDistrict() {
    return district;
  }

  public VoterEntity setDistrict(ElectoralEntity district) {
    this.district = district;
    return this;
  }

  public ElectoralEntity getVdc() {
    return vdc;
  }

  public VoterEntity setVdc(ElectoralEntity vdc) {
    this.vdc = vdc;
    return this;
  }

  public ElectoralEntity getWard() {
    return ward;
  }

  public VoterEntity setWard(ElectoralEntity ward) {
    this.ward = ward;
    return this;
  }

  public ElectoralEntity getRegistrationCenter() {
    return registrationCenter;
  }

  public VoterEntity setRegistrationCenter(ElectoralEntity registrationCenter) {
    this.registrationCenter = registrationCenter;
    return this;
  }
}

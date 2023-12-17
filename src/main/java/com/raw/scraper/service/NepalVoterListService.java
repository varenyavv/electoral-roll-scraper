package com.raw.scraper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raw.scraper.constant.NepalState;
import com.raw.scraper.model.DistrictPerState;
import com.raw.scraper.model.ElectoralEntity;
import com.raw.scraper.model.GetRollRequest;
import com.raw.scraper.model.RegCenterPerWard;
import com.raw.scraper.model.VdcPerDistrict;
import com.raw.scraper.model.VoterEntity;
import com.raw.scraper.model.VoterListResponseDto;
import com.raw.scraper.model.WardPerVdc;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NepalVoterListService {

  private static final Logger LOG = LoggerFactory.getLogger(NepalVoterListService.class);

  @Autowired NepalVoterListClient nepalVoterListClient;

  public DistrictPerState getDistrictsByState(NepalState state) throws JsonProcessingException {
    String districtApiResponse = nepalVoterListClient.getDistrictsByState(state);

    Document document = getHtmlDocumentFromApiResponse(districtApiResponse);

    List<ElectoralEntity> districts = getElectoralEntitiesFromApiResponse(document);
    return new DistrictPerState(new ElectoralEntity(state.getKey(), state.name()), districts);
  }

  public VdcPerDistrict getVdcListByDistrict(ElectoralEntity district)
      throws JsonProcessingException {
    String vdcListApiResponse = nepalVoterListClient.getVdcListByDistrict(district.getKey());
    Document document = getHtmlDocumentFromApiResponse(vdcListApiResponse);
    List<ElectoralEntity> vdcList = getElectoralEntitiesFromApiResponse(document);
    return new VdcPerDistrict(district, vdcList);
  }

  public WardPerVdc getWardsByVdc(ElectoralEntity vdc) throws JsonProcessingException {
    String wardsApiResponse = nepalVoterListClient.getWardsByVdc(vdc.getKey());
    Document document = getHtmlDocumentFromApiResponse(wardsApiResponse);
    List<ElectoralEntity> wardList = getElectoralEntitiesFromApiResponse(document);
    return new WardPerVdc(vdc, wardList);
  }

  public RegCenterPerWard getRegCentersByVdcAndWard(ElectoralEntity vdc, ElectoralEntity ward)
      throws JsonProcessingException {
    String regCentersApiResponse =
        nepalVoterListClient.getRegistrationCentersByWardAndVdc(vdc.getKey(), ward.getKey());
    Document document = getHtmlDocumentFromApiResponse(regCentersApiResponse);
    List<ElectoralEntity> regCenters = getElectoralEntitiesFromApiResponse(document);
    return new RegCenterPerWard(ward, regCenters);
  }

  public List<GetRollRequest> getRollRequestList(NepalState state, ElectoralEntity districtEntity)
      throws JsonProcessingException {
    List<GetRollRequest> getRollRequestList = new ArrayList<>();
    VdcPerDistrict vdcsPerDistrict = getVdcListByDistrict(districtEntity);
    LOG.info("District: {}, Total VDCs: {}", districtEntity, vdcsPerDistrict.getVdcList().size());

    for (ElectoralEntity vdcEntity : vdcsPerDistrict.getVdcList()) {
      WardPerVdc wardsPerVdc = getWardsByVdc(vdcEntity);
      LOG.info("VDC {}; Total Wards: {}", vdcEntity, vdcsPerDistrict.getVdcList().size());
      for (ElectoralEntity wardEntity : wardsPerVdc.getWards()) {
        RegCenterPerWard regCenterPerWard = getRegCentersByVdcAndWard(vdcEntity, wardEntity);
        LOG.info(
            "Ward {}; Total Reg Centers: {}", wardEntity, regCenterPerWard.getRegCenters().size());
        regCenterPerWard
            .getRegCenters()
            .forEach(
                regCenterEntity ->
                    getRollRequestList.add(
                        new GetRollRequest(
                            state, districtEntity, vdcEntity, wardEntity, regCenterEntity)));
      }
    }
    return getRollRequestList;
  }

  private static List<ElectoralEntity> getElectoralEntitiesFromApiResponse(Document document) {
    return document.select("option").stream()
        .filter(element -> !element.attr("value").isEmpty())
        .map(
            element -> new ElectoralEntity(Integer.parseInt(element.attr("value")), element.text()))
        .sorted(Comparator.comparingInt(ElectoralEntity::getKey))
        .toList();
  }

  private static Document getHtmlDocumentFromApiResponse(String apiResponseString)
      throws JsonProcessingException {
    ObjectMapper objectMapper =
        new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    VoterListResponseDto apiResponseObject =
        objectMapper.readValue(apiResponseString, VoterListResponseDto.class);

    return Jsoup.parse(apiResponseObject.getResult());
  }

  public List<VoterEntity> getVotersPerRequest(GetRollRequest getRollRequest) {
    String viewWardApiResponse = nepalVoterListClient.getVoterRoll(getRollRequest);
    Document document = Jsoup.parse(viewWardApiResponse);
    Element table = document.select("table#tbl_data").first();
    List<VoterEntity> voterEntities = new ArrayList<>();
    if (table != null) {
      // Select all rows in the table except the header row
      Elements rows = table.select("tbody tr");

      for (Element row : rows) {
        // Extract data from each column
        Elements columns = row.select("td");
        VoterEntity voterEntity =
            new VoterEntity(
                getRollRequest.getState(),
                getRollRequest.getDistrict(),
                getRollRequest.getVdc(),
                getRollRequest.getWard(),
                getRollRequest.getRegCenter());

        voterEntity
            .setVoterId(columns.get(1).text())
            .setName(columns.get(2).text())
            .setAge(columns.get(3).text())
            .setGender(columns.get(4).text())
            .setSpouse(columns.get(5).text())
            .setParent(columns.get(6).text());

        voterEntities.add(voterEntity);
      }
    }

    return voterEntities;
  }
}

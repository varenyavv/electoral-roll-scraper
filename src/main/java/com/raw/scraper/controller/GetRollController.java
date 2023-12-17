package com.raw.scraper.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.CSVWriter;
import com.raw.scraper.constant.NepalState;
import com.raw.scraper.exception.AppException;
import com.raw.scraper.model.DistrictPerState;
import com.raw.scraper.model.ElectoralEntity;
import com.raw.scraper.model.GetRollRequest;
import com.raw.scraper.model.ListWithCount;
import com.raw.scraper.model.RegCenterPerWard;
import com.raw.scraper.model.VdcPerDistrict;
import com.raw.scraper.model.VoterEntity;
import com.raw.scraper.model.WardPerVdc;
import com.raw.scraper.service.NepalVoterListService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@OpenAPIDefinition(
    info = @Info(title = "Nepal Electoral Roll", description = "", version = "1.0.0"))
public class GetRollController {

  private static final Logger LOG = LoggerFactory.getLogger(GetRollController.class);

  @Autowired NepalVoterListService nepalVoterListService;

  @GetMapping("districts")
  @Tag(name = "Get Districts By State")
  public ResponseEntity<DistrictPerState> getDistrictsByState(@RequestParam NepalState state)
      throws JsonProcessingException {
    DistrictPerState districtPerState = nepalVoterListService.getDistrictsByState(state);
    return ResponseEntity.status(HttpStatus.OK).body(districtPerState);
  }

  @GetMapping("roll")
  @Tag(name = "Get Electoral Rolls By State and District")
  public ResponseEntity<byte[]> getElectoralRoll(
      @RequestParam NepalState state, @RequestParam("district") Integer inputDistrict)
      throws JsonProcessingException, ExecutionException, InterruptedException {
    DistrictPerState districtList = nepalVoterListService.getDistrictsByState(state);

    if (districtList.getDistricts().stream()
        .noneMatch(district -> district.getKey() == inputDistrict)) {
      throw new AppException(
          String.format(
              "Not a valid district code, valid districts of %s are: %s",
              districtList.getState(), districtList.getDistricts()));
    }

    ElectoralEntity districtEntity =
        districtList.getDistricts().stream()
            .filter(district -> district.getKey() == inputDistrict)
            .findFirst()
            .get();

    List<GetRollRequest> getRollRequestList =
        nepalVoterListService.getRollRequestList(state, districtEntity);

    List<VoterEntity> mergedVoterEntities = processRollRequests(getRollRequestList).get();

    byte[] csvBytes = convertToCSV(mergedVoterEntities);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType("text/csv"));
    headers.setContentDispositionFormData("attachment", "voter-rolls.csv");

    return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
  }

  private byte[] convertToCSV(List<VoterEntity> voterEntities) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {

      // Add Byte Order Mark (BOM)
      osw.write('\ufeff');

      // Create CSV writer with custom settings
      CSVWriter csvWriter =
          new CSVWriter(
              osw,
              CSVWriter.DEFAULT_SEPARATOR,
              CSVWriter.DEFAULT_QUOTE_CHARACTER,
              CSVWriter.DEFAULT_ESCAPE_CHARACTER,
              CSVWriter.DEFAULT_LINE_END);

      // Write header
      csvWriter.writeNext(
          new String[] {
            "Voter ID",
            "Name",
            "Age",
            "Gender",
            "Spouse",
            "Parent",
            "State",
            "District",
            "VDC",
            "Ward",
            "Registration Center"
          });

      // Write data rows
      for (VoterEntity voter : voterEntities) {
        csvWriter.writeNext(
            new String[] {
              voter.getVoterId(),
              voter.getName(),
              String.valueOf(voter.getAge()),
              voter.getGender(),
              voter.getSpouse(),
              voter.getParent(),
              voter.getState().name(),
              voter.getDistrict().getName(),
              voter.getVdc().getName(),
              voter.getWard().getName(),
              voter.getRegistrationCenter().getName()
            });
      }

      csvWriter.flush();
      return baos.toByteArray();

    } catch (Exception e) {
      throw new AppException("Error converting to CSV", e);
    }
  }

  @GetMapping("single-roll")
  @Tag(name = "Get Electoral Rolls By State, District, VDC, Ward and Reg Center")
  public ResponseEntity<byte[]> getSingleElectoralRoll(
      @RequestParam NepalState state,
      @RequestParam("district") Integer inputDistrict,
      @RequestParam Integer vdc,
      @RequestParam Integer ward,
      @RequestParam Integer regCenter)
      throws JsonProcessingException {
    DistrictPerState districtList = nepalVoterListService.getDistrictsByState(state);

    if (districtList.getDistricts().stream()
        .noneMatch(district -> district.getKey() == inputDistrict)) {
      throw new AppException(
          String.format(
              "Not a valid district code, valid districts of %s are: %s",
              districtList.getState(), districtList.getDistricts()));
    }

    ElectoralEntity districtEntity =
        districtList.getDistricts().stream()
            .filter(district -> district.getKey() == inputDistrict)
            .findFirst()
            .get();

    VdcPerDistrict vdcPerDistrict = nepalVoterListService.getVdcListByDistrict(districtEntity);
    if (vdcPerDistrict.getVdcList().stream()
        .noneMatch(electoralEntity -> electoralEntity.getKey() == vdc)) {
      throw new AppException(
          String.format(
              "Not a valid VDC code, valid VDCs of %s are: %s",
              districtEntity, vdcPerDistrict.getVdcList()));
    }
    ElectoralEntity vdcEntity =
        vdcPerDistrict.getVdcList().stream()
            .filter(electoralEntity -> electoralEntity.getKey() == vdc)
            .findFirst()
            .get();

    WardPerVdc wardPerVdc = nepalVoterListService.getWardsByVdc(vdcEntity);
    if (wardPerVdc.getWards().stream()
        .noneMatch(electoralEntity -> electoralEntity.getKey() == ward)) {
      throw new AppException(
          String.format(
              "Not a valid Ward code, valid Wards of %s are: %s",
              vdcEntity, wardPerVdc.getWards()));
    }
    ElectoralEntity wardEntity =
        wardPerVdc.getWards().stream()
            .filter(electoralEntity -> electoralEntity.getKey() == ward)
            .findFirst()
            .get();

    RegCenterPerWard regCenterPerWard =
        nepalVoterListService.getRegCentersByVdcAndWard(vdcEntity, wardEntity);
    if (regCenterPerWard.getRegCenters().stream()
        .noneMatch(electoralEntity -> electoralEntity.getKey() == regCenter)) {
      throw new AppException(
          String.format(
              "Not a valid Registration Center code, valid Registration Centers of %s-%s are: %s",
              vdcEntity, wardEntity, regCenterPerWard.getRegCenters()));
    }
    ElectoralEntity regCenterEntity =
        regCenterPerWard.getRegCenters().stream()
            .filter(electoralEntity -> electoralEntity.getKey() == regCenter)
            .findFirst()
            .get();

    GetRollRequest getRollRequest =
        new GetRollRequest(state, districtEntity, vdcEntity, wardEntity, regCenterEntity);

    List<VoterEntity> voterEntities = nepalVoterListService.getVotersPerRequest(getRollRequest);
    byte[] csvBytes = convertToCSV(voterEntities);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType("text/csv"));
    headers.setContentDispositionFormData("attachment", "voter-rolls.csv");

    return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
  }

  private CompletableFuture<List<VoterEntity>> processRollRequests(
      List<GetRollRequest> rollRequests) {
    List<CompletableFuture<List<VoterEntity>>> futures =
        rollRequests.parallelStream()
            .map(
                request ->
                    CompletableFuture.supplyAsync(
                        () -> nepalVoterListService.getVotersPerRequest(request)))
            .toList();

    CompletableFuture<Void> allOf =
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

    return allOf.thenApply(
        ignored ->
            futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList()));
  }
}

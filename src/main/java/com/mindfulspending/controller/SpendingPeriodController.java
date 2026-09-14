package com.mindfulspending.controller;

import com.mindfulspending.dto.spendingPeriod.SpendingPeriodCreateRequest;
import com.mindfulspending.dto.spendingPeriod.SpendingPeriodResponse;
import com.mindfulspending.dto.spendingPeriod.SpendingPeriodUpdateRequest;
import com.mindfulspending.service.SpendingPeriodService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spending-periods")
public class SpendingPeriodController {
    private final SpendingPeriodService spendingPeriodService;

    public SpendingPeriodController(SpendingPeriodService spendingPeriodService) {
        this.spendingPeriodService = spendingPeriodService;
    }

    @PostMapping
    public ResponseEntity<SpendingPeriodResponse> createSpendingPeriod(@Valid @RequestBody SpendingPeriodCreateRequest request){
        SpendingPeriodResponse response = spendingPeriodService.createSpendingPeriod(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{spendingPeriodId}")
    public ResponseEntity<SpendingPeriodResponse> updateSpendingPeriod(@PathVariable Long spendingPeriodId, @Valid @RequestBody SpendingPeriodUpdateRequest request){
        SpendingPeriodResponse response = spendingPeriodService.updateSpendingPeriod(spendingPeriodId,request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SpendingPeriodResponse>> getSpendingPeriods(){
        return ResponseEntity.ok(spendingPeriodService.getSpendingPeriods());
    }

    @GetMapping("/{spendingPeriodId}")
    public ResponseEntity<SpendingPeriodResponse> getSpendingPeriodById(@PathVariable Long spendingPeriodId){
        return ResponseEntity.ok(spendingPeriodService.getSpendingPeriodById(spendingPeriodId));
    }


}

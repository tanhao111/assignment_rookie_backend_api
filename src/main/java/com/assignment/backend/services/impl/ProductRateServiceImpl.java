package com.assignment.backend.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.assignment.backend.data.entities.Product;
import com.assignment.backend.data.entities.ProductRate;
import com.assignment.backend.data.repositories.ProductRateRepository;
import com.assignment.backend.data.repositories.ProductRepository;
import com.assignment.backend.dto.request.ProductRateCreateDto;
import com.assignment.backend.dto.response.Error;
import com.assignment.backend.dto.response.ProductRateResponseDto;
import com.assignment.backend.dto.response.SuccessResponse;
import com.assignment.backend.exceptions.ResourceNotFoundException;
import com.assignment.backend.services.ProductRateService;
import com.assignment.backend.utils.Utils;

@Service
public class ProductRateServiceImpl implements ProductRateService {

    @Autowired
    private ProductRateRepository productRateRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<?> createNewRate(ProductRateCreateDto dto) {

        // TODO change this
        // check account nay da review product nay chua
        Product pro = productRepository.findById(dto.getProId()).orElseThrow(() -> new ResourceNotFoundException());
        Optional<ProductRate> pRate = productRateRepository.findByAccIdAndProduct(dto.getAccId(), pro);
        if (pRate.isPresent()) {
            // throw new ()
        }
        this.productRateRepository.save(modelMapper.map(dto, ProductRate.class));
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.CREATED, "Create successfully"));
    }

    @Override
    public List<ProductRateResponseDto> getRatesByProductId(int proId, boolean status) {
        List<ProductRate> lProductRates = new ArrayList<>();
        if (status) {
            lProductRates = this.productRateRepository.findByStatus(true);
        } else {
            lProductRates = this.productRateRepository.findAll();
        }

        if (lProductRates.isEmpty()) {
            throw new ResourceNotFoundException(Utils.NO_PRODUCT);
        }

        List<ProductRateResponseDto> result = new ArrayList<>();
        for (ProductRate pro : lProductRates) {
            result.add(modelMapper.map(pro, ProductRateResponseDto.class));
        }
        return result;
    }

    @Override
    public ResponseEntity<?> changeStatus(int rateId) {
        ProductRate productRateOptional = this.productRateRepository.findById(rateId)
                .orElseThrow(() -> new ResourceNotFoundException(Utils.PRODUCT_NOT_FOUND));

        boolean oldStatus = productRateOptional.isStatus();
        productRateOptional.setStatus(!oldStatus);
        this.productRateRepository.save(productRateOptional);

        return ResponseEntity.ok(
                new SuccessResponse(HttpStatus.OK,
                        "Change status have Id: " + rateId + " to " + !oldStatus + " successfully"));
    }

}
package org.springboot.pdv.controller;

import org.springboot.pdv.dto.ResponseDTO;
import org.springboot.pdv.dto.SaleDTO;
import org.springboot.pdv.dto.SaleInfoDTO;
import org.springboot.pdv.repository.SaleRepository;
import org.springboot.pdv.service.SaleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/sale")
public class SaleController {

    private final SaleService saleService;
    private SaleRepository saleRepository;

    public SaleController(SaleRepository saleRepository, SaleService saleService) {
        this.saleRepository = saleRepository;
        this.saleService = saleService;
    }

    @GetMapping
    public ResponseEntity<List<SaleInfoDTO>> getAllSales() {
        return new ResponseEntity<>(saleService.findAllByMap(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<SaleInfoDTO> getSaleById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(saleService.getById(id), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(new ResponseDTO(e.getMessage()),HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping
    public ResponseEntity<String> postSale(@RequestBody SaleDTO saleDTO) {
        try {
            return new ResponseEntity(saleService.save(saleDTO) ,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(new ResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}

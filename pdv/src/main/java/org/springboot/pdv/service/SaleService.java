package org.springboot.pdv.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springboot.pdv.dto.ProductDTO;
import org.springboot.pdv.dto.ProductInfoDTO;
import org.springboot.pdv.dto.SaleDTO;
import org.springboot.pdv.dto.SaleInfoDTO;
import org.springboot.pdv.entity.ItemSale;
import org.springboot.pdv.entity.Product;
import org.springboot.pdv.entity.Sale;
import org.springboot.pdv.entity.User;
import org.springboot.pdv.repository.ItemSaleRepository;
import org.springboot.pdv.repository.ProductRepository;
import org.springboot.pdv.repository.SaleRepository;
import org.springboot.pdv.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SaleService {


    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final ItemSaleRepository itemSaleRepository;

    /*
    {
        "user": "nome do usuario",
        "data": data da venda "08/10/2024"
        "products": [
                {
                    "description": "notebook dell"
                    "quantity": 1
                }
        ]
    }
     */

    public List<SaleInfoDTO> findAllByMap() {

        return saleRepository.findAll().stream().map(sale -> {
            SaleInfoDTO saleInfoDTO = new SaleInfoDTO();
            saleInfoDTO.setSaleId(sale.getId());
            saleInfoDTO.setUser(sale.getUser().getName());
            saleInfoDTO.setDate(sale.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            List<ProductInfoDTO> products = sale.getItemSales().stream().map(itemSale -> {
                ProductInfoDTO productInfoDTO = new ProductInfoDTO();
                productInfoDTO.setDescription(itemSale.getProduct().getDescription());
                productInfoDTO.setQuantity(itemSale.getQuantity());
                return productInfoDTO;
            }).toList();

            saleInfoDTO.setProducts(products);
            return saleInfoDTO;

        }).toList();
    }


    public List<SaleInfoDTO> findAll() {
        return saleRepository.findAll().stream().map(sale -> getSaleInfo(sale)).collect(Collectors.toList());
    }

    private SaleInfoDTO getSaleInfo(Sale sale) {
        SaleInfoDTO saleInfoDTO = new SaleInfoDTO();
        saleInfoDTO.setSaleId(sale.getId());
        saleInfoDTO.setUser(sale.getUser().getName());
        saleInfoDTO.setDate(sale.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        saleInfoDTO.setProducts(getProductInfo(sale.getItemSales()));

        return saleInfoDTO;
    }

    private List<ProductInfoDTO> getProductInfo(List<ItemSale> itemSales) {
        return itemSales.stream().map(itemSale -> {
            ProductInfoDTO productInfoDTO = new ProductInfoDTO();
            productInfoDTO.setDescription(itemSale.getProduct().getDescription());
            productInfoDTO.setQuantity(itemSale.getQuantity());
            return productInfoDTO;

        }).collect(Collectors.toList());

    }

    @Transactional
    public long save(SaleDTO saleDTO) {

        User user = userRepository.
                findById(saleDTO.getUserid()).
                orElseThrow(() -> new RuntimeException("user not found"));


        Sale newSale = new Sale();
        newSale.setDate(LocalDate.now());
        newSale.setUser(user);
        List<ItemSale> items = getItemSales(saleDTO.getItems());

        newSale = saleRepository.save(newSale);

        saveItemSale(items, newSale);

        return newSale.getId();
    }

    // ou posso fazer assim

    @Transactional
    public long saveRefactor(SaleDTO saleDTO) {
        User user = userRepository
                .findById(saleDTO.getUserid())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Sale sale = new Sale();
        sale.setDate(LocalDate.now());
        sale.setUser(user);

        List<ItemSale> items = getItemSales(saleDTO.getItems());

        sale = saleRepository.save(sale);

        for (ItemSale itemSale : items) {
            itemSale.setSale(sale);
        }

        itemSaleRepository.saveAll(items);

        return sale.getId();
    }

    private void saveItemSale(List<ItemSale> items, Sale newSale) {

        for (ItemSale item : items) {
            item.setSale(newSale);
        }
        itemSaleRepository.saveAll(items);

    }

    private List<ItemSale> getItemSales(List<ProductDTO> products) {

        return products.stream().map(
                item -> {
                    Product product = productRepository.getReferenceById(item.getProductid());

                    ItemSale itemSale = new ItemSale();
                    itemSale.setProduct(product);
                    itemSale.setQuantity(item.getQuantity());

                    return itemSale;
                }).collect(Collectors.toList());


    }

    //ou posso fazer um for

    private List<ItemSale> getItemSalesFor(List<ProductDTO> products) {

        List<ItemSale> itemSales = new ArrayList<>();

        for (ProductDTO item : products) {

            Product product = productRepository.getReferenceById(item.getProductid());

            ItemSale itemSale = new ItemSale();
            itemSale.setProduct(product);
            itemSale.setQuantity(item.getQuantity());
            itemSales.add(itemSale);
        }
        return itemSales;
    }

    public SaleInfoDTO getById(Long id) {
        Sale sale = saleRepository.findById(id).get();
        return getSaleInfo(sale);

    }
}

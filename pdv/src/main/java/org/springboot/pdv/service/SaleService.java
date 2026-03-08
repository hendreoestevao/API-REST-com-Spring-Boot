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
import org.springboot.pdv.exceptions.NoItemException;
import org.springboot.pdv.repository.ItemSaleRepository;
import org.springboot.pdv.repository.ProductRepository;
import org.springboot.pdv.repository.SaleRepository;
import org.springboot.pdv.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

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

            List<ProductInfoDTO> products = sale.getItemSales().stream()
                    .map(itemSale -> ProductInfoDTO.builder()
                            .id(itemSale.getProduct().getId())
                            .description(itemSale.getProduct().getDescription())
                            .quantity(itemSale.getQuantity())
                            .build())
                    .toList();

            return SaleInfoDTO.builder()
                    .saleId(sale.getId())
                    .user(sale.getUser().getName())
                    .date(sale.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .products(products)
                    .build();
        }).toList();
    }

    //ou posso fazer assim

    public List<SaleInfoDTO> findAllByMapRefactor() {

        return saleRepository.findAll()
                .stream()
                .map(sale -> SaleInfoDTO.builder()
                        .saleId(sale.getId())
                        .user(sale.getUser().getName())
                        .date(sale.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .products(
                                sale.getItemSales().stream()
                                        .map(itemSale -> ProductInfoDTO.builder()
                                                .id(itemSale.getProduct().getId())
                                                .description(itemSale.getProduct().getDescription())
                                                .quantity(itemSale.getQuantity())
                                                .build()
                                        ).toList()
                        ).build()).toList();
    }


    public List<SaleInfoDTO> findAll() {
        return saleRepository.findAll().stream().map(this::getSaleInfo).collect(toList());
    }

    private SaleInfoDTO getSaleInfo(Sale sale) {
        return SaleInfoDTO.builder()
                .saleId(sale.getId())
                .user(sale.getUser().getName())
                .date(sale.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .products(getProductInfo(sale.getItemSales())).
                build();
    }

    private List<ProductInfoDTO> getProductInfo(List<ItemSale> itemSales) {

        if (CollectionUtils.isEmpty(itemSales)) {
            return Collections.emptyList();
        }

        return itemSales.stream().map(itemSale ->
                ProductInfoDTO.builder()
                        .id(itemSale.getProduct().getId())
                        .description(itemSale.getProduct().getDescription())
                        .quantity(itemSale.getQuantity())
                        .build()

        ).collect(toList());

    }

    /*
    quantidade = 10
    qnt_vendida = 11
    erro
     */


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

        if (products.isEmpty()) {
            throw new NoItemException("No items found");
        }

        return products.stream().map(
                item -> {
                    Product product = productRepository.findById(item.getProductid())
                            .orElseThrow(() -> new RuntimeException("Product not found"));

                    ItemSale itemSale = new ItemSale();
                    itemSale.setProduct(product);
                    itemSale.setQuantity(item.getQuantity());

                    if (product.getQuantity() == 0 || product.getQuantity() < item.getQuantity()) {
                        throw new NoItemException(String.format("Produto sem estoque %s é a quantidade de produtos " +
                                "em estoque.", product.getQuantity()));
                    }

                    int total = product.getQuantity() - item.getQuantity();
                    product.setQuantity(total);
                    productRepository.save(product);

                    return itemSale;
                }).collect(toList());


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
        Sale sale = saleRepository.findById(id).orElseThrow(() -> new RuntimeException("Sale not found"));
        return getSaleInfo(sale);

    }
}

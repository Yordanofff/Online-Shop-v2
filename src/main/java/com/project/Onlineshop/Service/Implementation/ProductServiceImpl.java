package com.project.Onlineshop.Service.Implementation;

import com.project.Onlineshop.Dto.Request.ProductRequestDto;
import com.project.Onlineshop.Entity.Products.*;
import com.project.Onlineshop.Mapper.ProductMapper;
import com.project.Onlineshop.Repository.BrandRepository;
import com.project.Onlineshop.Repository.ColorRepository;
import com.project.Onlineshop.Repository.MaterialRepository;
import com.project.Onlineshop.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl {
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final MaterialRepository materialRepository;
    private final ColorRepository colorRepository;
    private final BrandRepository brandRepository;

    public Class<? extends Product> getProductClass(String category) {
        return switch (category) {
            case "FOOD" -> Food.class;
            case "DRINK" -> Drink.class;
            case "SANITARY" -> Sanitary.class;
            case "RAILING" -> Railing.class;
            case "ACCESSORIES" -> Accessories.class;
            case "DECORATION" -> Decoration.class;
            case "OTHERS" -> Others.class;
            default -> null; // Handle unknown category
        };
    }

    // TODO - method for custom errors msgs
    public void checkFields(ProductRequestDto productRequestDto, Model model){
        if(productRequestDto.getName().length()<0){
            model.addAttribute("name_too_short", "Please enter a valid name!");
        }
    }

    public String saveProduct(String productType,
                              ProductRequestDto productRequestDto,
                              BindingResult bindingResult,
                              Model model) {


        Product product = new Product();
        // TODO - binding results is catching the errors but they are not being displayed.
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", productRequestDto);
            model.addAttribute("product_type", productType);
            if (productType.equals("Sanitary") || productType.equals("Railing")) {
                model.addAttribute("materials", materialRepository.findAll());
            }
            if (productType.equals("Railing")) {
                model.addAttribute("colors", colorRepository.findAll());
                model.addAttribute("brands", brandRepository.findAll());
            }
            //TODO - maybe add custom messages to the attribute?
            // checkFields(productRequestDto, model);

            return "redirect:/products/add?productType="+productType;
        }

        if (productType.equalsIgnoreCase("food")) {
            product = productMapper.toFood(productRequestDto);
        } else if (productType.equalsIgnoreCase("drink")) {
            product = productMapper.toDrink(productRequestDto);
        } else if (productType.equalsIgnoreCase("accessory")) {
            product = productMapper.toAccessories(productRequestDto);
        } else if (productType.equalsIgnoreCase("sanitary")) {
            product = productMapper.toSanitary(productRequestDto);
        } else if (productType.equalsIgnoreCase("railing")) {
            product = productMapper.toRailing(productRequestDto);
        } else if (productType.equalsIgnoreCase("decoration")) {
            product = productMapper.toDecoration(productRequestDto);
        } else if (productType.equalsIgnoreCase("others")) {
            product = productMapper.toOthers(productRequestDto);
        }
        productRepository.save(product);
        return "redirect:/products/show";
    }

}

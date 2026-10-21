package com.gabriel.SpringEcom.mappers;

import com.gabriel.SpringEcom.dto.ProductDTO.ProductImageResponseDTO;
import com.gabriel.SpringEcom.dto.ProductDTO.ProductRequestDTO;
import com.gabriel.SpringEcom.dto.ProductDTO.ProductResponseDTO;
import com.gabriel.SpringEcom.model.Product;
import com.gabriel.SpringEcom.model.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(source = "user", target = "seller")
    ProductResponseDTO mapToProductResponseDTO(Product product);

//    @Mapping(source = "externalId", target = "externalId")
//    SellerSummaryDTO mapToSellerSummaryDTO(User user);

    @Mapping(target = "imageUrl", expression = "java(\"/products/images/\" + image.getId())")
    ProductImageResponseDTO mapToProductImageDTO(ProductImage image);

    //void, pois é um recurso definitivo para update que não retorna nada e apenas faz uma "cirugia" no Product que foi passado como parâmetro
    void updateProductFromRequest(ProductRequestDTO request, @MappingTarget Product product);

}

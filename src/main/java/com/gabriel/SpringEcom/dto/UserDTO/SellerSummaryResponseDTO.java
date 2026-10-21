package com.gabriel.SpringEcom.dto.UserDTO;

public record SellerSummaryResponseDTO(
//        UUID externalId, //Vou deixar comentador por agora, pois sei se é uma prática colocar o id nesse DTO
        String username,
        String email
) {}
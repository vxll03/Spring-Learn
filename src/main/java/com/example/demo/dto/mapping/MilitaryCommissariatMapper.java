package com.example.demo.dto.mapping;

import com.example.demo.dto.MilitaryCommissariatRequestDTO;
import com.example.demo.dto.MilitaryCommissariatResponseDTO;
import com.example.demo.model.MilitaryCommissariat;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MilitaryCommissariatMapper {
    MilitaryCommissariat toEntity(MilitaryCommissariatRequestDTO dto);
    MilitaryCommissariatResponseDTO toDTO(MilitaryCommissariat entity);
}

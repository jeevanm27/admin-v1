package com.syncride.admin.service;

import com.syncride.admin.dto.OrganizationDTO;
import com.syncride.admin.model.Organization;
import com.syncride.admin.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository repository;

    //  DTO Mapper Method
    private OrganizationDTO mapToDTO(Organization org) {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setOrgId(org.getOrgId());
        dto.setOrganizationName(org.getOrganizationName());
        dto.setOrganizationAddress(org.getOrganizationAddress());
        dto.setPhone(org.getPhone());
        dto.setEnabled(org.isEnabled());
        return dto;
    }

    // CREATE
    public OrganizationDTO create(OrganizationDTO dto) {
        Organization org = new Organization();
        org.setOrganizationName(dto.getOrganizationName());
        org.setOrganizationAddress(dto.getOrganizationAddress());
        org.setPhone(dto.getPhone());
        org.setEnabled(dto.isEnabled());
        return mapToDTO(repository.save(org));
    }

    // UPDATE
    public OrganizationDTO update(String orgId, OrganizationDTO dto) {

        Organization org = repository.findByOrgId(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        org.setOrganizationName(dto.getOrganizationName());
        org.setOrganizationName(dto.getOrganizationName());
        org.setOrganizationAddress(dto.getOrganizationAddress());
        org.setPhone(dto.getPhone());
        org.setEnabled(dto.isEnabled());

        return mapToDTO(repository.save(org));
    }

    // GET BY ID
    public OrganizationDTO getByOrgId(String orgId) {

        Organization org = repository.findByOrgId(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        return mapToDTO(org);
    }

    // GET ALL
    public List<OrganizationDTO> getAll() {

        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // DELETE
    public void delete(String orgId) {

        Organization org = repository.findByOrgId(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        repository.delete(org);
    }
}
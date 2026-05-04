package org.example.cafecrm.service;

import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.staff.StaffCreateRequest;
import org.example.cafecrm.domain.dto.staff.StaffDto;
import org.example.cafecrm.domain.dto.staff.StaffUpdateRequest;
import org.example.cafecrm.domain.entity.Staff;
import org.example.cafecrm.exception.NotFoundException;
import org.example.cafecrm.mapper.StaffMapper;
import org.example.cafecrm.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;

    @Transactional(readOnly = true)
    public Staff getEntityById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Staff not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public StaffDto findById(Long id) {
        return staffMapper.toDto(getEntityById(id));
    }

    @Transactional(readOnly = true)
    public List<StaffDto> findAll() {
        return staffRepository.findAll()
                .stream()
                .map(staffMapper::toDto)
                .toList();
    }

    @Transactional
    public StaffDto create(StaffCreateRequest request) {

        Staff staff = staffMapper.toEntity(request);

        return staffMapper.toDto(staffRepository.save(staff));
    }

    @Transactional
    public StaffDto update(Long id, StaffUpdateRequest request) {

        Staff staff = getEntityById(id);

        staffMapper.updateEntity(request, staff);

        return staffMapper.toDto(staffRepository.save(staff));
    }

    @Transactional
    public void delete(Long id) {

        Staff staff = getEntityById(id);

        staffRepository.delete(staff);
    }
}

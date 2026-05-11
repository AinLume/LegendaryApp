package org.example.cafecrm.service;

import org.example.cafecrm.domain.dto.staff.StaffCreateRequest;
import org.example.cafecrm.domain.dto.staff.StaffDto;
import org.example.cafecrm.domain.dto.staff.StaffUpdateRequest;
import org.example.cafecrm.domain.entity.Staff;
import org.example.cafecrm.domain.values.StaffRole;
import org.example.cafecrm.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class StaffServiceTest extends BaseServiceTest {

    private StaffService staffService;

    @BeforeEach
    void setUp() {
        staffService = new StaffService(staffRepository, staffMapper, passwordEncoder);
        setUpBaseEntities();
    }

    @Test
    void getEntityById_shouldReturnStaff() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(testStaff));

        Staff result = staffService.getEntityById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Staff");
        verify(staffRepository).findById(1L);
    }

    @Test
    void getEntityById_shouldThrowNotFoundException_whenStaffNotExists() {
        when(staffRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> staffService.getEntityById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Staff not found with id: 999");
    }

    @Test
    void getStaffByEmail_shouldReturnStaff() {
        when(staffRepository.findByEmail("test@example.com")).thenReturn(testStaff);

        Staff result = staffService.getStaffByEmail("test@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getPhone()).isEqualTo("+79001234568");
        verify(staffRepository).findByEmail("test@example.com");
    }

    @Test
    void getStaffByEmail_shouldReturnNull_whenStaffNotExists() {
        when(staffRepository.findByEmail("notfound@example.com")).thenReturn(null);

        Staff result = staffService.getStaffByEmail("notfound@example.com");

        assertThat(result).isNull();
        verify(staffRepository).findByEmail("notfound@example.com");
    }

    @Test
    void findById_shouldReturnStaffDto() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(testStaff));

        StaffDto dto = new StaffDto(1L, "Test Staff", "+79001234568", "test@example.com", StaffRole.WAITER);
        when(staffMapper.toDto(testStaff)).thenReturn(dto);

        StaffDto result = staffService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Test Staff");
        verify(staffRepository).findById(1L);
        verify(staffMapper).toDto(testStaff);
    }

    @Test
    void findAll_shouldReturnAllStaff() {
        Staff staff2 = createTestStaff(2L, "Staff 2", StaffRole.COOK);
        Staff staff3 = createTestStaff(3L, "Staff 3", StaffRole.BARTENDER);

        List<Staff> staffList = Arrays.asList(testStaff, staff2, staff3);
        when(staffRepository.findAll()).thenReturn(staffList);

        StaffDto dto1 = new StaffDto(1L, "Test Staff", "+79001234568", "test@example.com", StaffRole.WAITER);
        StaffDto dto2 = new StaffDto(2L, "Staff 2", "+79001234568", "test@example.com", StaffRole.COOK);
        StaffDto dto3 = new StaffDto(3L, "Staff 3", "+79001234568", "test@example.com", StaffRole.BARTENDER);

        when(staffMapper.toDto(testStaff)).thenReturn(dto1);
        when(staffMapper.toDto(staff2)).thenReturn(dto2);
        when(staffMapper.toDto(staff3)).thenReturn(dto3);

        List<StaffDto> result = staffService.findAll();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(2).id()).isEqualTo(3L);

        verify(staffRepository).findAll();
        verify(staffMapper, times(3)).toDto(any(Staff.class));
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoStaff() {
        when(staffRepository.findAll()).thenReturn(List.of());

        List<StaffDto> result = staffService.findAll();

        assertThat(result).isEmpty();
        verify(staffRepository).findAll();
    }

    @Test
    void create_shouldCreateStaff() {
        StaffCreateRequest request = new StaffCreateRequest(
                "New Staff",
                "+79001234570",
                "new@example.com",
                "password",
                StaffRole.COOK
        );

        Staff staffToSave = createTestStaff(null, "New Staff", StaffRole.COOK);
        when(staffMapper.toEntity(request)).thenReturn(staffToSave);
        when(passwordEncoder.encode("password")).thenReturn("$2a$10$encodedPassword");

        Staff savedStaff = createTestStaff(1L, "New Staff", StaffRole.COOK);
        when(staffRepository.save(any(Staff.class))).thenReturn(savedStaff);

        StaffDto expectedDto = new StaffDto(1L, "New Staff", "+79001234570", "new@example.com", StaffRole.COOK);
        when(staffMapper.toDto(savedStaff)).thenReturn(expectedDto);

        StaffDto result = staffService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("New Staff");

        verify(staffMapper).toEntity(request);
        verify(staffRepository).save(any(Staff.class));
    }

    @Test
    void update_shouldUpdateStaff() {
        StaffUpdateRequest request = new StaffUpdateRequest(
                "Updated Name",
                "+79001234571",
                "updated@example.com",
                null,
                StaffRole.BARTENDER
        );

        when(staffRepository.findById(1L)).thenReturn(Optional.of(testStaff));
        when(staffRepository.save(any(Staff.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Staff updatedStaff = createTestStaff(1L, "Updated Name", StaffRole.BARTENDER);
        StaffDto expectedDto = new StaffDto(1L, "Updated Name", "+79001234571", "updated@example.com", StaffRole.BARTENDER);
        when(staffMapper.toDto(any(Staff.class))).thenReturn(expectedDto);

        StaffDto result = staffService.update(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Updated Name");
        assertThat(result.role()).isEqualTo(StaffRole.BARTENDER);

        verify(staffRepository).findById(1L);
        verify(staffMapper).updateEntity(request, testStaff);
        verify(staffRepository).save(any(Staff.class));
    }

    @Test
    void update_shouldThrowNotFoundException_whenStaffNotExists() {
        StaffUpdateRequest request = new StaffUpdateRequest(
                "Updated Name",
                "+79001234571",
                "updated@example.com",
                null,
                StaffRole.BARTENDER
        );

        when(staffRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> staffService.update(999L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Staff not found with id: 999");

        verify(staffRepository).findById(999L);
        verify(staffMapper, never()).updateEntity(any(), any());
        verify(staffRepository, never()).save(any(Staff.class));
    }

    @Test
    void delete_shouldDeleteStaff() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(testStaff));
        doNothing().when(staffRepository).delete(testStaff);

        staffService.delete(1L);

        verify(staffRepository).findById(1L);
        verify(staffRepository).delete(testStaff);
    }

    @Test
    void delete_shouldThrowNotFoundException_whenStaffNotExists() {
        when(staffRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> staffService.delete(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Staff not found with id: 999");

        verify(staffRepository).findById(999L);
        verify(staffRepository, never()).delete(any(Staff.class));
    }
}

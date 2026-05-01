package org.example.cafecrm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cafecrm.domain.dto.table.CreateTableRequest;
import org.example.cafecrm.domain.dto.table.TableResponse;
import org.example.cafecrm.domain.dto.table.UpdateTablePositionRequest;
import org.example.cafecrm.domain.dto.table.UpdateTableStatusRequest;
import org.example.cafecrm.domain.entity.Tables;
import org.example.cafecrm.domain.values.TableStatus;
import org.example.cafecrm.exception.ConflictException;
import org.example.cafecrm.exception.NotFoundException;
import org.example.cafecrm.mapper.TableMapper;
import org.example.cafecrm.repository.TableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;
    private final TableMapper mapper;

    @Transactional(readOnly = true)
    public Tables getTableById(Integer id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Table with id %d does not exist", id)));
    }

    @Transactional
    public TableResponse create(CreateTableRequest dto) {

        if (tableRepository.existsByNumber(dto.number())) {
            throw new ConflictException(
                    String.format("Table with number %s already exists", dto.number())
            );
        }

        Tables table = mapper.toEntity(dto);
        table.setStatus(TableStatus.FREE);

        return mapper.toResponse(tableRepository.save(table));
    }

    @Transactional(readOnly = true)
    public List<TableResponse> findAll() {
        return tableRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public void deleteTableById(Integer id) {

        if (!tableRepository.existsById(id)) {
            throw new NotFoundException(String.format("Table with id %d does not exist", id));
        }

        tableRepository.deleteById(id);
    }

    @Transactional
    public TableResponse updateTableStatusById(Integer id, UpdateTableStatusRequest dto) {
        Tables table = getTableById(id);

        if (table.getStatus().equals(dto.status()))
            return mapper.toResponse(table);

        table.setStatus(dto.status());
        return mapper.toResponse(tableRepository.save(table));
    }

    @Transactional
    public TableResponse updateTablePosition(Integer id, UpdateTablePositionRequest dto) {
        Tables table = getTableById(id);

        if (table.getPosX().equals(dto.posX()) && table.getPosY().equals(dto.posY()))
            return mapper.toResponse(table);

        table.setPosX(dto.posX());
        table.setPosY(dto.posY());

        return mapper.toResponse(tableRepository.save(table));
    }
}
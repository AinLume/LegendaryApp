package org.example.cafecrm.service;

import org.example.cafecrm.domain.entity.*;
import org.example.cafecrm.domain.values.*;
import org.example.cafecrm.mapper.*;
import org.example.cafecrm.repository.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest {

    @Mock
    protected TableRepository tableRepository;

    @Mock
    protected ClientRepository clientRepository;

    @Mock
    protected StaffRepository staffRepository;

    @Mock
    protected MenuCategoryRepository menuCategoryRepository;

    @Mock
    protected MenuItemRepository menuItemRepository;

    @Mock
    protected OrderRepository orderRepository;

    @Mock
    protected OrderItemRepository orderItemRepository;

    @Mock
    protected ReservationRepository reservationRepository;

    @Mock
    protected TableMapper tableMapper;

    @Mock
    protected ClientMapper clientMapper;

    @Mock
    protected StaffMapper staffMapper;

    @Mock
    protected MenuItemMapper menuItemMapper;

    @Mock
    protected OrderMapper orderMapper;

    @Mock
    protected OrderItemMapper orderItemMapper;

    @Mock
    protected ReservationMapper reservationMapper;

    @Mock
    protected PasswordEncoder passwordEncoder;

    protected Tables testTable;
    protected Client testClient;
    protected Staff testStaff;
    protected MenuCategory testMenuCategory;
    protected MenuItem testMenuItem;
    protected Order testOrder;
    protected OrderItem testOrderItem;
    protected Reservation testReservation;

    protected void setUpBaseEntities() {
        testTable = createTestTable(1, 1, 4, 100, 200, TableStatus.FREE);
        testClient = createTestClient(1L, "Test Client", "+79001234567");
        testStaff = createTestStaff(1L, "Test Staff", StaffRole.WAITER);
        testMenuCategory = createTestMenuCategory(1, "Test Category");
        testMenuItem = createTestMenuItem(1L, "Test Item", 1000L, MenuItemType.FOOD, testMenuCategory);
        testOrder = createTestOrder(1L, OrderType.DINE_IN, OrderStatus.NEW, testTable, testClient, testStaff);
        testOrderItem = createTestOrderItem(1L, 2, testMenuItem, testOrder);
        testReservation = createTestReservation(1L, testClient, testTable);
    }

    protected void setUpPasswordEncoder() {
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
    }

    protected Tables createTestTable(Integer id, Integer number, Integer capacity, Integer posX, Integer posY, TableStatus status) {
        Tables table = new Tables();
        table.setId(id);
        table.setNumber(number);
        table.setCapacity(capacity);
        table.setPosX(posX);
        table.setPosY(posY);
        table.setStatus(status);
        table.setOrders(new ArrayList<>());
        table.setReservations(new ArrayList<>());
        return table;
    }

    protected Tables createTestTable(Integer id, Integer number, Integer capacity) {
        return createTestTable(id, number, capacity, 0, 0, TableStatus.FREE);
    }

    protected Client createTestClient(Long id, String name, String phone) {
        Client client = new Client();
        client.setId(id);
        client.setName(name);
        client.setPhone(phone);
        client.setEmail("test@example.com");
        client.setPassword("password");
        client.setDeliveryAddress("Test Address");
        client.setCreatedAt(LocalDateTime.now());
        client.setOrders(new ArrayList<>());
        return client;
    }

    protected Staff createTestStaff(Long id, String name, StaffRole role) {
        Staff staff = new Staff();
        staff.setId(id);
        staff.setName(name);
        staff.setPhone("+79001234568");
        staff.setRole(role);
        staff.setCreatedAt(LocalDateTime.now());
        staff.setOrders(new ArrayList<>());
        return staff;
    }

    protected MenuCategory createTestMenuCategory(Integer id, String name) {
        MenuCategory category = new MenuCategory();
        category.setId(id);
        category.setName(name);
        category.setItems(new ArrayList<>());
        return category;
    }

    protected MenuItem createTestMenuItem(Long id, String name, Long price, MenuItemType type, MenuCategory category) {
        MenuItem item = new MenuItem();
        item.setId(id);
        item.setName(name);
        item.setDescription("Test description");
        item.setPrice(price);
        item.setType(type);
        item.setIsAvailable(true);
        item.setPhotoUrl("http://example.com/photo.jpg");
        item.setCategory(category);
        item.setCreatedAt(LocalDateTime.now());
        return item;
    }

    protected Order createTestOrder(Long id, OrderType type, OrderStatus status, Tables table, Client client, Staff staff) {
        Order order = new Order();
        order.setId(id);
        order.setType(type);
        order.setStatus(status);
        order.setTable(table);
        order.setClient(client);
        order.setStaff(staff);
        order.setDeliveryAddress("Test Address");
        order.setPaymentMethod(PaymentMethod.CASH);
        order.setTotalAmount(2000L);
        order.setCreatedAt(LocalDateTime.now());
        order.setItems(new ArrayList<>());
        return order;
    }

    protected OrderItem createTestOrderItem(Long id, Integer quantity, MenuItem menuItem, Order order) {
        OrderItem item = new OrderItem();
        item.setId(id);
        item.setQuantity(quantity);
        item.setMenuItem(menuItem);
        item.setOrder(order);
        item.setStatus(OrderItemStatus.NEW);
        item.setDestination(Destination.KITCHEN);
        return item;
    }

    protected Reservation createTestReservation(Long id, Client client, Tables table) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setGuestName(client.getName());
        reservation.setGuestPhone(client.getPhone());
        reservation.setTable(table);
        reservation.setStartTime(LocalDateTime.now());
        reservation.setEndTime(LocalDateTime.now().plusHours(2));
        reservation.setPersons(4);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setType(ReservationType.TABLE);
        reservation.setCreatedAt(LocalDateTime.now());
        return reservation;
    }
}

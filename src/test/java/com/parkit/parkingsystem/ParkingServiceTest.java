package com.parkit.parkingsystem;

import com.mysql.cj.protocol.InternalTime;
import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }

    @Test
    public void processExitingVehicleTest() throws Exception {

        Ticket t = new Ticket();
        t.setInTime(new Date());
        t.setOutTime(null);
        t.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCD");
        when(ticketDAO.getTicket(any())).thenReturn(t);
        when(ticketDAO.updateTicket(any())).thenReturn(true);
        when(parkingSpotDAO.updateParking(any())).thenReturn(true);


        parkingService.processExitingVehicle();


        verify(ticketDAO, times(1)).updateTicket(any());
        verify(parkingSpotDAO, times(1)).updateParking(any());
    }


    @Test
    public void processExitingVehicleTestUpdateTicketFails() throws Exception {

        Ticket t = new Ticket();
        t.setInTime(new Date());
        t.setOutTime(null);
        t.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCD");
        when(ticketDAO.getTicket(any())).thenReturn(t);
        when(ticketDAO.updateTicket(any())).thenReturn(false);



        parkingService.processExitingVehicle();


        verify(ticketDAO, times(1)).updateTicket(any());
        verify(parkingSpotDAO, times(0)).updateParking(any());
    }

    @Test
    public void processExitingVehicleGetVehicleRegNumberFails() throws Exception {

        Ticket t = new Ticket();
        t.setInTime(new Date());
        t.setOutTime(null);
        t.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenThrow(new Exception());


        parkingService.processExitingVehicle();


        verify(ticketDAO, times(0)).updateTicket(any());
        verify(parkingSpotDAO, times(0)).updateParking(any());
    }

    @Test
    public void processIncomingVehicle() throws Exception {

        Ticket t = new Ticket();
        ParkingSpot parkingspot = new ParkingSpot(1, ParkingType.CAR, false);
        t.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");


        parkingService.processIncomingVehicle();



        verify(ticketDAO, times(1)).saveTicket(any());


    }

    @Test
    public void processIncomingVehiclefail() throws Exception {

        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenThrow(new Exception());


        parkingService.processIncomingVehicle();


        verify(ticketDAO, times(0)).saveTicket(any());
    }

    @Test
    public void getNextParkingNumberIfAvailableCar() {

        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);


        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        assertEquals(1, parkingSpot.getId());
        assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
        assertTrue(parkingSpot.isAvailable());
    }

    @Test
    public void getNextParkingNumberIfAvailableBike() {

        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(4);


        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        assertEquals(4, parkingSpot.getId());
        assertEquals(ParkingType.BIKE, parkingSpot.getParkingType());
        assertTrue(parkingSpot.isAvailable());
    }

    @Test
    public void getNextParkingNumberIfAvailableCarFailVehicleType() {
        when(inputReaderUtil.readSelection()).thenReturn(3);

        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        assertEquals(null,parkingSpot);
    }
}

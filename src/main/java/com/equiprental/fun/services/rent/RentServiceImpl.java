package com.equiprental.fun.services.rent;

import com.equiprental.fun.exceptions.NotFoundException;
import com.equiprental.fun.exceptions.RentException;
import com.equiprental.fun.models.entity.Customer;
import com.equiprental.fun.models.entity.Equipment;
import com.equiprental.fun.models.entity.Rent;
import com.equiprental.fun.repositories.CustomerRepository;
import com.equiprental.fun.repositories.EquipmentRepository;
import com.equiprental.fun.repositories.RentRepository;
import com.equiprental.fun.util.RentStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RentServiceImpl implements RentService {

    private final RentRepository rentRepository;
    private final EquipmentRepository equipmentRepository;
    private final CustomerRepository customerRepository;


    public RentServiceImpl(RentRepository rentRepository, EquipmentRepository equipmentRepository, CustomerRepository customerRepository) {
        this.rentRepository = rentRepository;
        this.equipmentRepository = equipmentRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public void rentEquipment(Long equipmentId, Long customerId, LocalDate startDate, LocalDate endDate) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException.EquipmentNotFoundException(equipmentId));
        if (equipment.getAvailableCount() == 0) {
            throw new NotFoundException.NotAvailableException("Equipment not available for rent.");
        }
        equipment.setAvailableCount(equipment.getAvailableCount() - 1);
        equipmentRepository.save(equipment);
        double calculatedPrice = calculateRentPrice(startDate, endDate, equipment.getPrice());
        Rent rental = new Rent();
        rental.setEquipment(equipment);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException.UserNotFoundException(customerId));
        rental.setCustomer(customer);
        rental.setStartDate(startDate);
        rental.setEndDate(endDate);
        rental.setRentalPrice(calculatedPrice);
        rental.setRentStatus(RentStatus.RENTED);
        rentRepository.save(rental);
    }

    @Override
    public double calculateRentPrice(LocalDate startDate, LocalDate endDate, double price) {
        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate);
        double totalPrice = numberOfDays * price;
        return totalPrice;
    }

    public double calculateTotalRentPriceForCustomer(Long customerId) {
        List<Rent> rents = rentRepository.findByCustomerId(customerId);
        double totalRentPrice = 0;
        for (Rent rent : rents) {
            double rentPrice = rent.getRentalPrice();
            totalRentPrice += rentPrice;
        }
        System.out.println("The total rental price for customer with ID " + customerId + " is: " + totalRentPrice);
        return totalRentPrice;
    }

    @Override
    public String deleteRent(Long rentId) {
        Rent rent = rentRepository.findById(rentId)
                .orElseThrow(() -> new NotFoundException.RentNotFoundException(rentId));
        if (rent.getRentStatus() == RentStatus.RETURNED) {
            throw new RentException.RentHasReturnException(rent);
        }
        rentRepository.delete(rent);
        Equipment equipment = rent.getEquipment();
        equipment.setAvailableCount(equipment.getAvailableCount() + 1);
        equipmentRepository.save(equipment);
        return "Rent with ID " + rentId + " has been successfully deleted.";
    }
}
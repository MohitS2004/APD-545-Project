package com.blueharbor.hotel.viewmodel;

import com.blueharbor.hotel.model.RoomType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class KioskWizardViewModel {

    private int adults = 1;
    private int children;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private final Map<RoomType, RoomSelection> roomSelections = new EnumMap<>(RoomType.class);
    private GuestDetails guestDetails = new GuestDetails();
    private final List<AddOnSelection> addOnSelections = new ArrayList<>();
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal taxes = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private boolean enrollInLoyalty;

    public int getAdults() {
        return adults;
    }

    public void setAdults(int adults) {
        this.adults = adults;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Map<RoomType, RoomSelection> getRoomSelections() {
        return roomSelections;
    }

    public GuestDetails getGuestDetails() {
        return guestDetails;
    }

    public void setGuestDetails(GuestDetails guestDetails) {
        this.guestDetails = guestDetails;
    }

    public List<AddOnSelection> getAddOnSelections() {
        return addOnSelections;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTaxes() {
        return taxes;
    }

    public void setTaxes(BigDecimal taxes) {
        this.taxes = taxes;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public boolean isEnrollInLoyalty() {
        return enrollInLoyalty;
    }

    public void setEnrollInLoyalty(boolean enrollInLoyalty) {
        this.enrollInLoyalty = enrollInLoyalty;
    }

    public int totalNights() {
        if (checkInDate == null || checkOutDate == null) {
            return 0;
        }
        return (int) (checkOutDate.toEpochDay() - checkInDate.toEpochDay());
    }

    public static class GuestDetails {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String notes;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    public static class AddOnSelection {
        private String code;
        private String name;
        private BigDecimal price;
        private boolean perNight;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public boolean isPerNight() {
            return perNight;
        }

        public void setPerNight(boolean perNight) {
            this.perNight = perNight;
        }
    }

    public static class RoomSelection {
        private RoomType roomType;
        private int quantity;
        private BigDecimal nightlyRate = BigDecimal.ZERO;

        public RoomType getRoomType() {
            return roomType;
        }

        public void setRoomType(RoomType roomType) {
            this.roomType = roomType;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getNightlyRate() {
            return nightlyRate;
        }

        public void setNightlyRate(BigDecimal nightlyRate) {
            this.nightlyRate = nightlyRate;
        }
    }
}

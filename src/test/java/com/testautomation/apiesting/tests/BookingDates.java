package com.testautomation.apiesting.tests;

public class BookingDates {

    private String checkin,checkout;

    public BookingDates(){}

    public BookingDates(String checkin, String checkout){
        setCheckin(checkin);
        setCheckout(checkout);
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }


}

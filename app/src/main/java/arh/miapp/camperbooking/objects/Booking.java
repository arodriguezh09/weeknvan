package arh.miapp.camperbooking.objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Booking {

    private String idUser;
    private String plate;
    private String checkin;
    private String checkout;

    @Override
    public String toString() {
        return "Booking{" +
                "idUser='" + idUser + '\'' +
                ", plate='" + plate + '\'' +
                ", checkin='" + checkin + '\'' +
                ", checkout='" + checkout + '\'' +
                ", grandTotal=" + grandTotal +
                '}';
    }

    private double grandTotal;

    public Booking() {
    }

    public Booking(String idUser, String plate, String checkin, String checkout, double grandTotal) {
        this.idUser = idUser;
        this.plate = plate;
        this.checkin = checkin;
        this.checkout = checkout;
        this.grandTotal = grandTotal;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
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

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public boolean isReserved(Date checkin, Date checkout, String plate) {
        boolean reserved = false;
        if (checkin == null && checkout == null) {
            return false;
        }
        Date bookingCI = null;
        Date bookingCO = null;
        try {
            bookingCI = new SimpleDateFormat("yyyy-MM-dd").parse(this.checkin);
            bookingCO = new SimpleDateFormat("yyyy-MM-dd").parse(this.checkout);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (this.plate.equals(plate)) {
            if (checkin.before(bookingCO) && (checkout.after(bookingCI) && (checkout.before(bookingCO) || checkout.after(bookingCO)))) {
                reserved = true;
            }
            if (checkin.equals(bookingCI) || checkout.equals(bookingCI) || checkin.equals(bookingCO) || checkout.equals(bookingCO)) {
                reserved = true;
            }
            if (checkin.after(bookingCI) && checkin.before(bookingCO) &&
                    checkout.before(bookingCO) && checkout.after(bookingCI)) {
                reserved = true;
            }
            if (checkin.after(bookingCI) && checkin.before(bookingCO) &&
                    checkout.after(bookingCI) && checkout.after(bookingCO)) {
                reserved = true;
            }
            if (checkin.before(bookingCI) && checkin.before(bookingCO) &&
                    checkout.before(bookingCO) && checkout.after(bookingCI)) {
                reserved = true;
            }
            if (checkin.before(bookingCI) && checkin.before(bookingCO) &&
                    checkout.after(bookingCI) && checkout.after(bookingCO)) {
                reserved = true;
            }
        }
        return reserved;
    }
}

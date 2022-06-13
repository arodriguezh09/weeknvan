package arh.miapp.camperbooking.objects;

import android.graphics.Bitmap;

public class ItemBanner {

    private int image;
    private String text, city, checkin, checkout;

    public ItemBanner() {
    }

    public ItemBanner(int image, String text, String city, String checkin, String checkout) {
        this.image = image;
        this.text = text;
        this.city = city;
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public ItemBanner(int image, String text, String checkin, String checkout) {
        this.image = image;
        this.text = text;
        this.city = "";
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public ItemBanner(int image, String text, String city) {
        this.image = image;
        this.text = text;
        this.city = city;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    @Override
    public String toString() {
        return "ItemBanner{" +
                "image=" + image +
                ", text='" + text + '\'' +
                ", city='" + city + '\'' +
                ", checkin='" + checkin + '\'' +
                ", checkout='" + checkout + '\'' +
                '}';
    }
}

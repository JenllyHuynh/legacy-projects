package vn.edu.fpt.model.dto;

public class Bill {
    private CheckoutFormDTO checkoutFormDTO;

    public Bill() {
    }

    public Bill(CheckoutFormDTO checkoutFormDTO) {
        this.checkoutFormDTO = checkoutFormDTO;
    }

    public CheckoutFormDTO getInvoiceInfo() {
        return checkoutFormDTO;
    }

    public void setInvoiceInfo(CheckoutFormDTO checkoutFormDTO) {
        this.checkoutFormDTO = checkoutFormDTO;
    }

    @Override
    public String toString() {
        return "Bill{" +
                ", invoiceInfo=" + checkoutFormDTO +
                '}';
    }
}

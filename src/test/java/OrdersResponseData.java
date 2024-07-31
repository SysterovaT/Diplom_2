public class OrdersResponseData {
    private Order[] orders;
    private boolean success;

    public OrdersResponseData(Order[] orders) {
        this.orders = orders;
    }

    public Order[] getOrders() {
        return orders;
    }

    public void setOrders(Order[] orders) {
        this.orders = orders;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

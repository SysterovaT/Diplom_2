public class OrdersCreateResponseData {
    private Order order;
    private boolean success;

    public OrdersCreateResponseData(Order order, boolean success) {
        this.order = order;
        this.success = success;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

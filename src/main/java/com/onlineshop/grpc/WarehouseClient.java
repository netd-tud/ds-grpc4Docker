package com.onlineshop.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class WarehouseClient {

    // static final String host = "ds-exercise-05.netd.cs.tu-dresden.de";
    static final String host = "localhost";
    static final int port = 8080;
    private final WarehouseServiceGrpc.WarehouseServiceBlockingStub blockingStub;

    public WarehouseClient(String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = WarehouseServiceGrpc.newBlockingStub(channel);
        System.out.println("WarehouseClient created for "+host+":"+port);
    }

    public GetItemsResponse getAllItems() {
        GetItemsRequest request = GetItemsRequest.newBuilder().build();
        try {
            return blockingStub.getAllItems(request);
        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
            return null;
        }
    }

    public AddItemResponse addItem(String id, ItemData data) {
        AddItemRequest request = AddItemRequest.newBuilder()
                .setId(id)
                .setData(data)
                .build();
        try {
            return blockingStub.addItem(request);
        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
            return null;
        }
    }

    public DeleteItemResponse removeItem(String id) {
        DeleteItemRequest request = DeleteItemRequest.newBuilder()
                .setId(id)
                .build();
        try {
            return blockingStub.removeItem(request);
        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
            return null;
        }
    }

    public UpdateItemQuantityResponse updateItemQuantity(String id, int quantity) {
        UpdateItemQuantityRequest request = UpdateItemQuantityRequest.newBuilder()
                .setId(id)
                .setQuantity(quantity)
                .build();
        try {
            return blockingStub.updateItemQuantity(request);
        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
            return null;
        }
    }
 

    public static void main(String[] args) {
        WarehouseClient client = new WarehouseClient(host, port);
        ItemData itemData;
        DeleteItemResponse deleteResponse;
        GetItemsResponse itemsResponse;

        itemData = ItemData.newBuilder()
                .setName("TV Set")
                .setCategory("Electronics")
                .setCountry("USA")
                .setCity("New York")
                .setQuantity(10)
                .build();

        AddItemResponse addItemResponse = client.addItem("item123", itemData);
        System.out.println("Add Item Response: " + addItemResponse);

        itemData = ItemData.newBuilder()
        .setName("Smartphone")
        .setCategory("Electronics")
        .setCountry("USA")
        .setCity("New York")
        .setQuantity(3)
        .build();

        addItemResponse = client.addItem("item124", itemData);
        System.out.println("Add Item Response: " + addItemResponse.getResponseMessage());

        itemsResponse = client.getAllItems();
        System.out.println("getAllItems(): \n" + itemsResponse);

        deleteResponse = client.removeItem("item123");
        System.out.println("Delete Item Response: " + deleteResponse);

        deleteResponse = client.removeItem("item124");
        System.out.println("Delete Item Response: " + deleteResponse);

        itemsResponse = client.getAllItems();
        System.out.println("Get All Items: " + itemsResponse);


    }
}

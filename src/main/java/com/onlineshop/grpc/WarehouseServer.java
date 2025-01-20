package com.onlineshop.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WarehouseServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(8080)
                .addService(new WarehouseServiceImpl())
                .build();

        System.out.println("Starting WarehouseService on port 8080...");
        server.start();
        System.out.println("WarehouseService started.");
        server.awaitTermination();
    }

    static class WarehouseServiceImpl extends WarehouseServiceGrpc.WarehouseServiceImplBase {
        private final Map<String, ItemData> warehouse = new HashMap<>();

        @Override
        public void getAllItems(GetItemsRequest request, StreamObserver<GetItemsResponse> responseObserver) {
            System.out.println("getAllItems called");
            StringBuilder itemsList = new StringBuilder("Items in Warehouse:\n");
            warehouse.forEach((id, item) -> itemsList.append("ID: ").append(id)
                    .append(", Name: ").append(item.getName())
                    .append(", Quantity: ").append(item.getQuantity()).append("\n"));

            GetItemsResponse response = GetItemsResponse.newBuilder()
                    .setResponseMessage(itemsList.toString())
                    .setResponseCode(200)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void addItem(AddItemRequest request, StreamObserver<AddItemResponse> responseObserver) {
            String id = request.getId();
            ItemData itemData = request.getData();
            System.out.println("addItem("+id+") called");

            if (warehouse.containsKey(id)) {
                AddItemResponse response = AddItemResponse.newBuilder()
                        .setResponseMessage("Item already exists.")
                        .setResponseCode(400)
                        .build();
                responseObserver.onNext(response);
            } else {
                warehouse.put(id, itemData);
                AddItemResponse response = AddItemResponse.newBuilder()
                        .setResponseMessage("Item added successfully.")
                        .setResponseCode(201)
                        .build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        }

        @Override
        public void removeItem(DeleteItemRequest request, StreamObserver<DeleteItemResponse> responseObserver) {
            String id = request.getId();
            System.out.println("removeItem("+id+") called");

            if (warehouse.remove(id) != null) {
                DeleteItemResponse response = DeleteItemResponse.newBuilder()
                        .setResponseMessage("Item removed successfully.")
                        .setResponseCode(200)
                        .build();
                responseObserver.onNext(response);
            } else {
                DeleteItemResponse response = DeleteItemResponse.newBuilder()
                        .setResponseMessage("Item not found.")
                        .setResponseCode(404)
                        .build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        }

        @Override
        public void updateItemQuantity(UpdateItemQuantityRequest request, StreamObserver<UpdateItemQuantityResponse> responseObserver) {
            String id = request.getId();
            int quantity = request.getQuantity();
            System.out.println("updateItemQuantit("+id+") called");

            if (warehouse.containsKey(id)) {
                ItemData currentItem = warehouse.get(id);
                ItemData updatedItem = ItemData.newBuilder(currentItem)
                        .setQuantity(quantity)
                        .build();
                warehouse.put(id, updatedItem);

                UpdateItemQuantityResponse response = UpdateItemQuantityResponse.newBuilder()
                        .setResponseMessage("Item quantity updated successfully.")
                        .setResponseCode(200)
                        .build();
                responseObserver.onNext(response);
            } else {
                UpdateItemQuantityResponse response = UpdateItemQuantityResponse.newBuilder()
                        .setResponseMessage("Item not found.")
                        .setResponseCode(404)
                        .build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        }
    }
}
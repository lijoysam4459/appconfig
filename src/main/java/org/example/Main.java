package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;

public class Main extends AbstractVerticle {

    private Map<String, JsonObject> products = new HashMap<>();
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Launcher.executeCommand("run",Main.class.getName());
    }

    @Override
    public void start(){

        setUpInitialData();
        Router router = Router.router(vertx);
        router.get("/products/:productID").handler(this::handleGetProduct);
        router.put("/products/:productID").handler(this::handleAddProduct);
        router.get("/products").handler(this::handleListProduct);

        vertx.createHttpServer().requestHandler(router).listen(9090);
        vertx.createHttpServer().requestHandler(router).listen(9090);


    }

    private void handleGetProduct(RoutingContext routingContext) {
        String productID = routingContext.request().getParam("productID");
        HttpServerResponse httpServerResponse = routingContext.response();
        if (productID == null){
            httpServerResponse.setStatusCode(400);

        }else{
            JsonObject product = products.get(productID);
            if (product == null){
                httpServerResponse.setStatusCode(404);
            }else{
                httpServerResponse
                        .putHeader("content-type","application/json")
                        .end(product.encodePrettily());

            }

        }

    }

    private void handleAddProduct(RoutingContext routingContext) {
        String productID = routingContext.request().getParam("productID");
        HttpServerResponse httpServerResponse = routingContext.response();
        if (productID ==null ){
            httpServerResponse.setStatusCode(400);
        }else{
            JsonObject prod = routingContext.body().asJsonObject();
            if (prod == null){
                httpServerResponse.setStatusCode(400);
            }else{
                products.put(productID,prod);
                httpServerResponse.end();
            }
        }
    }

    private void handleListProduct(RoutingContext routingContext) {
        JsonArray jsonArray = new JsonArray();
        products.forEach((k,v) -> jsonArray.add(v));
        routingContext.response().putHeader("content-type","application/json")
                .end(jsonArray.encodePrettily());
    }

    private void setUpInitialData() {
        addProduct(new JsonObject().put("id","prod1").put("name","americano").put("price",3.99));
        addProduct(new JsonObject().put("id","prod2").put("name","blackcoffee").put("price",2.99));
        addProduct(new JsonObject().put("id","prod3").put("name","pinkdrink").put("price",9.99));
        addProduct(new JsonObject().put("id","prod4").put("name","capuccino").put("price",6.99));

    }

    private void addProduct(JsonObject jsonObject) {
       products.put(jsonObject.getString("id"),jsonObject);
    }


}